package com.example.springbootlucenedemo.controller;

import com.example.springbootlucenedemo.model.FileEntiry;
import com.example.springbootlucenedemo.model.Page;
import com.example.springbootlucenedemo.util.MyIKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author:钟湘
 * @data: 16:43
 */
@Controller
public class FileController {

    @ResponseBody
    @RequestMapping("/page")
    public Page queryFile(Page page, String select) throws IOException, ParseException, InvalidTokenOffsetsException {

        System.out.println(select);
//        System.out.println("===================="+page.getPageNo());
//        System.out.println("===================="+page.getPageSize());

        int start=((page.getPageNo())-1)*page.getPageSize();
        int end=start+page.getPageSize();

        List<FileEntiry> list=new ArrayList<>();

        Directory indexDir = FSDirectory.open(Paths.get("F:\\indexFile"));
        DirectoryReader directoryReader = DirectoryReader.open(indexDir); // 读取索引库索引
        IndexSearcher searcher = new IndexSearcher(directoryReader); // 创建查询索引库核心对象

        QueryParser queryParser=new QueryParser("fileName",new MyIKAnalyzer());
        QueryParser queryParser1=new QueryParser("fileContent",new MyIKAnalyzer());

        Query query=queryParser.parse(select);
        Query query1=queryParser1.parse(select);

        QueryScorer scorer = new QueryScorer(query);
        QueryScorer scorer1=new QueryScorer(query1);

        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color='red'>","</font></b>"); //

        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);
        Highlighter highlighter1= new Highlighter(simpleHTMLFormatter,scorer1);

        highlighter.setTextFragmenter(new SimpleFragmenter(30));//高亮后的段落范围在20字内
        highlighter1.setTextFragmenter(new SimpleFragmenter(30));//高亮后的段落范围在20字内

        BooleanQuery.Builder booleanquery=new BooleanQuery.Builder();
        booleanquery.add(query, BooleanClause.Occur.SHOULD);
        booleanquery.add(query1, BooleanClause.Occur.SHOULD);

        TopDocs topDocs=searcher.search(booleanquery.build(),10000);
        ScoreDoc[] scoreDocs=topDocs.scoreDocs;

        System.out.println("查询结果的总条数："+ topDocs.totalHits);

        int count=0;

        for(ScoreDoc scoreDoc:scoreDocs){
            if(count>=start&&count<end){//大于等于当前页的第一条数据,小于当前页的最后一条数据,最后一条数据不显示
                Document document=searcher.doc(scoreDoc.doc);
                System.out.println("文件大小========"+document.get("fileSize"));
                System.out.println("文件名字========"+document.get("fileName"));
                System.out.println("文件路径========"+document.get("fileUrl"));
                System.out.println("文件内容========"+document.get("fileContent"));

                FileEntiry fileEntiry=new FileEntiry();
                String filename=highlighter.getBestFragment(new MyIKAnalyzer(), "fileName",document.get("fileName"));
                String filecontent=highlighter1.getBestFragment(new MyIKAnalyzer(),"fileContent",document.get("fileContent"));
                if(filename ==null){//如果当前对象没有高亮就取消高亮显示
                     filename=document.get("fileName");
                }
                if(filecontent ==null){//如果当前对象没有高亮就取消高亮显示
                     filecontent=document.get("fileContent");
                    if(filecontent.length()>30){
                        String filecontents=filecontent.substring(0,30)+"...";
                        filecontent=filecontents;
                    }
                 }
                   if(filecontent.length()>30){
                     filecontent=filecontent+"...";
                 }
                     fileEntiry.setFileName(filename);
                     fileEntiry.setFileContent(filecontent);
                     fileEntiry.setFileSize(Integer.valueOf(document.get("fileSize")));
                     fileEntiry.setFileUrl(document.get("fileUrl"));

                     list.add(fileEntiry);
            }
            count++;
        }
        if(scoreDocs.length % page.getPageSize()==0){
            page.setPageCount(scoreDocs.length/ page.getPageSize());
        }else{
            page.setPageCount(scoreDocs.length/ page.getPageSize()+1);
        }
        page.setPageSizeSum(scoreDocs.length);
        page.setFiles(list);
        return page;
    }

    @RequestMapping("/query")
    public String value(String value,Map<String,String> map){
        map.put("value",value);
        return "list";
    }

    /**
     * 文件下载
     * @param response
     * @param request
     * @param loadFilePath
     * @throws IOException
     */
    @RequestMapping("/download")
    public void downloadFile(HttpServletResponse response, HttpServletRequest request,String loadFilePath) throws IOException {
        String[] filepath=loadFilePath.split("//");
        String filename=filepath[filepath.length-1];

        response.setHeader("Content-Disposition","attachment;filename="+filename);

        response.addHeader("Content-Type","application/json;charset=UTF-8");

        FileInputStream fileInputStream=new FileInputStream(new File(loadFilePath));

        OutputStream outputStream=response.getOutputStream();

        byte[] bytes=new byte[2048];
        int read=0;
        if((read=fileInputStream.read(bytes))!=-1){
            outputStream.write(bytes,0,read);
        }
    }
}

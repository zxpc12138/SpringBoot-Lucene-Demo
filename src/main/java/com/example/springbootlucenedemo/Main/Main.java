package com.example.springbootlucenedemo.Main;

import com.example.springbootlucenedemo.util.MyIKAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.nio.file.Paths;

/**
 * @author:钟湘
 * @data: 15:14
 */

public class Main {

     public static void main(String [] stags) throws IOException {
          upload("F:\\oo","F:\\indexFile");
//         String value=readFile(new File("F:\\oo\\aa.txt"));
//         System.out.println(value);
     }

     public static void upload(String FilePath,String IndexPath) throws IOException {

          File filepath=new File(FilePath);
          if(filepath.isDirectory()){
               String[] files=filepath.list();
               for(String file:files){
                    upload(FilePath+File.separator+file,IndexPath);
               }
               System.out.println("文件夹"+filepath.getName());
          }else{
               if(filepath.getName().endsWith(".txt")){
                    System.out.println("我是"+filepath.getName()+"文件");

                    // 创建检索的对象,需要指定从哪里检索
                    Directory directory = FSDirectory.open(Paths.get(IndexPath));
                    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new MyIKAnalyzer()); // 创建查询索引库核心对象,此处不加就是默认的分词器

                    indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);//创建索引库或者追加

                    IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig); // 创建写索引库核心对象

                    Document document=new Document();

                    Field field=new LongPoint("fileSize",filepath.length());
                    Field field1=new StoredField("fileSize",filepath.length());
                    Field field2=new TextField("fileName",filepath.getName(), Field.Store.YES);
                    Field field3=new StringField("fileUrl",filepath.getPath(), Field.Store.YES);
                    Field field4=new TextField("fileContent",readFile(filepath), Field.Store.YES);

                    document.add(field);
                    document.add(field1);
                    document.add(field2);
                    document.add(field3);
                    document.add(field4);

                    indexWriter.addDocument(document);

                    indexWriter.commit();
                    indexWriter.close();
               }
          }
     }

     /**
      * 读取文件内容
      * @param file
      * @return
      * @throws IOException
      */
     public static String readFile(File file) throws IOException {

          String value="";
          if(file.exists()&&file.isFile()){
               InputStream inputStream=new FileInputStream(file);
               BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
               String line=null;
               if((line=bufferedReader.readLine())!=null){
                    value +=line;
               }
               bufferedReader.close();
               inputStream.close();
          }
          return value;
     }
}

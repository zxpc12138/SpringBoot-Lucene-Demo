package com.example.springbootlucenedemo.util;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

/**
 * @author:钟湘
 * @data: 8:34
 */

/**
 * 解决ik分词器跟lucene版本不支持
 */
public class MyIKAnalyzer extends Analyzer {
    private boolean useSmart;

    public boolean useSmart() {
        return this.useSmart;
    }

    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }

    public MyIKAnalyzer() {
        this(false);
    }

    @Override
    protected TokenStreamComponents createComponents(String s) {
        Tokenizer _MyIKTokenizer = new com.example.springbootlucenedemo.util.MyIKTokenizer(this.useSmart());
        return new TokenStreamComponents(_MyIKTokenizer);
    }

    public MyIKAnalyzer(boolean useSmart) {
        this.useSmart = useSmart;
    }
}

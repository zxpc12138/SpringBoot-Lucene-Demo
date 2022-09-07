package com.example.springbootlucenedemo.model;

/**
 * @author:钟湘
 * @data: 9:17
 */

public class FileEntiry {

    private Integer fileSize;//文件长度
    private String fileName;//文件名字
    private String fileUrl;//文件路径
    private String fileContent;//文件内容

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
}

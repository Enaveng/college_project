package com.college.service.oss.service;


import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;


public interface FileService {
    /**
     * 阿里云oss文件上传
     * inputStream 输入流
     * module 文件传输位置
     * originalFileName 原始文件名
     * upload 文件在oss服务器上的url地址
     */
    //上传头像
    String upload(InputStream inputStream, String module, String originalFileName);
    //删除头像
    void removeFile(String url);

    //批量删除头像
    void removeFiles(List<String> urls);
}

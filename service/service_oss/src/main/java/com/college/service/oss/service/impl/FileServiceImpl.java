package com.college.service.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.college.service.oss.service.FileService;
import com.college.service.oss.util.OssProperties;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private OssProperties ossProperties;

    @Override
    public String upload(InputStream inputStream, String module, String originalFileName) {
        //读取oss相关配置信息
        String bucketname = ossProperties.getBucketname();
        String keyid = ossProperties.getKeyid();
        String endpoint = ossProperties.getEndpoint();
        String keysecret = ossProperties.getKeysecret();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, keysecret);
        //判断Bucket是否存在
        if (!ossClient.doesBucketExist(bucketname)) {
            //不存在先创建Bucket
            // 创建CreateBucketRequest对象。
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketname);
            // 设置存储空间读写权限为公共读，默认为私有。
            createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
            // 创建存储空间。
            ossClient.createBucket(createBucketRequest);
        }

        //构建一个objectName 文件上传路径  例如: avatar/2022/02/04/img-1672418655847bc057443a657e01a72ae6eea9a59f4d6.jpg
        //日期格式
        String folder = new DateTime().toString("yyyy/MM/dd");
        //创建随机数作为文件名
        String fileName = UUID.randomUUID().toString();
        //获取文件后缀
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        //拼接完整路径名
        String objectName = module + "/" + folder + "/img-" + fileName + "." + fileExtension;

        // 创建PutObjectRequest对象。
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketname, objectName, inputStream);
        // 创建PutObject请求。 并上传文件流
        PutObjectResult result = ossClient.putObject(putObjectRequest);
        //关闭ossClient
        ossClient.shutdown();
        //返回url
        return "https://" + bucketname + "." + endpoint + "/" + objectName;
    }

    //单个删除头像
    @Override
    public void removeFile(String url) {
        //读取oss相关配置信息
        String bucketname = ossProperties.getBucketname();
        String keyid = ossProperties.getKeyid();
        String endpoint = ossProperties.getEndpoint();
        String keysecret = ossProperties.getKeysecret();
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, keysecret);
        //https://avatar-college-file03.oss-cn-beijing.aliyuncs.com/test/2023/07/15/img-605c5cc2-9065-462e-92c4-245a169d441d..jpg
        //    test/2023/07/15/img-605c5cc2-9065-462e-92c4-245a169d441d..jpg
        String host = "https://" + bucketname + "." + endpoint + "/";
        //根据文件名删除头像  根据url进行截取
        String objectName = url.substring(host.length());
        // 删除文件或目录。如果要删除目录，目录必须为空。
        ossClient.deleteObject(bucketname, objectName);
        //关闭ossClient
        ossClient.shutdown();
    }

    //批量删除头像
    public void removeFiles(List<String> urlList) {
        //读取oss相关配置信息
        String bucketname = ossProperties.getBucketname();
        String keyid = ossProperties.getKeyid();
        String endpoint = ossProperties.getEndpoint();
        String keysecret = ossProperties.getKeysecret();
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, keysecret);
        //截取url地址得到地址
        for (int i = 0; i < urlList.size(); i++) {
            String key = urlList.get(i);
            String host = "https://" + bucketname + "." + endpoint + "/";
            String newKey = key.substring(host.length());
            urlList.set(i, newKey); // 将截取后的字符串放回列表中
        }
        DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(new DeleteObjectsRequest(bucketname).withKeys(urlList));
        //输出删除的文件名
//        List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
//        for(String obj : deletedObjects) {
//            String deleteObj =  URLDecoder.decode(obj, "UTF-8");
//            System.out.println(deleteObj+"1111111");
//        }
        //关闭ossClient
        ossClient.shutdown();
    }


}

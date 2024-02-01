package com.college.service.vod.service.imp;


import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.DeleteVideoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.college.baseservice.exception.CollegeException;
import com.college.commonutils.result.R;
import com.college.commonutils.result.ResultCodeEnum;
import com.college.service.vod.service.VideoService;
import com.college.service.vod.util.AliyunVodSDKUtils;
import com.college.service.vod.util.VodProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VodProperties vodProperties;

    @Override
    public String uploadStream(InputStream inputStream, String originalFileName) {
        //读取相关信息
        String accessKeySecret = vodProperties.getKeysecret();
        String accessKeyId = vodProperties.getKeyid();
        String title = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        //以文件流的形式上传
        UploadStreamRequest request = new UploadStreamRequest(accessKeyId, accessKeySecret, title, originalFileName, inputStream);
        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadStreamResponse response = uploader.uploadStream(request);
        String videoId = response.getVideoId();//请求视频点播服务的视频ID
        if (StringUtils.isEmpty(videoId)) {
            log.error("阿里云上传失败: " + response.getCode() + "-" + response.getMessage());
            throw new CollegeException(ResultCodeEnum.VIDEO_UPLOAD_ALIYUN_ERROR);
        }
        return videoId;
    }

    @Override
    public void removeVideo(String videoId) throws ClientException {
        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                vodProperties.getKeyid(),
                vodProperties.getKeysecret());
        System.out.println(videoId);
        DeleteVideoRequest request = new DeleteVideoRequest();
        request.setVideoIds(videoId);
        client.getAcsResponse(request);
    }

    @Override
    public void removeVideoByIdList(List<String> videoIdList) throws ClientException {
        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(vodProperties.getKeyid(), vodProperties.getKeysecret());
        DeleteVideoRequest request = new DeleteVideoRequest();
        int size = videoIdList.size();//id列表的长度

        /**
         *  使用StringBuffer与StringBuilder类似，都是可变的字符串序列，可以在原始字符串上进行修改。
         *  如果单纯使用String进行字符串拼接 每次对字符串进行修改时，底层实际使用的是StringBuild的append()方法，最后再使用toString方法转换为字符串对象。
         *  在循环中频繁地进行字符串拼接操作，会导致大量的字符串对象创建，这会消耗大量的内存和时间。
         */
        StringBuffer idListStr = new StringBuffer(); //组装好的字符串
        for (int i = 0; i < size; i++) {
            idListStr.append(videoIdList.get(i));
            //i == size - 1   表示已经循环到最后一个id值 不需要再其后面添加 ' , '
            // i % 20 == 19   表示已经达到最大的20个数 需要重新添加
            if (i == size - 1 || i % 20 == 19) {
                request.setVideoIds(idListStr.toString());
                client.getAcsResponse(request);
                //重置idListStr  每20个数之后创建新的StringBuffer对象进行存储
                idListStr = new StringBuffer();
            } else if (i < size - 1) {
                idListStr.append(",");
            }
        }
    }

    @Override
    public String getPlayAuth(String videoSourceId) throws ClientException {

        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                vodProperties.getKeyid(),
                vodProperties.getKeysecret());

        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();//创建请求对象
        request.setVideoId(videoSourceId);//设置请求参数

        GetVideoPlayAuthResponse response = client.getAcsResponse(request);//发送请求得到响应

        return response.getPlayAuth();
    }
}

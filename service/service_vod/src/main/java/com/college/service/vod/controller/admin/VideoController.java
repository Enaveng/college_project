package com.college.service.vod.controller.admin;

import com.college.baseservice.exception.CollegeException;
import com.college.commonutils.result.R;
import com.college.commonutils.result.ResultCodeEnum;
import com.college.commonutils.util.ExceptionUtils;
import com.college.service.vod.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Api("阿里云视频点播")
@RestController
//@CrossOrigin
@Slf4j
@RequestMapping("/admin/vod/video")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @PostMapping("/upload")
    public R upload(@ApiParam(value = "上传视频文件")
                    @RequestParam("file") MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String requestId = videoService.uploadStream(inputStream, originalFilename);
            return R.ok().message("视频上传成功").data("videoId", requestId);
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new CollegeException(ResultCodeEnum.VIDEO_UPLOAD_TOMCAT_ERROR);
        }
    }

    @DeleteMapping("remove/{vodId}")
    public R removeVideo(@ApiParam(name = "vodId", value = "阿里云视频id", required = true)
                         @PathVariable String vodId) {
        log.warn("service-vod MediaController：videoSourceId= " + vodId);
        try {
            videoService.removeVideo(vodId);
            return R.ok().message("视频删除成功");
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new CollegeException(ResultCodeEnum.VIDEO_DELETE_ALIYUN_ERROR);
        }
    }

    //批量删除
    @DeleteMapping("remove")
    public R removeVideoByIdList(
            @ApiParam(value = "阿里云视频id列表", required = true)
            @RequestBody List<String> videoIdList) {
        try {
            videoService.removeVideoByIdList(videoIdList);
            return R.ok().message("视频删除成功");
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new CollegeException(ResultCodeEnum.VIDEO_DELETE_ALIYUN_ERROR);
        }
    }

}

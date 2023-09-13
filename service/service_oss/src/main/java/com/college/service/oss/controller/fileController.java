package com.college.service.oss.controller;

import com.college.baseservice.exception.CollegeException;
import com.college.commonutils.result.R;
import com.college.commonutils.result.ResultCodeEnum;
import com.college.commonutils.util.ExceptionUtils;
import com.college.service.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Api("阿里云文件管理")
@RestController
@RequestMapping("/admin/oss/file")
//@CrossOrigin //跨域
@Slf4j
public class fileController {
    @Autowired
    private FileService fileService;

    //MultipartFile会自动封装上传过来的文件 可以通过内置方法获取文件相关信息
    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public R upload(@ApiParam(value = "文件", required = true) @RequestParam("file") MultipartFile file,
                    @ApiParam(value = "上传路径", required = true) @RequestParam("module") String module) {
        //对可能出现的异常进行捕获处理
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String url = fileService.upload(inputStream, module, originalFilename);
            return R.ok().message("文件上传成功").data("url", url);
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new CollegeException(ResultCodeEnum.FILE_UPLOAD_ERROR);
        }
    }


    @ApiOperation("从OSS上删除头像")
    @DeleteMapping("/remove")
    public R remove(@ApiParam(value = "头像在OSS上的URL地址", required = true)
                    @RequestBody String url) {
        fileService.removeFile(url);
        return R.ok();
    }


    @ApiOperation("从OSS上批量删除头像")
    @DeleteMapping("/removeFiles")
    public R removeFiles(@ApiParam("讲师id列表")
                        //前端传递的是json字符串所以需要规定泛型为String
                        @RequestBody List<String> urlList) {
        fileService.removeFiles(urlList);
        return R.ok();
    }


    @ApiOperation("测试OpenFeign的基本使用")
    @GetMapping("/test")
    public R test() {
        log.info("oss被调用");
        return R.ok().message("调用成功");
    }


}

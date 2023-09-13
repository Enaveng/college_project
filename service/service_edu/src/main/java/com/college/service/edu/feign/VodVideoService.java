package com.college.service.edu.feign;


import com.college.commonutils.result.R;
import com.college.service.edu.feign.fallback.VodVideoServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@FeignClient(value = "service-vod", fallback = VodVideoServiceFallback.class)
public interface VodVideoService {
    //使用Feign调用远程微服务接口删除课程视频
    @DeleteMapping("/admin/vod/video/remove/{vodId}")
    //Feign使用@PathVariable注解接收参数时必须添加value属性
    R removeVideo(@PathVariable("vodId") String vodId);

    //批量删除视频
    @DeleteMapping("/admin/vod/video/remove")
    R removeVideoByIdList(@RequestBody List<String> videoIdList);
}

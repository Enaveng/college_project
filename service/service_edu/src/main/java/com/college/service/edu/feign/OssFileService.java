package com.college.service.edu.feign;

import com.college.commonutils.result.R;
import com.college.service.edu.feign.fallback.OssFileServiceFallback;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Service
//fallback为当远程调用OSS微服务出现错误时的服务降级方法
@FeignClient(value = "service-oss", fallback = OssFileServiceFallback.class) //所要调用的微服务名
public interface OssFileService {
    @GetMapping("/admin/oss/file/test")
    R test();

    //删除图片
    @DeleteMapping("/admin/oss/file/remove")
    R remove(@RequestBody String url);
}

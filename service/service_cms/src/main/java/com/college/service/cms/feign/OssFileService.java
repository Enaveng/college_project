package com.college.service.cms.feign;

import com.college.commonutils.result.R;
import com.college.service.cms.feign.fallback.OssFileServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(value = "service-oss", fallback = OssFileServiceFallback.class)
public interface OssFileService {
    //删除图片
    @DeleteMapping("/admin/oss/file/remove")
    R remove(@RequestBody String url);
}

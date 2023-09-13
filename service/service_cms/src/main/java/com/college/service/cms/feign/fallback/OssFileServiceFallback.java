package com.college.service.cms.feign.fallback;

import com.college.commonutils.result.R;
import com.college.service.cms.feign.OssFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OssFileServiceFallback implements OssFileService {
    @Override
    public R remove(String url) {
        log.info("开启服务降级");
        return R.error();
    }
}

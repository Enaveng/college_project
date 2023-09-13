package com.college.service.edu.feign.fallback;

import com.college.commonutils.result.R;
import com.college.service.edu.feign.VodVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class VodVideoServiceFallback implements VodVideoService {

    @Override
    public R removeVideo(String vodId) {
        log.info("开启服务降级");
        return R.error();
    }

    @Override
    public R removeVideoByIdList(List<String> videoIdList) {
        log.info("开启服务降级");
        return R.error();
    }
}

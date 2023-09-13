package com.college.service.statistics.feign.fallback;

import com.college.commonutils.result.R;
import com.college.service.statistics.feign.RegisterNumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RegisterNumServiceFallback implements RegisterNumService {

    @Override
    public R getRegisterNum(String date) {
        log.error("开启服务降级");
        return R.ok().data("registerNum",0);
    }
}

package com.college.service.statistics.feign;


import com.college.commonutils.result.R;
import com.college.service.statistics.feign.fallback.RegisterNumServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@FeignClient(value = "service-ucenter", fallback = RegisterNumServiceFallback.class)
public interface RegisterNumService {
    @GetMapping("/admin/ucenter/member/count-register-num/{date}")
    R getRegisterNum(@PathVariable("date") String date);
}

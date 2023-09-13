package com.college.service.sms.controller.api;

import com.aliyuncs.exceptions.ClientException;
import com.college.baseservice.exception.CollegeException;
import com.college.commonutils.result.R;
import com.college.commonutils.result.ResultCodeEnum;
import com.college.commonutils.util.FormUtils;
import com.college.commonutils.util.RandomUtils;
import com.college.service.sms.service.SmsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sms")
@Api("短信管理")
//@CrossOrigin //跨域
@Slf4j
public class ApiSmsController {
    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("send/{mobile}")
    public R getCode(@PathVariable String mobile) throws ClientException {

        //校验手机号是否合法
        if (StringUtils.isEmpty(mobile) || !FormUtils.isMobile(mobile)) {
            log.error("手机号不正确");
            new CollegeException(ResultCodeEnum.LOGIN_MOBILE_ERROR);
        }
        //生成四位验证码
        String checkCode = RandomUtils.getFourBitRandom();
        //发送验证码
        //smsService.send(mobile, checkCode);
        //存储验证码到redis 五分钟过期
        redisTemplate.opsForValue().set(mobile, checkCode, 5, TimeUnit.MINUTES);
        return R.ok().message("短信发送成功");
    }
}

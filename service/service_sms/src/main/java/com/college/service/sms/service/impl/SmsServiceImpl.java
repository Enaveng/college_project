package com.college.service.sms.service.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.college.baseservice.exception.CollegeException;
import com.college.commonutils.result.ResultCodeEnum;
import com.college.service.sms.service.SmsService;
import com.college.service.sms.util.SmsProperties;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Autowired
    private SmsProperties smsProperties;

    //参数为手机号和随机生成的四位验证码  该方法使用的是sdk1.0版本的短信发送代码
    @Override
    public void send(String mobile, String checkCode) {
        //创建配置对象
        DefaultProfile profile = DefaultProfile.getProfile(
                smsProperties.getRegionId(),
                smsProperties.getKeyId(),
                smsProperties.getKeySecret());
        //创建client对象
        IAcsClient client = new DefaultAcsClient(profile);

        //组装参数对象
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(mobile);
        request.setSignName(smsProperties.getSignName());
        request.setTemplateCode(smsProperties.getTemplateCode());
        Map<String, String> param = new HashMap<>();
        param.put("code", checkCode);
        //注:这里使用的Gson版本必须是2.8.6及以上
        Gson gson = new Gson();
        String json = gson.toJson(param);
        request.setTemplateParam(json);

        SendSmsResponse response = null;
        try {
            response = client.getAcsResponse(request);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }

        //判断执行结果
        String message = response.getMessage();
        String code = response.getCode();
        if ("isv.BUSINESS_LIMIT_CONTROL".equals(code)) {
            log.error("发送短信过于频繁：" + "code-" + code + ", message-" + message);
            throw new CollegeException(ResultCodeEnum.SMS_SEND_ERROR_BUSINESS_LIMIT_CONTROL);
        }

        if (!"OK".equals(code)) {
            log.error("短信发送失败：" + "code-" + code + ", message-" + message);
            throw new CollegeException(ResultCodeEnum.SMS_SEND_ERROR);
        }
    }
}
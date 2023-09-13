package com.college.infrastructure.security.handle;

import com.college.commonutils.util.MD5;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *   密码处理的工具类
 */
@Component
public class DefaultPasswordEncoder implements PasswordEncoder {


    //参数为要验证的原始密码，通常是用户输入的密码
    //该方法对用户输入的密码进行MD5加密
    @Override
    public String encode(CharSequence charSequence) {
        return MD5.encrypt(charSequence.toString());
    }

    //对密码进行校验
    //rawPassword：要验证的原始密码，通常是用户输入的密码。
    //encodedPassword：已加密的密码，通常是从数据库或其他存储中获取的密码。
    @Override
    public boolean matches(CharSequence charSequence, String encodedPassword) {
        return encodedPassword.equals(MD5.encrypt(charSequence.toString()));
    }
}
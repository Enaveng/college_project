package com.college.service.ucenter.entity.vo;

import lombok.Data;

import java.io.Serializable;


@Data
public class RegisterVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nickname; //昵称
    private String mobile;   //手机号
    private String password; //密码
    private String code;     //验证码
}
package com.college.baseservice.exception;


import com.college.commonutils.result.ResultCodeEnum;
import lombok.Data;

@Data
public class CollegeException extends RuntimeException {
    //自定义一个异常返回码
    private Integer code;

    //自定义异常返回信息
    public CollegeException(String message, Integer code) {
        super(message);
        this.code = code;
    }
    //使用自定义过的异常枚举类对结果进行返回
    public CollegeException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

}

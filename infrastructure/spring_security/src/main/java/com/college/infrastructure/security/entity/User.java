package com.college.infrastructure.security.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * <p>
 * 用户表
 * </p>
 *
 * @author dlwlrma
 * @since 2023-08-12
 */
@Data
@ApiModel(value = "User对象", description = "用户表")
public class User {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "微信openid")
    private String openid;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "昵称")
    @JsonProperty("username")
    private String nickName;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "用户签名")
    private String token;

    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    @TableLogic
    private Integer isDeleted;


}

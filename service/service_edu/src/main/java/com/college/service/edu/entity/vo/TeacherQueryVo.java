package com.college.service.edu.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


//前台页面当中存在根据以下参数进行条件查询的业务
@ApiModel(value = "Teacher查询对象")
@Data
public class TeacherQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "讲师姓名,模糊查询")
    private String name;

    @ApiModelProperty(value = "讲师级别 1高级讲师 2首席讲师")
    private Integer level;

    @ApiModelProperty(value = "开始时间")
    private String joinDateBegin;

    @ApiModelProperty(value = "结束时间")
    private String joinDateEnd;
}
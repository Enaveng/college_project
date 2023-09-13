package com.college.baseservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

//Dto表示两个不同微服务之间传输的实体类对象
@Data
public class CourseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;//课程ID
    private String title;//课程标题
    private BigDecimal price;//课程销售价格，设置为0则可免费观看
    private String cover;//课程封面图片路径
    private String teacherName;//课程讲师
}

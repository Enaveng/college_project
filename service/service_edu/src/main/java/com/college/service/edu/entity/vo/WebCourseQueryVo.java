package com.college.service.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;


//前台网页条件查询的实体类
@Data
public class WebCourseQueryVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String subjectParentId;
    private String subjectId;
    private String buyCountSort;
    private String gmtCreateSort;
    private String priceSort;

    private Integer type;
}

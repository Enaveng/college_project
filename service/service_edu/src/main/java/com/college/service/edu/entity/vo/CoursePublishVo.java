package com.college.service.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;


//发布课程页所需要展示的信息
@Data
public class CoursePublishVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String title;
    private String cover;
    private Integer lessonNum;
    private String subjectParentTitle;
    private String subjectTitle;
    private String teacherName;
    private String price;//只用于显示
}

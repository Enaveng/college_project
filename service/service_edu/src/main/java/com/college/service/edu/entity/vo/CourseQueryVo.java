package com.college.service.edu.entity.vo;


import lombok.Data;

import java.io.Serializable;

//课程模糊查询相关实体类
@Data
public class CourseQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String teacherId;
    private String subjectParentId;
    private String subjectId;

}

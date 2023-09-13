package com.college.service.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//实现自关联嵌套
@Data
public class SubjectVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String sort;

    //二级类别
    private List<SubjectVo> children = new ArrayList<>();
}

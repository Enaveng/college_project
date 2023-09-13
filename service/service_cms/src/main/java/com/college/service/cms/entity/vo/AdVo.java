package com.college.service.cms.entity.vo;

import lombok.Data;

import java.io.Serializable;

//广告推荐 分页列表所需要展示的实体类对象
@Data
public class AdVo implements Serializable {

    private static final long serialVersionUID=1L;
    private String id;
    private String title;//广告标题
    private Integer sort;//广告排序
    private String type; //广告位
}
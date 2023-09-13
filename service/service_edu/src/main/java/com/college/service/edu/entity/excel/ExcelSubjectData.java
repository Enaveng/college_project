package com.college.service.edu.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

//实体类对应的是excel数据表中的列名   通过监听器实现读取数据并写入到数据库的操作
@Data
public class ExcelSubjectData {

    @ExcelProperty("一级分类")
    private String levelOneTitle;

    @ExcelProperty("二级分类")
    private String leveTwoTitle;
}

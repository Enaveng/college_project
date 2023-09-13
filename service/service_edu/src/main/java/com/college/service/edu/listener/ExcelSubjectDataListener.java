package com.college.service.edu.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.college.service.edu.entity.Subject;
import com.college.service.edu.entity.excel.ExcelSubjectData;
import com.college.service.edu.mapper.SubjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;

import java.util.Map;

//监听器
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class ExcelSubjectDataListener extends AnalysisEventListener<ExcelSubjectData> {

    private SubjectMapper subjectMapper;

    //该方法会逐条解析excel表数据
    @Override
    public void invoke(ExcelSubjectData excelSubjectData, AnalysisContext analysisContext) {
        log.info("解析到一条数据: " + excelSubjectData);
        //处理读取出来的数据
        String levelOneTitle = excelSubjectData.getLevelOneTitle();  //得到第一列的数据
        String leveTwoTitle = excelSubjectData.getLeveTwoTitle();    //得到第二列的数据
        log.info("levelOneTitle: " + levelOneTitle);
        log.info("leveTwoTitle: " + leveTwoTitle);
        //判断是否有相同的一级类别存在
        Subject subjectLevelOne = this.getByTitle(levelOneTitle);
        String parentId = null;
        if (subjectLevelOne == null) {
            //组装excel中的一级类别
            Subject subject = new Subject();
            //一级类别的ParentId为0  二级类别的ParentId指向一级类别的id
            subject.setParentId("0");
            subject.setTitle(levelOneTitle);
            //并将数据插入到数据库当中
            subjectMapper.insert(subject);
            parentId = subject.getId();
        } else {
            parentId = subjectLevelOne.getId();
        }

        //判断相同的一级类别下是否存在相同的二级类别
        Subject subjectLeveTwo = this.getSubByTitle(leveTwoTitle, parentId);
        if (subjectLeveTwo == null) {
            //组装excel中的二级类别
            Subject subject = new Subject();
            subject.setParentId(parentId);
            subject.setTitle(leveTwoTitle);
            //插入到数据库
            subjectMapper.insert(subject);
        }


    }

    //所有数据读取之后的收尾工作
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("全部数据解析完成");
    }

    //判断一级类别是否存在
    private Subject getByTitle(String title) {
        //根据一级类别的字段查询其是否存在 防止多余添加
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", "0");
        queryWrapper.eq("title", title);
        return subjectMapper.selectOne(queryWrapper);
    }

    //判断二级类别是否存在
    //判断同一一级类别下是否存在相同的二级类别
    private Subject getSubByTitle(String title, String parentId) {
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId);
        queryWrapper.eq("title", title);
        return subjectMapper.selectOne(queryWrapper);
    }
}

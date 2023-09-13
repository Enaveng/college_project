package com.college.service.edu.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.college.service.edu.entity.Subject;
import com.college.service.edu.entity.excel.ExcelSubjectData;
import com.college.service.edu.entity.vo.SubjectVo;
import com.college.service.edu.listener.ExcelSubjectDataListener;
import com.college.service.edu.mapper.SubjectMapper;
import com.college.service.edu.service.SubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-04
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Override
    public void batchImport(InputStream inputStream) {
        // 有个很重要的点 监听器的类 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
        // 这里需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        //参数一: 可以时文件名也可以是上传的文件流 参数二:实体类 参数三：new一个监听器
        EasyExcel.read(inputStream, ExcelSubjectData.class, new ExcelSubjectDataListener(baseMapper))
                .excelType(ExcelTypeEnum.XLS)  //定义读取的excel的类型 XLS为excel3版本  XLSX为excel7版本
                .sheet()
                .doRead();
    }

    @Override
    public List<SubjectVo> nestedList() {
        //查询parent_id为0代表的是一级类别
        return baseMapper.selectNestedListByParentId("0");
    }
}

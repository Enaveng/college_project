package com.college.service.edu.service;

import com.college.service.edu.entity.Subject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.college.service.edu.entity.vo.SubjectVo;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-04
 */
public interface SubjectService extends IService<Subject> {
    //批量导入excel文件的方法
    void batchImport(InputStream inputStream);

    List<SubjectVo> nestedList();
}

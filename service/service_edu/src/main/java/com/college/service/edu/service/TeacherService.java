package com.college.service.edu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.service.edu.entity.Teacher;
import com.baomidou.mybatisplus.extension.service.IService;
import com.college.service.edu.entity.vo.TeacherQueryVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-04
 */
public interface TeacherService extends IService<Teacher> {

    Page<Teacher> selectPage(Page<Teacher> objectPage, TeacherQueryVo teacherQueryVo);

    List<Map<String, Object>> selectNameListByKey(String key);

    //删除单个头像
    boolean removeAvatarById(String id);

    //批量删除头像
    boolean removeAvatarByIds(List<String> ids);

    Map<String, Object> selectTeacherInfoById(String id);

    List<Teacher> selectHotTeacher();
}


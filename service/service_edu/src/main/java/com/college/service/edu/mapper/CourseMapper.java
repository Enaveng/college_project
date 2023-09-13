package com.college.service.edu.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.baseservice.dto.CourseDto;
import com.college.service.edu.entity.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.college.service.edu.entity.vo.CoursePublishVo;
import com.college.service.edu.entity.vo.CourseVo;
import com.college.service.edu.entity.vo.WebCourseVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-04
 */
@Repository
public interface CourseMapper extends BaseMapper<Course> {


    List<CourseVo> selectPageByCourseQueryVo(Page<CourseVo> coursePage,
                                             @Param(Constants.WRAPPER) QueryWrapper<CourseVo> queryWrapper);

    CoursePublishVo selectCoursePublishVoById(String id);

    WebCourseVo selectWebCourseVoById(String id);

    CourseDto selectCourseDtoById(String courseId);
}

package com.college.service.edu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.baseservice.dto.CourseDto;
import com.college.service.edu.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import com.college.service.edu.entity.form.CourseInfoForm;
import com.college.service.edu.entity.vo.*;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-04
 */
public interface CourseService extends IService<Course> {

    String saveCourseInfo(CourseInfoForm courseInfoForm);

    CourseInfoForm getCourseInfoById(String id);

    void updateCourseInfoById(CourseInfoForm courseInfoForm);

    Page<CourseVo> selectPage(Page<CourseVo> coursePage, CourseQueryVo courseQueryVo);

    boolean removeCoverById(String id);

    boolean removeCourseById(String id);

    CoursePublishVo getCoursePublishVoById(String id);

    boolean publishCourseById(String id);

    List<Course> webGetCourseList(WebCourseQueryVo webCourseQueryVo);

    WebCourseVo selectWebCourseVoById(String id);

    List<Course> selectHotCourse();

    CourseDto getCourseDtoById(String courseId);

}


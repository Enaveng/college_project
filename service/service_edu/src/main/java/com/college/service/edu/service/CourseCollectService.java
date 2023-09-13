package com.college.service.edu.service;

import com.college.service.edu.entity.CourseCollect;
import com.baomidou.mybatisplus.extension.service.IService;
import com.college.service.edu.entity.vo.CourseCollectVo;

import java.util.List;

/**
 * <p>
 * 课程收藏 服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-04
 */
public interface CourseCollectService extends IService<CourseCollect> {

    void saveCollect(String courseId, String memberId);

    boolean isCollect(String courseId, String memberId);

    List<CourseCollectVo> selectListByMemberId(String memberId);

    boolean removeCourseCollect(String courseId, String id);
}

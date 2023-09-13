package com.college.service.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.college.service.edu.entity.CourseCollect;
import com.college.service.edu.entity.vo.CourseCollectVo;
import com.college.service.edu.mapper.CourseCollectMapper;
import com.college.service.edu.service.CourseCollectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 课程收藏 服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-04
 */
@Service
public class CourseCollectServiceImpl extends ServiceImpl<CourseCollectMapper, CourseCollect> implements CourseCollectService {

    //收藏课程  先判断是否收藏 不然会出现重复插入数据表的情况
    @Override
    public void saveCollect(String courseId, String memberId) {
        if (!this.isCollect(courseId, memberId)) {
            CourseCollect courseCollect = new CourseCollect();
            courseCollect.setCourseId(courseId);
            courseCollect.setMemberId(memberId);
            baseMapper.insert(courseCollect);
        }
    }

    //判断是否收藏
    @Override
    public boolean isCollect(String courseId, String memberId) {
        QueryWrapper<CourseCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId).eq("member_id", memberId);
        CourseCollect courseCollect = baseMapper.selectOne(queryWrapper);
        if (courseCollect != null) {
            return true;
        } else {
            return false;
        }
    }

    //查询用户已经收藏的课程
    @Override
    public List<CourseCollectVo> selectListByMemberId(String memberId) {
        return baseMapper.selectPageByMemberId(memberId);
    }


    @Override
    public boolean removeCourseCollect(String courseId, String memberId) {
        //已收藏则删除
        if(this.isCollect(courseId, memberId)) {
            QueryWrapper<CourseCollect> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("course_id", courseId).eq("member_id", memberId);
            return this.remove(queryWrapper);
        }
        return false;
    }
}

package com.college.service.edu.job;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.college.service.edu.entity.Course;
import com.college.service.edu.mapper.CourseMapper;
import com.college.service.edu.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

//使用SpirngScheduled实现定时任务
@Component
public class PreCacheJob {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CourseMapper courseMapper;

    //表示每天的五点自动执行
    @Scheduled(cron = "0 0 17 * * *")
    public void doCacheRecommendCourse() {
        String redisKey = "college:index:course";
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("view_count");
        queryWrapper.last("limit 8");
        List<Course> courseList = courseMapper.selectList(queryWrapper);
        //写缓存 必须要指定过期时间 此处指定30s
        redisTemplate.opsForValue().set(redisKey, courseList, 30000, TimeUnit.MILLISECONDS);
    }
}

package com.college.service.edu.job;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.college.service.edu.config.RedissonConfig;
import com.college.service.edu.entity.Course;
import com.college.service.edu.mapper.CourseMapper;
import com.college.service.edu.service.CourseService;
import org.redisson.api.RLock;
import org.redisson.api.RLockAsync;
import org.redisson.api.RedissonClient;
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
    @Autowired
    private RedissonClient redissonClient;

    //使用redisson实现分布式锁
    //表示每天的五点自动执行
    @Scheduled(cron = "0 0 17 * * *")
    public void doCacheRecommendCourse() {
        RLock lock = redissonClient.getLock("college:index:course"); //提供一个锁
        //得到锁
        try {
            //参数: (long waitTime 等待时间, long leaseTime 锁的过期时间 设置-1开启看门狗机制, TimeUnit unit)
            if (lock.tryLock(0, -1, TimeUnit.SECONDS)) {  //表示线程获取锁成功 执行定时任务方法
                String redisKey = "college:index:course";
                QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
                queryWrapper.orderByDesc("view_count");
                queryWrapper.last("limit 8");
                List<Course> courseList = courseMapper.selectList(queryWrapper);
                //写缓存 必须要指定过期时间 此处指定30s
                redisTemplate.opsForValue().set(redisKey, courseList, 30000, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //必须在finally当中去释放锁 并且判断只能删除自己的锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}

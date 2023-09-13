package com.college.service.edu.testredis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;


//单元测试的测试类一定要和启动类在同一个根目录下
@SpringBootTest
public class testRedis {

    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    void test01(){
        redisTemplate.opsForValue().set("yupi","1111");
    }
}

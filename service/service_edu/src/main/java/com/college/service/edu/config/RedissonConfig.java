package com.college.service.edu.config;


import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedissonConfig {

    private String port;
    private String host;
    private String password;


    @Bean
    public RedissonClient redissonClient() {
        //1.创建配置
        String redisAddress = String.format("redis://%s:%s", host, port);
        //Create config object
        Config config = new Config();
        //设置为单机模式  use "rediss://" for SSL connection
        config.useSingleServer().setAddress(redisAddress).setPassword(password).setDatabase(3);
        //创建实例
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}

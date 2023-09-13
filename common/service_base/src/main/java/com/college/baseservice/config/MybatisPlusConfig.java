package com.college.baseservice.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//该类为MybatisPlus的配置类 所有service下的微服务都会配置
@Configuration
@EnableTransactionManagement //开启事务管理
@MapperScan("com.college.service.*.mapper") //扫描所有微服务下的mapper文件
public class MybatisPlusConfig {
    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
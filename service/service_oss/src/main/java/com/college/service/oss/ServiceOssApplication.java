package com.college.service.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//因为该模块的的父模块导入了mybatis相关依赖 在启动时需要自动配置数据源 防止报错取消这一个行为
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan("com.college")
@EnableDiscoveryClient //服务注册
public class ServiceOssApplication {
     public static void main(String[] args) {
           SpringApplication.run(ServiceOssApplication.class, args);
      }

}

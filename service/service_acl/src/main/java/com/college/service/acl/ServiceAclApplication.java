package com.college.service.acl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan("com.college") //扫描指定的配置类  默认从启动类所在包开始，扫描当前包及其子级包下的所有文件
@EnableDiscoveryClient //服务注册
public class ServiceAclApplication {
     public static void main(String[] args) {
           SpringApplication.run(ServiceAclApplication.class, args);
      }
}

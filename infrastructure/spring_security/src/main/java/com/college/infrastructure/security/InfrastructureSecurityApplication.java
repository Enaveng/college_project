package com.college.infrastructure.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.college")
public class InfrastructureSecurityApplication {
     public static void main(String[] args) {
           SpringApplication.run(InfrastructureSecurityApplication.class, args);
      }

}

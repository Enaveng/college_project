package com.college.infrastructure.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

//使用Gateway解决跨域问题就不需要再添加@CrossOrigin注解 并且该注解与跨域配置类会发生冲突
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");//允许所有请求头
        config.addAllowedOrigin("*");//允许所有请求方法，例如get，post等
        config.addAllowedHeader("*");//允许所有的请求来源

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);//对所有经过网关的请求都生效

        return new CorsWebFilter(source);
    }
}

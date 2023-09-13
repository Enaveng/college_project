package com.college.service.oss.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
@Data //添加get方法方便获取
@Component
@ConfigurationProperties(prefix = "aliyun.oss")  //用于将配置文件中的属性值绑定到一个Java对象上
public class OssProperties {
    private String endpoint;
    private String keyid;
    private String keysecret;
    private String bucketname;
}

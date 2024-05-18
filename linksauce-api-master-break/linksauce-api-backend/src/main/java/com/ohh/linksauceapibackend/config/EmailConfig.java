package com.ohh.linksauceapibackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Ohh
 * @Desctription: 电子邮件配置
 * @Date: 2024-05-18 11:55
 * @Version: 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
@Data
public class EmailConfig {
    private String emailFrom = "1299410261@qq.com";
}

package com.ohh.linksauceapibackend;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableDubbo
@MapperScan("com.ohh.linksauceapibackend.mapper")
public class LinkSauceApiBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(LinkSauceApiBackendApplication.class, args);
    }
}

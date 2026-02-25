package com.example.healthdiet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用入口类。
 * 启动后端服务：默认端口 8080，可在 application.yml 中配置。
 */
@SpringBootApplication
public class HealthDietApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthDietApplication.class, args);
    }
}


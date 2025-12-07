package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka 服务注册中心启动类
 */
@SpringBootApplication  // 1. Spring Boot 应用注解
@EnableEurekaServer     // 2. 启用 Eureka Server 功能
public class EurekaApplication {

    public static void main(String[] args) {
        // 3. 启动 Spring Boot 应用
        SpringApplication.run(EurekaApplication.class, args);
    }
}
package org.example;


import org.example.feign.UserClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@SpringBootApplication
@EnableDiscoveryClient // 通用注解，Nacos 和 Eureka 都支持
@RestController
@EnableFeignClients(basePackages = "org.example.feign")     // 启用 OpenFeign
public class OrderApplication {

    @GetMapping("/hello")
    public String home() {
        return "hello world!";
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(OrderApplication.class, args);
        // 查看所有UserClient类型的Bean
        String[] beans = context.getBeanNamesForType(UserClient.class);
        System.out.println("UserClient Beans: " + Arrays.toString(beans));
    }
}
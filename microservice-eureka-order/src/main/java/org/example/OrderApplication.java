package org.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class OrderApplication {

    @GetMapping("/hello")  // 建议使用 @GetMapping 替代 @RequestMapping
    public String home() {
        return "hello world!";  // 修正：引号配对
    }

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
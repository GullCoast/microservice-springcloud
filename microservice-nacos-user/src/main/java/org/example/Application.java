package org.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class Application {

    @GetMapping("/hello")
    public String home() {
        return "hello world!";
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    /**
     * 创建 RestTemplate 实例并交给 Spring 容器管理
     * 这样在其他地方就可以直接 @Autowired 注入使用了
     * RestTemplate 是 Spring 提供的用于访问 Rest 服务的客户端实例，
     * 它提供了多种便捷访问远程 Http 服务的方法，能够大大提高客户端的编写效率
     */
    @Bean
    @LoadBalanced  // restTemplate被@LoadBalanced注解后就具有了负载均衡的能力
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
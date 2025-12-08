package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class UserController {

    private final RestTemplate restTemplate;

    /**
     * 构造器注入 RestTemplate 依赖
     *
     * @param restTemplate 用于发送HTTP请求的RestTemplate实例
     *                      由Spring容器自动注入
     */
    public UserController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 查找与用户相关的订单
     */
    @GetMapping("/findOrdersByUser/{id}")
    public String findOrdersByUser(@PathVariable String id) {
        return this.restTemplate
                .getForObject("http://microservice-eureka-order/order/" + id, String.class);  // 使用注册中心单独订单服务实例名称
    }
}
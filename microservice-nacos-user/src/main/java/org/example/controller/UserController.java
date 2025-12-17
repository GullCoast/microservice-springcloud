package org.example.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import jakarta.annotation.PostConstruct;
import org.example.api.dto.UserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
                .getForObject("http://microservice-nacos-order/order/" + id, String.class);  // 使用注册中心单独订单服务实例名称
    }
    @GetMapping("/slow")
    @SentinelResource(value = "slowMethod", fallback = "slowFallback")
    public String slowMethod() throws InterruptedException {
        // 模拟慢调用：随机休眠
        long sleepTime = (long) (Math.random() * 3000);  // 0-3秒
        TimeUnit.MILLISECONDS.sleep(sleepTime);
        return "正常响应，耗时：" + sleepTime + "ms";
    }

    @GetMapping("/api/users/{userId}")
    public UserDTO getUserById(@PathVariable Long userId) {
        // 模拟返回用户数据
        return UserDTO.builder()
                .id(userId)
                .name("模拟用户" + userId)
                .email("user" + userId + "@test.com")
                .build();
    }

    @GetMapping("/api/users/check/{userId}")
    public Boolean checkUserExists(@PathVariable Long userId) {
        // 模拟用户检查：ID>0的用户存在
        return userId != null && userId > 0;
    }

    // 降级方法
    public String slowFallback(Throwable t) {
        return "服务降级，请稍后重试。原因：" + t.getMessage();
    }

    @PostConstruct
    public void initDegradeRules() {
        System.out.println("UserController 初始化熔断规则...");
        // 1. 获取现有规则
        List<DegradeRule> rules = new ArrayList<>(DegradeRuleManager.getRules());
        // 2. 添加 slowMethod 规则
        DegradeRule rule = new DegradeRule();
        rule.setResource("slowMethod");
        rule.setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType());
        rule.setCount(1000);  // 响应时间超过1000ms视为慢调用
        rule.setTimeWindow(10);  // 熔断时间10秒
        rule.setSlowRatioThreshold(0.5);  // 慢调用比例阈值50%
        rule.setMinRequestAmount(5);  // 最小请求数
        rule.setStatIntervalMs(10000);  // 统计窗口10秒
        rules.add(rule);

        DegradeRuleManager.loadRules(rules);
    }
}
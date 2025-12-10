package org.example.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private AtomicInteger requestCount = new AtomicInteger(0);
    private AtomicInteger errorCount = new AtomicInteger(0);

    @GetMapping("/pay")
    @SentinelResource(
            value = "payment",
            blockHandler = "paymentBlockHandler",
            fallback = "paymentFallback"           // 业务异常的处理方法
    )
    public String pay(@RequestParam(defaultValue = "0") int type) {
        int count = requestCount.incrementAndGet();

        System.out.println("第 " + count + " 次请求，type=" + type);

        if (type == 0) {
            errorCount.incrementAndGet();
            System.out.println("当前异常比例：" +
                    (errorCount.get() * 100.0 / requestCount.get()) + "%");
            // 这里抛异常会被统计到异常比例中
            throw new RuntimeException("支付系统异常");
        }
        return "支付成功，当前请求数：" + count + "，异常数：" + errorCount.get();
    }

    // 这个方法是当熔断触发时被调用（请求被拒绝时）
    public String paymentBlockHandler(int type, BlockException ex) {
        System.out.println("===== 请求被熔断拒绝 =====");
        return "支付服务熔断中（请求被拒绝），请10秒后重试";
    }

    // 这个方法是当方法内部抛异常时被调用
    public String paymentFallback(int type, Throwable t) {
        return "支付服务异常：" + t.getMessage();
    }

    @PostConstruct
    public void initDegradeRules() {
        System.out.println("PaymentController 初始化熔断规则...");
        // 1. 获取现有规则
        List<DegradeRule> rules = new ArrayList<>(DegradeRuleManager.getRules());
        // 2. 添加 payment 规则
        DegradeRule rule = new DegradeRule();
        rule.setResource("payment");
        rule.setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType());
        rule.setCount(0.5);  // 异常比例阈值50%
        rule.setTimeWindow(10);  // 熔断10秒
        rule.setMinRequestAmount(10);  // 最小请求数
        rule.setStatIntervalMs(10000);  // 添加统计窗口
        rules.add(rule);

        DegradeRule rule1 = new DegradeRule();
        rule1.setResource("payment");
        rule1.setGrade(CircuitBreakerStrategy.ERROR_COUNT.getType());
        rule1.setCount(5);  // 异常数达到5次触发熔断
        rule1.setTimeWindow(10);
        rule1.setMinRequestAmount(10);
        rules.add(rule1);

        DegradeRuleManager.loadRules(rules);
    }
}
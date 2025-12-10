package org.example.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jakarta.annotation.PostConstruct;
import org.example.util.ServiceInfoUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.po.Order;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    /**
     * 通过 id 查询订单
     */
    @GetMapping("/{id}")
    @SentinelResource(value = "findOrderById", blockHandler = "handleBlock")
    public String findOrderById(@PathVariable String id) {
        Order order = new Order();
        order.setId("123");
        order.setPrice(23.5);
        order.setReceiverAddress("Beijing");
        order.setReceiverName("ltt");
        order.setReceiverPhone("13422343311");
        System.out.println(ServiceInfoUtil.getPort());  // 输出当前实例端口号
        return order.toString();
    }

    // 限流处理方法
    public String handleBlock(String id, BlockException ex) {
        return "请求被限流了，请稍后重试！";
    }

    // 初始化限流规则
    @PostConstruct
    public void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("findOrderById");  // 资源名（与@SentinelResource的value一致）
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);  // 限流阈值类型：QPS模式
        rule.setCount(2);  // 每秒最多2个请求
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
}
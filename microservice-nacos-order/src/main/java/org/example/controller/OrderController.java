package org.example.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jakarta.annotation.PostConstruct;
import org.example.api.dto.OrderDTO;
import org.example.api.dto.UserDTO;
import org.example.feign.UserClient;
import org.example.util.ServiceInfoUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.po.Order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final UserClient userClient;  // 注入 Feign 客户端

    // 模拟订单数据
    private Map<Long, OrderDTO> orderMap = new HashMap<>();

    public OrderController(
            @Qualifier("org.example.feign.UserClient") UserClient userClient) {
        this.userClient = userClient;
        orderMap.put(1L, OrderDTO.builder().id(1L).orderNo("ORD001").userId(1L).amount(new BigDecimal("100.50")).build());
        orderMap.put(2L, OrderDTO.builder().id(2L).orderNo("ORD002").userId(2L).amount(new BigDecimal("200.00")).build());
        orderMap.put(3L, OrderDTO.builder().id(3L).orderNo("ORD003").userId(3L).amount(new BigDecimal("300.75")).build());
    }
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

    @GetMapping("/{id}/with-user")
    public Map<String, Object> getOrderWithUser(@PathVariable Long id) {
        // 1. 获取订单
        OrderDTO order = orderMap.get(id);
        if (order == null) {
            order = OrderDTO.builder().id(id).orderNo("NOT_FOUND").userId(0L).amount(BigDecimal.ZERO).build();
        }

        // 2. 通过 Feign 调用用户服务获取用户信息
        UserDTO user = userClient.getUserById(order.getUserId());

        // 3. 组合返回
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("user", user);
        result.put("timestamp", System.currentTimeMillis());

        return result;
    }

    @GetMapping("/test-feign/{userId}")
    public Map<String, Object> testFeign(@PathVariable Long userId) {
        // 专门测试 Feign 的接口
        Map<String, Object> result = new HashMap<>();

        // 测试用户是否存在
        Boolean exists = userClient.checkUserExists(userId);
        result.put("userExists", exists);

        // 获取用户信息
        UserDTO user = userClient.getUserById(userId);
        result.put("userInfo", user);

        // 模拟创建订单
        if (exists) {
            OrderDTO newOrder = OrderDTO.builder()
                    .id(System.currentTimeMillis())
                    .orderNo("TEST-" + System.currentTimeMillis())
                    .userId(userId)
                    .amount(new BigDecimal("99.99"))
                    .build();
            result.put("testOrder", newOrder);
        }

        result.put("message", "Feign 调用成功!");
        return result;
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
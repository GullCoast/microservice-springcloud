package org.example.controller;

import org.example.util.ServiceInfoUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.example.po.Order;

@RestController
public class OrderController {

    /**
     * 通过 id 查询订单
     */
    @GetMapping("/order/{id}")
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
}
package org.example.feign;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        // 日志级别：
        // NONE: 不记录任何日志（默认）
        // BASIC: 仅记录请求方法、URL、响应状态码和执行时间
        // HEADERS: 记录基本信息以及请求和响应的头信息
        // FULL: 记录请求和响应的所有信息（头、正文、元数据）
        return Logger.Level.FULL;
    }
}
package org.example.util;

import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 服务信息工具类 - 用于获取当前Web服务器的端口号
 *
 * <p>实现ApplicationListener接口监听ServletWebServerInitializedEvent事件，
 * 该事件在Servlet Web服务器（如Tomcat、Netty、Jetty等）初始化完成后触发。</p>
 *
 * <p>注意：</p>
 * <p>1. 在Spring Boot 3.x中，这仍然是获取动态端口的标准方式</p>
 * <p>2. 使用@Component而非@Configuration，因为这不是配置类</p>
 * <p>3. 确保只在Web环境中使用（spring-boot-starter-web存在时）</p>
 *
 * @since Spring Boot 2.0（Spring Framework 4.2+）
 */
@Component
public class ServiceInfoUtil implements ApplicationListener<ServletWebServerInitializedEvent> {
    /**
     * 保存Web服务器初始化事件
     * 注意：静态变量在多实例环境下需要谨慎使用
     * 但在单应用场景下，这是获取端口号的常用模式
     */
    private static ServletWebServerInitializedEvent event;

    /**
     * 处理Web服务器初始化完成事件
     *
     * @param event 包含Web服务器配置信息的事件
     */
    @Override
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        ServiceInfoUtil.event = event;
    }

    /**
     * 获取当前Web服务器实际绑定的端口号
     *
     * @return 端口号，例如7900
     * @throws IllegalStateException 如果Web服务器尚未初始化完成
     */
    public static int getPort() {
        if (event == null) {
            throw new IllegalStateException("Web服务器尚未初始化完成，无法获取端口号");
        }
        return event.getWebServer().getPort();
    }
}

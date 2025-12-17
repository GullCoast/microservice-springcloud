package org.example.feign;

import lombok.extern.slf4j.Slf4j;
import org.example.api.dto.UserDTO;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserDTO getUserById(Long userId) {
        log.warn("用户服务调用失败，触发降级，userId: {}", userId);
        // 返回默认用户
        return UserDTO.builder()
                .id(userId)
                .name("默认用户")
                .email("default@example.com")
                .build();
    }

    @Override
    public Boolean checkUserExists(Long userId) {
        log.warn("用户服务调用失败，返回默认值");
        return false;
    }
}
package org.example.feign;

import org.example.api.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// 指向用户服务（从Nacos获取地址）
@FeignClient(
        name = "microservice-nacos-user",  // 用户服务的注册名
        contextId = "userServiceClient",
        fallback = UserClientFallback.class  // 熔断降级
        //configuration = FeignConfig.class  // 指定配置类（在配置文件中配置了，这里就不用再写了）
)
public interface UserClient {

    /**
     * 调用用户服务的接口
     * 注意：路径和参数必须与用户服务完全一致
     */
    @GetMapping("/api/users/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long userId);

    @GetMapping("/api/users/check/{userId}")
    Boolean checkUserExists(@PathVariable("userId") Long userId);
}

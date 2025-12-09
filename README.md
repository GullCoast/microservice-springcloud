# 微服务示例项目（Nacos 版本）

## 项目描述
这是一个基于Spring Cloud Alibaba的微服务演示项目，使用 **Nacos** 作为服务注册中心，包含两个业务微服务，演示了微服务架构中的服务注册、发现、负载均衡和HTTP调用。

## 项目结构
```
microservice-springcloud/                    # 父项目
├── microservice-nacos-order/               # 订单微服务（端口：7900/7901）
└── microservice-nacos-user/                # 用户微服务（端口：8000）
# 注意：Nacos为独立服务，不再需要注册中心模块
```

## 环境准备
### 1. 启动 Nacos Server（本地安装）
```bash
# 1. 下载Nacos：https://nacos.io/download/nacos-server/?spm=5238cd80.cff869d.0.0.237f7e84ap5bNY
# 2. 解压并进入bin目录

# Linux/Mac
sh startup.sh -m standalone

# Windows
startup.cmd -m standalone

# 访问：http://localhost:8848/nacos

# 访问：http://127.0.0.1:8080/index.html 可进入Nacos控制台页面
```

## 启动顺序
### 1. 启动订单服务（多实例）
```bash
# 终端1 - 启动第一个实例（7900）
cd microservice-eureka-order
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=7900 --eureka.instance.instance-id=${spring.application.name}:7900"

# 终端2 - 启动第二个实例（7901）
cd microservice-eureka-order
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=7901 --eureka.instance.instance-id=${spring.application.name}:7901"
```

### 2. 启动用户服务
```bash
cd microservice-nacos-user
mvn spring-boot:run
```

## 接口测试
### 直接调用订单服务
```
GET http://localhost:7900/order/1
或
GET http://localhost:7901/order/1
```

### 通过用户服务调用（演示负载均衡）
```
GET http://localhost:8000/findOrdersByUser/1
```

### 负载均衡演示
连续访问4次用户服务接口：
```bash
curl http://localhost:8000/findOrdersByUser/1
```
**预期效果**：订单服务控制台交替输出端口号：7900 → 7901 → 7900 → 7901

## 技术栈
- **Spring Boot 3.x** - 微服务应用开发框架
- **Spring Cloud Alibaba Nacos** - 服务注册与发现
- **Spring Cloud LoadBalancer** - 客户端负载均衡
- **RestTemplate + @LoadBalanced** - 服务间HTTP通信

## 关键配置更新
### 1. 依赖变更
每个微服务需要添加：
```xml
<!-- Nacos 服务发现（替换Eureka Client） -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>

<!-- 负载均衡器 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

### 2. 配置文件变更
```yaml
# application.yml
spring:
  application:
    name: microservice-nacos-user  # 服务名
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848  # Nacos地址
```

### 3. 代码不变
```java
@Bean
@LoadBalanced  // 负载均衡注解（保持不变）
public RestTemplate restTemplate() {
    return new RestTemplate();
}

// 服务调用（保持不变）
String url = "http://microservice-nacos-order/order/" + id;
```

## 与Eureka版本的主要区别
1. **注册中心独立**：Nacos作为外部服务运行，不再需要项目内模块
2. **依赖必须添加负载均衡器**：Nacos Discovery不包含负载均衡，需要显式添加
3. **配置更简单**：只需配置Nacos服务器地址

## 迁移总结
✅ **已完成迁移**：从Eureka切换到Nacos  
✅ **功能完全正常**：服务注册、发现、负载均衡  
✅ **代码改动最小**：仅更新依赖和配置文件

**注意**：确保Nacos Server已启动，否则服务无法注册。
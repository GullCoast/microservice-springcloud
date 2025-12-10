# 微服务示例项目（Nacos 版本）

## 项目描述
这是一个基于Spring Cloud Alibaba的微服务演示项目，使用 **Nacos** 作为服务注册中心，**Sentinel** 作为流量控制与熔断降级组件，包含两个业务微服务，演示了微服务架构中的服务注册、发现、负载均衡、HTTP调用以及Sentinel的熔断与降级策略。

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

### 2. 启动 Sentinel Dashboard（可选）
```bash
# 下载 Sentinel 控制台 jar 包
# 启动命令
java -jar sentinel-dashboard-1.8.9.jar

# 访问：http://localhost:8080
```

## 启动顺序
### 1. 启动订单服务（多实例）
```bash
# 终端1 - 启动第一个实例（7900）
cd microservice-nacos-order
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=7900"

# 终端2 - 启动第二个实例（7901）
cd microservice-nacos-order
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=7901"
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

### Sentinel 流量控制演示
#### 1. 订单服务限流测试（QPS限流）
```bash
# 快速连续访问3次以上，触发限流
curl http://localhost:7900/order/1
```
**预期效果**：前2次请求正常响应，第3次请求返回"请求被限流了，请稍后重试！"

#### 2. 用户服务慢调用熔断测试
```bash
# 访问慢调用接口
curl http://localhost:8000/slow
```
**熔断规则**：
- 慢调用比例：50%
- 慢调用阈值：1000ms
- 最小请求数：5
- 熔断时间：10秒

#### 3. 支付服务异常熔断测试
```bash
# 测试异常比例熔断（type=0时抛出异常）
curl "http://localhost:8000/payment/pay?type=0"

# 测试正常请求（type=1时正常响应）
curl "http://localhost:8000/payment/pay?type=1"
```
**熔断规则**：
- 异常比例熔断：异常比例达50%触发熔断，熔断10秒
- 异常数熔断：异常数达5次触发熔断，熔断10秒

## 技术栈
- **Spring Boot 3.x** - 微服务应用开发框架
- **Spring Cloud Alibaba Nacos** - 服务注册与发现
- **Spring Cloud LoadBalancer** - 客户端负载均衡
- **Spring Cloud Alibaba Sentinel** - 流量控制与熔断降级
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

<!-- Sentinel 流量控制与熔断降级 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
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
    sentinel:
      transport:
        dashboard: localhost:8080  # Sentinel控制台地址
        port: 8719  # Sentinel HTTP Server端口
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

## Sentinel 特性演示总结
### ✅ 流量控制
- **订单服务**：QPS限流（每秒2个请求）

### ✅ 熔断降级
1. **慢调用比例熔断**（UserController）
    - 响应时间 > 1000ms视为慢调用
    - 慢调用比例达50%触发熔断
    - 熔断时长10秒

2. **异常比例熔断**（PaymentController）
    - 异常比例达50%触发熔断
    - 最小请求数10个
    - 熔断时长10秒

3. **异常数熔断**（PaymentController）
    - 异常数达5次触发熔断
    - 最小请求数10个
    - 熔断时长10秒

## 与Eureka版本的主要区别
1. **注册中心独立**：Nacos作为外部服务运行，不再需要项目内模块
2. **新增Sentinel支持**：提供流量控制、熔断降级能力
3. **依赖必须添加负载均衡器**：Nacos Discovery不包含负载均衡，需要显式添加
4. **配置更简单**：只需配置Nacos服务器地址

## 迁移总结
✅ **已完成迁移**：从Eureka切换到Nacos  
✅ **新增Sentinel支持**：流量控制、熔断降级  
✅ **功能正常**：服务注册、发现、负载均衡、熔断降级  

**注意**：确保Nacos Server已启动，否则服务无法注册。Sentinel Dashboard为可选组件，用于可视化监控。
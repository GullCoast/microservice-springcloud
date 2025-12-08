# 微服务示例项目

## 项目描述
这是一个基于Spring Cloud的微服务演示项目，包含Eureka服务注册中心和两个业务微服务，演示了微服务架构中的服务注册、发现和HTTP调用。
（仅作学习记录之用）

## 项目结构
```
microservice-springcloud/                    # 父项目
├── microservice-eureka-server/              # Eureka服务注册中心（端口：8761）
├── microservice-eureka-order/               # 订单微服务（端口：7900）
└── microservice-eureka-user/                # 用户微服务（端口：8000）
```

## 启动顺序

### 1. 启动服务注册中心
```bash
# 进入注册中心模块
cd microservice-eureka-server
mvn spring-boot:run
# 访问：http://localhost:8761
```

### 2. 启动订单服务
```bash
# 进入订单服务模块
cd microservice-eureka-order
mvn spring-boot:run
```

### 3. 启动用户服务
```bash
# 进入用户服务模块
cd microservice-eureka-user
mvn spring-boot:run
```

## 接口测试

### 直接调用订单服务
```
GET http://localhost:7900/order/123
响应示例：Order [id=123, price=23.5, receiverName=ltt, ...]
```

### 通过用户服务调用（演示服务间通信）
```
GET http://localhost:8000/findOrdersByUser/1
流程：用户服务 → 订单服务 → 返回订单信息
```

## 技术栈
- **Spring Boot 3.x** - 微服务应用开发框架
- **Spring Cloud Netflix Eureka** - 服务注册与发现
- **RestTemplate** - 服务间HTTP通信

## 核心功能
1. **服务注册与发现** - 各微服务自动注册到Eureka
2. **服务间调用** - 用户服务通过HTTP调用订单服务
3. **独立部署** - 每个服务可独立启动、停止、部署

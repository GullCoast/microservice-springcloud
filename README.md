# 微服务示例项目

## 项目描述
这是一个基于Spring Cloud的微服务演示项目，包含Eureka服务注册中心和两个业务微服务，演示了微服务架构中的服务注册、发现、负载均衡和HTTP调用。
（仅作学习记录之用）

## 项目结构
```
microservice-springcloud/                    # 父项目
├── microservice-eureka-server/              # Eureka服务注册中心（端口：8761）
├── microservice-eureka-order/               # 订单微服务（端口：7900/7901）
└── microservice-eureka-user/                # 用户微服务（端口：8000）
```

## 启动顺序

### 1. 启动服务注册中心
```bash
# 进入注册中心模块
cd microservice-eureka-server
mvn spring-boot:run
# 访问：http://localhost:8761 查看服务注册情况
```

### 2. 启动订单服务（多实例演示）
#### 方式一：使用IDE配置不同端口启动
- **实例1**：Program arguments 添加 `--server.port=7900 --eureka.instance.instance-id=${spring.application.name}:7900`
- **实例2**：Program arguments 添加 `--server.port=7901 --eureka.instance.instance-id=${spring.application.name}:7901`

#### 方式二：命令行启动多个实例
```bash
# 终端1 - 启动第一个实例（7900）
cd microservice-eureka-order
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=7900 --eureka.instance.instance-id=${spring.application.name}:7900"

# 终端2 - 启动第二个实例（7901）
cd microservice-eureka-order
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=7901 --eureka.instance.instance-id=${spring.application.name}:7901"
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
GET http://localhost:7900/order/1
或
GET http://localhost:7901/order/1
响应示例：Order [id=123, price=23.5, receiverName=ltt, ...]
```

### 通过用户服务调用（演示服务间通信与负载均衡）
```
GET http://localhost:8000/findOrdersByUser/123
流程：用户服务 → Spring Cloud LoadBalancer → 轮询调用订单服务 → 返回订单信息
```

### 负载均衡演示
连续访问4次用户服务接口：
```bash
curl http://localhost:8000/findOrdersByUser/1
curl http://localhost:8000/findOrdersByUser/1
curl http://localhost:8000/findOrdersByUser/1
curl http://localhost:8000/findOrdersByUser/1
```
**预期效果**：
- 两个订单服务控制台交替输出端口号：7900 → 7901 → 7900 → 7901
- 验证负载均衡正常工作

## 技术栈
- **Spring Boot 3.x** - 微服务应用开发框架
- **Spring Cloud Netflix Eureka** - 服务注册与发现
- **Spring Cloud LoadBalancer** - 客户端负载均衡（替代Ribbon）
- **RestTemplate + @LoadBalanced** - 服务间HTTP通信

## 核心功能
1. **服务注册与发现** - 各微服务自动注册到Eureka注册中心
2. **客户端负载均衡** - 使用Spring Cloud LoadBalancer实现轮询策略
3. **服务间调用** - 用户服务通过HTTP调用订单服务
4. **多实例部署** - 支持同一服务的多个实例同时运行
5. **独立部署** - 每个服务可独立启动、停止、部署

## 新增功能说明

### 🔄 负载均衡演示
- **技术**：Spring Cloud LoadBalancer
- **策略**：默认轮询（Round Robin）
- **效果**：当订单服务启动两个实例（7900/7901）时，用户服务会自动在两个实例间轮询调用
- **验证**：查看订单服务控制台端口号交替输出

### 🛠️ 配置要点
1. **RestTemplate配置**：
   ```java
   @Bean
   @LoadBalanced  // 启用负载均衡
   public RestTemplate restTemplate() {
       return new RestTemplate();
   }
   ```

2. **服务调用**：
   ```java
   // 使用服务名调用，而非具体IP
   String url = "http://microservice-eureka-order/" + id;
   ```

3. **Eureka实例标识**（确保多实例注册）：
   ```yaml
   eureka:
     instance:
       instance-id: ${spring.application.name}:${server.port}
   ```

## 学习要点
1. **服务发现**：如何通过服务名而非IP进行服务调用
2. **负载均衡**：客户端负载均衡的工作原理
3. **多实例部署**：同一服务的多个实例如何协同工作
4. **微服务通信**：基于HTTP/REST的服务间通信模式

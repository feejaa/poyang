# PoYang RPC框架

![License](https://img.shields.io/github/license/alibaba/dubbo.svg)
![](https://img.shields.io/badge/Github-181717?style=plastic&logo=github&logoColor=white)
## 项目介绍
PoYang是一个基于 Java + Etcd + Vert.x 的RPC 框架，基于对Vert.x网络服务器的学习，以及Java的一些高级特性，其中包括反射、动态代理、序列化器，参考Dubbo、SpringBoot的等框架，基于Etcd和Zookeeper的注册中心、自定义网络协议、动态代理、多种设计模式（单例/工厂/装饰者等）、负载均衡器设计、重试和容错机制、Spring Boot Starter注解驱动开发、依赖注入等，总结实现了一个高性能的RPC框架

该项目总结了自己对RPC框架的理解，是一次从0到1的实践和对自己知识的一场总结
## 架构
![](./docs/tutorial.jpg)
## 技术选型
主要以 Java 为主，但所有的思想和设计都是可以复用到其他语言的，代码不同罢了。
- ⭐️ Vert.x 框架（网络通信框架，基于Netty实现）
- ⭐️ Etcd 云原生存储中间件（jetcd 客户端）
- ⭐️ SPI 机制
- ⭐️ 多种序列化器
    - JSON 序列化
    - Kryo 序列化
    - Hessian 序列化
- ⭐️ 多种设计模式
    - 双检锁单例模式
    - 工厂模式
    - 代理模式
    - 装饰者模式
- ⭐️ 负载均衡器设计
    - 随机负载均衡 
    - 轮询负载均衡
    - 加权随机负载均衡(暂未实现)
- ⭐️ 重试和容错机制（暂未实现完全）
    - 快速失败（Fast-Fail）
    - 静默处理（Fast-Safe）
    - 自动重试（Retry）
    - 降级（Degrade / Fallback）
    - 熔断（Circuit Breaker）
    - 隔离（Bulkhead）
    - 超时控制（Timeout Control）
    - 幂等控制（Idempotency）
- ⭐️ Spring Boot Starter 开发
## 待拓展点
- ⭐支持多种类的配置文件形式，例如(xml、properties等)
- ⭐支持多种类的注册中心，例如(Zookeeper、Consul等)
- ⭐支持多种类的序列化方式，例如(Avro、Protobuf等)
- ⭐支持多种类的负载均衡方式，例如(加权轮询、最少连接数等)
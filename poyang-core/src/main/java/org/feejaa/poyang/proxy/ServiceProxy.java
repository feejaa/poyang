package org.feejaa.poyang.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;
import org.feejaa.poyang.PoYangApplication;
import org.feejaa.poyang.config.PoYangConfig;
import org.feejaa.poyang.constant.RpcConstant;
import org.feejaa.poyang.loadBalance.LoadBalancerFactory;
import org.feejaa.poyang.model.RpcRequest;
import org.feejaa.poyang.model.RpcResponse;
import org.feejaa.poyang.model.ServiceMetaInfo;
import org.feejaa.poyang.protocol.*;
import org.feejaa.poyang.registry.Registry;
import org.feejaa.poyang.registry.RegistryFactory;
import org.feejaa.poyang.serializer.Serializer;
import org.feejaa.poyang.serializer.SerializerFactory;
import org.feejaa.poyang.server.tcp.TcpClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 服务代理
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 指定序列化对象
        Serializer serializer = SerializerFactory.getInstance(
                PoYangApplication.getRpcConfig().getPoyang().getSerializer());

        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // 从注册中心获取服务提供者请求地址
            PoYangConfig rpcConfig = PoYangApplication.getRpcConfig();
            Registry registry = RegistryFactory.getRegistry(rpcConfig.getRegistryConfig().getRegistryType());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.discover(serviceMetaInfo.getServiceKey());
            log.info("serviceMetaInfoList = {}", serviceMetaInfoList);
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                System.out.printf("服务未注册: %s%n", serviceMetaInfo);
                log.error("服务未注册: {}", serviceMetaInfo);
                throw new RuntimeException("服务未注册");
            }
            log.info("服务继续提供者列表: {}", serviceMetaInfoList);
            // 此处可以实现负载均衡 todo
            HashMap<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo serviceMeta  = LoadBalancerFactory.getInstance
                    (rpcConfig.getPoyang().getLoadBalancer()).select(requestParams, serviceMetaInfoList);
            log.info("选择的服务提供者: {}", serviceMeta);
            RpcResponse rpcResponse = TcpClient.doRequest(rpcRequest, serviceMeta);
            log.info("rpcResponse = {}", rpcResponse);
            return rpcResponse.getData();
        } catch (Exception e) {
            log.error("err is = ", e);
            throw new RuntimeException(e.getMessage());
        }
    }
}

package org.feejaa.poyang.proxy;

import org.feejaa.poyang.PoYangApplication;
import org.feejaa.poyang.config.PoYangConfig;
import org.feejaa.poyang.model.RpcRequest;
import org.feejaa.poyang.registry.RegistryFactory;
import org.feejaa.poyang.serializer.Serializer;
import org.feejaa.poyang.serializer.SerializerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 服务代理
 */
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 指定序列化对象
        Serializer serializer = SerializerFactory.getInstance(PoYangApplication.getRpcConfig().getPoyang().getSerializer());

        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        // 序列化
        byte[] bodyBytes = serializer.serialize(rpcRequest);

        // 从注册中心获取服务提供者请求地址
        PoYangConfig rpcConfig = PoYangApplication.getRpcConfig();
        RegistryFactory.getRegistry(rpcConfig.getRegistryConfig());


        return null;
    }
}

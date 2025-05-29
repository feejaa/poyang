package org.feejaa.poyang.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.feejaa.poyang.PoYangApplication;
import org.feejaa.poyang.config.PoYangConfig;
import org.feejaa.poyang.constant.RpcConstant;
import org.feejaa.poyang.model.RpcRequest;
import org.feejaa.poyang.model.RpcResponse;
import org.feejaa.poyang.model.ServiceMetaInfo;
import org.feejaa.poyang.registry.Registry;
import org.feejaa.poyang.registry.RegistryFactory;
import org.feejaa.poyang.serializer.Serializer;
import org.feejaa.poyang.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

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
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            // 从注册中心获取服务提供者请求地址
            PoYangConfig rpcConfig = PoYangApplication.getRpcConfig();
            Registry registry = RegistryFactory.getRegistry(rpcConfig.getRegistryConfig().getRegistryType());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.discover(serviceMetaInfo.getServiceKey());
            Assert.isTrue(CollUtil.isNotEmpty(serviceMetaInfoList), "服务未注册");
            ServiceMetaInfo serviceMeta = serviceMetaInfoList.get(0);
            // 此处可以实现负载均衡 todo

            // 发送请求
            HttpResponse response = HttpRequest.post(serviceMeta.getServiceAddress())
                    .body(bodyBytes).execute();
            byte[] result = response.bodyBytes();
            // 反序列化
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);

            return rpcResponse.getData();
        } catch (IOException e) {


            throw new RuntimeException(e.getMessage());
        }
    }
}

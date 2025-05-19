package org.feejaa.poyang.proxy;

import org.feejaa.poyang.PoYangApplication;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂（用于创建代理对象）
 */
@SuppressWarnings("all")
public class ServiceProxyFacotry {

    /**
     * 根据服务类获取对象
     *
     * @param <T>   服务类类型
     * @param clazz 服务类
     * @return 代理对象，如果配置为mock，则返回null
     */
    public static <T> T getProxy(Class<T> clazz) {
        if (PoYangApplication.getRpcConfig().getPoyang().isMock()) {
            return getDefaultMockProxy(clazz);
        }
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new ServiceProxy());
    }

    private static <T> T getDefaultMockProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new MockServiceProxy()
        );
    }


}

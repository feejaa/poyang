package org.feejaa.boot;

import org.feejaa.base.PoYangInflow;
import org.feejaa.poyang.proxy.ServiceProxyFacotry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

public class PoYangInflowInitPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> beanClass = bean.getClass();

        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(PoYangInflow.class)) {
                continue;
            }
            // 生成代理对象
            PoYangInflow poYangInflow = field.getAnnotation(PoYangInflow.class);
            Class<?> aClass = poYangInflow.interfaceClass();
            Object proxy = ServiceProxyFacotry.getProxy(aClass);

            field.setAccessible(true);
            try {
                field.set(bean, proxy);
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to set proxy for field: " + field.getName(), e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}

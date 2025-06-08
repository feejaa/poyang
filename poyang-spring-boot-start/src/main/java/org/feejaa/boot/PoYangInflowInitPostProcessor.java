package org.feejaa.boot;

import lombok.extern.slf4j.Slf4j;
import org.feejaa.base.PoYangInflow;
import org.feejaa.poyang.proxy.ServiceProxyFacotry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

@Slf4j
public class PoYangInflowInitPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> beanClass = bean.getClass();

        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(PoYangInflow.class)) {
                continue;
            }
            log.info("加载PoyangInflow注解的字段: {}", field.getName());
            // 生成代理对象
            PoYangInflow poYangInflow = field.getAnnotation(PoYangInflow.class);
            Class<?> tClass = poYangInflow.interfaceClass();
            if (tClass == void.class) {
                tClass = field.getType();
            }
            try {
                Object proxy = ServiceProxyFacotry.getProxy(tClass);

                field.setAccessible(true);
                field.set(bean, proxy);
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                log.error("Failed to set proxy for field: {}", field.getName(), e);
                throw new RuntimeException("Failed to set proxy for field: " + field.getName(), e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}

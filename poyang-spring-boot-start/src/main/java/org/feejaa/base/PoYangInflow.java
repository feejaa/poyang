package org.feejaa.base;

import org.feejaa.poyang.constant.RpcConstant;
import org.feejaa.poyang.retry.RetryStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务消费方
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface PoYangInflow {


    Class<?> interfaceClass() default void.class;

    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;


    /**
     * 负载均衡器-暂时未实现
     */
    String loadBalancer() default "";

    /**
     * 重试策略-暂时未实现
     */
    String retryStrategy() default "";

    /**
     * 容错策略-暂时未实现
     */
    String tolerantStrategy() default "";

    /**
     * 模拟调用-暂时未实现
     */
    boolean mock() default false;

}

package org.feejaa.base;

import org.feejaa.boot.PoYangInflowInitPostProcessor;
import org.feejaa.boot.PoYangInitRegistart;
import org.feejaa.boot.PoYangOutflowInitPostProcessor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({PoYangInflowInitPostProcessor.class, PoYangOutflowInitPostProcessor.class, PoYangInitRegistart.class})
public @interface EnablePoYang {

    /**
     * 是否启用
     * @return true 启用，false 不启用
     */
    boolean value() default true;

    /**
     * 是否启用日志
     * @return true 启用，false 不启用
     */
    boolean enableLog() default true;

    /**
     * 是否启用缓存
     * @return true 启用，false 不启用
     */
    boolean enableCache() default true;
}

package org.feejaa.base;

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

package org.feejaa.poyang.serializer;

import org.feejaa.poyang.spi.SpiLoader;

public class SerializerFactory {


    /**
     * 单例模式
     */
    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器
     */
    public static final Serializer DEFAULT_SERIALIZER = new JDKSerializer();

    /**
     * 工厂模式
     * 获取实例
     */
    public static Serializer getInstance(String key) {

        return SpiLoader.getInstance(Serializer.class, key);
    }

}

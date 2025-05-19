package org.feejaa.poyang.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * kryo序列化
 */
public class KryoSerializer implements Serializer {

    /**
     * kyro 线程不安全
     *
     * @throws IOException
     */
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // 设置动态序列化和反序列化类，不提前注册所有类(所有安全类)
        kryo.setRegistrationRequired(false);
        return kryo;
    });



    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        kryoThreadLocal.get().writeClassAndObject(output, obj);
        output.close();
        return outputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        T result = kryoThreadLocal.get().readObject(input, clazz);
        input.close();
        return result;
    }
}

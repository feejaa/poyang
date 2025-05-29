package org.feejaa.poyang.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.feejaa.poyang.model.RpcRequest;
import org.feejaa.poyang.model.RpcResponse;

import java.io.IOException;

/**
 * JSON序列化工具
 */
public class JsonSerializer implements Serializer{

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        T obj = OBJECT_MAPPER.readValue(bytes, clazz);
        if (obj instanceof RpcRequest) {
            return handlerRequest((RpcRequest) obj, clazz);
        }
        if (obj instanceof RpcResponse) {
            return handlerResponse((RpcResponse) obj, clazz);
        }
        return obj;
    }

    private <T> T handlerRequest(RpcRequest req, Class<T> type) throws IOException{
        Class<?>[] parameterTypes = req.getParameterTypes();
        Object[] args = req.getArgs();

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> cl = parameterTypes[i];
            if (!cl.isAssignableFrom(args[i].getClass())) {
                byte[] argBytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(argBytes, cl);
            }
        }

        return type.cast(req);
    }

    private <T> T handlerResponse(RpcResponse response, Class<T> type) throws IOException {
        byte[] dataBytes = OBJECT_MAPPER.writeValueAsBytes(response.getData());
        response.setData(OBJECT_MAPPER.readValue(dataBytes, response.getDataType()));

        return type.cast(response);
    }
}

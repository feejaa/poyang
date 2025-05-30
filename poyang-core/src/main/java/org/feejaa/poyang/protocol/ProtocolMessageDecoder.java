package org.feejaa.poyang.protocol;


import cn.hutool.core.lang.Assert;
import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;
import org.feejaa.poyang.model.RpcRequest;
import org.feejaa.poyang.model.RpcResponse;
import org.feejaa.poyang.serializer.Serializer;
import org.feejaa.poyang.serializer.SerializerFactory;

import java.io.IOException;

@SuppressWarnings("all")
@Slf4j
public class ProtocolMessageDecoder {

    public static ProtocolMessage decode(Buffer buffer) throws IOException {

        // check magic
        byte magic = buffer.getByte(0);
        // check magic
        Assert.isTrue(magic == ProtocolConstant.PROTOCOL_MAGIC, "magic is not equal");

        ProtocolMessage.Header header =
                ProtocolMessage.Header.builder()
                .magic(magic)
                .version(buffer.getByte(1))
                .serializer(buffer.getByte(2))
                .type(buffer.getByte(3))
                .status(buffer.getByte(4))
                .requestId(buffer.getLong(5))
                .bodyLength(buffer.getInt(13))
                .build();
        // 解决粘包问题，只读指定长度的数据
        byte[] bytes = buffer.getBytes(17, (int) (17 + header.getBodyLength()));
        // 解析消息体
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());

        Assert.notNull(serializerEnum, "serializerEnum is null");

        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());

        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        Assert.notNull(messageTypeEnum, "messageTypeEnum is null");
        switch (messageTypeEnum) {
            case REQUEST:
                RpcRequest request = serializer.deserialize(bytes, RpcRequest.class);
                return new ProtocolMessage<>(header, request);
            case RESPONSE:
                RpcResponse response = serializer.deserialize(bytes, RpcResponse.class);
                return new ProtocolMessage<>(header, response);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("messageTypeEnum is not support");
        }
    }
}

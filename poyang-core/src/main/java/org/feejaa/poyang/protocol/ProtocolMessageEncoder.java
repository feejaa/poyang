package org.feejaa.poyang.protocol;

import cn.hutool.core.lang.Assert;
import io.vertx.core.buffer.Buffer;
import org.feejaa.poyang.serializer.Serializer;
import org.feejaa.poyang.serializer.SerializerFactory;

import java.io.IOException;

@SuppressWarnings("all")
public class ProtocolMessageEncoder {

    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {

        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();

        // 写入基础信息
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());

        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        Assert.notNull(serializerEnum, "SerializerEnum is null");
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        // 写入 body
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);

        return buffer;
    }
}

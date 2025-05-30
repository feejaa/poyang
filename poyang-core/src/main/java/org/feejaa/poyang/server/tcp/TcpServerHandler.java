package org.feejaa.poyang.server.tcp;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;
import org.feejaa.poyang.model.RpcRequest;
import org.feejaa.poyang.model.RpcResponse;
import org.feejaa.poyang.protocol.ProtocolMessage;
import org.feejaa.poyang.protocol.ProtocolMessageDecoder;
import org.feejaa.poyang.protocol.ProtocolMessageEncoder;
import org.feejaa.poyang.protocol.ProtocolMessageTypeEnum;
import org.feejaa.poyang.registry.LocalRegistry;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class TcpServerHandler implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket netSocket) {

        // 构建请求
        TcpBufferWrapper tcpBufferWrapper = new TcpBufferWrapper(buffer -> {
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (Exception e) {
                throw new RuntimeException("decode error", e);
            }
            RpcRequest rpcRequest = protocolMessage.getBody();

            // handler req
            try {
                Class<?> tClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = tClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(tClass.newInstance(), rpcRequest.getArgs());
                RpcResponse response = RpcResponse.builder().data(result).dataType(method.getReturnType()).message("ok").build();
                ProtocolMessage.Header header = protocolMessage.getHeader();
                header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
                ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = new ProtocolMessage<>(header, response);
                try {
                    Buffer respByte = ProtocolMessageEncoder.encode(rpcResponseProtocolMessage);
                    netSocket.write(respByte);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });

        netSocket.handler(tcpBufferWrapper);
    }
}

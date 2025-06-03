package org.feejaa.poyang.tolerant;

import org.feejaa.poyang.model.RpcResponse;

import java.util.Map;

/**
 * Fail-Fast容错处理机制
 */
public class FFTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务出错", e);
    }

}

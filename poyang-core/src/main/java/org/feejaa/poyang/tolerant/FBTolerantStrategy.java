package org.feejaa.poyang.tolerant;

import org.feejaa.poyang.model.RpcResponse;

import java.util.Map;

/**
 * Fail-Back 降级策略处理异常
 */
public class FBTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // todo 将当前服务降级
        return null;
    }
}

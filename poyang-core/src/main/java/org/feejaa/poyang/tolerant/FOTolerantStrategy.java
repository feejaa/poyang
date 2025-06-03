package org.feejaa.poyang.tolerant;


import org.feejaa.poyang.model.RpcResponse;

import java.util.Map;

/**
 * Fail-Over 故障转移策略
 */
public class FOTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // todo 获取其他服务节点进行调用
        return null;
    }
}


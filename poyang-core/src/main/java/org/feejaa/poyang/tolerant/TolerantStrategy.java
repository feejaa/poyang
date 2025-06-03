package org.feejaa.poyang.tolerant;

import org.feejaa.poyang.model.RpcResponse;

import java.util.Map;

public interface TolerantStrategy {

    /**
     * 容错处理
     * @param context
     * @param e
     * @return
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);

}

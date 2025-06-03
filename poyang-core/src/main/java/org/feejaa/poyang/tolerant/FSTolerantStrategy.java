package org.feejaa.poyang.tolerant;

import lombok.extern.slf4j.Slf4j;
import org.feejaa.poyang.model.RpcResponse;

import java.util.Map;

/**
 * Fail-Safe 静默处理
 */
@Slf4j
public class FSTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.error("静默处理异常", e);
        return new RpcResponse();
    }
}

package org.feejaa.poyang.retry;

import org.feejaa.poyang.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 默认的这
 */
public class NormalRetryStrategy implements RetryStrategy {

    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return null;
    }

}

package org.feejaa.poyang.retry;

import org.feejaa.poyang.model.RpcResponse;

import java.util.concurrent.Callable;

public interface RetryStrategy {

    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;

}

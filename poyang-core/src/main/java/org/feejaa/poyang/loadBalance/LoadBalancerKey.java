package org.feejaa.poyang.loadBalance;

/**
 * 负载均衡器的键值定义
 * 用于标识不同的负载均衡策略
 */
public interface LoadBalancerKey {

    String ROUND_ROBIN = "roundRobin";

    String RANDOM = "random";

    String CONSISTENT_HASH = "consistentHash";

    String LeastConnections = "LeastConnections";
}

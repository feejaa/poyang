package org.feejaa.poyang.loadBalance;

import org.feejaa.poyang.spi.SpiLoader;

public class LoadBalancerFactory {

    static {
        // 加载负载均衡器SPI
        SpiLoader.load(LoadBalancer.class);
    }

    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RandomLoadBalancer();

    /**
     * 获取负载均衡器实例
     *
     * @param key 负载均衡器的键值
     * @return 负载均衡器实例
     */
    public static LoadBalancer getInstance(String key) {
        // 获取负载均衡器实例
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }

}

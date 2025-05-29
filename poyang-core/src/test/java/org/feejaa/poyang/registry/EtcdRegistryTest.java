package org.feejaa.poyang.registry;

import org.feejaa.poyang.config.RegistryConfig;
import org.feejaa.poyang.model.ServiceMetaInfo;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.feejaa.poyang.registry.EtcdRegistry.ETCD_ROOT_PATH;


public class EtcdRegistryTest {

    final Registry registry = new EtcdRegistry();

    @Before
    public void init(){
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://127.0.0.1:2379");
        registry.init(registryConfig);
    }

    @Test
    public void testRegister() throws Exception{
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("test");
        serviceMetaInfo.setServiceHost("127.0.0.1");
        serviceMetaInfo.setServicePort(1234);
        serviceMetaInfo.setServiceVersion("2.0");
        registry.register(serviceMetaInfo);

    }

    @Test
    public void discover(){
        List<ServiceMetaInfo> serviceMetaInfos = registry.discover("test:2.0");
        System.out.println("服务发现测试");
        System.out.println(serviceMetaInfos);
    }
    @Test
    public void testUnRegister(){
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("test");
        serviceMetaInfo.setServiceHost("127.0.0.1");
        serviceMetaInfo.setServicePort(1234);
        serviceMetaInfo.setServiceVersion("2.0");
        registry.unRegister(serviceMetaInfo);
    }

    @Test
    public void allLifeTimeTest() throws Exception {

        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("test");
        serviceMetaInfo.setServiceHost("127.0.0.1");
        serviceMetaInfo.setServicePort(1234);
        serviceMetaInfo.setServiceVersion("2.0");
        registry.register(serviceMetaInfo);
        List<ServiceMetaInfo> serviceMetaInfos = registry.discover(serviceMetaInfo.getServiceKey());
        System.out.println("服务发现测试");
        System.out.println(serviceMetaInfos);

//        Thread.sleep(5000);

//        registry.unRegister(serviceMetaInfo);

//        registry.destroy();

    }
}
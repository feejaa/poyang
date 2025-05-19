package org.feejaa.poyang.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import dev.failsafe.internal.util.Assert;
import dev.failsafe.internal.util.Lists;
import lombok.extern.slf4j.Slf4j;
import org.feejaa.poyang.serializer.Serializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spi 加载器
 */
@Slf4j
public class SpiLoader {

    /**
     * 存储已经加载的接口类: 接口名 -> (key -> 实现类)
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 对象实例缓存(避免重复 new ), 类路径-》对象实例，单例模式
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    private static final String RPC_SPI_PATH = "META-INF/rpc/sys/";

    private static final String RPC_SPI_CUSTOM_PATH = "META-INF/rpc/custom/";

    // 扫码路径用户自定义的优先
    private static final String[] SCAN_PATHS = new String[]{RPC_SPI_PATH, RPC_SPI_CUSTOM_PATH};

    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    /**
     * 加载所有类型
     */
    public static void loadAll() {
        log.info("开始加载所有 spi 类");
        for (Class<?> clazz : LOAD_CLASS_LIST) {
            load(clazz);
        }
    }

    /**
     * 加载某个类型
     */
    public static Map<String, Class<?>> load(Class<?> clazz) {
        log.info("开始加载 spi 类: {}", clazz.getName());
        Map<String, Class<?>> keyClassMap = new HashMap<>();

        for (String scanPath : SCAN_PATHS) {
            List<URL> resources = ResourceUtil.getResources(scanPath + clazz.getName());
            // 读取每个配置文件
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] strArray = line.split("=");
                        if (strArray.length > 1) {
                            String key = strArray[0];
                            String className = strArray[1];
                            keyClassMap.put(key, Class.forName(className));
                        }
                    }

                } catch (Exception e) {
                    log.error("spi resource load error", e);
                }
            }
        }

        loaderMap.put(clazz.getName(), keyClassMap);
        return keyClassMap;
    }

    @SuppressWarnings("all")
    public static <T> T getInstance(Class<?> tClass, String key) {
        Map<String, Class<?>> classMap = loaderMap.get(tClass.getName());

        Assert.notNull(classMap, "spi class not found: " + tClass.getName());
        Assert.isTrue(classMap.containsKey(key), "spi class not found: " + tClass.getName() + ", key: " + key);

        Class<?> importClass = classMap.get(key);
        // 从实例缓存中获取指定类型的实例
        String importClassName = importClass.getName();
        if (!instanceCache.containsKey(importClassName)) {
            try {
                instanceCache.put(importClassName, importClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("spi instance create error", e);
                String errorMsg = String.format("%s 实例化失败", importClassName);
                throw new RuntimeException(errorMsg, e);
            }
        }

        return (T) instanceCache.get(importClassName);
    }

    public static void main(String[] args) {

    }


}

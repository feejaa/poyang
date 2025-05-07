package org.feejaa.poyang.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.yaml.YamlUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * config util
 */
public class ConfigUtils {

    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    public static <T> T loadConfig(Class<T> tClass, String prefix, String env) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(env)) {
            configFileBuilder.append("-").append(env);
        }
        // try load yaml file
        configFileBuilder.append(".yml");

        return YamlUtil.loadByPath(configFileBuilder.toString(), tClass);
    }
}

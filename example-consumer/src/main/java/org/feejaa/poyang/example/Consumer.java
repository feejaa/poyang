package org.feejaa.poyang.example;

import org.feejaa.poyang.config.PoYangConfig;
import org.feejaa.poyang.utils.ConfigUtils;

public class Consumer {

    public static void main(String[] args) {

        PoYangConfig poYangConfig = ConfigUtils.loadConfig(PoYangConfig.class, "poyang");
        System.out.printf("config" + poYangConfig);
    }
}

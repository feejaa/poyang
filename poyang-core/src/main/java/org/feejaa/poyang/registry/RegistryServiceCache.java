package org.feejaa.poyang.registry;

import org.feejaa.poyang.model.ServiceMetaInfo;

import java.util.List;

public class RegistryServiceCache {

    List<ServiceMetaInfo> serviceMetaInfoCache;

    void writeCache(List<ServiceMetaInfo> newServiceCache) {
        this.serviceMetaInfoCache = newServiceCache;
    }

    List<ServiceMetaInfo> readCache() {
        return this.serviceMetaInfoCache;
    }

    void clearCache() {
        this.serviceMetaInfoCache = null;
    }
}

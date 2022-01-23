package com.pn.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

public class AzureVaultEhCache {

	Logger LOGGER = LogManager.getLogger(getClass());

	public static Cache<String, String> buildInMemoryCache(String cacheName, Class<?> keyType, Class<?> valueType){
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .withCache(cacheName, CacheConfigurationBuilder.newCacheConfigurationBuilder(keyType, valueType,
        ResourcePoolsBuilder.heap(100)))
        .build();

        cacheManager.init();
        
        return (Cache<String, String>) cacheManager.getCache(cacheName, keyType, valueType);
        
        }
}

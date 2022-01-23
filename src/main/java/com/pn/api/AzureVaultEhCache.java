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
	
	static CacheManager cacheManager;
	
	private static final int cacheLimit = 100;

	protected static Cache buildInMemoryCache(String cacheName, Class<?> keyType, Class<?> valueType){
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .withCache(cacheName, CacheConfigurationBuilder.newCacheConfigurationBuilder(keyType, valueType,
        ResourcePoolsBuilder.heap(cacheLimit)))
        .build();

        cacheManager.init();
        
        return getInMemoryCache(cacheName, keyType, valueType);
        
        }
	
	protected static Cache getInMemoryCache(String cacheName, Class<?> keyType, Class<?> valueType){
        
        return cacheManager.getCache(cacheName, keyType, valueType);
        
        }
}

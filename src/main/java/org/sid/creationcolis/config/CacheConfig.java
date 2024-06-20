package org.sid.creationcolis.config;


import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.config.JCacheConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.jcache.JCacheCacheManager;

import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

@Configuration
@EnableCaching
public class CacheConfig extends JCacheConfigurerSupport {

    @Bean
    @Override
    public CacheManager cacheManager() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        javax.cache.CacheManager cacheManager = cachingProvider.getCacheManager(
                cachingProvider.getDefaultURI(), cachingProvider.getDefaultClassLoader());

        MutableConfiguration<Object, Object> configuration = new MutableConfiguration<>()
                .setStoreByValue(false)
                .setStatisticsEnabled(true);

        cacheManager.createCache("colisCache", configuration);

        return new JCacheCacheManager(cacheManager);
    }
}


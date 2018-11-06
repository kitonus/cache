package com.github.kitonus.cache.distributed;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.cache.CachesEndpointAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;

import com.hazelcast.core.HazelcastInstance;

public abstract class CacheManagerConfigAdapter extends CachingConfigurerSupport{

	@Bean
	public CacheManager cacheManager(CacheNameTree cacheNameTree,
			HazelcastInstance hzInstance,
			@Value("${cache.hazelcastTopicName:default}") String hzTopicName,
			@Value("${cache.maxItems:512}") long maxItems, 
			CacheSerializer cacheSerializer){
		CacheManagerImpl impl = new CacheManagerImpl();
		impl.setHazelcastInstance(hzInstance);
		impl.setHazelcastTopicName(hzTopicName);
		impl.setCacheNameTree(cacheNameTree);
		impl.setCacheSerializer(cacheSerializer);
		impl.setCacheNamesToPreInitialize(cacheNamesToPreInitialize());
		impl.setTtlConfig(this.ttlConfig());
		impl.setFirstLevelCacheSize(maxItems);
		return impl;
	}
	
	@Bean
	public CacheNameTree cacheNameTree(){
		CacheNameTreeImpl cacheNameTree = new CacheNameTreeImpl();
		configureCacheNameTree(cacheNameTree);
		return cacheNameTree;
	}
	
	/**
	 * Configure cache dependencies by names
	 * @param cacheNameTree cache name tree
	 */
	protected abstract void configureCacheNameTree(CacheNameTree cacheNameTree);
	
	/**
	 * Configure pre-initialization of caches
	 * @return by default returns empty list
	 */
	protected Collection<String> cacheNamesToPreInitialize(){
		return Collections.emptyList();
	}

	@Override
	public KeyGenerator keyGenerator() {
		return new CacheKeyGeneratorImpl(true);
	}
	
	@Bean
	public CacheSerializer cacheSerializer(){
		return new JacksonSerializer();
	}
	
	protected TimeToLiveConfig ttlConfig(){
		TimeToLiveConfig ttlConfig = new TimeToLiveConfigAdapter();
		this.applyItemTimeToLiveValues(ttlConfig);
		return ttlConfig;
	}
	
	/**
	 * Configure per item class time-to-live
	 * @param ttlConfig time-to-live configuration
	 */
	protected abstract void applyItemTimeToLiveValues(TimeToLiveConfig ttlConfig);
	
	CachesEndpointAutoConfiguration conf;

}

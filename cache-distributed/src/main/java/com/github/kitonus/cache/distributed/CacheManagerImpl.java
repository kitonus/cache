package com.github.kitonus.cache.distributed;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import com.hazelcast.core.HazelcastInstance;

/**
 * Cache manager implementation
 * There should be only 1 cache manager for each JVM
 * @author hanendyo.wicaksono
 *
 */
public class CacheManagerImpl implements CacheManager, InitializingBean, ApplicationListener<ContextClosedEvent> {
	private final Logger log = LoggerFactory.getLogger(CacheManagerImpl.class);
	
	private CacheNameTree cacheNameTree;
	
	private static final ConcurrentHashMap<String, Cache> caches = new ConcurrentHashMap<>();
	
	private String hazelcastTopicName = "default";
	private String hazelcastCacheMapPrefix = "basic.cache.";
	private CacheSerializer cacheSerializer;
	private HazelcastInstance hazelcastInstance;
	private TimeToLiveConfig ttlConfig;
	private long firstLevelCacheSize;
	private Collection<String> cacheNamesToPreInitialize;
	
	/**
	 * Set cache implementations
	 * @param cacheImpls cache implementation classes
	 */
	public void setCacheImplementations(Collection<Cache> cacheImpls){
		if (cacheImpls == null){
			return;
		}
		for (Cache c : cacheImpls){
			if (c instanceof DependencyAwareCache){
				DependencyAwareCache dac = (DependencyAwareCache)c;
				dac.setCacheManager(this);
				dac.setCacheNameTree(this.cacheNameTree);
			}
			caches.put(c.getName(), c);
		}
	}

	@Override
	public Cache getCache(String name) {
		Cache c = caches.get(name);
		if (c == null){
			synchronized(caches){
				c = caches.get(name);
				if (c == null){
					c = new CacheImpl(hazelcastInstance,hazelcastTopicName, this.hazelcastCacheMapPrefix,
							name,
							cacheSerializer, ttlConfig, firstLevelCacheSize);
					if (c instanceof DependencyAwareCache){
						DependencyAwareCache dac = (DependencyAwareCache)c;
						dac.setCacheManager(this);
						dac.setCacheNameTree(this.cacheNameTree);
					}
					caches.put(name, c);
				}
			}
		}
		return c;
	}

	@Override
	public Collection<String> getCacheNames() {
		return caches.keySet();
	}

	public void setCacheNameTree(CacheNameTree cacheNameTree) {
		this.cacheNameTree = cacheNameTree;
	}

	public void setCacheSerializer(CacheSerializer cacheSerializer) {
		this.cacheSerializer = cacheSerializer;
	}

	public void setHazelcastTopicName(String hazelcastTopicName) {
		this.hazelcastTopicName = hazelcastTopicName;
	}

	public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
	}

	public void setCacheNamesToPreInitialize(Collection<String> cacheNamesToPreInitialize) {
		this.cacheNamesToPreInitialize = cacheNamesToPreInitialize;
	}

	public void setTtlConfig(TimeToLiveConfig ttlConfig) {
		this.ttlConfig = ttlConfig;
	}

	public void setFirstLevelCacheSize(long firstLevelCacheSize) {
		this.firstLevelCacheSize = firstLevelCacheSize;
	}

	public void setHazelcastCacheMapPrefix(String hazelcastCacheMapPrefix) {
		this.hazelcastCacheMapPrefix = hazelcastCacheMapPrefix;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.cacheNamesToPreInitialize != null){
			for (String name : cacheNamesToPreInitialize){
				if (log.isDebugEnabled()){
					log.debug("Pre-initialize cache name: "+name);
				}
				this.getCache(name);
			}
		}
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		try {
			this.hazelcastInstance.shutdown();
		} catch (Throwable t) {
			if (log.isDebugEnabled()) {
				log.warn("Failed shutting down Hazelcast Instance: ", t);
			} else {
				log.warn("Failed shutting down Hazelcast Instance: "+t.getMessage());
			}
		}
	}

}

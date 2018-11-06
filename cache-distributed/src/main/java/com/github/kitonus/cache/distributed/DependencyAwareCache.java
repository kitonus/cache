package com.github.kitonus.cache.distributed;

import java.util.UUID;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * Dependency aware cache
 * @author hanendyo.wicaksono
 *
 */
public interface DependencyAwareCache extends Cache {
	public static final String NULL = "NULL"; 
	
	/**
	 * Set cache name tree
	 * @param nameTree name tree
	 */
	public void setCacheNameTree(CacheNameTree nameTree);

	void setCacheManager(CacheManager cacheManager);
	
	void localClear();
	
	public UUID getImplId();
}

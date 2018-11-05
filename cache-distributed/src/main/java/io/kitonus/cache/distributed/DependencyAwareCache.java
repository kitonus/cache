package io.kitonus.cache.distributed;

import java.util.UUID;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public interface DependencyAwareCache extends Cache {
	public static final String NULL = "NULL"; 
	
	public void setCacheNameTree(CacheNameTree nameTree);

	void setCacheManager(CacheManager cacheManager);
	
	void localClear();
	
	public UUID getImplId();
}

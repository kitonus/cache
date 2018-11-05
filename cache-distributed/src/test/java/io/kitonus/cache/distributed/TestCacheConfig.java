package io.kitonus.cache.distributed;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import io.kitonus.cache.distributed.CacheManagerConfigAdapter;
import io.kitonus.cache.distributed.CacheNameTree;
import io.kitonus.cache.distributed.TimeToLiveConfig;

@Configuration
@EnableCaching(proxyTargetClass=true)
public class TestCacheConfig extends CacheManagerConfigAdapter{

	@Override
	protected void configureCacheNameTree(CacheNameTree cacheNameTree) {
		cacheNameTree.addName("TestDTO2", Arrays.asList("TestDTO"));
	}

	@Override
	protected Collection<String> cacheNamesToPreInitialize() {
		return Arrays.asList("TestDTO2", "TestDTO");
	}

	@Override
	protected void applyItemTimeToLiveValues(TimeToLiveConfig ttlConfig) {
		ttlConfig
			.setTimeToLive(TestDTO.class, 1200, TimeUnit.SECONDS)
			.setTimeToLive(TestDTO2.class, 1200, TimeUnit.SECONDS);
	}

}

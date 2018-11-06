package com.github.kitonus.cache.distributed;

import java.util.concurrent.TimeUnit;

public interface TimeToLiveConfig {

	/**
	 * Accessed by CacheImpl to check if item is stale
	 * @param item
	 * @param cacheName cache name
	 * @return true if stale, otherwise returns false
	 */
	public boolean isStale(DeserializedItem item, String cacheName);
	
	/**
	 * Get time-to-live in milliseconds
	 * @param v Actual cached value
	 * @param cacheName cache name
	 * @return time-to-live in milliseconds
	 */
	public long getTimeToLiveMillis(Object v, String cacheName);
	
	/**
	 * Set item time to live by item class
	 * @param cls class type
	 * @param timeToLive time to live
	 * @param timeUnit time-to-live unit
	 * @return TimeToLiveConfig
	 */
	public TimeToLiveConfig setTimeToLive(Class<?> cls, long timeToLive, TimeUnit timeUnit);
	
	/**
	 * Set item time to live by cache name
	 * @param cache name
	 * @param timeToLive time to live
	 * @param timeUnit time-to-live unit
	 * @return TimeToLiveConfig
	 */
	public TimeToLiveConfig setTimeToLive(String cacheName, long timeToLive, TimeUnit timeUnit);

	/**
	 * Define default time to live in milliseconds
	 * @param defaultTimeToLiveMillis default time-to-live in milliseconds
	 * @param timeUnit time-to-live time unit
	 */
	public void setDefaultTimeToLive(long timeToLive, TimeUnit timeUnit);
}

package com.github.kitonus.cache.distributed;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * If timeToLive less than 0 then timeToLive is set to forever
 * @author hanendyo.wicaksono
 *
 */
public class TimeToLiveConfigAdapter implements TimeToLiveConfig {
	private final Logger log = LoggerFactory.getLogger(TimeToLiveConfigAdapter.class);
	
	/**
	 * map of class to millisecond time-to-live
	 */
	private final HashMap<Class<?>, Long> timeToLiveMap = new HashMap<>();
	
	private final HashMap<String, Long> timeToLiveByCacheMap = new HashMap<>();
	
	private long defaultTimeToLiveMillis = 0L;

	@Override
	public boolean isStale(DeserializedItem item, String cacheName) {
		return this.isStale(item.getSerializationTime(), item.getTimeToLiveMillis());
	}
	
	protected Long decideTtl(Long byItemCls, Long byCacheName){
		if (byItemCls != null){
			if (log.isDebugEnabled()){
				log.debug("---> decided byItemCls TTL (millis) = "+byItemCls);
			}
			return byItemCls;
		}
		if (byCacheName != null){
			if (log.isDebugEnabled()){
				log.debug("---> decided to use byCacheName TTL (millis) = "+byCacheName);
			}
			return byCacheName;
		}
		if (log.isDebugEnabled()){
			log.debug("---> decided to use defaultTimeToLiveMillis TTL = "+this.defaultTimeToLiveMillis);
		}
		return this.defaultTimeToLiveMillis;
	}

	@Override
	public TimeToLiveConfig setTimeToLive(String cacheName, long timeToLive, TimeUnit timeUnit) {
		this.timeToLiveByCacheMap.put(cacheName, timeUnit.toMillis(timeToLive));
		return this;
	}
	@Override
	public TimeToLiveConfig setTimeToLive(Class<?> cls, long timeToLive, TimeUnit timeUnit) {
		this.timeToLiveMap.put(cls, timeUnit.toMillis(timeToLive));
		return this;
	}

	protected boolean isStale(long serializationTime, long timeToLiveMillis){
		long itemAgeMillis = System.currentTimeMillis()-serializationTime;
		if (log.isDebugEnabled()){
			log.debug("isStale ? timeToLiveMillis="+timeToLiveMillis+" < itemAgeMillis="+itemAgeMillis+"?");
		}
		if (timeToLiveMillis <= 0){
			if (log.isDebugEnabled()){
				log.debug("timeToLiveMillis is FOREVER");
			}
			return false;
		}
		return timeToLiveMillis < itemAgeMillis;
	}

	@Override
	public void setDefaultTimeToLive(long defaultTimeToLive, TimeUnit timeUnit) {
		this.defaultTimeToLiveMillis = timeUnit.toMillis(defaultTimeToLive);
	}

	@Override
	public String toString() {
		return "TimeToLiveConfigAdapter [timeToLiveMap=" + timeToLiveMap + ", timeToLiveByCacheMap="
				+ timeToLiveByCacheMap + ", defaultTimeToLiveMillis=" + defaultTimeToLiveMillis + "]";
	}

	@Override
	public long getTimeToLiveMillis(Object v, String cacheName) {
		if (v == null){
			return this.defaultTimeToLiveMillis;
		}
		Long ttlByItemCls = timeToLiveMap.get(v.getClass());
		Long ttlByCacheName = timeToLiveByCacheMap.get(cacheName);
		return this.decideTtl(ttlByItemCls, ttlByCacheName);
	}

}

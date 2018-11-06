package com.github.kitonus.cache.distributed;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.AbstractValueAdaptingCache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;

/**
 * Cache implementation Caffeine and Hazelcast map
 * @author hanendyo.wicaksono
 *
 */
public class CacheImpl extends AbstractValueAdaptingCache implements DependencyAwareCache, DisposableBean {

	private Logger log = LoggerFactory.getLogger(CacheImpl.class);
	
	private final UUID cacheImplId = UUID.randomUUID();
	
	private String name;
//	private final Map<String, String> offHeapCache;
	
	private CacheNameTree nameTree;
	private CacheManager manager;
	
	private final CacheSerializer cacheSerializer;
	
	private final TimeToLiveConfig ttlConfig;
	
	private ITopic<Command> topic;
	
	private IMap<String, Serializable> hzMap; 
	
	private final ExecutorService exec = Executors.newFixedThreadPool(calcMaxNoOfThreads());
	private final ExecutorService shortTaskExec = Executors.newCachedThreadPool();
	
	private final com.github.benmanes.caffeine.cache.Cache<String, DeserializedItem> firstLevelCache;
	
	private volatile AtomicLong hit = new AtomicLong(0);
	private volatile AtomicLong miss = new AtomicLong(0);
	
	private static int calcMaxNoOfThreads(){
		int procsPer2 = Runtime.getRuntime().availableProcessors()/2;
		return procsPer2 < 2 ? 2 : procsPer2;
	}
	
	public CacheImpl(HazelcastInstance hz, String hzTopicName, String hzMapNamePrefix, String name, CacheSerializer serializer, 
			TimeToLiveConfig ttlConfig, long firstLevelCacheSize){
		super(false);
		if (name == null || name.length() == 0){
			throw new IllegalArgumentException("name must not be null or empty");
		}
		
		if (log.isInfoEnabled()){
			log.info("<<<----Cache is cofugured as follows---->>>");
			log.info("                   hz="+hz);
			log.info("          hzTopicName="+hzTopicName);
			log.info("                 name="+name);
			log.info("           serializer="+serializer);
			log.info("            ttlConfig="+ttlConfig);
			log.info("  firstLevelCacheSize="+firstLevelCacheSize);
		}
		
		this.name = name;
		this.hzMap = hz.getMap(hzMapNamePrefix+name); 
		
		this.firstLevelCache = Caffeine.newBuilder().maximumSize(firstLevelCacheSize).build();		
		this.cacheSerializer = serializer;
		
		this.topic = hz.getTopic(hzTopicName);
		
		this.topic.removeMessageListener(name);
		this.ttlConfig = ttlConfig;
		this.topic.addMessageListener((message)->
		{
			exec.execute(()->{
				executeCommand(message.getMessageObject());
			});
		});
		
		if (log.isDebugEnabled()){
			log.debug(">>>>CACHE CREATED ==>"+String.valueOf(this));
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Object getNativeCache() {
		return this.firstLevelCache;
	}

	@Override
	public void put(final Object key, final Object value) {
		long serializationTime = System.currentTimeMillis();
		final long ttlMillis = this.ttlConfig.getTimeToLiveMillis(value, this.name);
		_put(String.valueOf(key), value, serializationTime, ttlMillis);
//		shortTaskExec.execute(()->{
//			try {
//				if (value instanceof Map<?,?> || value instanceof Collection<?>){
//					//prevent any form of map or collection to be in distributed cache
//					return;
//				}
//				this.topic.publish(new Command(name, cacheImplId, CommandType.PUT, String.valueOf(key), 
//					this.cacheSerializer.serialize(value, serializationTime, ttlMillis)));
//			} catch (Throwable t){
//				if (log.isDebugEnabled()){
//					log.debug("Failed to serialize:"+t.getMessage(), t);
//				}
//			}
//		});
	}

	private void _put(final String key, final Object value, 
			long serializationTime, final long ttlMillis) {
		
		if (key == null){
			return;
		}
		
		final DeserializedItem desered = new DeserializedItem(value, serializationTime, ttlMillis);
		firstLevelCache.put(key, desered);
		if (log.isDebugEnabled()){
			log.debug(">>In firstLevelCache "+name+". Put key="+String.valueOf(key)+", value="+String.valueOf(value));
		}
		shortTaskExec.execute(()-> {
			try {
				if (value instanceof Object[] || value instanceof List<?>) {
					hzMap.put(key, cacheSerializer.serialize(value, 
							serializationTime, ttlMillis));
				} else if (value instanceof Serializable) {
					hzMap.put(key, new DeserializedItem(value, serializationTime, ttlMillis));
					if (log.isDebugEnabled()){
						log.debug(">>In offHeapCache "+name+". Put as Serializable value. key="+String.valueOf(key)+", value="+String.valueOf(value));
					}											
				} else if (value instanceof Map<?,?> || value instanceof Collection<?>) {
					log.debug("A Map or Collection cannot be saved in off heap cache. Key = "+key);
					return;
				} else {
					hzMap.put(key, cacheSerializer.serialize(value, 
							serializationTime, ttlMillis));
				}
				if (log.isDebugEnabled()){
					log.debug(">>In offHeapCache "+name+". Put key="+String.valueOf(key)+", value="+String.valueOf(value));
				}
			} catch (Throwable t){
				if (log.isDebugEnabled()){
					log.debug("Failed to serialize: "+String.valueOf(value), t);
				}
			}
		});
	}
	
	@Override
	public void evict(final Object key) {
		_evict(String.valueOf(key));
		this.topic.publish(new Command(name, cacheImplId, CommandType.REMOVE, String.valueOf(key), null));
	}
	
	private void _evict(final String key) {
		if (key == null){
			return;
		}
		if (log.isDebugEnabled()){
			log.debug(">>In cache "+this.name+". Key "+String.valueOf(key)+" is being evicted");
		}
		try {
			if (log.isDebugEnabled()){
				log.debug(">>In offHeapCache "+name+". Key "+String.valueOf(key)+" is being evicted");
			}
			firstLevelCache.invalidate(key);
			hzMap.remove(key);
			if (childrenClearingInProgress){
				return;
			}
			synchronized(this){
				childrenClear();
			}
		} catch (Throwable e){
			log.warn("Failed evicting off heap cache");
		}
	}

	private volatile boolean clearingInProgress = false;
	private final Object clearingLock = new Object();
	private volatile boolean childrenClearingInProgress = false;
	private final Object childrenClearingLock = new Object();

	@Override
	public void clear() {
		_clear();
		this.topic.publish(new Command(name, cacheImplId, CommandType.REMOVE_ALL, null, null));
		this.publishChildrenClear();
	}

	private void _clear() {
		if (clearingInProgress){
			if (log.isDebugEnabled()){
				log.debug(">>Clearing in "+this.name+" is in progress (1).  Doing nothing");
			}
			return;
		}
		synchronized(clearingLock){
			if (clearingInProgress){
				if (log.isDebugEnabled()){
					log.debug(">>Clearing in "+this.name+" is in progress (2).  Doing nothing");
				}
				return;
			}
			try {
				clearingInProgress = true;
				localClear();
				
				exec.execute(()-> childrenClear());
			} catch (Throwable t){
				if (log.isDebugEnabled()){
					log.warn("Failed local clear", t);
				} else {
					log.warn("Failed local clear: "+t.getMessage());
				}
			} finally {
				clearingInProgress = false;
			}
			
		}
	}


	@Override
	public String toString() {
		return "CacheImpl [cacheImplId=" + cacheImplId + ", name=" + name + ", offHeapCache=" + hzMap + "]";
	}

	@Override
	public void localClear() {
		if (log.isInfoEnabled()){
			log.info(">>LOCAL CLEAR in "+this.name+" cache");
		}
		this.firstLevelCache.invalidateAll();
		this.hzMap.clear();
	}
	
	private void childrenClear(){
		if (childrenClearingInProgress){
			if (log.isDebugEnabled()){
				log.debug(">>>>Children clearing in "+this.name+" is in progress (1).  Doing nothing");
			}
			return;
		}
		synchronized(childrenClearingLock){
			if (childrenClearingInProgress){
				if (log.isDebugEnabled()){
					log.debug(">>>>Children clearing in "+this.name+" is in progress (2).  Doing nothing");
				}
				return;
			}
			try {
				childrenClearingInProgress = true;
				for (String s : nameTree.getUsedBy(name)){
					if (log.isDebugEnabled()){
						log.debug("   -----> Clearing child cache -> "+s);
					}
					Cache c = manager.getCache(s);
					if (c != null){
						if (c instanceof DependencyAwareCache){
							((DependencyAwareCache)c).localClear();
						} else {
							c.clear();
						}
					}
				}
			} catch (Throwable e) {
				log.error("Failed children clearing", e);
			} finally {
				childrenClearingInProgress = false;
			}
		}
	}
	
	/**
	 * Publish clearing of children
	 */
	private void publishChildrenClear() {
		try {
			for (String s : nameTree.getUsedBy(name)){
				if (log.isDebugEnabled()){
					log.debug("   -----> Publish clearing child cache -> "+s);
				}
				Cache c = manager.getCache(s);
				if (c != null){
					if (c instanceof DependencyAwareCache) {
						DependencyAwareCache dc = (DependencyAwareCache)c;
						this.topic.publish(new Command(dc.getName(), dc.getImplId(), CommandType.REMOVE_ALL, null, null));
					}
				}
			}
		} catch (Throwable e) {
			log.error("Publish children clearing", e);
		}
	}

	private DeserializedItem _getValue(String key){
		try {
			DeserializedItem desered = this.firstLevelCache.getIfPresent(key);
			boolean foundInOffHeap = false;
			if (desered == null){
				Serializable offHeapValue = this.hzMap.get(key);
				if (offHeapValue == null){
					if (log.isDebugEnabled()){
						log.debug("Off heap value is null");
					}
					miss();
					return null;
				}
				desered = offHeapValue instanceof DeserializedItem ? (DeserializedItem)offHeapValue : this.cacheSerializer.deserialize(
						(String)offHeapValue);
				if (log.isDebugEnabled()){
					log.debug("Value found in off heap for key = "+key);
				}
				foundInOffHeap = true;
			}
			if (this.ttlConfig.isStale(desered, this.name)){
				if (log.isDebugEnabled()){
					log.debug("Item is stale. Key = "+String.valueOf(key));
				}
				shortTaskExec.execute(()-> { 
					this.hzMap.remove(key);
					this.firstLevelCache.invalidate(key);
				});
				miss();
				return null;
			}
			if (log.isDebugEnabled()){
				log.debug("Deserialized value for key = "+String.valueOf(key)+" is: "+String.valueOf(desered));
			}
			if (foundInOffHeap) {
				if (log.isDebugEnabled()){
					log.debug("Copy value found in off heap into first level for key = "+key);
				}
				this.firstLevelCache.put(key, desered);
			}
			hit();
			return desered;
		} catch (Throwable e){
			if (log.isDebugEnabled()){
				log.debug("Failed getting from off heap cache for key = "+String.valueOf(key), e);
			}
			miss();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		T v = (T)this.get(key);
		if (v == null){
			try {
				v = valueLoader.call();
				this.put(key, v);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return v;
	}

	private static final ValueWrapper NULL_VALUE_WRAPPER = ()->null;
	
	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		ValueWrapper existingValue = get(key);
		if (this.get(key) == null){
			put(key, value);			
			return NULL_VALUE_WRAPPER;
		}
		return existingValue;
	}

//	private static final DeserializedItem DUMMY_DESERIALIZED = new DeserializedItem(null, 0, 0);
	
	private void executeCommand(Command cmd){
		try {
			if (cmd == null || !this.name.equals(cmd.getCacheName())){
				if (log.isDebugEnabled()){
					log.debug("Command is null or cache name is different");
				}
				return;
			}
			
			if (this.cacheImplId.equals(cmd.getCacheImplId())){
				if (log.isDebugEnabled()){
					log.debug("Command type:"+cmd.getType()+", key:"+cmd.getKey()+" is ignored");
				}
				return;
			}
			
			if (log.isDebugEnabled()){
				log.debug("cacheImplId = "+this.cacheImplId.toString()+" vs cmd.getCacheImplId() -> "+cmd.getCacheImplId());
			}
			CommandType type = cmd.getType();
			if (log.isDebugEnabled()){
				log.debug("Executing command: "+type.toString()+", key: "+cmd.getKey());
			}
			
//			DeserializedItem desered;
//			//Object value;
//			String serializedValue = cmd.getSerializedValue();
//			if (serializedValue != null){
//				desered = this.cacheSerializer.deserialize(serializedValue);
//				value = desered.getValue();
//			} else {
//				desered = DUMMY_DESERIALIZED;
//				value = null;
//			}
			switch(type){
				//case PUT : this._put(cmd.getKey(), value, desered.getSerializationTime(), desered.getTimeToLiveMillis()); break; //DO NOTHING ON PUT
				case REMOVE : this._evict(cmd.getKey()); break;
				case REMOVE_ALL : this._clear(); break;
				default : break;
			}
		} catch (Throwable t){
			if (log.isDebugEnabled()){
				log.debug("Failed to execute command", t);
			}
		}
	}

	@Override
	public void setCacheNameTree(CacheNameTree nameTree) {
		this.nameTree = nameTree;
	}

	@Override
	public void setCacheManager(CacheManager cacheManager) {
		this.manager = cacheManager;
	}

	@Override
	public void destroy() throws Exception {
		this.exec.shutdown();
		this.shortTaskExec.shutdown();
	}

	@Override
	protected Object lookup(Object key) {
		DeserializedItem desered = this._getValue(String.valueOf(key));
		if (desered == null){
			return null;
		}
		return desered.getValue();
	}
	
	private void hit(){
		if (hit.compareAndSet((long)Integer.MAX_VALUE, 0l)){
			miss.set(0l);
			return;
		}
		hit.getAndIncrement();
	}
	
	private void miss(){
		if (miss.compareAndSet((long)Integer.MAX_VALUE, 0l)){
			hit.set(0l);
			return;
		}
		miss.getAndIncrement();
	}
	
	public long getHits(){
		return hit.get();
	}
	
	public long getMisses(){
		return miss.get();
	}
	
	public long size(){
		return Math.max(this.firstLevelCache.estimatedSize(), this.hzMap.size());
	}

	@Override
	public UUID getImplId() {
		return this.cacheImplId;
	}
}

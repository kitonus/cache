package com.github.kitonus.cache.distributed;

/**
 * Template for cache serializer
 * @author hanendyo.wicaksono
 *
 */
public interface CacheSerializer {
	
	/**
	 * Serialize
	 * @param object object to be deserialized
	 * @param serializationTime time of serialization
	 * @param timeToLive item time to live
	 * @return serialized object
	 */
	public String serialize(Object object, long serializationTime, long timeToLive);
	
	/**
	 * Deserialize from String
	 * @param serialized serialized object form
	 * @return deserialized object container
	 */
	public DeserializedItem deserialize(String serialized);
}

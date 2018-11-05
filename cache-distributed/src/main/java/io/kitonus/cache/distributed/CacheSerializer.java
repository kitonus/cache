package io.kitonus.cache.distributed;

public interface CacheSerializer {
	public String serialize(Object object, long serializationTime, long timeToLive);
	
	public DeserializedItem deserialize(String serialized);
}

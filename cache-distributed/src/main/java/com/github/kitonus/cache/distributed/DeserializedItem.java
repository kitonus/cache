package com.github.kitonus.cache.distributed;

import java.io.Serializable;

public class DeserializedItem implements Serializable{
	private static final long serialVersionUID = 1L;
	private Object value;
	private long serializationTime;
	private long timeToLiveMillis;
	
	public DeserializedItem(Object value, long serializationTime, long timeToLiveMillis){
		this.value = value;
		this.serializationTime = serializationTime;
		this.timeToLiveMillis = timeToLiveMillis;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public long getSerializationTime() {
		return serializationTime;
	}

	public void setSerializationTime(long serializationTime) {
		this.serializationTime = serializationTime;
	}

	public long getTimeToLiveMillis() {
		return timeToLiveMillis;
	}

	public void setTimeToLiveMillis(long timeToLive) {
		this.timeToLiveMillis = timeToLive;
	}
}

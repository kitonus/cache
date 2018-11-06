package com.github.kitonus.cache.distributed;

import java.io.Serializable;
import java.util.UUID;

public class Command implements Serializable{
	private static final long serialVersionUID = 1L;
	private CommandType type;
	private String cacheName;
	private UUID cacheImplId;
	private String key;
	private String serializedValue;
	
	public Command(){}
	
	public Command(String cacheName, UUID cacheImplId, CommandType type, String key, String serializedValue){
		this.cacheName = cacheName;
		this.cacheImplId = cacheImplId;
		this.type = type;
		this.key = key;
		this.serializedValue = serializedValue;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public CommandType getType() {
		return type;
	}
	public void setType(CommandType type) {
		this.type = type;
	}
	public String getSerializedValue() {
		return serializedValue;
	}
	public void setSerializedValue(String serializedValue) {
		this.serializedValue = serializedValue;
	}
	public UUID getCacheImplId() {
		return cacheImplId;
	}
	public void setCacheImplId(UUID cacheImplId) {
		this.cacheImplId = cacheImplId;
	}
	public String getCacheName() {
		return cacheName;
	}
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
}

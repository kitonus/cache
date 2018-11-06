package com.github.kitonus.cache.distributed;

import java.util.List;

public abstract class AbstractJsonSerializer implements CacheSerializer {
	public static final char TYPE_CODE_LIST = 'l';
	
	@Override
	public String serialize(Object obj, long serializationTime, long timeToLive){
		try {
			StringBuilder b = new StringBuilder().append(getType(obj));
			Object processed = preProcess(obj);
			if (obj == null){
				return b.append(DependencyAwareCache.NULL).append(":")
						.append(serializationTime).append(":")
						.append(timeToLive).append(":")
						.append(toJson(processed)).toString();
			}
			return b.append(getClsName(processed)).append(":")
					.append(serializationTime).append(":")
					.append(timeToLive).append(":")
					.append(toJson(processed)).toString();
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
	
	protected char getType(Object obj){
		return obj instanceof List<?> ? TYPE_CODE_LIST : 'o';
	}
	
	protected Object preProcess(Object obj) {
		return obj;
	}

	@Override
	public DeserializedItem deserialize(String serialized){
		try {
			int firstColonIdx = serialized.indexOf(":");
			char typeCode = serialized.charAt(0);
			String clsName = serialized.substring(1, firstColonIdx);
			int secondColonIdx = serialized.indexOf(":", firstColonIdx+1);
			String timeStampStr = serialized.substring(firstColonIdx+1, secondColonIdx);
			int thirdColonIdx = serialized.indexOf(":", secondColonIdx+1);
			String ttlStr = serialized.substring(secondColonIdx+1, thirdColonIdx);
			if (DependencyAwareCache.NULL.equals(clsName)){
				return new DeserializedItem(null, Long.valueOf(timeStampStr), Long.valueOf(ttlStr));
			}
			String json = serialized.substring(thirdColonIdx+1);
			return new DeserializedItem(
					fromJson(json, Class.forName(clsName), typeCode), 
						Long.valueOf(timeStampStr), Long.valueOf(ttlStr));
		} catch (Exception e ) {
			throw new RuntimeException(e);
		}
	}
	
	protected String getClsName(Object object){
		return object.getClass().getName();
	}
	
	protected abstract String toJson(Object object) throws Exception;
	
	protected abstract Object fromJson(String str, Class<?> cls, char typeCode) throws Exception;
}

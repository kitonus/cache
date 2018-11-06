package com.github.kitonus.cache.distributed;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CacheKeyGeneratorImpl implements KeyGenerator {
	private final ObjectMapper jackson; 
	private boolean useMethodOwnerClassInKey = true;//by default use method owner class
	public CacheKeyGeneratorImpl(boolean useMethodOwnerClassInKey){
		this.useMethodOwnerClassInKey = useMethodOwnerClassInKey;
		this.jackson = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
	}
	
	public CacheKeyGeneratorImpl(ObjectMapper jackson, boolean useMethodOwnerClassInKey){
		this.useMethodOwnerClassInKey = useMethodOwnerClassInKey;
		this.jackson = jackson;
		
	}
	@Override
	public Object generate(Object target, Method method, Object... params) {
		try {
			if (useMethodOwnerClassInKey){
					return new StringBuilder(target.getClass().getSimpleName())
							.append(".")
							.append(method.getName()).append(jackson.writeValueAsString(params)).toString();
			}
			return new StringBuilder(method.getName()).append(jackson.writeValueAsString(params)).toString();
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}

}

package com.github.kitonus.cache.distributed;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSerializer extends AbstractJsonSerializer {
	private final ObjectMapper jackson = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
	
	private final Logger log = LoggerFactory.getLogger(JacksonSerializer.class);

	@Override
	protected String toJson(Object object) throws Exception {
		try {
			return jackson.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Object fromJson(String str, Class<?> cls, char typeCode) throws Exception{
		if (cls.isArray()){
			if (typeCode == TYPE_CODE_LIST){
				if (log.isDebugEnabled()){
					log.debug("--->fromJson(): Returning list of: "+cls.getName());
				}
				return Arrays.asList((Object[])jackson.readValue(str, cls));
			}
			if (log.isDebugEnabled()){
				log.debug("--->fromJson(): Returning array: "+str);
			}
			return jackson.readValue(str, cls);			
		}
		return jackson.readValue(str, cls);
	}

	@Override
	protected String getClsName(Object object) {
		if (object instanceof Object[]){
			Object[] array = (Object[])object;
			if (array.length > 0){
				return new StringBuilder("[L").append(array[0].getClass().getName()).append(";").toString();
			}
		}
		return object.getClass().getName();
	}

	@Override
	protected Object preProcess(Object object) {
		if (object instanceof List<?>){
			Object[] a = ((List<?>)object).toArray();
			if (log.isDebugEnabled()){
				log.debug("--->preProcess(): Object is a list");
				log.debug("--->preProcess(): Object converted to: "+a.getClass().getName());
			}
			return a;
		}
		return object;
	}

}

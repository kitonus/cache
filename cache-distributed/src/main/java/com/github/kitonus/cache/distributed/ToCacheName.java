package com.github.kitonus.cache.distributed;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Converter from class to cache name
 * @author hanendyo.wicaksono
 *
 */
public class ToCacheName {

	/**
	 * Convert a class to cache name
	 * @param cls java class
	 * @return cache name
	 */
	public static String name(Class<?> cls){
		return cls.getSimpleName();
	}
	
	
	/**
	 * Convert classes to cache name
	 * @param classes classes
	 * @return array of cache names
	 */
	public static String[] names(Class<?>...classes){
		String[] names = new String[classes.length];
		int i = 0;
		for (Class<?> c : classes){
			names[i++] = c.getSimpleName();
		}
		return names;
	}

	/**
	 * Convert classes to cache name
	 * @param classes classes
	 * @return array of cache names
	 */
	public static Collection<String> names(Collection<Class<?>> classes){
		ArrayList<String> names = new ArrayList<>(classes.size());
		for (Class<?> c : classes){
			names.add(c.getSimpleName());
		}
		return names;
	}
}

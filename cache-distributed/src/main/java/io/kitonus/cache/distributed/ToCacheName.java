package io.kitonus.cache.distributed;

import java.util.ArrayList;
import java.util.Collection;

public class ToCacheName {

	/**
	 * Convert a class to cache name
	 * @param cls
	 * @return
	 */
	public static String name(Class<?> cls){
		return cls.getSimpleName();
	}
	
	
	/**
	 * Convert classes to cache name
	 * @param classes
	 * @return
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
	 * @param classes
	 * @return
	 */
	public static Collection<String> names(Collection<Class<?>> classes){
		ArrayList<String> names = new ArrayList<>(classes.size());
		for (Class<?> c : classes){
			names.add(c.getSimpleName());
		}
		return names;
	}
}

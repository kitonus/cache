package com.github.kitonus.cache.distributed;

import java.util.Collection;
import java.util.Map;

/**
 * Cache name tree
 * @author hanendyo.wicaksono
 *
 */
public interface CacheNameTree {

	/**
	 * Add name
	 * @param name cache name
	 * @param dependsOnNames dependencies cache names
	 * @return cache name tree
	 */
	public CacheNameTree addName(String name, Collection<String> dependsOnNames);

	/**
	 * Add Class and their dependencies
	 * @param cls name based on simple class name
	 * @param dependsOnCls dependencies cache names based on simple class name
	 * @return cache name tree
	 */
	public CacheNameTree addCls(Class<?> cls, Collection<Class<?>> dependsOnCls);

	/**
	 * Get name and its user
	 * @param name cache name
	 * @return caches that depends on this name
	 */
	public String[] getUsedBy(String name);

	/**
	 * Get and its users
	 * @param cls class to be used to derive cache name (by simple class name)
	 * @return caches that depends on this name
	 */
	public String[] getUsedBy(Class<?> cls);

	/**
	 * Set dependency definitions
	 * @param dependencyMap dependency map
	 * @return cache name tree
	 */
	CacheNameTree setDependencyDefinitions(Map<Class<?>, Class<?>[]> dependencyMap);
}

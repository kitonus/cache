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
	 * @param name
	 * @param dependsOn
	 */
	public CacheNameTree addName(String name, Collection<String> dependsOnNames);

	/**
	 * Add Class and their dependencies
	 * @param cls
	 * @param dependsOnCls
	 */
	public CacheNameTree addCls(Class<?> cls, Collection<Class<?>> dependsOnCls);

	/**
	 * Get name and its user
	 * @param name
	 * @return
	 */
	public String[] getUsedBy(String name);

	/**
	 * Get and its users
	 * @param cls
	 * @return
	 */
	public String[] getUsedBy(Class<?> cls);

	/**
	 * Set dependency definitions
	 * @param dependencyMap
	 */
	CacheNameTree setDependencyDefinitions(Map<Class<?>, Class<?>[]> dependencyMap);
}

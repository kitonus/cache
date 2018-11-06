package com.github.kitonus.cache.distributed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;

public class CacheNameTreeImpl implements InitializingBean, CacheNameTree{
	
	private final Map<String, List<String>> dependsOnMap = new HashMap<String, List<String>>();
	
	private final Map<String, List<String>> usedByMap = new HashMap<String, List<String>>();

	/* (non-Javadoc)
	 * @see id.co.fifgroup.fifcore.om.util.cache.CacheNameTree#addName(java.lang.String, java.lang.String[])
	 */
	@Override
	public CacheNameTree addName(String name, Collection<String> dependsOn){
		List<String> l = dependsOnMap.get(name);
		if (l == null){
			l = new ArrayList<String>(dependsOn);
			dependsOnMap.put(name, l);
		} else {
			for (String s : dependsOn){
				l.add(s);
			}
		}
		return this;
	}
	
	/* (non-Javadoc)
	 * @see id.co.fifgroup.fifcore.om.util.cache.CacheNameTree#addCls(java.lang.Class, java.lang.Class)
	 */
	@Override
	public CacheNameTree addCls(Class<?> cls, Collection<Class<?>> dependsOnCls){
		return addName(ToCacheName.name(cls), ToCacheName.names(dependsOnCls));
	}
	
	private void putUsedByMap(String name, String usedBy){
		List<String> list = this.usedByMap.get(name);
		if (list == null){
			list = new ArrayList<String>();
			list.add(usedBy);
			this.usedByMap.put(name, list);
		} else {
			list.add(usedBy);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		for (String name : dependsOnMap.keySet()){
			List<String> dependsOnList = dependsOnMap.get(name);
			if (dependsOnList != null){
				for (String s : dependsOnList){
					putUsedByMap(s, name);
				}
			}
		}
	}
	
	private final ConcurrentHashMap<String, String[]> usedByCache = new ConcurrentHashMap<String, String[]>();
	
	/* (non-Javadoc)
	 * @see id.co.fifgroup.fifcore.om.util.cache.CacheNameTree#getNameAndItsUsers(java.lang.String)
	 */
	@Override
	public String[] getUsedBy(String name){
		String[] result = this.usedByCache.get(name);
		if (result == null){
			synchronized(usedByCache){
				result = this.usedByCache.get(name);
				if (result == null){
					List<String> out = new ArrayList<String>();
					Map<String, Boolean> traverseMap = new HashMap<String, Boolean>();
					getUsedBy(name, traverseMap);
					for (String k : traverseMap.keySet()){
						if (Boolean.TRUE.equals(traverseMap.get(k))){
							out.add(k);
						}
					}
					
					result = out.toArray(new String[out.size()]);
				}
				this.usedByCache.put(name, result);
			}
		}
		return result;
	}
	
	private void getUsedBy(String name, Map<String,Boolean> traverseMap){
		List<String> usedBy = this.usedByMap.get(name);
		if (usedBy != null && usedBy.size() > 0){
			for (String ub : usedBy){
				if (!Boolean.TRUE.equals(traverseMap.get(ub))){
					traverseMap.put(ub, true);
					getUsedBy(ub, traverseMap);
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see id.co.fifgroup.fifcore.om.util.cache.CacheNameTree#getNameAndItsUsers(java.lang.Class)
	 */
	@Override
	public String[] getUsedBy(Class<?> cls){
		return getUsedBy(ToCacheName.name(cls));
	}
	
	@Override
	public CacheNameTree setDependencyDefinitions(Map<Class<?>, Class<?>[]> dependencyMap){
		for (Entry<Class<?>, Class<?>[]> entry : dependencyMap.entrySet()){
			this.addCls(entry.getKey(), entry.getValue());
		}
		return this;
	}

	private void addCls(Class<?> key, Class<?>[] value) {
		addName(ToCacheName.name(key), ToCacheName.names(Arrays.asList(value)));
	}
	
}

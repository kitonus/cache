package io.kitonus.cache.distributed;

/**
 * Cache command types
 * @author hanendyo.wicaksono
 *
 */
public enum CommandType {
	/**
	 * Put operation
	 */
	PUT,
	
	/**
	 * Remove/evict one item
	 */
	REMOVE,
	
	/**
	 * Remove all / evict all / clear operation
	 */
	REMOVE_ALL
}

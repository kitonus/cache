package com.github.kitonus.cache.distributed.test;

import java.util.Locale;

import org.junit.Test;

import com.github.kitonus.cache.distributed.CacheKeyGeneratorImpl;

public class TestCacheKeyGenerator {

	@Test
	public void test() throws NoSuchMethodException, SecurityException{
		CacheKeyGeneratorImpl gen = new CacheKeyGeneratorImpl(false);
		System.out.println(
				gen.generate(
					this, 
						TestCacheKeyGenerator.class.getMethod("test", (Class<?>[])null), new Object[]{Locale.ENGLISH}));
		System.out.println(
				gen.generate(
					this, 
						TestCacheKeyGenerator.class.getMethod("test", (Class<?>[])null), (Object[])null));
	}
}

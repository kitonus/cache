package io.kitonus.cache.distributed.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestInstanceof {

	@Test
	public void test() {
		Object x = "abc";
		assertFalse(x instanceof Object[]);
		assertTrue(x instanceof Object);
		System.out.println(x instanceof Object[]);
		System.out.println(x instanceof Object);
	}
}

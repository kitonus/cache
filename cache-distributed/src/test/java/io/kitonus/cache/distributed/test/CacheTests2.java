package io.kitonus.cache.distributed.test;

import java.util.Scanner;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.kitonus.cache.distributed.TestService;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CacheTests2 {

	@Autowired
	TestService service;
	
	@Test
	public void test2() throws InterruptedException{
		Scanner scan = new Scanner(System.in);
		System.out.print("Provide a name (use 'exit' name to quit) ... ");
		String name = scan.next();
		while (!"exit".equals(name)){
			System.out.println("Found dto = "+service.getDTO(name));
			System.out.print("Provide a name (use 'exit' name to quit) ... ");
			name = scan.next();
		}
		scan.close();
		
	}

}

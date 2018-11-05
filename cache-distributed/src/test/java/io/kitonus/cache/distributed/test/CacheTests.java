package io.kitonus.cache.distributed.test;

import java.util.Scanner;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

import io.kitonus.cache.distributed.TestDTO;
import io.kitonus.cache.distributed.TestDTO2;
import io.kitonus.cache.distributed.TestService;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CacheTests {

	@Autowired
	TestService service;
	
	@Autowired
	CacheManager cm;
	
	@Test
	public void test0() throws InterruptedException{
		System.out.println("--------------test0-----------------");
		TestDTO dto = service.getDefault();
		System.out.println("dto2 --> "+String.valueOf(dto));
		Thread.sleep(1000);
		TestDTO dto2 = service.getDefault();
		Assert.assertNotNull(dto2);
		System.out.println("dto2 --> "+String.valueOf(dto2));
		Thread.sleep(10000);
		TestDTO dto3 = service.getDefault();
		Assert.assertNotNull(dto3);
		System.out.println("dto3 --> "+String.valueOf(dto2));
	}
	
	@Test
	public void test1() throws InterruptedException{
		Scanner scan = new Scanner(System.in);
		System.out.println("============TEST null===========");
		TestDTO nullDto = service.getNull();
		System.out.println("Null dto = "+nullDto);
		nullDto = service.getNull();
		System.out.println("Null dto the second time = "+nullDto);
		
		if ("true".equals(System.getProperty("interactive"))){
			System.out.println("Enter to continue");
			scan.nextLine();
		}
		
		System.out.println("============TEST 1!=============");
		TestDTO dt11 = service.getDTO("dto1Name1 in test1");
		System.out.println("key 'dto1Name1 in test1' is assigned to dt11 ->"+dt11);
		
		TestDTO dt11_1 = service.getDTO("dto1Name1 in test1");
		System.out.println("key 'dto1Name1 in test1' is assigned to dt11_1 ->"+dt11_1);
		
		if (!"true".equals(System.getProperty("interactive"))){
			Assert.assertTrue(dt11.equals(dt11_1));
		}
		
		TestDTO2 dt21 = service.getDTO2("dto2Name2 in test1");
		System.out.println("key 'dto2Name2 in test1' is assigned to dt21 ->"+dt21);
		
		TestDTO2 dt21_1 = service.getDTO2("dto2Name2 in test1");
		System.out.println("key 'dto2Name2 in test1' is assigned to dt21_1 ->"+dt21_1);
		
		if (!"true".equals(System.getProperty("interactive"))){
			Assert.assertTrue(dt21.equals(dt21_1));
		}
		
		service.saveDTO2("dto2Name3 in test1");
		System.out.println("***key 'dto2Name3 in test1' should be cleared!!! please verify!!!");
		
		Thread.sleep(2000);

		TestDTO dt11_2 = service.getDTO("dto1Name1 in test1");
		System.out.println("key 'dto1Name1 in test1' => "+dt11_2);
		
		if (!"true".equals(System.getProperty("interactive"))){
			System.out.println("dt11 equals dt11_2????");
			Assert.assertTrue(dt11.equals(dt11_2));
		}
		
		TestDTO2 dt21_2 = service.getDTO2("dto2Name2 in test1");
		System.out.println("key 'dto2Name2 in test1' (dt21_2) = "+dt21_2);
		
		//Assert.assertFalse(dt21 == dt21_2);
		if ("true".equals(System.getProperty("interactive"))){
			System.out.println("Enter to continue");
			scan.nextLine();
		}

		System.out.println("============TEST 2!=============");
		dt11 = service.getDTO("dto1Name1 in test2");
		System.out.println("key 'dto2Name1 in test2' (dt11) = "+dt11);
		
		dt11_1 = service.getDTO("dto1Name1 in test2");
		System.out.println("key 'dto2Name1 in test2' (dt11_1) = "+dt11_1);
		
		if (!"true".equals(System.getProperty("interactive"))){
			System.out.println("dt11 equals dt11_1????");
			Assert.assertTrue(dt11.equals(dt11_1));
		}
		
		dt21 = service.getDTO2("dto2Name2 in test2");
		System.out.println("key 'dto2Name2 in test2' (dt21) = "+dt21);
		
		dt21_1 = service.getDTO2("dto2Name2 in test2");
		System.out.println("key 'dto2Name2 in test2' (dt21_1) = "+dt21_1);
		
		if (!"true".equals(System.getProperty("interactive"))){
			System.out.println("dt21 equals dt21_1????");
			Assert.assertTrue(dt21.equals(dt21_1));
		}
		
		service.saveDTO("dto2Name3");
		System.out.println("TestDTO and TestDTO2 caches should be cleared");
		
		Thread.sleep(2000);
		
		dt11_2 = service.getDTO("dto1Name1 in test2");
		System.out.println(dt11_2);
		
		if (!"true".equals(System.getProperty("interactive"))){
			Assert.assertTrue(dt11.equals(dt11_2));
		}
		
		dt21_2 = service.getDTO2("dto2Name2 in test2");
		System.out.println(dt21_2);
		
		if (!"true".equals(System.getProperty("interactive"))){
			Assert.assertFalse(dt21.equals(dt21_2));
		}
		scan.close();
	}

}

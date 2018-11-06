package com.github.kitonus.cache.distributed;

import java.util.Date;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TestService {
	@Cacheable("TestDTO")
	public TestDTO getNull(){
		return null;
	}
	
	@Cacheable("TestDTO")
	public TestDTO getDefault(){
		return new TestDTO("bleh bleh", new Date());
	}
	
	@Cacheable("TestDTO")
	public TestDTO getDTO(String name){
		TestDTO t = new TestDTO();
		t.setName(name);
		t.setDateOfBirth(new Date());
		return t;
	}
	
	@CacheEvict("TestDTO")
	public void saveDTO(String name){
	}
	
	@CacheEvict("TestDTO2")
	public void saveDTO2(String name){
		
	}

	@Cacheable("TestDTO2")
	public TestDTO2 getDTO2(String name){
		TestDTO2 dto2 = new TestDTO2();
		dto2.setName(name);
		return dto2;
	}

}

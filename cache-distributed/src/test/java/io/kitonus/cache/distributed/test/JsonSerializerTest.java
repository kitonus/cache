package io.kitonus.cache.distributed.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.kitonus.cache.distributed.JacksonSerializer;
import io.kitonus.cache.distributed.TestDTO;

public class JsonSerializerTest {

	static JacksonSerializer ser;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ser = new JacksonSerializer();
	}

	@Test
	public void test() {
		String nullSerialized = ser.serialize(null, System.currentTimeMillis(), 20000l);
		System.out.println("null -> "+nullSerialized);
		System.out.println("null deserialized ->" + ser.deserialize(nullSerialized).getValue());
		
		TestDTO dto = new TestDTO();
		dto.setDateOfBirth(new Date());
		dto.setName("Kiton");
		System.out.println("Before: "+dto.toString());
		String s = ser.serialize(dto, System.currentTimeMillis(), 20000l);
		System.out.println(s);
		
		TestDTO dto2 = (TestDTO) ser.deserialize(s).getValue();
		System.out.println("Deserialized: "+dto2);
		
		List<TestDTO> dto2s = Arrays.asList(new TestDTO("test1", new Date()), new TestDTO("test2", new Date()));
		System.out.println(dto2s.toArray().getClass());
		ObjectMapper om = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
		try {
			String serialized = om.writeValueAsString(dto2s);
			Object[] dto2s2 = om.readValue(serialized, dto2s.toArray().getClass());
			System.out.println(dto2s2 instanceof TestDTO[]);
			for (TestDTO dt : (TestDTO[])dto2s2){
				System.out.println("-"+dt.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}

package io.kitonus.cache.distributed.test;

import java.util.UUID;

import org.junit.Test;
import org.springframework.util.SerializationUtils;

import io.kitonus.cache.distributed.Command;
import io.kitonus.cache.distributed.CommandType;
import io.kitonus.cache.distributed.JacksonSerializer;

public class SerializationComparisonTest {

	@Test
	public void test(){
		Command cmd = new Command(
				"cache1", UUID.randomUUID(), CommandType.PUT, "abcde", "{com.indivaragroup.test.HelloWorld\",\"{\"test\":\"Hello World!\"}");
		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000; ++i){
			byte[] cmdSerd = SerializationUtils.serialize(cmd);
			//System.out.println("Serialized = "+cmdSerd);
			//Object deser = SerializationUtils.deserialize(cmdSerd);
		}
		long elapsed = System.currentTimeMillis()-start;
		System.out.println("1) Normal serialization/deserialization elapsed time = "+(double)elapsed/1000d);

		JacksonSerializer ser = new JacksonSerializer();
		start = System.currentTimeMillis();
		for (int i = 0; i < 10000; ++i){		
			String cmdSerd = ser.serialize(cmd, System.currentTimeMillis(), 20000l);
			//System.out.println("Serialized = "+cmdSerd);
			//Object deser = ser.deserialize(cmdSerd);
		}
		elapsed = System.currentTimeMillis()-start;
		System.out.println("2) json serialization/deserialization elapsed time = "+(double)elapsed/1000d);
		 
		start = System.currentTimeMillis();
		for (int i = 0; i < 10000; ++i){
			byte[] cmdSerd = SerializationUtils.serialize(cmd);
			//System.out.println("Serialized = "+cmdSerd);
			Object deser = SerializationUtils.deserialize(cmdSerd);
		}
		elapsed = System.currentTimeMillis()-start;
		System.out.println("3) Normal serialization/deserialization elapsed time = "+(double)elapsed/1000d);

		start = System.currentTimeMillis();
		for (int i = 0; i < 10000; ++i){		
			String cmdSerd = ser.serialize(cmd, System.currentTimeMillis(), 20000l);
			//System.out.println("Serialized = "+cmdSerd);
			Object deser = ser.deserialize(cmdSerd).getValue();
		}
		elapsed = System.currentTimeMillis()-start;
		System.out.println("4) Json serialization/deserialization elapsed time = "+(double)elapsed/1000d);
	}
}

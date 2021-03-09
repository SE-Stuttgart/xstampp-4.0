package de.xstampp.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializationUtil {

	ObjectMapper mapper;

	public SerializationUtil() {
		mapper = new ObjectMapper();
	}
	
	public String serialize (Object object) throws JsonProcessingException {	
		return mapper.writeValueAsString(object);
	}
	
//	private boolean canSerialize(Class<?> clazz) {
//		return mapper.canSerialize(clazz);
//	}
}

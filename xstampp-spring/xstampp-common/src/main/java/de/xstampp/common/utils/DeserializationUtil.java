package de.xstampp.common.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeserializationUtil {

	ObjectMapper mapper;
	
	public DeserializationUtil() {
		mapper = new ObjectMapper();
	}
	
	public <T> T deserialize (String json, Class<T> targetClass) throws JsonParseException, JsonMappingException, IOException {
		
		return mapper.readValue(json, targetClass);
	}
	
	public JsonNode deserialize (String json) throws IOException {
		
		return mapper.readTree(json);
	}
}

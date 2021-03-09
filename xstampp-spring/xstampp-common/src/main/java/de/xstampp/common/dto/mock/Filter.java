package de.xstampp.common.dto.mock;

import java.util.List;

public class Filter {
	public enum Type {AND, OR, FIELD_EQUALS, FIELD_LIKE, ANY_EQUALS, ANY_LIKE}
	
	private Type type;
	private String fieldName;
	private String fieldValue;
	private List<Filter> body;
		
	public Filter(Type type, String fieldName, String fieldValue) {
		super();
		this.type = type;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}
	
	public Filter(Type type, List<Filter> body) {
		super();
		this.type = type;
		this.body = body;
	}

	public Filter () {
		/* public default constructor required for data mapping */
	}

	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	public List<Filter> getBody() {
		return body;
	}
	public void setBody(List<Filter> body) {
		this.body = body;
	}
	
	

}

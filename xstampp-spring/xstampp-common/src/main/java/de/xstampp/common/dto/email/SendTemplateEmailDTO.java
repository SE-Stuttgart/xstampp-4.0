package de.xstampp.common.dto.email;

import java.util.Map;

public class SendTemplateEmailDTO {
	private String toAddress;
	private String toDisplayName;
	private String subject;
	private String body;
	private Map<String, String> fields;
	
	public SendTemplateEmailDTO() {
		super();
	}

	public SendTemplateEmailDTO(String toAddress, String toDisplayName, String subject, String body, Map<String, String> fields) {
		super();
		this.toAddress = toAddress;
		this.toDisplayName = toDisplayName;
		this.subject = subject;
		this.body = body;
		this.fields = fields;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getToDisplayName() {
		return toDisplayName;
	}

	public void setToDisplayName(String toDisplayName) {
		this.toDisplayName = toDisplayName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}
}

package de.xstampp.common.dto.email;

public class SendRawEmailDTO {
	private String toAddress;
	private String toDisplayName;
	private String subject;
	private String body;

	public SendRawEmailDTO() {
	}

	public SendRawEmailDTO(String toAddress, String toDisplayName, String subject, String body) {
		this.toAddress = toAddress;
		this.toDisplayName = toDisplayName;
		this.subject = subject;
		this.body = body;
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

}

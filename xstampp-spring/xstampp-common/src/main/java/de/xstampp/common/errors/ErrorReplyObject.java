package de.xstampp.common.errors;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ErrorReplyObject {
	@JsonUnwrapped
	private final ErrorCondition errorCondition;

	private final String detailsOptional;

	private final String timestamp;

	public ErrorReplyObject(ErrorCondition errorCondition, String detailsOptional, String formattedTimestamp) {
		this.errorCondition = errorCondition;
		this.detailsOptional = detailsOptional;
		this.timestamp = formattedTimestamp;
	}

	public ErrorCondition getErrorCondition() {
		return errorCondition;
	}

	public String getDetailsOptional() {
		return detailsOptional;
	}

	public String getTimestamp() {
		return timestamp;
	}
}

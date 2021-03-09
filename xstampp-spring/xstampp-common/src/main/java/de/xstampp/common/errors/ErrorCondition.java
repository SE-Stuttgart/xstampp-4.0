package de.xstampp.common.errors;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorCondition implements Serializable {
	private static final long serialVersionUID = 7725419417904368840L;

	/*
	 * using @JsonProperty to set names that are similar to the Spring default error
	 * jsons
	 */

	@JsonProperty("error")
	private final String errorCode;

	@JsonProperty("status")
	private final int httpStatusCode;

	@JsonProperty("message")
	private final String userVisibleMessage;

	private boolean retriable = false;

	@JsonIgnore
	private boolean showNextMessage = false;

	public ErrorCondition(String errorCode, int httpStatusCode, String userVisibleMessage) {
		this.errorCode = errorCode;
		this.httpStatusCode = httpStatusCode;
		this.userVisibleMessage = userVisibleMessage;
	}

	private ErrorCondition(ErrorCondition other) {
		this.errorCode = other.errorCode;
		this.httpStatusCode = other.httpStatusCode;
		this.userVisibleMessage = other.userVisibleMessage;
		this.showNextMessage = other.showNextMessage;
		this.retriable = other.retriable;
	}

	/* getters */
	public String getErrorCode() {
		return errorCode;
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public String getUserVisibleMessage() {
		return userVisibleMessage;
	}

	public boolean getRetriable() {
		return retriable;
	}

	@JsonIgnore
	public boolean isShowNextMesage() {
		return showNextMessage;
	}

	/*
	 * "with" methods that return new ErrorConditions based on this one but with
	 * different settings
	 */
	public ErrorCondition withShowNextMessage() {
		ErrorCondition cond = copy();
		cond.showNextMessage = true;
		return cond;
	}

	public ErrorCondition withRetriable() {
		ErrorCondition cond = copy();
		cond.retriable = true;
		return cond;
	}

	/* exception building */
	public GenericXSTAMPPException exc(Throwable cause, String internalMessage) {
		return new GenericXSTAMPPException(this, cause, internalMessage);
	}

	public GenericXSTAMPPException exc(Throwable cause) {
		return new GenericXSTAMPPException(this, cause);
	}

	public GenericXSTAMPPException exc(String internalMessage) {
		return new GenericXSTAMPPException(this, internalMessage);
	}

	public GenericXSTAMPPException exc() {
		return new GenericXSTAMPPException(this);
	}

	/* cloning */
	private ErrorCondition copy() {
		return new ErrorCondition(this);
	}
}

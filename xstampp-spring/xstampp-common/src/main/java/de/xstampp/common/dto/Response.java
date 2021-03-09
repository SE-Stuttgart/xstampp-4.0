package de.xstampp.common.dto;

public class Response {
	public enum Status {
		SUCCESS, ERROR
	}
	
	private Status status;
	
	public Response() {
		/* public default constructor required for data mapping */
	}
	
	public Response(Status status) {
		this.status = status;
	}
	
	public Response(boolean isSuccess) {
		if (isSuccess) {
			this.status = Status.SUCCESS;
		} else {
			this.status = Status.ERROR;
		}
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}

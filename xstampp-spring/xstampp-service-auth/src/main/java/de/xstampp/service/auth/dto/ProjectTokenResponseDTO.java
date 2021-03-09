package de.xstampp.service.auth.dto;

import java.util.Objects;

import de.xstampp.common.dto.Response;

public class ProjectTokenResponseDTO extends Response{
	private final String token;

	public ProjectTokenResponseDTO(String token) {
		super(Status.SUCCESS);
		this.token = Objects.requireNonNull(token);
	}

	public String getToken() {
		return token;
	}
}

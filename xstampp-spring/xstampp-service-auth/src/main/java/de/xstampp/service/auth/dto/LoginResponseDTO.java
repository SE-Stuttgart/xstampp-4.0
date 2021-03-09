package de.xstampp.service.auth.dto;

import java.util.Objects;

import de.xstampp.common.dto.Response;

public class LoginResponseDTO extends Response {
	private final String token;

	public LoginResponseDTO(String token) {
		super(Status.SUCCESS);
		this.token = Objects.requireNonNull(token);
	}

	public String getToken() {
		return token;
	}
}

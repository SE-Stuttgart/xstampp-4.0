package de.xstampp.service.auth.dto;

public class RegisterRequestDTO {
	String email;
	String displayName;
	String password;
	String passwordRepeat;
	
	public RegisterRequestDTO(String email, String displayName, String password, String passwordRepeat) {
		this.email = email;
		this.displayName = displayName;
		this.password = password;
		this.passwordRepeat = passwordRepeat;
	}

	public RegisterRequestDTO() {
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPasswordRepeat() {
		return passwordRepeat;
	}
	public void setPasswordRepeat(String passwordRepeat) {
		this.passwordRepeat = passwordRepeat;
	}
	
	@Override
	public String toString() {
		return "RegisterRequestDTO [email=" + email + ", displayName=" + displayName + ", password=" + password
				+ ", passwordRepeat=" + passwordRepeat + "]";
	}
	
	
}

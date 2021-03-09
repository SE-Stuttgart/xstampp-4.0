package de.xstampp.service.auth.dto;

public class AdminUserdataRequestDTO {

    private String displayName;
    private String email;

    public AdminUserdataRequestDTO(String displayName, String email) {

        this.displayName = displayName;
        this.email = email;
    }

    public AdminUserdataRequestDTO() {

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
}
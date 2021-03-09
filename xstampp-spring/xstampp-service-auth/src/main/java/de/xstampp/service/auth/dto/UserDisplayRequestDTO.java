package de.xstampp.service.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDisplayRequestDTO {

    public UserDisplayRequestDTO() {
    }

    public UserDisplayRequestDTO(List<UUID> userIds) {
        this.userIds = userIds;
    }

    public List<UUID> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<UUID> userIds) {
        this.userIds = userIds;
    }

    private List<UUID> userIds;
}

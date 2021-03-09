package de.xstampp.service.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportDTO {

    public ImportDTO() {
    }

    private ProjectRequestDTO projectRequest;
    private String groupId;
    private String type;
    private String file;

    public ProjectRequestDTO getProjectRequest() {
        return projectRequest;
    }

    public void setProjectRequest(ProjectRequestDTO projectRequest) {
        this.projectRequest = projectRequest;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}

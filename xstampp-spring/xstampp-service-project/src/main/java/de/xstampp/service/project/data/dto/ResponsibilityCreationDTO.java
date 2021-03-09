package de.xstampp.service.project.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsibilityCreationDTO extends ResponsibilityRequestDTO {

    Integer[] systemConstraintIds;

    public ResponsibilityCreationDTO() {
        // empty constructor for jackson
    }

    public Integer[] getSystemConstraintIds() {
        return systemConstraintIds;
    }

    public void setSystemConstraintIds(Integer[] systemConstraintIds) {
        this.systemConstraintIds = systemConstraintIds;
    }
}

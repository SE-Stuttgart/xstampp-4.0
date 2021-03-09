package de.xstampp.service.project.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This object represents a Responsibility filtering configuration. Each attribute represents a filtering criterion.
 * Only Responsibilities that fulfill all criteria pass the filter.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsibilityFilterRequestDTO {

    /**
     * A Responsibility must link to the specified System Constraint to pass the filter. This criterion gets ignored if
     * this value is null.
     *
     * @see ResponsibilityFilterRequestDTO
     */
    Integer systemConstraintId;
    /**
     * A Responsibility must link to the specified Controller to pass the filter. This criterion gets ignored if this
     * value is null.
     *
     * @see ResponsibilityFilterRequestDTO
     */
    Integer controllerId;

    public Integer getSystemConstraintId() {
        return systemConstraintId;
    }

    public void setSystemConstraintId(Integer systemConstraintId) {
        this.systemConstraintId = systemConstraintId;
    }

    public Integer getControllerId() {
        return controllerId;
    }

    public void setControllerId(Integer controllerId) {
        this.controllerId = controllerId;
    }
}

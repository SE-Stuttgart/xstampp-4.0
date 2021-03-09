package de.xstampp.service.project.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "unsafe_control_action_hazard_link")
@Entity
public class UnsafeControlActionHazardLink implements Serializable {

    private static final long serialVersionUID = -5175186919193332004L;

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @Id
    @Column(name = "unsafe_control_action_id")
    private int unsafeControlActionId;

    @Id
    @Column(name = "hazard_id")
    private int hazardId;

    @Id
    @Column(name = "control_action_id")
    private int controlActionId;

    public UnsafeControlActionHazardLink() {
        /* default constructor for Hibernate */
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public int getUnsafeControlActionId() {
        return unsafeControlActionId;
    }

    public void setUnsafeControlActionId(int unsafeControlActionId) {
        this.unsafeControlActionId = unsafeControlActionId;
    }

    public int getHazardId() {
        return hazardId;
    }

    public void setHazardId(int hazardId) {
        this.hazardId = hazardId;
    }

    public int getControlActionId() {
        return controlActionId;
    }

    public void setControlActionId(int controlActionId) {
        this.controlActionId = controlActionId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + controlActionId;
        result = prime * result + hazardId;
        result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
        result = prime * result + unsafeControlActionId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UnsafeControlActionHazardLink other = (UnsafeControlActionHazardLink) obj;
        if (controlActionId != other.controlActionId)
            return false;
        if (hazardId != other.hazardId)
            return false;
        if (projectId == null) {
            if (other.projectId != null)
                return false;
        } else if (!projectId.equals(other.projectId))
            return false;
        if (unsafeControlActionId != other.unsafeControlActionId)
            return false;
        return true;
    }

    public static final class EntityAttributes {
        public static final String PROJECT_ID = "projectId";
        public static final String CONTROL_ACTION_ID = "controlActionId";
        public static final String UNSAFE_CONTROL_ACTION_ID = "unsafeControlActionId";
        public static final String HAZARD_ID = "hazardId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(PROJECT_ID);
            result.add(CONTROL_ACTION_ID);
            result.add(UNSAFE_CONTROL_ACTION_ID);
            result.add(HAZARD_ID);
            return result;
        }
    }

    public static final class UnsafeControlActionHazardLinkBuilder {
        private UUID projectId;
        private int unsafeControlActionId;
        private int hazardId;
        private int controlActionId;

        private UnsafeControlActionHazardLinkBuilder() {
        }

        public static UnsafeControlActionHazardLinkBuilder anUnsafeControlActionHazardLink() {
            return new UnsafeControlActionHazardLinkBuilder();
        }

        public UnsafeControlActionHazardLinkBuilder withProjectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public UnsafeControlActionHazardLinkBuilder withUnsafeControlActionId(int unsafeControlActionId) {
            this.unsafeControlActionId = unsafeControlActionId;
            return this;
        }

        public UnsafeControlActionHazardLinkBuilder withHazardId(int hazardId) {
            this.hazardId = hazardId;
            return this;
        }

        public UnsafeControlActionHazardLinkBuilder withControlActionId(int controlActionId) {
            this.controlActionId = controlActionId;
            return this;
        }

        public UnsafeControlActionHazardLinkBuilder from(
                UnsafeControlActionHazardLink unsafeControlActionHazardLink) {
            this.projectId = unsafeControlActionHazardLink.getProjectId();
            this.unsafeControlActionId = unsafeControlActionHazardLink.getUnsafeControlActionId();
            this.hazardId = unsafeControlActionHazardLink.getHazardId();
            this.controlActionId = unsafeControlActionHazardLink.getControlActionId();
            return this;
        }

        public UnsafeControlActionHazardLink build() {
            UnsafeControlActionHazardLink unsafeControlActionHazardLink = new UnsafeControlActionHazardLink();
            unsafeControlActionHazardLink.setProjectId(projectId);
            unsafeControlActionHazardLink.setUnsafeControlActionId(unsafeControlActionId);
            unsafeControlActionHazardLink.setHazardId(hazardId);
            unsafeControlActionHazardLink.setControlActionId(controlActionId);
            return unsafeControlActionHazardLink;
        }
    }
}

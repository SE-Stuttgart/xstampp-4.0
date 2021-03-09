package de.xstampp.service.project.data.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "unsafe_control_action_sub_hazard_link")
@Entity
public class UnsafeControlActionSubHazardLink implements Serializable {

    private static final long serialVersionUID = -6347045517169004829L;

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @Id
    @Column(name = "control_action_id")
    private int controlActionId;

    @Id
    @Column(name = "unsafe_control_action_id")
    private int unsafeControlActionId;

    @Id
    @Column(name = "hazard_id")
    private int hazardId;

    @Id
    @Column(name = "sub_hazard_id")
    private int subHazardId;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public int getControlActionId() {
        return controlActionId;
    }

    public void setControlActionId(int controlActionId) {
        this.controlActionId = controlActionId;
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

    public int getSubHazardId() {
        return subHazardId;
    }

    public void setSubHazardId(int subHazardId) {
        this.subHazardId = subHazardId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + controlActionId;
        result = prime * result + hazardId;
        result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
        result = prime * result + subHazardId;
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
        UnsafeControlActionSubHazardLink other = (UnsafeControlActionSubHazardLink) obj;
        if (controlActionId != other.controlActionId)
            return false;
        if (hazardId != other.hazardId)
            return false;
        if (projectId == null) {
            if (other.projectId != null)
                return false;
        } else if (!projectId.equals(other.projectId))
            return false;
        if (subHazardId != other.subHazardId)
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
        public static final String SUB_HAZARD_ID = "subHazardId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(PROJECT_ID);
            result.add(CONTROL_ACTION_ID);
            result.add(UNSAFE_CONTROL_ACTION_ID);
            result.add(HAZARD_ID);
            result.add(SUB_HAZARD_ID);
            return result;
        }
    }

    public static final class UnsafeControlActionSubHazardLinkBuilder {
        private UUID projectId;
        private int controlActionId;
        private int unsafeControlActionId;
        private int hazardId;
        private int subHazardId;

        private UnsafeControlActionSubHazardLinkBuilder() {
        }

        public static UnsafeControlActionSubHazardLinkBuilder anUnsafeControlActionSubHazardLink() {
            return new UnsafeControlActionSubHazardLinkBuilder();
        }

        public UnsafeControlActionSubHazardLinkBuilder withProjectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public UnsafeControlActionSubHazardLinkBuilder withControlActionId(int controlActionId) {
            this.controlActionId = controlActionId;
            return this;
        }

        public UnsafeControlActionSubHazardLinkBuilder withUnsafeControlActionId(int unsafeControlActionId) {
            this.unsafeControlActionId = unsafeControlActionId;
            return this;
        }

        public UnsafeControlActionSubHazardLinkBuilder withHazardId(int hazardId) {
            this.hazardId = hazardId;
            return this;
        }

        public UnsafeControlActionSubHazardLinkBuilder withSubHazardId(int subHazardId) {
            this.subHazardId = subHazardId;
            return this;
        }

        public UnsafeControlActionSubHazardLinkBuilder from(
                UnsafeControlActionSubHazardLink unsafeControlActionSubHazardLink) {
            this.projectId = unsafeControlActionSubHazardLink.getProjectId();
            this.controlActionId = unsafeControlActionSubHazardLink.getControlActionId();
            this.unsafeControlActionId = unsafeControlActionSubHazardLink.getUnsafeControlActionId();
            this.hazardId = unsafeControlActionSubHazardLink.getHazardId();
            this.subHazardId = unsafeControlActionSubHazardLink.getSubHazardId();
            return this;
        }

        public UnsafeControlActionSubHazardLink build() {
            UnsafeControlActionSubHazardLink unsafeControlActionSubHazardLink = new UnsafeControlActionSubHazardLink();
            unsafeControlActionSubHazardLink.setProjectId(projectId);
            unsafeControlActionSubHazardLink.setControlActionId(controlActionId);
            unsafeControlActionSubHazardLink.setUnsafeControlActionId(unsafeControlActionId);
            unsafeControlActionSubHazardLink.setHazardId(hazardId);
            unsafeControlActionSubHazardLink.setSubHazardId(subHazardId);
            return unsafeControlActionSubHazardLink;
        }
    }
}

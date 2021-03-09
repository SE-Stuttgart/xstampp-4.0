package de.xstampp.service.project.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class is used to Link System constraints with Responsibilities.  Here the mapping for those links to table columns is established.
 */
@Table(name = "system_constraint_responsibility_link")
@Entity
public class ResponsibilitySystemConstraintLink implements Serializable {
    private static final long serialVersionUID = 211943772774007328L;

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @Id
    @Column(name = "system_constraint_id")
    private int systemConstraintId;

    @Id
    @Column(name = "responsibility_id")
    private int responsibilityId;


    public ResponsibilitySystemConstraintLink() {
        // empty constructor for mapping
    }

    public ResponsibilitySystemConstraintLink(UUID projectId, int systemConstraintId, int responsibilityId) {
        super();
        this.projectId = projectId;
        this.systemConstraintId = systemConstraintId;
        this.responsibilityId = responsibilityId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public int getSystemConstraintId() {
        return systemConstraintId;
    }

    public void setSystemConstraintId(int systemConstraintId) {
        this.systemConstraintId = systemConstraintId;
    }

    public int getResponsibilityId() {
        return responsibilityId;
    }

    public void setResponsibilityId(int responsibilityId) {
        this.responsibilityId = responsibilityId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + responsibilityId;
        result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
        result = prime * result + systemConstraintId;
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
        ResponsibilitySystemConstraintLink other = (ResponsibilitySystemConstraintLink) obj;
        if (responsibilityId != other.responsibilityId)
            return false;
        if (projectId == null) {
            if (other.projectId != null)
                return false;
        } else if (!projectId.equals(other.projectId))
            return false;
        if (systemConstraintId != other.systemConstraintId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ResponsibilitySystemConstraintLink [projectId=" + projectId + ", systemConstraintId=" + systemConstraintId
                + ", responsibilityId=" + responsibilityId + "]";
    }

    /**
     * Used for Hibernate search queries.
     */
    public static final class EntityAttributes {
        public static final String PROJECT_ID = "projectId";

        public static final String RESPONSIBILITY_ID = "responsibilityId";
        public static final String SYSTEM_CONSTRAINT_ID = "systemConstraintId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(PROJECT_ID);
            result.add(RESPONSIBILITY_ID);
            result.add(SYSTEM_CONSTRAINT_ID);
            return result;
        }
    }

    public static final class ResponsibilitySystemConstraintLinkBuilder {
        UUID projectId;
        int systemConstraintId;
        int responsibilityId;

        private ResponsibilitySystemConstraintLinkBuilder() {
        }

        public static ResponsibilitySystemConstraintLinkBuilder aResponsibilitySystemConstraintLink() {
            return new ResponsibilitySystemConstraintLinkBuilder();
        }

        public ResponsibilitySystemConstraintLinkBuilder withProjectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public ResponsibilitySystemConstraintLinkBuilder withSystemConstraintId(int systemConstraintId) {
            this.systemConstraintId = systemConstraintId;
            return this;
        }

        public ResponsibilitySystemConstraintLinkBuilder withResponsibilityId(int responsibilityId) {
            this.responsibilityId = responsibilityId;
            return this;
        }

        public ResponsibilitySystemConstraintLinkBuilder from(
                ResponsibilitySystemConstraintLink responsibilitySystemConstraintLink) {
            this.projectId = responsibilitySystemConstraintLink.getProjectId();
            this.systemConstraintId = responsibilitySystemConstraintLink.getSystemConstraintId();
            this.responsibilityId = responsibilitySystemConstraintLink.getResponsibilityId();
            return this;
        }

        public ResponsibilitySystemConstraintLink build() {
            ResponsibilitySystemConstraintLink responsibilitySystemConstraintLink = new ResponsibilitySystemConstraintLink();
            responsibilitySystemConstraintLink.setProjectId(projectId);
            responsibilitySystemConstraintLink.setSystemConstraintId(systemConstraintId);
            responsibilitySystemConstraintLink.setResponsibilityId(responsibilityId);
            return responsibilitySystemConstraintLink;
        }
    }
}

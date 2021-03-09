package de.xstampp.service.project.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "system_constraint_hazard_link")
@Entity
public class HazardSystemConstraintLink implements Serializable {

    private static final long serialVersionUID = 211943772774007399L;

    /**
     * Reference:
     * <a href="https://vladmihalcea.com/hibernate-and-uuid-identifiers" >V.
     * Mihalcea: Hibernate and UUID identifiers, 2014.</a>
     */
    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @Id
    @Column(name = "system_constraint_id")
	private int systemConstraintId;

    @Id
    @Column(name = "hazard_id")
	private int hazardId;

    public HazardSystemConstraintLink() {
    }

    public HazardSystemConstraintLink(UUID projectId, int systemConstraintId, int hazardId) {
        super();
        this.projectId = projectId;
        this.systemConstraintId = systemConstraintId;
        this.hazardId = hazardId;
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

    public int getHazardId() {
        return hazardId;
    }

    public void setHazardId(int hazardId) {
        this.hazardId = hazardId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + hazardId;
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
        HazardSystemConstraintLink other = (HazardSystemConstraintLink) obj;
        if (hazardId != other.hazardId)
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
        return "HazardSystemConstraintLink [projectId=" + projectId + ", systemConstraintId=" + systemConstraintId
                + ", responsibilityId=" + hazardId + "]";
    }

    public static final class EntityAttributes {
        public static final String PROJECT_ID = "projectId";
        public static final String HAZARD_ID = "hazardId";
        public static final String SYSTEM_CONSTRAINT_ID = "systemConstraintId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(PROJECT_ID);
            result.add(HAZARD_ID);
            result.add(SYSTEM_CONSTRAINT_ID);
            return result;
        }
    }

    public static final class HazardSystemConstraintLinkBuilder {
        UUID projectId;
        int systemConstraintId;
        int hazardId;

        private HazardSystemConstraintLinkBuilder() {
        }

        public static HazardSystemConstraintLinkBuilder aHazardSystemConstraintLink() {
            return new HazardSystemConstraintLinkBuilder();
        }

        public HazardSystemConstraintLinkBuilder withProjectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public HazardSystemConstraintLinkBuilder withSystemConstraintId(int systemConstraintId) {
            this.systemConstraintId = systemConstraintId;
            return this;
        }

        public HazardSystemConstraintLinkBuilder withHazardId(int hazardId) {
            this.hazardId = hazardId;
            return this;
        }

        public HazardSystemConstraintLinkBuilder from(
                HazardSystemConstraintLink hazardSystemConstraintLink) {
            this.projectId = hazardSystemConstraintLink.getProjectId();
            this.systemConstraintId = hazardSystemConstraintLink.getSystemConstraintId();
            this.hazardId = hazardSystemConstraintLink.getHazardId();
            return this;
        }

        public HazardSystemConstraintLink build() {
            HazardSystemConstraintLink hazardSystemConstraintLink = new HazardSystemConstraintLink();
            hazardSystemConstraintLink.setProjectId(projectId);
            hazardSystemConstraintLink.setSystemConstraintId(systemConstraintId);
            hazardSystemConstraintLink.setHazardId(hazardId);
            return hazardSystemConstraintLink;
        }
    }
}

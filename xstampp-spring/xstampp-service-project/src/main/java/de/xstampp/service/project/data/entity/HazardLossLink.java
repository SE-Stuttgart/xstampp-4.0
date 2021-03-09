package de.xstampp.service.project.data.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "hazard_loss_link")
@Entity
public class HazardLossLink implements Serializable {

    private static final long serialVersionUID = 4734030403041174366L;

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @Id
    @Column(name = "loss_id")
    private int lossId;

    @Id
    @Column(name = "hazard_id")
    private int hazardId;

    public HazardLossLink() {
    }

    public HazardLossLink(UUID projectId, int lossId, int hazardId) {
        this.projectId = projectId;
        this.lossId = lossId;
        this.hazardId = hazardId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public int getLossId() {
        return lossId;
    }

    public void setLossId(int lossId) {
        this.lossId = lossId;
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
        result = prime * result + lossId;
        result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
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
        HazardLossLink other = (HazardLossLink) obj;
        if (hazardId != other.hazardId)
            return false;
        if (lossId != other.lossId)
            return false;
        if (projectId == null) {
            if (other.projectId != null)
                return false;
        } else if (!projectId.equals(other.projectId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "HazardLossLink [projectId=" + projectId + ", lossId=" + lossId + ", responsibilityId=" + hazardId + "]";
    }

    public static final class EntityAttributes {
        public static final String PROJECT_ID = "projectId";
        public static final String LOSS_ID = "lossId";
        public static final String HAZARD_ID = "hazardId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(PROJECT_ID);
            result.add(LOSS_ID);
            result.add(HAZARD_ID);
            return result;
        }
    }

    public static final class HazardLossLinkBuilder {
        private UUID projectId;
        private int lossId;
        private int hazardId;

        private HazardLossLinkBuilder() {
        }

        public static HazardLossLinkBuilder aHazardLossLink() {
            return new HazardLossLinkBuilder();
        }

        public HazardLossLinkBuilder withProjectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public HazardLossLinkBuilder withLossId(int lossId) {
            this.lossId = lossId;
            return this;
        }

        public HazardLossLinkBuilder withHazardId(int hazardId) {
            this.hazardId = hazardId;
            return this;
        }

        public HazardLossLinkBuilder from(HazardLossLink hazardLossLink) {
            this.projectId = hazardLossLink.getProjectId();
            this.lossId = hazardLossLink.getLossId();
            this.hazardId = hazardLossLink.getHazardId();
            return this;
        }

        public HazardLossLink build() {
            HazardLossLink hazardLossLink = new HazardLossLink();
            hazardLossLink.setProjectId(projectId);
            hazardLossLink.setLossId(lossId);
            hazardLossLink.setHazardId(hazardId);
            return hazardLossLink;
        }
    }
}

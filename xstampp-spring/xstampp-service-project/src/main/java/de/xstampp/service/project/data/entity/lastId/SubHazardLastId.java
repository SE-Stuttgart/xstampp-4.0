package de.xstampp.service.project.data.entity.lastId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "sub_hazard_last_id")
public class SubHazardLastId implements Serializable {

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @Id
    @Column(name = "hazard_id")
    private int hazardId;

    @Column(name = "last_id")
    private int lastId;

    public SubHazardLastId() {
    }

    public SubHazardLastId(UUID projectId, int hazardId, int lastId) {
        this.projectId = projectId;
        this.hazardId = hazardId;
        this.lastId = lastId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public int getHazardId() {
        return hazardId;
    }

    public void setHazardId(int hazardId) {
        this.hazardId = hazardId;
    }

    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, hazardId, lastId);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (!(obj instanceof SubHazardLastId))
            return false;

        SubHazardLastId subHazardLastId = (SubHazardLastId) obj;
        boolean sameUUID    = this.projectId.equals(subHazardLastId.projectId);
        boolean sameEntity  = this.hazardId == subHazardLastId.hazardId;
        boolean sameLastId  = this.lastId == subHazardLastId.lastId;

        return sameUUID && sameEntity && sameLastId;
    }

    @Override
    public String toString() {
        return String.format("{project_id = %s, hazard_id = %s, last_id = %d}", projectId, hazardId, lastId);
    }

    public static final class EntityAttributes {
        public static final String PROJECT_ID = "projectId";
        public static final String HAZARD_ID = "hazard_id";
        public static final String LAST_ID = "lastId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(PROJECT_ID);
            result.add(HAZARD_ID);
            result.add(LAST_ID);
            return result;
        }
    }

    public static final class SubHazardLastIdBuilder {
        private UUID projectId;
        private int hazardId;
        private int lastId;

        private SubHazardLastIdBuilder() {
        }

        public static SubHazardLastIdBuilder aSubHazardLastId() {
            return new SubHazardLastIdBuilder();
        }

        public SubHazardLastIdBuilder withProjectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public SubHazardLastIdBuilder withHazardId(int hazardId) {
            this.hazardId = hazardId;
            return this;
        }

        public SubHazardLastIdBuilder withLastId(int lastId) {
            this.lastId = lastId;
            return this;
        }

        public SubHazardLastIdBuilder from(SubHazardLastId subHazardLastId) {
            this.projectId = subHazardLastId.projectId;
            this.hazardId = subHazardLastId.hazardId;
            this.lastId = subHazardLastId.lastId;
            return this;
        }

        public SubHazardLastId build() {
            SubHazardLastId subHazardLastId = new SubHazardLastId();
            subHazardLastId.setProjectId(projectId);
            subHazardLastId.setHazardId(hazardId);
            subHazardLastId.setLastId(lastId);
            return subHazardLastId;
        }
    }
}

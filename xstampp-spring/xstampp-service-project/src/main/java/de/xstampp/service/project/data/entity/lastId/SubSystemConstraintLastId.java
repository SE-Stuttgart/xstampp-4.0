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
@Table(name = "sub_system_constraint_last_id")
public class SubSystemConstraintLastId implements Serializable {

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @Id
    @Column(name = "system_constraint_id")
    private int systemConstraintId;

    @Column(name = "last_id")
    private int lastId;

    public SubSystemConstraintLastId() {
    }

    public SubSystemConstraintLastId(UUID projectId, int hazardId, int lastId) {
        this.projectId = projectId;
        this.systemConstraintId = hazardId;
        this.lastId = lastId;
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

    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, systemConstraintId, lastId);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (!(obj instanceof SubSystemConstraintLastId))
            return false;

        SubSystemConstraintLastId subSystemConstraintLastId = (SubSystemConstraintLastId) obj;
        boolean sameUUID    = this.projectId.equals(subSystemConstraintLastId.projectId);
        boolean sameEntity  = this.systemConstraintId == subSystemConstraintLastId.systemConstraintId;
        boolean sameLastId  = this.lastId == subSystemConstraintLastId.lastId;

        return sameUUID && sameEntity && sameLastId;
    }

    @Override
    public String toString() {
        return String.format("{project_id = %s, system_constraint_id = %s, last_id = %d}", projectId, systemConstraintId, lastId);
    }

    public static final class EntityAttributes {
        public static final String PROJECT_ID = "projectId";
        public static final String HAZARD_ID = "system_constraint_id";
        public static final String LAST_ID = "lastId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(PROJECT_ID);
            result.add(HAZARD_ID);
            result.add(LAST_ID);
            return result;
        }
    }

    public static final class SubSystemConstraintLastIdBuilder {
        private UUID projectId;
        private int systemConstraintId;
        private int lastId;

        private SubSystemConstraintLastIdBuilder() {
        }

        public static SubSystemConstraintLastIdBuilder aSubSystemConstraintLastId() {
            return new SubSystemConstraintLastIdBuilder();
        }

        public SubSystemConstraintLastIdBuilder withProjectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public SubSystemConstraintLastIdBuilder withSystemConstraintId(int systemConstraintId) {
            this.systemConstraintId = systemConstraintId;
            return this;
        }

        public SubSystemConstraintLastIdBuilder withLastId(int lastId) {
            this.lastId = lastId;
            return this;
        }

        public SubSystemConstraintLastIdBuilder from(SubSystemConstraintLastId subSystemConstraintLastId) {
            this.projectId = subSystemConstraintLastId.projectId;
            this.systemConstraintId = subSystemConstraintLastId.systemConstraintId;
            this.lastId = subSystemConstraintLastId.lastId;
            return this;
        }

        public SubSystemConstraintLastId build() {
            SubSystemConstraintLastId subSystemConstraintLastId = new SubSystemConstraintLastId();
            subSystemConstraintLastId.setProjectId(projectId);
            subSystemConstraintLastId.setSystemConstraintId(systemConstraintId);
            subSystemConstraintLastId.setLastId(lastId);
            return subSystemConstraintLastId;
        }
    }
}

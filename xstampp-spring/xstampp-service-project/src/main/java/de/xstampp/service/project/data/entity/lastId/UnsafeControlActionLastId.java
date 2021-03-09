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
@Table(name = "unsafe_control_action_last_id")
public class UnsafeControlActionLastId implements Serializable {

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @Id
    @Column(name = "control_action_id")
    private int controlActionId;

    @Column(name = "last_id")
    private int lastId;

    public UnsafeControlActionLastId() {
    }

    public UnsafeControlActionLastId(UUID projectId, int hazardId, int lastId) {
        this.projectId = projectId;
        this.controlActionId = hazardId;
        this.lastId = lastId;
    }

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

    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, controlActionId, lastId);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (!(obj instanceof UnsafeControlActionLastId))
            return false;

        UnsafeControlActionLastId unsafeControlActionLastId = (UnsafeControlActionLastId) obj;
        boolean sameUUID    = this.projectId.equals(unsafeControlActionLastId.projectId);
        boolean sameEntity  = this.controlActionId == unsafeControlActionLastId.controlActionId;
        boolean sameLastId  = this.lastId == unsafeControlActionLastId.lastId;

        return sameUUID && sameEntity && sameLastId;
    }

    @Override
    public String toString() {
        return String.format("{project_id = %s, control_action_id = %s, last_id = %d}", projectId, controlActionId, lastId);
    }

    public static final class EntityAttributes {
        public static final String PROJECT_ID = "projectId";
        public static final String HAZARD_ID = "control_action_id";
        public static final String LAST_ID = "lastId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(PROJECT_ID);
            result.add(HAZARD_ID);
            result.add(LAST_ID);
            return result;
        }
    }

    public static final class UnsafeControlActionLastIdBuilder {
        private UUID projectId;
        private int controlActionId;
        private int lastId;

        private UnsafeControlActionLastIdBuilder() {
        }

        public static UnsafeControlActionLastIdBuilder anUnsafeControlActionLastId() {
            return new UnsafeControlActionLastIdBuilder();
        }

        public UnsafeControlActionLastIdBuilder withProjectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public UnsafeControlActionLastIdBuilder withControlActionId(int controlActionId) {
            this.controlActionId = controlActionId;
            return this;
        }

        public UnsafeControlActionLastIdBuilder withLastId(int lastId) {
            this.lastId = lastId;
            return this;
        }

        public UnsafeControlActionLastIdBuilder from (UnsafeControlActionLastId unsafeControlActionLastId) {
            this.projectId = unsafeControlActionLastId.projectId;
            this.controlActionId = unsafeControlActionLastId.controlActionId;
            this.lastId = unsafeControlActionLastId.lastId;
            return this;
        }

        public UnsafeControlActionLastId build() {
            UnsafeControlActionLastId unsafeControlActionLastId = new UnsafeControlActionLastId();
            unsafeControlActionLastId.setProjectId(projectId);
            unsafeControlActionLastId.setControlActionId(controlActionId);
            unsafeControlActionLastId.setLastId(lastId);
            return unsafeControlActionLastId;
        }
    }
}

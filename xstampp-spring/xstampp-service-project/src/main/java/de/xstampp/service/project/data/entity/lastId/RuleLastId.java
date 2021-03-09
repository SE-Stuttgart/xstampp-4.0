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
@Table(name = "rule_last_id")
public class RuleLastId implements Serializable {

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @Id
    @Column(name = "controller_id")
    private int controllerId;

    @Column(name = "last_id")
    private int lastId;

    public RuleLastId() {
    }

    public RuleLastId(UUID projectId, int hazardId, int lastId) {
        this.projectId = projectId;
        this.controllerId = hazardId;
        this.lastId = lastId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public int getControllerId() {
        return controllerId;
    }

    public void setControllerId(int controllerId) {
        this.controllerId = controllerId;
    }

    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, controllerId, lastId);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (!(obj instanceof RuleLastId))
            return false;

        RuleLastId ruleLastId = (RuleLastId) obj;
        boolean sameUUID    = this.projectId.equals(ruleLastId.projectId);
        boolean sameEntity  = this.controllerId == ruleLastId.controllerId;
        boolean sameLastId  = this.lastId == ruleLastId.lastId;

        return sameUUID && sameEntity && sameLastId;
    }

    @Override
    public String toString() {
        return String.format("{project_id = %s, controller_id = %s, last_id = %d}", projectId, controllerId, lastId);
    }

    public static final class EntityAttributes {
        public static final String PROJECT_ID = "projectId";
        public static final String HAZARD_ID = "controller_id";
        public static final String LAST_ID = "lastId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(PROJECT_ID);
            result.add(HAZARD_ID);
            result.add(LAST_ID);
            return result;
        }
    }

    public static final class RuleLastIdBuilder {
        private UUID projectId;
        private int controllerId;
        private int lastId;

        private RuleLastIdBuilder() {
        }

        public static RuleLastIdBuilder aRuleLastId() {
            return new RuleLastIdBuilder();
        }

        public RuleLastIdBuilder withProjectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public RuleLastIdBuilder withControllerId(int controllerId) {
            this.controllerId = controllerId;
            return this;
        }

        public RuleLastIdBuilder withLastId(int lastId) {
            this.lastId = lastId;
            return this;
        }

        public RuleLastIdBuilder from(RuleLastId ruleLastId) {
            this.projectId = ruleLastId.projectId;
            this.controllerId = ruleLastId.controllerId;
            this.lastId = ruleLastId.lastId;
            return this;
        }

        public RuleLastId build() {
            RuleLastId ruleLastId = new RuleLastId();
            ruleLastId.setProjectId(projectId);
            ruleLastId.setControllerId(controllerId);
            ruleLastId.setLastId(lastId);
            return ruleLastId;
        }
    }
}

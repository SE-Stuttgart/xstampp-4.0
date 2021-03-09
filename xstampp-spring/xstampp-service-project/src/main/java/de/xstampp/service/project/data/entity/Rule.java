package de.xstampp.service.project.data.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.data.entity.report.*;
import de.xstampp.service.project.data.entity.report.structureElements.ReportHeader;
import de.xstampp.service.project.data.entity.report.structureElements.ReportTitledReference;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rule")
public class Rule extends XStamppDependentEntity implements Serializable, ReportableEntity {

    private static final long serialVersionUID = 6371755108695351502L;

    @EmbeddedId
    @JsonUnwrapped
    private EntityDependentKey id;
    @Column(name = "rule")
    private String rule;
    @Column(name = "control_action_id")
    private int controlActionId;
    @Column(name = "controller_id")
    private int controllerId;

    @Column(name = "state")
    private String state;

    public Rule() {
    }

    public Rule(EntityDependentKey id) {
        super();
        this.id = id;
        this.controllerId = id.getParentId();
    }

    public EntityDependentKey getId() {
        return id;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public int getControlActionId() {
        return controlActionId;
    }

    public void setControlActionId(int controlActionId) {
        this.controlActionId = controlActionId;
    }

    public int getControllerId() {
        return controllerId;
    }

    public void setControllerId(int controllerId) {
        this.controllerId = controllerId;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        Rule other = (Rule) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Rule [id=" + id + ", rule=" + rule + ", controlActionId=" + controlActionId + ", controllerId" + controllerId + "]";
    }

    @Override
    public ReportSegment createReportSegment(XStamppProject xstamppProject) {

        ReportSegment segment = new ReportSegment(getReportReferenceId());

        segment.add(new ReportHeader("", getReportReferenceId()));
        segment.add(new ReportTitledReference("Rule", rule, false));
        segment.add(new ReportTitledReference("Controller", ReportSegment.CONTROLLER_ID_PREFIX + controllerId, true));
        if (controlActionId != 0) {
            segment.add(new ReportTitledReference("Control Action", ReportSegment.CONTROL_ACTION_ID_PREFIX + controlActionId, true));
        } else  {
            segment.add(new ReportTitledReference("Control Action", "none", false));
        }

        return segment;
    }

    @Override
    public ReportNameIdPair createNameIdPair() {
        return new ReportNameIdPair(rule, getReportReferenceId());
    }

    private String getReportReferenceId() {
        return ReportSegment.CONTROL_ALGORITHM_ID_PREFIX + controllerId + "." + id.getId();
    }

    public static final class EntityAttributes {
        public static final String ID = DEPENDENT_KEY_ATTRIBUTE;
        public static final String RULE = "rule";
        public static final String CONTROL_ACTION_ID = "controlActionId";
        public static final String CONTROLLER_ID = "controllerId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(RULE);
            result.add(CONTROL_ACTION_ID);
            result.add(CONTROLLER_ID);
            return result;
        }
    }

    public static final class RuleBuilder {
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private EntityDependentKey id;
        private Timestamp lockExpirationTime;
        private String rule;
        private int controlActionId;
        private int controllerId;
        private String state;

        private RuleBuilder() {
        }

        public static RuleBuilder aRule() {
            return new RuleBuilder();
        }

        public RuleBuilder withLastEditor(UUID lastEditorId) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public RuleBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public RuleBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public RuleBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public RuleBuilder withId(EntityDependentKey id) {
            this.id = id;
            return this;
        }

        public RuleBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public RuleBuilder withRule(String rule) {
            this.rule = rule;
            return this;
        }

        public RuleBuilder withControlActionId(int controlActionId) {
            this.controlActionId = controlActionId;
            return this;
        }

        public RuleBuilder withControllerId(int controllerId) {
            this.controllerId = controllerId;
            return this;
        }

        public RuleBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public RuleBuilder from(Rule rule) {
            this.lastEditorId = rule.getLastEditorId();
            this.lastEdited = rule.getLastEdited();
            this.lockHolderId = rule.getLockHolderId();
            this.lockHolderDisplayName = rule.getLockHolderDisplayName();
            this.id = rule.getId();
            this.lockExpirationTime = rule.getLockExpirationTime();
            this.rule = rule.getRule();
            this.controlActionId = rule.getControlActionId();
            this.controllerId = rule.getControllerId();
            this.state = rule.state;
            return this;
        }

        public Rule build() {
            Rule resultRule = new Rule(id);
            resultRule.setLastEditorId(lastEditorId);
            resultRule.setLastEdited(lastEdited);
            resultRule.setLockHolderId(lockHolderId);
            resultRule.setLockHolderDisplayName(lockHolderDisplayName);
            resultRule.setLockExpirationTime(lockExpirationTime);
            resultRule.setRule(rule);
            resultRule.setControlActionId(controlActionId);
            resultRule.setControllerId(controllerId);
            resultRule.setState(state);
            return resultRule;
        }
    }
}

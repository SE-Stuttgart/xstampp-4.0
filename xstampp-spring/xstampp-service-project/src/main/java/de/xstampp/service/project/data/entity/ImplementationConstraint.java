package de.xstampp.service.project.data.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.data.entity.report.*;
import de.xstampp.service.project.data.entity.report.structureElements.ReportHeader;
import de.xstampp.service.project.data.entity.report.structureElements.ReportPlainText;
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

/**
 * Implementation constraint entity class. Its objects are managed by hibernate.
 */
@Table(name = "implementation_constraint")
@Entity
public class ImplementationConstraint extends XStamppDependentEntity implements Serializable, ReportableEntity {

    private static final long serialVersionUID = -2805745671684751905L;

    //This entity depends on a LossScenario object lso. Its parentId (part of the EntityDependent Key) is lso.id.
    @EmbeddedId
    @JsonUnwrapped
    private ProjectDependentKey id;
    //loss_scenario_id is sufficient to identify a loss scenario since the loss scenario has the same projectId as this object
    @Column(name = "loss_scenario_id")
    private Integer lossScenarioId;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;

    @Column(name = "state")
    private String state;

    public ProjectDependentKey getId() {
        return id;
    }

    public void setId(ProjectDependentKey id) {
        this.id = id;
    }

    public Integer getLossScenarioId() {
        return lossScenarioId;
    }

    public void setLossScenarioId(Integer lossScenarioId) {
        this.lossScenarioId = lossScenarioId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        ControllerConstraint other = (ControllerConstraint) obj;
        if (id == null) {
            if (other.getId() != null)
                return false;
        } else if (!id.equals(other.getId()))
            return false;
        return true;
    }

    @Override
    public ReportSegment createReportSegment(XStamppProject xstamppProject) {
        ReportSegment segment = new ReportSegment(getReportReferenceId());
        segment.add(new ReportHeader(name, getReportReferenceId()));
        segment.add(new ReportPlainText(description));
        segment.add(new ReportTitledReference("Related Loss Scenario", ReportSegment.LOSS_SCENARIO_ID_PREFIX + lossScenarioId, true));

        return segment;
    }

    @Override
    public ReportNameIdPair createNameIdPair() {
        return new ReportNameIdPair(name, getReportReferenceId());
    }

    private String getReportReferenceId() {
        return ReportSegment.IMPLEMENTATION_CONSTRAINT_ID_PREFIX + id.getId();
    }

    public static final class EntityAttributes {
        public static final String ID = DEPENDENT_KEY_ATTRIBUTE;
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String LOSS_SCENARIO_ID = "lossScenarioId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(ID);
            result.add(NAME);
            result.add(DESCRIPTION);
            result.add(LOSS_SCENARIO_ID);
            return result;
        }
    }

    public static final class ImplementationConstraintBuilder {
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private Timestamp lockExpirationTime;
        //This entity depends on a LossScenario object lso. Its parentId (part of the EntityDependent Key) is lso.id.
        private ProjectDependentKey id;
        //loss_scenario_id is sufficient to identify a loss scenario since the loss scenario has the same projectId as this object
        private Integer lossScenarioId;
        private String name;
        private String description;
        private String state;

        private ImplementationConstraintBuilder() {
        }

        public static ImplementationConstraintBuilder anImplementationConstraint() {
            return new ImplementationConstraintBuilder();
        }

        public ImplementationConstraintBuilder withLastEditor(UUID lastEditorId) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public ImplementationConstraintBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public ImplementationConstraintBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public ImplementationConstraintBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public ImplementationConstraintBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public ImplementationConstraintBuilder withId(ProjectDependentKey id) {
            this.id = id;
            return this;
        }

        public ImplementationConstraintBuilder withLossScenarioId(Integer lossScenarioId) {
            this.lossScenarioId = lossScenarioId;
            return this;
        }

        public ImplementationConstraintBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ImplementationConstraintBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ImplementationConstraintBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public ImplementationConstraintBuilder from(ImplementationConstraint implementationConstraint) {
            this.lastEditorId = implementationConstraint.getLastEditorId();
            this.lastEdited = implementationConstraint.getLastEdited();
            this.lockHolderId = implementationConstraint.getLockHolderId();
            this.lockHolderDisplayName = implementationConstraint.getLockHolderDisplayName();
            this.lockExpirationTime = implementationConstraint.getLockExpirationTime();
            this.id = implementationConstraint.getId();
            this.lossScenarioId = implementationConstraint.getLossScenarioId();
            this.name = implementationConstraint.getName();
            this.description = implementationConstraint.getDescription();
            this.state = implementationConstraint.state;
            return this;
        }

        public ImplementationConstraint build() {
            ImplementationConstraint implementationConstraint = new ImplementationConstraint();
            implementationConstraint.setLastEditorId(lastEditorId);
            implementationConstraint.setLastEdited(lastEdited);
            implementationConstraint.setLockHolderId(lockHolderId);
            implementationConstraint.setLockHolderDisplayName(lockHolderDisplayName);
            implementationConstraint.setLockExpirationTime(lockExpirationTime);
            implementationConstraint.setId(id);
            implementationConstraint.setLossScenarioId(lossScenarioId);
            implementationConstraint.setName(name);
            implementationConstraint.setDescription(description);
            implementationConstraint.setState(state);
            return implementationConstraint;
        }
    }
}

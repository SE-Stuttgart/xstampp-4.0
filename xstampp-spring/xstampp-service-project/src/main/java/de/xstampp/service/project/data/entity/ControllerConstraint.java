package de.xstampp.service.project.data.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;
import de.xstampp.service.project.util.annotation.CheckState;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import de.xstampp.service.project.data.entity.report.*;
import de.xstampp.service.project.data.entity.report.structureElements.ReportHeader;
import de.xstampp.service.project.data.entity.report.structureElements.ReportPlainText;
import de.xstampp.service.project.data.entity.report.structureElements.ReportTitledReference;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "controller_constraint")
@Entity
public class ControllerConstraint extends XStamppDependentEntity implements Serializable, ReportableEntity {

    private static final long serialVersionUID = 3833567903198770941L;

    @EmbeddedId
    @JsonUnwrapped
    private EntityDependentKey id;

    @CheckState
    @Column(name = "name")
	private String name;

    @CheckState
    @Column(name = "description")
	private String description;

    @Column(name = "state")
	private String state;

    public ControllerConstraint() {
        // empty contructor for hibernate mapper
    }

    public EntityDependentKey getId() {
        return id;
    }

    public void setId(EntityDependentKey id) {
        this.id = id;
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
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public ReportSegment createReportSegment(XStamppProject xstamppProject) {
        ReportSegment segment = new ReportSegment(getReportReferenceId());
        segment.add(new ReportHeader(name, getReportReferenceId()));
        segment.add(new ReportPlainText(description));
        segment.add(new ReportTitledReference("Connected to Control Action", ReportSegment.CONTROL_ACTION_ID_PREFIX + id.getParentId(),true));
        //UCA reference is control action ID + unsafe control action ID
        segment.add(new ReportTitledReference("Connected to Unsafe Control Action", ReportSegment.UCA_ID_PREFIX + id.getParentId() + "." + id.getId(),true));

        return segment;
    }

    @Override
    public ReportNameIdPair createNameIdPair() {
        return new ReportNameIdPair(name, getReportReferenceId());
    }

    private String getReportReferenceId() {
        return ReportSegment.CONTROLLER_CONSTRAINTS_ID_PREFIX + id.getParentId() + "." + id.getId();
    }

    public static final class EntityAttributes {
        public static final String ID = DEPENDENT_KEY_ATTRIBUTE;
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String STATE = "state";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(ID);
            result.add(NAME);
            result.add(DESCRIPTION);
            result.add(STATE);
            return result;
        }
    }

    public static final class ControllerConstraintBuilder {
        EntityDependentKey id;
        String name;
        String description;
        String state;
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private Timestamp lockExpirationTime;

        private ControllerConstraintBuilder() {
        }

        public static ControllerConstraintBuilder aControllerConstraint() {
            return new ControllerConstraintBuilder();
        }

        public ControllerConstraintBuilder withLastEditor(UUID lastEditorId) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public ControllerConstraintBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public ControllerConstraintBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public ControllerConstraintBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public ControllerConstraintBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public ControllerConstraintBuilder withId(EntityDependentKey id) {
            this.id = id;
            return this;
        }

        public ControllerConstraintBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ControllerConstraintBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ControllerConstraintBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public ControllerConstraintBuilder from(ControllerConstraint controllerConstraint) {
            this.lastEditorId = controllerConstraint.getLastEditorId();
            this.lastEdited = controllerConstraint.getLastEdited();
            this.lockHolderId = controllerConstraint.getLockHolderId();
            this.lockHolderDisplayName = controllerConstraint.getLockHolderDisplayName();
            this.lockExpirationTime = controllerConstraint.getLockExpirationTime();

            this.id = controllerConstraint.getId();
            this.name = controllerConstraint.getName();
            this.description = controllerConstraint.getDescription();
            this.state = controllerConstraint.state;
            return this;
        }

        public ControllerConstraint build() {
            ControllerConstraint controllerConstraint = new ControllerConstraint();
            controllerConstraint.setLastEditorId(lastEditorId);
            controllerConstraint.setLastEdited(lastEdited);
            controllerConstraint.setLockHolderId(lockHolderId);
            controllerConstraint.setLockHolderDisplayName(lockHolderDisplayName);
            controllerConstraint.setLockExpirationTime(lockExpirationTime);
            controllerConstraint.setId(id);
            controllerConstraint.setName(name);
            controllerConstraint.setDescription(description);
            controllerConstraint.setState(state);
            return controllerConstraint;
        }
    }
}

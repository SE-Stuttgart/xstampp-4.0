package de.xstampp.service.project.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.data.dto.SubSystemConstraintRequestDTO;
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

@Table(name = "sub_system_constraint")
@Entity
public class SubSystemConstraint extends XStamppDependentEntity implements Serializable, ReportableEntity {

    private static final long serialVersionUID = -3164074343268107470L;

    @EmbeddedId
    @JsonUnwrapped
    private EntityDependentKey id;

    @Column(name = "sub_hazard_project_id")
    private UUID subHazardProjectId;

    @CheckState
    @Column(name = "name")
    private String name;

    @CheckState
    @Column(name = "description")
    private String description;

    @Column(name = "sub_hazard_id")
    private Integer subHazardId;

    @Column(name = "hazard_id")
    private Integer hazardId;

    @Column(name = "state")
    private String state;

    public SubSystemConstraint(SubSystemConstraintRequestDTO subConstRequestDTO, int constId, int subConstId) {
        EntityDependentKey key = new EntityDependentKey(UUID.fromString(subConstRequestDTO.getProjectId()), constId,
                subConstId);
        this.id = key;
        this.name = subConstRequestDTO.getName();
        this.description = subConstRequestDTO.getDescription();
    }

    public SubSystemConstraint(EntityDependentKey key) {
        this.id = key;
    }

    public SubSystemConstraint() {
    }

    public EntityDependentKey getId() {
        return id;
    }

    public Integer getHazardId() {
        return hazardId;
    }

    public void setHazardId(Integer hazardId) {
        this.hazardId = hazardId;
    }

    public void setSubHazardId(Integer subHazardId) {
        this.subHazardId = subHazardId;
    }

    public Integer getSubHazardId() {
        return subHazardId;
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

    @JsonIgnore
    public UUID getSubHazardProjectId() {
        return subHazardProjectId;
    }

    public void setSubHazardProjectId(UUID subHazardProjectId) {
        this.subHazardProjectId = subHazardProjectId;
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
        SubSystemConstraint other = (SubSystemConstraint) obj;
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
		segment.add(new ReportTitledReference("Related System Constraint", ReportSegment.SYSTEM_CONSTRAINT_ID_PREFIX + id.getParentId(),true));
		segment.add(new ReportTitledReference("Sub-Hazard", ReportSegment.SUBHAZARD_ID_PREFIX + hazardId + "." + subHazardId,true));

		return segment;
	}

	@Override
	public ReportNameIdPair createNameIdPair() {
		return new ReportNameIdPair(name, getReportReferenceId());
	}

	private String getReportReferenceId() {
		return ReportSegment.SUBSYSTEM_CONSTRAINT_ID_PREFIX + id.getParentId() + "." + id.getId();
	}

    public static final class EntityAttributes {

        public static final String ID = DEPENDENT_KEY_ATTRIBUTE;
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String SUB_HAZARD_ID = "subHazardId";
        public static final String HAZARD_ID = "hazardId";
        public static final String SUB_HAZARD_PROJECT_ID = "subHazardProjectId";
        public static final String STATE = "state";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(NAME);
            result.add(DESCRIPTION);
            result.add(SUB_HAZARD_ID);
            result.add(HAZARD_ID);
            result.add(SUB_HAZARD_PROJECT_ID);
            result.add(STATE);
            return result;
        }
    }

    public static final class SubSystemConstraintBuilder {
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private Timestamp lockExpirationTime;
        private EntityDependentKey id;
        private UUID subHazardProjectId;
        private String name;
        private String description;
        private Integer subHazardId;
        private Integer hazardId;
        private String state;

        private SubSystemConstraintBuilder() {
        }

        public static SubSystemConstraintBuilder aSubSystemConstraint() {
            return new SubSystemConstraintBuilder();
        }

        public SubSystemConstraintBuilder withLastEditor(UUID lastEditorId) {
            this.lastEditorId = lastEditorId;
            return this;
        }

        public SubSystemConstraintBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public SubSystemConstraintBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public SubSystemConstraintBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public SubSystemConstraintBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public SubSystemConstraintBuilder withId(EntityDependentKey id) {
            this.id = id;
            return this;
        }

        public SubSystemConstraintBuilder withSubHazardProjectId(UUID subHazardProjectId) {
            this.subHazardProjectId = subHazardProjectId;
            return this;
        }

        public SubSystemConstraintBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SubSystemConstraintBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public SubSystemConstraintBuilder withSubHazardId(Integer subHazardId) {
            this.subHazardId = subHazardId;
            return this;
        }

        public SubSystemConstraintBuilder withHazardId(Integer hazardId) {
            this.hazardId = hazardId;
            return this;
        }

        public SubSystemConstraintBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public SubSystemConstraintBuilder from(SubSystemConstraint subSystemConstraint) {
            this.lastEditorId = subSystemConstraint.getLastEditorId();
            this.lastEdited = subSystemConstraint.getLastEdited();
            this.lockHolderId = subSystemConstraint.getLockHolderId();
            this.lockHolderDisplayName = subSystemConstraint.getLockHolderDisplayName();
            this.lockExpirationTime = subSystemConstraint.getLockExpirationTime();

            this.subHazardProjectId = subSystemConstraint.getSubHazardProjectId();
            this.name = subSystemConstraint.getName();
            this.description = subSystemConstraint.getDescription();
            this.subHazardId = subSystemConstraint.subHazardId;
            this.hazardId = subSystemConstraint.hazardId;
            this.state = subSystemConstraint.state;
            this.id = subSystemConstraint.getId();
            return this;
        }

        public SubSystemConstraint build() {
            SubSystemConstraint subSystemConstraint = new SubSystemConstraint();
            subSystemConstraint.setLastEditorId(lastEditorId);
            subSystemConstraint.setLastEdited(lastEdited);
            subSystemConstraint.setLockHolderId(lockHolderId);
            subSystemConstraint.setLockHolderDisplayName(lockHolderDisplayName);
            subSystemConstraint.setLockExpirationTime(lockExpirationTime);
            subSystemConstraint.setSubHazardProjectId(subHazardProjectId);
            subSystemConstraint.setName(name);
            subSystemConstraint.setDescription(description);
            subSystemConstraint.setSubHazardId(subHazardId);
            subSystemConstraint.setHazardId(hazardId);
            subSystemConstraint.setState(state);
            subSystemConstraint.id = this.id;
            return subSystemConstraint;
        }
    }
}

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
import de.xstampp.service.project.data.entity.report.structureElements.ReportColumns;
import de.xstampp.service.project.data.entity.report.structureElements.ReportHeader;
import de.xstampp.service.project.data.entity.report.structureElements.ReportPlainText;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "system_constraint")
@Entity
public class SystemConstraint extends XStamppDependentEntity implements Serializable, ReportableEntity {

    private static final long serialVersionUID = -852439825882508238L;

    @EmbeddedId
    @JsonUnwrapped
    private ProjectDependentKey id;

    @CheckState
    @Column(name = "name")
    private String name;

    @CheckState
    @Column(name = "description")
    private String description;

    @Column(name = "state")
    private String state;

    public SystemConstraint(ProjectDependentKey id) {
        this.id = id;
    }

    public SystemConstraint() {

    }

    public ProjectDependentKey getId() {
        return id;
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
        SystemConstraint other = (SystemConstraint) obj;
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

        ReportColumns reportColumns = new ReportColumns();
        List<Integer> hazardReferences = new ArrayList<>();
        List<String> subSystemConstraintReferences = new ArrayList<>();

        List<HazardSystemConstraintLink> hazardSystemConstraintLinks = xstamppProject.getHazardSystemConstraintLinks();
        List<SubSystemConstraint> subSystemConstraints = xstamppProject.getSubSystemConstraints();


        if (hazardSystemConstraintLinks != null) {
            for (HazardSystemConstraintLink hazardLink : hazardSystemConstraintLinks) {
                if (hazardLink.getSystemConstraintId() == id.getId()) {
                    hazardReferences.add(hazardLink.getHazardId());
                }
            }
        }

        if (subSystemConstraints != null) {
            for (SubSystemConstraint subSystemConstraint : subSystemConstraints) {
                if (subSystemConstraint.getId().getParentId() == id.getId()) {
                    subSystemConstraintReferences.add(ReportSegment.SUBSYSTEM_CONSTRAINT_ID_PREFIX + subSystemConstraint.getId().getParentId() + "." + subSystemConstraint.getId().getId());
                }
            }
        }

        reportColumns.addColumn("Hazards", hazardReferences, ReportSegment.HAZARD_ID_PREFIX);
        reportColumns.addColumn("Sub System Constraints", subSystemConstraintReferences);
        segment.add(reportColumns);

        return segment;
    }

    @Override
    public ReportNameIdPair createNameIdPair() {
        return new ReportNameIdPair(name, getReportReferenceId());
    }

    private String getReportReferenceId() {
        return ReportSegment.SYSTEM_CONSTRAINT_ID_PREFIX + id.getId();
    }

    public static final class EntityAttributes {

        public static final String ID = DEPENDENT_KEY_ATTRIBUTE;
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String STATE = "state";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(NAME);
            result.add(DESCRIPTION);
            result.add(STATE);
            return result;
        }
    }

    public static final class SystemConstraintBuilder {
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private Timestamp lockExpirationTime;
        private ProjectDependentKey id;
        private String name;
        private String description;
        private String state;

        private SystemConstraintBuilder() {
        }

        public static SystemConstraintBuilder aSystemConstraint() {
            return new SystemConstraintBuilder();
        }

        public SystemConstraintBuilder withLastEditor(UUID lastEditorId) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public SystemConstraintBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public SystemConstraintBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public SystemConstraintBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public SystemConstraintBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public SystemConstraintBuilder withId(ProjectDependentKey id) {
            this.id = id;
            return this;
        }

        public SystemConstraintBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SystemConstraintBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public SystemConstraintBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public SystemConstraintBuilder from(SystemConstraint systemConstraint) {
            this.lastEditorId = systemConstraint.getLastEditorId();
            this.lastEdited = systemConstraint.getLastEdited();
            this.lockHolderId = systemConstraint.getLockHolderId();
            this.lockHolderDisplayName = systemConstraint.getLockHolderDisplayName();
            this.lockExpirationTime = systemConstraint.getLockExpirationTime();

            this.id = systemConstraint.getId();
            this.name = systemConstraint.getName();
            this.description = systemConstraint.getDescription();
            this.state = systemConstraint.state;
            return this;
        }

        public SystemConstraint build() {
            SystemConstraint systemConstraint = new SystemConstraint(id);
            systemConstraint.setLastEditorId(lastEditorId);
            systemConstraint.setLastEdited(lastEdited);
            systemConstraint.setLockHolderId(lockHolderId);
            systemConstraint.setLockHolderDisplayName(lockHolderDisplayName);
            systemConstraint.setLockExpirationTime(lockExpirationTime);
            systemConstraint.setName(name);
            systemConstraint.setDescription(description);
            systemConstraint.setState(state);
            return systemConstraint;
        }
    }
}

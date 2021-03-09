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

@Entity
@Table(name = "sub_hazard")
public class SubHazard extends XStamppDependentEntity implements Serializable, ReportableEntity {

    private static final long serialVersionUID = 662061821051559950L;

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

    public SubHazard() {
        // empty constructor for data mapping
    }

    public SubHazard(EntityDependentKey id) {
        super();
        this.id = id;
    }

    public EntityDependentKey getId() {
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
        SubHazard other = (SubHazard) obj;
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
        segment.add(new ReportTitledReference("Related Hazard", ReportSegment.HAZARD_ID_PREFIX + id.getParentId(),true));

        for (SubSystemConstraint constraint: xstamppProject.getSubSystemConstraints()) {
            if (id.getParentId() == constraint.getHazardId() && id.getId() == constraint.getSubHazardId()){
                segment.add(new ReportTitledReference("System Constraints", ReportSegment.SYSTEM_CONSTRAINT_ID_PREFIX + constraint.getId().getParentId(),true));
                segment.add(new ReportTitledReference("Sub System Constraints", ReportSegment.SUBSYSTEM_CONSTRAINT_ID_PREFIX + constraint.getId().getParentId() + "." + constraint.getId().getId(),true));
            }
        }

        return segment;

    }

    @Override
    public ReportNameIdPair createNameIdPair() {
        return new ReportNameIdPair(name, getReportReferenceId());
    }

    private String getReportReferenceId() {
        return ReportSegment.SUBHAZARD_ID_PREFIX+ id.getParentId()+ "." + id.getId();
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

    public static final class SubHazardBuilder {
        EntityDependentKey id;
        String name;
        String description;
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private Timestamp lockExpirationTime;
        private String state;

        private SubHazardBuilder() {
        }

        public static SubHazardBuilder aSubHazard() {
            return new SubHazardBuilder();
        }

        public SubHazardBuilder withLastEditor(UUID lastEditorId) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public SubHazardBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public SubHazardBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public SubHazardBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public SubHazardBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public SubHazardBuilder withId(EntityDependentKey id) {
            this.id = id;
            return this;
        }

        public SubHazardBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SubHazardBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public SubHazardBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public SubHazardBuilder from(SubHazard subHazard) {
            this.lastEditorId = subHazard.getLastEditorId();
            this.lastEdited = subHazard.getLastEdited();
            this.lockHolderId = subHazard.getLockHolderId();
            this.lockHolderDisplayName = subHazard.getLockHolderDisplayName();
            this.lockExpirationTime = subHazard.getLockExpirationTime();

            this.id = subHazard.getId();
            this.name = subHazard.getName();
            this.description = subHazard.getDescription();
            this.state = subHazard.state;
            return this;
        }

        public SubHazard build() {
            SubHazard subHazard = new SubHazard(id);
            subHazard.setLastEditorId(lastEditorId);
            subHazard.setLastEdited(lastEdited);
            subHazard.setLockHolderId(lockHolderId);
            subHazard.setLockHolderDisplayName(lockHolderDisplayName);
            subHazard.setLockExpirationTime(lockExpirationTime);
            subHazard.setName(name);
            subHazard.setDescription(description);
            subHazard.setState(state);
            return subHazard;
        }
    }
}

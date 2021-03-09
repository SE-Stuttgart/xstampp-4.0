package de.xstampp.service.project.data.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.xstampp.service.project.data.XStamppDependentEntity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import de.xstampp.service.project.data.entity.report.*;
import de.xstampp.service.project.data.entity.report.structureElements.ReportHeader;
import de.xstampp.service.project.data.entity.report.structureElements.ReportPlainText;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "control_algorithm")
@Entity
public class ControlAlgorithm extends XStamppDependentEntity implements ReportableEntity {

    @EmbeddedId
    @JsonUnwrapped
    private ProjectDependentKey id;
    private String name;
    private String description;
    @Column(name = "box_id")
    private String boxId;

    @Column(name = "state")
    private String state;

    public ControlAlgorithm() {
        // default constructor for hibernate
    }

    public ProjectDependentKey getId() {
        return id;
    }

    public void setId(ProjectDependentKey id) {
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

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
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
        ControlAlgorithm other = (ControlAlgorithm) obj;
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
        return segment;
    }

    @Override
    public ReportNameIdPair createNameIdPair() {
        return new ReportNameIdPair(name, getReportReferenceId());
    }

    private String getReportReferenceId() {
        return ReportSegment.CONTROL_ALGORITHM_ID_PREFIX + id.getId();
    }

    public static final class EntityAttributes {
        public static final String ID = DEPENDENT_KEY_ATTRIBUTE;
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String BOXID = "boxId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(ID);
            result.add(BOXID);
            result.add(NAME);
            result.add(DESCRIPTION);
            return result;
        }
    }

    public static final class ControlAlgorithmBuilder {
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private ProjectDependentKey id;
        private String name;
        private String description;
        private Timestamp lockExpirationTime;
        private String boxId;
        private String state;

        private ControlAlgorithmBuilder() {
        }

        public static ControlAlgorithmBuilder aControlAlgorithm() {
            return new ControlAlgorithmBuilder();
        }

        public ControlAlgorithmBuilder withLastEditor(UUID lastEditorId) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public ControlAlgorithmBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public ControlAlgorithmBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public ControlAlgorithmBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public ControlAlgorithmBuilder withId(ProjectDependentKey id) {
            this.id = id;
            return this;
        }

        public ControlAlgorithmBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ControlAlgorithmBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ControlAlgorithmBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public ControlAlgorithmBuilder withBoxId(String boxId) {
            this.boxId = boxId;
            return this;
        }

        public ControlAlgorithmBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public ControlAlgorithmBuilder from(ControlAlgorithm controlAlgorithm) {
            this.lastEditorId = controlAlgorithm.getLastEditorId();
            this.lastEdited = controlAlgorithm.getLastEdited();
            this.lockHolderId = controlAlgorithm.getLockHolderId();
            this.lockHolderDisplayName = controlAlgorithm.getLockHolderDisplayName();
            this.id = controlAlgorithm.id;
            this.name = controlAlgorithm.name;
            this.description = controlAlgorithm.description;
            this.lockExpirationTime = controlAlgorithm.getLockExpirationTime();
            this.boxId = controlAlgorithm.boxId;
            this.state = controlAlgorithm.state;
            return this;
        }

        public ControlAlgorithm build() {
            ControlAlgorithm controlAlgorithm = new ControlAlgorithm();
            controlAlgorithm.setLastEditorId(lastEditorId);
            controlAlgorithm.setLastEdited(lastEdited);
            controlAlgorithm.setLockHolderId(lockHolderId);
            controlAlgorithm.setLockHolderDisplayName(lockHolderDisplayName);
            controlAlgorithm.setId(id);
            controlAlgorithm.setName(name);
            controlAlgorithm.setDescription(description);
            controlAlgorithm.setLockExpirationTime(lockExpirationTime);
            controlAlgorithm.setBoxId(boxId);
            controlAlgorithm.setState(state);
            return controlAlgorithm;
        }
    }
}

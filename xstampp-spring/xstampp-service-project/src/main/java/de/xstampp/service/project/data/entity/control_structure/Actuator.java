package de.xstampp.service.project.data.entity.control_structure;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.data.entity.ProjectDependentKey;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import de.xstampp.service.project.data.entity.report.*;
import de.xstampp.service.project.data.entity.report.structureElements.ReportHeader;
import de.xstampp.service.project.data.entity.report.structureElements.ReportPlainText;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Actuator extends XStamppDependentEntity implements ReportableEntity {

    @EmbeddedId
    @JsonUnwrapped
    private ProjectDependentKey id;
    private String name;
    private String description;
    @Column(name = "box_id")
    private String boxId;

    @Column(name = "state")
    private String state;

    public Actuator() {
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
        Actuator other = (Actuator) obj;
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
        return ReportSegment.ACTUATOR_ID_PREFIX + id.getId();
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

    public static final class ActuatorBuilder {
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private ProjectDependentKey id;
        private String name;
        private String description;
        private String lockHolderDisplayName;
        private String boxId;
        private Timestamp lockExpirationTime;
        private String state;

        private ActuatorBuilder() {
        }

        public static ActuatorBuilder anActuator() {
            return new ActuatorBuilder();
        }

        public ActuatorBuilder withLastEditor(UUID lastEditorId) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public ActuatorBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public ActuatorBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public ActuatorBuilder withId(ProjectDependentKey id) {
            this.id = id;
            return this;
        }

        public ActuatorBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ActuatorBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ActuatorBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public ActuatorBuilder withBoxId(String boxId) {
            this.boxId = boxId;
            return this;
        }

        public ActuatorBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public ActuatorBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public ActuatorBuilder from(Actuator actuator) {
            this.lastEditorId = actuator.getLastEditorId();
            this.lastEdited = actuator.getLastEdited();
            this.lockHolderId = actuator.getLockHolderId();
            this.id = actuator.id;
            this.name = actuator.name;
            this.description = actuator.description;
            this.lockHolderDisplayName = actuator.getLockHolderDisplayName();
            this.boxId = actuator.boxId;
            this.lockExpirationTime = actuator.getLockExpirationTime();
            this.state = actuator.state;
            return this;
        }

        public Actuator build() {
            Actuator actuator = new Actuator();
            actuator.setLastEditorId(lastEditorId);
            actuator.setLastEdited(lastEdited);
            actuator.setLockHolderId(lockHolderId);
            actuator.setId(id);
            actuator.setName(name);
            actuator.setDescription(description);
            actuator.setLockHolderDisplayName(lockHolderDisplayName);
            actuator.setBoxId(boxId);
            actuator.setLockExpirationTime(lockExpirationTime);
            actuator.setState(state);
            return actuator;
        }
    }
}

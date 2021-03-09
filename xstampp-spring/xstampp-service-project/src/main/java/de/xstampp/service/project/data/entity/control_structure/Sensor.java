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
public class Sensor extends XStamppDependentEntity implements ReportableEntity {

    @EmbeddedId
    @JsonUnwrapped
    private ProjectDependentKey id;
    private String name;
    private String description;
    @Column(name = "box_id")
    private String boxId;

    @Column(name = "state")
    private String state;

    public Sensor() {
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
        Sensor other = (Sensor) obj;
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
        return ReportSegment.SENSOR_ID_PREFIX + id.getId();
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

    public static final class SensorBuilder {
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

        private SensorBuilder() {
        }

        public static SensorBuilder aSensor() {
            return new SensorBuilder();
        }

        public SensorBuilder withLastEditor(UUID lastEditor) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public SensorBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public SensorBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public SensorBuilder withId(ProjectDependentKey id) {
            this.id = id;
            return this;
        }

        public SensorBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SensorBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public SensorBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public SensorBuilder withBoxId(String boxId) {
            this.boxId = boxId;
            return this;
        }

        public SensorBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public SensorBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public SensorBuilder from(Sensor sensor) {
            this.lastEditorId = sensor.getLastEditorId();
            this.lastEdited = sensor.getLastEdited();
            this.lockHolderId = sensor.getLockHolderId();
            this.lockHolderDisplayName = sensor.getLockHolderDisplayName();
            this.lockExpirationTime = sensor.getLockExpirationTime();

            this.id = sensor.getId();
            this.name = sensor.getName();
            this.description = sensor.getDescription();
            this.boxId = sensor.getBoxId();
            this.state = sensor.state;
            return this;
        }

        public Sensor build() {
            Sensor sensor = new Sensor();
            sensor.setLastEditorId(lastEditorId);
            sensor.setLastEdited(lastEdited);
            sensor.setLockHolderId(lockHolderId);
            sensor.setId(id);
            sensor.setName(name);
            sensor.setDescription(description);
            sensor.setLockHolderDisplayName(lockHolderDisplayName);
            sensor.setBoxId(boxId);
            sensor.setLockExpirationTime(lockExpirationTime);
            sensor.setState(state);
            return sensor;
        }
    }
}

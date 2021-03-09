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
@Table(name = "conversion")
public class Conversion extends XStamppDependentEntity implements Serializable, ReportableEntity {

    private static final long serialVersionUID = 6371755108695351502L;

    @EmbeddedId
    @JsonUnwrapped
    private EntityDependentKey id;
    @Column(name = "conversion")
    private String conversion;
    @Column(name = "control_action_id")
    private int controlActionId;
    @Column(name = "actuator_id")
    private int actuatorId;

    @Column(name = "state")
    private String state;

    public Conversion() {
    }

    public Conversion(EntityDependentKey id) {
        super();
        this.id = id;
        this.actuatorId = id.getParentId();
    }

    public EntityDependentKey getId() {
        return id;
    }

    public String getConversion() {
        return conversion;
    }

    public void setConversion(String conversion) {
        this.conversion = conversion;
    }

    public int getControlActionId() {
        return controlActionId;
    }

    public void setControlActionId(int controlActionId) {
        this.controlActionId = controlActionId;
    }

    public int getActuatorId() {
        return actuatorId;
    }

    public void setActuatorId(int actuatorId) {
        this.actuatorId = actuatorId;
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
        Conversion other = (Conversion) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Conversion [id=" + id + ", conversion=" + conversion + ", controlActionId=" + controlActionId + ", actuatorId" + actuatorId + "]";
    }

    @Override
    public ReportSegment createReportSegment(XStamppProject xstamppProject) {
        ReportSegment segment = new ReportSegment(getReportReferenceId());
        segment.add(new ReportHeader("",getReportReferenceId()));
        segment.add(new ReportTitledReference("conversion", conversion, false));
        segment.add(new ReportTitledReference("Actuator", ReportSegment.ACTUATOR_ID_PREFIX + actuatorId,true));
        if (controlActionId != 0) {
            segment.add(new ReportTitledReference("Control Action", ReportSegment.CONTROL_ACTION_ID_PREFIX + controlActionId, true));
        } else {
            segment.add(new ReportTitledReference("Control Action", "none", false));
        }
        return segment;
    }

    @Override
    public ReportNameIdPair createNameIdPair() {
        return new ReportNameIdPair(conversion, getReportReferenceId());
    }

    private String getReportReferenceId() {
        return ReportSegment.CONVERSION_ID_PREFIX + actuatorId + "."+ id.getId();
    }

    public static final class EntityAttributes {
        public static final String ID = DEPENDENT_KEY_ATTRIBUTE;
        public static final String CONVERSION = "conversion";
        public static final String CONTROL_ACTION_ID = "controlActionId";
        public static final String ACTUATOR_ID = "actuatorId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(CONVERSION);
            result.add(CONTROL_ACTION_ID);
            result.add(ACTUATOR_ID);
            return result;
        }
    }

    public static final class ConversionBuilder {
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private EntityDependentKey id;
        private Timestamp lockExpirationTime;
        private String conversion;
        private int controlActionId;
        private int actuatorId;
        private String state;

        private ConversionBuilder() {
        }

        public static ConversionBuilder aConversion() {
            return new ConversionBuilder();
        }

        public ConversionBuilder withLastEditorId(UUID lastEditorId) {
            this.lastEditorId = lastEditorId;
            return this;
        }

        public ConversionBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public ConversionBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public ConversionBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public ConversionBuilder withId(EntityDependentKey id) {
            this.id = id;
            return this;
        }

        public ConversionBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public ConversionBuilder withConversion(String conversion) {
            this.conversion = conversion;
            return this;
        }

        public ConversionBuilder withControlActionId(int controlActionId) {
            this.controlActionId = controlActionId;
            return this;
        }

        public ConversionBuilder withActuatorId(int actuatorId) {
            this.actuatorId = actuatorId;
            return this;
        }

        public ConversionBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public ConversionBuilder from(Conversion conversion) {
            this.lastEditorId = conversion.getLastEditorId();
            this.lastEdited = conversion.getLastEdited();
            this.lockHolderId = conversion.getLockHolderId();
            this.lockHolderDisplayName = conversion.getLockHolderDisplayName();
            this.lockExpirationTime = conversion.getLockExpirationTime();
            this.conversion = conversion.conversion;
            this.controlActionId = conversion.controlActionId;
            this.actuatorId = conversion.actuatorId;
            this.state = conversion.state;
            return this;
        }

        public Conversion build() {
            Conversion resultConversion = new Conversion(id);
            resultConversion.setLastEditorId(lastEditorId);
            resultConversion.setLastEdited(lastEdited);
            resultConversion.setLockHolderId(lockHolderId);
            resultConversion.setLockHolderDisplayName(lockHolderDisplayName);
            resultConversion.setLockExpirationTime(lockExpirationTime);
            resultConversion.setConversion(conversion);
            resultConversion.setControlActionId(controlActionId);
            resultConversion.setActuatorId(actuatorId);
            resultConversion.setState(state);
            return resultConversion;
        }
    }
}

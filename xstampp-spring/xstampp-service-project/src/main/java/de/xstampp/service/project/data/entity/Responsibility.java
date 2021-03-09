package de.xstampp.service.project.data.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;
import de.xstampp.service.project.util.annotation.CheckState;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import de.xstampp.service.project.data.entity.report.*;
import de.xstampp.service.project.data.entity.report.structureElements.ReportHeader;
import de.xstampp.service.project.data.entity.report.structureElements.ReportPlainText;
import de.xstampp.service.project.data.entity.report.structureElements.ReportTitledReference;

/**
 * Represents a Object from type Responsibility. This class is used to map values to an existing table.
 */
@Table(name = "responsibility")
@Entity
public class Responsibility extends XStamppDependentEntity implements ReportableEntity {

    @EmbeddedId
    @JsonUnwrapped
    private ProjectDependentKey id;

    @CheckState
    @Column(name = "controller_id")
    private Integer controllerId;

    @Column(name = "controller_project_id")
    private UUID controllerProjectId;

    @CheckState
    private String name;

    @CheckState
    private String description;

    @Column(name = "state")
    private String state;

    public Responsibility() {
        // empty constructor for hibernate
    }

    public ProjectDependentKey getId() {
        return id;
    }

    public void setId(ProjectDependentKey id) {
        this.id = id;
    }

    public Integer getControllerId() {
        return controllerId;
    }

    public void setControllerId(Integer controllerId) {
        this.controllerId = controllerId;
    }

    public UUID getControllerProjectId() {
        return controllerProjectId;
    }

    public void setControllerProjectId(UUID controllerProjectId) {
        this.controllerProjectId = controllerProjectId;
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

    @Override
    public ReportSegment createReportSegment(XStamppProject xstamppProject) {
        ReportSegment segment = new ReportSegment(getReportReferenceId());
        segment.add(new ReportHeader(name, getReportReferenceId()));
        segment.add(new ReportPlainText(description));
        segment.add(new ReportTitledReference("Controller", ReportSegment.CONTROLLER_ID_PREFIX + controllerId, true));
        return segment;
    }

    @Override
    public ReportNameIdPair createNameIdPair() {
        return new ReportNameIdPair(name, getReportReferenceId());
    }

    private String getReportReferenceId() {
        return ReportSegment.RESPONSIBILITY_ID_PREFIX + id.getId();
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * Used for Hibernate search queries.
     */
    public static final class EntityAttributes {
        public static final String ID = DEPENDENT_KEY_ATTRIBUTE;
        public static final String NAME = "name";
        public static final String CONTROLLER_ID = "controllerId";
        public static final String DESCRIPTION = "description";
        public static final String STATE = "state";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(NAME);
            result.add(DESCRIPTION);
            result.add(ID);
            result.add(CONTROLLER_ID);
            result.add(STATE);
            return result;
        }

    }

        public static final class ResponsibilityBuilder {
            ProjectDependentKey id;
            Integer controllerId;
            UUID controllerProjectId;
            String name;
            String description;
            String state;
            private UUID lastEditorId;
            private Timestamp lastEdited;
            private UUID lockHolderId;
            private String lockHolderDisplayName;
            private Timestamp lockExpirationTime;

            private ResponsibilityBuilder() {
            }

            public static ResponsibilityBuilder aResponsibility() {
                return new ResponsibilityBuilder();
            }

            public ResponsibilityBuilder withLastEditor(UUID lastEditorId) {
                this.lastEditorId = lastEditorId;                return this;
            }

            public ResponsibilityBuilder withLastEdited(Timestamp lastEdited) {
                this.lastEdited = lastEdited;
                return this;
            }

            public ResponsibilityBuilder withLockHolderId(UUID lockHolderId) {
                this.lockHolderId = lockHolderId;
                return this;
            }

            public ResponsibilityBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
                this.lockHolderDisplayName = lockHolderDisplayName;
                return this;
            }

            public ResponsibilityBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
                this.lockExpirationTime = lockExpirationTime;
                return this;
            }

            public ResponsibilityBuilder withId(ProjectDependentKey id) {
                this.id = id;
                return this;
            }

            public ResponsibilityBuilder withControllerId(Integer controllerId) {
                this.controllerId = controllerId;
                return this;
            }

            public ResponsibilityBuilder withControllerProjectId(UUID controllerProjectId) {
                this.controllerProjectId = controllerProjectId;
                return this;
            }

            public ResponsibilityBuilder withName(String name) {
                this.name = name;
                return this;
            }

            public ResponsibilityBuilder withDescription(String description) {
                this.description = description;
                return this;
            }

            public ResponsibilityBuilder withState(String state) {
                this.state = state;
                return this;
            }

            public ResponsibilityBuilder from(Responsibility responsibility) {
                this.lastEditorId = responsibility.getLastEditorId();
                this.lastEdited = responsibility.getLastEdited();
                this.lockHolderId = responsibility.getLockHolderId();
                this.lockHolderDisplayName = responsibility.getLockHolderDisplayName();
                this.lockExpirationTime = responsibility.getLockExpirationTime();

                this.id = responsibility.getId();
                this.controllerId = responsibility.getControllerId();
                this.controllerId = responsibility.getControllerId();
                this.name = responsibility.getName();
                this.description = responsibility.getDescription();
                this.state = responsibility.state;
                return this;
            }

            public Responsibility build() {
                Responsibility responsibility = new Responsibility();
                responsibility.setLastEditorId(lastEditorId);
                responsibility.setLastEdited(lastEdited);
                responsibility.setLockHolderId(lockHolderId);
                responsibility.setLockHolderDisplayName(lockHolderDisplayName);
                responsibility.setLockExpirationTime(lockExpirationTime);
                responsibility.setId(id);
                responsibility.setControllerId(controllerId);
                responsibility.setControllerProjectId(controllerProjectId);
                responsibility.setName(name);
                responsibility.setDescription(description);
                responsibility.setState(state);
                return responsibility;
            }
        }
    }
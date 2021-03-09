package de.xstampp.service.project.data.entity.control_structure;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.data.entity.ProjectDependentKey;
import de.xstampp.service.project.data.entity.report.*;
import de.xstampp.service.project.data.entity.report.structureElements.ReportHeader;
import de.xstampp.service.project.data.entity.report.structureElements.ReportPlainText;
import de.xstampp.service.project.data.entity.report.structureElements.ReportTitledReference;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "process_model")
@Entity
public class ProcessModel extends XStamppDependentEntity implements ReportableEntity {

    @EmbeddedId
    @JsonUnwrapped
    private ProjectDependentKey id;
    private String name;
    private String description;

    @Column(name = "controller_id")
    private int controllerId;

    @Column(name = "state")
    private String state;


    public ProcessModel() {
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

    public void setControllerId(int controllerId) {
        this.controllerId = controllerId;
    }

    public int getControllerId() {
        return controllerId;
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
        ProcessModel other = (ProcessModel) obj;
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
        segment.add(new ReportTitledReference("Controller", ReportSegment.CONTROLLER_ID_PREFIX + controllerId, true));

        return segment;
    }

    @Override
    public ReportNameIdPair createNameIdPair() {
        return new ReportNameIdPair(name, getReportReferenceId());
    }

    private String getReportReferenceId() {
        return ReportSegment.PROCESS_MODEL_ID_PREFIX + id.getId();
    }

    public static final class EntityAttributes {
        public static final String ID = DEPENDENT_KEY_ATTRIBUTE;
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String CONTROLLERID = "controllerId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(ID);
            result.add(NAME);
            result.add(DESCRIPTION);
            result.add(CONTROLLERID);
            return result;
        }
    }

    public static final class ProcessModelBuilder {
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private ProjectDependentKey id;
        private String name;
        private String description;
        private Timestamp lockExpirationTime;
        private int controllerId;
        private String state;

        private ProcessModelBuilder() {
        }

        public static ProcessModelBuilder aProcessModel() {
            return new ProcessModelBuilder();
        }

        public ProcessModelBuilder withLastEditor(UUID lastEditor) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public ProcessModelBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public ProcessModelBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public ProcessModelBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public ProcessModelBuilder withId(ProjectDependentKey id) {
            this.id = id;
            return this;
        }

        public ProcessModelBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ProcessModelBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ProcessModelBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public ProcessModelBuilder withControllerId(int controllerId) {
            this.controllerId = controllerId;
            return this;
        }

        public ProcessModelBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public ProcessModelBuilder from(ProcessModel processModel) {
            this.lastEditorId = processModel.getLastEditorId();
            this.lastEdited = processModel.getLastEdited();
            this.lockHolderId = processModel.getLockHolderId();
            this.lockHolderDisplayName = processModel.getLockHolderDisplayName();
            this.id = processModel.getId();
            this.name = processModel.getName();
            this.description = processModel.getDescription();
            this.lockExpirationTime = processModel.getLockExpirationTime();
            this.controllerId = processModel.getControllerId();
            this.state = processModel.state;
            return this;
        }

        public ProcessModel build() {
            ProcessModel processModel = new ProcessModel();
            processModel.setLastEditorId(lastEditorId);
            processModel.setLastEdited(lastEdited);
            processModel.setLockHolderId(lockHolderId);
            processModel.setLockHolderDisplayName(lockHolderDisplayName);
            processModel.setId(id);
            processModel.setName(name);
            processModel.setDescription(description);
            processModel.setLockExpirationTime(lockExpirationTime);
            processModel.setControllerId(controllerId);
            processModel.setState(state);
            return processModel;
        }
    }
}

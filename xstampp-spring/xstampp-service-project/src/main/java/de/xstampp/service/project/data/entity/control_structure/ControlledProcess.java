package de.xstampp.service.project.data.entity.control_structure;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.xstampp.service.project.data.XStamppDependentEntity;
import de.xstampp.service.project.data.entity.ProjectDependentKey;

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

@Table(name = "controlled_process")
@Entity
public class ControlledProcess extends XStamppDependentEntity implements ReportableEntity {

    @EmbeddedId
    @JsonUnwrapped
    private ProjectDependentKey id;
    private String name;
    private String description;
    @Column(name = "box_id")
    private String boxId;

    @Column(name = "state")
    private String state;

    public ControlledProcess() {
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
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        ControlledProcess other = (ControlledProcess) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
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
		return ReportSegment.CONTROLLED_PROCESS_ID_PREFIX + id.getId();
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

    public static final class ControlledProcessBuilder {
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

        private ControlledProcessBuilder() {
        }

        public static ControlledProcessBuilder aControlledProcess() {
            return new ControlledProcessBuilder();
        }

        public ControlledProcessBuilder withLastEditor(UUID lastEditor) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public ControlledProcessBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public ControlledProcessBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public ControlledProcessBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public ControlledProcessBuilder withId(ProjectDependentKey id) {
            this.id = id;
            return this;
        }

        public ControlledProcessBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ControlledProcessBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ControlledProcessBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public ControlledProcessBuilder withBoxId(String boxId) {
            this.boxId = boxId;
            return this;
        }

        public ControlledProcessBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public ControlledProcessBuilder from(ControlledProcess controlledProcess) {
            this.lastEditorId = controlledProcess.getLastEditorId();
            this.lastEdited = controlledProcess.getLastEdited();
            this.lockHolderId = controlledProcess.getLockHolderId();
            this.lockHolderDisplayName = controlledProcess.getLockHolderDisplayName();
            this.lockExpirationTime = controlledProcess.getLockExpirationTime();

            this.id = controlledProcess.getId();
            this.name = controlledProcess.getName();
            this.description = controlledProcess.getDescription();
            this.boxId = controlledProcess.getBoxId();
            this.state = controlledProcess.state;
            return this;
        }

        public ControlledProcess build() {
            ControlledProcess controlledProcess = new ControlledProcess();
            controlledProcess.setLastEditorId(lastEditorId);
            controlledProcess.setLastEdited(lastEdited);
            controlledProcess.setLockHolderId(lockHolderId);
            controlledProcess.setLockHolderDisplayName(lockHolderDisplayName);
            controlledProcess.setId(id);
            controlledProcess.setName(name);
            controlledProcess.setDescription(description);
            controlledProcess.setLockExpirationTime(lockExpirationTime);
            controlledProcess.setBoxId(boxId);
            controlledProcess.setState(state);
            return controlledProcess;
        }
    }
}

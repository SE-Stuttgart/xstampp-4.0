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

@Table(name = "control_action")
@Entity
public class ControlAction extends XStamppDependentEntity implements ReportableEntity {
	
	@EmbeddedId
	@JsonUnwrapped
	private ProjectDependentKey id;
	private String name;
	private String description;
	@Column(name = "arrow_id")
	private String arrowId;

	private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ControlAction() {
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
	public String getArrowId() {
		return arrowId;
	}
	public void setArrowId(String arrowId) {
		this.arrowId = arrowId;
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
		ControlAction other = (ControlAction) obj;
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
		segment.add(new ReportHeader(name,getReportReferenceId()));
		segment.add(new ReportPlainText(description));
		return segment;
	}

	@Override
	public ReportNameIdPair createNameIdPair() {
		return new ReportNameIdPair(name, getReportReferenceId());
	}

	private String getReportReferenceId() {
		return ReportSegment.CONTROL_ACTION_ID_PREFIX + id.getId();
	}

	public static final class EntityAttributes {
		public static final String ID=DEPENDENT_KEY_ATTRIBUTE;
		public static final String NAME = "name";
		public static final String DESCRIPTION="description";
		public static final String ARROWID = "arrowId";
		
		public static List<String> getAllAttributes() {
			List<String> result = new ArrayList<>();
			result.add(ID);
			result.add(ARROWID);
			result.add(NAME);
			result.add(DESCRIPTION);
			return result;
		}
	}
    public static final class ControlActionBuilder {
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private ProjectDependentKey id;
        private String name;
        private String description;
        private Timestamp lockExpirationTime;
        private String arrowId;
        private String state;

        private ControlActionBuilder() {
        }

        public static ControlActionBuilder aControlAction() {
            return new ControlActionBuilder();
        }

        public ControlActionBuilder withLastEditor(UUID lastEditor) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public ControlActionBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public ControlActionBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public ControlActionBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public ControlActionBuilder withId(ProjectDependentKey id) {
            this.id = id;
            return this;
        }

        public ControlActionBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ControlActionBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ControlActionBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public ControlActionBuilder withArrowId(String arrowId) {
            this.arrowId = arrowId;
            return this;
        }

        public ControlActionBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public ControlActionBuilder from(ControlAction controlAction) {
            this.lastEditorId = controlAction.getLastEditorId();
            this.lastEdited = controlAction.getLastEdited();
            this.lockHolderId = controlAction.getLockHolderId();
            this.lockHolderDisplayName = controlAction.getLockHolderDisplayName();
            this.lockExpirationTime = controlAction.getLockExpirationTime();

            this.id = controlAction.getId();
            this.name = controlAction.getName();
            this.description = controlAction.getDescription();
            this.arrowId = controlAction.getArrowId();
            this.state = controlAction.state;
            return this;
        }

        public ControlAction build() {
            ControlAction controlAction = new ControlAction();
            controlAction.setLastEditorId(lastEditorId);
            controlAction.setLastEdited(lastEdited);
            controlAction.setLockHolderId(lockHolderId);
            controlAction.setLockHolderDisplayName(lockHolderDisplayName);
            controlAction.setId(id);
            controlAction.setName(name);
            controlAction.setDescription(description);
            controlAction.setLockExpirationTime(lockExpirationTime);
            controlAction.setArrowId(arrowId);
            controlAction.setState(state);
            return controlAction;
        }
    }
}

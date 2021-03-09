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
public class Input extends XStamppDependentEntity implements ReportableEntity {

    @EmbeddedId
    @JsonUnwrapped
    private ProjectDependentKey id;
    private String name;
    private String description;
    @Column(name = "arrow_id")
    private String arrowId;

    @Column(name = "state")
    private String state;

    public Input() {
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
        Input other = (Input) obj;
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
        return ReportSegment.INPUT_ID_PREFIX + id.getId();
    }

    public static final class EntityAttributes {
        public static final String ID = DEPENDENT_KEY_ATTRIBUTE;
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
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

    public static final class InputBuilder {
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private ProjectDependentKey id;
        private String name;
        private String description;
        private String lockHolderDisplayName;
        private String arrowId;
        private Timestamp lockExpirationTime;
        private String state;

        private InputBuilder() {
        }

        public static InputBuilder anInput() {
            return new InputBuilder();
        }

        public InputBuilder withLastEditor(UUID lastEditor) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public InputBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public InputBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public InputBuilder withId(ProjectDependentKey id) {
            this.id = id;
            return this;
        }

        public InputBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public InputBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public InputBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public InputBuilder withArrowId(String arrowId) {
            this.arrowId = arrowId;
            return this;
        }

        public InputBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public InputBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public InputBuilder from(Input input) {
            this.lastEditorId = input.getLastEditorId();
            this.lastEdited = input.getLastEdited();
            this.lockHolderId = input.getLockHolderId();
            this.lockHolderDisplayName = input.getLockHolderDisplayName();
            this.lockExpirationTime = input.getLockExpirationTime();

            this.id = input.getId();
            this.name = input.getName();
            this.description = input.getDescription();
            this.arrowId = input.getArrowId();
            this.state = input.state;
            return this;
        }

        public Input build() {
            Input input = new Input();
            input.setLastEditorId(lastEditorId);
            input.setLastEdited(lastEdited);
            input.setLockHolderId(lockHolderId);
            input.setId(id);
            input.setName(name);
            input.setDescription(description);
            input.setLockHolderDisplayName(lockHolderDisplayName);
            input.setArrowId(arrowId);
            input.setLockExpirationTime(lockExpirationTime);
            input.setState(state);
            return input;
        }
    }
}

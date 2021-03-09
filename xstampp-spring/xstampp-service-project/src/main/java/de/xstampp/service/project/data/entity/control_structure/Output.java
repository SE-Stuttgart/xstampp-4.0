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
public class Output extends XStamppDependentEntity implements ReportableEntity {

    @EmbeddedId
    @JsonUnwrapped
    private ProjectDependentKey id;
    private String name;
    private String description;
    @Column(name = "arrow_id")
    private String arrowId;

    @Column(name = "state")
    private String state;

    public Output() {
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
        Output other = (Output) obj;
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
        return ReportSegment.OUTPUT_ID_PREFIX + id.getId();
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

    public static final class OutputBuilder {
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

        private OutputBuilder() {
        }

        public static OutputBuilder anOutput() {
            return new OutputBuilder();
        }

        public OutputBuilder withLastEditor(UUID lastEditor) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public OutputBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public OutputBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public OutputBuilder withId(ProjectDependentKey id) {
            this.id = id;
            return this;
        }

        public OutputBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public OutputBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public OutputBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public OutputBuilder withArrowId(String arrowId) {
            this.arrowId = arrowId;
            return this;
        }

        public OutputBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public OutputBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public OutputBuilder from(Output output) {
            this.lastEditorId = output.getLastEditorId();
            this.lastEdited = output.getLastEdited();
            this.lockHolderId = output.getLockHolderId();
            this.lockHolderDisplayName = output.getLockHolderDisplayName();
            this.lockExpirationTime = output.getLockExpirationTime();

            this.id = output.getId();
            this.name = output.getName();
            this.description = output.getDescription();
            this.arrowId = output.getArrowId();
            this.state = output.state;
            return this;
        }

        public Output build() {
            Output output = new Output();
            output.setLastEditorId(lastEditorId);
            output.setLastEdited(lastEdited);
            output.setLockHolderId(lockHolderId);
            output.setId(id);
            output.setName(name);
            output.setDescription(description);
            output.setLockHolderDisplayName(lockHolderDisplayName);
            output.setArrowId(arrowId);
            output.setLockExpirationTime(lockExpirationTime);
            output.setState(state);
            return output;
        }
    }
}

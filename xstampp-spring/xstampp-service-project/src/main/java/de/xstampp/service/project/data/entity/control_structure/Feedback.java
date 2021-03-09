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
public class Feedback extends XStamppDependentEntity implements ReportableEntity{

    @EmbeddedId
    @JsonUnwrapped
    private ProjectDependentKey id;
    @Column(name = "process_variable_id")
    private Integer processVariableId;
    private String name;
    private String description;
    @Column(name = "arrow_id")
    private String arrowId;

    @Column(name = "state")
    private String state;

    public Feedback() {
        // default constructor for hibernate
    }

    public ProjectDependentKey getId() {
        return id;
    }

    public void setId(ProjectDependentKey id) {
        this.id = id;
    }

    public Integer getProcessVariableId() {
        return processVariableId;
    }

    public void setProcessVariableId(Integer processVariableId) {
        this.processVariableId = processVariableId;
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
        Feedback other = (Feedback) obj;
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
        return ReportSegment.FEEDBACK_ID_PREFIX + id.getId();
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

    public static final class FeedbackBuilder {
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private ProjectDependentKey id;
        private String lockHolderDisplayName;
        private Integer processVariableId;
        private String name;
        private String description;
        private Timestamp lockExpirationTime;
        private String arrowId;
        private String state;

        private FeedbackBuilder() {
        }

        public static FeedbackBuilder aFeedback() {
            return new FeedbackBuilder();
        }

        public FeedbackBuilder withLastEditor(UUID lastEditor) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public FeedbackBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public FeedbackBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public FeedbackBuilder withId(ProjectDependentKey id) {
            this.id = id;
            return this;
        }

        public FeedbackBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public FeedbackBuilder withProcessVariableId(Integer processVariableId) {
            this.processVariableId = processVariableId;
            return this;
        }

        public FeedbackBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public FeedbackBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public FeedbackBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public FeedbackBuilder withArrowId(String arrowId) {
            this.arrowId = arrowId;
            return this;
        }

        public FeedbackBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public FeedbackBuilder from(Feedback feedback) {
            this.lastEditorId = feedback.getLastEditorId();
            this.lastEdited = feedback.getLastEdited();
            this.lockHolderId = feedback.getLockHolderId();
            this.lockHolderDisplayName = feedback.getLockHolderDisplayName();
            this.lockExpirationTime = feedback.getLockExpirationTime();

            this.id = feedback.getId();
            this.processVariableId = feedback.getProcessVariableId();
            this.name = feedback.getName();
            this.description = feedback.getDescription();
            this.arrowId = feedback.getArrowId();
            this.state = feedback.state;
            return this;
        }

        public Feedback build() {
            Feedback feedback = new Feedback();
            feedback.setLastEditorId(lastEditorId);
            feedback.setLastEdited(lastEdited);
            feedback.setLockHolderId(lockHolderId);
            feedback.setId(id);
            feedback.setLockHolderDisplayName(lockHolderDisplayName);
            feedback.setProcessVariableId(processVariableId);
            feedback.setName(name);
            feedback.setDescription(description);
            feedback.setLockExpirationTime(lockExpirationTime);
            feedback.setArrowId(arrowId);
            feedback.setState(state);
            return feedback;
        }
    }
}


package de.xstampp.service.project.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class is used to Link ProcessVariables with different ProcessModels and their corresponding values
 */
@Table(name = "process_model_process_variable_link")
@Entity
public class ProcessModelProcessVariableLink implements Serializable {
    private static final long serialVersionUID = 221943772774007339L;

    @Id
    @Column(name = "project_id")
    UUID projectId;

    @Id
    @Column(name = "process_model_id")
    int processModelId;

    @Id
    @Column(name = "process_variable_id")
    int processVariableId;

    @Column(name = "process_variable_value")
    String processVariableValue;

    public ProcessModelProcessVariableLink() {
        // empty constructor for mapping
    }

    public ProcessModelProcessVariableLink(UUID projectId, int processModelId, int processVariableId, String processVariableValue) {
        super();
        this.projectId = projectId;
        this.processModelId = processModelId;
        this.processVariableId = processVariableId;
        this.processVariableValue = processVariableValue;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public int getProcessVariableId() {
        return processVariableId;
    }

    public void setProcessVariableId(int processVariableId) {
        this.processVariableId = processVariableId;
    }

    public int getProcessModelId() {
        return processModelId;
    }

    public void setProcessModelId(int processModelId) {
        this.processModelId = processModelId;
    }

    public String getProcessVariableValue() {
        return processVariableValue;
    }

    public void setProcessVariableValue(String processVariableValue) {
        this.processVariableValue = processVariableValue;
    }

    @Override
    public String toString() {
        return "ProcessModelProcessVariableLink{" +
                "projectId=" + projectId +
                ", processModelId=" + processModelId +
                ", processVariableId=" + processVariableId +
                ", processVariableValue='" + processVariableValue + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + processModelId;
        result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
        result = prime * result + processVariableId;
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
        ProcessModelProcessVariableLink other = (ProcessModelProcessVariableLink) obj;
        if (processModelId != other.processModelId)
            return false;
        if (projectId == null) {
            if (other.projectId != null)
                return false;
        } else if (!projectId.equals(other.projectId))
            return false;
        if (processVariableId != other.processVariableId)
            return false;
        if (processVariableValue != other.processVariableValue)
            return false;
        return true;
    }

    /**
     * Used for Hibernate search queries.
     */
    public static final class EntityAttributes {
        public static final String PROJECT_ID = "projectId";
        public static final String PROCESS_MODEL_ID = "processModelId";
        public static final String PROCESS_VARIABLE_ID = "processVariableId";
        public static final String PROCESS_VARIABLE_VALUE = "processVariableValue";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(PROJECT_ID);
            result.add(PROCESS_MODEL_ID);
            result.add(PROCESS_VARIABLE_ID);
            result.add(PROCESS_VARIABLE_VALUE);
            return result;
        }
    }

    public static final class ProcessModelProcessVariableLinkBuilder {
        UUID projectId;
        int processModelId;
        int processVariableId;
        String processVariableValue;

        private ProcessModelProcessVariableLinkBuilder() {
        }

        public static ProcessModelProcessVariableLinkBuilder aProcessModelProcessVariableLink() {
            return new ProcessModelProcessVariableLinkBuilder();
        }

        public ProcessModelProcessVariableLinkBuilder withProjectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public ProcessModelProcessVariableLinkBuilder withProcessModelId(int processModelId) {
            this.processModelId = processModelId;
            return this;
        }

        public ProcessModelProcessVariableLinkBuilder withProcessVariableId(int processVariableId) {
            this.processVariableId = processVariableId;
            return this;
        }

        public ProcessModelProcessVariableLinkBuilder withProcessVariableValue(String processVariableValue) {
            this.processVariableValue = processVariableValue;
            return this;
        }

        public ProcessModelProcessVariableLinkBuilder from(ProcessModelProcessVariableLink link) {
            this.projectId = link.getProjectId();
            this.processModelId = link.getProcessModelId();
            this.processVariableId = link.getProcessVariableId();
            this.processVariableValue = link.getProcessVariableValue();
            return this;
        }

        public ProcessModelProcessVariableLink build() {
            ProcessModelProcessVariableLink processModelProcessVariableLink = new ProcessModelProcessVariableLink();
            processModelProcessVariableLink.setProjectId(projectId);
            processModelProcessVariableLink.setProcessModelId(processModelId);
            processModelProcessVariableLink.setProcessVariableId(processVariableId);
            processModelProcessVariableLink.setProcessVariableValue(processVariableValue);
            return processModelProcessVariableLink;
        }
    }
}
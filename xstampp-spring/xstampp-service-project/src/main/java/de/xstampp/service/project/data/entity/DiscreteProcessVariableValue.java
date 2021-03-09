package de.xstampp.service.project.data.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Table used to save all possible values for discrete processVariables.
 */
@Entity
@Table(name = "discrete_process_variable_values")
public class DiscreteProcessVariableValue implements Serializable {

    private static final long serialVersionUID = 221943772774007371L;

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @Id
    @Column(name = "process_variable_id")
    private int processVariableId;

    @Id
    @Column(name = "variable_value")
    private String variableValue;

    public DiscreteProcessVariableValue() {
    }

    public DiscreteProcessVariableValue(UUID projectId, int processVariableId, String variableValue) {
        this.projectId = projectId;
        this.processVariableId = processVariableId;
        this.variableValue = variableValue;
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

    public String getVariableValue() {
        return variableValue;
    }

    public void setVariableValue(String variableValue) {
        this.variableValue = variableValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + processVariableId;
        result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
        result = prime * result + variableValue.hashCode();
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
        DiscreteProcessVariableValue other = (DiscreteProcessVariableValue) obj;
        if (projectId == null) {
            if (other.projectId != null)
                return false;
        } else if (!projectId.equals(other.projectId))
            return false;
        if (processVariableId != other.processVariableId)
            return false;
        if (!variableValue.equals(other.variableValue))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DiscreteProcessVariableValues{" +
                "projectId=" + projectId +
                ", processVariableId=" + processVariableId +
                ", variableValue='" + variableValue + '\'' +
                '}';
    }

    /**
     * Used for Hibernate search queries.
     */
    public static final class EntityAttributes {
        public static final String PROJECT_ID = "projectId";
        public static final String PROCESS_VARIABLE_ID = "processVariableId";
        public static final String PROCESS_VARIABLE_VALUE = "variableValue";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(PROJECT_ID);
            result.add(PROCESS_VARIABLE_ID);
            result.add(PROCESS_VARIABLE_VALUE);
            return result;
        }
    }

    public static final class DiscreteProcessVariableValueBuilder {
        private UUID projectId;
        private int processVariableId;
        private String variableValue;

        private DiscreteProcessVariableValueBuilder() {
        }

        public static DiscreteProcessVariableValueBuilder aDiscreteProcessVariableValue() {
            return new DiscreteProcessVariableValueBuilder();
        }

        public DiscreteProcessVariableValueBuilder withProjectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public DiscreteProcessVariableValueBuilder withProcessVariableId(int processVariableId) {
            this.processVariableId = processVariableId;
            return this;
        }

        public DiscreteProcessVariableValueBuilder withVariableValue(String variableValue) {
            this.variableValue = variableValue;
            return this;
        }

        public DiscreteProcessVariableValueBuilder from(DiscreteProcessVariableValue discreteProcessVariableValue) {
            this.projectId = discreteProcessVariableValue.getProjectId();
            this.processVariableId = discreteProcessVariableValue.getProcessVariableId();
            this.variableValue = discreteProcessVariableValue.getVariableValue();
            return this;
        }

        public DiscreteProcessVariableValue build() {
            DiscreteProcessVariableValue discreteProcessVariableValue = new DiscreteProcessVariableValue();
            discreteProcessVariableValue.setProjectId(projectId);
            discreteProcessVariableValue.setProcessVariableId(processVariableId);
            discreteProcessVariableValue.setVariableValue(variableValue);
            return discreteProcessVariableValue;
        }
    }
}

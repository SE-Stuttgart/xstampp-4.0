package de.xstampp.service.project.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Table(name = "process_variable_responsibility_link")
@Entity
public class ProcessVariableResponsibilityLink implements Serializable{

    private static final long serialVersionUID = 221243772774107348L;

    @Id
    @Column(name = "project_id")
    UUID projectId;

    @Id
    @Column(name = "process_variable_id")
    int processVariableId;

    @Id
    @Column(name = "responsibility_id")
    int responsibilityId;

    public ProcessVariableResponsibilityLink() {
        // empty constructor for mapping
    }

    public ProcessVariableResponsibilityLink(UUID projectId, int processVariableId, int responsibilityId) {
        super();
        this.projectId = projectId;
        this.processVariableId = processVariableId;
        this.responsibilityId = responsibilityId;
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

    public int getResponsibilityId() {
        return responsibilityId;
    }

    public void setResponsibilityId(int responsibilityId) {
        this.responsibilityId = responsibilityId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + processVariableId;
        result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
        result = prime * result + responsibilityId;
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
        ProcessVariableResponsibilityLink other = (ProcessVariableResponsibilityLink) obj;
        if (processVariableId != other.processVariableId)
            return false;
        if (projectId == null) {
            if (other.projectId != null)
                return false;
        } else if (!projectId.equals(other.projectId))
            return false;
        if (responsibilityId != other.responsibilityId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ProcessVariableResponsibilityLink [projectId=" + projectId + ", ProcessVariableID=" + processVariableId
                + ", ResponsibilityId=" + responsibilityId + "]";
    }

    /**
     * Used for Hibernate search queries.
     */
    public static final class EntityAttributes {
        public static final String PROJECT_ID = "projectId";

        public static final String PROCESS_VARIABLE_ID = "processVariableId";
        public static final String RESPONSIBILITY_ID = "responsibilityId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(PROJECT_ID);
            result.add(PROCESS_VARIABLE_ID);
            result.add(RESPONSIBILITY_ID);
            return result;
        }
    }

    public static final class ProcessVariableResponsibilityLinkBuilder {
        UUID projectId;
        int processVariableId;
        int responsibilityId;

        private ProcessVariableResponsibilityLinkBuilder() {
        }

        public static ProcessVariableResponsibilityLinkBuilder aProcessVariableResponsibilityLink() {
            return new ProcessVariableResponsibilityLinkBuilder();
        }

        public ProcessVariableResponsibilityLinkBuilder withProjectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public ProcessVariableResponsibilityLinkBuilder withProcessVariableId(int processVariableId) {
            this.processVariableId = processVariableId;
            return this;
        }

        public ProcessVariableResponsibilityLinkBuilder withResponsibilityId(int responsibilityId) {
            this.responsibilityId = responsibilityId;
            return this;
        }

        public ProcessVariableResponsibilityLinkBuilder from(ProcessVariableResponsibilityLink link) {
            this.projectId = link.getProjectId();
            this.processVariableId = link.getProcessVariableId();
            this.responsibilityId = link.getResponsibilityId();
            return this;
        }

        public ProcessVariableResponsibilityLink build() {
            ProcessVariableResponsibilityLink processVariableResponsibilityLink = new ProcessVariableResponsibilityLink();
            processVariableResponsibilityLink.setProjectId(projectId);
            processVariableResponsibilityLink.setProcessVariableId(processVariableId);
            processVariableResponsibilityLink.setResponsibilityId(responsibilityId);
            return processVariableResponsibilityLink;
        }
    }
}

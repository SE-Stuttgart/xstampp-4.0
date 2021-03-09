package de.xstampp.service.project.data.entity;

import de.xstampp.service.project.data.XStamppEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import de.xstampp.service.project.data.entity.report.*;
import de.xstampp.service.project.data.entity.report.structureElements.ReportPlainText;
import de.xstampp.service.project.data.entity.report.structureElements.ReportXslFo;
import de.xstampp.service.project.service.data.XStamppProject.XStamppProject;
import de.xstampp.service.project.service.data.report.xmlProcessor.XmlProcessor;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * Defines mapping to the database entity and represents this entity in the
 * application. This class may not have setter-methods for its id.
 * </p>
 */
@Entity
@Table(name = "system_description")
public class SystemDescription extends XStamppEntity implements Serializable, ReportableEntity {

    private static final long serialVersionUID = 721744408479941318L;

    @Id
    @Column(name = "id")
    private UUID projectId;

    @Column(name = "description")
    private String description;

    /**
     * Dummy constructor for persistence framework.
     */
    public SystemDescription() {
        super();
    }

    /**
     * Is used to create a new entity.
     *
     * @param projectId id of the corresponding project.
     */
    public SystemDescription(UUID projectId) {
        super();
        this.projectId = projectId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String content) {
        this.description = content;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SystemDescription)) {
            return false;
        }
        SystemDescription other = (SystemDescription) obj;
        if (projectId == null) {
            if (other.projectId != null) {
                return false;
            }
        } else if (!projectId.equals(other.projectId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SystemDescription [projectId=" + projectId + ", description=" + convertDescription() + ", lastEditor="
                + getLastEditorId()
                + ", lastEdited=" + getLastEdited() + ", lockExpirationTime=" + getLockExpirationTime()
                + ", lockHolderId=" + getLockHolderId() + ", lockHolderDisplayName=" + getLockHolderDisplayName() + "]";
    }


    private String convertDescription() {
        int maxLength = 50;
        if (this.description == null) {
            return null;
        }
        if (this.description.length() < maxLength) {
            return description;
        }
        return description.substring(0, maxLength) + "...";
    }

    @Override
    public ReportSegment createReportSegment(XStamppProject xstamppProject) {
        ReportSegment segment = new ReportSegment(getReportReferenceId());
        if ( description != null && !description.trim().equals("")){
            segment.add(new ReportXslFo(XmlProcessor.convertSystemDescription(description)));
        }else {
            segment.add(new ReportPlainText("-None-"));
        }
        
        return segment;
    }

    @Override
    public ReportNameIdPair createNameIdPair() {
        return new ReportNameIdPair("System Description", getReportReferenceId());
    }

    private String getReportReferenceId() {
        return "SysDesc_unique";
    }


    public static final class EntityAttributes {
        public static final String PROJECT_ID = "projectId";
        public static final String DESCRIPTION = "description";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(PROJECT_ID);
            result.add(DESCRIPTION);
            return result;
        }
    }

    public static final class SystemDescriptionBuilder {
        private UUID lastEditorId;
        private Timestamp lastEdited;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private Timestamp lockExpirationTime;
        private UUID projectId;
        private String description;

        private SystemDescriptionBuilder() {
        }

        public static SystemDescriptionBuilder aSystemDescription() {
            return new SystemDescriptionBuilder();
        }

        public SystemDescriptionBuilder withLastEditor(UUID lastEditorId) {
            this.lastEditorId = lastEditorId;            return this;
        }

        public SystemDescriptionBuilder withLastEdited(Timestamp lastEdited) {
            this.lastEdited = lastEdited;
            return this;
        }

        public SystemDescriptionBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public SystemDescriptionBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public SystemDescriptionBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public SystemDescriptionBuilder withProjectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public SystemDescriptionBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public SystemDescriptionBuilder from(SystemDescription systemDescription) {
            this.lastEditorId = systemDescription.getLastEditorId();
            this.lastEdited = systemDescription.getLastEdited();
            this.lockHolderId = systemDescription.getLockHolderId();
            this.lockHolderDisplayName = systemDescription.getLockHolderDisplayName();
            this.lockExpirationTime = systemDescription.getLockExpirationTime();

            this.projectId = systemDescription.getProjectId();
            this.description = systemDescription.getDescription();
            return this;
        }

        public SystemDescription build() {
            SystemDescription systemDescription = new SystemDescription(projectId);
            systemDescription.setLastEditorId(lastEditorId);
            systemDescription.setLastEdited(lastEdited);
            systemDescription.setLockHolderId(lockHolderId);
            systemDescription.setLockHolderDisplayName(lockHolderDisplayName);
            systemDescription.setLockExpirationTime(lockExpirationTime);
            systemDescription.setDescription(description);
            return systemDescription;
        }
    }
}

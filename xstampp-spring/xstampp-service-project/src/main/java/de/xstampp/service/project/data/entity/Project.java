package de.xstampp.service.project.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Project entity for primary keys in entities. The real project entity is stored in the authentication service
 */
@Entity
public class Project implements Serializable {

    private static final long serialVersionUID = 3647592812058457952L;

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "lock_holder_id", columnDefinition = "BINARY(16)")
    private UUID lockHolderId;

    @Column(name = "lock_holder_displayname")
    private String lockHolderDisplayName;

    @Column(name = "lock_exp_time")
    private Timestamp lockExpirationTime;

    /**
     * Dummy constructor only for the persistence framework.
     */
    public Project() {
        super();
    }

    public Project(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public UUID getLockHolderId() {
        return lockHolderId;
    }

    public void setLockHolderId(UUID lockHolderId) {
        this.lockHolderId = lockHolderId;
    }

    // FIXME: [hotfix] no user information in project service (DSGVO)
    @Deprecated
    public String getLockHolderDisplayName() {
        return "anonymous";
    }

    // FIXME: [hotfix] no user information in project service (DSGVO)
    @Deprecated
    public void setLockHolderDisplayName(String lockHolderDisplayName) {
        this.lockHolderDisplayName = "anonymous";
    }

    public Timestamp getLockExpirationTime() {
        return lockExpirationTime;
    }

    public void setLockExpirationTime(Timestamp lockExpirationTime) {
        this.lockExpirationTime = lockExpirationTime;
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
        Project other = (Project) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public static final class EntityAttributes {
        public static final String ID = "id";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(ID);
            return result;
        }
    }

    public static final class ProjectBuilder {
        private UUID id;
        private UUID lockHolderId;
        private String lockHolderDisplayName;
        private Timestamp lockExpirationTime;

        private ProjectBuilder() {
        }

        public static ProjectBuilder aProject() {
            return new ProjectBuilder();
        }

        public ProjectBuilder withId(UUID id) {
            this.id = id;
            return this;
        }

        public ProjectBuilder withLockHolderId(UUID lockHolderId) {
            this.lockHolderId = lockHolderId;
            return this;
        }

        public ProjectBuilder withLockHolderDisplayName(String lockHolderDisplayName) {
            this.lockHolderDisplayName = lockHolderDisplayName;
            return this;
        }

        public ProjectBuilder withLockExpirationTime(Timestamp lockExpirationTime) {
            this.lockExpirationTime = lockExpirationTime;
            return this;
        }

        public ProjectBuilder from(Project project) {
            this.id = project.getId();
            this.lockHolderId = project.getLockHolderId();
            this.lockHolderDisplayName = project.getLockHolderDisplayName();
            this.lockExpirationTime = project.getLockExpirationTime();
            return this;
        }

        public Project build() {
            Project project = new Project(id);
            project.setLockHolderId(lockHolderId);
            project.setLockHolderDisplayName(lockHolderDisplayName);
            project.setLockExpirationTime(lockExpirationTime);
            return project;
        }
    }
}

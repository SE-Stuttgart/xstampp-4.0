package de.xstampp.service.project.data.entity.lastId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "project_entity_last_id")
public class ProjectEntityLastId implements Serializable {

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @Id
    @Column(name = "entity")
    private String entity;

    @Column(name = "last_id")
    private int lastId;

    public ProjectEntityLastId() {
    }

    public ProjectEntityLastId(UUID projectId, String entity, int lastId) {
        this.projectId = projectId;
        this.entity = entity;
        this.lastId = lastId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, entity, lastId);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (!(obj instanceof ProjectEntityLastId))
            return false;

        ProjectEntityLastId projectEntityLastId = (ProjectEntityLastId) obj;
        boolean sameUUID    = this.projectId.equals(projectEntityLastId.projectId);
        boolean sameEntity  = this.entity.equals(projectEntityLastId.entity);
        boolean sameLastId  = this.lastId == projectEntityLastId.lastId;

        return sameUUID && sameEntity && sameLastId;
    }

    @Override
    public String toString() {
        return String.format("{project_id = %s, entity = %s, last_id = %d}", projectId, entity, lastId);
    }

    public static final class EntityAttributes {
        public static final String PROJECT_ID = "projectId";
        public static final String ENTITY = "entity";
        public static final String LAST_ID = "lastId";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(PROJECT_ID);
            result.add(ENTITY);
            result.add(LAST_ID);
            return result;
        }
    }

    public static final class ProjectEntityLastIdBuilder {
        private UUID projectId;
        private String entity;
        private int lastId;

        private ProjectEntityLastIdBuilder() {
        }

        public static ProjectEntityLastIdBuilder aProjectEntityLastId() {
            return new ProjectEntityLastIdBuilder();
        }

        public ProjectEntityLastIdBuilder withProjectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public ProjectEntityLastIdBuilder withEntity(String entity) {
            this.entity = entity;
            return this;
        }

        public ProjectEntityLastIdBuilder withLastId(int lastId) {
            this.lastId = lastId;
            return this;
        }

        public ProjectEntityLastIdBuilder from(ProjectEntityLastId projectEntityLastId) {
            this.projectId = projectEntityLastId.projectId;
            this.entity = projectEntityLastId.entity;
            this.lastId = projectEntityLastId.lastId;
            return this;
        }

        public ProjectEntityLastId build() {
            ProjectEntityLastId projectEntityLastId = new ProjectEntityLastId();
            projectEntityLastId.setProjectId(projectId);
            projectEntityLastId.setEntity(entity);
            projectEntityLastId.setLastId(lastId);
            return projectEntityLastId;
        }
    }
}

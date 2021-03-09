package de.xstampp.service.project.data.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Embeddable
public class EntityDependentKey implements Serializable {

    private static final long serialVersionUID = -8872054439437002110L;

    @Column(name = "project_id")
    private UUID projectId;
    @Column(name = "parent_id")
    private int parentId;
    @Column(name = "id")
    private int id;

    public EntityDependentKey() {

    }

    public EntityDependentKey(UUID projectId, int superId, int id) {
        super();
        this.projectId = projectId;
        this.parentId = superId;
        this.id = id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public int getParentId() {
        return parentId;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + parentId;
        result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
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
        EntityDependentKey other = (EntityDependentKey) obj;
        if (id != other.id)
            return false;
        if (parentId != other.parentId)
            return false;
        if (projectId == null) {
            if (other.projectId != null)
                return false;
        } else if (!projectId.equals(other.projectId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ProjectDependentKey [projectId=" + projectId + ", parentId=" + parentId + ", id=" + id
                + "]";
    }

    public static final class EntityAttributes {
        public static final String PROJECT_ID = "projectId";
        public static final String PARENT_ID = "parentId";
        public static final String ID = "id";

        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();

            result.add(PROJECT_ID);
            result.add(PARENT_ID);
            result.add(ID);

            return result;
        }
    }

}

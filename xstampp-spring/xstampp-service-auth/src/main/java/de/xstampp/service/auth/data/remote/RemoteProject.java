package de.xstampp.service.auth.data.remote;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * This entity class represents a project on the project service. It is made accessible by postgres_fdw and can be used
 * like a locally stored entity.
 */
@Table(name="project_remote")
@Entity
public class RemoteProject {
    private static final long serialVersionUID = 6026467082717346976L;

    @Id
    @Column(name = "id")
    private UUID id;

    /**
     * Dummy constructor only for the persistence framework.
     */
    public RemoteProject() {
        super();
    }

    public RemoteProject(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
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
        RemoteProject other = (RemoteProject) obj;
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
}

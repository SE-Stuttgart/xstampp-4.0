package de.xstampp.service.auth.data.remote;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 *This entity class represents a user on the project service. It is made accessible by postgres_fdw and can be used like
 * a locally stored entity.
 */
@Table(name = "user_remote")
@Entity
public class RemoteUser {
    private static final long serialVersionUID = 2276413945275331681L;

    @Id
    @Column(name = "id")
    private UUID id;

    /**
     * Dummy constructor only for the persistence framework.
     */
    public RemoteUser() {
        super();
    }

    public RemoteUser(UUID id) {
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

    public static final class EntityAttributes {
        public static final String ID = "id";
        public static List<String> getAllAttributes() {
            List<String> result = new ArrayList<>();
            result.add(ID);
            return result;
        }
    }
}

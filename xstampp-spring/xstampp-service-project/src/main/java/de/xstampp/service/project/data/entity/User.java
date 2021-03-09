package de.xstampp.service.project.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User entity which is relevant for saving the uuid and displayname of the last editor of entities.
 * Whenever an entity is created or updated we extract these two values from the token of the user doing that
 * and updating our local user database. This data is made available to the auth service via postgres_fdw to support
 * deletion of a user across services. If a user is deleted all the entities which were last edited by him will
 * have their last_editor_displayname and last_editor_id set to null.
 */
@Table(name = "user", schema = "public")
@Entity
public class User implements Serializable {

	private static final long serialVersionUID = 6900137110545759797L;

	@Id
	@Column(name = "id")
	private UUID id;

	@Column(name = "displayname")
	private String displayName;

	/**
	 * Dummy constructor only for the persistence framework.
	 */
	public User() {
		super();
	}

	public User(UUID id, String displayName) {
		this.id = id;
		this.displayName = displayName;
	}

	public UUID getId() {
		return id;
	}


	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
		public static final String DISPLAY_NAME = "displayName";

		public static List<String> getAllAttributes() {
			List<String> result = new ArrayList<>();
			result.add(ID);
			result.add(DISPLAY_NAME);
			return result;
		}
	}

}

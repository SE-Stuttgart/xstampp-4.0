package de.xstampp.service.auth.data;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

@Table(name = "group", schema = "public")
@Entity
public class Group implements Serializable {

	private static final long serialVersionUID = 3418232206948068574L;

	@Id
	private UUID id;
	
	private String name;
	private String description;


	@JsonProperty("private")
	@Column(name = "private")
	private boolean privateFlag = false;

	public Group() {
		/* default constructor for hibernate */
	}

	public Group(UUID id) {
		this.id = id;
	}

	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getPrivateFlag() {
		return privateFlag;
	}

	public void setPrivateFlag(boolean privateFlag) {
		this.privateFlag = privateFlag;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		Group other = (Group) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}



	public static final class EntityAttributes {
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String PRIVATE = "privateFlag";
		public static final String DESCRIPTION = "description";
	}
}

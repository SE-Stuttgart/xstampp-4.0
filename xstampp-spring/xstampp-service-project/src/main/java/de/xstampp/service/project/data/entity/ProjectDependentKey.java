package de.xstampp.service.project.data.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Needed to access tables through Hibernate with ONE primary key. Newer Versions of Hibernate don't necessarily require this anymore
 */
@Embeddable
public class ProjectDependentKey implements Serializable {

	private static final long serialVersionUID = 7851789519951252290L;
	
	@Column(name = "project_id")
	private UUID projectId;
	@Column(name = "id")
	private int id;

	public ProjectDependentKey() {

	}

	public ProjectDependentKey(UUID projectId, int id) {
		super();
		this.projectId = projectId;
		this.id = id;
	}

	public UUID getProjectId() {
		return projectId;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		ProjectDependentKey other = (ProjectDependentKey) obj;
		if (id != other.id)
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
		return "ProjectDependentKey [projectId=" + projectId + ", id=" + id + "]";
	}

	public static final class EntityAttributes {
		public static final String PROJECT_ID = "projectId";
		public static final String ID = "id";
		
		public static List<String> getAllAttributes () {
			List<String> result = new ArrayList<>();
			
			result.add(PROJECT_ID);
			result.add(ID);
			
			return result;
		}
	}

}

package de.xstampp.service.auth.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "project", schema ="public")
@Entity
public class Project implements Serializable{

	private static final long serialVersionUID = 8443666023737300882L;

	@Id
	private UUID id;
	
	private String name;
	
	private String description;
	
	@Column(name="group_id")
	private UUID groupId;
	
	@Column(name="reference_number")
	private String referenceNumber;
	
	@Column(name="created_at")
	private Timestamp createdAt;

	public Project() {
		/* default constructor for hibernate */
	}
	
	public Project(UUID id) {
		this.id = id;
	}

	public UUID getId(){
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UUID getGroupId() {
		return groupId;
	}

	public void setGroupId(UUID group_id) {
		this.groupId = group_id;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp created_at) {
		this.createdAt = created_at;
	}
	
	public static final class EntityAttributes{
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String DESCRIPTION = "description";
		public static final String GROUP_ID = "groupId";
		public static final String REFERENCE_NUMBER = "referenceNumber";
		public static final String CREATED_AT = "createdAt";
	}
	
	
}

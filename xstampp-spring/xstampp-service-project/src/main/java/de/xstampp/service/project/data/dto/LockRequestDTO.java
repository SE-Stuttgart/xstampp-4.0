package de.xstampp.service.project.data.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LockRequestDTO {
	String id;
	String parentId;
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
	Timestamp expirationTime;
	String entityName;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public Timestamp getExpirationTime() {
		return expirationTime;
	}
	public void setExpirationTime(Timestamp expirationTime) {
		this.expirationTime = expirationTime;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	
}

package com.likya.tlossw.model.auth;

import java.io.Serializable;

public class ResourcePermission implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String resourceType;
	private String resourceName;
	private boolean admin;
	private boolean superUser;
	private boolean normalUser;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public boolean getAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public boolean getSuperUser() {
		return superUser;
	}
	public void setSuperUser(boolean superUser) {
		this.superUser = superUser;
	}
	public boolean getNormalUser() {
		return normalUser;
	}
	public void setNormalUser(boolean normalUser) {
		this.normalUser = normalUser;
	}
	
}

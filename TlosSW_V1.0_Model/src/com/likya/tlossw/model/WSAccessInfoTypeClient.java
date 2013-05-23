package com.likya.tlossw.model;

import java.io.Serializable;

import com.likya.tlos.model.xmlbeans.webservice.UserAccessProfileDocument.UserAccessProfile;

public class WSAccessInfoTypeClient implements Serializable {

	private static final long serialVersionUID = -6868566540771893293L;

	private UserAccessProfile wsAccessProfile;
	
	private String serviceName;
	private String description;
	private String userOrRoleList;
	
	public UserAccessProfile getWsAccessProfile() {
		return wsAccessProfile;
	}
	public void setWsAccessProfile(UserAccessProfile wsAccessProfile) {
		this.wsAccessProfile = wsAccessProfile;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUserOrRoleList() {
		return userOrRoleList;
	}
	public void setUserOrRoleList(String userOrRoleList) {
		this.userOrRoleList = userOrRoleList;
	}
	
}

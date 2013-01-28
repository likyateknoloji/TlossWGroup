/*
 * TlosFaz_V2.0_Model
 * com.likya.tlos.model.auth : Resource.java
 * @author Serkan Tas
 * Tarih : Jul 20, 2009 11:23:33 AM
 */

package com.likya.tlossw.model.auth;

import java.io.Serializable;
import java.util.HashMap;

public class Resource implements Serializable {

	private static final long serialVersionUID = 1L;

	private String resourceId;
	private HashMap<String, Role> roleList;

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public HashMap<String, Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(HashMap<String, Role> roleList) {
		this.roleList = roleList;
	}
}

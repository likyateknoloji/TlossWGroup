/*
 * TlosSW_V1.0_Model
 * com.likya.tlossw.model.client.resource : ResourceInfoTypeClient.java
 * @author Merve Ozbey
 * Tarih : 20.Sub.2012 15:00:37
 */

package com.likya.tlossw.model.client.resource;

import java.io.Serializable;


public class ResourceInfoTypeClient implements Serializable {
	
	private static final long serialVersionUID = 3320781518103055869L;
	
	private String resourceName;
	private String osType;
	private boolean includesServer = false;
	
	private boolean active;
	
	public ResourceInfoTypeClient() {
		super();
	}
	
	public ResourceInfoTypeClient(ResourceInfoTypeClient cloneResourceInfoTypeClient) {
		
		super();
		
		if (cloneResourceInfoTypeClient.getResourceName() != null) {
			resourceName = cloneResourceInfoTypeClient.getResourceName();
		} else {
			resourceName = null;
		}
		
		if (cloneResourceInfoTypeClient.getOsType() != null) {
			osType = cloneResourceInfoTypeClient.getOsType();
		} else {
			osType = null;
		}
		
		includesServer = cloneResourceInfoTypeClient.getIncludesServer();
		active = cloneResourceInfoTypeClient.isActive();
	}
	
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	
	public String getResourceName() {
		return resourceName;
	}
	
	public void setOsType(String osType) {
		this.osType = osType;
	}
	
	public String getOsType() {
		return osType;
	}

	public void setIncludesServer(boolean includesServer) {
		this.includesServer = includesServer;
	}

	public boolean getIncludesServer() {
		return includesServer;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
}

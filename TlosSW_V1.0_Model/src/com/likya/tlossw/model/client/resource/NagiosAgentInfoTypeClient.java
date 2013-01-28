/*
 * TlosSW_V1.0_Model
 * com.likya.tlossw.model.client.resource : NagiosAgentInfoTypeClient.java
 * @author Merve Ozbey
 * Tarih : 20.Sub.2012 15:00:37
 */

package com.likya.tlossw.model.client.resource;

import java.io.Serializable;

public class NagiosAgentInfoTypeClient implements Serializable{

	private static final long serialVersionUID = -3846051460928498514L;
	
	private String resourceName;
	private int nrpePort;
	private boolean nrpeAvailable;
	
	public NagiosAgentInfoTypeClient() {
		super();
	}
	
	public NagiosAgentInfoTypeClient(NagiosAgentInfoTypeClient cloneNagiosAgentInfoTypeClient) {
		
		super();
		
		nrpePort = cloneNagiosAgentInfoTypeClient.getNrpePort();
			
		if(cloneNagiosAgentInfoTypeClient.getResourceName() != null) {
			resourceName = cloneNagiosAgentInfoTypeClient.getResourceName();
		}else {
			resourceName = null;
		}
		
		nrpeAvailable = cloneNagiosAgentInfoTypeClient.isNrpeAvailable();
	}
	
	public void setNrpePort(int nrpePort) {
		this.nrpePort = nrpePort;
	}
	public int getNrpePort() {
		return nrpePort;
	}
	public void setNrpeAvailable(boolean nrpeAvailable) {
		this.nrpeAvailable = nrpeAvailable;
	}
	public boolean isNrpeAvailable() {
		return nrpeAvailable;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getResourceName() {
		return resourceName;
	}
	
}

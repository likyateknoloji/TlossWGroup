/*
 * TlosSW_V1.0_Model
 * com.likya.tlossw.model.client.resource : TlosAgentInfoTypeClient.java
 * @author Merve Ozbey
 * Tarih : 20.Sub.2012 15:00:37
 */

package com.likya.tlossw.model.client.resource;

import java.io.Serializable;


public class TlosAgentInfoTypeClient implements Serializable {
	
	private static final long serialVersionUID = -3076637266079312091L;
	
	private int agentId;
	private String agentName;
	private String agentType;
	
	private int jmxPort;
	
	private boolean inJmxAvailable;
	private boolean outJmxAvailable;
	private boolean jmxAvailable;
	private String userStopRequest;
	
//	private int numOfJobs;
//	private int numOfActiveJobs;
//
	
	private boolean forcedDeactivate;
	private boolean normalDeactivate;
	private boolean activate;
	
	public TlosAgentInfoTypeClient() {
		super();
	}
	
	public TlosAgentInfoTypeClient(TlosAgentInfoTypeClient cloneTlosAgentInfoTypeClient) {
		
		super();
		
		agentId = cloneTlosAgentInfoTypeClient.getAgentId();
		jmxPort = cloneTlosAgentInfoTypeClient.getJmxPort();
			
		if(cloneTlosAgentInfoTypeClient.getAgentName() != null) {
			agentName = cloneTlosAgentInfoTypeClient.getAgentName();
		}else {
			agentName = null;
		}
		
		if(cloneTlosAgentInfoTypeClient.getAgentType() != null) {
			agentType = cloneTlosAgentInfoTypeClient.getAgentType();
		}else {
			agentType = null;
		}
		
		if(cloneTlosAgentInfoTypeClient.getUserStopRequest() != null) {
			userStopRequest = cloneTlosAgentInfoTypeClient.getUserStopRequest();
		} else {
			userStopRequest = "null";
		}
		
		inJmxAvailable = cloneTlosAgentInfoTypeClient.isInJmxAvailable();
		outJmxAvailable = cloneTlosAgentInfoTypeClient.isOutJmxAvailable();
		jmxAvailable = cloneTlosAgentInfoTypeClient.isJmxAvailable();
		
//		numOfJobs = cloneTlosAgentInfoTypeClient.getNumOfJobs();
//		numOfActiveJobs = cloneTlosAgentInfoTypeClient.getNumOfActiveJobs();

		forcedDeactivate = cloneTlosAgentInfoTypeClient.isForcedDeactivate();
		normalDeactivate = cloneTlosAgentInfoTypeClient.isNormalDeactivate();
		activate = cloneTlosAgentInfoTypeClient.isActivate();
	}
	
	public int getAgentId() {
		return agentId;
	}
	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}
	public String getAgentType() {
		return agentType;
	}
	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}
	public boolean isInJmxAvailable() {
		return inJmxAvailable;
	}
	public void setInJmxAvailable(boolean inJmxAvailable) {
		this.inJmxAvailable = inJmxAvailable;
	}
	public boolean isOutJmxAvailable() {
		return outJmxAvailable;
	}
	public void setOutJmxAvailable(boolean outJmxAvailable) {
		this.outJmxAvailable = outJmxAvailable;
	}
	public boolean isJmxAvailable() {
		return jmxAvailable;
	}
	public void setJmxAvailable(boolean jmxAvailable) {
		this.jmxAvailable = jmxAvailable;
	}
	public void setJmxPort(int jmxPort) {
		this.jmxPort = jmxPort;
	}
	public int getJmxPort() {
		return jmxPort;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setForcedDeactivate(boolean forcedDeactivate) {
		this.forcedDeactivate = forcedDeactivate;
	}

	public boolean isForcedDeactivate() {
		return forcedDeactivate;
	}

	public void setNormalDeactivate(boolean normalDeactivate) {
		this.normalDeactivate = normalDeactivate;
	}

	public boolean isNormalDeactivate() {
		return normalDeactivate;
	}

	public void setActivate(boolean activate) {
		this.activate = activate;
	}

	public boolean isActivate() {
		return activate;
	}

	public void setUserStopRequest(String userStopRequest) {
		this.userStopRequest = userStopRequest;
	}

	public String getUserStopRequest() {
		return userStopRequest;
	}



//	public int getNumOfJobs() {
//		return numOfJobs;
//	}
//
//	public void setNumOfJobs(int numOfJobs) {
//		this.numOfJobs = numOfJobs;
//	}
//
//	public int getNumOfActiveJobs() {
//		return numOfActiveJobs;
//	}
//
//	public void setNumOfActiveJobs(int numOfActiveJobs) {
//		this.numOfActiveJobs = numOfActiveJobs;
//	}
//
}

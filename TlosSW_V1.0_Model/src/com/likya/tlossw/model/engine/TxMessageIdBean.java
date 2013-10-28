package com.likya.tlossw.model.engine;

import java.io.Serializable;

public class TxMessageIdBean implements Serializable{

	private static final long serialVersionUID = -194711940047178506L;
	
	private String runId;
	private String spcId;
	private String jobKey;
	private int agentId;
	private String LSIDateTime;
	
	public String getSpcId() {
		return spcId;
	}
	public void setSpcId(String spcId) {
		this.spcId = spcId;
	}
	public String getJobKey() {
		return jobKey;
	}
	public void setJobKey(String jobKey) {
		this.jobKey = jobKey;
	}
	public int getAgentId() {
		return agentId;
	}
	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}
	
	public String getLSIDateTime() {
		return LSIDateTime;
	}
	
	public void setLSIDateTime(String lSIDateTime) {
		LSIDateTime = lSIDateTime;
	}
	public String getRunId() {
		return runId;
	}
	public void setRunId(String runId) {
		this.runId = runId;
	}
	
	
}

/*
 * TlosFaz_V2.0_Model
 * com.likya.tlos.core.cpc.model : SpcInfoTypeClient.java
 * @author Serkan Tas
 * Tarih : Apr 20, 2009 11:15:58 AM
 */

package com.likya.tlossw.model.client.spc;

import java.util.ArrayList;

import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;

public class JobInfoTypeClient extends JobInfoTypeClientBase {

	// Paramaters from JobPropertiesType

	/**
	 * 
	 */
	private static final long serialVersionUID = -6027037594587545575L;

	private String jobCommandType;
	private ArrayList<String> jobDependencyList;
	private String jobAutoRetry;
	private ArrayList<Integer> jobReturnCodeIgnoreList;

	// Paramaters from JobRunPropertiesType
	private String plannedExecutionDate = "-";
	private String completionDate = "-";
	private String workDuration = "-";
	private boolean isOver;
	private LiveStateInfo liveStateInfo;

	private String resourceName;
	private int agentId;
	private String nativeRunId;
	private String currentRunId;
	private String LSIDateTime;

	private String outParameterName;
	private int outParameterType;
	private String outParameterValue;

	private String inParameterName;
	private int inParameterType;
	private String inParameterValue;

	// Paramaters for webpage
	// private String statusStr;

	// private int dependJobNumber;

	public String getPlannedExecutionDate() {
		return plannedExecutionDate;
	}

	public void setPlannedExecutionDate(String plannedExecutionDate) {
		this.plannedExecutionDate = plannedExecutionDate;
	}

	public String getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(String completionDate) {
		this.completionDate = completionDate;
	}

	public String getWorkDuration() {
		return workDuration;
	}

	public void setWorkDuration(String workDuration) {
		this.workDuration = workDuration;
	}

	public boolean isOver() {
		return isOver;
	}

	public void setOver(boolean isOver) {
		this.isOver = isOver;
	}

	public java.lang.String getJobCommandType() {
		return jobCommandType;
	}

	public void setJobCommandType(java.lang.String jobCommandType) {
		this.jobCommandType = jobCommandType;
	}

	public ArrayList<String> getJobDependencyList() {
		return jobDependencyList;
	}

	public void setJobDependencyList(ArrayList<String> jobDependencyList) {
		this.jobDependencyList = jobDependencyList;
	}

	public String getJobAutoRetry() {
		return jobAutoRetry;
	}

	public void setJobAutoRetry(String jobAutoRetry) {
		this.jobAutoRetry = jobAutoRetry;
	}

	public ArrayList<Integer> getJobReturnCodeIgnoreList() {
		return jobReturnCodeIgnoreList;
	}

	public void setJobReturnCodeIgnoreList(ArrayList<Integer> jobReturnCodeIgnoreList) {
		this.jobReturnCodeIgnoreList = jobReturnCodeIgnoreList;
	}

	// public void setStatusStr(String statusStr) {
	// this.statusStr = statusStr;
	// }

	// public int getDependJobNumber() {
	// return dependJobNumber;
	// }
	//
	// public void setDependJobNumber(int dependJobNumber) {
	// this.dependJobNumber = dependJobNumber;
	// }

	public LiveStateInfo getLiveStateInfo() {
		return liveStateInfo;
	}

	public void setLiveStateInfo(LiveStateInfo liveStateInfo) {
		this.liveStateInfo = liveStateInfo;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}

	public int getAgentId() {
		return agentId;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setLSIDateTime(String lSIDateTime) {
		LSIDateTime = lSIDateTime;
	}

	public String getLSIDateTime() {
		return LSIDateTime;
	}

	// public String getStatusStr() {
	// return statusStr;
	// }

	public String getOutParameterName() {
		return outParameterName;
	}

	public void setOutParameterName(String outParameterName) {
		this.outParameterName = outParameterName;
	}

	public String getOutParameterValue() {
		return outParameterValue;
	}

	public void setOutParameterValue(String outParameterValue) {
		this.outParameterValue = outParameterValue;
	}

	public String getInParameterName() {
		return inParameterName;
	}

	public void setInParameterName(String inParameterName) {
		this.inParameterName = inParameterName;
	}

	public String getInParameterValue() {
		return inParameterValue;
	}

	public void setInParameterValue(String inParameterValue) {
		this.inParameterValue = inParameterValue;
	}

	public int getOutParameterType() {
		return outParameterType;
	}

	public void setOutParameterType(int outParameterType) {
		this.outParameterType = outParameterType;
	}

	public int getInParameterType() {
		return inParameterType;
	}

	public void setInParameterType(int inParameterType) {
		this.inParameterType = inParameterType;
	}

	public String getNativeRunId() {
		return nativeRunId;
	}

	public void setNativeRunId(String nativeRunId) {
		this.nativeRunId = nativeRunId;
	}

	public String getCurrentRunId() {
		return currentRunId;
	}

	public void setCurrentRunId(String currentRunId) {
		this.currentRunId = currentRunId;
	}



}

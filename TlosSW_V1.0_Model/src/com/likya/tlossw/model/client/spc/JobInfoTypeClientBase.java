/*
 * TlosFaz_V2.0_Model
 * com.likya.tlos.core.cpc.model : SpcInfoTypeClient.java
 * @author Serkan Tas
 * Tarih : Apr 20, 2009 11:15:58 AM
 */

package com.likya.tlossw.model.client.spc;


public class JobInfoTypeClientBase implements JobInfoTypeClientInterface {

	private static final long serialVersionUID = 1L;

	private String jobId;
	private String jobName;
//	private String jobKey;
	private String treePath;
	private String jobPath;
	private String jobCommand;
	private String jobLogPath;
	private String jobLogName;
	private int jobPriority;
	private String jobPlanTime;
	private String jobPlanEndTime;
	private String jobTimeOut;
	private String safeRestart; 
	private String oSystem;
	
	 // Paramaters from functions
	
	private boolean retriable;
	private boolean successable;
	private boolean skippable;
	private boolean stopable;
	private boolean pausable;
	private boolean resumable;
	private boolean startable;
	
	public String getJobPath() {
		return jobPath;
	}

	public void setJobPath(String jobPath) {
		this.jobPath = jobPath;
	}

//	public String getJobKey() {
//		return jobKey;
//	}
//
//	public void setJobKey(String jobKey) {
//		this.jobKey = jobKey;
//	}

	public String getJobCommand() {
		return jobCommand;
	}

	public void setJobCommand(String jobCommand) {
		this.jobCommand = jobCommand;
	}

	public String getJobLogPath() {
		return jobLogPath;
	}

	public void setJobLogPath(String jobLogPath) {
		this.jobLogPath = jobLogPath;
	}

	public int getJobPriority() {
		return jobPriority;
	}

	public void setJobPriority(int jobPriority) {
		this.jobPriority = jobPriority;
	}

	public String getJobPlanTime() {
		return jobPlanTime;
	}

	public void setJobPlanTime(String jobPlanTime) {
		this.jobPlanTime = jobPlanTime;
	}

	public String getJobTimeOut() {
		return jobTimeOut;
	}

	public void setJobTimeOut(String jobTimeOut) {
		this.jobTimeOut = jobTimeOut;
	}
	
	/**
	 * Icefaces Bean compatibility
	 * @return
	 */
	public boolean getStopable() {
		return stopable;
	}

	public void setStopable(boolean stopable) {
		this.stopable = stopable;
	}

	public boolean getRetriable() {
		return retriable;
	}

	public void setRetriable(boolean retriable) {
		this.retriable = retriable;
	}

	public boolean getSuccessable() {
		return successable;
	}

	public void setSuccessable(boolean successable) {
		this.successable = successable;
	}

	public boolean getSkippable() {
		return skippable;
	}

	public void setSkippable(boolean skippable) {
		this.skippable = skippable;
	}

	public boolean getPausable() {
		return pausable;
	}

	public void setPausable(boolean pausable) {
		this.pausable = pausable;
	}

	public boolean getResumable() {
		return resumable;
	}

	public void setResumable(boolean resumable) {
		this.resumable = resumable;
	}

	public boolean getStartable() {
		return startable;
	}

	public void setStartable(boolean startable) {
		this.startable = startable;
	}

	public String getSafeRestart() {
		return safeRestart;
	}

	public void setSafeRestart(String safeRestart) {
		this.safeRestart = safeRestart;
	}

	public String getTreePath() {
		return treePath;
	}

	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}

	public void setoSystem(String oSystem) {
		this.oSystem = oSystem;
	}

	public String getoSystem() {
		return oSystem;
	}

	public void setJobLogName(String jobLogName) {
		this.jobLogName = jobLogName;
	}

	public String getJobLogName() {
		return jobLogName;
	}

	public void setJobPlanEndTime(String jobPlanEndTime) {
		this.jobPlanEndTime = jobPlanEndTime;
	}

	public String getJobPlanEndTime() {
		return jobPlanEndTime;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
}

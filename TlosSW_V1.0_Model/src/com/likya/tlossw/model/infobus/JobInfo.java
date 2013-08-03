/*
 * TlosFaz_V2.0
 * com.likya.tlos.infobus.helper : ScenarioStart.java
 * @author Serkan Taş
 * Tarih : Nov 28, 2008 4:13:46 PM
 */

package com.likya.tlossw.model.infobus;

import java.util.Date;

import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;

/**
 * InfoType interface inden de yararlanarak diger ilgili class lari
 * iliskilendirdigimiz Job class i. Temel Job bilgilerini iceriyor. treePath,
 * jobKey, infoDate, ve calisan islerin state lerinin tutuldugu
 * liveLiveStateInfo.
 * 
 * @author tlosSW Dev Team
 * @since v1.0
 * 
 */
public class JobInfo implements InfoType {

	private static final long serialVersionUID = 6215054106876862103L;

	private String treePath;
//	private String jobKey;
	private String jobName;
	private String jobID;
	private int agentID;
	private int userID;
	private Date infoDate;
	private LiveStateInfo liveLiveStateInfo;

//	public String getJobKey() {
//		return jobKey;
//	}
//
//	public void setJobKey(String jobKey) {
//		this.jobKey = jobKey;
//	}

	public Date getInfoDate() {
		return infoDate;
	}

	public void setInfoDate(Date infoDate) {
		this.infoDate = infoDate;
	}

	public String getTreePath() {
		return treePath;
	}

	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}

	public LiveStateInfo getLiveLiveStateInfo() {
		return liveLiveStateInfo;
	}

	public void setLiveLiveStateInfo(LiveStateInfo liveLiveStateInfo) {
		this.liveLiveStateInfo = liveLiveStateInfo;
	}

	public String getJobID() {
		return jobID;
	}

	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getAgentID() {
		return agentID;
	}

	public void setAgentID(int agentID) {
		this.agentID = agentID;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

}

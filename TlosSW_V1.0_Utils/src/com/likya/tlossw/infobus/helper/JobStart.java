/*
 * TlosFaz_V2.0
 * com.likya.tlos.infobus.helper : ScenarioStart.java
 * @author Serkan Taş
 * Tarih : Nov 28, 2008 4:13:46 PM
 */

package com.likya.tlossw.infobus.helper;

/**
 * JobInfo class indan extend edilen jobPath ve jobKey parametrelerini set ve
 * get edebildigimiz class.
 * 
 * @author tlosSW Dev Team
 * @since v1.0
 * 
 */
public class JobStart extends JobInfo {

	private static final long serialVersionUID = 6164026950216235026L;

	private String jobPath;
//	private String jobKey;

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

}

/*
 * TlosFaz_V2.0_Model
 * com.likya.tlos.model.tree.serkan : JobNode.java
 * @author Serkan Tas
 * Tarih : 18.Nis.2010 23:46:40
 */

package com.likya.tlossw.model.tree;

import java.io.Serializable;

import com.likya.tlossw.model.client.spc.JobInfoTypeClient;

public class JobNode implements Serializable {

	private static final long serialVersionUID = 3493229044094671923L;
	
	private JobInfoTypeClient jobInfoTypeClient = new JobInfoTypeClient();
	private String leafIcon;
	private String jobPath;
	private String jobName;
	
	public JobInfoTypeClient getJobInfoTypeClient() {
		return jobInfoTypeClient;
	}

	public void setJobInfoTypeClient(JobInfoTypeClient jobInfoTypeClient) {
		this.jobInfoTypeClient = jobInfoTypeClient;
	}

	public String getLeafIcon() {
		return leafIcon;
	}

	public void setLeafIcon(String leafIcon) {
		this.leafIcon = leafIcon;
	}

	public String getJobPath() {
		return jobPath;
	}

	public void setJobPath(String jobPath) {
		this.jobPath = jobPath;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
}

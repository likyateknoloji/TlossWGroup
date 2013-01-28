package com.likya.tlossw.model;

import java.io.Serializable;

public class JobStatusSummary implements Serializable {

	private static final long serialVersionUID = 6608170787599812635L;

	private String jobId;
	private int jobStatus;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public int getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(int jobStatus) {
		this.jobStatus = jobStatus;
	}

}

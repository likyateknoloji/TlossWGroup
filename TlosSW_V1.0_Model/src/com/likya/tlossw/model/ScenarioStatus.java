package com.likya.tlossw.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ScenarioStatus implements Serializable {

	private static final long serialVersionUID = 1983923774289746028L;

	private String scenarioId;
	private boolean scenarioStatus;
	private ArrayList<JobStatusSummary> jobList;

	public String getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(String scenarioId) {
		this.scenarioId = scenarioId;
	}

	public boolean isScenarioStatus() {
		return scenarioStatus;
	}

	public void setScenarioStatus(boolean scenarioStatus) {
		this.scenarioStatus = scenarioStatus;
	}

	public ArrayList<JobStatusSummary> getJobList() {
		return jobList;
	}

	public void setJobList(ArrayList<JobStatusSummary> jobList) {
		this.jobList = jobList;
	}

}

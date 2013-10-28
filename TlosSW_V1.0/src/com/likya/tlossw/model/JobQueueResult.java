package com.likya.tlossw.model;

public class JobQueueResult {

	private boolean isJobQueueOver = true;
	
	private int numOfDailyJobsNotOver;
	
	private int numOfNonDailyJobsNotOver;

	public boolean isJobQueueOver() {
		return isJobQueueOver;
	}

	public void setJobQueueOver(boolean isJobQueueOver) {
		this.isJobQueueOver = isJobQueueOver;
	}

	public int getNumOfDailyJobsNotOver() {
		return numOfDailyJobsNotOver;
	}

	public void setNumOfDailyJobsNotOver(int numOfDailyJobsNotOver) {
		this.numOfDailyJobsNotOver = numOfDailyJobsNotOver;
	}

	public int getNumOfNonDailyJobsNotOver() {
		return numOfNonDailyJobsNotOver;
	}

	public void setNumOfNonDailyJobsNotOver(int numOfNonDailyJobsNotOver) {
		this.numOfNonDailyJobsNotOver = numOfNonDailyJobsNotOver;
	}

}

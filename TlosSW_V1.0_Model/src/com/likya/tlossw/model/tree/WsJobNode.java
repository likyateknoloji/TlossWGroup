package com.likya.tlossw.model.tree;


/**
 * Thia model class is designed to carry the job information for workspace operations that are
 * related to database.
 * 
 * @author serkan
 * 
 */
public class WsJobNode extends WsNode {

	private static final long serialVersionUID = 1L;
	
	private int jobType;

	public int getJobType() {
		return jobType;
	}

	public void setJobType(int jobType) {
		this.jobType = jobType;
	}

}

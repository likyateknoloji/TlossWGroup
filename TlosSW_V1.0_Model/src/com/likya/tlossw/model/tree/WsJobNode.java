/*
 * TlosFaz_V2.0_Model
 * com.likya.tlos.model.tree.serkan : JobNode.java
 * @author Serkan Tas
 * Tarih : 18.Nis.2010 23:46:40
 */

package com.likya.tlossw.model.tree;

import java.io.Serializable;

/**
 * Thia model class is designed to carry the job information 
 * for workspace operations that are related to database.
 * 
 * @author serkan
 * 
 */
public class WsJobNode implements Serializable {

	private static final long serialVersionUID = 3493229044094671923L;

	private String leafIcon;
	private String jobId;
	private String jobPath;
	private String jobName;

	private String labelText;

	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
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

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

}

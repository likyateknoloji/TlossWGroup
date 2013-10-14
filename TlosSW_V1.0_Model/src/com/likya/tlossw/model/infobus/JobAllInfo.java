package com.likya.tlossw.model.infobus;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;

/**
 * Job ile ilgili calistirmalarda temel bilgileri aldigimiz class. JobProperties
 * ve Senaryo nun proses ID si olan spcAbsolutePath i set ve get ile aliyoruz.
 * 
 * @author tlosSW Dev Team
 * @since v1.0
 * 
 */
public class JobAllInfo implements InfoType {

	private static final long serialVersionUID = -4207776239698697355L;

	private JobProperties jobProperties;
	private String spcAbsolutePath;
	private boolean isFirstJobInfo;

	public JobProperties getJobProperties() {
		return jobProperties;
	}

	public void setJobProperties(JobProperties jobProperties) {
		this.jobProperties = jobProperties;
	}

	public boolean isFirstJobInfo() {
		return isFirstJobInfo;
	}

	public void setFirstJobInfo(boolean isFirstJobInfo) {
		this.isFirstJobInfo = isFirstJobInfo;
	}

	public String getSpcAbsolutePath() {
		return spcAbsolutePath;
	}

	public void setSpcAbsolutePath(String spcAbsolutePath) {
		this.spcAbsolutePath = spcAbsolutePath;
	}

}

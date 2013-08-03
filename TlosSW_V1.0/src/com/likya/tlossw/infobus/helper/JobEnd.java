/*
 * TlosFaz_V2.0
 * com.likya.tlos.infobus.helper : ScenarioStart.java
 * @author Serkan Ta�
 * Tarih : Nov 28, 2008 4:13:46 PM
 */

package com.likya.tlossw.infobus.helper;

import com.likya.tlossw.model.infobus.JobInfo;

/**
 * Job in sonlanmasi sirasinda elde edilebilen bazi bilgilerin tutuldugu class.
 * Bunlar; isin toplam calisma suresi (jobDuration), 
 * isin bitiminde cikis bilgilerinin yazildigi (outputBuffer), 
 * isin herhangi bir hata vermesi durumunda bu hatalarin yazildigi (errorBuffer). 
 * Bunlara iliskin get ve set metod lari da icerilmektedir.
 * 
 * @author tlosSW Dev Team
 * @since v1.0
 * 
 */
public class JobEnd extends JobInfo {

	private static final long serialVersionUID = 4356950268545788505L;

	private long jobDuration;
	private StringBuffer outputBuffer;
	private StringBuffer errorBuffer;

	public long getJobDuration() {
		return jobDuration;
	}

	public void setJobDuration(long jobDuration) {
		this.jobDuration = jobDuration;
	}

	public StringBuffer getOutputBuffer() {
		return outputBuffer;
	}

	public void setOutputBuffer(StringBuffer outputBuffer) {
		this.outputBuffer = outputBuffer;
	}

	public StringBuffer getErrorBuffer() {
		return errorBuffer;
	}

	public void setErrorBuffer(StringBuffer errorBuffer) {
		this.errorBuffer = errorBuffer;
	}
}

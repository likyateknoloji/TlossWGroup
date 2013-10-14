/*
 * TlosFaz_V2.0
 * com.likya.tlos.infobus.helper : ScenarioStart.java
 * @author Serkan Taş
 * Tarih : Nov 28, 2008 4:13:46 PM
 */

package com.likya.tlossw.infobus.helper;

import java.util.Date;

import com.likya.tlossw.model.infobus.InfoType;

/**
 * Senaryonun bitisi sirasinda belirlenmis olmasi gereken;
 * scenarioID, endDate ve jobCount bilgilerinin ve ilgili set ve get metodlarinin tanimlandigi class.
 * 
 * @author tlosSW Dev Team
 * @since v1.0
 * 
 */
public class ScenarioEnd implements InfoType {

	private static final long serialVersionUID = 1355434222323320780L;

	private String scenarioId;
	private Date endDate;
	private int jobCount;

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getJobCount() {
		return jobCount;
	}

	public void setJobCount(int jobCount) {
		this.jobCount = jobCount;
	}

	public String getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(String scenarioId) {
		this.scenarioId = scenarioId;
	}

}

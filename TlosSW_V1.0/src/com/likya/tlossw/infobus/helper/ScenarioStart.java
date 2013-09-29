/*
 * TlosFaz_V2.0
 * com.likya.tlos.infobus.helper : ScenarioStart.java
 * @author Serkan Ta�
 * Tarih : Nov 28, 2008 4:13:46 PM
 */

package com.likya.tlossw.infobus.helper;

import java.util.Date;

import com.likya.tlossw.model.infobus.InfoType;
import com.likya.tlossw.model.path.ScenarioPathType;

/**
 * Senaryonun baslatilmasi sirasinda belirlenmis olmasi gereken; 
 * scenarioID, startDate ve jobCount bilgilerinin ve ilgili set ve get metodlarinin tanimlandigi class. 
 * @author tlosSW Dev Team
 * @since v1.0
 * 
 */
public class ScenarioStart implements InfoType {

	private static final long serialVersionUID = -1137085811031817643L;
	
	private ScenarioPathType scenarioId;
	private Date startDate;
	private int jobCount;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public int getJobCount() {
		return jobCount;
	}

	public void setJobCount(int jobCount) {
		this.jobCount = jobCount;
	}

	public ScenarioPathType getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(ScenarioPathType scenarioId) {
		this.scenarioId = scenarioId;
	}

}

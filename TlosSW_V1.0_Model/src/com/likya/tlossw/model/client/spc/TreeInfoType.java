package com.likya.tlossw.model.client.spc;

import java.io.Serializable;
import java.util.HashMap;

import com.likya.tlossw.model.ScenarioStatus;

public class TreeInfoType implements Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<String, ScenarioStatus> scenarioStatusList;

	public HashMap<String, ScenarioStatus> getScenarioStatusList() {
		return scenarioStatusList;
	}

	public void setScenarioStatusList(
			HashMap<String, ScenarioStatus> scenarioStatusList) {
		this.scenarioStatusList = scenarioStatusList;
	}



}

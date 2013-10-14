package com.likya.tlossw.infobus.helper;

import java.util.Calendar;

public class ScenarioMessageFactory {
	
	public static ScenarioStart generateScenarioStart(String spcAbsolutePath, int queueSize) {
		
		ScenarioStart scenarioStart = new ScenarioStart();
		scenarioStart.setScenarioId(spcAbsolutePath);
		scenarioStart.setStartDate(Calendar.getInstance().getTime());
		scenarioStart.setJobCount(queueSize);
		
		return scenarioStart;
	}
	
	public static ScenarioEnd generateScenarioEnd(String spcAbsolutePath, int queueSize) {
		
		ScenarioEnd scenarioEnd = new ScenarioEnd();
		scenarioEnd.setScenarioId(spcAbsolutePath);
		scenarioEnd.setEndDate(Calendar.getInstance().getTime());
		scenarioEnd.setJobCount(queueSize);
		
		return scenarioEnd;
	}
}

package com.likya.tlossw.infobus.helper;

import java.util.Calendar;

import com.likya.tlossw.model.path.TlosSWPathType;

public class ScenarioMessageFactory {
	
	public static ScenarioStart generateScenarioStart(TlosSWPathType spcId, int queueSize) {
		
		ScenarioStart scenarioStart = new ScenarioStart();
		scenarioStart.setScenarioId(spcId);
		scenarioStart.setStartDate(Calendar.getInstance().getTime());
		scenarioStart.setJobCount(queueSize);
		
		return scenarioStart;
	}
	
	public static ScenarioEnd generateScenarioEnd(TlosSWPathType spcId, int queueSize) {
		
		ScenarioEnd scenarioEnd = new ScenarioEnd();
		scenarioEnd.setScenarioId(spcId);
		scenarioEnd.setEndDate(Calendar.getInstance().getTime());
		scenarioEnd.setJobCount(queueSize);
		
		return scenarioEnd;
	}
}

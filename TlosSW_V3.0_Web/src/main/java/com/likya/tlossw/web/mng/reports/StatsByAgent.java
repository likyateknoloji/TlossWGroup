package com.likya.tlossw.web.mng.reports;

import java.util.HashMap;

public class StatsByAgent {
	private Integer agentId;
	private String label;
	private Integer index;

	HashMap<String, Integer> statsArray = new HashMap<String, Integer>(); 
	
	public StatsByAgent(Integer index) {
		super();
		this.index = index;
	}
	
	public Integer getAgentId() {
		return agentId;
	}

	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public void incrementCount(String key) {
		this.statsArray.put(key, this.statsArray.get(key).intValue() + 1);
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public HashMap<String, Integer> getStatsArray() {
		return statsArray;
	}

	public void setStatsArray(String key, Integer value) {
		this.statsArray.put(key, value);
	}
}

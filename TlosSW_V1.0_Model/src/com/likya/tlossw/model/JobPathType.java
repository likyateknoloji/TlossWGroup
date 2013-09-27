package com.likya.tlossw.model;

import com.likya.tlossw.exceptions.TlosException;

public class JobPathType extends ScenarioPathType {

	private String id;
	
	public JobPathType() {
		super();
	}

	public JobPathType(String pathId) throws TlosException {
		
		String pathArray[] = pathId.split(".");
		
		if (pathArray.length < 4) {
			throw new TlosException("Job path format exception ! Valid syntax : root.instanceId.scenarioId.jobId");
		}
		
		parsePathString(pathId);
		
	}
	
	public String getParentId() {
		return super.getId();
	}

	public void setParentId(String Id) {
		super.setId(id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}

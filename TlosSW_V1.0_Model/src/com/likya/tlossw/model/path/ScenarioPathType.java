package com.likya.tlossw.model.path;

import java.util.ArrayList;
import java.util.Arrays;

import com.likya.tlossw.exceptions.TlosException;

public class ScenarioPathType extends BasePathType {

	/**
	 * Path Type Components
	 * root.instance.x.y.z.c.id
	 * 1 : root : root
	 * 2 : instance : instance
	 * 3 : root.instance.x.y.z.c.id : full path
	 * 4 : x.y.z.c.id : absolute path
	 * 5 : id : id
	 */

	public ScenarioPathType() {
		super();
	}

	public ScenarioPathType(String pathId) {
		super();
		parsePathString(pathId);
	}

	public ScenarioPathType(ScenarioPathType scenarioPathType) {
		super();
		setPathArray(new ArrayList<String>(scenarioPathType.getPathArray()));
	}

	protected void parsePathString(String pathText) {

		String pathArray[] = pathText.split("\\.");

		if (pathArray.length > 1) {
			getPathArray().clear();
			getPathArray().addAll(Arrays.asList(pathArray));
		} else {
			try {
				throw new TlosException("Invalid path string format : [root.instanceid...] >> " + pathText);
			} catch (TlosException e) {
				e.printStackTrace();
			}
		}
	}
 
	public void setInstanceId(String instanceId) {
		if(hasDots(instanceId, "instanceId")) {
			return;
		}
		if (getPathArray().size() > 1) {
			getPathArray().set(1, instanceId);
		} else {
			getPathArray().add(instanceId);
		}
	}

	public String getInstanceId() {
		if (getPathArray().size() > 1) {
			return getPathArray().get(1);
		} else {
			return null;
		}
	}

	public String getAbsolutePath() {
		String listString = "";

		if(getPathArray().size() > 2) {
			for (int i = 2; i < getPathArray().size(); i ++) {
				listString += getPathArray().get(i) + ".";
			}
			return listString.substring(0, listString.length() - 1);
		} 
		
		return listString;
		
	}

	public String getId() {
		if(getPathArray().size() > 2) {
			return getPathArray().get(getPathArray().size() - 1);
		} 
		return null;
	}

	public void setId(String idText) {
		if(hasDots(idText, "Id")) {
			return;
		}
		if(getPathArray().size() > 2) {
			getPathArray().set(2, idText);
		} else {
			getPathArray().add(idText);
		}
	}

	public String getFullPath() {
		String listString = "";

		for (String item: getPathArray()) {
			listString += item + ".";
		}
		
		return listString.substring(0, listString.length() - 1);
	}

	public void add(String idText) {
		if(hasDots(idText, "Id")) {
			return;
		}
		getPathArray().add(idText);
	}

	private boolean hasDots(String item, String itemName) {
		if(item.contains(".")) {
			try {
				throw new TlosException("Invalid " + itemName + ", can not contain dots ! >> " + item);
			} catch (TlosException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
}

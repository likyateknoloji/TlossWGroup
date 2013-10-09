package com.likya.tlossw.model.path;

import java.util.ArrayList;
import java.util.Arrays;

import com.likya.tlossw.exceptions.TlosException;

public class TlosSWPathType extends BasePathType {

	/**
	 * Path Type Components
	 * root.plan.x.y.z.c.id
	 * 1 : root : root
	 * 2 : plan : plan
	 * 3 : root.plan.x.y.z.c.id : full path
	 * 4 : x.y.z.c.id : absolute path
	 * 5 : id : id
	 */

	public TlosSWPathType() {
		super();
	}

	public TlosSWPathType(String pathId) {
		super();
		parsePathString(pathId);
	}

	public TlosSWPathType(TlosSWPathType scenarioPathType) {
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
				throw new TlosException("Invalid path string format : [root.planId...] >> " + pathText);
			} catch (TlosException e) {
				e.printStackTrace();
			}
		}
	}
 
	public void setPlanId(String planId) {
		if(hasDots(planId, "planId")) {
			return;
		}
		if (getPathArray().size() > 1) {
			getPathArray().set(1, planId);
		} else {
			getPathArray().add(planId);
		}
	}

	public String getPlanId() {
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

	public JSPathId getId() {
		if(getPathArray().size() > 2) {
			return new JSPathId(getPathArray().get(getPathArray().size() - 1));
		} 
		return null;
	}

	public void setId(JSPathId idText) {
		if(hasDots(idText.toString(), "Id")) {
			return;
		}
		if(getPathArray().size() > 2) {
			getPathArray().set(2, idText.toString());
		} else {
			getPathArray().add(idText.toString());
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

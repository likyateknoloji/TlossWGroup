package com.likya.tlossw.model;

public class ScenarioPathType {

	/**
	 * Path Type Components
	 * root.instance.x.y.z.c.id 
	 * 1 : root : root 
	 * 2 : instance : instance 
	 * 3 : root.instance.x.y.z.c.id : full path 
	 * 4 : x.y.z.c : simple path 
	 * 5 : root.instance.x.y.z.c : absolute path 
	 * 6 : id : id
	 */

	private String root;
	private String instanceId;
	private String simplePath;
	private String id;

	public ScenarioPathType() {
	}

	protected void parsePathString(String pathId) {

		String pathArray[] = pathId.split(".");

		if (pathArray.length >= 1) {
			this.root = pathArray[0];
			if (pathArray.length >= 2) {
				this.instanceId = pathArray[1];
				if (pathArray.length == 3) {
					this.id = pathArray[2];
				} else if (pathArray.length > 3) {
					String tmpStr = pathArray[3];
					this.simplePath = tmpStr.substring(0, tmpStr.lastIndexOf("."));
					this.id = tmpStr.substring(tmpStr.lastIndexOf(".") + 1);
				}
			}
		}
	}

	public ScenarioPathType(String pathId) {
		parsePathString(pathId);
	}

	public ScenarioPathType(ScenarioPathType pathId) {
		this.id = pathId.id;
		this.simplePath = pathId.simplePath;
		this.root = pathId.root;
		this.instanceId = pathId.instanceId;
	}

	public ScenarioPathType(String root, String instanceId) {
		super();
		this.root = root;
		this.instanceId = instanceId;
	}

	public ScenarioPathType(String root, String instanceId, String id) {
		super();
		this.root = root;
		this.instanceId = instanceId;
		this.id = id;
	}

	public String getAbsolutePath() {
		return root + "." + instanceId + "." + simplePath;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFromInstance() {
		return instanceStr();
	}

	public String getFromRoot() {
		return toString();
	}

	public String getSimplePath() {
		return simplePath;
	}

	public String getFullPath() {
		return getAbsolutePath() + "." + id;
	}
	
	public void add(String id) {
		if(simplePath == null) {
			simplePath = this.id;	
		} else {
			simplePath += "." + this.id;
		}
		this.id = id;
	}
	
	public String toString() {

		String toStringValue = "";

		if (root != null) {
			toStringValue = root;
			toStringValue += instanceStr();
		}

		return toStringValue;
	}

	private String instanceStr() {

		String toStringValue = "";

		if (instanceId != null) {
			toStringValue += "." + instanceId;
			if (simplePath != null) {
				toStringValue += "." + simplePath;
			}
			if (id != null) {
				toStringValue += "." + id;
			}
		}

		return toStringValue;
	}

}

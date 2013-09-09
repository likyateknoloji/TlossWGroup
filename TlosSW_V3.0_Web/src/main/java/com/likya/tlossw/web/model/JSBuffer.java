package com.likya.tlossw.web.model;

public class JSBuffer {

	private boolean isJob;
	private String jsId;
	private String jsName;
	private String newJSName;
	private String fromTree;
	private String toTree;

	public boolean isJob() {
		return isJob;
	}

	public void setJob(boolean isJob) {
		this.isJob = isJob;
	}

	public String getJsId() {
		return jsId;
	}

	public void setJsId(String jsId) {
		this.jsId = jsId;
	}

	public String getFromTree() {
		return fromTree;
	}

	public void setFromTree(String fromTree) {
		this.fromTree = fromTree;
	}

	public String getToTree() {
		return toTree;
	}

	public void setToTree(String toTree) {
		this.toTree = toTree;
	}

	public String getNewJSName() {
		return newJSName;
	}

	public void setNewJSName(String newJSName) {
		this.newJSName = newJSName;
	}

	public String getJsName() {
		return jsName;
	}

	public void setJsName(String jsName) {
		this.jsName = jsName;
	}


}

package com.likya.tlossw.web.model;

public class JSBuffer {

	private boolean isJob;
	private String jsId;
	private String jsName;
	private String newJSName;
	private String fromDocId;
	private String toDocId;
	private Integer fromScope;
    private Integer toScope; 

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

	public String getFromDocId() {
		return fromDocId;
	}

	public void setFromDocId(String fromDocId) {
		this.fromDocId = fromDocId;
	}

	public String getToDocId() {
		return toDocId;
	}

	public void setToDocId(String toDocId) {
		this.toDocId = toDocId;
	}

	public Integer getFromScope() {
		return fromScope;
	}

	public void setFromScope(Integer fromScope) {
		this.fromScope = fromScope;
	}

	public Integer getToScope() {
		return toScope;
	}

	public void setToScope(Integer toScope) {
		this.toScope = toScope;
	}


}

package com.likya.tlossw.core.dss;

public abstract class Result {

	private int resultCode;
	private String resultDescription;

	public Result(int resultCode, String resultDescription) {
		super();
		this.resultCode = resultCode;
		this.resultDescription = resultDescription;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultDescription() {
		return resultDescription;
	}

	public void setResultDescription(String resultDescription) {
		this.resultDescription = resultDescription;
	}

}

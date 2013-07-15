package com.likya.tlossw.web.definitions.helpers;

public class LogAnalyzingTabBean {

	private boolean useLogAnalyzer;
	
	private int searchType = 1;
	
	private int actionType = 1;

	public boolean isUseLogAnalyzer() {
		return useLogAnalyzer;
	}

	public void setUseLogAnalyzer(boolean useLogAnalyzer) {
		this.useLogAnalyzer = useLogAnalyzer;
	}

	public int getSearchType() {
		return searchType;
	}

	public void setSearchType(int searchType) {
		this.searchType = searchType;
	}

	public int getActionType() {
		return actionType;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
	}

}

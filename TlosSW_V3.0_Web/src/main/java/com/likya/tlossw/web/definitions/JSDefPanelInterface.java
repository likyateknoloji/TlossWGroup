package com.likya.tlossw.web.definitions;

public interface JSDefPanelInterface {

	public void init();
	
	abstract public void insertJsAction();
	abstract public void updateJsAction();
	abstract public void sendDeploymentRequest();
}

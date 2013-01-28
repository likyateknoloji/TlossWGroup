package com.likya.tlossw.model.webservice;

import java.io.Serializable;
import java.util.ArrayList;

public class WebService implements Serializable {

	private static final long serialVersionUID = 1880643013997125228L;

	private String serviceName;
	
	private ArrayList<Function> functionList;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public ArrayList<Function> getFunctionList() {
		return functionList;
	}

	public void setFunctionList(ArrayList<Function> functionList) {
		this.functionList = functionList;
	}
}

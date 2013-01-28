package com.likya.tlossw.model.webservice;

import java.io.Serializable;
import java.util.ArrayList;

public class Function implements Serializable {

	private static final long serialVersionUID = 1L;

	private String functionName;
	
	private ArrayList<Parameter> parameterList = new ArrayList<Parameter>();

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setParameterList(ArrayList<Parameter> parameterList) {
		this.parameterList = parameterList;
	}

	public ArrayList<Parameter> getParameterList() {
		return parameterList;
	}
	
}

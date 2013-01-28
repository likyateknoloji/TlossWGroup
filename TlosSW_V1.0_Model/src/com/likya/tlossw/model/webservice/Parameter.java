package com.likya.tlossw.model.webservice;

import java.io.Serializable;

public class Parameter implements Serializable {

	private static final long serialVersionUID = -7492095746086740266L;

	private String parameterName;
	
	private String parameterType;
	
	private Object value;

	private boolean isEnum = false;
	private String[] enumList;
	
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	public String getParameterType() {
		return parameterType;
	}

	public void setEnumList(String[] enumList) {
		this.enumList = enumList;
	}

	public String[] getEnumList() {
		return enumList;
	}

	public boolean getIsEnum() {
		return isEnum;
	}

	public void setIsEnum(boolean isEnum) {
		this.isEnum = isEnum;
	}


}

package com.likya.tlossw.model;

import java.io.Serializable;

public class TlosJmxReturnValue implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private TlosGuiMessage tlosGuiMessage;
	private Object returnObject;
	
	public TlosJmxReturnValue(TlosGuiMessage tlosGuiMessage, Object returnObject) {
		super();
		this.tlosGuiMessage = tlosGuiMessage;
		this.returnObject = returnObject;
	}
	
	public Object getReturnObject() {
		return returnObject;
	}
	
	public void setReturnObject(Object returnObject) {
		this.returnObject = returnObject;
	}

	public TlosGuiMessage getTlosGuiMessage() {
		return tlosGuiMessage;
	}

	public void setTlosGuiMessage(TlosGuiMessage tlosGuiMessage) {
		this.tlosGuiMessage = tlosGuiMessage;
	}
	
	
}

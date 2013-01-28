package com.likya.tlossw.model;

import java.io.Serializable;

public class TlosGuiMessage implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String message;
	private TlosGuiMessageType messageType;
	
	public TlosGuiMessage(String message, TlosGuiMessageType messageType) {
		this.message = message;
		this.messageType = messageType;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public TlosGuiMessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(TlosGuiMessageType messageType) {
		this.messageType = messageType;
	}
	
	
}

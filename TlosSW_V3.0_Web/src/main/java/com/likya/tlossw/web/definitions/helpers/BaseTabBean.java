package com.likya.tlossw.web.definitions.helpers;

import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;

import com.likya.tlossw.web.appmng.SessionMediator;
import com.likya.tlossw.web.utils.BeanUtils;


public class BaseTabBean {

	@ManagedProperty(value = "#{sessionMediator}")
	private SessionMediator sessionMediator;

	public void addMessage(String fieldName, FacesMessage.Severity severity, String errorMessage, String miscText) {
		ResourceBundle messages = sessionMediator.getMessageBundle();
		BeanUtils.addMessage(messages, fieldName, severity, errorMessage, miscText);
	}
	
	public SessionMediator getSessionMediator() {
		return sessionMediator;
	}

	public void setSessionMediator(SessionMediator sessionMediator) {
		this.sessionMediator = sessionMediator;
	}

}

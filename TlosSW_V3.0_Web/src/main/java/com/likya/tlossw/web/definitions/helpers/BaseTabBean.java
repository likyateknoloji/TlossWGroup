package com.likya.tlossw.web.definitions.helpers;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.likya.tlossw.web.appmng.SessionMediator;
import com.likya.tlossw.web.utils.BeanUtils;

public class BaseTabBean implements Serializable {

	private static final long serialVersionUID = -8738173023349050795L;

	public void addMessage(String fieldName, FacesMessage.Severity severity, String errorMessage, String miscText) {
		SessionMediator sessionMediator = (SessionMediator) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessionMediator");
		ResourceBundle messages = sessionMediator.getMessageBundle();
		BeanUtils.addMessage(messages, fieldName, severity, errorMessage, miscText);
	}
	
}

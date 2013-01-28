package com.likya.tlossw.web;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.likya.tlossw.web.appmng.SessionMediator;
import com.likya.tlossw.web.db.DBOperations;

public abstract class TlosSWBaseBean {

	private static final Logger logger = Logger.getLogger(TlosSWBaseBean.class);

	@ManagedProperty(value = "#{sessionMediator}")
	private SessionMediator sessionMediator;
	
	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;
	
	public String resolveMessage(String errorMessage) {
	
		ResourceBundle messages = sessionMediator.getMessageBundle();
		
		if (errorMessage != null) {
			try {
				errorMessage = messages.getString(errorMessage);
			}
			// eat any errors, and just use original message if there is a
			// problem
			catch (MissingResourceException e) {
				logger.error("Missing Resource bundle, could not display message");
			} catch (NullPointerException e) {
				logger.error("Missing Resource bundle, could not dipslay message");
			}
		} else {
			errorMessage = "";
		}
		
		return errorMessage;
	}
	
	public void addMessage(String formName, String fieldName, String errorMessage, String miscText) {

		errorMessage = resolveMessage(errorMessage);
		
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message;

		if (miscText != null) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", (errorMessage + " " + miscText).trim());
		} else {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", errorMessage);
		}
		// report the message.
		context.addMessage(formName + ":" + fieldName, message);
	}
	
	public void addMessage(String fieldName, FacesMessage.Severity severity, String errorMessage, String miscText) {
		errorMessage = resolveMessage(errorMessage);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, errorMessage, miscText));
	}
	
	public void addSuccessMessage(String fieldName, String errorMessage, String miscText) {
		errorMessage = resolveMessage(errorMessage);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, errorMessage, miscText));
	}
	
	public void addFailMessage(String fieldName, String errorMessage, String miscText) {
		errorMessage = resolveMessage(errorMessage);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, miscText));
	}

	public SessionMediator getSessionMediator() {
		return sessionMediator;
	}

	public void setSessionMediator(SessionMediator sessionMediator) {
		this.sessionMediator = sessionMediator;
	}

	public DBOperations getDbOperations() {
		return dbOperations;
	}

	public void setDbOperations(DBOperations dbOperations) {
		this.dbOperations = dbOperations;
	}
}

package com.likya.tlossw.web;

import java.util.HashMap;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.likya.tlossw.model.DocMetaDataHolder;
import com.likya.tlossw.model.auth.WebAppUser;
import com.likya.tlossw.web.appmng.SessionMediator;
import com.likya.tlossw.web.db.DBOperations;
import com.likya.tlossw.web.utils.BeanUtils;

public abstract class TlosSWBaseBean {

	private static final Logger logger = Logger.getLogger(TlosSWBaseBean.class);

	@ManagedProperty(value = "#{sessionMediator}")
	private SessionMediator sessionMediator;

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;

	@ManagedProperty(value = "#{sessionMediator.webAppUser}")
	private WebAppUser webAppUser;
	
	public String resolveMessage(String errorMessage) {

		ResourceBundle messages = sessionMediator.getMessageBundle();

		errorMessage = BeanUtils.resolveMessage(messages, errorMessage);
		
		return errorMessage;
	}

	protected void setPassedParameters() {

		String doc1Id = getPassedParameter().get("doc1Id");
		int firstColumn = Integer.parseInt(getPassedParameter().get("firstColumn"));
		String doc2Id = getPassedParameter().get("doc2Id");
		int secondColumn = Integer.parseInt(getPassedParameter().get("secondColumn"));
		
		if(!doc1Id.isEmpty()) {
			sessionMediator.setDocumentScope( doc1Id, firstColumn);
			sessionMediator.setCurrentDoc( DocMetaDataHolder.FIRST_COLUMN, doc1Id);
		}
		if(!doc2Id.isEmpty()) {
			sessionMediator.setDocumentScope( doc2Id, secondColumn);
			sessionMediator.setCurrentDoc( DocMetaDataHolder.SECOND_COLUMN, doc2Id);
		}

	}

	public String getDocId(Integer columnId) {
		
		//setPassedParameters();
		String docId = getSessionMediator().getWebSpaceWideRegistery().getDocMetaDataInfo().getCurrentDocs()[columnId-1];	
		
		return docId;
	}
	
	public void addMessage(String formName, String fieldName, String errorMessage, String miscText) {

		ResourceBundle messages = sessionMediator.getMessageBundle();
		
		BeanUtils.addMessage(messages, formName, fieldName, errorMessage, miscText);
		
		return; 
	}

	public HashMap<String, String> getPassedParameter() {
		
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		parameterMap.putAll(facesContext.getExternalContext().getRequestParameterMap());

		return parameterMap;
	}

	public void addMessage(String fieldName, FacesMessage.Severity severity, String errorMessage, String miscText) {
		ResourceBundle messages = sessionMediator.getMessageBundle();
		BeanUtils.addMessage(messages, fieldName, severity, errorMessage, miscText);
	}

	public void addSuccessMessage(String fieldName, String errorMessage, String miscText) {
		ResourceBundle messages = sessionMediator.getMessageBundle();
		BeanUtils.addSuccessMessage(messages, fieldName, errorMessage, miscText);
	}

	public void addFailMessage(String fieldName, String errorMessage, String miscText) {
		ResourceBundle messages = sessionMediator.getMessageBundle();
		BeanUtils.addFailMessage(messages, fieldName, errorMessage, miscText);
	}

	public final SessionMediator getSessionMediator() {
		return sessionMediator;
	}

	public final void setSessionMediator(SessionMediator sessionMediator) {
		this.sessionMediator = sessionMediator;
	}

	public DBOperations getDbOperations() {
		return dbOperations;
	}

	public void setDbOperations(DBOperations dbOperations) {
		this.dbOperations = dbOperations;
	}

	public int getDocumentScope(String documentId) {
		return getSessionMediator().getDocumentScope(documentId);
	}

	public WebAppUser getWebAppUser() {
		return webAppUser;
	}

	public void setWebAppUser(WebAppUser webAppUser) {
		this.webAppUser = webAppUser;
	}


	public static Logger getLogger() {
		return logger;
	}

}

package com.likya.tlossw.web.appmng;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.xmldb.api.base.XMLDBException;

import com.likya.tlossw.model.MetaDataType;
import com.likya.tlossw.model.WebSpaceWideRegistery;
import com.likya.tlossw.model.auth.Resource;
import com.likya.tlossw.model.auth.ResourceMapper;
import com.likya.tlossw.model.auth.WebAppUser;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.web.db.DBOperations;
import com.likya.tlossw.web.model.JSBuffer;
import com.likya.tlossw.web.userpreferences.UserPreferencesBean;
import com.likyateknoloji.xmlMetaDataTypes.DocumentDocument.Document;
import com.likyateknoloji.xmlMetaDataTypes.MetaDataDocument.MetaData;

@ManagedBean(name = "sessionMediator")
@SessionScoped
public class SessionMediator implements Serializable {

	private static final long serialVersionUID = -6537744626412275191L;

	private WebAppUser webAppUser;

	private ResourceMapper resourceMapper;

	private WebSpaceWideRegistery webSpaceWideRegistery;
	private static ResourceBundle messageBundle = null;

	@ManagedProperty(value = "#{localeBean}")
	private LocaleBean localeBean;

	@ManagedProperty(value = "#{userPreferencesBean}")
	private UserPreferencesBean userPreferencesBean;

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;

	private JSBuffer jsBuffer;

	private String jobStateIconCssPath;

	private String jobStateColorCssPath;

	private String jobIconCssPath;

	private void initMetaData() {

		String documentId, documentType;

		MetaData metaData = null;

		try {
			metaData = getDbOperations().readMetaData();
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		Document[] document = metaData.getDocumentInfo().getDocumentArray();

		HashMap<String, Integer> documentScopes = webSpaceWideRegistery.getDocMetaDataInfo().getDocumentScopes();

		for (int i = 0; i < document.length; i++) {

			documentId = document[i].getId().toString();
			documentType = document[i].getType().toString();

			if (documentId.equalsIgnoreCase(documentType)) {
				documentScopes.put(document[i].getId().toString(), MetaDataType.GLOBAL);
			}

		}

		System.out.println("Sonuc");
	}

	public ResourceBundle getMessageBundle() {
		initMessageBundle();
		return messageBundle;
	}

	private void initMessageBundle() {
		if (messageBundle == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			if (locale == null) {
				locale = new Locale("tr", "TR");
			}
			// messageBundle =
			// ResourceBundle.getBundle("com.likya.tlossw.web.resources.messages_"
			// + locale , new UTF8Control());
			messageBundle = UTF8ResourceBundle.getBundle("com.likya.tlossw.web.resources.messages", new UTF8ResourceBundle.UTF8Control());
		}
	}

	public boolean authorizeResource(String resourceId) {

		Resource myResource = (Resource) resourceMapper.get(resourceId);
		if (myResource == null) {
			return false;
		}
		return true;
	}

	protected void setMessageBundle() {
		messageBundle = UTF8ResourceBundle.getBundle("com.likya.tlossw.web.resources.messages", localeBean.getCurrentLocale());
	}

	public ResourceMapper getResourceMapper() {
		return resourceMapper;
	}

	public void setResourceMapper(ResourceMapper resourceMapper) {
		this.resourceMapper = resourceMapper;
	}

	public WebSpaceWideRegistery getWebSpaceWideRegistery() {
		return webSpaceWideRegistery;
	}

	public void setWebSpaceWideRegistery(WebSpaceWideRegistery webSpaceWideRegistery) {
		this.webSpaceWideRegistery = webSpaceWideRegistery;
	}

	public LocaleBean getLocaleBean() {
		return localeBean;
	}

	public void setLocaleBean(LocaleBean localeBean) {
		this.localeBean = localeBean;
	}

	public WebAppUser getWebAppUser() {
		return webAppUser;
	}

	public void setWebAppUser(WebAppUser webAppUser) {
		this.webAppUser = webAppUser;
	}

	public String getJobStateIconCssPath() {
		return jobStateIconCssPath;
	}

	public void setJobStateIconCssPath() {
		this.jobStateIconCssPath = userPreferencesBean.getJobStateIconCssPath();
	}

	public String getJobStateColorCssPath() {
		return jobStateColorCssPath;
	}

	public void setJobStateColorCssPath() {
		this.jobStateColorCssPath = userPreferencesBean.getJobStateColorCssPath();
	}

	public String getJobIconCssPath() {
		return jobIconCssPath;
	}

	public void setJobIconCssPath() {
		this.jobIconCssPath = userPreferencesBean.getJobIconCssPath();
	}

	public UserPreferencesBean getUserPreferencesBean() {
		return userPreferencesBean;
	}

	public void setUserPreferencesBean(UserPreferencesBean userPreferencesBean) {
		this.userPreferencesBean = userPreferencesBean;
	}

	public JSBuffer getJsBuffer() {
		return jsBuffer;
	}

	public void setJsBuffer(JSBuffer jsBuffer) {
		this.jsBuffer = jsBuffer;
	}

	public DBOperations getDbOperations() {
		return dbOperations;
	}

	public void setDbOperations(DBOperations dbOperations) {
		this.dbOperations = dbOperations;
	}

	public int getDocumentScope(String documentId) {

		HashMap<String, Integer> documentScopes = webSpaceWideRegistery.getDocMetaDataInfo().getDocumentScopes();

		if (documentScopes.isEmpty()) {
			initMetaData();
		}

		Integer returnValue = documentScopes.get(documentId);

		if (returnValue == null) {
			returnValue = MetaDataType.GLOBAL;
		}

		//System.out.println("Sonuç = " + documentId + "," + returnValue);

		return returnValue;
	}

	public int getDocumentScopeByIndex(Integer index) {

		HashMap<String, Integer> documentScopes = webSpaceWideRegistery.getDocMetaDataInfo().getDocumentScopes();

		if (documentScopes.isEmpty()) {
			initMetaData();
		}

		Integer returnValue = documentScopes.get(index-1);

		if (returnValue == null) {
			returnValue = MetaDataType.GLOBAL;
		}

		System.out.println("Sonuç = " + index + "," + returnValue);

		return returnValue;
	}
	
	public void setDocumentScope(String documentId, Integer scope) {
		webSpaceWideRegistery.getDocMetaDataInfo().getDocumentScopes().put(documentId, scope);
	}

	public String getScopeText(Integer scopeId) {

		String result = scopeId == 1 ? CommonConstantDefinitions.EXIST_GLOBALDATA : CommonConstantDefinitions.EXIST_MYDATA;

		return result;
	}
	
	public Integer getReverseScope(Integer scopeId) {
		return scopeId == 1 ? 2 : 1;
	}

	public Integer getScope(Integer column) {
		return getDocumentScope(webSpaceWideRegistery.getDocMetaDataInfo().getCurrentDocs()[column - 1]);
	}

	public String getCurrentDoc(int column) {
		
		String head = webSpaceWideRegistery.getDocMetaDataInfo().getCurrentDocs()[column - 1];
		String result = head == null ? "Quick Links" : head;
		return result;
	}
}

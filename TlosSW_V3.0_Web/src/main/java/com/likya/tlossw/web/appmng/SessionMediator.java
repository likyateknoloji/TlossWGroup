package com.likya.tlossw.web.appmng;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.likya.tlossw.model.WebSpaceWideRegistery;
import com.likya.tlossw.model.auth.Resource;
import com.likya.tlossw.model.auth.ResourceMapper;

@ManagedBean(name = "sessionMediator")
@SessionScoped
public class SessionMediator implements Serializable {

	private static final long serialVersionUID = -6537744626412275191L;


	private ResourceMapper resourceMapper;
	
	private WebSpaceWideRegistery webSpaceWideRegistery;
	private static ResourceBundle messageBundle = null;
	
	@ManagedProperty(value = "#{localeBean}")
	private LocaleBean localeBean;
	
	public ResourceBundle getMessageBundle() {
		initMessageBundle();
		return messageBundle;
	}

	private void initMessageBundle() {
		if (messageBundle == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			if (locale == null) {
				locale =  new Locale("tr","TR");
			}
			// messageBundle = ResourceBundle.getBundle("com.likya.tlossw.web.resources.messages_" + locale , new UTF8Control());
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

}

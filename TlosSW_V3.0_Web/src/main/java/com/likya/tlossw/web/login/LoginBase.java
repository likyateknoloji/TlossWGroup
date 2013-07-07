package com.likya.tlossw.web.login;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.likya.tlossw.web.TlosSWBaseBean;

public class LoginBase extends TlosSWBaseBean {
	
	public static final String SESSION_KEY = "LoggedIn";
	
	public static final String LOGOUT_SUCCESS = "/login.jsf?faces-redirect=true";
	public static final String LOGIN_SUCCESS = "/inc/index.jsf?faces-redirect=true";
	public static final String LOGIN_FAILURE = "/login.jsf?faces-redirect=true";
	public static final String LOGIN_ENGINE_DIRECTOR = "/inc/index.jsf?faces-redirect=true";
	public static final String LOGIN_FORWARD = "inc/index.jsf?faces-redirect=true";
	
	protected void setSessionLoginParam(boolean isLoggedIn) {
		
		HttpSession httpSession = null;
		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (facesContext != null) {
			httpSession = (HttpSession) facesContext.getExternalContext().getSession(false);
		}

		if (httpSession != null) {
			httpSession.setAttribute(SESSION_KEY, Boolean.toString(isLoggedIn));
		}
	}
	
	protected boolean getSessionLoginParam() {
		
		boolean retValue = false;
		
		HttpSession httpSession = null;
		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (facesContext != null) {
			httpSession = (HttpSession) facesContext.getExternalContext().getSession(false);
		}

		if (httpSession != null && httpSession.getAttribute(SESSION_KEY) != null) {
			retValue = Boolean.parseBoolean(httpSession.getAttribute(SESSION_KEY).toString());
		}
		
		return retValue;
	}
	
	public void redirect(String url) {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

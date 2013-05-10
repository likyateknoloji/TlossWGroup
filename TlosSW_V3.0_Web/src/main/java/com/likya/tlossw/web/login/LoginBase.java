package com.likya.tlossw.web.login;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.likya.tlossw.web.TlosSWBaseBean;

public class LoginBase extends TlosSWBaseBean {
	
	public static final String SESSION_KEY = "LoggedIn";
	
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
	
	public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.jsf?faces-redirect=true";
    }
	
}

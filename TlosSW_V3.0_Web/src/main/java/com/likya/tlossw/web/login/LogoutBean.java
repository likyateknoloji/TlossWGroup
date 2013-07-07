package com.likya.tlossw.web.login;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

@ManagedBean(name = LogoutBean.BEAN_NAME)
@ViewScoped
public class LogoutBean extends LoginBase implements Serializable {

	public static final String BEAN_NAME = "logoutBean";
	private static final long serialVersionUID = -5124116113489857945L;

	private static final Logger logger = Logger.getLogger(LogoutBean.class);

	
	public String logout() {
		
		logger.info("logout is called !");
    
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        
        return LOGOUT_SUCCESS;
    }

}

package com.likya.tlossw.web.common;

import java.io.Serializable;
import java.util.HashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.likya.tlossw.web.appmng.SessionMediator;

@ManagedBean(name = "security")
@SessionScoped
public class Security extends HashMap<Object, Object> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(Security.class);

	@ManagedProperty(value = "#{sessionMediator}")
	private SessionMediator sessionMediator;
	
	public Object get(Object key) {

		// log.info("Checking permission for Resource " + key);
		if (sessionMediator.authorizeResource((String) key)) {
			// log.info("Permission allowed for Resource " + key);
			return Boolean.TRUE;
		}

		log.fatal("Permission denied for Resource " + key);
		return Boolean.FALSE;

	}

	public SessionMediator getSessionMediator() {
		return sessionMediator;
	}

	public void setSessionMediator(SessionMediator sessionMediator) {
		this.sessionMediator = sessionMediator;
	}

}

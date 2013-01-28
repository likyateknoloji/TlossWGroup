/*
 * TlosFaz_V2.0
 * com.likya.tlos.jmx.mp.model : JmxUser.java
 * @author Serkan Tas
 * Tarih : May 5, 2009 10:42:44 AM
 */

package com.likya.tlossw.model.jmx;

import java.io.Serializable;

import com.likya.tlossw.model.auth.AppUser;


public class JmxAppUser extends JmxUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private AppUser appUser;

	public JmxAppUser() {
		super();
	}
	
	public JmxAppUser(AppUser appUser) {
		super();
		this.appUser = appUser;
	}

	public AppUser getAppUser() {
		return appUser;
	}

	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}

}

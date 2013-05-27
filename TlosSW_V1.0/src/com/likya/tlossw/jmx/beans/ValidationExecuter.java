/*
 * TlosSW_V1.0
 * com.likya.tlos.jmx.mp.helper : ValidationExecuter.java
 * @author Merve Özbey
 * Tarih : May 27, 2013 
 */

package com.likya.tlossw.jmx.beans;

import com.likya.tlossw.model.FTPAccessInfoTypeClient;
import com.likya.tlossw.model.jmx.JmxUser;


public class ValidationExecuter implements ValidationExecuterMBean {

	@Override
	public String getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getNbChanges() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown(String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void redeploy(String str) {
		// TODO Auto-generated method stub
		
	}

	public String checkFTPAccess(JmxUser jmxUser, FTPAccessInfoTypeClient ftpProperties) {
		// TODO Auto-generated method stub
		return "******************************merve";
	}
	
	

}

/*
 * TlosFaz_V2.0
 * com.likya.tlos.jmx.mp.helper : ProcessInfoProvider.java
 * @author Serkan Taþ
 * Tarih : Apr 6, 2009 2:19:17 PM
 */

package com.likya.tlossw.jmx.beans;

import com.likya.tlossw.jmx.JMXTLSServer;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.utils.FileUtils;

public class RemoteFileOperator implements RemoteFileOperatorMBean {


	@Override
	public boolean checkFile(JmxUser jmxUser, String fileName) throws Exception {
		if(!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}
		
		return FileUtils.checkFile(fileName);
	}

	@Override
	public StringBuffer readFile(JmxUser jmxUser, String fileName) throws Exception {
		if(!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}
		
		return FileUtils.readFile(fileName);
	}

	@Override
	public StringBuffer readFile(JmxUser jmxUser, String fileName, String coloredLineIndicator, boolean useSections, boolean isXML) throws Exception {
		if(!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}
		
		return FileUtils.readFile(fileName, coloredLineIndicator, useSections, isXML);
	}
	
	@Override
	public int getNbChanges() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setState(String s) {
		// TODO Auto-generated method stub

	}
}

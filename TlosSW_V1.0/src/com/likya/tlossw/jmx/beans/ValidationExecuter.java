/*
 * TlosSW_V1.0
 * com.likya.tlos.jmx.mp.helper : ValidationExecuter.java
 * @author Merve Özbey
 * Tarih : May 27, 2013 
 */

package com.likya.tlossw.jmx.beans;

import org.apache.commons.net.ftp.FTPClient;

import com.likya.tlossw.jmx.JMXTLSServer;
import com.likya.tlossw.model.FTPAccessInfoTypeClient;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.utils.CommonConstantDefinitions;

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

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		FTPClient ftpClient = new FTPClient();

		try {
			if (ftpProperties.getPort() == 0) {
				ftpClient.connect(ftpProperties.getIpAddress());
			} else {
				ftpClient.connect(ftpProperties.getIpAddress(), ftpProperties.getPort());
			}
		} catch (Exception ex) {
			return CommonConstantDefinitions.FTP_CONNECTION_ERROR;
		}

		boolean login = false;

		try {
			login = ftpClient.login(ftpProperties.getUserName(), ftpProperties.getPassword());
		} catch (Exception ex) {
			return CommonConstantDefinitions.FTP_LOGIN_ERROR;
		}

		if (login) {
			return CommonConstantDefinitions.FTP_SUCCESSFUL;
		} else {
			return CommonConstantDefinitions.FTP_LOGIN_ERROR;
		}
	}

}

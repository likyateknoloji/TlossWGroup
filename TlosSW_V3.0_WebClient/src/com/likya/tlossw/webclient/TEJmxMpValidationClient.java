package com.likya.tlossw.webclient;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import com.likya.tlossw.model.FTPAccessInfoTypeClient;
import com.likya.tlossw.model.jmx.JmxUser;

public class TEJmxMpValidationClient extends TEJmxMpClientBase {

	private TEJmxMpValidationClient() {

	}

	public static String checkFTPAccess(JmxUser jmxUser, FTPAccessInfoTypeClient ftpProperties) {

		JMXConnector jmxConnector = TEJmxMpValidationClient.getJMXConnection();

		Object[] paramList = { jmxUser, ftpProperties };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "com.likya.tlossw.model.FTPAccessInfoTypeClient" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=7"), "checkFTPAccess", paramList, signature);
			TEJmxMpValidationClient.disconnect(jmxConnector);
			return (String) o;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}

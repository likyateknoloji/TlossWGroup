package com.likya.tlossw.webclient;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import com.likya.tlossw.model.jmx.JmxUser;

public class TEJmxMpWorkSpaceClient extends TEJmxMpClientBase {

	private TEJmxMpWorkSpaceClient() {
		
	}

	public static void addTestData(JmxUser jmxUser, String tlosProcessDataText) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();
		
		Object[] paramList = { jmxUser, tlosProcessDataText };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String"};
		
		try {
			
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + WSO), "addTestData", paramList, signature);
			
			TEJmxMpClient.disconnect(jmxConnector);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return;
	}
}

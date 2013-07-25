package com.likya.tlossw.agentclient;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import org.apache.log4j.Logger;

import com.likya.tlossw.model.jmx.JmxAgentUser;

public class TSWAgentJmxClient extends TSWAgentJmxClientBase {
	
	private static Logger logger = Logger.getLogger(TSWAgentJmxClient.class);
	
	public static boolean sendJob(String host, Integer port, String rxMessage, JmxAgentUser jmxAgentUser) {

		JMXConnector jmxConnector = TSWAgentJmxClient.getJMXTLSConnection(host, port);
		
		if (jmxConnector == null) {
			logger.info("TSWAgentJmxClient:JMXTLSConnection kurulamadi !!");
			return false;
		}
		
		Object[] paramList = { rxMessage, jmxAgentUser };
		String[] signature = { "java.lang.String", "com.likya.tlossw.model.jmx.JmxAgentUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=0"), "jobHandle", paramList, signature);
			TSWAgentJmxClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return ((Boolean) o).booleanValue();
	}
	
	public static Object runningJobs(String host, Integer port, JmxAgentUser jmxAgentUser) {

		JMXConnector jmxConnector = TSWAgentJmxClient.getJMXTLSConnection(host, port);

		if (jmxConnector == null) {
			logger.info("TSWAgentJmxClient:JMXTLSConnection kurulamadi !!");
			return false;
		}
		
		Object[] paramList = { jmxAgentUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxAgentUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=1"), "runningJobs", paramList, signature);
			TSWAgentJmxClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return o;
	}
	
	//resetAgent ascnyron oldugu icin bir sey donmeyecek sadece Mbean'de bir hata cikarsa bildirsin! 
	public static boolean resetAgent(String host, Integer port, JmxAgentUser jmxAgentUser) {

		JMXConnector jmxConnector = TSWAgentJmxClient.getJMXTLSConnection(host, port);

		if (jmxConnector == null) {
			logger.info("TSWAgentJmxClient:JMXTLSConnection kurulamadi !!");
			return false;
		}
		
		Object[] paramList = { jmxAgentUser };
		String[] signature = {  "com.likya.tlossw.model.jmx.JmxAgentUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=0"), "resetAgent", paramList, signature);
			TSWAgentJmxClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return ((Boolean) o).booleanValue();
	}
	
	
}

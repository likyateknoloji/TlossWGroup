package com.likya.tlosswagent.serverclient;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import org.apache.log4j.Logger;

import com.likya.tlossw.model.jmx.JmxAgentUser;
import com.likya.tlosswagent.TlosSWAgentBase;

public class TSWServerJmxClient extends TSWServerJmxClientBase {

	private static boolean logHBDot = false;

	private static Logger logger = Logger.getLogger(TSWServerJmxClient.class);

	public static String retrieveGlobalStates(JmxAgentUser jmxAgentUser, String host, Integer port) {

		JMXConnector jmxConnector = TSWServerJmxClient.getJMXTLSConnectionForComm(host, port);

		if (jmxConnector == null) {
			logger.info("getJMXTLSConnection is null !");
			logger.error("  > Server'a ba�lant� sa�lanamad� !!!!");
			TlosSWAgentBase.errprintln("  > Server'a ba�lant� sa�lanamad� [host:" + host + ", " + "port:" + port + "], agent sonland�r�l�yor !!!!");
			System.exit(-1);
		}

		Object[] paramList = { jmxAgentUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxAgentUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + AO), "retrieveGlobalStates", paramList, signature);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return o.toString();
	}

	public static int checkJmxUser(JmxAgentUser jmxAgentUser, String host, Integer port) {

		int checkJmx = -1;

		logger.info("HOST : " + host);
		logger.info("PORT : " + port);
		JMXConnector jmxConnector = TSWServerJmxClient.getJMXTLSConnectionForComm(host, port);

		if (jmxConnector == null) {
			logger.info("JMXTLSConnection kurulamadi !!");
			return -1;
		}

		Object[] paramList = { jmxAgentUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxAgentUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + AO), "checkJmxUser", paramList, signature);
			checkJmx = (Integer) o;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return checkJmx;
	}

	public static boolean txMessageHandle(JmxAgentUser jmxAgentUser, String host, Integer port, String txMessage) {

		JMXConnector jmxConnector = TSWServerJmxClient.getJMXTLSConnectionForComm(host, port);

		if (jmxConnector == null) {
			logger.info("txMessageHandle : getJMXTLSConnection is null !");
			return false;
		}

		Object[] paramList = { jmxAgentUser, txMessage };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxAgentUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + AO), "txMessageHandle", paramList, signature);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return ((Boolean) o).booleanValue();
	}

	public static void pulse(JmxAgentUser jmxAgentUser, String host, Integer port) {

		JMXConnector jmxConnector = TSWServerJmxClient.getJMXTLSConnectionForHeartBeat(host, port);

		if (jmxConnector == null) {
			if (logHBDot) {
				System.out.print(".");
			} else {
				logHBDot = true;
				logger.error("getJMXTLSConnectionForHeartBeat is null !");
			}
			return;
		} else {
			logHBDot = false;
		}

		Object[] paramList = { jmxAgentUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxAgentUser" };
		@SuppressWarnings("unused")
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + AO), "pulse", paramList, signature);
		} catch (IOException ioe) {
			System.out.println(ioe.getLocalizedMessage());
			try {
				if ("The client has been closed.".equals(ioe.getLocalizedMessage())) {
					clearHBJmx();
				} else {
					Thread.sleep(5000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object runningJobs(String host, Integer port) {

		JMXConnector jmxConnector = TSWServerJmxClient.getJMXTLSConnectionForComm(host, port);

		if (jmxConnector == null) {
			logger.info("getJMXConnection is null !");
			return null;
		}

		Object[] paramList = {};
		String[] signature = {};
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "runningJobs", paramList, signature);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return o;
	}

}

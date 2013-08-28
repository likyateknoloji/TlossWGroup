package com.likya.tlossw.webclient;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.NamingException;
import javax.print.DocFlavor.URL;

import org.apache.log4j.Logger;

public class TEJmxMpClientBase {

	private static final Logger logger = Logger.getLogger(TEJmxMpClientBase.class);

	public static boolean tryReconnect = true;

	public static boolean isEnvRead = false;
	public static String jmxIpEnv = null;
	public static Integer jmxPortEnv = null;
	
	/*
	String MBeanArray[] = { "LocalManager", "LiveJSTreeInfoProvider", "ProcessInfoProvider", "ProcessManagementInterface", "RemoteFileOperator", "RemoteDBOperator", "AgentOperator", "WebServiceOperator", "ValidationExecuter", "WorkSpaceOperator" };
	String MBeanTypeArray[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	*/
	
	protected static final int LJSTIP = 1;
	protected static final int PIP = 2; 
	protected static final int PMI = 3; 
	protected static final int RFO = 4;
	protected static final int RDBO = 5;
	
	protected static final int WEBSO = 7;
	protected static final int VE = 8;
	protected static final int WSO = 9;
	

	public static JMXConnector getJMXConnection() {

		if (!isEnvRead) {
			javax.naming.Context ctx;
			try {
				ctx = new javax.naming.InitialContext();
				jmxIpEnv = (String) ctx.lookup("java:comp/env/jmxIp");
				// jmxPortEnv = (Integer) ctx.lookup("java:comp/env/jmxPort");
				jmxPortEnv = (Integer) ctx.lookup("java:comp/env/jmxTlsPort"); // jmxtls icin
				isEnvRead = true;
			} catch (NamingException e1) {
				e1.printStackTrace();
				logger.info(">> Initialization Problem for JMX Ip and Port !! ");
			}
		}

		JMXConnector jmxConnector = null;
		String host = jmxIpEnv;
		int port = jmxPortEnv.intValue(); // jmx için 5554; //jmxtls icin 5555

		try {

			logger.debug("Creating JMXMP connector client on " + host + " at  " + port + " and connecting JMXMP server...");
			JMXServiceURL url = new JMXServiceURL("jmxmp", host, port);

			int attemptCount = 0;

			while (tryReconnect) {

				try {

					jmxConnector = JMXConnectorFactory.connect(url, getEnv());
					// jmxConnector = JMXConnectorFactory.connect(url);
					jmxConnector.addConnectionNotificationListener(new JmxConnectionListener(), null, jmxConnector);
					logger.info(">> JMXMP Connection successfully established to " + url);

					break;

				} catch (UnknownHostException uhe) {
					System.err.println("UnknownHostException ! url : " + url + " message : " + uhe.getLocalizedMessage());
				} catch (ConnectException ce) {
					System.err.println("ConnectException to ! url : "  + url + " message : " +  ce.getLocalizedMessage());
				} catch (SocketException se) {
					System.err.println("SocketException with ! url : "  + url + " message : " +  se.getLocalizedMessage());
				} catch (Throwable t) {
					t.printStackTrace();
				}

				if (attemptCount < 3) {
					logger.info(">> JMXMP Connection can NOT be established ! Waiting for 2 seconds before retry...");
					Thread.sleep(2000);
					logger.info(">> Trying to reconnect. Attempt count " + ++attemptCount);
				} else {
					return null;
				}
			}

			logger.debug("Connected to JMXMP server on host " + host + " through port " + port);

		} catch (Exception e) {
			System.out.println("HeartBeat icin baglanti kurulamadi !! >> " + e.getLocalizedMessage());

			System.out.println("> JMXMP connection has not been created with host: " + host + " and port: " + port + " !");
			logger.debug(">>JMXMP connection has not been created with host: " + host + " and port: " + port + " !");
			logger.debug("   > " + URL.hostEncoding);

		}

		return jmxConnector;

	}

	public static long dateDiffWithNow(long sDate) {

		Date now = Calendar.getInstance().getTime();
		long timeDiff = now.getTime() - sDate;

		return timeDiff;
	}

	protected static void disconnect(JMXConnector jmxConnector) {
//		try {
//			// Close MBeanServer connection
//			//
//			long startTime = System.currentTimeMillis();
//			Logger.getLogger(TEJmxMpClientBase.class).debug("Close the connection to the server...");
//			System.err.println(" TEJmxMpDBClient.disconnect :1 " + dateDiffWithNow(startTime) + "ms");
//			jmxConnector.close();
//			System.err.println(" TEJmxMpDBClient.disconnect :2 " + dateDiffWithNow(startTime) + "ms");
//			//selfInstance = null;
//			Logger.getLogger(TEJmxMpClientBase.class).debug("Closed !");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	protected static void disconnectJmx(JMXConnector jmxConnector) {
		try {
			// Close MBeanServer connection
			//
			// long startTime = System.currentTimeMillis();
			Logger.getLogger(TEJmxMpClientBase.class).debug("Close the connection to the server...");
			// System.err.println(" TEJmxMpDBClient.disconnect :1 " +
			// dateDiffWithNow(startTime) + "ms");
			jmxConnector.close();
			// System.err.println(" TEJmxMpDBClient.disconnect :2 " +
			// dateDiffWithNow(startTime) + "ms");
			// selfInstance = null;
			Logger.getLogger(TEJmxMpClientBase.class).debug("Closed !");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static Map<String, String> getEnv() {
		Map<String, String> env = new HashMap<String, String>();

		if (System.getProperty("javax.net.ssl.trustStore") == null) {
			//System.setProperty("javax.net.ssl.trustStore", "/Users/serkan/programlar/dev/workspace/TlosSW_V3.0_Web/likyaKeystore");
			logger.error("javax.net.ssl.trustStore is null, use -Djavax.net.ssl.trustStore as VM argument !");
		}
		
		if (System.getProperty("javax.net.ssl.keyStorePassword") == null) {
			// System.setProperty("javax.net.ssl.keyStorePassword", "likya1!+");
			logger.error("javax.net.ssl.trustStore is null, use -Djavax.net.ssl.trustStore as VM argument !");
		}
		
		if (System.getProperty("javax.net.ssl.keyStore") == null) {
			//System.setProperty("javax.net.ssl.keyStore", "/Users/serkan/programlar/dev/workspace/TlosSW_V3.0_Web/likyaKeystore");
			logger.error("javax.net.ssl.trustStore is null, use -Djavax.net.ssl.trustStore as VM argument !");
		}
		
		env.put("jmx.remote.profiles", "TLS");

		return env;

	}
}

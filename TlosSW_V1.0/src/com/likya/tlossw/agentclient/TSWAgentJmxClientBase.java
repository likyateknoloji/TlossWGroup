/*
 * Tlos_V2.0MC_JmxMp_Gxt
 * com.likya.tlos.omc : TEJmxMpClientBase.java
 * @author Serkan Taþ
 * Tarih : Apr 13, 2009 10:10:44 AM
 */

package com.likya.tlossw.agentclient;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

public class TSWAgentJmxClientBase {

	private static Logger logger = Logger.getLogger(TSWAgentJmxClientBase.class);
	
	protected static JMXConnector getJMXTLSConnection(String host, Integer port) {
		JMXConnector jmxConnector = null; 

		try {
			// Create a JMXMP connector client and
			// connect it to the JMXMP connector server
			logger.debug("Create a JMXMP connector client and connect it to the JMXMP connector server");
			JMXServiceURL url = new JMXServiceURL("jmxmp", host, port);

			int attemptCount = 0;
			
			while(true) {
				try {
					jmxConnector = JMXConnectorFactory.connect(url, getEnv());
					
					logger.info(">> JMXMP Connection successfully established to " + url);
					break;
				} catch (ConnectException ce) {
					// System.err.println(ce.getLocalizedMessage());
				}
				
				logger.info(">> JMXMP Connection can NOT be established ! Waiting for 5 second before retry...");
				Thread.sleep(5000);
				logger.info(">> Trying to reconnect. Attempt count " + ++ attemptCount);
			}
			
			// Get an MBeanServerConnection
			logger.debug("Get an MBeanServerConnection");

		} catch (Exception e) {
			System.out.println("JMXMP TLS connection has not been created with host: " + host + " and port: " + port + " !");
			logger.debug("JMXMP TLS connection has not been created with host: " + host + " and port: " + port + " !");
			e.printStackTrace();
		}

		return jmxConnector;
	}
	
	
	protected static void disconnect(JMXConnector jmxConnector) {
		try {
			// Close MBeanServer connection
			//
			logger.debug("Close the connection to the server...");
			jmxConnector.close();
			//selfInstance = null;
			logger.debug("Closed !");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected static Map<String, String> getEnv() {
		Map<String, String> env = new HashMap<String, String>();
		// TLS Mode setup.

		// Note that the settings below are conditional, so you can
		// override then with "java -Djavax...=Y... ConnectorClient..."
		// It's definitely not safe to use -D to set passwords, though,
		// but it's useful for prototyping.
//		if (System.getProperty("javax.net.ssl.trustStore") == null)
//			System.setProperty("javax.net.ssl.trustStore",
//					"server-cert.store");
//		if (System.getProperty("javax.net.ssl.keyStorePassword") == null)
//			System.setProperty("javax.net.ssl.keyStorePassword",
//					"likya1!+");
//		// Comment out the lines above if the server isn't requiring
//		// a cert for your client.

//		if (System.getProperty("javax.net.ssl.trustStore") == null)
//			System.setProperty("javax.net.ssl.trustStore",
//					"mySrvKeystore");
//		if (System.getProperty("javax.net.ssl.keyStorePassword") == null)
//			System.setProperty("javax.net.ssl.keyStorePassword", "123456");
//		if (System.getProperty("javax.net.ssl.keyStore") == null)
//			System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");
		
		if (System.getProperty("javax.net.ssl.trustStore") == null)
			System.setProperty("javax.net.ssl.trustStore",
					"likyaKeystore");
		if (System.getProperty("javax.net.ssl.keyStorePassword") == null)
			System.setProperty("javax.net.ssl.keyStorePassword", "likya1!+");
		if (System.getProperty("javax.net.ssl.keyStore") == null)
			System.setProperty("javax.net.ssl.keyStore", "likyaKeystore");
		
		
		/*
		 * The method above is the simplest (IMO therefore the best) method
		 * if your application doesn't use certs for any other purpose. You
		 * should use the instance-based TLS configuration method if your
		 * app uses certs for any other purpose in a single instantiation
		 * (i.e., it could fetch web pages over https, or be a TLS Soap
		 * client, etc., or it could run connect to multiple TLS
		 * JMXConnectorServers).
		 * 
		 * The instance-based method allocates a SSLSocketFactory based on
		 * the SSLContext instance which you instantiate and configure, so
		 * you can configure multiple SSLSocketFactories with different
		 * SSLContext instances. This all applies to any standard JSSE TLS
		 * application, but for JMX, you associate the allocated
		 * SSLSocketFactory to the Connector with:
		 * 
		 * env.put("jmx.remote.tls.socket.factory", yourFactory);
		 */

		env.put("jmx.remote.profiles", "TLS");
		// env.put("jmx.remote.tls.enabled.protocols", "TLSv1");
		// env.put("jmx.remote.tls.enabled.cipher.suites",
		// "SSL_RSA_WITH_NULL_MD5");
		// Most users will probably want to use the default TLS
		// protocols and suites.
		
		return env;

	}

}

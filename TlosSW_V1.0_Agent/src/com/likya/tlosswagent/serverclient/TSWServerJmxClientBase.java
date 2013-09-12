/*
 * Tlos_V2.0MC_JmxMp_Gxt
 * com.likya.tlos.omc : TEJmxMpClientBase.java
 * @author Serkan Ta�
 * Tarih : Apr 13, 2009 10:10:44 AM
 */

package com.likya.tlosswagent.serverclient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.print.DocFlavor.URL;

import org.apache.log4j.Logger;

public class TSWServerJmxClientBase {

	private static Logger logger = Logger.getLogger(TSWServerJmxClientBase.class);

	private static JMXConnector jmxConnectorForComm = null;
	private static JMXConnector jmxConnectorForHeartBeat = null;
	
	public static boolean tryReconnect = true;

	protected static final int PIP = 2;
	protected static final int AO = 6; 

	protected static JMXConnector getJMXTLSConnectionForComm(String host, Integer port) {

		if (jmxConnectorForComm == null) {
			jmxConnectorForComm = getJmxTlsConnection(host, port);
			try {
				System.err.println("Created jmx connection for comm with id :" + (jmxConnectorForComm == null ? "-1" : jmxConnectorForComm.getConnectionId()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return jmxConnectorForComm;
	}

	protected static JMXConnector getJMXTLSConnectionForHeartBeat(String host, Integer port) {

		if (jmxConnectorForHeartBeat == null) {
			jmxConnectorForHeartBeat = getJmxTlsConnection(host, port);
			try {
				System.err.println("Created jmx connection for HeatBeat with id :" + (jmxConnectorForHeartBeat == null ? "-1" : jmxConnectorForHeartBeat.getConnectionId()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return jmxConnectorForHeartBeat;
	}

	public static void releaseJMXTLSConnectionForComm() {

		if (jmxConnectorForComm != null) {
			try {
				jmxConnectorForComm.close();
			} catch (IOException e) {
				System.err.println(e.getLocalizedMessage());
			}
		}

	}

	public static void releaseJMXTLSConnectionForHeartBeat() {

		if (jmxConnectorForHeartBeat != null) {
			try {
				jmxConnectorForHeartBeat.close();
			} catch (IOException e) {
				System.err.println(e.getLocalizedMessage());
			}
		}
	}

	private static JMXConnector getJmxTlsConnection(String host, Integer port) {

		JMXConnector jmxConnector = null;

		try {

			logger.debug("Creating JMXMP connector client on " + host + " at  " + port + " and connecting JMXMP server...");
			JMXServiceURL url = new JMXServiceURL("jmxmp", host, port);

			int attemptCount = 0;

			while (tryReconnect) {
				try {
					jmxConnector = JMXConnectorFactory.connect(url, getEnv());
					jmxConnector.addConnectionNotificationListener(new JmxConnectionListener(), null, jmxConnector);
					logger.info(">> JMXMP Connection successfully established to " + url);
					break;
				} catch (UnknownHostException uhe) {
					System.err.println("UnknownHostException : " + uhe.getLocalizedMessage());
				} catch (ConnectException ce) {
					System.err.println("ConnectException : " + ce.getLocalizedMessage());
				} catch (SocketException se) {
					System.err.println("SocketException : " + se.getLocalizedMessage());
				} catch (Throwable t) {
					t.printStackTrace();
				}

				logger.info(">> JMXMP Connection can NOT be established ! Waiting for 5 second before retry...");
				Thread.sleep(5000);
				logger.info(">> Trying to reconnect. Attempt count " + ++attemptCount);
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

	protected static void disconnect1(JMXConnector jmxConnector) {
		try {
			// Close MBeanServer connection
			//
			logger.debug("Close the connection to the server...");
			jmxConnector.close();
			// selfInstance = null;
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
// if (System.getProperty("javax.net.ssl.trustStore") == null)
// System.setProperty("javax.net.ssl.trustStore",
// "server-cert.store");
// if (System.getProperty("javax.net.ssl.keyStorePassword") == null)
// System.setProperty("javax.net.ssl.keyStorePassword",
// "likya1!+");
// // Comment out the lines above if the server isn't requiring
// // a cert for your client.

// if (System.getProperty("javax.net.ssl.trustStore") == null)
// System.setProperty("javax.net.ssl.trustStore",
// "mySrvKeystore");
// if (System.getProperty("javax.net.ssl.keyStorePassword") == null)
// System.setProperty("javax.net.ssl.keyStorePassword", "123456");
// if (System.getProperty("javax.net.ssl.keyStore") == null)
// System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");

		if (System.getProperty("javax.net.ssl.trustStore") == null)
			System.setProperty("javax.net.ssl.trustStore", "likyaKeystore");
		if (System.getProperty("javax.net.ssl.keyStorePassword") == null)
			System.setProperty("javax.net.ssl.keyStorePassword", "likya1!+");
		if (System.getProperty("javax.net.ssl.keyStore") == null)
			System.setProperty("javax.net.ssl.keyStore", "likyaKeystore");

		/*
		 * The method above is the simplest (IMO therefore the best) method if
		 * your application doesn't use certs for any other purpose. You should
		 * use the instance-based TLS configuration method if your app uses
		 * certs for any other purpose in a single instantiation (i.e., it could
		 * fetch web pages over https, or be a TLS Soap client, etc., or it
		 * could run connect to multiple TLS JMXConnectorServers).
		 * 
		 * The instance-based method allocates a SSLSocketFactory based on the
		 * SSLContext instance which you instantiate and configure, so you can
		 * configure multiple SSLSocketFactories with different SSLContext
		 * instances. This all applies to any standard JSSE TLS application, but
		 * for JMX, you associate the allocated SSLSocketFactory to the
		 * Connector with:
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

	public static void clearHBJmx() throws IOException {
		jmxConnectorForHeartBeat.close();
		jmxConnectorForHeartBeat = null;
	}
}

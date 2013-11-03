package com.likya.tlossw.test.jmxconn;

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

import com.likya.tlossw.utils.date.DateUtils;

public class JmxTlsClient implements Runnable {

	private static Logger logger = Logger.getLogger(JmxTlsClient.class);

	private static boolean isConnected = false;
	
	private static JMXConnector jmxConnector = null;
	
	public static boolean tryReconnect = true;
	
	private static final JmxTlsClient instance = new JmxTlsClient();

	public static void main(String[] args) {
		new Thread(new JmxTlsClient()).start();
	}

	public void run() {

		boolean isLoop = true;

		while (isLoop) {
	
			JMXConnector jmxConnector = getJMXConnection();
			try {
				System.out.println("jmxConnector.getConnectionId() : " + jmxConnector.getConnectionId());

			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				break;
			}
			
		}
		
	}

	public static JMXConnector getJMXConnection() {

		if(isConnected) {
			return jmxConnector;
		}
		
		String host = "0.0.0.0";
		int port = 5555;

		try {

			logger.debug("Creating JMXMP connector client on " + host + " at  " + port + " and connecting JMXMP server...");
			JMXServiceURL url = new JMXServiceURL("jmxmp", host, port);

			int attemptCount = 0;
			
			while (tryReconnect) {

				try {

					long startTime = System.currentTimeMillis();

					// jmxConnector = JMXConnectorFactory.newJMXConnector(url, setupTls());
					// jmxConnector.connect();

					jmxConnector = JMXConnectorFactory.connect(url, setupTls());

					System.err.println("Connected to Jmx in " + DateUtils.dateDiffWithNow(startTime) + " ms");
					setConnected(true);
					if (jmxConnector.getConnectionId() != null) {
						// jmxConnector = JMXConnectorFactory.connect(url);
						jmxConnector.addConnectionNotificationListener(new JmxConnectionListener(), null, JmxTlsClient.getInstance());
						logger.info(">> JMXMP Connection successfully established to " + url);
						break;
					} 
//					else {
//						jmxConnector.close();
//					}

				} catch (UnknownHostException uhe) {
					System.err.println("UnknownHostException ! url : " + url + " message : " + uhe.getLocalizedMessage());
				} catch (ConnectException ce) {
					System.err.println("ConnectException to ! url : " + url + " message : " + ce.getLocalizedMessage());
				} catch (SocketException se) {
					System.err.println("SocketException with ! url : " + url + " message : " + se.getLocalizedMessage());
				} catch (Throwable t) {
					t.printStackTrace();
				}


				logger.info(">> JMXMP Connection can NOT be established ! Waiting for 2 seconds before retry...");
				Thread.sleep(2000);
				logger.info(">> Trying to reconnect. Attempt count " + ++attemptCount);

				//				if (attemptCount < 3) {
				//					logger.info(">> JMXMP Connection can NOT be established ! Waiting for 2 seconds before retry...");
				//					Thread.sleep(2000);
				//					logger.info(">> Trying to reconnect. Attempt count " + ++attemptCount);
				//				} else {
				//					return null;
				//				}
			}

			logger.info("Connected to JMXMP server on host " + host + " through port " + port);

		} catch (Exception e) {
			System.out.println("HeartBeat icin baglanti kurulamadi !! >> " + e.getLocalizedMessage());

			System.out.println("> JMXMP connection has not been created with host: " + host + " and port: " + port + " !");
			logger.debug(">>JMXMP connection has not been created with host: " + host + " and port: " + port + " !");
			logger.debug("   > " + URL.hostEncoding);

		}

		return jmxConnector;

	}

	protected static Map<String, Object> setupTls() {

		Map<String, Object> env = new HashMap<String, Object>();

		String keyStore = "likyaKeystore";
		String trustStore = "likyaKeystore";
		String password = "likya1!+";

		if (System.getProperty("javax.net.ssl.trustStore") == null)
			System.setProperty("javax.net.ssl.trustStore", keyStore);
		if (System.getProperty("javax.net.ssl.keyStorePassword") == null)
			System.setProperty("javax.net.ssl.keyStorePassword", password);
		if (System.getProperty("javax.net.ssl.keyStore") == null)
			System.setProperty("javax.net.ssl.keyStore", trustStore);

		env.put("jmx.remote.profiles", "TLS");
		env.put("jmx.remote.tls.need.client.authentication", "true");

		// Bu parametre çok tehlikeri serkan
		// env.put("jmx.remote.x.server.connection.timeout", "0");
		// env.put("jmx.remote.x.server.connection.timeout", "10");
		
		
		// Denemeler
		
		env.put("jmx.remote.x.request.timeout", new Long(Long.MAX_VALUE));
		env.put("jmx.remote.x.client.connection.check.period", new Long(0));
		env.put("jmx.remote.x.server.connection.timeout", new Long(Long.MAX_VALUE));
		
		return env;
	}

	public static boolean isConnected() {
		return isConnected;
	}

	public static void setConnected(boolean isConnected) {
		JmxTlsClient.isConnected = isConnected;
	}

	public static JmxTlsClient getInstance() {
		return instance;
	}

}

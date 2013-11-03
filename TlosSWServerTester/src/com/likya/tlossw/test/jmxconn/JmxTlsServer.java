package com.likya.tlossw.test.jmxconn;

import java.io.IOException;
import java.net.BindException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

public class JmxTlsServer implements Runnable {

	private static Logger logger = Logger.getLogger(JmxTlsServer.class);

	private static MBeanServer mbeanServer;
	private static JMXConnectorServer jConnectorServer;

	public static void main(String[] args) {
		new Thread(new JmxTlsServer()).start();
	}

	public void run() {
		initialize();
	}

	public static void initialize() {

		try {
			setupTls();

			// Create a JMXMP-TLS connector server
			//
			logger.info("Create a JMXMP-TLS connector server... > ");

			// hardcoded ip : localhost port : 5555
			int port = 5555;

			logger.info("Using port number : " + port);

			String ipAddress = "0.0.0.0";

			logger.info("Using ip address : " + ipAddress);

			logger.info("");
			logger.info("############# MBean Server ##################");
			
			logger.info("Create the MBean server...");
			mbeanServer = MBeanServerFactory.createMBeanServer();

			logger.info("Created !");
			
			JMXServiceURL url = new JMXServiceURL("jmxmp", ipAddress, port);
			jConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, setupTls(), mbeanServer);

			logger.info("Created !");

			// Start the JMXMP-TLS connector server
			//
			logger.info("Start the JMXMP-TLS connector server... > ");
			jConnectorServer.start();

			logger.info("Started !");
			logger.info("Waiting for incoming connections...");
			logger.info("#############################################");
			logger.info("");

		} catch (MalformedURLException mue) {
			logger.error("### MalformedURLException ###");
			mue.printStackTrace();
			try {
				jConnectorServer.stop();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SecurityException e) {
			// System.out.println(" ### SecurityException ### ");
			logger.error("### SecurityException ###");
			e.printStackTrace();
			System.exit(-1);
		} catch (BindException e) {
			// System.out.println(" ### BindException ### ");
			logger.error("### BindException ###");
			e.printStackTrace();
			System.exit(-1);
		} catch (Exception e) {
			// System.out.println(" ### Unclassified Error ### ");
			logger.error("### Unclassified Error ###");
			e.printStackTrace();
			System.exit(-1);
		}
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
		
		return env;
	}

}

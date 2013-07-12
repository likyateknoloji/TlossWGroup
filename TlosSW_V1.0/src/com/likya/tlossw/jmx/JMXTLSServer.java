/*
 * @(#)file      Server.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.6
 * @(#)lastedit  03/10/07
 * @(#)build     jmxremote-1_0_1_04-b58 2005.11.23_16:04:12_MET
 *
 * Copyright 2005 Sun Microsystems, Inc.  All rights reserved. Use is subject to license terms.
 */

package com.likya.tlossw.jmx;

import java.io.IOException;
import java.net.BindException;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.model.jmx.JmxAgentUser;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.XmlUtils;
import com.likya.tlossw.utils.i18n.ResourceMapper;

public class JMXTLSServer {

	private static MBeanServer mbeanServer;
	private static JMXConnectorServer jConnectorServer;
	private static Map<String, String> env = new HashMap<String, String>();

	private static SpaceWideRegistry spaceWideRegistry = SpaceWideRegistry.getInstance(); 
	
	private static Logger logger = SpaceWideRegistry.getGlobalLogger();
	
	/**
	 * This function is designed for descrete testing procedures.
	 * DO NOT USE for server functionality.
	 * @author Serkan Taş 12.09.2012 
	 * @param spaceWideRegistry
	 */
	public static void initialize(SpaceWideRegistry spaceWideRegistry ) {
		JMXTLSServer.spaceWideRegistry = spaceWideRegistry;
		initialize();
	}
	
	public static void initialize() {

		try {
			setupTls();

			logger.info("");
			logger.info("############# MBean Server ##################");
			
			logger.info("Create the MBean server...");
			mbeanServer = MBeanServerFactory.createMBeanServer();

			logger.info("Created !");

			String MBeanArray[] = { "LocalManager", "ProcessInfoProvider", "ProcessManagementInterface", "RemoteFileOperator", "RemoteDBOperator", "AgentOperator", "WebServiceOperator", "ValidationExecuter", "WorkSpaceOperator" };
			String MBeanTypeArray[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8" };

			for (int i = 0; i < MBeanArray.length; i++) {
				ObjectName mbeanName = new ObjectName("MBeans:type=" + MBeanTypeArray[i]);
				logger.info(MBeanArray[i] + " MBean is created ...");
				mbeanServer.createMBean(JMXServer.class.getPackage().getName() + ".beans." + MBeanArray[i], mbeanName, null, null);
			}

			logger.info(ResourceMapper.SECTION_DIVISON_KARE);
			logger.info("");
			logger.info("######### JMXMP-TLS Connector Server ############");
			logger.info("");
			
			// Create a JMXMP-TLS connector server
			//
			logger.info("Create a JMXMP-TLS connector server... > ");
			
			// hardcoded ip : localhost port : 5555
			int port = TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo().getJmxParams().getJmxTlsPort().getPortNumber();
			if (port <= 0) {
				port = 5555;
			}

			logger.info("Using port number : " + port);

			String ipAddress = TlosSpaceWide.getSpaceWideRegistry().getServerConfig().getServerParams().getIpAddress();
			if (ipAddress == null || ipAddress.equals("")) {
				ipAddress = null;
			}

			logger.info("Using ip address : " + ipAddress);		
			
			JMXServiceURL url = new JMXServiceURL("jmxmp", ipAddress, port);
			jConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, env, mbeanServer);

			logger.info("Created !");
			
			// Start the JMXMP-TLS connector server
			//
			logger.info("Start the JMXMP-TLS connector server... > ");
			jConnectorServer.start();

			logger.info("Started !");
			logger.info("Waiting for incoming connections...");
			logger.info("#############################################");
			logger.info("");

			String jmxUserName = "" + new Random(new Long(Calendar.getInstance().getTimeInMillis())).nextLong();
			Thread.sleep(10);
			String jmxPassWord = "" + new Random(new Long(Calendar.getInstance().getTimeInMillis())).nextLong();

			JmxUser jmxUser = new JmxUser();
			jmxUser.setJmxClientAuthanticationId(jmxUserName);
			jmxUser.setJmxClientAuthanticationKey(jmxPassWord);
			
			TlosSpaceWide.getSpaceWideRegistry().setJmxUser(jmxUser);

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

	public static void disconnect() {
		try {
			
			logger.info("Closing jmx server...");
			
			String[] connIdList = jConnectorServer.getConnectionIds();
			
			logger.info("Current active JMXMP-TLS client count : " + connIdList.length);
			logger.info("Waiting for the connections to be closed...");
			
			int counter = 0;
			
			while (true) {
				if (jConnectorServer.getConnectionIds().length == 0 || counter++ == 20) {
					break;
				}
				try {
					Thread.sleep(1000);
					System.out.print(".");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if (counter == 20) {
				logger.info("Client(s) are not disconnected, terminated by server !");
			} else {
				logger.info("Client(s) disconnected !");
			}
			
			jConnectorServer.stop();
			
			logger.info("Closed !");
			logger.info("Releasing MBean Server...");

			MBeanServerFactory.releaseMBeanServer(mbeanServer);
		
			logger.info("Released !");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean authorizeAgent(JmxAgentUser jmxAgentUser) {

		SWAgent clientSideSwAgent = XmlUtils.convertToSwAgent(jmxAgentUser.getSwAgentXML());

		SWAgent serverSideSwAgent = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference().getSwAgentsCache().get(jmxAgentUser.getAgentId() + "");

		if (serverSideSwAgent == null || !clientSideSwAgent.getJmxUser().equals(serverSideSwAgent.getJmxUser()) || !clientSideSwAgent.getJmxPassword().equals(serverSideSwAgent.getJmxPassword())) {
			return false;
		}
		return true;
	}

	public static boolean authorizeWeb(JmxUser jmxUser) {
		
		String clientAuthanticationId = TlosSpaceWide.getSpaceWideRegistry().getJmxUser().getJmxClientAuthanticationId();
		String jmxClientAuthanticationKey = TlosSpaceWide.getSpaceWideRegistry().getJmxUser().getJmxClientAuthanticationKey();
		
		if (!clientAuthanticationId.equals(jmxUser.getJmxClientAuthanticationId()) || !jmxClientAuthanticationKey.equals(jmxUser.getJmxClientAuthanticationKey())) {
			//return false;
		}
		return true;
	}

	protected static void setupTls() {
		// Note that the settings below are conditional, so you can
		// override then with "java -Djavax...=Y... ConnectorServerAgent..."
		// It's definitely not safe to use -D to set passwords, though,
		// but it's useful for prototyping.

		String keyStore = spaceWideRegistry.getTlosSWConfigInfo().getJmxParams().getKeyStore();
		String trustStore = spaceWideRegistry.getTlosSWConfigInfo().getJmxParams().getTrustStore();
		String password = spaceWideRegistry.getTlosSWConfigInfo().getJmxParams().getPassword();
		
		if (System.getProperty("javax.net.ssl.trustStore") == null)
			System.setProperty("javax.net.ssl.trustStore", keyStore);
		if (System.getProperty("javax.net.ssl.keyStorePassword") == null)
			System.setProperty("javax.net.ssl.keyStorePassword", password);
		if (System.getProperty("javax.net.ssl.keyStore") == null)
			System.setProperty("javax.net.ssl.keyStore", trustStore);

		/*
		 * The method above is the simplest (IMO therefore the best) method if
		 * your application doesn't use certs for any other purpose. You should
		 * use the instance-based TLS configuration method if your app uses
		 * certs for any other purpose (i.e., it could fetch web pages over
		 * https, or be a TLS Soap client, etc., or it could run multiple TLS
		 * JMXConnectorServers).
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
		env.put("jmx.remote.tls.need.client.authentication", Boolean.toString(isRequireClientAuth()));
		// Comment out the line above if you don't want to require
		// clients to have their own certs.

		// if ((new File("access.properties")).isFile())
		// env.put("jmx.remote.x.access.file", "access.properties");
		// IF file "access.properties" is present in $PWD (from where server
		// is started), it must have keys of permitted client cert subjects,
		// and values of "readwrite" or "readonly".
		// N.b. You MUST ESCAPE all spaces, colons, and equal signes in the
		// subject with backslashes!
		// Example record:
		// CN\=proto\ client\ 1,OU\=RND,O\=Fake\ Corp.,C\=US readwrite
	}

	/**
	 * Only used if running with TLS mode.
	 * 
	 * If you want to run TLS mode without Client certs, just override this
	 * class and override this method to return false.
	 * 
	 * @returns true (unless this method is overridden).
	 */
	protected static boolean isRequireClientAuth() {
		return true;
	}

}

/*
 * @(#)file      Server.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.6
 * @(#)lastedit  03/10/07
 * @(#)build     jmxremote-1_0_1_04-b58 2005.11.23_16:04:12_MET
 *
 * Copyright 2005 Sun Microsystems, Inc.  All rights reserved. Use is subject to license terms.
 */

package com.likya.tlosswagent.jmx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlossw.model.jmx.JmxAgentUser;
import com.likya.tlosswagent.TlosSWAgent;
import com.likya.tlosswagent.utils.SWAgentRegistry;
import com.likya.tlosswagent.utils.XmlUtils;
import com.likya.tlosswagent.utils.i18n.ResourceMapper;

public class JMXServer {

	private static MBeanServer mbeanServerTls;
	private static MBeanServer mbeanServer;
	private static JMXConnectorServer jConnectorServerTls;
	private static JMXConnectorServer jConnectorServer;

	public static void initializeTls() {
		try {
			SWAgentRegistry.getsWAgentLogger().info("");

			Map<String, String> env = null;

			SWAgentRegistry.getsWAgentLogger().info("  > ********************* MBean Server (TLS) ********************");
			env = setupTls();

			SWAgentRegistry.getsWAgentLogger().info("  > Create the MBean server...");
			mbeanServerTls = MBeanServerFactory.createMBeanServer();
			SWAgentRegistry.getsWAgentLogger().info("  > Created !");

			String MBeanArray[] = { "TaskAgent", "ProcessInfoProvider" };
			String MBeanTypeArray[] = { "0", "1" };

			for (int i = 0; i < MBeanArray.length; i++) {
				ObjectName mbeanName = new ObjectName("MBeans:type=" + MBeanTypeArray[i]);
				SWAgentRegistry.getsWAgentLogger().info("  > Create " + MBeanArray[i] + " MBean...");
				mbeanServerTls.createMBean(JMXServer.class.getPackage().getName() + ".beans." + MBeanArray[i], mbeanName, null, null);
				SWAgentRegistry.getsWAgentLogger().info("  > Created !");
			}
			SWAgentRegistry.getsWAgentLogger().info("  > *************************************************************");
			SWAgentRegistry.getsWAgentLogger().info("  > ");
			SWAgentRegistry.getsWAgentLogger().info("  > ********************** JMXMP Server (TLS) *******************");

			// Create a JMXMP connector server
			//
			SWAgentRegistry.getsWAgentLogger().info("  > Create a JMXMP connector server...");

			JMXServiceURL url = null;

			url = new JMXServiceURL("jmxmp", null, TlosSWAgent.getSwAgentRegistry().getAgentConfigInfo().getSettings().getJmxTlsPort().getPortNumber());
			jConnectorServerTls = JMXConnectorServerFactory.newJMXConnectorServer(url, env, mbeanServerTls);

			SWAgentRegistry.getsWAgentLogger().info("  > Created !");
			SWAgentRegistry.getsWAgentLogger().info("  > " + url.toString() + " Created !");
			// Start the JMXMP connector server
			//
			SWAgentRegistry.getsWAgentLogger().info("  > Start the JMXMP-TLS connector server");
			jConnectorServerTls.start();
			SWAgentRegistry.getsWAgentLogger().info("  > JMXMP-TLS connector server successfully started");

			SWAgentRegistry.getsWAgentLogger().info("  > *************************************************************");
			SWAgentRegistry.getsWAgentLogger().info("  > Waiting for incoming connections...");
			SWAgentRegistry.getsWAgentLogger().info("  > ");

		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			try {
				jConnectorServerTls.stop();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			TlosSWAgent.errprintln(TlosSWAgent.getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.TERMINATE_APPLICATION));
			SWAgentRegistry.getsWAgentLogger().error(TlosSWAgent.getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.TERMINATE_APPLICATION));
			System.exit(-1);
		}
	}

	public static void initializeNormal() {
		try {
			SWAgentRegistry.getsWAgentLogger().info("");

			Map<String, String> env = null;

			SWAgentRegistry.getsWAgentLogger().info("  > ********************* MBean Server ********************");

			SWAgentRegistry.getsWAgentLogger().info("  > Create the MBean server...");
			mbeanServer = MBeanServerFactory.createMBeanServer();
			SWAgentRegistry.getsWAgentLogger().info("  > Created !");

			String MBeanArray[] = { "TaskAgent", "ProcessInfoProvider" };
			String MBeanTypeArray[] = { "0", "1" };

			for (int i = 0; i < MBeanArray.length; i++) {
				ObjectName mbeanName = new ObjectName("MBeans:type=" + MBeanTypeArray[i]);
				SWAgentRegistry.getsWAgentLogger().info("  > Create " + MBeanArray[i] + " MBean...");
				mbeanServer.createMBean(JMXServer.class.getPackage().getName() + ".beans." + MBeanArray[i], mbeanName, null, null);
				SWAgentRegistry.getsWAgentLogger().info("  > Created !");
			}
			SWAgentRegistry.getsWAgentLogger().info("  > *************************************************************");
			SWAgentRegistry.getsWAgentLogger().info("  > ");
			SWAgentRegistry.getsWAgentLogger().info("  > ********************** JMXMP Server *******************");

			SWAgentRegistry.getsWAgentLogger().info("  > Create a JMXMP connector server...");

			JMXServiceURL url = null;

			url = new JMXServiceURL("jmxmp", null, TlosSWAgent.getSwAgentRegistry().getAgentConfigInfo().getSettings().getJmxPort().getPortNumber());
			jConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, env, mbeanServer);

			SWAgentRegistry.getsWAgentLogger().info("  > Created !");
			SWAgentRegistry.getsWAgentLogger().info("  > " + url.toString() + " Created !");
			SWAgentRegistry.getsWAgentLogger().info("  > Start the JMXMP connector server");
			jConnectorServer.start();
			SWAgentRegistry.getsWAgentLogger().info("  > JMXMP connector server successfully started");

			SWAgentRegistry.getsWAgentLogger().info("  > *************************************************************");
			SWAgentRegistry.getsWAgentLogger().info("  > Waiting for incoming connections...");
			SWAgentRegistry.getsWAgentLogger().info("  > ");

		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			try {
				jConnectorServer.stop();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			TlosSWAgent.errprintln(TlosSWAgent.getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.TERMINATE_APPLICATION));
			SWAgentRegistry.getsWAgentLogger().error(TlosSWAgent.getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.TERMINATE_APPLICATION));
			System.exit(-1);
		}
	}

	public static void disconnectTls() {
		try {

			TlosSWAgent.println("Closing jmx server...");
			String[] connIdList = jConnectorServerTls.getConnectionIds();
			System.out.println("Current active JMX client count : " + connIdList.length);
			System.out.println("Waiting for the connections to be closed...");
			int counter = 0;
			while (true) {
				if (jConnectorServerTls.getConnectionIds().length == 0 || counter++ == 20) {
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
				System.out.println("\nClient(s) are not disconnected, terminated bby server !");
			} else {
				System.out.println("\nClient(s) disconnected !");
			}
			jConnectorServerTls.stop();
			TlosSWAgent.println("Closed !");
			TlosSWAgent.print("Releasing MBean Server...");
			MBeanServerFactory.releaseMBeanServer(mbeanServerTls);
			TlosSWAgent.println("Released !");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void disconnectNormal() {
		try {

			TlosSWAgent.println("Closing jmx server...");
			String[] connIdList = jConnectorServer.getConnectionIds();
			System.out.println("Current active JMX client count : " + connIdList.length);
			System.out.println("Waiting for the connections to be closed...");
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
				System.out.println("\nClient(s) are not disconnected, terminated bby server !");
			} else {
				System.out.println("\nClient(s) disconnected !");
			}
			jConnectorServer.stop();
			TlosSWAgent.println("Closed !");
			TlosSWAgent.print("Releasing MBean Server...");
			MBeanServerFactory.releaseMBeanServer(mbeanServer);
			TlosSWAgent.println("Released !");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean authroize(JmxAgentUser jmxAgentUser) {
		SWAgent serverSideAgent = XmlUtils.convertToSwAgent(jmxAgentUser.getSwAgentXML());
		SWAgent clientSideAgent = XmlUtils.convertToSwAgent(TlosSWAgent.getSwAgentRegistry().getJmxAgentUser().getSwAgentXML());

		if (!clientSideAgent.getJmxUser().equals(serverSideAgent.getJmxUser()) || !clientSideAgent.getJmxPassword().equals(serverSideAgent.getJmxPassword())) {
			return false;
		}

		return true;
	}

	private static HashMap<String, String> setupTls() {

		HashMap<String, String> env = new HashMap<String, String>();

		// Note that the settings below are conditional, so you can
		// override then with "java -Djavax...=Y... ConnectorServerAgent..."
		// It's definitely not safe to use -D to set passwords, though,
		// but it's useful for prototyping.

		if (System.getProperty("javax.net.ssl.trustStore") == null)
			System.setProperty("javax.net.ssl.trustStore", "likyaKeystore");
		if (System.getProperty("javax.net.ssl.keyStorePassword") == null)
			System.setProperty("javax.net.ssl.keyStorePassword", "likya1!+");
		if (System.getProperty("javax.net.ssl.keyStore") == null)
			System.setProperty("javax.net.ssl.keyStore", "likyaKeystore");

// if (System.getProperty("javax.net.ssl.trustStore") == null)
// System.setProperty("javax.net.ssl.trustStore", "mySrvKeystore");
// if (System.getProperty("javax.net.ssl.keyStorePassword") == null)
// System.setProperty("javax.net.ssl.keyStorePassword", "123456");
// if (System.getProperty("javax.net.ssl.keyStore") == null)
// System.setProperty("javax.net.ssl.keyStore", "mySrvKeystore");

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

		return env;

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

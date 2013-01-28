/*
 * @(#)file      Client.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.7
 * @(#)lastedit  03/10/07
 * @(#)build     jmxremote-1_0_1_04-b58 2005.11.23_16:04:12_MET
 *
 * Copyright 2005 Sun Microsystems, Inc.  All rights reserved. Use is subject to license terms.
 */

package com.likya.tlosswagent;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.likya.tlos.model.xmlbeans.agentconfig.AgentConfigInfoDocument.AgentConfigInfo;
import com.likya.tlosswagent.utils.ConfigLoader;

public class StopAgent {
	
	private static Map<String, String> env = new HashMap<String, String>();	

	public static void main(String[] args) {

		String ipAddr = null;
		try {
			InetAddress addr = InetAddress.getLocalHost();

			// Get IP Address
			ipAddr = addr.getHostAddress();
		} catch (UnknownHostException e) {
		}

		try {

			Object[] paramList = { ipAddr.toString() };
			String[] signature = { "java.lang.String" };

			// Create a JMXMP connector client and
			// connect it to the JMXMP connector server
			//
			setUpTls();
			System.out.println("\nCreate a JMXMP connector client and " + "connect it to the JMXMP connector server");
			
			AgentConfigInfo agentConfigInfo = ConfigLoader.readTlosConfig();
			
			JMXServiceURL url = new JMXServiceURL("jmxmp", null, agentConfigInfo.getSettings().getJmxTlsPort().getPortNumber());
			JMXConnector jmxc = JMXConnectorFactory.connect(url, env);

			// Get an MBeanServerConnection
			//
			System.out.println("\nGet an MBeanServerConnection");
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

			mbsc.invoke(new ObjectName("MBeans:type=0"), "shutdown", paramList, signature);

			// Sleep for 2 seconds in order to have time to receive the
			// notification before removing the notification listener.
			//
			// System.out.println("\nWaiting for notification...");
			// Thread.sleep(2000);

			// Remove notification listener on SimpleStandard MBean
			//
			// System.out.println("\nRemove notification listener...");
			// mbsc.removeNotificationListener(mbeanName, listener);

			// Close MBeanServer connection
			//
			System.out.println("\nClose the connection to the server");
			jmxc.close();
			System.out.println("\nBye! Bye!");
		} catch (ConnectException cex) {
			System.out.println("Can not connect to JMX Provider ! Terminated.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static void setUpTls() {

		if (System.getProperty("javax.net.ssl.trustStore") == null)
			System.setProperty("javax.net.ssl.trustStore", "likyaKeystore");
		if (System.getProperty("javax.net.ssl.keyStorePassword") == null)
			System.setProperty("javax.net.ssl.keyStorePassword", "likya1!+");
		if (System.getProperty("javax.net.ssl.keyStore") == null)
			System.setProperty("javax.net.ssl.keyStore", "likyaKeystore");

		env.put("jmx.remote.profiles", "TLS");
		env.put("jmx.remote.tls.need.client.authentication", "true");
	}
}

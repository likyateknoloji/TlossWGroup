/*
 * @(#)file      Client.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.7
 * @(#)lastedit  03/10/07
 * @(#)build     jmxremote-1_0_1_04-b58 2005.11.23_16:04:12_MET
 *
 * Copyright 2005 Sun Microsystems, Inc.  All rights reserved. Use is subject to license terms.
 */

package com.likya.tlossw;

import java.net.ConnectException;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

public class SimulateGunDonumu extends LocalJmx {

	
	public static void main(String[] args) {
		new SimulateGunDonumu().doIt(args);
	}
	
	public void doIt(String[] args) {

		try {
			
			parseArguments("SimulateGunDonumu", args);

			Object[] paramList = { };
			String[] signature = { };

			// Create a JMXMP connector client and
			// connect it to the JMXMP connector server
			//
			JMXConnector jmxc = getJMXConnector();

			// Get an MBeanServerConnection
			//
			System.out.println("\nGet an MBeanServerConnection");
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

			mbsc.invoke(new ObjectName("MBeans:type=0"), "simulateGunDonumu", paramList, signature);

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

}

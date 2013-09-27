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
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class StartOver extends LocalJmx {
	
	public static void main(String[] args) {

		try {
			
			boolean backupReports = false;

			Object[] paramList = { backupReports };
			String[] signature = { "boolean" };

			setUpTls();
			System.out.println("\nCreate a JMXMP connector client and " + "connect it to the JMXMP connector server");
			JMXServiceURL url = new JMXServiceURL("jmxmp", null, 5555);
			JMXConnector jmxc = JMXConnectorFactory.connect(url, env);

			System.out.println("\nGet an MBeanServerConnection");
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

			mbsc.invoke(new ObjectName("MBeans:type=0"), "startOver", paramList, signature);

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

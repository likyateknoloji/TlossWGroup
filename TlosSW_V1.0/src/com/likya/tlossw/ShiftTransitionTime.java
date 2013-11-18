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

public class ShiftTransitionTime extends LocalJmx {
	
	public static void main(String[] args) {
		new ShiftTransitionTime().doIt(args);
	}

	public void doIt(String[] args) {

		try {
			
			parseArguments("ShiftTransitionTime", args);
			
			boolean backupReports = false;

			Object[] paramList = { backupReports };
			String[] signature = { "boolean" };

			JMXConnector jmxc = getJMXConnector();

			System.out.println("\nGet an MBeanServerConnection");
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

			mbsc.invoke(new ObjectName("MBeans:type=0"), "shiftTransitionTime", paramList, signature);

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

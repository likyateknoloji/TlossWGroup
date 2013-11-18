package com.likya.tlossw;

import java.net.ConnectException;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

public class StopTlos extends LocalJmx {

	public static void main(String[] args) {
		new StopTlos().doIt(args);
	}

	public void doIt(String[] args) {
		try {

			parseArguments("StopTlos", args);

			Object[] paramList = { getIpAddress() };
			String[] signature = { "java.lang.String" };

			// Create a JMXMP connector client and
			// connect it to the JMXMP connector server
			//
		
			JMXConnector jmxc = getJMXConnector();

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
			cex.printStackTrace();
			System.err.println("Can not connect to server, check it !");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package com.likya.tlossw;

import java.net.ConnectException;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

public class StartOver extends LocalJmx {
	
	public static void main(String[] args) {
		new StartOver().doIt(args);
	}
	
	public void doIt(String[] args) {

		try {
			
			parseArguments("StartOver", args);
			
			boolean backupReports = false;

			Object[] paramList = { backupReports };
			String[] signature = { "boolean" };

			JMXConnector jmxc = getJMXConnector();

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

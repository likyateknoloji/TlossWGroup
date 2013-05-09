package com.likya.tlossw.jmx;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.model.jmx.JmxAgentUser;
import com.likya.tlossw.utils.XmlUtils;
import com.likya.tlossw.utils.i18n.ResourceMapper;

public class JMXServer {

	private static MBeanServer mbeanServer;
	private static JMXConnectorServer jConnectorServer;

	public static void initialize() {

		try {
			System.out.print("Create the MBean server...");
			mbeanServer = MBeanServerFactory.createMBeanServer();
			JMRuntimeException("HATA : XXX");
			System.out.println("Created !");

			String MBeanArray[] = { "LocalManager", "ProcessInfoProvider", "ProcessManagementInterface", "RemoteFileOperator", "RemoteDBOperator", "AgentOperator", "WebServiceOperator" };
			String MBeanTypeArray[] = { "0", "1", "2", "3", "4", "5", "6" };

			for (int i = 0; i < MBeanArray.length; i++) {
				ObjectName mbeanName = new ObjectName("MBeans:type=" + MBeanTypeArray[i]);
				System.out.print("Create " + MBeanArray[i] + " MBean...");
				mbeanServer.createMBean(JMXServer.class.getPackage().getName() + ".beans." + MBeanArray[i], mbeanName, null, null);
				System.out.println("Created !");
			}

			// Create a JMXMP connector server
			//
			System.out.print("Create a JMXMP connector server...");
			// hardcoded ip : localhost port : 5554
			int port = TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo().getJmxParams().getJmxPort().getPortNumber();
			if(port <= 0) {
				port = 5554;
			}
			
			System.out.println("Using port number : " + port);
			
			String ipAddress = TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo().getSettings().getIpAddress();
			if(ipAddress == null || ipAddress.equals("")) {
				ipAddress = null;
			}
			
			System.out.println("Using ip address : " + ipAddress);
			
			JMXServiceURL url = new JMXServiceURL("jmxmp", ipAddress, port);
			jConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbeanServer);
			System.out.println("Created !");
			System.out.println(url.toString() + "Created !");
			// Start the JMXMP connector server
			//
			System.out.print("Start the JMXMP connector server");
			jConnectorServer.start();
			System.out.println("JMXMP connector server successfully started");
			System.out.println("Waiting for incoming connections...");

			// TODO bu sleepin bir amaci var mi?
			// Buradaki sleep random sayı üretiminde farklı sayılar oluşsun diye kondu.
			// String jmxUserName = "" + new Random(new Long(Calendar.getInstance().getTimeInMillis())).nextLong();
			// Thread.sleep(10);
			// String jmxPassWord = "" + new Random(new Long(Calendar.getInstance().getTimeInMillis())).nextLong();
			//
			// JmxUser jmxUser = new JmxUser(jmxUserName, jmxPassWord);
			// TlosSpaceWide.getSpaceWideRegistry().setJmxUser(jmxUser);

		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			try {
				jConnectorServer.stop();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			TlosSpaceWide.errprintln(TlosSpaceWide.getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.TERMINATE_APPLICATION));
			System.exit(-1);
		}
	}

	private static void JMRuntimeException(String string) {
		// TODO Auto-generated method stub

	}

	public static void disconnect() {
		try {
			TlosSpaceWide.println("Closing jmx server...");
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
			TlosSpaceWide.println("Closed !");
			TlosSpaceWide.print("Releasing MBean Server...");
			MBeanServerFactory.releaseMBeanServer(mbeanServer);
			TlosSpaceWide.println("Released !");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean authorize(JmxAgentUser jmxAgentUser) {
		SWAgent clientSideSwAgent = XmlUtils.convertToSwAgent(jmxAgentUser.getSwAgentXML());
		// SWAgent serverSideSwAgent = TlosSpaceWide.getSpaceWideRegistry().getHeartBeatListenerRef().getSwAgentsCache().get(clientSideSwAgent.getIpAddress()+"."+clientSideSwAgent.getJmxPort());
		SWAgent serverSideSwAgent = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference().getSwAgentsCache().get(jmxAgentUser.getAgentId() + "");

		if (serverSideSwAgent == null || !clientSideSwAgent.getJmxUser().equals(serverSideSwAgent.getJmxUser()) || !clientSideSwAgent.getJmxPassword().equals(serverSideSwAgent.getJmxPassword())) {
			return false;
		}
		return true;
	}
}

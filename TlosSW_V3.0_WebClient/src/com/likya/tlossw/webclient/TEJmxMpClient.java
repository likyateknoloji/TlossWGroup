package com.likya.tlossw.webclient;

import java.util.ArrayList;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceDocument.Resource;
import com.likya.tlossw.model.MessagesCodeMapping;
import com.likya.tlossw.model.TlosJmxReturnValue;
import com.likya.tlossw.model.WebSpaceWideRegistery;
import com.likya.tlossw.model.client.resource.ResourceInfoTypeClient;
import com.likya.tlossw.model.client.resource.TlosAgentInfoTypeClient;
import com.likya.tlossw.model.client.spc.InfoTypeClient;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.model.client.spc.SpcInfoTypeClient;
import com.likya.tlossw.model.client.spc.SpcLookUpTableTypeClient;
import com.likya.tlossw.model.client.spc.TreeInfoType;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.model.tree.TlosSpaceWideNode;
import com.likya.tlossw.model.tree.resource.TlosSWResourceNode;

public class TEJmxMpClient extends TEJmxMpClientBase {
	
	private TEJmxMpClient() {
		// initCommanderInstance();
	}

/*	private static TEJmxMpClientBase initInstance() {
		if (getSelfInstance() == null) {
			setSelfInstance(new TEJmxMpClient());
		}

		return getSelfInstance();
	}
*/
	public static JobInfoTypeClient getJobInfoTypeClient(JmxUser jmxUser, String groupId, String jobId, boolean transformToLocalTime) {
		
		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, groupId, jobId, transformToLocalTime };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String", "java.lang.Boolean" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "retrieveJobDetails", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (JobInfoTypeClient) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<JobInfoTypeClient> getJobInfoTypeClientList(JmxUser jmxUser, String groupId) {
		return getJobInfoTypeClientList(jmxUser, groupId, false);
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<JobInfoTypeClient> getJobInfoTypeClientList(JmxUser jmxUser, String groupId, boolean transformToLocalTime) {
		
		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, groupId, transformToLocalTime };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.Boolean" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + LJSTIP), "retrieveJobListDetails", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<JobInfoTypeClient>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static SpcLookUpTableTypeClient getSpsLookUpTable(JmxUser jmxUser, String instanceId, String treePath) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, instanceId, treePath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "retrieveSpcLookupTable", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (SpcLookUpTableTypeClient) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static SpcInfoTypeClient retrieveSpcInfo(JmxUser jmxUser, String treePath) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, treePath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "retrieveSpcInfo", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (SpcInfoTypeClient) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Management Functions
	 */
	public static void shutDown(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "shutdown", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public static void stopJob(JmxUser jmxUser, String jobPath) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, jobPath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "stopJob", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public static void retryJob(JmxUser jmxUser, String jobPath) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, jobPath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "retryJob", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public static void doSuccess(JmxUser jmxUser, String jobPath) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, jobPath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "doSuccess", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public static void skipJob(JmxUser jmxUser, String jobPath) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, jobPath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "skipJob", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public static void pauseJob(JmxUser jmxUser, String jobPath) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, jobPath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "pauseJob", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public static void resumeJob(JmxUser jmxUser, String jobPath) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, jobPath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "resumeJob", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public static void startJob(JmxUser jmxUser, String jobPath) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, jobPath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "startJob", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	public static void startUserBasedJob(JmxUser jmxUser, String jobPath) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, jobPath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "startUserBasedJob", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Resource> getAvailableResourcesForJob(JmxUser jmxUser, String jobPath) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, jobPath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "getAvailableResourcesForJob", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<Resource>)o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean assignAgentForJob(JmxUser jmxUser, String jobPath, String agentId) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, jobPath, agentId };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "assignAgentForJob", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return ((Boolean) o).booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * File operations
	 */

	public static boolean checkFile(JmxUser jmxUser, String fileName) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();		

		Object[] paramList = { jmxUser, fileName };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + RFO), "checkFile", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return ((Boolean) o).booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static StringBuffer readFile(JmxUser jmxUser, String fileName) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, fileName };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + RFO), "readFile", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (StringBuffer) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static StringBuffer readFile(JmxUser jmxUser, String fileName, String coloredLineIndicator, boolean useSections, boolean isXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, fileName, coloredLineIndicator, useSections, isXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String", "java.lang.Boolean", "java.lang.Boolean" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + RFO), "checkFile", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (StringBuffer) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<String> retrieveViewFiles(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "retrieveViewFiles", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<String>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void stopScenario(JmxUser jmxUser, String scenarioId, boolean isForced) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, scenarioId, new Boolean(isForced) };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.Boolean" };

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "stopScenario", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public static void restartScenario(JmxUser jmxUser, String scenarioId) {
		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, scenarioId };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "restartScenario", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public static void resumeScenario(JmxUser jmxUser, String scenarioId) {
		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, scenarioId };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "resumeScenario", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public static void suspendScenario(JmxUser jmxUser, String scenarioId) {
		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, scenarioId };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "suspendScenario", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public static TlosJmxReturnValue addJob(JmxUser jmxUser, String jobPropertiesXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, jobPropertiesXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "addJob", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return  (TlosJmxReturnValue) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new TlosJmxReturnValue(MessagesCodeMapping.fetchTlosGuiMessage(MessagesCodeMapping.JMX_ERROR), null);
	}

	public static TreeInfoType retrieveTreeInfo(JmxUser jmxUser, String instanceId, ArrayList<String> scenariodIdList) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, instanceId, scenariodIdList };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.util.ArrayList" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "retrieveTreeInfo", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (TreeInfoType) o;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	public static boolean retrieveWaitConfirmOfGUI(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "retrieveWaitConfirmOfGUI", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return ((Boolean) o).booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;

	}

	@SuppressWarnings("unchecked")
	public static ArrayList<String> retrieveInstanceIds(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "retrieveInstanceIds", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<String>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static InfoTypeClient getInfoTypeClient(JmxUser jmxUser, String instanceId, String treePath) {
		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, instanceId, treePath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "getInfoTypeClient", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (InfoTypeClient) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	public static ScenarioNode getLiveTreeInfo(JmxUser jmxUser, ScenarioNode scenarioNode) {
//
//		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();
//
//		Object[] paramList = { jmxUser, scenarioNode};
//		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "com.likya.tlossw.model.tree.ScenarioNode"};
//		Object o;
//
//		try {
//			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
//			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "getLiveTreeInfo", paramList, signature);
//			TEJmxMpClient.disconnect(jmxConnector);
//			return (ScenarioNode) o;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	public static TlosSpaceWideNode getLiveTreeInfo(JmxUser jmxUser, TlosSpaceWideNode tlosSpaceWideNode) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, tlosSpaceWideNode};
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "com.likya.tlossw.model.tree.TlosSpaceWideNode"};

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			Object o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + LJSTIP), "getLiveTreeInfo", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			
			TlosSpaceWideNode tmp = (TlosSpaceWideNode) o;
			
			return tmp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("getLiveTreeInfo NULL olmamali !! ");
		return null;
	}
	/**
	 * Management Functions
	 */
	public static void recover(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "recover", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	public static void shiftSolstice(JmxUser jmxUser, boolean backupReports) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, backupReports };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "boolean" };

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "shiftSolstice", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	public static void startOver(JmxUser jmxUser, boolean backupReports) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, backupReports };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "boolean" };

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "startOver", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	public static WebSpaceWideRegistery retrieveWebSpaceWideRegistery(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();
		
		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "retrieveSpaceWideRegistery", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (WebSpaceWideRegistery) o;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	public static void forceCpcStart(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "forceCpcStart", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	
	/**
	 * Live Resource Operations
	 */
	
	//Web ekranindaki kaynak listesi agaci render edilmeden once guncel data isteniyor
	/**
	 * Sunucudan kullanilabilir makine bilgilerini istiyor
	 * 
	 * @param jmxUser Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param tlosSpaceWideNode Web ekranindaki agacta acik olan makine bilgileri
	 * @return Sunucudan aldigi kullanilabilir makine bilgilerini donuyor
	 */
	public static TlosSWResourceNode getLiveResourceTreeInfo(JmxUser jmxUser, TlosSWResourceNode tlosSpaceWideNode) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, tlosSpaceWideNode};
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "com.likya.tlossw.model.tree.resource.TlosSWResourceNode"};
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "getLiveResourceTreeInfo", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (TlosSWResourceNode) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//Web ekranindaki kaynak listesi agacinda herhangi bir Tlos Agent secildiginde buraya gelip agent bilgilerini aliyor
	/**
	 * Sunucudan Tlos Agent bilgilerini istiyor
	 * 
	 * @param jmxUser Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param tlosAgentId Bilgileri istenen Tlos Agent'in id numarasi
	 * @return Sunucudan aldigi Tlos Agent bilgilerini donuyor
	 */
	public static TlosAgentInfoTypeClient retrieveTlosAgentInfo(JmxUser jmxUser, int tlosAgentId) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, tlosAgentId };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "int" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "retrieveTlosAgentInfo", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (TlosAgentInfoTypeClient) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//Agent bilgilerinden sonra da o agentta calisan job bilgilerini aliyor
	/**
	 * Sunucudan Tlos Agent'ta calisan job bilgilerini istiyor
	 * 
	 * @param jmxUser Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param tlosAgentId Bilgileri istenen Tlos Agent'in id numarasi
	 * @return Sunucudan aldigi job bilgilerini donuyor
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<JobInfoTypeClient> getAgentsJobList(JmxUser jmxUser, int tlosAgentId, boolean transformToLocalTime) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, tlosAgentId, transformToLocalTime };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "int", "java.lang.Boolean" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "getAgentsJobList", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<JobInfoTypeClient>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Sunucuya ilgili Tlos Agent'i devre disi birakma istegi gonderiyor
	 * 
	 * @param jmxUser Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param tlosAgentId Tlos Agent'in id numarasi
	 * @param isForced Hemen devre disi birakma parametresi. Tlos Agent'ta calisan islerin bitmesi beklenecekse false beklenmeyecekse true veriliyor
	 */
	public static void deactivateTlosAgent(JmxUser jmxUser, int tlosAgentId, boolean isForced) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, tlosAgentId, new Boolean(isForced) };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "int", "java.lang.Boolean" };

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "deactivateTlosAgent", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	/**
	 * Sunucuya ilgili Tlos Agent'i devreye alma istegi gonderiyor
	 * 
	 * @param jmxUser Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param tlosAgentId Tlos Agent'in id numarasi
	 */
	public static void activateTlosAgent(JmxUser jmxUser, int tlosAgentId) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, tlosAgentId };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "int" };

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "activateTlosAgent", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	/**
	 * Sunucudan agent listesini istiyor
	 * 
	 * @param jmxUser Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @return Sunucudan aldigi agent listesini donuyor
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<SWAgent> getAgentList(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "getAgentList", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<SWAgent>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Senaryo agacini, gun donumunun gelmesini beklemeden o anda baslatmasi icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 */
	public static void restartScenarioTree(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PMI), "restartScenarioTree", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	/**
	 * O gunku instance'lar icerisinde joblari bitmemis instance olup olmadigini sunucuya soruyor
	 * 
	 * @param jmxUser Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @return calisan instance varsa true yoksa false donuyor
	 */
	public static boolean runningInstanceExists(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "runningInstanceExists", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return ((Boolean) o).booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}
	
	/**
	 * Sistemde kayıtlı olan kaynakların bilgilerini sunucudan istiyor
	 * 
	 * @param jmxUser Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<ResourceInfoTypeClient> getResourceInfoTypeClientList(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "getResourceInfoTypeClientList", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<ResourceInfoTypeClient>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sunucudan ilgili kaynaktaki Tlos Agent listesini istiyor
	 * 
	 * @param jmxUser Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param resourceName Kaynak adı
	 * @return Tlos Agent listesi
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<TlosAgentInfoTypeClient> getTlosAgentInfoTypeClientList(JmxUser jmxUser, String resourceName) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, resourceName };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=" + PIP), "getTlosAgentInfoTypeClientList", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<TlosAgentInfoTypeClient>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}

package com.likya.tlossw.agent.test;

import org.apache.xmlbeans.XmlException;

import com.likya.tlos.model.xmlbeans.agent.RxMessageDocument.RxMessage;
import com.likya.tlos.model.xmlbeans.config.JmxParamsDocument;
import com.likya.tlos.model.xmlbeans.config.JmxParamsDocument.JmxParams;
import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument;
import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument.TlosConfigInfo;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.GlobalStateDefinitionDocument;
import com.likya.tlos.model.xmlbeans.state.GlobalStateDefinitionDocument.GlobalStateDefinition;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.agentclient.TSWAgentJmxClient;
import com.likya.tlossw.jmx.JMXTLSServer;
import com.likya.tlossw.model.auth.AppUser;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.utils.FileUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.XmlUtils;

public class TestAgent {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		/**
		 * Start agent listener
		 */
		
		startJmxTLSServer();
		
		// String globalStates =
		// FileUtils.readFile("/Users/serkan/Documents/workspace/TlosSWAgentTester/globalStates.xml").toString();
		// GlobalStateDefinition globalStateDefinition =
		// retrieveGlobalStates(globalStates);
		// System.out.println(globalStateDefinition.xmlText());

		String jobPropertiesFieName = "/Users/serkan/Documents/workspace/TlosSWAgentTester/normalJobProperties.xml";
		// String jobPropertiesFieName =
		// "D:\\dev\\likyateknoloji\\likyaWorkspace\\TlosSWAgentTester\\jobPropeties.xml";

		String jobPropertiesStr = FileUtils.readFile(jobPropertiesFieName).toString();

		JobProperties jobProperties = null;
		try {
			jobProperties = JobPropertiesDocument.Factory.parse(jobPropertiesStr).getJobProperties();
		} catch (XmlException e) {
			e.printStackTrace();
		}

		jobProperties.setAgentId(12);
		
		String rxMessageKey = getTransferedJobKey(jobProperties.getAgentId(), jobProperties.getID(), jobProperties.getLSIDateTime());
		
		RxMessage rxMessage = XmlUtils.generateRxMessage(jobProperties, rxMessageKey);

		AppUser appUser = new AppUser();
		appUser.setUsername("jmxuser");
		appUser.setPassword("jmxpaswd");
		JmxUser jmxUser = new JmxUser(appUser);

		// String agentXmlFileName =
		// "D:\\dev\\likyateknoloji\\likyaWorkspace\\TlosSWAgentTester\\agent.xml";
		String agentXmlFileName = "/Users/serkan/Documents/workspace/TlosSWAgentTester/agent.xml";

		String agentXml = FileUtils.readFile(agentXmlFileName).toString();

		jmxUser.setSwAgentXML(agentXml);

		boolean transferSuccess = TSWAgentJmxClient.jobHandle("Serkan-MacBook-Pro.local", 5556, XmlUtils.getRxMessageXML(rxMessage), jmxUser);

		if (!transferSuccess) {
			System.out.println(transferSuccess);
		}

	}
	
	public static String getTransferedJobKey(int agentId, String jobKey, String LSIDateTime) {

		String transferedJobKey = "instance01|spc01|"+ jobKey + "|" + agentId + "|" + "12.02.2012" ;

		return transferedJobKey;
	}

	protected static void startJmxTLSServer() {
		
		SpaceWideRegistry spaceWideRegistry = SpaceWideRegistry.getInstance();

		TlosConfigInfoDocument tlosConfigInfoDocument = TlosConfigInfoDocument.Factory.newInstance();
		tlosConfigInfoDocument.addNewTlosConfigInfo();

		TlosConfigInfo tlosConfigInfo = tlosConfigInfoDocument.getTlosConfigInfo();
		
		JmxParamsDocument jmxParamsDocument = JmxParamsDocument.Factory.newInstance();
		jmxParamsDocument.addNewJmxParams();
		
		JmxParams jmxParams = jmxParamsDocument.getJmxParams();
		
		jmxParams.setKeyStore("likyaKeystore");
		jmxParams.setTrustStore("likyaKeystore");
		jmxParams.setPassword("likya1!+");
		
		tlosConfigInfo.setJmxParams(jmxParams);
		
		spaceWideRegistry.setTlosSWConfigInfo(tlosConfigInfo);
		
		TlosSpaceWide.setSpaceWideRegistry(spaceWideRegistry);
		
		JMXTLSServer.initialize(spaceWideRegistry);
	}

	public static GlobalStateDefinition retrieveGlobalStates(String globalStates) {

		GlobalStateDefinition globalStateDefinition = null;

		try {
			globalStateDefinition = GlobalStateDefinitionDocument.Factory.parse(globalStates.toString()).getGlobalStateDefinition();
		} catch (XmlException e) {
			e.printStackTrace();
		}

		return globalStateDefinition;
	}

}

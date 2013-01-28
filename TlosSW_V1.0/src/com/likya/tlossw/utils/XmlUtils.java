package com.likya.tlossw.utils;

import java.util.HashMap;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.likya.tlos.model.xmlbeans.agent.RxMessageBodyTypeDocument.RxMessageBodyType;
import com.likya.tlos.model.xmlbeans.agent.RxMessageDocument.RxMessage;
import com.likya.tlos.model.xmlbeans.agent.RxMessageTypeEnumerationDocument.RxMessageTypeEnumeration;
import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument;
import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.agent.SWAgentsDocument.SWAgents;
import com.likya.tlos.model.xmlbeans.agent.TxMessageDocument;
import com.likya.tlos.model.xmlbeans.agent.TxMessageDocument.TxMessage;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.GlobalStateDefinitionDocument;
import com.likya.tlos.model.xmlbeans.state.GlobalStateDefinitionDocument.GlobalStateDefinition;
import com.likya.tlos.model.xmlbeans.state.ReturnCodeListDocument.ReturnCodeList;
import com.likya.tlos.model.xmlbeans.state.State;
import com.likya.tlos.model.xmlbeans.state.Status;
import com.likya.tlos.model.xmlbeans.state.SubstateDocument.Substate;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.model.engine.TxMessageIdBean;
import com.likya.tlossw.model.jmx.JmxAgentUser;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;

public class XmlUtils {

	public static String getJobPropertiesXML(JobProperties jobProperties) {
		QName qName = JobProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String jobPropertiesXML = jobProperties.xmlText(xmlOptions);

		return jobPropertiesXML;
	}

	public static String getRxMessageXML(RxMessage rxMessage) {
		QName qName = RxMessage.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String rxMessageXML = rxMessage.xmlText(xmlOptions);

		return rxMessageXML;
	}

	public static String getGlobalStateDefinitionsXML(GlobalStateDefinition globalStateDefinition) {
		QName qName = GlobalStateDefinition.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String globalStateDefinitionXML = globalStateDefinition.xmlText(xmlOptions);

		return globalStateDefinitionXML;
	}

	public static HashMap<String, SWAgent> generateSWAgentCache(SWAgents swAgents) {
		HashMap<String, SWAgent> swAgentsCache = new HashMap<String, SWAgent>();

		for (SWAgent swAgent : swAgents.getSWAgentArray()) {
			swAgentsCache.put(swAgent.getId() + "", swAgent);
		}

		return swAgentsCache;
	}

	public static String getSWAgentXML(SWAgent swAgent) {
		QName qName = SWAgent.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String swAgentXML = swAgent.xmlText(xmlOptions);

		return swAgentXML;
	}

	public static String getResourceXML(com.likya.tlos.model.xmlbeans.resourceextdefs.ResourceDocument.Resource resource) {
		QName qName = com.likya.tlos.model.xmlbeans.resourceextdefs.ResourceDocument.Resource.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String resourceXML = resource.xmlText(xmlOptions);

		return resourceXML;
	}

	public static JmxAgentUser getJmxAgentUser(String swAgentXML) {
		return new JmxAgentUser(swAgentXML);
	}

	public static JmxAgentUser getJmxAgentUser(SWAgent swAgent) {
		String swAgentXML = getSWAgentXML(swAgent);
		return new JmxAgentUser(swAgentXML);
	}

	public static SWAgent convertToSwAgent(String swAgentXML) {
		SWAgent swAgent = null;

		try {
			swAgent = SWAgentDocument.Factory.parse(swAgentXML).getSWAgent();
		} catch (XmlException e) {
			e.printStackTrace();
		}

		return swAgent;
	}

	public static GlobalStateDefinition convertToGlobalStateDefinitions(String globalStateDefinitionXML) {
		GlobalStateDefinition globalStateDefinition = null;

		try {
			globalStateDefinition = GlobalStateDefinitionDocument.Factory.parse(globalStateDefinitionXML).getGlobalStateDefinition();
		} catch (XmlException e) {
			e.printStackTrace();
		}

		return globalStateDefinition;
	}

	public static GlobalStateDefinition copyGlobalStateDefinitionsXML(GlobalStateDefinition globalStateDefinition) {
		QName qName = GlobalStateDefinition.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String globalStateDefinitionXML = globalStateDefinition.xmlText(xmlOptions);

		return convertToGlobalStateDefinitions(globalStateDefinitionXML);
	}

	public static String getGlobalStateDefinitionsXML(JmxAgentUser jmxAgentUser) {

		String globalStateDefinitionsXML = null;
		// SWAgent agent = convertToSwAgent(jmxUser.getSwAgentXML());
		// SWAgent swAgent =
		// TlosSpaceWide.getSpaceWideRegistry().getHeartBeatListenerRef().getSwAgentsCache().get(agent.getIpAddress()+"."+agent.getJmxPort());
		SWAgent swAgent = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference().getSwAgentsCache().get(jmxAgentUser.getAgentId() + "");
		GlobalStateDefinition globalStateDefinition = GlobalStateDefinition.Factory.newInstance();
		globalStateDefinition = copyGlobalStateDefinitionsXML(TlosSpaceWide.getSpaceWideRegistry().getGlobalStateDefinition());

		for (State state : globalStateDefinition.getGlobalStateArray()) {
			for (Substate substate : state.getSubstateArray()) {
				for (Status status : substate.getSubStateStatusesArray()) {
					for (ReturnCodeList returnCodeList : status.getReturnCodeListArray()) {

						if (!returnCodeList.getOsType().equals(swAgent.getOsType())) {
							XmlCursor xmlCursor = returnCodeList.newCursor();
							xmlCursor.removeXml();
						}

					}
				}
			}
		}

		globalStateDefinitionsXML = getGlobalStateDefinitionsXML(globalStateDefinition);

		return globalStateDefinitionsXML;
	}

	public static String getAgentKey(String ipAddress, short port) {

		return ipAddress + "." + port;

	}

	public static RxMessage generateRxMessage(JobProperties job, String id) {
		RxMessage rxMessage = RxMessage.Factory.newInstance();
		rxMessage.setId(id);
		rxMessage.setRxMessageTypeEnumeration(RxMessageTypeEnumeration.JOB);
		RxMessageBodyType rxMessageBodyType = RxMessageBodyType.Factory.newInstance();
		rxMessageBodyType.setJobProperties(job);
		rxMessage.setRxMessageBodyType(rxMessageBodyType);

		return rxMessage;
	}

	public static TxMessage convertToTxMessage(String txMessageXML) {
		TxMessage txMessage = null;

		try {
			txMessage = TxMessageDocument.Factory.parse(txMessageXML).getTxMessage();
		} catch (XmlException e) {
			e.printStackTrace();
		}

		return txMessage;
	}

	public static TxMessageIdBean tokenizeTxIds(String txMessageId) {
		String delimiter = "\\|";
		TxMessageIdBean txMessageIdBean = new TxMessageIdBean();
		String[] temp;

		temp = txMessageId.split(delimiter);

		txMessageIdBean.setInstanceId(temp[0]);
		txMessageIdBean.setSpcId(temp[1]);
		txMessageIdBean.setJobKey(temp[2]);
		txMessageIdBean.setAgentId(Integer.parseInt(temp[3]));
		txMessageIdBean.setLSIDateTime(temp[4]);

		return txMessageIdBean;
	}

	/*
	 * public static LiveStateInfo generateTransferLiveStateInfo() {
	 * LiveStateInfo liveStateInfo = LiveStateInfo.Factory.newInstance();
	 * liveStateInfo.setStateName(StateName.PENDING);
	 * liveStateInfo.setSubstateName(SubstateName.TRANSFERED);
	 * 
	 * return liveStateInfo; }
	 */
}

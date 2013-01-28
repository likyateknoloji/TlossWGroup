package com.likya.tlosswagent.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.likya.tlos.model.xmlbeans.agent.RxMessageDocument.RxMessage;
import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument;
import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.agent.TxMessageDocument.TxMessage;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.resourceextdefs.ResourceDocument.Resource;
import com.likya.tlos.model.xmlbeans.state.GlobalStateDefinitionDocument.GlobalStateDefinition;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlosswagent.TlosSWAgent;


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
	
	public static String getTxMessageXML(TxMessage txMessage) {
		QName qName = TxMessage.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String txMessageXML = txMessage.xmlText(xmlOptions);

		return txMessageXML;
	}
	
	public static String getGlobalStateDefinitionsXML(GlobalStateDefinition globalStateDefinition) {
		QName qName = GlobalStateDefinition.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String globalStateDefinitionXML = globalStateDefinition.xmlText(xmlOptions);

		return globalStateDefinitionXML;
	}
	
	public static String getSWAgentXML(SWAgent swAgent) {
		QName qName = SWAgent.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String swAgentXML = swAgent.xmlText(xmlOptions);

		return swAgentXML;
	}
	
	public static SWAgent initSwAgent() {
		
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}

		SWAgent swAgent = SWAgent.Factory.newInstance();
		Resource resource = Resource.Factory.newInstance();
		resource.setStringValue(addr.getHostName());
		swAgent.setResource(resource);
		swAgent.setJmxPort(TlosSWAgent.getSwAgentRegistry().getAgentConfigInfo().getSettings().getJmxTlsPort().getPortNumber());
		
		return swAgent;
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

}

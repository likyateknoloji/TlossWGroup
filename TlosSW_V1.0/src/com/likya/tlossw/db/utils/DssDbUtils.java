package com.likya.tlossw.db.utils;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument;
import com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.functionpass.JobPropFuncPassDocument.JobPropFuncPass;
import com.likya.tlos.model.xmlbeans.functionpass.OSystemDocument.OSystem;
import com.likya.tlos.model.xmlbeans.functionpass.ResourceRequirementDocument.ResourceRequirement;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceAgentListDocument;
import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceAgentListDocument.ResourceAgentList;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;



public class DssDbUtils {
	
	private static final String standartNameSpaceDeclaritions = "declare namespace dat  = \"http://www.likyateknoloji.com/XML_data_types\";  "
			   + "declare namespace com  = \"http://www.likyateknoloji.com/XML_common_types\";  "
			   + "declare namespace cal  = \"http://www.likyateknoloji.com/XML_calendar_types\";  "
			   + "declare namespace agnt = \"http://www.likyateknoloji.com/XML_agent_types\";  "	       				   
			   + "declare namespace xsi  = \"http://www.w3.org/2001/XMLSchema-instance\";  "
			   + "declare namespace fn   = \"http://www.w3.org/2005/xpath-functions\";  "
			   + "declare namespace lrns   = \"www.likyateknoloji.com/XML_SWResourceNS_types\";  "
			   + "declare namespace nrp   = \"www.likyateknoloji.com/XML_nrpe_types\";  "	       				   
		       + "declare namespace res = \"http://www.likyateknoloji.com/resource-extension-defs\"; ";
	
	
	public static ResourceAgentList swFindResourcesForAJob(JobProperties jobProperties){

		JobPropFuncPass jobPropFuncPass = JobPropFuncPass.Factory.newInstance();
		String OSystemType = jobProperties.getBaseJobInfos().getOSystem().toString();
		
		jobPropFuncPass.setOSystem(OSystem.Enum.forString(OSystemType));
		jobPropFuncPass.setID(jobProperties.getID());
		if(jobPropFuncPass.getSLAId()>0) jobPropFuncPass.setSLAId(jobProperties.getAdvancedJobInfos().getSLAId());
		
		if( jobPropFuncPass.getResourceRequirement() != null ) jobPropFuncPass.setResourceRequirement((ResourceRequirement) jobProperties.getAdvancedJobInfos().getResourceRequirement());
		
		QName qName = JobPropFuncPass.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String jobPropFuncPassXML = jobPropFuncPass.xmlText(xmlOptions);
		
		ResourceAgentList resourceAgentList = ResourceAgentList.Factory.newInstance();
		//System.out.println("  > jobPropFuncPassXML : " + jobPropFuncPassXML);
		String xQueryStr = "xquery version \"1.0\";"
		                   + "import module namespace dss=\"http://tlos.dss.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleDSSOperations.xquery\";" 
		                   + standartNameSpaceDeclaritions
		                   + "dss:SWFindResourcesForAJob("+ jobPropFuncPassXML  +", fn:current-dateTime())";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return null;
		}
		
		try {
			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();
				try {
					resourceAgentList = ResourceAgentListDocument.Factory.parse(xmlContent).getResourceAgentList();
				}catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}
		
		return resourceAgentList;
	}

	public static Alarm swFindAlarms(String jobId, int userID, int agentId, LiveStateInfo liveStateInfo) throws XmlException{

		QName qName = LiveStateInfo.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String liveStateInfoXML = liveStateInfo.xmlText(xmlOptions);
		
		Alarm alarm= Alarm.Factory.newInstance();
		
		String xQueryStr = "xquery version \"1.0\";"
		                   + "import module namespace lk = \"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAlarmOperations.xquery\";" 
		                   + standartNameSpaceDeclaritions
	       				   + "declare namespace state-types=\"http://www.likyateknoloji.com/state-types\";" 
					       + "lk:SWFindAlarms("+ "'" + jobId + "', " + userID + ", " + agentId + ", " + liveStateInfoXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			System.out.println("  > Alarm sorgusunda Hata(kod : 1123 ! " + xQueryStr);
			e2.printStackTrace();
			return null;
		}
		
		try {
			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();
				if (xmlContent=="true") System.out.println("  > Alarm uredi !");
				alarm = AlarmDocument.Factory.parse(xmlContent).getAlarm();
			}
		} catch (XMLDBException e) {
			System.out.println("  > Alarm sorgusunda Hata(kod : 1124 ! " + xQueryStr);
			e.printStackTrace();
			return null;
		}
		
		return alarm;
	}


}

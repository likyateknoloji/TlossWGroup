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
import com.likya.tlossw.utils.ConstantDefinitions;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;



public class DssDbUtils {
	
	private static final String standartNameSpaceDeclaritions = ConstantDefinitions.decNsDat + ConstantDefinitions.decNsCom + ConstantDefinitions.decNsCal
			   + ConstantDefinitions.decNsAgnt + ConstantDefinitions.decNsXsi + ConstantDefinitions.decNsFn 
			   + ConstantDefinitions.decNsLrns + ConstantDefinitions.decNsNrp + ConstantDefinitions.decNsRes;

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
		
		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.dssNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleDSSOperations.xquery\";" + 
			standartNameSpaceDeclaritions + "dss:SWFindResourcesForAJob("+ jobPropFuncPassXML  +", fn:current-dateTime())";

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

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

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.lkNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleAlarmOperations.xquery\";" + 
				standartNameSpaceDeclaritions + ConstantDefinitions.decNsSt + "lk:SWFindAlarms("+ "'" + jobId + "', " + userID + ", " + agentId + ", " + liveStateInfoXML + ")";

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

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

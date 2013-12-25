package com.likya.tlossw.db.utils;

import java.util.ArrayList;

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
import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument.TlosConfigInfo;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.functionpass.JobPropFuncPassDocument.JobPropFuncPass;
import com.likya.tlos.model.xmlbeans.functionpass.OSystemDocument.OSystem;
import com.likya.tlos.model.xmlbeans.functionpass.ResourceRequirementDocument.ResourceRequirement;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceAgentListDocument;
import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceAgentListDocument.ResourceAgentList;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;



public class DssDbUtils extends DBBase {
	
	private static final String standartNameSpaceDeclaritions = CommonConstantDefinitions.decNsDat + CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsCal
			   + CommonConstantDefinitions.decNsAgnt + CommonConstantDefinitions.decNsXsi + CommonConstantDefinitions.decNsFn 
			   + CommonConstantDefinitions.decNsLrns + CommonConstantDefinitions.decNsNrp + CommonConstantDefinitions.decNsRes;

	public static ResourceAgentList swFindResourcesForAJob(JobProperties jobProperties){

		JobPropFuncPass jobPropFuncPass = JobPropFuncPass.Factory.newInstance();
		
		String OSystemType = jobProperties.getBaseJobInfos().getOSystem().toString();
		
		jobPropFuncPass.setOSystem(OSystem.Enum.forString(OSystemType));
		jobPropFuncPass.setID(jobProperties.getID());
		
		TlosConfigInfo tlosConfigInfo = TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo();
		
		jobPropFuncPass.setUseSLA( tlosConfigInfo.getSettings().getUseSLA().getUse() );
		jobPropFuncPass.setUseMonitoringData( tlosConfigInfo.getMonitoringAgentParams().getUse() );

		jobPropFuncPass.setPlanId( TlosSpaceWide.getSpaceWideRegistry().getTlosProcessData().getPlanId() );
		
		//TODO Server in timezone bilgisini DB ye gondermemiz gerekiyor.
		
		if(jobProperties.getAdvancedJobInfos().getSLAId()>0) {
			jobPropFuncPass.setSLAId(jobProperties.getAdvancedJobInfos().getSLAId());
		}
		
		if( jobPropFuncPass.getResourceRequirement() != null ) {
			jobPropFuncPass.setResourceRequirement((ResourceRequirement) jobProperties.getAdvancedJobInfos().getResourceRequirement());
		}
		
		QName qName = JobPropFuncPass.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String jobPropFuncPassXML = jobPropFuncPass.xmlText(xmlOptions);
		
		ResourceAgentList resourceAgentList = ResourceAgentList.Factory.newInstance();
		//System.out.println("  > jobPropFuncPassXML : " + jobPropFuncPassXML);
		
		String xQueryStr = dssFunctionConstructor("dss:SWFindResourcesForAJob", jobPropFuncPassXML, " fn:current-dateTime() ");
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			resourceAgentList = ((ResourceAgentListDocument) currentObject).getResourceAgentList();
		}

		return resourceAgentList;
	}

	public static ResourceAgentList swFindResourcesForAJobOld(JobProperties jobProperties){

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

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.dssNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleDSSOperations.xquery\";" + 
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

		String xQueryStr = alarmFunctionConstructor("lk:SWFindAlarms", "xs:string(" + jobId + ")", "" + userID, "" + agentId, liveStateInfoXML);
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			alarm = ((AlarmDocument) currentObject).getAlarm();
		}

		return alarm;
	}

	public static Alarm swFindAlarmsOld(String jobId, int userID, int agentId, LiveStateInfo liveStateInfo) throws XmlException{

		QName qName = LiveStateInfo.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String liveStateInfoXML = liveStateInfo.xmlText(xmlOptions);
		
		Alarm alarm= Alarm.Factory.newInstance();

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		/*String docAlarmFile = spaceWideRegistry.getXmlsUrl() + CommonConstantDefinitions.ALARM_DATA;
		String docHistoryFile = spaceWideRegistry.getXmlsUrl() + CommonConstantDefinitions.ALARM_HISTORY_DATA;
		String docDataFile = spaceWideRegistry.getXmlsUrl() + CommonConstantDefinitions.JOB_DEFINITION_DATA;
		String seqDataFile = spaceWideRegistry.getXmlsUrl() + CommonConstantDefinitions.SEQUENCE_DATA;
		*/
		String metaData = spaceWideRegistry.getXmlsUrl() + CommonConstantDefinitions.META_DATA;

		String funcDef = "lk:SWFindAlarms(\""+ metaData + "\", '" + jobId + "', " + userID + ", " + agentId + ", " + liveStateInfoXML + ")";
		
		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleAlarmOperations.xquery\";" + 
				standartNameSpaceDeclaritions + CommonConstantDefinitions.decNsSt + funcDef;

		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
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

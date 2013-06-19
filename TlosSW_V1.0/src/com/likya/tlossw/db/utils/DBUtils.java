/*
 * Tlos SW 1.0
 * com.likya.tlos.utils : DBUtils.java
 * @author Serkan Taş
 * Tarih : 02.Nis.2010 16:00:06
 */

package com.likya.tlossw.db.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import com.likya.tlos.model.xmlbeans.agent.SWAgentsDocument;
import com.likya.tlos.model.xmlbeans.agent.SWAgentsDocument.SWAgents;
import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument;
import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument.TlosConfigInfo;
import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.data.JobListDocument.JobList;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.JsPlannedTimeDocument.JsPlannedTime;
import com.likya.tlos.model.xmlbeans.data.StartTimeDocument.StartTime;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.state.GlobalStateDefinitionDocument;
import com.likya.tlos.model.xmlbeans.state.GlobalStateDefinitionDocument.GlobalStateDefinition;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.user.PersonDocument;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.cpc.model.InstanceInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.exceptions.TlosFatalException;
import com.likya.tlossw.utils.ConstantDefinitions;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.PersistenceUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.XmlUtils;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;

public class DBUtils {

	private static TlosProcessData getTlosSolsticeDataXml() {

		TlosProcessData tlosProcessData = TlosProcessData.Factory.newInstance();
		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.hsNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleDailyOperations.xquery\";" + 
				ConstantDefinitions.decNsCom + ConstantDefinitions.decNsDat + "hs:getSolsticeJobsAndScenarios()";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();
				try {
					tlosProcessData = TlosProcessDataDocument.Factory.parse(xmlContent).getTlosProcessData();
				} catch (XmlException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}

				System.out.println();
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return tlosProcessData;

	}

	public static TlosProcessData getSolsticeShit() throws TlosFatalException {

		TlosProcessData tlosProcessData = getTlosSolsticeDataXml();
		/**
		 * Aşağıdaki kontroller, aslında bir nevi validasyon işlevi görüyor.
		 * Bağımlılık kuralını alıp, kural içinde tanımlı joblar, bağımlılık
		 * listesinde var mı yok mu kotrol ediliyor. Eğer yok ise, hata verilip,
		 * uygulama sonlandırılıyor.
		 */
		tlosProcessData.validate();

		JobList jobList = tlosProcessData.getJobList();
		if (jobList != null && jobList.getJobPropertiesArray().length != 0) {
			ArrayIterator jobPropertiesListIterator = new ArrayIterator(jobList.getJobPropertiesArray());
			while (jobPropertiesListIterator.hasNext()) {
				JobProperties jobPropertiesType = (JobProperties) jobPropertiesListIterator.next();
				DependencyList dependentJobList = (jobPropertiesType).getDependencyList();
				if (dependentJobList != null) {
					String dependencyExpression = dependentJobList.getDependencyExpression().trim();
					ArrayIterator dependentJobListIterator = new ArrayIterator(dependentJobList.getItemArray());
					while (dependentJobListIterator.hasNext()) {
						Item item = (Item) dependentJobListIterator.next();
						// Eğer, expression içinde bu job yok ise, tanımda hata
						// vardır !
						if (dependencyExpression.indexOf(item.getDependencyID()) < 0) {
							SpaceWideRegistry.getGlobalLogger().error("[" + item.getJsName() + "], bağımlılık kuralı olan [" + dependencyExpression + "] içinde bulunamadı !");
							SpaceWideRegistry.getGlobalLogger().error("Hatalı tanımlama ! Uygulama sona eriyor !");
							throw new TlosFatalException();
						}
					}
				}
			}
		}

		return tlosProcessData;
	}

	public static void backupCurrentStatusOfSpcsAndJobs(SpaceWideRegistry spaceWideRegistry) {

		for (String instanceId : spaceWideRegistry.getInstanceLookupTable().keySet()) {
			InstanceInfoType instanceInfoType = spaceWideRegistry.getInstanceLookupTable().get(instanceId);

			HashMap<String, SpcInfoType> spcLookupTable = instanceInfoType.getSpcLookupTable();

			for (String spcId : spcLookupTable.keySet()) {
				HashMap<String, Job> jobQueue = PersistenceUtils.recoverTempFiles(spcId);
				Iterator<Job> jobsIterator = jobQueue.values().iterator();

				while (jobsIterator.hasNext()) {
					Job scheduledJob = jobsIterator.next();
					JobProperties myJobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();
					updateJob("tlosSWDailyScenarios10.xml", myJobProperties, ParsingUtils.getJobXPath(spcId));
				}
			}
		}
		return;
	}

	public static JobProperties updateJob(String documentName, JobProperties jobProperties, String jobPath) {

		QName qName = JobProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String jobPropetiesXML = jobProperties.xmlText(xmlOptions);

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.hsNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleScenarioOperations.xquery\";" + 
				ConstantDefinitions.decNsCom + ConstantDefinitions.decNsDat + "hs:updateLiveJobLock(" + "xs:string(\"" + documentName + "\")" + "," + jobPropetiesXML + "," + jobPath + ")";

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			@SuppressWarnings("unused")
			ResourceSet result = service.query(xQueryStr);
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return jobProperties;
	}

	public static JobProperties updateFirstJob(String documentName, JobProperties jobProperties, String jobPath) {

		QName qName = JobProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String jobPropetiesXML = jobProperties.xmlText(xmlOptions);

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.hsNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleScenarioOperations.xquery\";" + 
				ConstantDefinitions.decNsCom + ConstantDefinitions.decNsDat + "hs:updateFirstLiveJobLock(" + "xs:string(\"" + documentName + "\")" + "," + jobPropetiesXML + "," + jobPath + "/dat:jobProperties[@ID='" + jobProperties.getID() + "'])";

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			@SuppressWarnings("unused")
			ResourceSet result = service.query(xQueryStr);
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return jobProperties;
	}

	// Kullanilmiyor
	public static JobProperties insertJobInTheBeginning(String documentName, JobProperties jobProperties, String jobPath) {

		QName qName = JobProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String jobPropetiesXML = jobProperties.xmlText(xmlOptions);

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.hsNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleScenarioOperations.xquery\";" + 
				ConstantDefinitions.decNsCom + ConstantDefinitions.decNsDat + "hs:insertJobInTheBeginningLock(" + "xs:string(\"" + documentName + "\")" + "," + jobPropetiesXML + "," + jobPath + ")";

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			@SuppressWarnings("unused")
			ResourceSet result = service.query(xQueryStr);
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return jobProperties;
	}

	public static TlosProcessData getTlosDailyData(int scenarioId, int planId) throws TlosFatalException {

		TlosProcessData tlosProcessData = getTlosDailyDataXml(scenarioId, planId);

		/**
		 * Aşağıdaki kontroller, aslında bir nevi validasyon işlevi görüyor.
		 * Bağımlılık kuralını alıp, kural içinde tanımlı joblar, bağımlılık
		 * listesinde var mı yok mu kontrol ediliyor. Eğer yok ise, hata verilip,
		 * uygulama sonlandırılıyor.
		 */

		tlosProcessData.validate();

		JobList jobList = tlosProcessData.getJobList();
		if (jobList != null && jobList.getJobPropertiesArray().length != 0) {
			ArrayIterator jobPropertiesListIterator = new ArrayIterator(jobList.getJobPropertiesArray());
			while (jobPropertiesListIterator.hasNext()) {
				JobProperties jobPropertiesType = (JobProperties) jobPropertiesListIterator.next();
				DependencyList dependentJobList = (jobPropertiesType).getDependencyList();
				if (dependentJobList != null) {
					String dependencyExpression = dependentJobList.getDependencyExpression().trim();
					ArrayIterator dependentJobListIterator = new ArrayIterator(dependentJobList.getItemArray());
					while (dependentJobListIterator.hasNext()) {
						Item item = (Item) dependentJobListIterator.next();
						// Eğer, expression içinde bu job yok ise, tanımda hata
						// vardır !
						if (dependencyExpression.indexOf(item.getDependencyID()) < 0) {
							SpaceWideRegistry.getGlobalLogger().error("[" + item.getDependencyID() + "], bağımlılık kuralı olan [" + dependencyExpression + "] içinde bulunamadı !");
							SpaceWideRegistry.getGlobalLogger().error("Hatalı tanımlama ! Uygulama sona eriyor !");
							throw new TlosFatalException();
						}
					}

					// baska islere bagimli islerde baslangic zamani girilmesi zorunlu olmadigi icin bagli oldugu islerden birinden bu bilgiyi kopyaliyoruz
					if (jobPropertiesType.getTimeManagement().getJsPlannedTime() == null || jobPropertiesType.getTimeManagement().getJsPlannedTime().getStartTime() == null) {
						JsPlannedTime jsPlannedTime = JsPlannedTime.Factory.newInstance();
						jsPlannedTime.addNewStartTime();
						StartTime startTime = getStartTimeFromDependentJobs(dependentJobList, jobList.getJobPropertiesArray());

						if (startTime != null) {
							jsPlannedTime.setStartTime(startTime);
						} else {
							SpaceWideRegistry.getGlobalLogger().error("[" + jobPropertiesType.getBaseJobInfos().getJsName() + "] içinde ve bağlı olduğu işlerde planlanan başlagıç zamanı bilgisi bulunamadı !");
							SpaceWideRegistry.getGlobalLogger().error("Hatalı tanımlama ! Uygulama sona eriyor !");
							throw new TlosFatalException();
						}

						jobPropertiesType.getTimeManagement().addNewJsPlannedTime();
						jobPropertiesType.getTimeManagement().setJsPlannedTime(jsPlannedTime);
					}
				}
			}
		}

		return tlosProcessData;
	}

	private static StartTime getStartTimeFromDependentJobs(DependencyList dependentJobList, JobProperties[] jobList) {

		ArrayIterator dependentJobListIterator = new ArrayIterator(dependentJobList.getItemArray());

		while (dependentJobListIterator.hasNext()) {
			Item item = (Item) dependentJobListIterator.next();
			String jobName = item.getJsName();

			ArrayIterator jobListIterator = new ArrayIterator(jobList);

			while (jobListIterator.hasNext()) {
				JobProperties jobProperties = (JobProperties) jobListIterator.next();

				if (jobProperties.getBaseJobInfos().getJsName().equals(jobName)) {
					if (jobProperties.getTimeManagement().getJsPlannedTime() != null && jobProperties.getTimeManagement().getJsPlannedTime().getStartTime() != null) {
						return jobProperties.getTimeManagement().getJsPlannedTime().getStartTime();
					} else {
						break;
					}
				}
			}
		}

		return null;
	}

	private static TlosProcessData getTlosDailyDataXml(int scenarioId, int planId) {

		TlosProcessData tlosProcessData = TlosProcessData.Factory.newInstance();

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.hsNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleDailyOperations.xquery\";" + 
				ConstantDefinitions.decNsCom + ConstantDefinitions.decNsDat + "hs:doPlanAndSelectJobsAndScenarios(" + scenarioId + "," + planId + ")";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();
				try {
					tlosProcessData = TlosProcessDataDocument.Factory.parse(xmlContent).getTlosProcessData();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}

				System.out.println();

			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return tlosProcessData;

	}

	/**
	 * Eskiden kullanılıyordu. Tüm senaryoyu okuyordu. Sahin Kekevi
	 */

	public static TlosProcessData getTlosDataXml(String documentName) {

		TlosProcessData tlosProcessData = TlosProcessData.Factory.newInstance();

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.hsNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleScenarioOperations.xquery\";" + 
				ConstantDefinitions.decNsCom + ConstantDefinitions.decNsDat + ConstantDefinitions.decNsSt + "hs:getTlosDataXml(xs:string(\"" + spaceWideRegistry.getXmlsUrl() + documentName + "\"))";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();
				try {
					tlosProcessData = TlosProcessDataDocument.Factory.parse(xmlContent).getTlosProcessData();
				} catch (XmlException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
				System.out.println();
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return tlosProcessData;

	}

	public static GlobalStateDefinition getGlobalStateDefinitions() {

		GlobalStateDefinition globalStateDefinition = null;
		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.lkNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleStateOperations.xquery\";" + 
				ConstantDefinitions.decNsSt + "lk:getTlosGlobalStates()";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();
				try {
					globalStateDefinition = GlobalStateDefinitionDocument.Factory.parse(xmlContent).getGlobalStateDefinition();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
				System.out.println();
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return globalStateDefinition;

	}

	public static TlosConfigInfo getTlosConfig() {

		TlosConfigInfo tlosConfigInfo = TlosConfigInfo.Factory.newInstance();

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.hsNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleTlosManagementOperations.xquery\";" + 
				ConstantDefinitions.decNsCom + ConstantDefinitions.decNsCon + "hs:getTlosConfig()";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();
				try {
					tlosConfigInfo = TlosConfigInfoDocument.Factory.parse(xmlContent).getTlosConfigInfo();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
				System.out.println();
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return tlosConfigInfo;

	}

	public static ArrayList<Parameter> getTlosParameters() {

		Parameter tlosParameter = Parameter.Factory.newInstance();

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.lkNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleParameterOperations.xquery\";" + "lk:parameterList(1,10)";

		ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
		
		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();
				try {
					tlosParameter = ParameterDocument.Factory.parse(xmlContent).getParameter();
					parameterList.add(tlosParameter);

				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
				System.out.println();
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return parameterList;

	}

	public static boolean insertFreeJobToDailyXML(String documentName, String jobPropertiesXML, String runId) {

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.hsNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleScenarioOperations.xquery\";" + 
				ConstantDefinitions.decNsCom + ConstantDefinitions.decNsDat + "hs:insertFreeJobLock(" + "xs:string(\"" + documentName + "\")" + "," + jobPropertiesXML + "," + runId + ")";

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			@SuppressWarnings("unused")
			ResourceSet result = service.query(xQueryStr);
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static SWAgents initAgentList() {

		SWAgents swAgents = null;

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.lkNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleAgentOperations.xquery\";" + 
				ConstantDefinitions.decNsAgnt + "lk:getAgents()";

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
					swAgents = SWAgentsDocument.Factory.parse(xmlContent).getSWAgents();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return swAgents;
	}

	public static boolean insertJobAgentId(String documentName, String agentId, String jobId, String jobPath) {

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.hsNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleScenarioOperations.xquery\";" + 
				ConstantDefinitions.decNsCom + ConstantDefinitions.decNsDat + "hs:insertJobAgentIdLock(" + "xs:string(\"" + documentName + "\")" + "," + "'" + agentId + "'" + "," + "'" + jobId + "'" + "," + jobPath + ")";

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			@SuppressWarnings("unused")
			ResourceSet result = service.query(xQueryStr);
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean insertJob(String documentName, JobProperties jobProperties, String jobPath) {

		String jobPropertiesXML = XmlUtils.getJobPropertiesXML(jobProperties);

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.hsNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleScenarioOperations.xquery\";" + 
				ConstantDefinitions.decNsCom + ConstantDefinitions.decNsDat + "hs:insertLiveJobLock(" + "xs:string(\"" + documentName + "\")" + "," + jobPropertiesXML + "," + jobPath + ")";

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			@SuppressWarnings("unused")
			ResourceSet result = service.query(xQueryStr);
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static LiveStateInfo insertJobState(Collection collection, String documentName, LiveStateInfo liveStateInfo, String jobPath) {

		QName qName = LiveStateInfo.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String liveStateInfoXML = liveStateInfo.xmlText(xmlOptions);

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.hsNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleScenarioOperations.xquery\";" + 
				ConstantDefinitions.decNsCom + ConstantDefinitions.decNsDat + ConstantDefinitions.decNsSt + "hs:insertJobStateLock(" + "xs:string(\"" + documentName + "\")" + "," + liveStateInfoXML + "," + jobPath + ")";

		SpaceWideRegistry.getGlobalLogger().debug(" >> STATE >> " + liveStateInfo + " X " + jobPath);
		SpaceWideRegistry.getGlobalLogger().debug(" >> STATE >> " + xQueryStr);

		XPathQueryService service = null;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			SpaceWideRegistry.getGlobalLogger().debug(" >> Query Result >> " + result);
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return liveStateInfo;
	}

	public int getNextErrorId() throws XMLDBException {

		int errorId = -1;

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.sqNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleSequenceOperations.xquery\";" + "sq:getNextId(\"errorId\")";

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e) {
			e.printStackTrace();
			return -1;
		}

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			errorId = Integer.parseInt(r.getContent().toString());
		}

		return errorId;
	}

	public static Person getSubscribers(int userId, String role) throws XMLDBException {

		Person person = null;

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.hsNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleUserOperations.xquery\";" + "hs:getSubscribers(" + userId + ",'" + role + "')";

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();
			try {
				person = PersonDocument.Factory.parse(xmlContent).getPerson();
			} catch (XmlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// errorId = Integer.parseInt(r.getContent().toString());
		}
		return person;
	}

	public static boolean insertLogFileNameForJob(String documentName, JobProperties jobProperties, String jobPath, String logFileName) {

		QName qName = JobProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String jobPropertiesXML = jobProperties.xmlText(xmlOptions);

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		// TODO dailyscenarios.xml'de gerekli kisma logname yazilacak
		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.hsNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleScenarioOperations.xquery\";" + 
				ConstantDefinitions.decNsCom + ConstantDefinitions.decNsDat + "hs:??????(" + "xs:string(\"" + documentName + "\")" + "," + jobPropertiesXML + "," + jobPath + "," + logFileName + " )";

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			@SuppressWarnings("unused")
			ResourceSet result = service.query(xQueryStr);
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static FtpProperties searchFTPConnectionById(int ftpConnectionId) throws XMLDBException {

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.fcNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleFTPConnectionsOperations.xquery\";" + 
				"fc:searchFTPConnectionById(" + ftpConnectionId + ")";

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		FtpProperties ftpProperties = null;

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			try {
				ftpProperties = FtpPropertiesDocument.Factory.parse(xmlContent).getFtpProperties();

				return ftpProperties;
			} catch (XmlException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static DbProperties searchDBPropertiesById(int dbPropertiesID) throws XMLDBException {

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.dbNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleDBConnectionsOperations.xquery\";" + 
				ConstantDefinitions.decNsDbc + ConstantDefinitions.decNsCom + ConstantDefinitions.decNsSt + "db:getDbConnection(" + dbPropertiesID + ")";

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		DbProperties dbProperties = null;

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			try {
				dbProperties = DbPropertiesDocument.Factory.parse(xmlContent).getDbProperties();

				return dbProperties;
			} catch (XmlException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static DbConnectionProfile searchDBConnectionProfilesById(int dbCPID) throws XMLDBException {

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String xQueryStr = ConstantDefinitions.xQueryNsHeader + ConstantDefinitions.dbNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleDBConnectionsOperations.xquery\";" + 
				ConstantDefinitions.decNsDbc + ConstantDefinitions.decNsCom + ConstantDefinitions.decNsSt + "db:getDbCP(" + dbCPID + ")";

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		DbConnectionProfile dbConnectionProfile = null;

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			try {
				dbConnectionProfile = DbConnectionProfileDocument.Factory.parse(xmlContent).getDbConnectionProfile();

				return dbConnectionProfile;
			} catch (XmlException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

}

/*
 * Tlos SW 1.0
 * com.likya.tlos.utils : DBUtils.java
 * @author Serkan Taş
 * Tarih : 02.Nis.2010 16:00:06
 */

package com.likya.tlossw.db.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.xmlbeans.XmlOptions;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.agent.SWAgentsDocument;
import com.likya.tlos.model.xmlbeans.agent.SWAgentsDocument.SWAgents;
import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument;
import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument.TlosConfigInfo;
import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.data.JobListDocument.JobList;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument;
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
import com.likya.tlossw.core.cpc.model.PlanInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.exceptions.TlosFatalException;
import com.likya.tlossw.exceptions.XSLLoadException;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.PersistenceUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.XmlUtils;
import com.likya.tlossw.utils.transform.TransformUtils;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;

public class DBUtils extends DBBase {
	
	private static final String dbUserId = TransformUtils.toXSString("0");
	private static final String dbDocId = TransformUtils.toXSString("scenarios");
	
	public static void backupCurrentStatusOfSpcsAndJobs(SpaceWideRegistry spaceWideRegistry) {

		for (String planId : spaceWideRegistry.getPlanLookupTable().keySet()) {
			PlanInfoType planInfoType = spaceWideRegistry.getPlanLookupTable().get(planId);

			HashMap<String, SpcInfoType> spcLookupTable = planInfoType.getSpcLookupTable().getTable();

			for (String spcId : spcLookupTable.keySet()) {
				HashMap<String, Job> jobQueue = PersistenceUtils.recoverTempFiles(spcId);
				Iterator<Job> jobsIterator = jobQueue.values().iterator();

				while (jobsIterator.hasNext()) {
					Job scheduledJob = jobsIterator.next();
					JobProperties myJobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();
					updateJob(myJobProperties, ParsingUtils.getJobXPath(spcId));
				}
			}
		}
		return;
	}

	public static boolean updateJob(JobProperties jobProperties, String jobPath) {
		
		boolean returnValue = false;
		
		QName qName = JobProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String jobPropetiesXML = jobProperties.xmlText(xmlOptions);
		
		String xQueryStr = scenarioFunctionConstructor("hs:updateLiveJobLock", dbDocId, dbUserId, "true()", jobPropetiesXML, jobPath);
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			returnValue = Boolean.parseBoolean(currentObject.toString());
		}

		return returnValue;
	}

	public static boolean updateFirstJob(JobProperties jobProperties, String jobPath) {

		boolean returnValue = false;
		
		QName qName = JobProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String jobPropetiesXML = jobProperties.xmlText(xmlOptions);

		String xQueryStr = scenarioFunctionConstructor("hs:updateFirstLiveJobLock", dbDocId, dbUserId, "true()", jobPropetiesXML, jobPath + "/dat:jobProperties[@ID='" + jobProperties.getID() + "']");
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			returnValue = Boolean.parseBoolean(currentObject.toString());
		}

		return returnValue;
	}

	public static TlosProcessData getTlosDailyData(int scenarioId, int planId) throws TlosFatalException {

		TlosProcessData tlosProcessData = getTlosDailyDataXml(scenarioId, planId);
		JobProperties jobPropertiesData = getTlosJobPropertiesXml(214, planId);
		/**
		 * Aşağıdaki kontroller, aslında bir nevi validasyon işlevi görüyor.
		 * Bağımlılık kuralını alıp, kural içinde tanımlı joblar, bağımlılık
		 * listesinde var mı yok mu kontrol ediliyor. Eğer yok ise, hata verilip,
		 * uygulama sonlandırılıyor.
		 */

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
			String jobId = item.getJsId();
			
			ArrayIterator jobListIterator = new ArrayIterator(jobList);

			while (jobListIterator.hasNext()) {
				JobProperties jobProperties = (JobProperties) jobListIterator.next();

				if (jobProperties.getID().equals(jobId)) {
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

		TlosProcessData tlosProcessData = null;
		
		String xQueryStr = dailyFunctionConstructor("hs:doPlanAndSelectJobsAndScenarios", "" + scenarioId, "" + planId);
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			tlosProcessData = ((TlosProcessDataDocument) currentObject).getTlosProcessData();
		}

		return tlosProcessData;

	}

	private static JobProperties getTlosJobPropertiesXml(int jobId, int planId) {

		JobProperties jobPropertiesData = null;
		
		String xQueryStr = dailyFunctionConstructor("hs:doPlanAndSelectJob", "" + jobId, "" + planId);
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			jobPropertiesData = ((JobPropertiesDocument) currentObject).getJobProperties();
		}

		return jobPropertiesData;

	}
	
	public static GlobalStateDefinition getGlobalStateDefinitions() {

		GlobalStateDefinition globalStateDefinition = null;
		
		String xQueryStr = stateFunctionConstructor("lk:getTlosGlobalStates");
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			globalStateDefinition = ((GlobalStateDefinitionDocument) currentObject).getGlobalStateDefinition();
		}

		return globalStateDefinition;

	}

	public static TlosConfigInfo getTlosConfig() {

		TlosConfigInfo tlosConfigInfo = null;

		String xQueryStr = managementFunctionConstructor("hs:getTlosConfig");
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			tlosConfigInfo = ((TlosConfigInfoDocument) currentObject).getTlosConfigInfo();
		}
		
		return tlosConfigInfo;

	}

	public static ArrayList<Parameter> getTlosParameters() {

		Parameter tlosParameter = Parameter.Factory.newInstance();
		ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
		
		String xQueryStr = parameterFunctionConstructor("lk:parameterList", "1", "10");
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			tlosParameter = ((ParameterDocument) currentObject).getParameter();
			parameterList.add(tlosParameter);
		}

		return parameterList;

	}

	public static boolean insertFreeJobToDailyXML(String jobPropertiesXML, String runId) {

		boolean returnValue = false;
		
		String xQueryStr = scenarioFunctionConstructor("hs:insertFreeJobLock", jobPropertiesXML, runId);
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			returnValue = ((Boolean.parseBoolean(currentObject.toString())));
		}

		return returnValue;
	}

	public static SWAgents initAgentList() {
		
		SWAgents swAgents = null;
		
		String xQueryStr = agentFunctionConstructor("lk:getAgents");
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			swAgents =   ((SWAgentsDocument) currentObject).getSWAgents();
		}
		
		return swAgents;
	}

	public static boolean insertJob(JobProperties jobProperties, String jobPath) {
		
		boolean returnValue = false;
		
		String jobPropertiesXML = XmlUtils.getJobPropertiesXML(jobProperties);
		
		String xQueryStr = scenarioFunctionConstructor("hs:insertLiveJobLock", dbDocId, dbUserId, "true()", jobPropertiesXML, jobPath);
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			returnValue = ((Boolean.parseBoolean(currentObject.toString())));
		}
		
		return returnValue;
	}

	public static LiveStateInfo insertJobState(LiveStateInfo liveStateInfo, String jobPath) {

		QName qName = LiveStateInfo.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String liveStateInfoXML = liveStateInfo.xmlText(xmlOptions);

		/*String xQueryStr =*/ scenarioFunctionConstructor("hs:insertJobStateLock", dbDocId, dbUserId, "true()", liveStateInfoXML, jobPath);
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		// SpaceWideRegistry.getGlobalLogger().debug(" >> STATE >> " + liveStateInfo + " X " + jobPath);
		// SpaceWideRegistry.getGlobalLogger().debug(" >> STATE >> " + xQueryStr);

//		ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		
//		for(Object currentObject : objectList) {
//			SpaceWideRegistry.getGlobalLogger().debug(" >> Query Result >> " + currentObject);
//		}

		return liveStateInfo;
	}

	public static Person getSubscribers(int userId, String role) throws XMLDBException {

		Person person = null;

		String xQueryStr = userFunctionConstructor("hs:getSubscribers", "" + userId, TransformUtils.toXSString(role));
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			person = ((PersonDocument) currentObject).getPerson();
		}
		
		return person;
	}

	public static FtpProperties searchFTPConnectionById(int ftpConnectionId) throws XMLDBException {
		
		FtpProperties ftpProperties = null;
		
		String xQueryStr = ftpFunctionConstructor("fc:searchFTPConnectionById", "" + ftpConnectionId);
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			ftpProperties = ((FtpPropertiesDocument) currentObject).getFtpProperties();
		}

		return ftpProperties;
	}

	public static DbProperties searchDBPropertiesById(int dbPropertiesId) throws XMLDBException {
		
		DbProperties dbProperties = null;
		
		String xQueryStr = dbConnFunctionConstructor("db:getDbConnection", "" + dbPropertiesId);
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			dbProperties = ((DbPropertiesDocument) currentObject).getDbProperties();
		}

		return dbProperties;
	}

	public static DbConnectionProfile searchDBConnectionProfilesById(int dbCPID) throws XMLDBException {

		DbConnectionProfile dbConnectionProfile = null;
		
		String xQueryStr = dbConnFunctionConstructor("db:getDbCP", "" + dbCPID);
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			dbConnectionProfile = ((DbConnectionProfileDocument) currentObject).getDbConnectionProfile();
		}

		return dbConnectionProfile;
	}

	public static StreamSource getTransformXslCode() throws IOException {

		String tlosJobTransformXsl = null;

		try {
			tlosJobTransformXsl = getDbDoc();
		} catch (XMLDBException e) {
			e.printStackTrace();
		} catch (XSLLoadException e) {
			e.printStackTrace();
		}

		StringReader xslReader = new StringReader(tlosJobTransformXsl);

		StreamSource streamSource = new StreamSource(xslReader);

		return streamSource;
	}

	public static String getDbDoc() throws XMLDBException, XSLLoadException {

		String tlosData = null;
		
		String xQueryStr = xslConnFunctionConstructor("hs:tlosJobTransformXsl");
		
		// SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			tlosData = currentObject.toString();
		}
				
		if (tlosData == null) {
			throw new XSLLoadException("moduleXslOperations.xquery returned empty result !");
		}
		
		return tlosData;
	}
}

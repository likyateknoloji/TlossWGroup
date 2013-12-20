package com.likya.tlossw.web.db;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.ogf.schemas.rns.x2009.x12.rns.RNSEntryType;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument;
import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.agent.SWAgentsDocument;
import com.likya.tlos.model.xmlbeans.agent.SWAgentsDocument.SWAgents;
import com.likya.tlos.model.xmlbeans.alarm.AlarmDocument;
import com.likya.tlos.model.xmlbeans.alarm.AlarmDocument.Alarm;
import com.likya.tlos.model.xmlbeans.alarmhistory.AlarmReportDocument;
import com.likya.tlos.model.xmlbeans.alarmhistory.AlarmReportDocument.AlarmReport;
import com.likya.tlos.model.xmlbeans.calendar.CalendarPropertiesDocument;
import com.likya.tlos.model.xmlbeans.calendar.CalendarPropertiesDocument.CalendarProperties;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.data.TimeManagementDocument.TimeManagement;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlos.model.xmlbeans.nrperesults.NrpeDataDocument;
import com.likya.tlos.model.xmlbeans.nrperesults.NrpeDataDocument.NrpeData;
import com.likya.tlos.model.xmlbeans.permission.PermissionDocument;
import com.likya.tlos.model.xmlbeans.permission.PermissionDocument.Permission;
import com.likya.tlos.model.xmlbeans.programprovision.LicenseDocument;
import com.likya.tlos.model.xmlbeans.programprovision.LicenseDocument.License;
import com.likya.tlos.model.xmlbeans.report.JobArrayDocument;
import com.likya.tlos.model.xmlbeans.report.JobArrayDocument.JobArray;
import com.likya.tlos.model.xmlbeans.report.LocalStatsDocument;
import com.likya.tlos.model.xmlbeans.report.LocalStatsDocument.LocalStats;
import com.likya.tlos.model.xmlbeans.report.ReportDocument;
import com.likya.tlos.model.xmlbeans.report.ReportDocument.Report;
import com.likya.tlos.model.xmlbeans.report.StatisticsDocument;
import com.likya.tlos.model.xmlbeans.report.StatisticsDocument.Statistics;
import com.likya.tlos.model.xmlbeans.sla.SLADocument;
import com.likya.tlos.model.xmlbeans.sla.SLADocument.SLA;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.swresourcens.ResourceListDocument;
import com.likya.tlos.model.xmlbeans.swresourcens.ResourceListType;
import com.likya.tlos.model.xmlbeans.swresourcens.ResourceType;
import com.likya.tlos.model.xmlbeans.user.PersonDocument;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlos.model.xmlbeans.useroutput.UserResourceMapDocument;
import com.likya.tlos.model.xmlbeans.useroutput.UserResourceMapDocument.UserResourceMap;
import com.likya.tlos.model.xmlbeans.webservice.AllowedRolesDocument.AllowedRoles;
import com.likya.tlos.model.xmlbeans.webservice.AllowedUsersDocument.AllowedUsers;
import com.likya.tlos.model.xmlbeans.webservice.UserAccessProfileDocument;
import com.likya.tlos.model.xmlbeans.webservice.UserAccessProfileDocument.UserAccessProfile;
import com.likya.tlos.model.xmlbeans.webservice.WebServiceDefinitionDocument;
import com.likya.tlos.model.xmlbeans.webservice.WebServiceDefinitionDocument.WebServiceDefinition;
import com.likya.tlossw.model.AlarmInfoTypeClient;
import com.likya.tlossw.model.DBAccessInfoTypeClient;
import com.likya.tlossw.model.WSAccessInfoTypeClient;
import com.likya.tlossw.model.auth.ResourcePermission;
import com.likya.tlossw.model.auth.WebAppUser;
import com.likya.tlossw.model.client.resource.MonitorAgentInfoTypeClient;
import com.likya.tlossw.model.client.resource.NrpeDataInfoTypeClient;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.XmlBeansTransformer;
import com.likya.tlossw.utils.date.DateUtils;
import com.likya.tlossw.utils.transform.TransformUtils;
import com.likya.tlossw.web.exist.ExistClient;
import com.likya.tlossw.web.exist.ExistConnectionHolder;
import com.likya.tlossw.web.utils.DefinitionUtils;
import com.likya.tlossw.web.utils.LiveUtils;
import com.likyateknoloji.xmlMetaDataTypes.MetaDataDocument;
import com.likyateknoloji.xmlMetaDataTypes.MetaDataDocument.MetaData;

@ManagedBean(name = "dbOperations")
@SessionScoped
public class DBOperations implements Serializable {

	// @ManagedProperty(value = "#{sessionMediator}")
	// private SessionMediator sessionMediator;

	private static final long serialVersionUID = 8575509360685840755L;

	private static final String xQueryNsHeader = CommonConstantDefinitions.xQueryNsHeader;

	@ManagedProperty(value = "#{existConnectionHolder}")
	private ExistConnectionHolder existConnectionHolder;

	private String xQueryModuleUrl = null;

	@PostConstruct
	public void init() {
		xQueryModuleUrl = ParsingUtils.getXQueryModuleUrl(ExistClient.dbUri);
	}

	private String localFunctionConstructor(String moduleName, String functionName, String moduleNamesSpace, String... param) {
		return ParsingUtils.getFunctionString(ExistClient.dbUri, xQueryModuleUrl, moduleName, functionName, moduleNamesSpace, param);
	}

	private String localFunctionConstructorNS(String moduleName, String functionName, String declaredNameSpaces, String moduleNamesSpace, String... param) {
		return ParsingUtils.getFunctionString(ExistClient.dbUri, xQueryModuleUrl, moduleName, functionName, declaredNameSpaces, moduleNamesSpace, param);
	}

	private String agentFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructorNS("moduleAgentOperations.xquery", functionName, CommonConstantDefinitions.decNsRes, CommonConstantDefinitions.lkNsUrl, param);
	}

	private String alarmFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructor("moduleAlarmOperations.xquery", functionName, CommonConstantDefinitions.lkNsUrl, param);
	}

	private String calendarFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructor("moduleCalendarOperations.xquery", functionName, CommonConstantDefinitions.hsNsUrl, param);
	}

	private String dbFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructorNS("moduleDBConnectionsOperations.xquery", functionName, CommonConstantDefinitions.decNsDbc + CommonConstantDefinitions.decNsCom, CommonConstantDefinitions.dbNsUrl, param);
	}

	private String ftpFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructorNS("moduleFTPConnectionsOperations.xquery", functionName, CommonConstantDefinitions.decNsFtp + CommonConstantDefinitions.decNsCom, CommonConstantDefinitions.fcNsUrl, param);
	}

	private String ppFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructor("moduleProgramProvisioningOperations.xquery", functionName, CommonConstantDefinitions.ksNsUrl, param);
	}

	private String reportFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructorNS("moduleReportOperations.xquery", functionName, CommonConstantDefinitions.decNsRep, CommonConstantDefinitions.hsNsUrl, param);
	}

	private String resourceFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructorNS("moduleResourcesOperations.xquery", functionName, CommonConstantDefinitions.decNsRes, CommonConstantDefinitions.rscNsUrl, param);
	}

	private String scenarioFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructorNS("moduleScenarioOperations.xquery", functionName, CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + CommonConstantDefinitions.decNsSt + CommonConstantDefinitions.decNsJsdl, CommonConstantDefinitions.hsNsUrl, param);
	}

	private String userFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructor("moduleUserOperations.xquery", functionName, CommonConstantDefinitions.hsNsUrl, param);
	}

	private String slaFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructor("moduleSLAOperations.xquery", functionName, CommonConstantDefinitions.hsNsUrl, param);
	}

	private String wsFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructorNS("moduleWebServiceOperations.xquery", functionName, CommonConstantDefinitions.decNsWs + CommonConstantDefinitions.decNsCom, CommonConstantDefinitions.wsoNsUrl, param);
	}

	private String metaFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructor("moduleMetaDataOperations.xquery", functionName, CommonConstantDefinitions.metaNsUrl, param);
	}

	public ArrayList<Object> moduleGeneric(String xQueryStr) {

		ArrayList<Object> returnObjectArray = new ArrayList<Object>();
		Object objectProperties = null;

		Collection collection = null;
		try {
			collection = existConnectionHolder.getCollection();
			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr.toString());
			ResourceIterator i = result.getIterator();
			Resource r = null;
			String xmlContent = null;

			while (i.hasMoreResources()) {
				r = i.nextResource();
				xmlContent = (String) r.getContent();

				try {
					objectProperties = XmlObject.Factory.parse(xmlContent);
					returnObjectArray.add(objectProperties);
				} catch (XmlException e) {
					// e.printStackTrace();
					returnObjectArray.add(xmlContent);
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		return returnObjectArray;
	}

	public ArrayList<SWAgent> searchAgent(String agentXML) {

		ArrayList<SWAgent> agentList = new ArrayList<SWAgent>();

		String xQueryStr = agentFunctionConstructor("lk:searchAgent", agentXML);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for (Object currentObject : objectList) {
			SWAgent agent = ((SWAgentDocument) currentObject).getSWAgent();
			agentList.add(agent);
		}

		return agentList;
	}

	public ArrayList<Person> searchUser(String personXML) {

		ArrayList<Person> prsList = new ArrayList<Person>();

		String xQueryStr = userFunctionConstructor("hs:searchUser", personXML);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		Person prs;
		for (Object currentObject : objectList) {
			prs = ((PersonDocument) currentObject).getPerson();
			prsList.add(prs);
		}

		return prsList;
	}

	public int getNextId(String component) {

		String xQueryStr = localFunctionConstructor("moduleSequenceOperations.xquery", "sq:getNextId", CommonConstantDefinitions.sqNsUrl, "\"" + component + "\"");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		int id = -1;
		for (Object currentObject : objectList) {
			id = Integer.parseInt(currentObject.toString());
		}

		return id;
	}

	public boolean updateAgent(String agentXML) {

		String xQueryStr = agentFunctionConstructor("lk:updateAgentLock", agentXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean updateUser(String personXML) {

		String xQueryStr = userFunctionConstructor("hs:updateUserLock", personXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean insertAgent(String agentXML) {

		String xQueryStr = agentFunctionConstructor("lk:insertAgentLock", agentXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean insertUser(String personXML) {

		String xQueryStr = userFunctionConstructor("hs:insertUserLock", personXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public SWAgent searchAgentByResource(String resourcename) throws XMLDBException {

		String xQueryStr = agentFunctionConstructor("lk:searchAgentByResource", "\"" + resourcename + "\"");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		SWAgent agent = null;
		for (Object currentObject : objectList) {
			agent = ((SWAgentDocument) currentObject).getSWAgent();
		}

		return agent;
	}

	public SWAgent searchAgentById(String id) {

		String xQueryStr = agentFunctionConstructor("lk:searchAgentByAgentId", id);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		SWAgent agent = null;
		for (Object currentObject : objectList) {
			agent = ((SWAgentDocument) currentObject).getSWAgent();
		}

		return agent;
	}

	public Person searchUserByUsername(String username) {

		String xQueryStr = userFunctionConstructor("hs:searchUserByUsername", "\"" + username + "\"");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		Person person = null;
		for (Object currentObject : objectList) {
			person = ((PersonDocument) currentObject).getPerson();
		}

		return person;
	}

	public boolean deleteAgent(String agentXML) {

		String xQueryStr = agentFunctionConstructor("lk:deleteAgentLock", agentXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean deleteUser(String personXML) {

		String xQueryStr = userFunctionConstructor("hs:deleteUserLock", personXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public Object checkUser(WebAppUser webAppUser) {

		if ((webAppUser.getUsername() == null) || (webAppUser.getPassword() == null)) {
			return false;
		}

		String xQueryStr = localFunctionConstructor("moduleGetResourceListByRole.xquery", "hs:query_username", CommonConstantDefinitions.hsNsUrl, TransformUtils.toXSString(webAppUser.getUsername()));

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for (Object currentObject : objectList) {
			UserResourceMap myUserPermission = ((UserResourceMapDocument) currentObject).getUserResourceMap();
			if (myUserPermission.getPerson().getUserPassword().equals(webAppUser.getPassword())) {
				webAppUser = XmlBeansTransformer.personToAppUser(myUserPermission);
				return webAppUser;
			}
		}

		return false;
	}

	public TlosProcessData getTlosDataXml(String docId, int userId, Integer scope) {

		String xQueryStr = scenarioFunctionConstructor("hs:getTlosDataXml", toXSString(docId), toXSString(userId), toXSInteger2Boolean(scope));

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		TlosProcessData tlosProcessData = TlosProcessData.Factory.newInstance();
		for (Object currentObject : objectList) {
			tlosProcessData = ((TlosProcessDataDocument) currentObject).getTlosProcessData();
		}

		return tlosProcessData;
	}

	// public TlosProcessData getDeploymentDataXml(int userId, Integer scope) {
	//
	// String xQueryStr = scenarioFunctionConstructor("hs:getDeploymentDataXml", toXSString(userId), toXSInteger2Boolean(scope));
	//
	// ArrayList<Object> objectList = moduleGeneric(xQueryStr);
	//
	// TlosProcessData tlosProcessData = TlosProcessData.Factory.newInstance();
	// for (Object currentObject : objectList) {
	// tlosProcessData = ((TlosProcessDataDocument) currentObject).getTlosProcessData();
	// }
	//
	// return tlosProcessData;
	// }
	//
	// public TlosProcessData getTlosTemplateDataXml(int userId, Integer scope) {
	//
	// String xQueryStr = scenarioFunctionConstructor("hs:getTlosTemplateDataXml", toXSString(userId), toXSInteger2Boolean(scope));
	//
	// ArrayList<Object> objectList = moduleGeneric(xQueryStr);
	//
	// TlosProcessData tlosProcessData = TlosProcessData.Factory.newInstance();
	// for (Object currentObject : objectList) {
	// tlosProcessData = ((TlosProcessDataDocument) currentObject).getTlosProcessData();
	// }
	//
	// return tlosProcessData;
	// }

	public ArrayList<ResourcePermission> getPermissions() {

		ArrayList<ResourcePermission> resourcePermission = new ArrayList<ResourcePermission>();

		String xQueryStr = localFunctionConstructor("modulePermissionOperations.xquery", "hs:getPermisions", CommonConstantDefinitions.hsNsUrl);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for (Object currentObject : objectList) {
			Permission permission = ((PermissionDocument) currentObject).getPermission();
			resourcePermission.add(XmlBeansTransformer.permissionsToResourcePermissions(permission));
		}

		return resourcePermission;
	}

	public boolean updatePermissions(String permissionsXML) {

		String xQueryStr = localFunctionConstructorNS("modulePermissionOperations.xquery", "hs:getPermisions", CommonConstantDefinitions.decNsPer + CommonConstantDefinitions.decNsCom, CommonConstantDefinitions.hsNsUrl, permissionsXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public ArrayList<DbProperties> searchDBConnection(String dbConnectionXML) {
		ArrayList<DbProperties> dbList = new ArrayList<DbProperties>();

		String xQueryStr = dbFunctionConstructor("db:searchDbConnection", dbConnectionXML);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for (Object currentObject : objectList) {
			DbProperties dbProperties = ((DbPropertiesDocument) currentObject).getDbProperties();
			dbList.add(dbProperties);
		}

		return dbList;
	}

	public ArrayList<Alarm> getAlarms() {

		ArrayList<Alarm> almList = new ArrayList<Alarm>();

		String xQueryStr = alarmFunctionConstructor("lk:alarms");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		Alarm alarm;
		for (Object currentObject : objectList) {
			alarm = ((AlarmDocument) currentObject).getAlarm();
			almList.add(alarm);
		}

		return almList;
	}

	public ArrayList<Person> getUsers() {

		ArrayList<Person> prsList = new ArrayList<Person>();

		String xQueryStr = userFunctionConstructor("hs:users");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		Person prs;
		for (Object currentObject : objectList) {
			prs = ((PersonDocument) currentObject).getPerson();
			prsList.add(prs);
		}

		return prsList;
	}

	public ArrayList<JobProperties> getJobList(String documentId, int userId, Integer scope, int maxNumber) throws XMLDBException {

		ArrayList<JobProperties> jobList = new ArrayList<JobProperties>();

		String xQueryStr = scenarioFunctionConstructor("hs:jobList", toXSString(documentId), toXSString(userId), toXSInteger2Boolean(scope), "1", maxNumber + "");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		JobProperties jobProperties = null;
		for (Object currentObject : objectList) {
			jobProperties = ((JobPropertiesDocument) currentObject).getJobProperties();
			jobList.add(jobProperties);
		}

		return jobList;
	}

	public ArrayList<Scenario> getScenarioList(String documentId, int userId, Integer scope) throws XMLDBException {

		ArrayList<Scenario> scenarioList = new ArrayList<Scenario>();

		String xQueryStr = scenarioFunctionConstructor("hs:scenarioList", toXSString(documentId), toXSString(userId), toXSInteger2Boolean(scope));

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		Scenario scenario = null;
		for (Object currentObject : objectList) {
			scenario = ((ScenarioDocument) currentObject).getScenario();
			scenarioList.add(scenario);
		}

		return scenarioList;
	}

	public ArrayList<RNSEntryType> getResources() {

		ArrayList<RNSEntryType> resources = new ArrayList<RNSEntryType>();

		// ilk 20 kaynağı getiriyor
		String xQueryStr = resourceFunctionConstructor("rsc:resourcesList", "1", "20");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		ResourceListType resourceList = null;
		for (Object currentObject : objectList) {
			resourceList = ((ResourceListDocument) currentObject).getResourceList();
		}

		for (RNSEntryType resource : resourceList.getResourceArray()) {
			resources.add(resource);
		}

		return resources;
	}

	public ArrayList<Alarm> searchAlarm(String alarmXML) throws XMLDBException {

		ArrayList<Alarm> alarmList = new ArrayList<Alarm>();

		String xQueryStr = alarmFunctionConstructor("lk:searchAlarm", alarmXML);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		Alarm alarm;
		for (Object currentObject : objectList) {
			alarm = ((AlarmDocument) currentObject).getAlarm();
			alarmList.add(alarm);
		}

		return alarmList;
	}

	public Boolean deleteAlarm(String alarmXML) {

		String xQueryStr = alarmFunctionConstructor("lk:deleteAlarmLock", alarmXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public ArrayList<AlarmReport> getAlarmReportList(String date1, String date2, String alarmLevel, String alarmName, String alarmUser) throws XMLDBException {

		ArrayList<AlarmReport> alarmList = new ArrayList<AlarmReport>();

		String xQueryStr = alarmFunctionConstructor("lk:getAlarms", date1, date2, alarmLevel, alarmName, alarmUser);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		AlarmReport alarmReport = null;
		for (Object currentObject : objectList) {
			alarmReport = ((AlarmReportDocument) currentObject).getAlarmReport();
			alarmList.add(alarmReport);
		}

		return alarmList;
	}

	public Boolean updateAlarm(String alarmXML) {

		String xQueryStr = alarmFunctionConstructor("lk:updateAlarmLock", alarmXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public Boolean insertAlarm(String alarmXML) {

		String xQueryStr = alarmFunctionConstructor("lk:insertAlarmLock", alarmXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean deleteDBConnection(String dbConnectionXML) {

		String xQueryStr = dbFunctionConstructor("db:deleteDbConnection", dbConnectionXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean insertDBConnection(String dbConnectionXML) {

		String xQueryStr = dbFunctionConstructor("db:insertDbConnection", dbConnectionXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean updateDBConnection(String dbConnectionXML) {

		String xQueryStr = dbFunctionConstructor("db:updateDbConnectionLock", dbConnectionXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean checkDBConnectionName(String dbConnectionXML) {

		String xQueryStr = dbFunctionConstructor("db:checkDbConnectionName", dbConnectionXML);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		if (objectList != null && objectList.size() > 0) {
			return false;
		}

		return true;
	}

	public DbProperties searchDBByID(String id) {

		String xQueryStr = dbFunctionConstructor("db:getDbConnection", id);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		DbProperties dbProperties = null;

		for (Object currentObject : objectList) {
			dbProperties = ((DbPropertiesDocument) currentObject).getDbProperties();
		}

		return dbProperties;
	}

	public ArrayList<DBAccessInfoTypeClient> searchDBAccessProfile(String dbAccessProfileXML) {

		HashMap<BigInteger, DbProperties> dbDefinitionList = new HashMap<BigInteger, DbProperties>();

		ArrayList<DbProperties> dbList = getDBConnections();

		for (DbProperties dbProperties : dbList) {
			dbDefinitionList.put(dbProperties.getID(), dbProperties);
		}

		ArrayList<DBAccessInfoTypeClient> dbAccessInfoTypeClients = new ArrayList<DBAccessInfoTypeClient>();

		String xQueryStr = dbFunctionConstructor("db:searchDbAccessProfile", dbAccessProfileXML);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for (Object currentObject : objectList) {
			DbConnectionProfile dbConnectionProfile = ((DbConnectionProfileDocument) currentObject).getDbConnectionProfile();

			DbProperties dbProperties = dbDefinitionList.get(dbConnectionProfile.getDbDefinitionId());

			DBAccessInfoTypeClient dbInfoTypeClient = new DBAccessInfoTypeClient();
			dbInfoTypeClient.setDbConnectionProfile(dbConnectionProfile);
			dbInfoTypeClient.setConnectionName(dbProperties.getConnectionName());
			dbInfoTypeClient.setDbType(dbProperties.getDbType().toString());

			dbAccessInfoTypeClients.add(dbInfoTypeClient);
		}

		return dbAccessInfoTypeClients;
	}

	public boolean deleteDBAccessProfile(String dbAccessProfileXML) {

		String xQueryStr = dbFunctionConstructor("db:deleteDbAccessProfile", dbAccessProfileXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean insertDBAccessProfile(String dbAccessProfileXML) {

		String xQueryStr = dbFunctionConstructor("db:insertDbAccessProfile", dbAccessProfileXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean updateDBAccessProfile(String dbAccessProfileXML) {

		String xQueryStr = dbFunctionConstructor("db:updateDbAccessProfileLock", dbAccessProfileXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public DbConnectionProfile searchDBAccessByID(String id) {
		String xQueryStr = dbFunctionConstructor("db:getDbCP", id);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		DbConnectionProfile dbConnectionProfile = null;

		for (Object currentObject : objectList) {
			dbConnectionProfile = ((DbConnectionProfileDocument) currentObject).getDbConnectionProfile();
		}

		return dbConnectionProfile;
	}

	public DbConnectionProfile searchDBAccessByDefID(String id, String userName) {
		String xQueryStr = dbFunctionConstructor("db:getDbCPfromDefId", id, toXSString(userName));

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		DbConnectionProfile dbConnectionProfile = null;

		for (Object currentObject : objectList) {
			dbConnectionProfile = ((DbConnectionProfileDocument) currentObject).getDbConnectionProfile();
		}

		return dbConnectionProfile;
	}
	
	public ArrayList<SLA> searchSla(String slaXML) {

		ArrayList<SLA> slaList = new ArrayList<SLA>();

		String xQueryStr = slaFunctionConstructor("hs:searchSLA", slaXML);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		SLA sla;
		for (Object currentObject : objectList) {
			sla = ((SLADocument) currentObject).getSLA();
			slaList.add(sla);
		}

		return slaList;
	}

	public boolean deleteSla(String slaXML) {

		String xQueryStr = slaFunctionConstructor("hs:deleteSLALock", slaXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean insertSla(String slaXML) {

		String xQueryStr = slaFunctionConstructor("hs:insertSlaLock", slaXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean updateSla(String slaXML) {

		String xQueryStr = slaFunctionConstructor("hs:updateSLALock", slaXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public ArrayList<CalendarProperties> getCalendars() {

		ArrayList<CalendarProperties> calendarList = new ArrayList<CalendarProperties>();

		String xQueryStr = calendarFunctionConstructor("hs:calendars");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for (Object currentObject : objectList) {
			CalendarProperties calendar = ((CalendarPropertiesDocument) currentObject).getCalendarProperties();
			calendarList.add(calendar);
		}

		return calendarList;
	}

	public Alarm searchAlarmByName(String alarmName) {

		String xQueryStr = alarmFunctionConstructor("lk:searchAlarmByName", "\"" + alarmName + "\"");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		Alarm alarm = null;
		for (Object currentObject : objectList) {
			alarm = ((AlarmDocument) currentObject).getAlarm();
		}

		return alarm;
	}

	public ArrayList<String> getSoftwareList() {

		ArrayList<String> softwareList = new ArrayList<String>();

		String xQueryStr = ppFunctionConstructor("ks:ppList", "1", "10");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for (Object currentObject : objectList) {
			License license = ((LicenseDocument) currentObject).getLicense();
			softwareList.add(license.getName());
		}

		return softwareList;
	}

	public SLA searchSlaByID(String slaId) {

		String xQueryStr = slaFunctionConstructor("hs:searchSlaBySlaId", slaId);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		SLA sla;
		for (Object currentObject : objectList) {
			sla = ((SLADocument) currentObject).getSLA();

			return sla;
		}

		return null;
	}

	public ArrayList<License> searchProvision(String provisionXML) {

		ArrayList<License> provisionList = new ArrayList<License>();

		String xQueryStr = ppFunctionConstructor("ks:searchPP", provisionXML);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for (Object currentObject : objectList) {
			License provision = ((LicenseDocument) currentObject).getLicense();
			provisionList.add(provision);
		}

		return provisionList;
	}

	public boolean deleteProvision(String provisionXML) {

		String xQueryStr = ppFunctionConstructor("ks:deletePpLock", provisionXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean insertProvision(String provisionXML) {

		String xQueryStr = ppFunctionConstructor("ks:insertPpLock", provisionXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean updateProvision(String provisionXML) {

		String xQueryStr = ppFunctionConstructor("ks:updatePpLock", provisionXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public License searchProvisionByID(String provisionId) {

		String xQueryStr = ppFunctionConstructor("ks:searchPpByID", provisionId);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for (Object currentObject : objectList) {
			License license = ((LicenseDocument) currentObject).getLicense();

			return license;
		}

		return null;
	}

	public boolean insertWSDefinition(String wsPropertiesXML) {

		String xQueryStr = wsFunctionConstructor("wso:insertWSDefinition", wsPropertiesXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// public JobProperties getTemplateJobFromName(String jobName) {
	//
	// String xQueryStr = scenarioFunctionConstructor("hs:getTemplateJobFromJobName", "\"" + jobName + "\"");
	//
	// ArrayList<Object> objectList = moduleGeneric(xQueryStr);
	//
	// JobProperties jobProperties = JobProperties.Factory.newInstance();
	// for (Object currentObject : objectList) {
	// jobProperties = ((JobPropertiesDocument) currentObject).getJobProperties();
	// }
	//
	// return jobProperties;
	// }

	// public JobProperties getTemplateJobFromId(String jobId) {
	//
	// String xQueryStr = scenarioFunctionConstructor("hs:getJobFromId", ConsjobId);
	//
	// ArrayList<Object> objectList = moduleGeneric(xQueryStr);
	//
	// JobProperties jobProperties = JobProperties.Factory.newInstance();
	// for (Object currentObject : objectList) {
	// jobProperties = ((JobPropertiesDocument) currentObject).getJobProperties();
	// }
	//
	// return jobProperties;
	// }

	public JobProperties getJob(String documentId, int userId, Integer scope, String jobPath, String jobName) {

		String xQueryStr = scenarioFunctionConstructor("hs:getJob", toXSString(documentId), toXSString(userId), toXSInteger2Boolean(scope), jobPath, "\"" + jobName + "\"");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		JobProperties jobProperties = JobProperties.Factory.newInstance();
		for (Object currentObject : objectList) {
			jobProperties = ((JobPropertiesDocument) currentObject).getJobProperties();
		}

		return jobProperties;
	}

	public boolean insertJob(String documentId, int userId, Integer scope, String jobPropertiesXML, String jobPath) {

		String xQueryStr = scenarioFunctionConstructor("hs:insertJobLock", toXSString(documentId), toXSString(userId), toXSInteger2Boolean(scope), jobPropertiesXML, jobPath);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public String getJobExistence(String documentId, int userId, Integer scope, String jobPath, String jobName) {

		String xQueryStr = scenarioFunctionConstructor("hs:getJobExistence", toXSString(documentId), toXSString(userId), toXSInteger2Boolean(scope), jobPath, toXSString(jobName));

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		String result = null;
		for (Object currentObject : objectList) {
			result = currentObject.toString();
		}

		return result;
	}

	public JobProperties getJobCopyFromId(String documentId, int userId, Integer scope, String jobId) {

		String xQueryStr = scenarioFunctionConstructor("hs:getJobCopyFromId", toXSString(documentId), toXSString(userId), toXSInteger2Boolean(scope), jobId);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		JobProperties jobProperties = null;
		for (Object currentObject : objectList) {
			jobProperties = ((JobPropertiesDocument) currentObject).getJobProperties();
		}

		return jobProperties;
	}

	public JobProperties getJobFromId(String documentId, int userId, Integer scope, String jobId) {

		String xQueryStr = scenarioFunctionConstructor("hs:getJobFromId", toXSString(documentId), toXSString(userId), toXSInteger2Boolean(scope), jobId);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		JobProperties jobProperties = null;
		for (Object currentObject : objectList) {
			jobProperties = ((JobPropertiesDocument) currentObject).getJobProperties();
		}

		return jobProperties;
	}
	
	public ArrayList<SWAgent> getAgents() {

		ArrayList<SWAgent> agentList = new ArrayList<SWAgent>();

		String xQueryStr = agentFunctionConstructor("lk:getAgents");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		SWAgents agents;
		for (Object currentObject : objectList) {
			agents = ((SWAgentsDocument) currentObject).getSWAgents();

			for (SWAgent agent : agents.getSWAgentArray()) {
				agentList.add(agent);
			}
		}

		return agentList;
	}

	public ArrayList<SLA> getSlaList() {

		ArrayList<SLA> slaList = new ArrayList<SLA>();

		String xQueryStr = slaFunctionConstructor("hs:slaList", "1", "10");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		SLA sla;
		for (Object currentObject : objectList) {
			sla = ((SLADocument) currentObject).getSLA();
			slaList.add(sla);
		}

		return slaList;
	}

	public ArrayList<WebServiceDefinition> getWebServiceListForActiveUser(int userId) {

		ArrayList<WebServiceDefinition> webServiceList = new ArrayList<WebServiceDefinition>();

		String xQueryStr = wsFunctionConstructor("wso:getWSDefinitionListForActiveUser", userId + "");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		WebServiceDefinition webServiceDefinition;
		for (Object currentObject : objectList) {
			webServiceDefinition = ((WebServiceDefinitionDocument) currentObject).getWebServiceDefinition();
			webServiceList.add(webServiceDefinition);
		}

		return webServiceList;
	}

	public ArrayList<WebServiceDefinition> getWSDefinitionListForAccessDef(int userId) {

		ArrayList<WebServiceDefinition> webServiceList = new ArrayList<WebServiceDefinition>();

		String xQueryStr = wsFunctionConstructor("wso:getWSDefinitionListForAccessDef", userId + "");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		WebServiceDefinition webServiceDefinition;
		for (Object currentObject : objectList) {
			webServiceDefinition = ((WebServiceDefinitionDocument) currentObject).getWebServiceDefinition();
			webServiceList.add(webServiceDefinition);
		}

		return webServiceList;
	}
	
	public ArrayList<FtpProperties> getFtpConnectionList() {

		ArrayList<FtpProperties> ftpConnectionList = new ArrayList<FtpProperties>();

		String xQueryStr = ftpFunctionConstructor("fc:getFTPConnectionList");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for (Object currentObject : objectList) {
			FtpProperties ftpProperties = ((FtpPropertiesDocument) currentObject).getFtpProperties();
			ftpConnectionList.add(ftpProperties);
		}

		return ftpConnectionList;
	}

	/**
	 * Veri tabaninda tanimli olan veri tabani tanimlari listesini sorguluyor
	 * 
	 * @return veri tabani tanimlari listesini donuyor
	 */
	public ArrayList<DbProperties> getDBConnections() {

		ArrayList<DbProperties> dbList = new ArrayList<DbProperties>();

		String xQueryStr = dbFunctionConstructor("db:getDbConnectionAll");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for (Object currentObject : objectList) {
			DbProperties dbProperties = ((DbPropertiesDocument) currentObject).getDbProperties();
			dbList.add(dbProperties);
		}

		return dbList;
	}

	/**
	 * Veri tabaninda tanimli olan veri tabani erisim profilleri listesini sorguluyor
	 * 
	 * @return veri tabani erisim profilleri listesini donuyor
	 */
	public ArrayList<DbConnectionProfile> getDBProfiles() {

		ArrayList<DbConnectionProfile> dbProfileList = new ArrayList<DbConnectionProfile>();

		String xQueryStr = dbFunctionConstructor("db:getDbProfileAll");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for (Object currentObject : objectList) {
			DbConnectionProfile connectionProfile = ((DbConnectionProfileDocument) currentObject).getDbConnectionProfile();
			dbProfileList.add(connectionProfile);
		}

		return dbProfileList;
	}

	public ResourceListType searchResource(String resourceXML) {

		String xQueryStr = resourceFunctionConstructor("rsc:searchResources", resourceXML);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		ResourceListType resourceList = null;
		for (Object currentObject : objectList) {
			resourceList = ((ResourceListDocument) currentObject).getResourceList();
		}

		return resourceList;
	}

	public boolean deleteResource(String resourceXML) {

		String xQueryStr = resourceFunctionConstructor("rsc:deleteResourceLock", resourceXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public ResourceType searchResourceByResourceName(String resourceName) {

		String xQueryStr = resourceFunctionConstructor("rsc:searchResourcesByResourceName", "\"" + resourceName + "\"");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		ResourceListType resourceList = null;
		for (Object currentObject : objectList) {
			resourceList = ((ResourceListDocument) currentObject).getResourceList();

			return resourceList.getResourceArray(0);
		}

		return null;
	}

	public boolean insertResource(String resourceXML) {

		String xQueryStr = resourceFunctionConstructor("rsc:insertResourceLock", resourceXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean updateResource(String resourceXML) {

		String xQueryStr = resourceFunctionConstructor("rsc:updateResourceLock", resourceXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public ArrayList<CalendarProperties> searchCalendar(String calendarPropertiesXML) {

		ArrayList<CalendarProperties> calendarList = new ArrayList<CalendarProperties>();

		String xQueryStr = calendarFunctionConstructor("hs:searchCalendar", calendarPropertiesXML);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for (Object currentObject : objectList) {
			CalendarProperties calendar = ((CalendarPropertiesDocument) currentObject).getCalendarProperties();
			calendarList.add(calendar);
		}

		return calendarList;
	}

	public boolean insertCalendar(String calendarPropertiesXML) {

		String xQueryStr = calendarFunctionConstructor("hs:insertCalendarLock", calendarPropertiesXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean deleteCalendar(String calendarPropertiesXML) {
		String xQueryStr = calendarFunctionConstructor("hs:deleteCalendarLock", calendarPropertiesXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public CalendarProperties searchCalendarByID(String calendarID) {

		String xQueryStr = calendarFunctionConstructor("hs:searchCalendarByID", calendarID);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for (Object currentObject : objectList) {
			CalendarProperties calendar = ((CalendarPropertiesDocument) currentObject).getCalendarProperties();

			return calendar;
		}

		return null;
	}

	public boolean updateCalendar(String calendarPropertiesXML) {

		String xQueryStr = calendarFunctionConstructor("hs:updateCalendarLock", calendarPropertiesXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public Scenario getScenarioFromId(String docId, int userId, Integer scope, String scenarioId) {

		String xQueryStr = scenarioFunctionConstructor("hs:getScenarioFromId", toXSString(docId), toXSString(userId), toXSInteger2Boolean(scope), scenarioId);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		Scenario scenario = null;
		for (Object currentObject : objectList) {
			scenario = ((ScenarioDocument) currentObject).getScenario();
		}

		return scenario;
	}

	public Scenario getScenario(String docId, int userId, Integer scope, String scenarioPath, String scenarioName) {

		String xQueryStr = scenarioFunctionConstructor("hs:getScenario", toXSString(docId), toXSString(userId), toXSInteger2Boolean(scope), scenarioPath, "\"" + scenarioName + "\"");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		Scenario scenario = null;
		for (Object currentObject : objectList) {
			scenario = ((ScenarioDocument) currentObject).getScenario();
		}

		return scenario;
	}

	public String getScenarioExistence(String docId, int userId, Integer scope, String scenarioPath, String scenarioName) {

		String xQueryStr = scenarioFunctionConstructor("hs:getScenarioExistence", toXSString(docId), toXSString(userId), toXSInteger2Boolean(scope), scenarioPath, "\"" + scenarioName + "\"");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		String result = null;
		for (Object currentObject : objectList) {
			result = currentObject.toString();
		}

		return result;
	}

	public boolean insertScenario(String docId, int userId, Integer scope, String scenarioXML, String scenarioPath) {

		String xQueryStr = scenarioFunctionConstructor("hs:insertScenarioLock", toXSString(docId), toXSString(userId), toXSInteger2Boolean(scope), scenarioXML, scenarioPath);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public LocalStats getStatsReport(String reportParametersXML) throws XMLDBException {

		String xQueryStr = reportFunctionConstructor("hs:calculateBaseStats", reportParametersXML);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		LocalStats localStats = null;
		for (Object currentObject : objectList) {
			localStats = ((LocalStatsDocument) currentObject).getLocalStats();
		}

		return localStats;
	}

	public JobArray getOverallReport(String reportParametersXML) throws XMLDBException {

		String xQueryStr = reportFunctionConstructor("hs:getOverallReport", "" + reportParametersXML);

		JobArray jobArray = null;

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for (Object currentObject : objectList) {
			jobArray = ((JobArrayDocument) currentObject).getJobArray1();
		}

		return jobArray;
	}

	public JobArray getOverallReportOld(int derinlik, int runType, int jobId, String refPoint, String orderType, int jobCount) throws XMLDBException {

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleReportOperations.xquery\"; " + CommonConstantDefinitions.decNsRep + "hs:getJobArray(hs:getJobsReport(" + derinlik + "," + runType + "," + jobId + "," + refPoint + "()),\"" + orderType + "\"," + jobCount + ")";

		Collection collection = existConnectionHolder.getCollection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();

		JobArray jobArray = null;

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			try {
				jobArray = JobArrayDocument.Factory.parse(xmlContent).getJobArray1();
			} catch (XmlException e) {
				e.printStackTrace();
				return null;
			}

		}

		return jobArray;
	}

	public MetaData readMetaData() throws XMLDBException {

		String xQueryStr = metaFunctionConstructor("met:readMetaData");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		MetaData metaData = null;
		for (Object currentObject : objectList) {
			metaData = ((MetaDataDocument) currentObject).getMetaData();
		}

		return metaData;
	}

	/*
	 * public ArrayList<Job> getLiveJobsScenarios(int derinlik, int runType, String orderType, int jobCount) throws XMLDBException {
	 * 
	 * TlosProcessData tlosProcessData = TlosProcessData.Factory.newInstance();
	 * 
	 * SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
	 * 
	 * String xQueryStr = xQueryNsHeader + hsNsUrl + xQueryModuleUrl + "/moduleReportOperations.xquery\"; decNsRep + "
	 * hs:getJobArray(hs:getJobsReport(" + derinlik + "," + runType + ",0, true()),\"" + orderType + "\"," + jobCount + ")";
	 * 
	 * Collection collection = existConnectionHolder.getCollection(); XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
	 * service.setProperty("indent", "yes");
	 * 
	 * ResourceSet result = service.query(xQueryStr); ResourceIterator i = result.getIterator();
	 * 
	 * ArrayList<Job> jobs = new ArrayList<Job>(); JobArray jobArray = null;
	 * 
	 * while (i.hasMoreResources()) { Resource r = i.nextResource(); String xmlContent = (String) r.getContent();
	 * 
	 * try {
	 * 
	 * // XmlOptions xmlOption = new XmlOptions(); // Map <String,String> map=new HashMap<String,String>(); // map.put("","http://www.likyateknoloji.com/XML_report_types"); //
	 * xmlOption.setLoadSubstituteNamespaces(map);
	 * 
	 * jobArray = JobArrayDocument.Factory.parse(xmlContent).getJobArray1(); } catch (XmlException e) { e.printStackTrace(); return null; } } for (Job job : jobArray.getJobArray())
	 * { jobs.add(job); }
	 * 
	 * return jobs; }
	 */

	public SWAgent checkAgent(String resource, int jmxPort) {

		String xQueryStr = agentFunctionConstructor("lk:searchAgent", "xs:string(\"" + resource + "\")", jmxPort + "");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		SWAgent agent = null;
		for (Object currentObject : objectList) {
			agent = ((SWAgentDocument) currentObject).getSWAgent();
		}

		return agent;
	}

	public Statistics getDensityReport(String reportParametersXML) throws XMLDBException {

		long startTime = System.currentTimeMillis();

		String xQueryStr = localFunctionConstructor("moduleDensityCalculations.xquery", "density:recStat", CommonConstantDefinitions.densityNsUrl, reportParametersXML);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		Statistics stat = null;
		for (Object currentObject : objectList) {
			stat = ((StatisticsDocument) currentObject).getStatistics();
		}

		System.err.println(" dashboardReport : " + DateUtils.dateDiffWithNow(startTime) + "ms");

		return stat;
	}

	public Report getDashboardReport(String reportParametersXML) throws XMLDBException {

		long startTime = System.currentTimeMillis();

		String xQueryStr = reportFunctionConstructor("hs:jobStateListbyRunId", "0", reportParametersXML);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		Report report = null;
		for (Object currentObject : objectList) {
			report = ((ReportDocument) currentObject).getReport();
		}

		System.err.println(" current States of Jobs Report > hs:jobStateListbyRunId , time : " + DateUtils.dateDiffWithNow(startTime) + "ms");
		return report;
	}

	public ArrayList<AlarmInfoTypeClient> getJobAlarmHistory(String jobId, Boolean transformToLocalTime) {

		ArrayList<AlarmInfoTypeClient> alarmList = new ArrayList<AlarmInfoTypeClient>();

		String xQueryStr = alarmFunctionConstructor("lk:jobAlarmListbyRunId", "3", "0", jobId, "false()", "30");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm alarm;
		for (Object currentObject : objectList) {
			alarm = ((com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument) currentObject).getAlarm();

			try {
				alarmList.add(fillAlarmInfoTypeClient(alarm, jobId, transformToLocalTime));
			} catch (XMLDBException e) {
				e.printStackTrace();
			}
		}

		return alarmList;
	}

	private AlarmInfoTypeClient fillAlarmInfoTypeClient(com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm alarm, String jobId, Boolean transformToLocalTime) throws XMLDBException {

		AlarmInfoTypeClient alarmInfoTypeClient = new AlarmInfoTypeClient();
		alarmInfoTypeClient.setAlarmId(alarm.getAlarmId() + "");
		alarmInfoTypeClient.setAlarmHistoryId(alarm.getAHistoryId() + "");
		alarmInfoTypeClient.setCreationDate(DateUtils.calendarToString(alarm.getCreationDate(), transformToLocalTime));
		alarmInfoTypeClient.setLevel(alarm.getLevel() + "");

		String xQueryStr;
		/*
		 * // alarmin gerceklestigi kaynak ve agant id'sini set ediyor
		 * 
		 * String dataFile = xmlsUrl + CommonConstantDefinitions.AGENT_DATA; xQueryStr = xQueryNsHeader + "lk=\"http://likya.tlos.com/\"" + xQueryModuleUrl +
		 * "/moduleAgentOperations.xquery\";" + decNsRes + "lk:searchAgentByAgentId(\"" + metaData + "\", " + alarm.getAgentId() + ")";
		 * 
		 * result = service.query(xQueryStr); i = result.getIterator(); SWAgent agent = null;
		 * 
		 * while (i.hasMoreResources()) { Resource r = i.nextResource(); String xmlContent = (String) r.getContent();
		 * 
		 * try { agent = SWAgentDocument.Factory.parse(xmlContent).getSWAgent(); break; } catch (XmlException e) { e.printStackTrace(); } }
		 * alarmInfoTypeClient.setResourceName(agent.getResource().getStringValue() + "." + alarm.getAgentId());
		 */

		alarmInfoTypeClient.setResourceName(alarm.getAgentId());

		// bu is icin kullanilan warnBy degerini set ediyor

		String warnByStr = "";
		for (int j = 0; j < alarm.getSubscriber().getAlarmChannelTypes().getWarnByArray().length; j++) {

			if (alarm.getSubscriber().getAlarmChannelTypes().getWarnByArray(j).getId() == 1)
				warnByStr = warnByStr + "e-mail;";
			if (alarm.getSubscriber().getAlarmChannelTypes().getWarnByArray(j).getId() == 2)
				warnByStr = warnByStr + "SMS;";
			if (alarm.getSubscriber().getAlarmChannelTypes().getWarnByArray(j).getId() == 3)
				warnByStr = warnByStr + "GUI;";
		}

		alarmInfoTypeClient.setWarnBy(warnByStr);

		// alarm tipini set ediyor
		if (alarm.getCaseManagement().getStateManagement() != null) {
			alarmInfoTypeClient.setAlarmType("State");
		} else if (alarm.getCaseManagement().getSLAManagement()) {
			alarmInfoTypeClient.setAlarmType("SLA");
		} else if (alarm.getCaseManagement().getTimeManagement() != null) {
			alarmInfoTypeClient.setAlarmType("Time");
		} else if (alarm.getCaseManagement().getSystemManagement() != null) {
			alarmInfoTypeClient.setAlarmType("System");
		}

		if (alarm.getSubscriber().getRole() != null) {
			alarmInfoTypeClient.setSubscriber(alarm.getSubscriber().getRole().toString());
		} else {
			// personid'ye gore kullanici adini db'den sorguluyor
			xQueryStr = userFunctionConstructor("hs:searchUserByUserId", alarm.getSubscriber().getPerson().getId().toString());

			ArrayList<Object> objectList = moduleGeneric(xQueryStr);

			Person user = null;
			for (Object currentObject : objectList) {
				user = ((PersonDocument) currentObject).getPerson();
				break;
			}

			if (user != null) {
				alarmInfoTypeClient.setSubscriber(user.getUserName());
			} else {
				alarmInfoTypeClient.setSubscriber("-");
			}
		}

		// alarm id'ye gore alarmi dbden sorguluyor
		xQueryStr = alarmFunctionConstructor("lk:searchAlarmByAlarmId", alarm.getAlarmId().toString());

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		Alarm alarmDefinition = null;
		for (Object currentObject : objectList) {
			alarmDefinition = ((AlarmDocument) currentObject).getAlarm();

			alarmInfoTypeClient.setAlarmName(alarmDefinition.getName());
			alarmInfoTypeClient.setDescription(alarmDefinition.getDesc());

			break;
		}

		return alarmInfoTypeClient;
	}

	public ArrayList<JobInfoTypeClient> getJobResultList(String docId, int userId, Integer scope, String jobId, int runNumber, Boolean transformToLocalTime) {

		String xQueryStr = scenarioFunctionConstructor("hs:jobResultListbyRunId", toXSString(docId), toXSString(userId), toXSInteger2Boolean(scope), runNumber + "", "0", jobId, "false()");

		ArrayList<JobInfoTypeClient> jobs = new ArrayList<JobInfoTypeClient>();

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		JobProperties jobProperties = null;
		for (Object currentObject : objectList) {
			jobProperties = ((JobPropertiesDocument) currentObject).getJobProperties();
			jobs.add(fillJobInfoTypeClient(jobProperties, jobs.size(), transformToLocalTime));
		}

		return jobs;
	}

	private JobInfoTypeClient fillJobInfoTypeClient(JobProperties jobProperties, int jobListSize, Boolean transformToLocalTime) {

		JobInfoTypeClient jobInfoTypeClient = new JobInfoTypeClient();

		jobInfoTypeClient.setLSIDateTime(jobProperties.getLSIDateTime());
		jobInfoTypeClient.setJobId(jobProperties.getID());
		// jobInfoTypeClient.setJobKey(jobProperties.getID());
		jobInfoTypeClient.setJobName(jobProperties.getBaseJobInfos().getJsName());
		jobInfoTypeClient.setJobCommand(jobProperties.getBaseJobInfos().getJobTypeDetails().getJobCommand());
		jobInfoTypeClient.setJobCommandType(jobProperties.getBaseJobInfos().getJobTypeDetails().getJobCommandType().toString());
		jobInfoTypeClient.setJobPath(jobProperties.getBaseJobInfos().getJobTypeDetails().getJobPath());
		jobInfoTypeClient.setJobLogPath(jobProperties.getBaseJobInfos().getJobLogPath());
		jobInfoTypeClient.setJobLogName(jobProperties.getBaseJobInfos().getJobLogFile());
		jobInfoTypeClient.setoSystem(jobProperties.getBaseJobInfos().getOSystem().toString());
		jobInfoTypeClient.setJobPriority(jobProperties.getBaseJobInfos().getJobPriority().intValue());

		TimeManagement timeManagement = jobProperties.getManagement().getTimeManagement();
		if (timeManagement.getJsPlannedTime().getStartTime() != null) {
			jobInfoTypeClient.setJobPlanTime(DateUtils.jobTimeToString(timeManagement.getJsPlannedTime().getStartTime().getTime(), transformToLocalTime));
		}

		if (timeManagement.getJsPlannedTime().getStopTime() != null) {
			jobInfoTypeClient.setJobPlanEndTime(DateUtils.jobTimeToString(timeManagement.getJsPlannedTime().getStopTime().getTime(), transformToLocalTime));
		}
		String timeOutputFormatHms = new String("HH:mm:ss");
		String jsTimeOutValue = DefinitionUtils.calendarTimeToStringTimeFormat(jobProperties.getManagement().getTimeControl().getJsTimeOut().getTime(), "Zulu", timeOutputFormatHms);
		jobInfoTypeClient.setJobTimeOut(jsTimeOutValue);

		if (timeManagement.getJsRealTime() != null) {
			jobInfoTypeClient.setPlannedExecutionDate(DateUtils.jobRealTimeToStringReport(timeManagement.getJsRealTime(), true, transformToLocalTime));

			// is bitmisse
			if (timeManagement.getJsRealTime().getStopTime() != null) {
				jobInfoTypeClient.setCompletionDate(DateUtils.jobRealTimeToStringReport(timeManagement.getJsRealTime(), false, transformToLocalTime));
			} else {
				jobInfoTypeClient.setCompletionDate("?");
			}

			// o gunku calisma mi yoksa eski calisma mi oldugunu kontrol ediyor
			boolean pastExecution = true;
			if (jobListSize == 0) {
				pastExecution = false;
			}

			jobInfoTypeClient.setWorkDuration(DateUtils.getJobWorkDuration(timeManagement.getJsRealTime(), pastExecution));
		}

		// LiveStateInfo listesindeki ilk eleman alinarak islem yapildi, yani guncel state i alindi
		jobInfoTypeClient.setOver(jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(StateName.FINISHED));
		jobInfoTypeClient.setLiveStateInfo(jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0));

		jobInfoTypeClient.setJobAutoRetry(jobProperties.getManagement().getCascadingConditions().getJobAutoRetry().getBooleanValue());
		jobInfoTypeClient.setSafeRestart(jobProperties.getManagement().getCascadingConditions().getJobSafeToRestart());

		if (jobProperties.getDependencyList() != null) {
			List<Item> myList = Arrays.asList(jobProperties.getDependencyList().getItemArray());
			ArrayList<Item> dependencyList = new ArrayList<Item>(myList);
			Iterator<Item> dependencyListIterator = dependencyList.iterator();
			ArrayList<String> depenArrayList = new ArrayList<String>();
			while (dependencyListIterator.hasNext()) {
				depenArrayList.add(dependencyListIterator.next().getJsId());
			}
			jobInfoTypeClient.setJobDependencyList(depenArrayList);
		}

		jobInfoTypeClient.setAgentId(jobProperties.getAgentId());

		/*
		 * if (jobInfoTypeClient.getAgentId() > 0) { SWAgent agent = TlosSpaceWide.getSpaceWideRegistry
		 * ().getAgentManagerReference().getSwAgentsCache().get(jobInfoTypeClient.getAgentId() + "");
		 * 
		 * jobInfoTypeClient.setResourceName(agent.getResource().getStringValue()); }
		 */

		return jobInfoTypeClient;
	}

	public com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm getAlarmHistoryById(int alarmHistoryId) {

		String xQueryStr = alarmFunctionConstructor("lk:searchAlarmHistoryById", alarmHistoryId + "");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm alarm = null;
		for (Object currentObject : objectList) {
			alarm = ((com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument) currentObject).getAlarm();

			return alarm;
		}

		return null;
	}

	public boolean updateJob(String docId, int userId, Integer scope, String jobPropertiesXML, String jobPath) {

		String xQueryStr = scenarioFunctionConstructor("hs:updateJobLock", toXSString(docId), toXSString(userId), toXSInteger2Boolean(scope), jobPropertiesXML, jobPath);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public ArrayList<WSAccessInfoTypeClient> searchWSAccessProfiles(String userAccessProfileXML) {

		HashMap<BigInteger, WebServiceDefinition> wsDefinitionList = new HashMap<BigInteger, WebServiceDefinition>();

		String xQueryStr = wsFunctionConstructor("wso:getWSDefinitionList");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		WebServiceDefinition webServiceDefinition;
		for (Object currentObject : objectList) {
			webServiceDefinition = ((WebServiceDefinitionDocument) currentObject).getWebServiceDefinition();
			wsDefinitionList.put(webServiceDefinition.getID(), webServiceDefinition);
		}

		ArrayList<WSAccessInfoTypeClient> wsAccessInfoTypeClients = new ArrayList<WSAccessInfoTypeClient>();

		xQueryStr = wsFunctionConstructor("wso:searchWSAccessProfiles", userAccessProfileXML);

		ArrayList<Object> wsObjectList = moduleGeneric(xQueryStr);

		UserAccessProfile userAccessProfile;
		for (Object currentObject : wsObjectList) {
			userAccessProfile = ((UserAccessProfileDocument) currentObject).getUserAccessProfile();

			WebServiceDefinition wsDefinition = wsDefinitionList.get(userAccessProfile.getWebServiceID());

			WSAccessInfoTypeClient wsInfoTypeClient = new WSAccessInfoTypeClient();
			wsInfoTypeClient.setWsAccessProfile(userAccessProfile);
			wsInfoTypeClient.setServiceName(wsDefinition.getServiceName());
			wsInfoTypeClient.setDescription(wsDefinition.getDescription());

			String userOrRoleStr = "";

			if (userAccessProfile.getAllowedRoles() != null) {
				AllowedRoles roles = userAccessProfile.getAllowedRoles();
				for (int j = 0; j < roles.getRoleArray().length; j++) {
					userOrRoleStr += roles.getRoleArray(j).toString() + ", ";
				}
			} else if (userAccessProfile.getAllowedUsers() != null) {
				ArrayList<Person> userList = getUsers();

				AllowedUsers users = userAccessProfile.getAllowedUsers();
				for (int j = 0; j < users.getUserIdArray().length; j++) {

					int userId = users.getUserIdArray(j);
					for (Person user : userList) {

						if (user.getId() == userId) {
							userOrRoleStr += user.getUserName() + ", ";
							break;
						}
					}
				}
			}

			if (!userOrRoleStr.equals("")) {
				userOrRoleStr = userOrRoleStr.substring(0, userOrRoleStr.length() - 2);
			}
			wsInfoTypeClient.setUserOrRoleList(userOrRoleStr);

			wsAccessInfoTypeClients.add(wsInfoTypeClient);
		}

		return wsAccessInfoTypeClients;
	}

	public boolean deleteWSAccessProfile(String userAccessProfileXML) {

		String xQueryStr = wsFunctionConstructor("wso:deleteWSAccessProfile", userAccessProfileXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean insertWSAccessProfile(String userAccessProfileXML) {

		String xQueryStr = wsFunctionConstructor("wso:insertWSAccessProfile", userAccessProfileXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public UserAccessProfile searchWSAccessByID(String id) {

		String xQueryStr = wsFunctionConstructor("wso:getWSAccessProfile", id);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		UserAccessProfile userAccessProfile = null;
		for (Object currentObject : objectList) {
			userAccessProfile = ((UserAccessProfileDocument) currentObject).getUserAccessProfile();
		}

		return userAccessProfile;
	}

	public boolean updateWSAccessProfile(String userAccessProfileXML) {

		String xQueryStr = wsFunctionConstructor("wso:updateWSAccessProfileLock", userAccessProfileXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public ArrayList<FtpProperties> searchFTPAccessConnection(String ftpAccessPropertiesXML) {

		ArrayList<FtpProperties> ftpConnectionList = new ArrayList<FtpProperties>();

		String xQueryStr = ftpFunctionConstructor("fc:searchFTPConnection", ftpAccessPropertiesXML);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for (Object currentObject : objectList) {
			FtpProperties ftpProperties = ((FtpPropertiesDocument) currentObject).getFtpProperties();
			ftpConnectionList.add(ftpProperties);
		}

		return ftpConnectionList;
	}

	public boolean deleteFTPAccessConnection(String ftpAccessPropertiesXML) {

		String xQueryStr = ftpFunctionConstructor("fc:deleteFTPConnection", ftpAccessPropertiesXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean checkFTPConnectionName(String ftpAccessPropertiesXML) {

		String xQueryStr = ftpFunctionConstructor("fc:checkFTPConnectionName", ftpAccessPropertiesXML);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		if (objectList != null && objectList.size() > 0) {
			return false;
		}

		return true;
	}

	public boolean insertFTPAccessConnection(String ftpAccessPropertiesXML) {

		String xQueryStr = ftpFunctionConstructor("fc:insertFTPConnection", ftpAccessPropertiesXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public FtpProperties searchFTPConnectionById(int ftpConnectionId) {

		String xQueryStr = ftpFunctionConstructor("fc:searchFTPConnectionById", ftpConnectionId + "");

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		FtpProperties ftpProperties = null;
		for (Object currentObject : objectList) {
			ftpProperties = ((FtpPropertiesDocument) currentObject).getFtpProperties();

			return ftpProperties;
		}

		return null;
	}

	public boolean updateFTPAccessConnection(String ftpAccessPropertiesXML) {

		String xQueryStr = ftpFunctionConstructor("fc:updateFTPConnectionLock", ftpAccessPropertiesXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean updateScenario(String docId, int userId, Integer scope, String scenarioPath, String scenarioXML) {

		String xQueryStr = scenarioFunctionConstructor("hs:updateScenarioLock", toXSString(docId), toXSString(userId), toXSInteger2Boolean(scope), scenarioPath, scenarioXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean deleteScenario(String docId, int userId, Integer scope, String scenarioPath, String scenarioXML) {

		String xQueryStr = scenarioFunctionConstructor("hs:deleteScenarioLock", toXSString(docId), toXSString(userId), toXSInteger2Boolean(scope), scenarioXML, scenarioPath);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean deleteJob(String docId, int userId, Integer scope, String jobPath, String jobPropertiesXML) {

		String xQueryStr = scenarioFunctionConstructor("hs:deleteJobLock", toXSString(docId), toXSString(userId), toXSInteger2Boolean(scope), jobPropertiesXML, jobPath);

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public NrpeDataInfoTypeClient retrieveNagiosAgentInfo(JmxUser jmxUser, MonitorAgentInfoTypeClient monitorAgentInfoTypeClient) {

		// 1.ve 2. parametreler hangi araliktaki datanin gelecegi, 3. parametre makine adi
		String xQueryStr = localFunctionConstructor("moduleNrpeOperations.xquery", "lk:nrpeOutput", CommonConstantDefinitions.lkNsUrl, "0", "5", toXSString(monitorAgentInfoTypeClient.getResourceName()));

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		NrpeData nrpeData = NrpeData.Factory.newInstance();

		NrpeDataInfoTypeClient nrpeDataInfoTypeClient = new NrpeDataInfoTypeClient();

		for (Object currentObject : objectList) {
			nrpeData = ((NrpeDataDocument) currentObject).getNrpeData();

			nrpeDataInfoTypeClient = LiveUtils.convertNrpeData(nrpeData);
		}

		return nrpeDataInfoTypeClient;
	}

	// XSLLoadException
	public String getDbDoc(String xqueryMethod) {

		String xQueryStr = localFunctionConstructorNS("moduleXslOperations.xquery", xqueryMethod, CommonConstantDefinitions.decNsFo + CommonConstantDefinitions.decNsXslfo, CommonConstantDefinitions.hsNsUrl);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for (Object currentObject : objectList) {
			String tlosData = currentObject.toString();

			return tlosData;
		}

		return null;
	}

	public boolean copyJSToJS(String fromDocId, String toDocId, Integer fromScope, Integer toScope, int userId, boolean isJob, String jsId, String jsPath, String newJSName) {

		String xQueryStr = scenarioFunctionConstructor("hs:copyJStoJS", toXSString(fromDocId), toXSString(toDocId), toXSInteger2Boolean(fromScope), toXSInteger2Boolean(toScope), toXSString(userId), isJob + "()", jsId, jsPath, toXSString(newJSName));

		try {
			@SuppressWarnings("unused")
			ArrayList<Object> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public ArrayList<JobProperties> getJobExistenceList(String docId, int userId, Integer scope, String jobPath, String jobName) {

		String xQueryStr = scenarioFunctionConstructor("hs:getJobExistenceResults", toXSString(docId), toXSString(userId), toXSInteger2Boolean(scope), jobPath, toXSString(jobName));

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		ArrayList<JobProperties> jobList = new ArrayList<JobProperties>();

		JobProperties jobProperties = null;
		for (Object currentObject : objectList) {
			jobProperties = ((JobPropertiesDocument) currentObject).getJobProperties();
			jobList.add(jobProperties);
		}

		return jobList;
	}

	public ExistConnectionHolder getExistConnectionHolder() {
		return existConnectionHolder;
	}

	public void setExistConnectionHolder(ExistConnectionHolder existConnectionHolder) {
		this.existConnectionHolder = existConnectionHolder;
	}

	private String toXSString(int i) {
		return TransformUtils.toXSString(i);
	}

	private String toXSString(String s) {
		return TransformUtils.toXSString(s);
	}

	public static String toXSBoolean(Boolean booleanData) {
		return booleanData ? "true()" : "false()";
	}

	public static String toXSInteger2Boolean(Integer intData) {
		return intData == 1 ? "true()" : "false()";
	}

}

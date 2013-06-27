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
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
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
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.model.jmx.JmxAppUser;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.XmlBeansTransformer;
import com.likya.tlossw.utils.date.DateUtils;
import com.likya.tlossw.web.exist.ExistClient;
import com.likya.tlossw.web.exist.ExistConnectionHolder;

@ManagedBean(name = "dbOperations")
@SessionScoped
public class DBOperations implements Serializable {

	private static final long serialVersionUID = 8575509360685840755L;

	private static final String xQueryNsHeader = CommonConstantDefinitions.xQueryNsHeader;

	@ManagedProperty(value = "#{existConnectionHolder}")
	private ExistConnectionHolder existConnectionHolder;

	private String xQueryModuleUrl = null;
	private String dataDocUrl = "";
	private String resourceDocUrl = "";
	private String xmlsUrl = "";
	private String metaData = "";

	@PostConstruct
	public void init() {
		xQueryModuleUrl = ParsingUtils.getXQueryModuleUrl(ExistClient.dbCollectionName);
		dataDocUrl = ParsingUtils.getXmlsPath(ExistClient.dbCollectionName) + CommonConstantDefinitions.JOB_DEFINITION_DATA;
		resourceDocUrl = ParsingUtils.getXmlsPath(ExistClient.dbCollectionName) + CommonConstantDefinitions.RESOURCES_DATA;
		xmlsUrl = ParsingUtils.getXmlsPath(ExistClient.dbCollectionName);
		metaData = xmlsUrl + CommonConstantDefinitions.META_DATA;
	}

	private String localFunctionConstructor(String moduleName, String functionName, String moduleNamesSpace, String... param) {
		return ParsingUtils.getFunctionString(ExistClient.dbCollectionName, xQueryModuleUrl, moduleName, functionName, moduleNamesSpace, param);
	}

	private String localFunctionConstructorNS(String moduleName, String functionName, String declaredNameSpaces, String moduleNamesSpace, String... param) {
		return ParsingUtils.getFunctionString(ExistClient.dbCollectionName, xQueryModuleUrl, moduleName, functionName, declaredNameSpaces, moduleNamesSpace, param);
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

	public ArrayList<XmlObject> moduleGeneric(String xQueryStr) {

		ArrayList<XmlObject> returnObjectArray = new ArrayList<XmlObject>();
		XmlObject objectProperties = null;

		try {
			Collection collection = existConnectionHolder.getCollection();
			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr.toString());
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					objectProperties = XmlObject.Factory.parse(xmlContent);
					returnObjectArray.add(objectProperties);
				} catch (XmlException e) {
					e.printStackTrace();
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

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		for (XmlObject currentObject : objectList) {
			SWAgent agent = ((SWAgentDocument) currentObject).getSWAgent();
			agentList.add(agent);
		}

		return agentList;
	}

	public ArrayList<SWAgent> searchAgentOld(String agentXML) {

		ArrayList<SWAgent> agentList = null;

		try {

			// String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.moduleImport + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAgentOperations.xquery\";" + CommonConstantDefinitions.decNsRes + "lk:searchAgent(\"" + metaData + "\", " + agentXML + ")";

			String xQueryStr = agentFunctionConstructor("lk:searchAgent", agentXML);

			Collection collection = existConnectionHolder.getCollection();
			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			agentList = new ArrayList<SWAgent>();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				SWAgent agent;
				try {
					agent = SWAgentDocument.Factory.parse(xmlContent).getSWAgent();
					agentList.add(agent);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException xmldbException) {
			xmldbException.printStackTrace();
		}
		return agentList;
	}

	public ArrayList<Person> searchUser(String personXML) {

		ArrayList<Person> prsList = null;

		try {

			String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleUserOperations.xquery\";" + "hs:searchUser(" + personXML + ")";

			Collection collection = existConnectionHolder.getCollection();
			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			prsList = new ArrayList<Person>();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				Person prs;
				try {
					prs = PersonDocument.Factory.parse(xmlContent).getPerson();
					prsList.add(prs);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}

			}

		} catch (XMLDBException xmldbException) {
			xmldbException.printStackTrace();
		}

		return prsList;
	}

	public int getNextId(String component) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.sqNsUrl + xQueryModuleUrl + "/moduleSequenceOperations.xquery\";" + "sq:getNextId(\"" + xmlsUrl + CommonConstantDefinitions.SEQUENCE_DATA + "\",\"" + component + "\")";

		int id = -1;

		try {
			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				id = Integer.parseInt(r.getContent().toString());
			}

		} catch (XMLDBException xmldbException) {
			xmldbException.printStackTrace();
		}

		return id;
	}

	public boolean updateAgent(String agentXML) {

		// String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAgentOperations.xquery\";" + CommonConstantDefinitions.decNsRes + "lk:updateAgentLock(\"" + metaData + "\", " + agentXML + ")";

		String xQueryStr = agentFunctionConstructor("lk:updateAgentLock", agentXML);

		Collection collection = existConnectionHolder.getCollection();
		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return false;
		}

		try {
			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean updateUser(String personXML) {

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleUserOperations.xquery\";" + "hs:updateUserLock(" + personXML + ")";

		Collection collection = existConnectionHolder.getCollection();
		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return false;
		}

		try {
			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean insertAgent(String agentXML) {

		/*
		 * String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAgentOperations.xquery\";" + CommonConstantDefinitions.decNsRes + "lk:insertAgentLock(\"" + metaData + "\", " + agentXML + ")";
		 */

		String xQueryStr = agentFunctionConstructor("lk:insertAgentLock", agentXML);

		Collection collection = existConnectionHolder.getCollection();
		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean insertUser(String personXML) {

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleUserOperations.xquery\";" + "hs:insertUserLock(" + personXML + ")";

		Collection collection = existConnectionHolder.getCollection();
		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return false;
		}

		try {
			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public SWAgent searchAgentByResource(String resourcename) throws XMLDBException {

		SWAgent agent = null;
		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAgentOperations.xquery\";" + CommonConstantDefinitions.decNsRes + "lk:searchAgentByResource(" + "\"" + resourcename + "\"" + ")";

		Collection collection = existConnectionHolder.getCollection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			try {
				agent = SWAgentDocument.Factory.parse(xmlContent).getSWAgent();
			} catch (XmlException e) {
				e.printStackTrace();
				return null;
			}
		}
		return agent;
	}

	public SWAgent searchAgentById(String id) {

		SWAgent agent = null;

		// String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAgentOperations.xquery\";" + CommonConstantDefinitions.decNsRes + "lk:searchAgentByAgentId(\"" + metaData + "\", " + id + ")";

		String xQueryStr = agentFunctionConstructor("lk:searchAgentByAgentId", id);

		Collection collection = existConnectionHolder.getCollection();
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
					agent = SWAgentDocument.Factory.parse(xmlContent).getSWAgent();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}

		} catch (XMLDBException xmldbException) {
			xmldbException.printStackTrace();
		}

		return agent;
	}

	public Person searchUserByUsername(String username) {

		Person person = null;

		try {

			Collection collection = existConnectionHolder.getCollection();
			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleUserOperations.xquery\";" + "hs:searchUserByUsername(" + "\"" + username + "\"" + ")";

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
					return null;
				}

			}

		} catch (XMLDBException xmldbException) {
			xmldbException.printStackTrace();
		}

		return person;
	}

	public boolean deleteAgent(String agentXML) {

		// String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAgentOperations.xquery\";" + CommonConstantDefinitions.decNsRes + "lk:deleteAgentLock(\"" + metaData + "\", " + agentXML + ")";

		String xQueryStr = agentFunctionConstructor("lk:deleteAgentLock", agentXML);

		Collection collection = existConnectionHolder.getCollection();
		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return false;
		}

		try {
			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean deleteUser(String personXML) {

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleUserOperations.xquery\";" + "hs:deleteUserLock(" + personXML + ")";

		Collection collection = existConnectionHolder.getCollection();
		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return false;
		}

		try {
			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public Object checkUser(JmxAppUser jmxAppUser) {

		try {

			// if ((jmxAppUser.getAppUser() == null) ||
			// (jmxAppUser.getAppUser().getUsername() == null) ||
			// (jmxAppUser.getAppUser().getPassword() == null)) {
			// return false;
			// }

			Collection collection = existConnectionHolder.getCollection();

			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");

			service.setProperty("indent", "yes");

			/*
			 * String usrDataFile = xmlsUrl + CommonConstantDefinitions.USER_DATA; String perDataFile = xmlsUrl + CommonConstantDefinitions.PERMISSION_DATA;
			 * 
			 * String funcDef = "hs:query_username(\"" + usrDataFile + "\", \"" + perDataFile + "\", xs:string(\"" + jmxAppUser.getAppUser().getUsername() + "\"))";
			 * 
			 * String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.moduleImport + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleGetResourceListByRole.xquery\";" + funcDef;
			 */

			String xQueryStr = localFunctionConstructor("moduleGetResourceListByRole.xquery", "hs:query_username", CommonConstantDefinitions.hsNsUrl, "xs:string(\"" + jmxAppUser.getAppUser().getUsername() + "\")");

			ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

			for (XmlObject currentObject : objectList) {
				UserResourceMap myUserPermission = ((UserResourceMapDocument) currentObject).getUserResourceMap();
				if (myUserPermission.getPerson().getUserPassword().equals(jmxAppUser.getAppUser().getPassword())) {
					jmxAppUser.setAppUser(XmlBeansTransformer.personToAppUser(myUserPermission));
					return jmxAppUser;
				}
			}

		} catch (XMLDBException xmldbException) {
			xmldbException.printStackTrace();
		}

		return false;
	}

	public TlosProcessData getTlosDataXml(String documentName) {

		TlosProcessData tlosProcessData = TlosProcessData.Factory.newInstance();

		try {

			Collection collection = existConnectionHolder.getCollection();

			XPathQueryService service;

			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + CommonConstantDefinitions.decNsSt + "hs:getTlosDataXml(xs:string(\"" + xmlsUrl + documentName + "\"))";

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

	public ArrayList<ResourcePermission> getPermissions() {

		ArrayList<ResourcePermission> resourcePermission = new ArrayList<ResourcePermission>();

		String xQueryStr = localFunctionConstructor("modulePermissionOperations.xquery", "hs:getPermisions", CommonConstantDefinitions.hsNsUrl);

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		for (XmlObject currentObject : objectList) {
			Permission permission = ((PermissionDocument) currentObject).getPermission();
			resourcePermission.add(XmlBeansTransformer.permissionsToResourcePermissions(permission));
		}

		return resourcePermission;
	}

	public boolean updatePermissions(String permissionsXML) {

		String xQueryStr = localFunctionConstructorNS("modulePermissionOperations.xquery", "hs:getPermisions", CommonConstantDefinitions.decNsPer + CommonConstantDefinitions.decNsCom, CommonConstantDefinitions.hsNsUrl, permissionsXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public ArrayList<DbProperties> searchDBConnection(String dbConnectionXML) {
		ArrayList<DbProperties> dbList = new ArrayList<DbProperties>();

		String xQueryStr = dbFunctionConstructor("db:searchDbConnection", dbConnectionXML);

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		for (XmlObject currentObject : objectList) {
			DbProperties dbProperties = ((DbPropertiesDocument) currentObject).getDbProperties();
			dbList.add(dbProperties);
		}

		return dbList;
	}

	public ArrayList<Alarm> getAlarms() {

		// String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:alarms(\"" + metaData + "\")";

		String xQueryStr = alarmFunctionConstructor("lk:alarms");

		ArrayList<Alarm> almList = new ArrayList<Alarm>();

		Collection collection = existConnectionHolder.getCollection();

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				Alarm alm;
				try {
					alm = AlarmDocument.Factory.parse(xmlContent).getAlarm();
					almList.add(alm);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}

			}

		} catch (XMLDBException e1) {
			e1.printStackTrace();
			return null;
		}

		return almList;
	}

	public ArrayList<Person> getUsers() {

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleUserOperations.xquery\";" + "hs:users()";

		Collection collection = existConnectionHolder.getCollection();

		ArrayList<Person> prsList = new ArrayList<Person>();

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				Person prs;
				try {
					prs = PersonDocument.Factory.parse(xmlContent).getPerson();
					prsList.add(prs);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}

			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return prsList;
	}

	public ArrayList<JobProperties> getJobList(int maxNumber) throws XMLDBException {

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + "hs:jobList(" + xmlsUrl + CommonConstantDefinitions.JOB_DEFINITION_DATA + ",1," + maxNumber + ")";

		Collection collection = existConnectionHolder.getCollection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();

		ArrayList<JobProperties> jobList = new ArrayList<JobProperties>();

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			JobProperties jobProperties;
			try {
				jobProperties = JobPropertiesDocument.Factory.parse(xmlContent).getJobProperties();
				jobList.add(jobProperties);
			} catch (XmlException e) {
				e.printStackTrace();
				return null;
			}
		}

		return jobList;
	}

	public ArrayList<Scenario> getScenarioList() throws XMLDBException {

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + "hs:scenarioList(" + dataDocUrl + ")";

		Collection collection = existConnectionHolder.getCollection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();

		ArrayList<Scenario> scenarioList = new ArrayList<Scenario>();

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			Scenario scenario;
			try {
				scenario = ScenarioDocument.Factory.parse(xmlContent).getScenario();
				scenarioList.add(scenario);
			} catch (XmlException e) {
				e.printStackTrace();
				return null;
			}
		}

		return scenarioList;
	}

	public ArrayList<RNSEntryType> getResources() {
		ArrayList<RNSEntryType> resources = new ArrayList<RNSEntryType>();
		ResourceListType resourceList = null;

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.moduleImport + CommonConstantDefinitions.rscNsUrl + xQueryModuleUrl + "/moduleResourcesOperations.xquery\";" + "rsc:resourcesList(\"" + resourceDocUrl + "\", 1, 10)";

		Collection collection = existConnectionHolder.getCollection();

		try {

			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					resourceList = ResourceListDocument.Factory.parse(xmlContent).getResourceList();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException xmldbException) {
			xmldbException.printStackTrace();
		}

		for (RNSEntryType resource : resourceList.getResourceArray()) {
			resources.add(resource);
		}

		return resources;
	}

	public ArrayList<Alarm> searchAlarm(String alarmXML) throws XMLDBException {

		// String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:searchAlarm(\"" + metaData + "\", " + alarmXML + ")";

		String xQueryStr = alarmFunctionConstructor("lk:searchAlarm", alarmXML);

		Collection collection = existConnectionHolder.getCollection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<Alarm> alarmList = new ArrayList<Alarm>();

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			Alarm alarm;
			try {
				alarm = AlarmDocument.Factory.parse(xmlContent).getAlarm();
				alarmList.add(alarm);
			} catch (XmlException e) {
				e.printStackTrace();
				return null;
			}
		}
		return alarmList;
	}

	public Boolean deleteAlarm(String alarmXML) {

		// String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:deleteAlarmLock(\"" + metaData + "\", " + alarmXML + ")";

		String xQueryStr = alarmFunctionConstructor("lk:deleteAlarmLock", alarmXML);

		Collection collection = existConnectionHolder.getCollection();

		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return false;
		}

		try {
			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public ArrayList<AlarmReport> getAlarmReportList(String date1, String date2, String alarmLevel, String alarmName, String alarmUser) throws XMLDBException {

		/*
		 * String funcDef = "lk:getAlarms(\"" + metaData + "\", \"" + date1 + "\", \"" + date2 + "\", \"" + alarmLevel + "\", \"" + alarmName + "\", \"" + alarmUser + "\")";
		 * 
		 * String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + funcDef;
		 */

		String xQueryStr = alarmFunctionConstructor("lk:getAlarms", date1, date2, alarmLevel, alarmName, alarmUser);

		Collection collection = existConnectionHolder.getCollection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<AlarmReport> alarmList = new ArrayList<AlarmReport>();
		AlarmReport alarmReport = null;

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			try {
				alarmReport = AlarmReportDocument.Factory.parse(xmlContent).getAlarmReport();
				alarmList.add(alarmReport);
			} catch (XmlException e) {
				e.printStackTrace();
				return null;
			}
		}
		return alarmList;
	}

	public Boolean updateAlarm(String alarmXML) {

		// String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:updateAlarmLock(\"" + metaData + "\", " + alarmXML + ")";

		String xQueryStr = alarmFunctionConstructor("lk:updateAlarmLock", alarmXML);

		Collection collection = existConnectionHolder.getCollection();
		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return false;
		}

		try {
			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public Boolean insertAlarm(String alarmXML) {

		// String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:insertAlarmLock(\"" + metaData + "\", " + alarmXML + ")";

		String xQueryStr = alarmFunctionConstructor("lk:insertAlarmLock", alarmXML);

		Collection collection = existConnectionHolder.getCollection();
		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return false;
		}

		try {
			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean deleteDBConnection(String dbConnectionXML) {

		String xQueryStr = dbFunctionConstructor("db:deleteDbConnection", dbConnectionXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean insertDBConnection(String dbConnectionXML) {
		Collection collection = existConnectionHolder.getCollection();

		String dataFile = xmlsUrl + CommonConstantDefinitions.DB_CONNECTIONS_DATA;

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.dbNsUrl + xQueryModuleUrl + "/moduleDBConnectionsOperations.xquery\";" + CommonConstantDefinitions.decNsDbc + CommonConstantDefinitions.decNsCom + "db:insertDbConnection(\"" + dataFile + "\", " + dbConnectionXML + ")";

		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean updateDBConnection(String dbConnectionXML) {

		String xQueryStr = dbFunctionConstructor("db:updateDbConnectionLock", dbConnectionXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean checkDBConnectionName(String dbConnectionXML) {

		String xQueryStr = dbFunctionConstructor("db:checkDbConnectionName", dbConnectionXML);

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		if (objectList != null && objectList.size() > 0) {
			return false;
		}

		return true;
	}

	public DbProperties searchDBByID(String id) {

		String xQueryStr = dbFunctionConstructor("db:getDbConnection", id);

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		DbProperties dbProperties = null;

		for (XmlObject currentObject : objectList) {
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

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		for (XmlObject currentObject : objectList) {
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
			ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);
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
			ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);
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
			ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public DbConnectionProfile searchDBAccessByID(String id) {
		String xQueryStr = dbFunctionConstructor("db:getDbCP", id);

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		DbConnectionProfile dbConnectionProfile = null;

		for (XmlObject currentObject : objectList) {
			dbConnectionProfile = ((DbConnectionProfileDocument) currentObject).getDbConnectionProfile();
		}

		return dbConnectionProfile;
	}

	public ArrayList<SLA> searchSla(String slaXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleSLAOperations.xquery\";" + "hs:searchSLA(" + slaXML + ")";

		ArrayList<SLA> slaList = new ArrayList<SLA>();

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				SLA sla;
				try {
					sla = SLADocument.Factory.parse(xmlContent).getSLA();
					slaList.add(sla);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}

		} catch (XMLDBException e1) {
			e1.printStackTrace();
		}

		return slaList;
	}

	public boolean deleteSla(String slaXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleSLAOperations.xquery\";" + "hs:deleteSLALock(" + slaXML + ")";

		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean insertSla(String slaXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleSLAOperations.xquery\";" + "hs:insertSlaLock(" + slaXML + ")";

		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean updateSla(String slaXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleSLAOperations.xquery\";" + "hs:updateSLALock(" + slaXML + ")";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public ArrayList<CalendarProperties> getCalendars() {

		ArrayList<CalendarProperties> calendarList = new ArrayList<CalendarProperties>();

		String xQueryStr = calendarFunctionConstructor("hs:calendars");

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		for (XmlObject currentObject : objectList) {
			CalendarProperties calendar = ((CalendarPropertiesDocument) currentObject).getCalendarProperties();
			calendarList.add(calendar);
		}

		return calendarList;
	}

	public Alarm searchAlarmByName(String alarmName) {

		Alarm alarm = null;

		try {

			Collection collection = existConnectionHolder.getCollection();
			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			// String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:searchAlarmByName(\"" + metaData + "\", " + "\"" + alarmname + "\"" + ")";

			String xQueryStr = alarmFunctionConstructor("lk:searchAlarmByName", alarmName);

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					alarm = AlarmDocument.Factory.parse(xmlContent).getAlarm();
				} catch (XmlException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}

			}

		} catch (XMLDBException xmldbException) {
			xmldbException.printStackTrace();
		}

		return alarm;
	}

	public ArrayList<String> getSoftwareList() {

		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.ksNsUrl + xQueryModuleUrl + "/moduleProgramProvisioningOperations.xquery\";" + "ks:ppList(1,10)";

		ArrayList<String> softwareList = new ArrayList<String>();

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				License license;
				try {
					license = LicenseDocument.Factory.parse(xmlContent).getLicense();
					softwareList.add(license.getName());
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}

		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return softwareList;
	}

	public SLA searchSlaByID(String slaId) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleSLAOperations.xquery\";" + "hs:searchSlaBySlaId(" + slaId + ")";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			SLA sla = null;

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					sla = SLADocument.Factory.parse(xmlContent).getSLA();

					return sla;

				} catch (XmlException e) {
					e.printStackTrace();
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		return null;
	}

	public ArrayList<License> searchProvision(String provisionXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.ksNsUrl + xQueryModuleUrl + "/moduleProgramProvisioningOperations.xquery\";" + "ks:searchPP(" + provisionXML + ")";

		ArrayList<License> provisionList = new ArrayList<License>();

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				License provision;
				try {
					provision = LicenseDocument.Factory.parse(xmlContent).getLicense();
					provisionList.add(provision);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return provisionList;
	}

	public boolean deleteProvision(String provisionXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.ksNsUrl + xQueryModuleUrl + "/moduleProgramProvisioningOperations.xquery\";" + "ks:deletePpLock(" + provisionXML + ")";

		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean insertProvision(String provisionXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.ksNsUrl + xQueryModuleUrl + "/moduleProgramProvisioningOperations.xquery\";" + "ks:insertPpLock(" + provisionXML + ")";

		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean updateProvision(String provisionXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.ksNsUrl + xQueryModuleUrl + "/moduleProgramProvisioningOperations.xquery\";" + "ks:updatePpLock(" + provisionXML + ")";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public License searchProvisionByID(String provisionId) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.ksNsUrl + xQueryModuleUrl + "/moduleProgramProvisioningOperations.xquery\";" + "ks:searchPpByID(" + provisionId + ")";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			License license = null;

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					license = LicenseDocument.Factory.parse(xmlContent).getLicense();

					return license;

				} catch (XmlException e) {
					e.printStackTrace();
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		return null;
	}

	public boolean insertWSDefinition(String wsPropertiesXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.wsoNsUrl + xQueryModuleUrl + "/moduleWebServiceOperations.xquery\";" + CommonConstantDefinitions.decNsWs + CommonConstantDefinitions.decNsCom + "wso:insertWSDefinition(\"" + metaData + "\", " + wsPropertiesXML + ")";

		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public JobProperties getTemplateJobFromName(String documentName, String jobName) {
		Collection collection = existConnectionHolder.getCollection();

		JobProperties jobProperties = JobProperties.Factory.newInstance();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + CommonConstantDefinitions.decNsSt + "hs:getJobFromJobName(" + "xs:string(\"" + xmlsUrl + documentName + "\")" + ", \"" + jobName + "\")";

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
					jobProperties = JobPropertiesDocument.Factory.parse(xmlContent).getJobProperties();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return jobProperties;
	}

	public JobProperties getJob(String documentName, String jobPath, String jobName) {
		Collection collection = existConnectionHolder.getCollection();

		JobProperties jobProperties = null;

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");

			service.setProperty("indent", "yes");

			String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:getJob(" + "xs:string(\"" + xmlsUrl + documentName + "\")" + "," + jobPath + ", xs:string(\"" + jobName + "\"))";

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					jobProperties = JobPropertiesDocument.Factory.parse(xmlContent).getJobProperties();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}

			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}
		return jobProperties;
	}

	public boolean insertJob(String documentName, String jobPropertiesXML, String jobPath) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:insertJobLock(" + "xs:string(\"" + xmlsUrl + documentName + "\")" + "," + jobPropertiesXML + "," + jobPath + " )";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.toString();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public String getJobExistence(String documentName, String jobPath, String jobName) {
		Collection collection = existConnectionHolder.getCollection();

		String sonuc = null;

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");

			service.setProperty("indent", "yes");

			String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:getJobExistence(" + "xs:string(\"" + xmlsUrl + documentName + "\")" + "," + jobPath + ", xs:string(\"" + jobName + "\"))";

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				sonuc = xmlContent.toString();
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}
		return sonuc;
	}

	public JobProperties getJobFromId(String documentName, String jobId) {
		Collection collection = existConnectionHolder.getCollection();

		JobProperties jobProperties = null;

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");

			service.setProperty("indent", "yes");

			String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:getJobFromId(" + "xs:string(\"" + xmlsUrl + documentName + "\")" + ", " + jobId + ")";

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					jobProperties = JobPropertiesDocument.Factory.parse(xmlContent).getJobProperties();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}

			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}
		return jobProperties;
	}

	public ArrayList<SWAgent> getAgents() {
		Collection collection = existConnectionHolder.getCollection();

		// String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAgentOperations.xquery\";" + "lk:getAgents(\"" + metaData + "\")";

		String xQueryStr = agentFunctionConstructor("lk:getAgents");

		ArrayList<SWAgent> agentList = new ArrayList<SWAgent>();

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				SWAgents agents;
				try {
					agents = SWAgentsDocument.Factory.parse(xmlContent).getSWAgents();

					for (SWAgent agent : agents.getSWAgentArray()) {
						agentList.add(agent);
					}

				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}

		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return agentList;
	}

	public ArrayList<SLA> getSlaList() {
		Collection collection = existConnectionHolder.getCollection();

		String dataFile = xmlsUrl + CommonConstantDefinitions.SLA_DATA;

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleSLAOperations.xquery\";" + "hs:slaList(\"" + dataFile + "\", " + "1,2)";

		ArrayList<SLA> slaList = new ArrayList<SLA>();

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				SLA sla;
				try {
					sla = SLADocument.Factory.parse(xmlContent).getSLA();
					slaList.add(sla);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return slaList;
	}

	public ArrayList<WebServiceDefinition> getWebServiceListForActiveUser(int userId) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.wsoNsUrl + xQueryModuleUrl + "/moduleWebServiceOperations.xquery\";" + CommonConstantDefinitions.decNsWs + CommonConstantDefinitions.decNsCom + "wso:getWSDefinitionListForActiveUser(\"" + metaData + "\", " + userId + ")";

		ArrayList<WebServiceDefinition> webServiceList = new ArrayList<WebServiceDefinition>();

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				WebServiceDefinition webServiceDefinition;
				try {
					webServiceDefinition = WebServiceDefinitionDocument.Factory.parse(xmlContent).getWebServiceDefinition();
					webServiceList.add(webServiceDefinition);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}

		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return webServiceList;
	}

	public ArrayList<FtpProperties> getFtpConnectionList() {

		ArrayList<FtpProperties> ftpConnectionList = new ArrayList<FtpProperties>();

		String xQueryStr = ftpFunctionConstructor("fc:getFTPConnectionList");

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		for (XmlObject currentObject : objectList) {
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

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		for (XmlObject currentObject : objectList) {
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

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		for (XmlObject currentObject : objectList) {
			DbConnectionProfile connectionProfile = ((DbConnectionProfileDocument) currentObject).getDbConnectionProfile();
			dbProfileList.add(connectionProfile);
		}

		return dbProfileList;
	}

	public ResourceListType searchResource(String resourceXML) {

		ResourceListType resourceList = null;

		try {

			String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.rscNsUrl + xQueryModuleUrl + "/moduleResourcesOperations.xquery\";" + "rsc:searchResources(" + resourceXML + ")";

			Collection collection = existConnectionHolder.getCollection();
			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					resourceList = ResourceListDocument.Factory.parse(xmlContent).getResourceList();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException xmldbException) {
			xmldbException.printStackTrace();
		}
		return resourceList;
	}

	public boolean deleteResource(String resourceXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.rscNsUrl + xQueryModuleUrl + "/moduleResourcesOperations.xquery\";" + "rsc:deleteResourceLock(" + resourceXML + ")";

		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public ResourceType searchResourceByResourceName(String resourceName) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.rscNsUrl + xQueryModuleUrl + "/moduleResourcesOperations.xquery\";" + "rsc:searchResourcesByResourceName(\"" + resourceName + "\")";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			ResourceListType resourceList = null;

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					resourceList = ResourceListDocument.Factory.parse(xmlContent).getResourceList();

					return resourceList.getResourceArray(0);

				} catch (XmlException e) {
					e.printStackTrace();
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		return null;
	}

	public boolean insertResource(String resourceXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.rscNsUrl + xQueryModuleUrl + "/moduleResourcesOperations.xquery\";" + "rsc:insertResourceLock(" + resourceXML + ")";

		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean updateResource(String resourceXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.rscNsUrl + xQueryModuleUrl + "/moduleResourcesOperations.xquery\";" + "rsc:updateResourceLock(" + resourceXML + ")";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public ArrayList<CalendarProperties> searchCalendar(String calendarPropertiesXML) {

		ArrayList<CalendarProperties> calendarList = new ArrayList<CalendarProperties>();

		String xQueryStr = calendarFunctionConstructor("hs:searchCalendar", calendarPropertiesXML);

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		for (XmlObject currentObject : objectList) {
			CalendarProperties calendar = ((CalendarPropertiesDocument) currentObject).getCalendarProperties();
			calendarList.add(calendar);
		}

		return calendarList;
	}

	public boolean insertCalendar(String calendarPropertiesXML) {

		String xQueryStr = calendarFunctionConstructor("hs:insertCalendarLock", calendarPropertiesXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);
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
			ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public CalendarProperties searchCalendarByID(String calendarID) {

		String xQueryStr = calendarFunctionConstructor("hs:searchCalendarByID", calendarID);

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		for (XmlObject currentObject : objectList) {
			CalendarProperties calendar = ((CalendarPropertiesDocument) currentObject).getCalendarProperties();

			return calendar;
		}

		return null;
	}

	public boolean updateCalendar(String calendarPropertiesXML) {

		String xQueryStr = calendarFunctionConstructor("hs:updateCalendarLock", calendarPropertiesXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public Scenario getScenarioFromId(String documentName, String scenarioId) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:getScenarioFromId(" + "xs:string(\"" + xmlsUrl + documentName + "\")" + ", " + scenarioId + " )";

		Scenario scenario = null;

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
					scenario = ScenarioDocument.Factory.parse(xmlContent).getScenario();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}

		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		return scenario;
	}

	public Scenario getScenario(String documentName, String scenarioPath, String scenarioName) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:getScenario(" + "xs:string(\"" + xmlsUrl + documentName + "\")" + "," + scenarioPath + ", \"" + scenarioName + "\" )";

		Scenario scenario = null;

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
					scenario = ScenarioDocument.Factory.parse(xmlContent).getScenario();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}

		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		return scenario;
	}

	public String getScenarioExistence(String documentName, String scenarioPath, String scenarioName) {
		Collection collection = existConnectionHolder.getCollection();

		String sonuc = null;

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");

			service.setProperty("indent", "yes");

			String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:getScenarioExistence(" + "xs:string(\"" + xmlsUrl + documentName + "\")" + "," + scenarioPath + ", xs:string(\"" + scenarioName + "\"))";

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				sonuc = xmlContent.toString();
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}
		return sonuc;
	}

	public boolean insertScenario(String documentName, String scenarioXML, String scenarioPath) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:insertScenarioLock(" + "xs:string(\"" + xmlsUrl + documentName + "\")" + "," + scenarioXML + "," + scenarioPath + " )";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.toString();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public LocalStats getStatsReport(int derinlik, int runId, int jobId, String refPoint) throws XMLDBException {

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleReportOperations.xquery\";" + CommonConstantDefinitions.decNsRep + "hs:calculateBaseStats(" + derinlik + "," + runId + "," + jobId + "," + refPoint + "())";

		Collection collection = existConnectionHolder.getCollection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		LocalStats localStats = null;

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			try {
				localStats = LocalStatsDocument.Factory.parse(xmlContent).getLocalStats();
			} catch (XmlException e) {
				e.printStackTrace();
				return null;
			}

		}

		return localStats;
	}

	public JobArray getOverallReport(int derinlik, int runType, int jobId, String refPoint, String orderType, int jobCount) throws XMLDBException {

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

	/*
	 * public ArrayList<Job> getLiveJobsScenarios(int derinlik, int runType, String orderType, int jobCount) throws XMLDBException {
	 * 
	 * TlosProcessData tlosProcessData = TlosProcessData.Factory.newInstance();
	 * 
	 * SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
	 * 
	 * String xQueryStr = xQueryNsHeader + hsNsUrl + xQueryModuleUrl + "/moduleReportOperations.xquery\"; decNsRep + " hs:getJobArray(hs:getJobsReport(" + derinlik + "," + runType + ",0, true()),\"" + orderType + "\"," + jobCount + ")";
	 * 
	 * Collection collection = existConnectionHolder.getCollection(); XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0"); service.setProperty("indent", "yes");
	 * 
	 * ResourceSet result = service.query(xQueryStr); ResourceIterator i = result.getIterator();
	 * 
	 * ArrayList<Job> jobs = new ArrayList<Job>(); JobArray jobArray = null;
	 * 
	 * while (i.hasMoreResources()) { Resource r = i.nextResource(); String xmlContent = (String) r.getContent();
	 * 
	 * try {
	 * 
	 * // XmlOptions xmlOption = new XmlOptions(); // Map <String,String> map=new HashMap<String,String>(); // map.put("","http://www.likyateknoloji.com/XML_report_types"); // xmlOption.setLoadSubstituteNamespaces(map);
	 * 
	 * jobArray = JobArrayDocument.Factory.parse(xmlContent).getJobArray1(); } catch (XmlException e) { e.printStackTrace(); return null; } } for (Job job : jobArray.getJobArray()) { jobs.add(job); }
	 * 
	 * return jobs; }
	 */
	public SWAgent checkAgent(String resource, int jmxPort) {
		Collection collection = existConnectionHolder.getCollection();

		// String dataFile = xmlsUrl + CommonConstantDefinitions.AGENT_DATA;

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAgentOperations.xquery\";" + CommonConstantDefinitions.decNsRes + "declare namespace agnt = \"http://www.likyateknoloji.com/XML_agent_types\";" + "lk:searchAgent(\"" + metaData + "\", " + "xs:string(\"" + resource + "\")," + jmxPort + ")";

		SWAgent agent = null;

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
					agent = SWAgentDocument.Factory.parse(xmlContent).getSWAgent();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
		return agent;
	}

	public Statistics getDensityReport(String state, String substate, String status, String startDateTime, String endDateTime, String step) throws XMLDBException {

		long startTime = System.currentTimeMillis();

		String xQueryStr = localFunctionConstructor("moduleDensityCalculations.xquery", "density:recStat", CommonConstantDefinitions.densityNsUrl, state, substate, status, startDateTime, endDateTime, step);

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		Statistics stat = null;
		for (XmlObject currentObject : objectList) {
			stat = ((StatisticsDocument) currentObject).getStatistics();
		}

		System.err.println(" dashboardReport : " + DateUtils.dateDiffWithNow(startTime) + "ms");

		return stat;
	}

	public Report getDashboardReport(int derinlik) throws XMLDBException {

		long startTime = System.currentTimeMillis();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleReportOperations.xquery\";" + "hs:jobStateListbyRunId(" + derinlik + ",0,0,true())";

		Collection collection = existConnectionHolder.getCollection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);

		ResourceIterator i = result.getIterator();
		Report report = null;

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			try {
				report = ReportDocument.Factory.parse(xmlContent).getReport();
			} catch (XmlException e) {
				e.printStackTrace();
				return null;
			}

		}
		System.err.println(" dashboardReport : " + DateUtils.dateDiffWithNow(startTime) + "ms");
		return report;
	}

	public ArrayList<AlarmInfoTypeClient> getJobAlarmHistory(String jobId, Boolean transformToLocalTime) {

		Collection collection = existConnectionHolder.getCollection();

		/*
		 * String funcDef = "lk:jobAlarmListbyRunId(\""+ metaData + "\", 3, 0, " + jobId + ", false(), 30)"; String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + funcDef;
		 */

		// verilen isin son 3 rundaki alarmini runid'den bagimsiz olarak
		// getiriyor
		// son 30 gun icerisinde ariyor
		String xQueryStr = alarmFunctionConstructor("lk:jobAlarmListbyRunId", "3", "0", jobId, "false()", "30");

		ArrayList<AlarmInfoTypeClient> alarmList = new ArrayList<AlarmInfoTypeClient>();

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm alarm;
				try {
					alarm = com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Factory.parse(xmlContent).getAlarm();

					alarmList.add(fillAlarmInfoTypeClient(service, alarm, jobId, transformToLocalTime));
				} catch (XmlException e) {
					e.printStackTrace();
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		return alarmList;
	}

	private AlarmInfoTypeClient fillAlarmInfoTypeClient(XPathQueryService service, com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm alarm, String jobId, Boolean transformToLocalTime) throws XMLDBException {

		AlarmInfoTypeClient alarmInfoTypeClient = new AlarmInfoTypeClient();
		alarmInfoTypeClient.setAlarmId(alarm.getAlarmId() + "");
		alarmInfoTypeClient.setAlarmHistoryId(alarm.getAHistoryId() + "");
		alarmInfoTypeClient.setCreationDate(DateUtils.calendarToString(alarm.getCreationDate(), transformToLocalTime));
		alarmInfoTypeClient.setLevel(alarm.getLevel() + "");

		String xQueryStr;
		ResourceSet result;
		ResourceIterator i;
		/*
		 * // alarmin gerceklestigi kaynak ve agant id'sini set ediyor
		 * 
		 * String dataFile = xmlsUrl + CommonConstantDefinitions.AGENT_DATA; xQueryStr = xQueryNsHeader + "lk=\"http://likya.tlos.com/\"" + xQueryModuleUrl + "/moduleAgentOperations.xquery\";" + decNsRes + "lk:searchAgentByAgentId(\"" + metaData + "\", " + alarm.getAgentId() + ")";
		 * 
		 * result = service.query(xQueryStr); i = result.getIterator(); SWAgent agent = null;
		 * 
		 * while (i.hasMoreResources()) { Resource r = i.nextResource(); String xmlContent = (String) r.getContent();
		 * 
		 * try { agent = SWAgentDocument.Factory.parse(xmlContent).getSWAgent(); break; } catch (XmlException e) { e.printStackTrace(); } } alarmInfoTypeClient.setResourceName(agent.getResource().getStringValue() + "." + alarm.getAgentId());
		 */

		alarmInfoTypeClient.setResourceName(alarm.getAgentId());

		// bu is icin kullanilan warnBy degerini set ediyor

		String warnByStr = "";
		for (int j = 0; j < alarm.getSubscriber().getAlarmChannelTypes().getWarnByArray().length; j++) {

			if (alarm.getSubscriber().getAlarmChannelTypes().getWarnByArray(j).getId().compareTo(BigInteger.valueOf(1)) == 0)
				warnByStr = warnByStr + "e-mail;";
			if (alarm.getSubscriber().getAlarmChannelTypes().getWarnByArray(j).getId().compareTo(BigInteger.valueOf(2)) == 0)
				warnByStr = warnByStr + "SMS;";
			if (alarm.getSubscriber().getAlarmChannelTypes().getWarnByArray(j).getId().compareTo(BigInteger.valueOf(3)) == 0)
				warnByStr = warnByStr + "GUI;";
		}

		alarmInfoTypeClient.setWarnBy(warnByStr);

		// alarm tipini set ediyor
		if (alarm.getCaseManagement().getStateManagement() != null) {
			alarmInfoTypeClient.setAlarmType("State");
		} else if (alarm.getCaseManagement().getSLAManagement() != null) {
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
			xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleUserOperations.xquery\";" + "hs:searchUserByUserId(" + alarm.getSubscriber().getPerson().getId() + ")";

			result = service.query(xQueryStr);
			i = result.getIterator();
			Person user = null;

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					user = PersonDocument.Factory.parse(xmlContent).getPerson();
					break;
				} catch (XmlException e) {
					e.printStackTrace();
				}
			}

			if (user != null) {
				alarmInfoTypeClient.setSubscriber(user.getUserName());
			} else {
				alarmInfoTypeClient.setSubscriber("-");
			}
		}

		// alarm id'ye gore alarmi dbden sorguluyor
		// xQueryStr = xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:searchAlarmByAlarmId(\"" + metaData + "\", " + alarm.getAlarmId() + ")";

		xQueryStr = alarmFunctionConstructor("lk:searchAlarmByAlarmId", alarm.getAlarmId().toString());

		result = service.query(xQueryStr);
		i = result.getIterator();
		Alarm alarmDefinition = null;

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			try {
				alarmDefinition = AlarmDocument.Factory.parse(xmlContent).getAlarm();

				alarmInfoTypeClient.setAlarmName(alarmDefinition.getName());
				alarmInfoTypeClient.setDescription(alarmDefinition.getDesc());

				break;
			} catch (XmlException e) {
				e.printStackTrace();
			}
		}

		return alarmInfoTypeClient;
	}

	public ArrayList<JobInfoTypeClient> getJobResultList(String jobId, int runNumber, Boolean transformToLocalTime) {

		Collection collection = existConnectionHolder.getCollection();

		String scenDocName = CommonConstantDefinitions.DAILY_SCENARIOS_DATA;
		String seqDocName = CommonConstantDefinitions.SEQUENCE_DATA;

		String funcName = "hs:jobResultListbyRunId(\"" + xmlsUrl + scenDocName + "\", " + "\"" + xmlsUrl + seqDocName + "\", " + runNumber + ", 0, " + jobId + ", false())";

		// verilen isin son runNumber sayisi kadar ki calisma listesini runid'den bagimsiz olarak getiriyor
		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + funcName;

		ArrayList<JobInfoTypeClient> jobs = new ArrayList<JobInfoTypeClient>();

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			JobProperties jobProperties = null;

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					jobProperties = JobPropertiesDocument.Factory.parse(xmlContent).getJobProperties();
					jobs.add(fillJobInfoTypeClient(jobProperties, jobs.size(), transformToLocalTime));

				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		return jobs;
	}

	private JobInfoTypeClient fillJobInfoTypeClient(JobProperties jobProperties, int jobListSize, Boolean transformToLocalTime) {

		JobInfoTypeClient jobInfoTypeClient = new JobInfoTypeClient();

		jobInfoTypeClient.setLSIDateTime(jobProperties.getLSIDateTime());
		jobInfoTypeClient.setJobId(jobProperties.getID());
		jobInfoTypeClient.setJobKey(jobProperties.getBaseJobInfos().getJsName());
		jobInfoTypeClient.setJobCommand(jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommand());
		jobInfoTypeClient.setJobCommandType(jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommandType().toString());
		jobInfoTypeClient.setJobPath(jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobPath());
		jobInfoTypeClient.setJobLogPath(jobProperties.getBaseJobInfos().getJobLogPath());
		jobInfoTypeClient.setJobLogName(jobProperties.getBaseJobInfos().getJobLogFile());
		jobInfoTypeClient.setoSystem(jobProperties.getBaseJobInfos().getOSystem().toString());
		jobInfoTypeClient.setJobPriority(jobProperties.getBaseJobInfos().getJobPriority().intValue());

		if (jobProperties.getTimeManagement().getJsPlannedTime().getStartTime() != null) {
			jobInfoTypeClient.setJobPlanTime(DateUtils.jobTimeToString(jobProperties.getTimeManagement().getJsPlannedTime().getStartTime().getTime(), transformToLocalTime));
		}

		if (jobProperties.getTimeManagement().getJsPlannedTime().getStopTime() != null) {
			jobInfoTypeClient.setJobPlanEndTime(DateUtils.jobTimeToString(jobProperties.getTimeManagement().getJsPlannedTime().getStopTime().getTime(), transformToLocalTime));
		}

		jobInfoTypeClient.setJobTimeOut(jobProperties.getTimeManagement().getJsTimeOut().toString() + jobProperties.getTimeManagement().getJsTimeOut().getUnit());

		if (jobProperties.getTimeManagement().getJsRealTime() != null) {
			jobInfoTypeClient.setPlannedExecutionDate(DateUtils.jobRealTimeToStringReport(jobProperties.getTimeManagement().getJsRealTime(), true, transformToLocalTime));

			// is bitmisse
			if (jobProperties.getTimeManagement().getJsRealTime().getStopTime() != null) {
				jobInfoTypeClient.setCompletionDate(DateUtils.jobRealTimeToStringReport(jobProperties.getTimeManagement().getJsRealTime(), false, transformToLocalTime));
			} else {
				jobInfoTypeClient.setCompletionDate("?");
			}

			// o gunku calisma mi yoksa eski calisma mi oldugunu kontrol ediyor
			boolean pastExecution = true;
			if (jobListSize == 0) {
				pastExecution = false;
			}

			jobInfoTypeClient.setWorkDuration(DateUtils.getJobWorkDuration(jobProperties.getTimeManagement().getJsRealTime(), pastExecution));
		}

		// LiveStateInfo listesindeki ilk eleman alinarak islem yapildi, yani guncel state i alindi
		jobInfoTypeClient.setOver(jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(StateName.FINISHED));
		jobInfoTypeClient.setLiveStateInfo(jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0));

		jobInfoTypeClient.setJobAutoRetry(jobProperties.getCascadingConditions().getJobAutoRetry().toString());
		jobInfoTypeClient.setSafeRestart(jobProperties.getCascadingConditions().getJobSafeToRestart().toString());

		if (jobProperties.getDependencyList() != null) {
			List<Item> myList = Arrays.asList(jobProperties.getDependencyList().getItemArray());
			ArrayList<Item> dependencyList = new ArrayList<Item>(myList);
			Iterator<Item> dependencyListIterator = dependencyList.iterator();
			ArrayList<String> depenArrayList = new ArrayList<String>();
			while (dependencyListIterator.hasNext()) {
				depenArrayList.add(dependencyListIterator.next().getJsName());
			}
			jobInfoTypeClient.setJobDependencyList(depenArrayList);
		}

		jobInfoTypeClient.setAgentId(jobProperties.getAgentId());

		/*
		 * if (jobInfoTypeClient.getAgentId() > 0) { SWAgent agent = TlosSpaceWide.getSpaceWideRegistry ().getAgentManagerReference().getSwAgentsCache().get(jobInfoTypeClient.getAgentId() + "");
		 * 
		 * jobInfoTypeClient.setResourceName(agent.getResource().getStringValue()); }
		 */

		return jobInfoTypeClient;
	}

	public com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm getAlarmHistoryById(int alarmHistoryId) {
		Collection collection = existConnectionHolder.getCollection();

		// String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:searchAlarmHistoryById(\"" + metaData + "\", " + alarmHistoryId + ")";

		String xQueryStr = alarmFunctionConstructor("lk:searchAlarmHistoryById", alarmHistoryId + "");

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm alarm = null;

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					alarm = com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Factory.parse(xmlContent).getAlarm();

					return alarm;
				} catch (XmlException e) {
					e.printStackTrace();
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		return null;
	}

	public SLA getSlaBySlaId(int slaId) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleSLAOperations.xquery\";" + "hs:searchSlaBySlaId(" + slaId + ")";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			SLA sla = null;

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					sla = SLADocument.Factory.parse(xmlContent).getSLA();

					return sla;
				} catch (XmlException e) {
					e.printStackTrace();
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

	public boolean updateJob(String documentName, String jobPropertiesXML, String jobPath) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:updateJobLock(" + "xs:string(\"" + xmlsUrl + documentName + "\")" + "," + jobPropertiesXML + "," + jobPath + " )";

		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public ArrayList<WSAccessInfoTypeClient> searchWSAccessProfiles(String userAccessProfileXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.wsoNsUrl + xQueryModuleUrl + "/moduleWebServiceOperations.xquery\";" + CommonConstantDefinitions.decNsWs + CommonConstantDefinitions.decNsCom + "wso:getWSDefinitionList(\"" + metaData + "\")";

		HashMap<BigInteger, WebServiceDefinition> wsDefinitionList = new HashMap<BigInteger, WebServiceDefinition>();

		XPathQueryService service = null;
		ResourceSet result = null;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			result = service.query(xQueryStr);
			ResourceIterator iterator = result.getIterator();

			while (iterator.hasMoreResources()) {
				Resource r = iterator.nextResource();
				String xmlContent = (String) r.getContent();

				WebServiceDefinition wsProperties;
				try {
					wsProperties = WebServiceDefinitionDocument.Factory.parse(xmlContent).getWebServiceDefinition();
					wsDefinitionList.put(wsProperties.getID(), wsProperties);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException exception) {
			exception.printStackTrace();
			return null;
		}

		xQueryStr = xQueryNsHeader + CommonConstantDefinitions.wsoNsUrl + xQueryModuleUrl + "/moduleWebServiceOperations.xquery\";" + CommonConstantDefinitions.decNsWs + CommonConstantDefinitions.decNsCom + "wso:searchWSAccessProfiles(\"" + metaData + "\", " + userAccessProfileXML + ")";

		ArrayList<WSAccessInfoTypeClient> wsAccessInfoTypeClients = new ArrayList<WSAccessInfoTypeClient>();

		try {
			result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				UserAccessProfile userAccessProfile;

				try {
					userAccessProfile = UserAccessProfileDocument.Factory.parse(xmlContent).getUserAccessProfile();

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
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException exception) {
			exception.printStackTrace();
			return null;
		}

		return wsAccessInfoTypeClients;
	}

	public boolean deleteWSAccessProfile(String userAccessProfileXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.wsoNsUrl + xQueryModuleUrl + "/moduleWebServiceOperations.xquery\";" + CommonConstantDefinitions.decNsWs + CommonConstantDefinitions.decNsCom + "wso:deleteWSAccessProfile(\"" + metaData + "\", " + userAccessProfileXML + ")";

		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean insertWSAccessProfile(String userAccessProfileXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.wsoNsUrl + xQueryModuleUrl + "/moduleWebServiceOperations.xquery\";" + CommonConstantDefinitions.decNsWs + CommonConstantDefinitions.decNsCom + "wso:insertWSAccessProfile(\"" + metaData + "\", " + userAccessProfileXML + ")";

		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public UserAccessProfile searchWSAccessByID(String id) {
		Collection collection = existConnectionHolder.getCollection();

		UserAccessProfile userAccessProfile = null;

		try {
			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.wsoNsUrl + xQueryModuleUrl + "/moduleWebServiceOperations.xquery\";" + CommonConstantDefinitions.decNsWs + CommonConstantDefinitions.decNsCom + "wso:getWSAccessProfile(\"" + metaData + "\", " + id + ")";

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					userAccessProfile = UserAccessProfileDocument.Factory.parse(xmlContent).getUserAccessProfile();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}

			}

		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		return userAccessProfile;
	}

	public boolean updateWSAccessProfile(String userAccessProfileXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.wsoNsUrl + xQueryModuleUrl + "/moduleWebServiceOperations.xquery\";" + CommonConstantDefinitions.decNsWs + CommonConstantDefinitions.decNsCom + "wso:updateWSAccessProfileLock(\"" + metaData + "\", " + userAccessProfileXML + ")";

		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.getSize();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public ArrayList<FtpProperties> searchFTPAccessConnection(String ftpAccessPropertiesXML) {

		ArrayList<FtpProperties> ftpConnectionList = new ArrayList<FtpProperties>();

		String xQueryStr = ftpFunctionConstructor("fc:searchFTPConnection", ftpAccessPropertiesXML);

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		for (XmlObject currentObject : objectList) {
			FtpProperties ftpProperties = ((FtpPropertiesDocument) currentObject).getFtpProperties();
			ftpConnectionList.add(ftpProperties);
		}

		return ftpConnectionList;
	}

	public boolean deleteFTPAccessConnection(String ftpAccessPropertiesXML) {

		String xQueryStr = ftpFunctionConstructor("fc:deleteFTPConnection", ftpAccessPropertiesXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean checkFTPConnectionName(String ftpAccessPropertiesXML) {

		String xQueryStr = ftpFunctionConstructor("fc:checkFTPConnectionName", ftpAccessPropertiesXML);

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		if (objectList != null && objectList.size() > 0) {
			return false;
		}

		return true;
	}

	public boolean insertFTPAccessConnection(String ftpAccessPropertiesXML) {

		String xQueryStr = ftpFunctionConstructor("fc:insertFTPConnection", ftpAccessPropertiesXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public FtpProperties searchFTPConnectionById(int ftpConnectionId) {

		String xQueryStr = ftpFunctionConstructor("fc:searchFTPConnectionById", ftpConnectionId + "");

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		FtpProperties ftpProperties = null;
		for (XmlObject currentObject : objectList) {
			ftpProperties = ((FtpPropertiesDocument) currentObject).getFtpProperties();

			return ftpProperties;
		}

		return null;
	}

	public boolean updateFTPAccessConnection(String ftpAccessPropertiesXML) {

		String xQueryStr = ftpFunctionConstructor("fc:updateFTPConnectionLock", ftpAccessPropertiesXML);

		try {
			@SuppressWarnings("unused")
			ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean updateScenario(String documentName, String scenarioPath, String scenarioXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsSt + CommonConstantDefinitions.decNsDat + "hs:updateScenarioLock(" + "xs:string(\"" + xmlsUrl + documentName + "\")" + "," + scenarioPath + "," + scenarioXML + " )";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.toString();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean deleteScenario(String documentName, String scenarioPath, String scenarioXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:deleteScenarioLock(" + "xs:string(\"" + xmlsUrl + documentName + "\")" + "," + scenarioXML + "," + scenarioPath + " )";

		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.toString();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean deleteJob(String documentName, String jobPath, String jobPropertiesXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:deleteJobLock(" + "xs:string(\"" + xmlsUrl + documentName + "\")" + "," + jobPropertiesXML + "," + jobPath + " )";

		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			result.toString();
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public ExistConnectionHolder getExistConnectionHolder() {
		return existConnectionHolder;
	}

	public void setExistConnectionHolder(ExistConnectionHolder existConnectionHolder) {
		this.existConnectionHolder = existConnectionHolder;
	}

}

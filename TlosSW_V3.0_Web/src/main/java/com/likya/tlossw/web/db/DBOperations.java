package com.likya.tlossw.web.db;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
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
import com.likya.tlos.model.xmlbeans.sla.SLADocument;
import com.likya.tlos.model.xmlbeans.sla.SLADocument.SLA;
import com.likya.tlos.model.xmlbeans.swresourcens.ResourceListDocument;
import com.likya.tlos.model.xmlbeans.swresourcens.ResourceListType;
import com.likya.tlos.model.xmlbeans.swresourcens.ResourceType;
import com.likya.tlos.model.xmlbeans.user.PersonDocument;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlos.model.xmlbeans.useroutput.UserResourceMapDocument;
import com.likya.tlos.model.xmlbeans.useroutput.UserResourceMapDocument.UserResourceMap;
import com.likya.tlos.model.xmlbeans.webservice.WebServiceDefinitionDocument;
import com.likya.tlos.model.xmlbeans.webservice.WebServiceDefinitionDocument.WebServiceDefinition;
import com.likya.tlossw.model.DBAccessInfoTypeClient;
import com.likya.tlossw.model.auth.ResourcePermission;
import com.likya.tlossw.model.jmx.JmxAppUser;
import com.likya.tlossw.utils.XmlBeansTransformer;
import com.likya.tlossw.web.exist.ExistConnectionHolder;

@ManagedBean(name = "dbOperations")
@SessionScoped
public class DBOperations implements Serializable {

	private static final long serialVersionUID = 8575509360685840755L;

	@ManagedProperty(value = "#{existConnectionHolder}")
	private ExistConnectionHolder existConnectionHolder;

	public ArrayList<SWAgent> searchAgent(String agentXML) {

		ArrayList<SWAgent> agentList = null;

		try {

			String xQueryStr = "xquery version \"1.0\";" + "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAgentOperations.xquery\";" + "declare namespace res=\"http://www.likyateknoloji.com/resource-extension-defs\";"
					+ "lk:searchAgent(" + agentXML + ")";

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

			String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleUserOperations.xquery\";" + "hs:searchUser(" + personXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace sq=\"http://sq.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleSequenceOperations.xquery\";" + "sq:getNextId(\"" + component + "\")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAgentOperations.xquery\";" + "declare namespace res=\"http://www.likyateknoloji.com/resource-extension-defs\";"
				+ "lk:updateAgentLock(" + agentXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleUserOperations.xquery\";" + "hs:updateUserLock(" + personXML + ")";

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
		String xQueryStr = "xquery version \"1.0\";" + "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAgentOperations.xquery\";" + "declare namespace res=\"http://www.likyateknoloji.com/resource-extension-defs\";"
				+ "lk:insertAgentLock(" + agentXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleUserOperations.xquery\";" + "hs:insertUserLock(" + personXML + ")";

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
		String xQueryStr = "xquery version \"1.0\";" + "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAgentOperations.xquery\";" + "declare namespace res=\"http://www.likyateknoloji.com/resource-extension-defs\";"
				+ "lk:searchAgentByResource(" + "\"" + resourcename + "\"" + ")";

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
		String xQueryStr = "xquery version \"1.0\";" + "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAgentOperations.xquery\";" + "declare namespace res=\"http://www.likyateknoloji.com/resource-extension-defs\";"
				+ "lk:searchAgentByAgentId(" + id + ")";

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

			String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleUserOperations.xquery\";" + "hs:searchUserByUsername(" + "\"" + username + "\"" + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAgentOperations.xquery\";" + "declare namespace res=\"http://www.likyateknoloji.com/resource-extension-defs\";"
				+ "lk:deleteAgentLock(" + agentXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleUserOperations.xquery\";" + "hs:deleteUserLock(" + personXML + ")";

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
			String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleGetResourceListByRole.xquery\";" + "hs:query_username(xs:string(\"" + jmxAppUser.getAppUser().getUsername() + "\"))";

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				UserResourceMap myUserPermission;
				try {
					myUserPermission = UserResourceMapDocument.Factory.parse(xmlContent).getUserResourceMap();
					if (myUserPermission.getPerson().getUserPassword().equals(jmxAppUser.getAppUser().getPassword())) {
						jmxAppUser.setAppUser(XmlBeansTransformer.personToAppUser(myUserPermission));
						return jmxAppUser;
					}
				} catch (XmlException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
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

			String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleScenarioOperations.xquery\";" + "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";  "
					+ "declare namespace dat = \"http://www.likyateknoloji.com/XML_data_types\";  " + "declare namespace state-types = \"http://www.likyateknoloji.com/state-types\";  " + "hs:getTlosDataXml(xs:string(\"" + documentName + "\"))";

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
		Collection collection = existConnectionHolder.getCollection();

		ArrayList<ResourcePermission> resourcePermission = new ArrayList<ResourcePermission>();

		XPathQueryService service = null;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/modulePermissionOperations.xquery\";" + "hs:getPermisions()";

			ResourceIterator i;

			ResourceSet result = service.query(xQueryStr);
			i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();
				Permission permission = PermissionDocument.Factory.parse(xmlContent).getPermission();
				resourcePermission.add(XmlBeansTransformer.permissionsToResourcePermissions(permission));
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		} catch (XmlException e) {
			e.printStackTrace();
			return null;
		}

		return resourcePermission;
	}

	public boolean updatePermissions(String permissionsXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/modulePermissionOperations.xquery\";" + "declare namespace per = \"http://www.likyateknoloji.com/XML_permission_types\";  "
				+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";  " + "hs:updatePermissionsLock(" + permissionsXML + " )";

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

	public ArrayList<DbProperties> searchDBConnection(String dbConnectionXML) {
		Collection collection = existConnectionHolder.getCollection();

		ArrayList<DbProperties> dbConnectionList = new ArrayList<DbProperties>();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace db=\"http://db.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleDBConnectionsOperations.xquery\";" + "declare namespace dbc = \"http://www.likyateknoloji.com/XML_dbconnection_types\";"
				+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";" + "db:searchDbConnection(" + dbConnectionXML + ")";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				DbProperties dbProperties;
				try {
					dbProperties = DbPropertiesDocument.Factory.parse(xmlContent).getDbProperties();
					dbConnectionList.add(dbProperties);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException e1) {
			e1.printStackTrace();
			return null;
		}

		return dbConnectionList;
	}

	public ArrayList<Alarm> getAlarms() {

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAlarmOperations.xquery\";" + "lk:alarms()";

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

	public ArrayList<Person> getUsers() throws XMLDBException {

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleUserOperations.xquery\";" + "hs:users()";

		Collection collection = existConnectionHolder.getCollection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<Person> prsList = new ArrayList<Person>();

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

		return prsList;
	}

	public ArrayList<JobProperties> getJobList() throws XMLDBException {

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAlarmOperations.xquery\";" + "lk:jobList(1,5)";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAlarmOperations.xquery\";" + "lk:scenarioList()";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace rsc=\"http://rsc.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleResourcesOperations.xquery\";" + "rsc:resourcesList(1,10)";

		Collection collection = existConnectionHolder.getCollection();

		ArrayList<RNSEntryType> resourceList = new ArrayList<RNSEntryType>();

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				RNSEntryType resource;
				try {
					resource = RNSEntryType.Factory.parse(xmlContent);
					resourceList.add(resource);

				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return resourceList;
	}

	public ArrayList<Alarm> searchAlarm(String alarmXML) throws XMLDBException {

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAlarmOperations.xquery\";" + "lk:searchAlarm(" + alarmXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAlarmOperations.xquery\";" + "lk:deleteAlarmLock(" + alarmXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAlarmOperations.xquery\";" + "lk:getAlarms(\"" + date1 + "\", \"" + date2 + "\", \"" + alarmLevel + "\", \"" + alarmName
				+ "\", \"" + alarmUser + "\")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAlarmOperations.xquery\";" + "lk:updateAlarmLock(" + alarmXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAlarmOperations.xquery\";" + "lk:insertAlarmLock(" + alarmXML + ")";

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
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace db=\"http://db.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleDBConnectionsOperations.xquery\";" + "declare namespace dbc = \"http://www.likyateknoloji.com/XML_dbconnection_types\";"
				+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";" + "db:deleteDbConnection(" + dbConnectionXML + ")";

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

	public boolean insertDBConnection(String dbConnectionXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace db=\"http://db.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleDBConnectionsOperations.xquery\";" + "declare namespace dbc = \"http://www.likyateknoloji.com/XML_dbconnection_types\";"
				+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";" + "db:insertDbConnection(" + dbConnectionXML + ")";

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
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace db=\"http://db.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleDBConnectionsOperations.xquery\";" + "declare namespace dbc = \"http://www.likyateknoloji.com/XML_dbconnection_types\";"
				+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";" + "db:updateDbConnectionLock(" + dbConnectionXML + ")";

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

	public boolean checkDBConnectionName(String dbConnectionXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace db=\"http://db.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleDBConnectionsOperations.xquery\";" + "declare namespace dbc = \"http://www.likyateknoloji.com/XML_dbconnection_types\";"
				+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";" + "db:checkDbConnectionName(" + dbConnectionXML + ")";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			if (result.getSize() > 0) {
				return false;
			}

		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public DbProperties searchDBByID(String id) {
		Collection collection = existConnectionHolder.getCollection();

		DbProperties dbProperties = null;

		try {
			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			String xQueryStr = "xquery version \"1.0\";" + "import module namespace db=\"http://db.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleDBConnectionsOperations.xquery\";" + "declare namespace dbc = \"http://www.likyateknoloji.com/XML_dbconnection_types\";"
					+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";" + "db:getDbConnection(" + id + ")";

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					dbProperties = DbPropertiesDocument.Factory.parse(xmlContent).getDbProperties();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}

			}

		} catch (XMLDBException xmldbException) {
			xmldbException.printStackTrace();
		}

		return dbProperties;
	}

	public ArrayList<DbProperties> getDbList() {
		Collection collection = existConnectionHolder.getCollection();

		ArrayList<DbProperties> dbList = new ArrayList<DbProperties>();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace db=\"http://db.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleDBConnectionsOperations.xquery\";" + "declare namespace dbc = \"http://www.likyateknoloji.com/XML_dbconnection_types\";"
				+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";" + "db:getDbConnectionAll()";

		try {
			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				DbProperties dbProperties;
				try {
					dbProperties = DbPropertiesDocument.Factory.parse(xmlContent).getDbProperties();
					dbList.add(dbProperties);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException xmldbException) {
			xmldbException.printStackTrace();
			return null;
		}

		return dbList;
	}

	public ArrayList<DBAccessInfoTypeClient> searchDBAccessProfile(String dbAccessProfileXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace db=\"http://db.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleDBConnectionsOperations.xquery\";" + "declare namespace dbc = \"http://www.likyateknoloji.com/XML_dbconnection_types\";"
				+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";" + "db:getDbConnectionAll()";

		HashMap<BigInteger, DbProperties> dbDefinitionList = new HashMap<BigInteger, DbProperties>();

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

				DbProperties dbProperties;
				try {
					dbProperties = DbPropertiesDocument.Factory.parse(xmlContent).getDbProperties();
					dbDefinitionList.put(dbProperties.getID(), dbProperties);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException exception) {
			exception.printStackTrace();
			return null;
		}

		xQueryStr = "xquery version \"1.0\";" + "import module namespace db=\"http://db.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleDBConnectionsOperations.xquery\";" + "declare namespace dbc = \"http://www.likyateknoloji.com/XML_dbconnection_types\";"
				+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";" + "db:searchDbAccessProfile(" + dbAccessProfileXML + ")";

		ArrayList<DBAccessInfoTypeClient> dbAccessInfoTypeClients = new ArrayList<DBAccessInfoTypeClient>();

		try {
			result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				DbConnectionProfile dbConnectionProfile;

				try {
					dbConnectionProfile = DbConnectionProfileDocument.Factory.parse(xmlContent).getDbConnectionProfile();

					DbProperties dbProperties = dbDefinitionList.get(dbConnectionProfile.getDbDefinitionId());

					DBAccessInfoTypeClient dbInfoTypeClient = new DBAccessInfoTypeClient();
					dbInfoTypeClient.setDbConnectionProfile(dbConnectionProfile);
					dbInfoTypeClient.setConnectionName(dbProperties.getConnectionName());
					dbInfoTypeClient.setDbType(dbProperties.getDbType().toString());

					dbAccessInfoTypeClients.add(dbInfoTypeClient);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException exception) {
			exception.printStackTrace();
			return null;
		}

		return dbAccessInfoTypeClients;
	}

	public boolean deleteDBAccessProfile(String dbAccessProfileXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace db=\"http://db.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleDBConnectionsOperations.xquery\";" + "declare namespace dbc = \"http://www.likyateknoloji.com/XML_dbconnection_types\";"
				+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";" + "db:deleteDbAccessProfile(" + dbAccessProfileXML + ")";

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

	public boolean insertDBAccessProfile(String dbAccessProfileXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace db=\"http://db.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleDBConnectionsOperations.xquery\";" + "declare namespace dbc = \"http://www.likyateknoloji.com/XML_dbconnection_types\";"
				+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";" + "db:insertDbAccessProfile(" + dbAccessProfileXML + ")";

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

	public boolean updateDBAccessProfile(String dbAccessProfileXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace db=\"http://db.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleDBConnectionsOperations.xquery\";" + "declare namespace dbc = \"http://www.likyateknoloji.com/XML_dbconnection_types\";"
				+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";" + "db:updateDbAccessProfileLock(" + dbAccessProfileXML + ")";

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

	public DbConnectionProfile searchDBAccessByID(String id) {
		Collection collection = existConnectionHolder.getCollection();

		DbConnectionProfile dbConnectionProfile = null;

		try {
			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			String xQueryStr = "xquery version \"1.0\";" + "import module namespace db=\"http://db.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleDBConnectionsOperations.xquery\";" + "declare namespace dbc = \"http://www.likyateknoloji.com/XML_dbconnection_types\";"
					+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";" + "db:getDbCP(" + id + ")";

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					dbConnectionProfile = DbConnectionProfileDocument.Factory.parse(xmlContent).getDbConnectionProfile();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}

			}

		} catch (XMLDBException xmldbException) {
			xmldbException.printStackTrace();
		}

		return dbConnectionProfile;
	}

	public ArrayList<SLA> searchSla(String slaXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleSLAOperations.xquery\";" + "hs:searchSLA(" + slaXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleSLAOperations.xquery\";" + "hs:deleteSLALock(" + slaXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleSLAOperations.xquery\";" + "hs:insertSlaLock(" + slaXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleSLAOperations.xquery\";" + "hs:updateSLALock(" + slaXML + ")";

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

		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleCalendarOperations.xquery\";" + "hs:calendars()";

		ArrayList<CalendarProperties> calendarList = new ArrayList<CalendarProperties>();

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				CalendarProperties calendar;
				try {
					calendar = CalendarPropertiesDocument.Factory.parse(xmlContent).getCalendarProperties();
					calendarList.add(calendar);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}

			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return calendarList;
	}

	public Alarm searchAlarmByName(String alarmname) {

		Alarm alarm = null;

		try {

			Collection collection = existConnectionHolder.getCollection();
			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			String xQueryStr = "xquery version \"1.0\";" + "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAlarmOperations.xquery\";" + "lk:searchAlarmByName(" + "\"" + alarmname + "\"" + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace ks=\"http://ks.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleProgramProvisioningOperations.xquery\";" + "ks:ppList(1,10)";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleSLAOperations.xquery\";" + "hs:searchSlaBySlaId(" + slaId + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace ks=\"http://ks.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleProgramProvisioningOperations.xquery\";" + "ks:searchPP(" + provisionXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace ks=\"http://ks.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleProgramProvisioningOperations.xquery\";" + "ks:deletePpLock(" + provisionXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace ks=\"http://ks.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleProgramProvisioningOperations.xquery\";" + "ks:insertPpLock(" + provisionXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace ks=\"http://ks.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleProgramProvisioningOperations.xquery\";" + "ks:updatePpLock(" + provisionXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace ks=\"http://ks.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleProgramProvisioningOperations.xquery\";" + "ks:searchPpByID(" + provisionId + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace wso=\"http://wso.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleWebServiceOperations.xquery\";" + "declare namespace ws = \"http://www.likyateknoloji.com/XML_web_service_types\";"
				+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";" + "wso:insertWSDefinition(" + wsPropertiesXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleScenarioOperations.xquery\";" + "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";  "
				+ "declare namespace dat = \"http://www.likyateknoloji.com/XML_data_types\";  " + "declare namespace state-types = \"http://www.likyateknoloji.com/state-types\";  " + "hs:getJobFromJobName(" + "xs:string(\"" + documentName + "\")" + ", \"" + jobName + "\")";

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

	public boolean insertJob(String documentName, String jobPropertiesXML, String jobPath) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleScenarioOperations.xquery\";" + "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";  "
				+ "declare namespace dat = \"http://www.likyateknoloji.com/XML_data_types\";  " + "hs:insertJobLock(" + "xs:string(\"" + documentName + "\")" + "," + jobPropertiesXML + "," + jobPath + " )";

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

	public JobProperties getJob(String documentName, String jobPath, String jobName) {
		Collection collection = existConnectionHolder.getCollection();

		JobProperties jobProperties = null;

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");

			service.setProperty("indent", "yes");

			String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleScenarioOperations.xquery\";" + "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";  "
					+ "declare namespace dat = \"http://www.likyateknoloji.com/XML_data_types\";  " + "hs:getJob(" + "xs:string(\"" + documentName + "\")" + "," + jobPath + ", xs:string(\"" + jobName + "\"))";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAgentOperations.xquery\";" + "lk:getAgents()";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs = \"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleSLAOperations.xquery\";" + "hs:slaList(1,2)";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace wso=\"http://wso.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleWebServiceOperations.xquery\";" + "declare namespace ws = \"http://www.likyateknoloji.com/XML_web_service_types\";"
				+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";" + "wso:getWSDefinitionListForActiveUser(" + userId + ")";

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
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\"; import module namespace fc = \"http://fc.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleFTPConnectionsOperations.xquery\";" + "fc:getFTPConnectionList()";

		ArrayList<FtpProperties> ftpConnectionList = new ArrayList<FtpProperties>();

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				FtpProperties ftpProperties;
				try {
					ftpProperties = FtpPropertiesDocument.Factory.parse(xmlContent).getFtpProperties();
					ftpConnectionList.add(ftpProperties);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}

		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return ftpConnectionList;
	}

	/**
	 * Veri tabaninda tanimli olan veri tabani tanimlari listesini sorguluyor
	 * 
	 * @return veri tabani tanimlari listesini donuyor
	 */
	public ArrayList<DbProperties> getDBConnections() {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace db=\"http://db.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleDBConnectionsOperations.xquery\";" + "declare namespace dbc = \"http://www.likyateknoloji.com/XML_dbconnection_types\";"
				+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";" + "db:getDbConnectionAll()";

		ArrayList<DbProperties> dbList = new ArrayList<DbProperties>();

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				DbProperties dbProperties;
				try {
					dbProperties = DbPropertiesDocument.Factory.parse(xmlContent).getDbProperties();
					dbList.add(dbProperties);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return dbList;
	}

	/**
	 * Veri tabaninda tanimli olan veri tabani erisim profilleri listesini
	 * sorguluyor
	 * 
	 * @return veri tabani erisim profilleri listesini donuyor
	 */
	public ArrayList<DbConnectionProfile> getDBProfiles() {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace db=\"http://db.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleDBConnectionsOperations.xquery\";" + "declare namespace dbc = \"http://www.likyateknoloji.com/XML_dbconnection_types\";"
				+ "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";" + "db:getDbProfileAll()";

		ArrayList<DbConnectionProfile> dbProfileList = new ArrayList<DbConnectionProfile>();

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				DbConnectionProfile connectionProfile;
				try {
					connectionProfile = DbConnectionProfileDocument.Factory.parse(xmlContent).getDbConnectionProfile();
					dbProfileList.add(connectionProfile);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return dbProfileList;
	}

	public ResourceListType searchResource(String resourceXML) {

		ResourceListType resourceList = null;

		try {

			String xQueryStr = "xquery version \"1.0\";" + "import module namespace rsc=\"http://rsc.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleResourcesOperations.xquery\";" + "rsc:searchResources(" + resourceXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace rsc=\"http://rsc.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleResourcesOperations.xquery\";" + "rsc:deleteResourceLock(" + resourceXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace rsc=\"http://rsc.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleResourcesOperations.xquery\";" + "rsc:searchResourcesByResourceName(\"" + resourceName + "\")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace rsc=\"http://rsc.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleResourcesOperations.xquery\";" + "rsc:insertResource(" + resourceXML + ")";

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

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace rsc=\"http://rsc.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleResourcesOperations.xquery\";" + "rsc:updateResourceLock(" + resourceXML + ")";

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
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleCalendarOperations.xquery\";" + "hs:searchCalendar(" + calendarPropertiesXML + ")";

		ArrayList<CalendarProperties> calendarList = new ArrayList<CalendarProperties>();

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				CalendarProperties calendar;
				try {
					calendar = CalendarPropertiesDocument.Factory.parse(xmlContent).getCalendarProperties();
					calendarList.add(calendar);
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}

			}
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		return calendarList;
	}

	public boolean insertCalendar(String calendarPropertiesXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleCalendarOperations.xquery\";" + "hs:insertCalendarLock(" + calendarPropertiesXML + ")";

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

	public boolean deleteCalendar(String calendarPropertiesXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleCalendarOperations.xquery\";" + "hs:deleteCalendarLock(" + calendarPropertiesXML + ")";

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

	public CalendarProperties searchCalendarByID(String calendarID) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleCalendarOperations.xquery\";" + "hs:searchCalendarByID(" + calendarID + ")";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			CalendarProperties calendar = null;

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					calendar = CalendarPropertiesDocument.Factory.parse(xmlContent).getCalendarProperties();

					return calendar;

				} catch (XmlException e) {
					e.printStackTrace();
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		return null;
	}

	public boolean updateCalendar(String calendarPropertiesXML) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleCalendarOperations.xquery\";" + "hs:updateCalendarLock(" + calendarPropertiesXML + ")";

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

	public Scenario getScenario(String documentName, String scenarioPath, String scenarioName) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleScenarioOperations.xquery\";" + "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";  "
				+ "declare namespace dat = \"http://www.likyateknoloji.com/XML_data_types\";  " + "hs:getScenario(" + "xs:string(\"" + documentName + "\")" + "," + scenarioPath + ", \"" + scenarioName + "\" )";

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

	public boolean insertScenario(String documentName, String scenarioXML, String scenarioPath) {
		Collection collection = existConnectionHolder.getCollection();

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleScenarioOperations.xquery\";" + "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";  "
				+ "declare namespace dat = \"http://www.likyateknoloji.com/XML_data_types\";  " + "hs:insertScenarioLock(" + "xs:string(\"" + documentName + "\")" + "," + scenarioXML + "," + scenarioPath + " )";

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

	public JobArray getJobArrayReport (int derinlik, String orderType, int jobCount) throws XMLDBException {

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleReportOperations.xquery\";" + "hs:getJobArray(hs:getJobsReport("+derinlik+",0,0, true()),\"" + orderType + "\"," + jobCount + ")";

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
				XmlOptions xmlOption = new XmlOptions(); 
				Map <String,String> map=new HashMap<String,String>(); 
				map.put("","http://www.likyateknoloji.com/XML_report_types");   
				xmlOption.setLoadSubstituteNamespaces(map); 
				
				jobArray = JobArrayDocument.Factory.parse(xmlContent,xmlOption).getJobArray1();
			} catch (XmlException e) {
				e.printStackTrace();
				return null;
			}
		}
		return jobArray;
	}
	
	public ExistConnectionHolder getExistConnectionHolder() {
		return existConnectionHolder;
	}

	public void setExistConnectionHolder(ExistConnectionHolder existConnectionHolder) {
		this.existConnectionHolder = existConnectionHolder;
	}
}

/*
 * TlosSW_V1.0
 * com.likya.tlos.jmx.mp.helper : RemoteDBOperator.java
 * @author Serkan Ta�
 * Tarih : Apr 6, 2009 4:30:25 PM
 */

package com.likya.tlossw.jmx.beans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.ogf.schemas.rns.x2009.x12.rns.RNSEntryType;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument;
import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.alarm.AlarmDocument;
import com.likya.tlos.model.xmlbeans.alarm.AlarmDocument.Alarm;
import com.likya.tlos.model.xmlbeans.alarmhistory.AlarmReportDocument;
import com.likya.tlos.model.xmlbeans.alarmhistory.AlarmReportDocument.AlarmReport;
import com.likya.tlos.model.xmlbeans.calendar.CalendarPropertiesDocument;
import com.likya.tlos.model.xmlbeans.calendar.CalendarPropertiesDocument.CalendarProperties;
import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument.TlosConfigInfo;
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
import com.likya.tlos.model.xmlbeans.nrperesults.CpuKullanimType.Timein;
import com.likya.tlos.model.xmlbeans.nrperesults.MessageDocument.Message;
import com.likya.tlos.model.xmlbeans.nrperesults.NrpeDataDocument;
import com.likya.tlos.model.xmlbeans.nrperesults.NrpeDataDocument.NrpeData;
import com.likya.tlos.model.xmlbeans.nrperesults.ResponseDocument.Response;
import com.likya.tlos.model.xmlbeans.nrperesults.ResponseDocument.Response.Command;
import com.likya.tlos.model.xmlbeans.permission.PermissionDocument;
import com.likya.tlos.model.xmlbeans.permission.PermissionDocument.Permission;
import com.likya.tlos.model.xmlbeans.programprovision.LicenseDocument;
import com.likya.tlos.model.xmlbeans.programprovision.LicenseDocument.License;
import com.likya.tlos.model.xmlbeans.report.ReportDocument;
import com.likya.tlos.model.xmlbeans.report.ReportDocument.Report;
import com.likya.tlos.model.xmlbeans.sla.ForWhatAttribute.ForWhat;
import com.likya.tlos.model.xmlbeans.sla.SLADocument;
import com.likya.tlos.model.xmlbeans.sla.SLADocument.SLA;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.user.PersonDocument;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlos.model.xmlbeans.useroutput.UserResourceMapDocument;
import com.likya.tlos.model.xmlbeans.useroutput.UserResourceMapDocument.UserResourceMap;
import com.likya.tlos.model.xmlbeans.webservice.UserAccessProfileDocument;
import com.likya.tlos.model.xmlbeans.webservice.UserAccessProfileDocument.UserAccessProfile;
import com.likya.tlos.model.xmlbeans.webservice.WebServiceDefinitionDocument;
import com.likya.tlos.model.xmlbeans.webservice.WebServiceDefinitionDocument.WebServiceDefinition;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.db.utils.DBUtils;
import com.likya.tlossw.jmx.JMXTLSServer;
import com.likya.tlossw.model.AlarmInfoTypeClient;
import com.likya.tlossw.model.DBAccessInfoTypeClient;
import com.likya.tlossw.model.auth.ResourcePermission;
import com.likya.tlossw.model.client.resource.CpuInfoTypeClient;
import com.likya.tlossw.model.client.resource.DiskInfoTypeClient;
import com.likya.tlossw.model.client.resource.MemoryInfoTypeClient;
import com.likya.tlossw.model.client.resource.MonitorAgentInfoTypeClient;
import com.likya.tlossw.model.client.resource.NrpeDataInfoTypeClient;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.model.jmx.JmxAppUser;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.XmlBeansTransformer;
import com.likya.tlossw.utils.date.DateUtils;

public class RemoteDBOperator implements RemoteDBOperatorMBean {

	String xQueryModuleUrl;
	String xmlsUrl;
	
	public RemoteDBOperator() {
		xQueryModuleUrl = TlosSpaceWide.getSpaceWideRegistry().getxQueryModuleUrl();
		xmlsUrl = TlosSpaceWide.getSpaceWideRegistry().getXmlsUrl();
	}

	@Override
	public int getNbChanges() {
		return 0;
	}

	@Override
	public String getState() {
		return null;
	}

	@Override
	public void reset() {

	}

	@Override
	public void setState(String s) {

	}

	@Override
	public Object checkUser(JmxAppUser jmxAppUser) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxAppUser)) {
			return false;
		}

		if ((jmxAppUser.getAppUser() == null) || (jmxAppUser.getAppUser().getUsername() == null) || (jmxAppUser.getAppUser().getPassword() == null)) {
			return false;
		}

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		Collection collection = spaceWideRegistry.getEXistColllection();

		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");

		service.setProperty("indent", "yes");

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleGetResourceListByRole.xquery\";" + 
				"hs:query_username(xs:string(\"" + jmxAppUser.getAppUser().getUsername() + "\"))";

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

		return false;
	}

	public ArrayList<Person> searchUser(JmxUser jmxUser, String personXML) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleUserOperations.xquery\";" + 
				"hs:searchUser(" + personXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	public ArrayList<Person> users(JmxUser jmxUser) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleUserOperations.xquery\";" + "hs:users()";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	public Object insertUser(JmxUser jmxUser, String personXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleUserOperations.xquery\";" + "hs:insertUserLock(" + personXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	public Object updateUser(JmxUser jmxUser, String personXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleUserOperations.xquery\";" + "hs:updateUserLock(" + personXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	public Object deleteUser(JmxUser jmxUser, String personXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleUserOperations.xquery\";" + "hs:deleteUserLock(" + personXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	public Person searchUserByUsername(JmxUser jmxUser, String username) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleUserOperations.xquery\";" + "hs:searchUserByUsername(" + "\"" + username + "\"" + ")";

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		Person person = null;

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

		return person;
	}

	public Object insertCalendar(JmxUser jmxUser, String calendarPropertiesXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleCalendarOperations.xquery\";" + "hs:insertCalendarLock(" + calendarPropertiesXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	public ArrayList<String> calendarNames(JmxUser jmxUser) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleCalendarOperations.xquery\";" + "hs:calendarNames()";

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<String> calendarNames = new ArrayList<String>();

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			calendarNames.add((String) r.getContent());
		}

		return calendarNames;
	}

	public Object insertScenario(JmxUser jmxUser, String documentName, String scenarioXML, String scenarioPath) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + 
				CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:insertScenarioLock(" + "xs:string(\"" + xmlsUrl + documentName + "\")" + "," + scenarioXML + "," + scenarioPath + " )";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	public Object insertJob(JmxUser jmxUser, String documentName, String jobPropertiesXML, String jobPath) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + 
				CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:insertJobLock(" + "xs:string(\"" + documentName + "\")" + "," + jobPropertiesXML + "," + jobPath + " )";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	public TlosConfigInfo getTlosConfig(JmxUser jmxUser) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		return DBUtils.getTlosConfig();

	}

	public ArrayList<CalendarProperties> searchCalendar(JmxUser jmxUser, String calendarPropertiesXML) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleCalendarOperations.xquery\";" + "hs:searchCalendar(" + calendarPropertiesXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<CalendarProperties> calendarList = new ArrayList<CalendarProperties>();

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

		return calendarList;
	}

	public ArrayList<CalendarProperties> calendars(JmxUser jmxUser) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleCalendarOperations.xquery\";" + "hs:calendars()";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<CalendarProperties> calendarList = new ArrayList<CalendarProperties>();

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

		return calendarList;
	}

	public Object updateCalendar(JmxUser jmxUser, String calendarPropertiesXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleCalendarOperations.xquery\";" + "hs:updateCalendarLock(" + calendarPropertiesXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
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

	public Object deleteCalendar(JmxUser jmxUser, String calendarPropertiesXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleCalendarOperations.xquery\";" + "hs:deleteCalendarLock(" + calendarPropertiesXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
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

	/* Kullanilmiyor ise kaldir. hs 13.12.2012 */
	public int getMaxScenarioId(JmxUser jmxUser, String documentName) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return -1;
		}

		int maxId = -1;
		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + 
				CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:getMaxScenarioId(" + "xs:string(\"" + documentName + "\")" + ")";

		XPathQueryService service;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			System.out.println();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				maxId = Integer.parseInt((String) r.getContent());
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return -1;
		}

		return maxId;

	}

	public Object deleteScenario(JmxUser jmxUser, String documentName, String scenarioXML, String scenarioPath) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + 
				CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:deleteScenarioLock(" + "xs:string(\"" + documentName + "\")" + "," + scenarioXML + "," + scenarioPath + " )";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
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

	public Object deleteJob(JmxUser jmxUser, String documentName, String jobPropertiesXML, String jobPath) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + 
				CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:deleteJobLock(" + "xs:string(\"" + documentName + "\")" + "," + jobPropertiesXML + "," + jobPath + " )";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
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

	public Object updateJob(JmxUser jmxUser, String documentName, String jobPropertiesXML, String jobPath) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + 
				CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:updateJobLock(" + "xs:string(\"" + documentName + "\")" + "," + jobPropertiesXML + "," + jobPath + " )";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
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

	public JobProperties getJobFromId(JmxUser jmxUser, String documentName, String jobPath, String jobId) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service;

		JobProperties jobProperties = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");

			service.setProperty("indent", "yes");

			String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + 
					CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:getJobFromId(" + "xs:string(\"" + documentName + "\")" + ", xs:string(\"" + jobId + "\"))";

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
		} catch (XMLDBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return jobProperties;
	}

	public Scenario getScenario(JmxUser jmxUser, String documentName, String scenariPath, String scenarioName) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + 
				CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:getScenario(" + "xs:string(\"" + xmlsUrl + documentName + "\")" + "," + scenariPath + ", \"" + scenarioName + "\" )";

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		Scenario scenario = null;

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

		return scenario;
	}

	public Object updateTlosConfig(JmxUser jmxUser, String tlosConfigInfoXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleTlosManagementOperations.xquery\";" + 
				CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsCon + "hs:updateTlosConfigInfoLock(" + tlosConfigInfoXML + " )";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
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

	public ArrayList<ResourcePermission> getPermissions(JmxUser jmxUser) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/modulePermissionOperations.xquery\";" + "hs:getPermisions()";

		ResourceIterator i;
		ArrayList<ResourcePermission> resourcePermission = new ArrayList<ResourcePermission>();

		try {
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

	public Object updatePermissions(JmxUser jmxUser, String permissionsXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/modulePermissionOperations.xquery\";" + 
				CommonConstantDefinitions.decNsPer + CommonConstantDefinitions.decNsCom + "hs:updatePermissionsLock(" + permissionsXML + " )";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
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

	public Object updateScenario(JmxUser jmxUser, String documentName, String scenarioPath, String scenarioXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + 
				CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:updateScenarioLock(" + "xs:string(\"" + documentName + "\")" + "," + scenarioPath + "," + scenarioXML + " )";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	public ArrayList<JobProperties> getReportJobs(JmxUser jmxUser, String jobPropertiesXML, String jobPath) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleReportOperations.xquery\";" + 
				CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:getJobs(" + jobPropertiesXML + "," + jobPath + " )";

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<JobProperties> jobs = new ArrayList<JobProperties>();
		JobProperties jobProperties = null;

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			try {
				jobProperties = JobPropertiesDocument.Factory.parse(xmlContent).getJobProperties();
				jobs.add(jobProperties);
			} catch (XmlException e) {
				e.printStackTrace();
				return null;
			}

		}

		return jobs;
	}

	public ArrayList<AlarmReport> getAlarmReportList(JmxUser jmxUser, String date1, String date2, String alarmLevel, String alarmName, String alarmUser) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:getAlarms(\"" + date1 + "\", \"" + date2 + "\", \"" + alarmLevel + "\", \"" + alarmName + "\", \"" + alarmUser + "\")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	public TlosProcessData getTlosDataXml(JmxUser jmxUser, String documentName) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		TlosProcessData tlosProcessData = TlosProcessData.Factory.newInstance();
		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		
		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + 
				CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + CommonConstantDefinitions.decNsSt + "hs:getTlosDataXml(xs:string(\"" + xmlsUrl + documentName + "\"))";

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

//	public byte[] getHtmlDoc(JmxUser jmxUser, String str) throws XMLDBException {
//
//		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
//			return null;
//		}
//
//		try {
//			return getHtmlContent();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return null;
//	}

//	public byte[] getPdfDoc(JmxUser jmxUser, String str) throws XMLDBException {
//
//		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
//			return null;
//		}
//
//		try {
//			return getPdfContent();
//		} catch (FOPException e) {
//			e.printStackTrace();
//		} catch (TransformerException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return null;
//
//		// EnterpriseRegistery enterpriseRegistery = TlosEnterprise.getEnterpriseRegistery();
//		// Collection collection = enterpriseRegistery.getEXistColllection();
//		// XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
//		// service.setProperty("indent", "yes");
//		//
//		// String xQueryStr = "xquery version \"1.0\";" +
//		// "import module namespace hs=\"http://hs.tlos.com/\" at \"xmldb:exist://db/TLOS/modules/moduleXslOperations.xquery\";" +
//		// "declare namespace fo = \"http://www.w3.org/1999/XSL/Format\";  "+
//		// "declare namespace xslfo = \"http://exist-db.org/xquery/xslfo\";  "+
//		// "hs:generateXslDoc()";
//		//
//		// ResourceSet result = service.query(xQueryStr);
//		// String pdfDoc = (String) result.getResource(0).getContent();
//		//
//		// Base64Decoder base64Decoder = new Base64Decoder();
//		// base64Decoder.translate(pdfDoc);
//		//
//		// return base64Decoder.getByteArray();
//	}

	public Object insertTrace(JmxUser jmxUser, String traceXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}
		// long startTime = System.currentTimeMillis();

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleTraceOperations.xquery\";" + "hs:insertTrace(" + traceXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

		// System.err.println(" insertTrace : " + DateUtils.dateDiffWithNow(startTime) + "ms");
		return true;
	}

	// XML lerde transformasyon yapmak icin
	// Hakan


//	public static byte[] getHtmlContent() throws IOException {
//
//		String tlosDataXslDoc = null;
//		String tlosData = null;
//		String tlosCalendar = null;
//		String tlosUser = null;
//
//		try {
//			tlosDataXslDoc = getDbDoc("hs:tlosDataXsl()");
//			tlosData = getDbDoc("hs:tlosData()");
//			tlosCalendar = getDbDoc("hs:tlosCalendar()");
//			tlosUser = getDbDoc("hs:tlosUser()");
//		} catch (XMLDBException e) {
//			e.printStackTrace();
//		} catch (XSLLoadException e) {
//			e.printStackTrace();
//		}
//
//		StringReader xslReader = new StringReader(tlosDataXslDoc);
//		StringReader tDataReader = new StringReader(tlosData);
//
//		File tlosCalendarFile = new File("tlosSWCalendar10.xml");
//		FileOutputStream calendarFos = new FileOutputStream(tlosCalendarFile);
//
//		OutputStreamWriter calendarOsw = new OutputStreamWriter(calendarFos, "UTF8");
//		BufferedWriter calendarBufferedWriter = new BufferedWriter(calendarOsw);
//
//		calendarBufferedWriter.write(tlosCalendar);
//		calendarBufferedWriter.close();
//
//		File tlosUserFile = new File("tlosSWUser10.xml");
//		FileOutputStream userFos = new FileOutputStream(tlosUserFile);
//
//		OutputStreamWriter userOsw = new OutputStreamWriter(userFos, "UTF8");
//		BufferedWriter userBufferedWriter = new BufferedWriter(userOsw);
//
//		userBufferedWriter.write(tlosUser);
//		userBufferedWriter.close();
//
//		TransformerFactory tFactory = TransformerFactory.newInstance();
//
//		StreamSource streamSource = new StreamSource(xslReader);
//
//		Transformer transformer = null;
//		try {
//			transformer = tFactory.newTransformer(streamSource);
//		} catch (TransformerConfigurationException e) {
//			e.printStackTrace();
//		}
//
//		StreamSource xmlDoc = new StreamSource(tDataReader);
//
//		// FileOutputStream fileOutputStream = null;
//		// try {
//		// fileOutputStream = new FileOutputStream("tlosData.html");
//		// } catch (FileNotFoundException e) {
//		// e.printStackTrace();
//		// }
//		// StreamResult streamResult = new StreamResult(fileOutputStream);
//
//		StringWriter stringWriter = new StringWriter();
//		StreamResult streamResult = new StreamResult(stringWriter);
//
//		try {
//			transformer.transform(xmlDoc, streamResult);
//		} catch (TransformerException e) {
//			e.printStackTrace();
//		}
//
//		return stringWriter.toString().getBytes("utf-8");
//	}

	
//	public static byte[] getPdfContent() throws TransformerException, FOPException, IOException {
//
//		String tlosDataXslDoc = null;
//		String tlosDataTemp = null;
//		String tlosCalendar = null;
//		String tlosUser = null;
//
//		try {
//			tlosDataXslDoc = getDbDoc("hs:tlosDataXslFo()");
//			tlosDataTemp = getDbDoc("hs:tlosData()");
//			tlosCalendar = getDbDoc("hs:tlosCalendar()");
//			tlosUser = getDbDoc("hs:tlosUser()");
//		} catch (XMLDBException e) {
//			e.printStackTrace();
//		} catch (XSLLoadException e) {
//			e.printStackTrace();
//		}
//
//		String tlosData = "<root>" + tlosDataTemp + "</root>";
//		StringReader xslReader = new StringReader(tlosDataXslDoc);
//		StringReader tDataReader = new StringReader(tlosData);
//
//		File tlosCalendarFile = new File("tlosSWCalendar10.xml");
//		FileOutputStream calendarFos = new FileOutputStream(tlosCalendarFile);
//
//		OutputStreamWriter calendarOsw = new OutputStreamWriter(calendarFos, "UTF8");
//		BufferedWriter calendarBufferedWriter = new BufferedWriter(calendarOsw);
//
//		calendarBufferedWriter.write(tlosCalendar);
//		calendarBufferedWriter.close();
//
//		File tlosUserFile = new File("tlosSWUser10.xml");
//		FileOutputStream userFos = new FileOutputStream(tlosUserFile);
//
//		OutputStreamWriter userOsw = new OutputStreamWriter(userFos, "UTF8");
//		BufferedWriter userBufferedWriter = new BufferedWriter(userOsw);
//
//		userBufferedWriter.write(tlosUser);
//		userBufferedWriter.close();
//
//		// the XML file from which we take the name
//		StreamSource source = new StreamSource(tDataReader);
//		// creation of transform source
//		StreamSource transformSource = new StreamSource(xslReader);
//		// create an instance of fop factory
//		FopFactory fopFactory = FopFactory.newInstance();
//		// a user agent is needed for transformation
//		FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
//		// to store output
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//
//		Transformer xslfoTransformer;
//		try {
//			xslfoTransformer = TransformUtils.getTransformer(transformSource);
//			// Construct fop with desired output format
//			Fop fop;
//			try {
//				fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outStream);
//				// Resulting SAX events (the generated FO)
//				// must be piped through to FOP
//				Result res = new SAXResult(fop.getDefaultHandler());
//
//				// Start XSLT transformation and FOP processing
//				try {
//					// everything will happen here..
//					xslfoTransformer.transform(source, res);
//					// if you want to get the PDF bytes, use the following code
//					// return outStream.toByteArray();
//
//					// if you want to save PDF file use the following code
//					// File pdffile = new File("TlosData.pdf");
//					// OutputStream out = new java.io.FileOutputStream(pdffile);
//					// out = new java.io.BufferedOutputStream(out);
//					// FileOutputStream str = new FileOutputStream(pdffile);
//					// str.write(outStream.toByteArray());
//					// str.close();
//					// out.close();
//
//					return outStream.toByteArray();
//
//					// to write the content to output stream
//					/*
//					 * byte[] pdfBytes = outStream.toByteArray();
//					 * response.setContentLength(pdfBytes.length);
//					 * response.setContentType("application/pdf");
//					 * response.addHeader("Content-Disposition", "attachment;filename=pdffile.pdf");
//					 * response.getOutputStream().write(pdfBytes);
//					 * response.getOutputStream().flush();
//					 */
//				} catch (TransformerException e) {
//					throw e;
//				}
//			} catch (FOPException e) {
//				throw e;
//			}
//		} catch (TransformerConfigurationException e) {
//			throw e;
//		} catch (TransformerFactoryConfigurationError e) {
//			throw e;
//		}
//	}

	// Web ekranindaki kaynak listesi agacinda herhangi bir Nagios Agent secildiginde buraya geliyor, sunucu da o agentin calistigi makinenin kullanim bilgisini donuyor
	public NrpeDataInfoTypeClient retrieveNagiosAgentInfo(JmxUser jmxUser, MonitorAgentInfoTypeClient nagiosAgentInfoTypeClient) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		NrpeData nrpeData = NrpeData.Factory.newInstance();

		NrpeDataInfoTypeClient nrpeDataInfoTypeClient = new NrpeDataInfoTypeClient();

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service;

		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return null;
		}

		// 1.ve 2. parametreler hangi araliktaki datanin gelecegi, 3. parametre makine adi
		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleNrpeOperations.xquery\";" + "lk:nrpeOutput(0, 5, \"" + nagiosAgentInfoTypeClient.getResourceName() + "\")";

		try {
			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					nrpeData = NrpeDataDocument.Factory.parse(xmlContent).getNrpeData();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		if (nrpeData != null) {
			nrpeDataInfoTypeClient = convertNrpeData(nrpeData);
		}

		return nrpeDataInfoTypeClient;
	}

	private NrpeDataInfoTypeClient convertNrpeData(NrpeData nrpeData) {

		NrpeDataInfoTypeClient nrpeDataInfoTypeClient = new NrpeDataInfoTypeClient();

		ArrayList<CpuInfoTypeClient> cpuInfoTypeClientList = new ArrayList<CpuInfoTypeClient>();
		ArrayList<DiskInfoTypeClient> diskInfoTypeClientList = new ArrayList<DiskInfoTypeClient>();
		ArrayList<MemoryInfoTypeClient> memoryInfoTypeClientList = new ArrayList<MemoryInfoTypeClient>();

		// kac ayri mesaj geldiyse o kadar tariyor, iceriklerini gerekli yerlere set ediyor
		for (int i = 0; i < nrpeData.getNrpeCallArray(0).getMessageArray().length; i++) {

			Message message = nrpeData.getNrpeCallArray(0).getMessageArray(i);

			// her komut icin ayri response degeri geldigi icin tum response degerleri taraniyor
			for (int j = 0; j < message.getResponseArray().length; j++) {

				Response response = message.getResponseArray(j);

				// cpu response kismi icin buraya geliyor
				if (response.getCommand().equals(Command.ALIAS_CPU)) {

					CpuInfoTypeClient cpuInfoTypeClient = new CpuInfoTypeClient();
					cpuInfoTypeClient.setCpuUnit(response.getCpuArray(0).getBirim().toString());

					for (int cpuCnt = 0; cpuCnt < response.getCpuArray().length; cpuCnt++) {
						if (response.getCpuArray(cpuCnt).getTimein().equals(Timein.X_1)) {
							cpuInfoTypeClient.setUsedCpuOneMin(response.getCpuArray(cpuCnt).getStringValue());

						} else if (response.getCpuArray(cpuCnt).getTimein().equals(Timein.X_5)) {
							cpuInfoTypeClient.setUsedCpuFiveMin(response.getCpuArray(cpuCnt).getStringValue());

						} else if (response.getCpuArray(cpuCnt).getTimein().equals(Timein.X_15)) {
							cpuInfoTypeClient.setUsedCpuFifteenMin(response.getCpuArray(cpuCnt).getStringValue());
						}
					}

					cpuInfoTypeClientList.add(cpuInfoTypeClient);

					// disk response kismi icin buraya geliyor
				} else if (response.getCommand().equals(Command.ALIAS_DISK)) {

					DiskInfoTypeClient diskInfoTypeClient = new DiskInfoTypeClient();
					diskInfoTypeClient.setDiskUnit(response.getDiskArray(0).getBirim().toString());

					for (int diskCnt = 0; diskCnt < response.getDiskArray().length; diskCnt++) {
						if (response.getDiskArray(diskCnt).getForWhat().equals(ForWhat.USED)) {
							diskInfoTypeClient.setUsedDisk(response.getDiskArray(diskCnt).getStringValue());

						} else if (response.getDiskArray(diskCnt).getForWhat().equals(ForWhat.FREE)) {
							diskInfoTypeClient.setFreeDisk(response.getDiskArray(diskCnt).getStringValue());
						}
					}

					diskInfoTypeClientList.add(diskInfoTypeClient);

					// memory response kismi icin buraya geliyor
				} else if (response.getCommand().equals(Command.ALIAS_MEM)) {

					MemoryInfoTypeClient memoryInfoTypeClient = new MemoryInfoTypeClient();
					memoryInfoTypeClient.setMemoryUnit(response.getMemArray(0).getBirim().toString());

					for (int memCnt = 0; memCnt < response.getMemArray().length; memCnt++) {
						if (response.getMemArray(memCnt).getForWhat().equals(ForWhat.USED)) {
							memoryInfoTypeClient.setUsedMemory(response.getMemArray(memCnt).getStringValue());

						} else if (response.getMemArray(memCnt).getForWhat().equals(ForWhat.FREE)) {
							memoryInfoTypeClient.setFreeMemory(response.getMemArray(memCnt).getStringValue());
						}
					}

					memoryInfoTypeClientList.add(memoryInfoTypeClient);
				}
			}
		}

		nrpeDataInfoTypeClient.setCpuInfoTypeClientList(cpuInfoTypeClientList);
		nrpeDataInfoTypeClient.setDiskInfoTypeClientList(diskInfoTypeClientList);
		nrpeDataInfoTypeClient.setMemoryInfoTypeClientList(memoryInfoTypeClientList);

		return nrpeDataInfoTypeClient;
	}

	@Override
	public ArrayList<SWAgent> searchAgent(JmxUser jmxUser, String agentXML) throws XMLDBException {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAgentOperations.xquery\";" + 
				CommonConstantDefinitions.decNsRes + "lk:searchAgent(" + agentXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<SWAgent> agentList = new ArrayList<SWAgent>();

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
		return agentList;
	}

	@Override
	public ArrayList<Alarm> searchAlarm(JmxUser jmxUser, String alarmXML) throws XMLDBException {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:searchAlarm(" + alarmXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public ArrayList<SLA> searchSla(JmxUser jmxUser, String slaXML) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleSLAOperations.xquery\";" + "hs:searchSLA(" + slaXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<SLA> slaList = new ArrayList<SLA>();

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

		return slaList;
	}

	@Override
	public Object deleteSla(JmxUser jmxUser, String slaXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleSLAOperations.xquery\";" + "hs:deleteSLALock(" + slaXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object insertSla(JmxUser jmxUser, String slaXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleSLAOperations.xquery\";" + "hs:insertSlaLock(" + slaXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object updateSla(JmxUser jmxUser, String slaXML) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleSLAOperations.xquery\";" + "hs:updateSLALock(" + slaXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public ArrayList<License> searchProvision(JmxUser jmxUser, String provisionXML) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.ksNsUrl + xQueryModuleUrl + "/moduleProgramProvisioningOperations.xquery\";" + "ks:searchPP(" + provisionXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<License> provisionList = new ArrayList<License>();

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

		return provisionList;
	}

	@Override
	public Object deleteProvision(JmxUser jmxUser, String provisionXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.ksNsUrl + xQueryModuleUrl + "/moduleProgramProvisioningOperations.xquery\";" + "ks:deletePpLock(" + provisionXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object insertProvision(JmxUser jmxUser, String provisionXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.ksNsUrl + xQueryModuleUrl + "/moduleProgramProvisioningOperations.xquery\";" + "ks:insertPpLock(" + provisionXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object updateProvision(JmxUser jmxUser, String provisionXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.ksNsUrl + xQueryModuleUrl + "/moduleProgramProvisioningOperations.xquery\";" + "ks:updatePpLock(" + provisionXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object deleteAgent(JmxUser jmxUser, String agentXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAgentOperations.xquery\";" + 
				CommonConstantDefinitions.decNsRes + "lk:deleteAgentLock(" + agentXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object insertAgent(JmxUser jmxUser, String agentXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAgentOperations.xquery\";" + 
				CommonConstantDefinitions.decNsRes + "lk:insertAgentLock(" + agentXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object deleteAlarm(JmxUser jmxUser, String alarmXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:deleteAlarmLock(" + alarmXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object insertAlarm(JmxUser jmxUser, String alarmXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:insertAlarmLock(" + alarmXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object updateAgent(JmxUser jmxUser, String agentXML) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAgentOperations.xquery\";" + 
				CommonConstantDefinitions.decNsRes + "lk:updateAgentLock(" + agentXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object updateAlarm(JmxUser jmxUser, String alarmXML) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:updateAlarmLock(" + alarmXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public ArrayList<RNSEntryType> resources(JmxUser jmxUser) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.rscNsUrl + xQueryModuleUrl + "/moduleResourcesOperations.xquery\";" + "rsc:resourcesList(1,10)";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<RNSEntryType> resourceList = new ArrayList<RNSEntryType>();

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			RNSEntryType resource;
			try {
				resource = RNSEntryType.Factory.parse(xmlContent);
				resourceList.add(resource);

				// resource.getEntryName();
				//
				// resource.setEntryName("test");
				// resource.setNilEndpoint();
				// resource.setNilMetadata();

			} catch (XmlException e) {
				e.printStackTrace();
				return null;
			}
		}

		return resourceList;

		// RNSEntryType resource1 = RNSEntryType.Factory.newInstance();
		// resource1.setEntryName("merve-laptop");
		//
		// RNSEntryType resource2 = RNSEntryType.Factory.newInstance();
		// resource2.setEntryName("laptop-hakan");
		//
		// RNSEntryType resource3 = RNSEntryType.Factory.newInstance();
		// resource3.setEntryName("nurkan-laptop");
		//
		// resourceList.add(resource1);
		// resourceList.add(resource2);
		// resourceList.add(resource3);
		//
		// return resourceList;

	}

	@Override
	public ArrayList<String> softwares(JmxUser jmxUser) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.ksNsUrl + xQueryModuleUrl + "/moduleProgramProvisioningOperations.xquery\";" + "ks:ppList(1,10)";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<String> softwareList = new ArrayList<String>();

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

		return softwareList;
	}

	@Override
	public ArrayList<JobProperties> jobList(JmxUser jmxUser) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:jobList(1,5)";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public ArrayList<SLA> slaList(JmxUser jmxUser) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleSLAOperations.xquery\";" + "hs:slaList(1,2)";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<SLA> slaList = new ArrayList<SLA>();

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

		return slaList;
	}

	@Override
	public ArrayList<Alarm> alarmList(JmxUser jmxUser) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:alarmList(1,50)";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public JobProperties getJobFromId(JmxUser jmxUser, String documentName, int jobId) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		JobProperties jobProperties = JobProperties.Factory.newInstance();

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		
		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + 
				CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + CommonConstantDefinitions.decNsSt + "hs:getJobFromId(" + "xs:string(\"" + documentName + "\")" + "," + jobId + ")";

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

	@Override
	public com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm getAlarmHistoryById(JmxUser jmxUser, int alarmHistoryId) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:searchAlarmHistoryById(" + alarmHistoryId + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
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

		return null;
	}

	@Override
	public ArrayList<AlarmInfoTypeClient> jobAlarmHistory(JmxUser jmxUser, String jobId, Boolean transformToLocalTime) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		// verilen isin son 5 rundaki alarmini runid'den bagimsiz olarak getiriyor
		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:jobAlarmListbyRunId(5, 0, " + jobId + ", false())";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<AlarmInfoTypeClient> alarmList = new ArrayList<AlarmInfoTypeClient>();

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm alarm;
			try {
				alarm = com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Factory.parse(xmlContent).getAlarm();

				alarmList.add(fillAlarmInfoTypeClient(service, alarm, jobId, transformToLocalTime));
			} catch (XmlException e) {
				e.printStackTrace();
				return null;
			}
		}

		return alarmList;
	}

	private AlarmInfoTypeClient fillAlarmInfoTypeClient(XPathQueryService service, com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm alarm, String jobId, Boolean transformToLocalTime) throws XMLDBException {

		AlarmInfoTypeClient alarmInfoTypeClient = new AlarmInfoTypeClient();
		alarmInfoTypeClient.setAlarmId(alarm.getAlarmId() + "");
		alarmInfoTypeClient.setAlarmHistoryId(alarm.getAHistoryId() + "");
		alarmInfoTypeClient.setCreationDate(DateUtils.calendarToString(alarm.getCreationDate(), transformToLocalTime));
		alarmInfoTypeClient.setLevel(alarm.getLevel() + "");

		// alarmin gerceklestigi kaynak ve agant id'sini set ediyor
		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAgentOperations.xquery\";" + 
				CommonConstantDefinitions.decNsRes + "lk:searchAgentByAgentId(" + alarm.getAgentId() + ")";

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		SWAgent agent = null;

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			try {
				agent = SWAgentDocument.Factory.parse(xmlContent).getSWAgent();
				break;
			} catch (XmlException e) {
				e.printStackTrace();
			}
		}
		alarmInfoTypeClient.setResourceName(agent.getResource().getStringValue() + "." + alarm.getAgentId());

		// bu is icin kullanilan warnBy degerini set ediyor

		String warnByStr = "";
		for (int j = 0; j < alarm.getSubscriber().getAlarmChannelTypes().getWarnByArray().length; j++) {

			if (alarm.getSubscriber().getAlarmChannelTypes().getWarnByArray(j).compareTo(BigInteger.valueOf(1)) == 0)
				warnByStr = warnByStr + "e-mail;";
			if (alarm.getSubscriber().getAlarmChannelTypes().getWarnByArray(j).compareTo(BigInteger.valueOf(2)) == 0)
				warnByStr = warnByStr + "SMS;";
			if (alarm.getSubscriber().getAlarmChannelTypes().getWarnByArray(j).compareTo(BigInteger.valueOf(3)) == 0)
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
			xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleUserOperations.xquery\";" + "hs:searchUserByUserId(" + alarm.getSubscriber().getPerson().getId() + ")";

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
		xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:searchAlarmByAlarmId(" + alarm.getAlarmId() + ")";

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

	public ArrayList<JobInfoTypeClient> getJobResultList(JmxUser jmxUser, String documentName, String jobId, int runNumber, Boolean transformToLocalTime) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		// verilen isin son runNumber sayisi kadar ki calisma listesini runid'den bagimsiz olarak getiriyor
		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + "hs:jobResultListbyRunId(" + "xs:string(\"" + documentName + "\")" + "," + runNumber + ", 0, " + jobId + ", false())";

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<JobInfoTypeClient> jobs = new ArrayList<JobInfoTypeClient>();
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
		return jobs;
	}

	public ArrayList<JobInfoTypeClient> getJobResultListByDates(JmxUser jmxUser, String documentName, String jobId, String date1, String date2, Boolean transformToLocalTime) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		// verilen isin son runNumber sayisi kadar ki calisma listesini runid'den bagimsiz olarak getiriyor
		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + "hs:jobResultListByDates(" + "xs:string(\"" + documentName + "\")" + "," + jobId + ", \"" + date1 + "\", \"" + date2 + "\", false())";

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<JobInfoTypeClient> jobs = new ArrayList<JobInfoTypeClient>();
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
		return jobs;
	}

	public ArrayList<JobProperties> getJobResultListByDates2(JmxUser jmxUser, String documentName, String jobId, String date1, String date2) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" + "hs:jobResultListByDates(" + "xs:string(\"" + documentName + "\")" + "," + jobId + ", \"" + date1 + "\", \"" + date2 + "\", false())";

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<JobProperties> jobs = new ArrayList<JobProperties>();
		JobProperties jobProperties = null;

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			try {
				jobProperties = JobPropertiesDocument.Factory.parse(xmlContent).getJobProperties();
				jobs.add(jobProperties);
			} catch (XmlException e) {
				e.printStackTrace();
				return null;
			}

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
		// jobInfoTypeClient.setTreePath(jobRuntimeProperties.getTreePath());
		jobInfoTypeClient.setJobPath(jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobPath());
		jobInfoTypeClient.setJobLogPath(jobProperties.getBaseJobInfos().getJobLogPath());
		jobInfoTypeClient.setJobLogName(jobProperties.getBaseJobInfos().getJobLogFile());
		jobInfoTypeClient.setoSystem(jobProperties.getBaseJobInfos().getOSystem().toString());

		// TODO Ge�ici olarak tip d�n���m� yapt�m.
		jobInfoTypeClient.setJobPriority(jobProperties.getBaseJobInfos().getJobPriority().intValue());

		jobInfoTypeClient.setJobPlanTime(DateUtils.jobTimeToString(jobProperties.getTimeManagement().getJsPlannedTime(), true, transformToLocalTime));
		jobInfoTypeClient.setJobPlanEndTime(DateUtils.jobTimeToString(jobProperties.getTimeManagement().getJsPlannedTime(), false, transformToLocalTime));
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
		// TODO ge�ici olarak d�n���m yapt�m ama xsd de problem var ????
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
			// jobInfoTypeClient.setDependJobNumber(depenArrayList.size());
		}

		jobInfoTypeClient.setAgentId(jobProperties.getAgentId());

		if (jobInfoTypeClient.getAgentId() > 0) {
			SWAgent agent = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference().getSwAgentsCache().get(jobInfoTypeClient.getAgentId() + "");

			jobInfoTypeClient.setResourceName(agent.getResource().getStringValue());
		}

		return jobInfoTypeClient;
	}

	@Override
	public Scenario getScenarioFromId(JmxUser jmxUser, String documentName, int scenarioId) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleScenarioOperations.xquery\";" +
				CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "hs:getScenarioFromId(" + "xs:string(\"" + xmlsUrl + documentName + "\")" + "," + scenarioId + ")";

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		Scenario scenario = null;

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
		return scenario;
	}

	@Override
	public Report dashboardReport(JmxUser jmxUser, int derinlik) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}
		long startTime = System.currentTimeMillis();

		String xQueryStr1 = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleReportOperations.xquery\";" + "hs:jobStateListbyRunId(" + derinlik + ",0,0,fn:boolean(0))";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		service.query(xQueryStr1);
		// System.err.println(" dashboardReport1 : " + DateUtils.dateDiffWithNow(startTime) + "ms");
		// Latest Report Id
		int reportId = -1;
		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.sqNsUrl + xQueryModuleUrl + "/moduleSequenceOperations.xquery\";" + "sq:getReportId()";

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i1 = result.getIterator();

		while (i1.hasMoreResources()) {
			Resource r = i1.nextResource();
			reportId = Integer.parseInt(r.getContent().toString());
		}
		System.err.println(" dashboardReport2 : " + DateUtils.dateDiffWithNow(startTime) + "ms");
		// get Report
		String xQueryStr2 = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleReportOperations.xquery\";" + "hs:searchStateReportById(" + reportId + ")";

		ResourceSet result2 = service.query(xQueryStr2);

		ResourceIterator i = result2.getIterator();
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
		System.err.println(" dashboardReport3 : " + DateUtils.dateDiffWithNow(startTime) + "ms");
		return report;
	}

	@Override
	public Calendar getGundonumu(JmxUser jmxUser) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		Calendar solsticeCalendar = DateUtils.normalizeDate(TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo().getSettings().getSolstice().getTime());

		return solsticeCalendar;
	}

	@Override
	public SLA getSlaBySlaId(JmxUser jmxUser, int slaId) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + xQueryModuleUrl + "/moduleSLAOperations.xquery\";" + "hs:searchSlaBySlaId(" + slaId + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
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

		return null;
	}

	@Override
	public ArrayList<DbProperties> searchDBAccessConnection(JmxUser jmxUser, String dbAccessPropertiesXML) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.dbNsUrl + xQueryModuleUrl + "/moduleDBConnectionsOperations.xquery\";" + 
				CommonConstantDefinitions.decNsDbc + CommonConstantDefinitions.decNsCom + "db:searchDbConnection(" + dbAccessPropertiesXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<DbProperties> dbConnectionList = new ArrayList<DbProperties>();

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

		return dbConnectionList;
	}

	@Override
	public ArrayList<DBAccessInfoTypeClient> searchDBAccessProfile(JmxUser jmxUser, String dbAccessProfileXML) throws XMLDBException {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.dbNsUrl + xQueryModuleUrl + "/moduleDBConnectionsOperations.xquery\";" + 
				CommonConstantDefinitions.decNsDbc + CommonConstantDefinitions.decNsCom + "db:getDbConnectionAll()";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator iterator = result.getIterator();
		HashMap<BigInteger, DbProperties> dbDefinitionList = new HashMap<BigInteger, DbProperties>();

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

		xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.dbNsUrl + xQueryModuleUrl + "/moduleDBConnectionsOperations.xquery\";" + 
				CommonConstantDefinitions.decNsDbc + CommonConstantDefinitions.decNsCom + "db:searchDbAccessProfile(" + dbAccessProfileXML + ")";

		result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<DBAccessInfoTypeClient> dbAccessInfoTypeClients = new ArrayList<DBAccessInfoTypeClient>();

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

		return dbAccessInfoTypeClients;
	}

	@Override
	public Object deleteDBAccessConnection(JmxUser jmxUser, String dbAccessPropertiesXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.dbNsUrl + xQueryModuleUrl + "/moduleDBConnectionsOperations.xquery\";" + 
				CommonConstantDefinitions.decNsDbc + CommonConstantDefinitions.decNsCom + "db:deleteDbConnection(" + dbAccessPropertiesXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object deleteDBAccessProfile(JmxUser jmxUser, String dbAccessProfileXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.dbNsUrl + xQueryModuleUrl + "/moduleDBConnectionsOperations.xquery\";" + 
				CommonConstantDefinitions.decNsDbc + CommonConstantDefinitions.decNsCom + "db:deleteDbAccessProfile(" + dbAccessProfileXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object insertDBAccessProfile(JmxUser jmxUser, String dbAccessProfileXML) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.dbNsUrl + xQueryModuleUrl + "/moduleDBConnectionsOperations.xquery\";" + 
				CommonConstantDefinitions.decNsDbc + CommonConstantDefinitions.decNsCom + "db:insertDbAccessProfile(" + dbAccessProfileXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object insertDBAccessConnection(JmxUser jmxUser, String dbAccessPropertiesXML) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.dbNsUrl + xQueryModuleUrl + "/moduleDBConnectionsOperations.xquery\";" + 
				CommonConstantDefinitions.decNsDbc + CommonConstantDefinitions.decNsCom + "db:insertDbConnection(" + dbAccessPropertiesXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object updateDBAccessProfile(JmxUser jmxUser, String dbAccessProfileXML) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.dbNsUrl + xQueryModuleUrl + "/moduleDBConnectionsOperations.xquery\";" + 
				CommonConstantDefinitions.decNsDbc + CommonConstantDefinitions.decNsCom + "db:updateDbAccessProfileLock(" + dbAccessProfileXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object updateDBAccessConnection(JmxUser jmxUser, String dbAccessPropertiesXML) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.dbNsUrl + xQueryModuleUrl + "/moduleDBConnectionsOperations.xquery\";" + 
				CommonConstantDefinitions.decNsDbc + CommonConstantDefinitions.decNsCom + "db:updateDbConnectionLock(" + dbAccessPropertiesXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public boolean checkDBConnectionName(JmxUser jmxUser, String dbAccessPropertiesXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.dbNsUrl + xQueryModuleUrl + "/moduleDBConnectionsOperations.xquery\";" + 
				CommonConstantDefinitions.decNsDbc + CommonConstantDefinitions.decNsCom + "db:checkDbConnectionName(" + dbAccessPropertiesXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public ArrayList<FtpProperties> ftpConnectionList(JmxUser jmxUser) throws XMLDBException {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.fcNsUrl + xQueryModuleUrl + "/moduleFTPConnectionsOperations.xquery\";" + "fc:getFTPConnectionList()";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<FtpProperties> ftpConnectionList = new ArrayList<FtpProperties>();

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

		return ftpConnectionList;
	}

	@Override
	public ArrayList<FtpProperties> searchFTPAccessConnection(JmxUser jmxUser, String ftpAccessPropertiesXML) throws XMLDBException {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.fcNsUrl + xQueryModuleUrl + "/moduleFTPConnectionsOperations.xquery\";" + 
				CommonConstantDefinitions.decNsFtp + CommonConstantDefinitions.decNsCom + "fc:searchFTPConnection(" + ftpAccessPropertiesXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<FtpProperties> ftpConnectionList = new ArrayList<FtpProperties>();

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

		return ftpConnectionList;
	}

	@Override
	public boolean checkFTPConnectionName(JmxUser jmxUser, String ftpAccessPropertiesXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.fcNsUrl + xQueryModuleUrl + "/moduleFTPConnectionsOperations.xquery\";" + 
				CommonConstantDefinitions.decNsFtp + CommonConstantDefinitions.decNsCom + "fc:checkFTPConnectionName(" + ftpAccessPropertiesXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object deleteFTPAccessConnection(JmxUser jmxUser, String ftpAccessPropertiesXML) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.fcNsUrl + xQueryModuleUrl + "/moduleFTPConnectionsOperations.xquery\";" + 
				CommonConstantDefinitions.decNsFtp + CommonConstantDefinitions.decNsCom + "fc:deleteFTPConnection(" + ftpAccessPropertiesXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object insertFTPAccessConnection(JmxUser jmxUser, String ftpAccessPropertiesXML) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}
		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.fcNsUrl + xQueryModuleUrl + "/moduleFTPConnectionsOperations.xquery\";" + 
				CommonConstantDefinitions.decNsFtp + CommonConstantDefinitions.decNsCom + "fc:insertFTPConnection(" + ftpAccessPropertiesXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object updateFTPAccessConnection(JmxUser jmxUser, String ftpAccessPropertiesXML) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}
		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.fcNsUrl + xQueryModuleUrl + "/moduleFTPConnectionsOperations.xquery\";" + 
				CommonConstantDefinitions.decNsFtp + CommonConstantDefinitions.decNsCom + "fc:updateFTPConnectionLock(" + ftpAccessPropertiesXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object insertWSDefinition(JmxUser jmxUser, String wsPropertiesXML) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.wsoNsUrl + xQueryModuleUrl + "/moduleWebServiceOperations.xquery\";" + 
				CommonConstantDefinitions.decNsWs + CommonConstantDefinitions.decNsCom + "wso:insertWSDefinition(" + wsPropertiesXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public ArrayList<WebServiceDefinition> webServiceList(JmxUser jmxUser) throws XMLDBException {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.wsoNsUrl + xQueryModuleUrl + "/moduleWebServiceOperations.xquery\";" + "wso:getWSDefinitionList()";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<WebServiceDefinition> webServiceList = new ArrayList<WebServiceDefinition>();

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

		return webServiceList;
	}

	@Override
	public ArrayList<UserAccessProfile> searchWSAccessProfiles(JmxUser jmxUser, String userAccessProfileXML) throws XMLDBException {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.wsoNsUrl + xQueryModuleUrl + "/moduleWebServiceOperations.xquery\";" + 
				CommonConstantDefinitions.decNsWs + CommonConstantDefinitions.decNsCom + "wso:searchWSAccessProfiles(" + userAccessProfileXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<UserAccessProfile> userAccessProfiles = new ArrayList<UserAccessProfile>();

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			UserAccessProfile userAccessProfile;
			try {
				userAccessProfile = UserAccessProfileDocument.Factory.parse(xmlContent).getUserAccessProfile();
				userAccessProfiles.add(userAccessProfile);
			} catch (XmlException e) {
				e.printStackTrace();
				return null;
			}
		}

		return userAccessProfiles;
	}

	public ArrayList<Alarm> alarms(JmxUser jmxUser) throws XMLDBException {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + xQueryModuleUrl + "/moduleAlarmOperations.xquery\";" + "lk:alarms()";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<Alarm> almList = new ArrayList<Alarm>();

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

		return almList;
	}

	@Override
	public Object insertWSAccessProfile(JmxUser jmxUser, String userAccessProfileXML) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.wsoNsUrl + xQueryModuleUrl + "/moduleWebServiceOperations.xquery\";" + 
				CommonConstantDefinitions.decNsWs + CommonConstantDefinitions.decNsCom + "wso:insertWSAccessProfile(" + userAccessProfileXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object deleteWSAccessProfile(JmxUser jmxUser, String userAccessProfileXML) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.wsoNsUrl + xQueryModuleUrl + "/moduleWebServiceOperations.xquery\";" + 
				CommonConstantDefinitions.decNsWs + CommonConstantDefinitions.decNsCom + "wso:deleteWSAccessProfile(" + userAccessProfileXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public Object updateWSAccessProfile(JmxUser jmxUser, String userAccessProfileXML) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.wsoNsUrl + xQueryModuleUrl + "/moduleWebServiceOperations.xquery\";" + 
				CommonConstantDefinitions.decNsWs + CommonConstantDefinitions.decNsCom + "wso:updateWSAccessProfileLock(" + userAccessProfileXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
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

	@Override
	public ArrayList<WebServiceDefinition> webServiceListForActiveUser(JmxUser jmxUser, int userId) throws XMLDBException {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.wsoNsUrl + xQueryModuleUrl + "/moduleWebServiceOperations.xquery\";" + 
				CommonConstantDefinitions.decNsWs + CommonConstantDefinitions.decNsCom + "wso:getWSDefinitionListForActiveUser(" + userId + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<WebServiceDefinition> webServiceList = new ArrayList<WebServiceDefinition>();

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

		return webServiceList;
	}

	@Override
	public ArrayList<DbProperties> dbList(JmxUser jmxUser) throws XMLDBException {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.dbNsUrl + xQueryModuleUrl + "/moduleDBConnectionsOperations.xquery\";" + 
				CommonConstantDefinitions.decNsDbc + CommonConstantDefinitions.decNsCom + "db:getDbConnectionAll()";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<DbProperties> dbList = new ArrayList<DbProperties>();

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

		return dbList;
	}

	@Override
	public ArrayList<DbConnectionProfile> dbProfileList(JmxUser jmxUser) throws XMLDBException {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.dbNsUrl + xQueryModuleUrl + "/moduleDBConnectionsOperations.xquery\";" + 
				CommonConstantDefinitions.decNsDbc + CommonConstantDefinitions.decNsCom + "db:getDbProfileAll()";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		ArrayList<DbConnectionProfile> dbProfileList = new ArrayList<DbConnectionProfile>();

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

		return dbProfileList;
	}

}

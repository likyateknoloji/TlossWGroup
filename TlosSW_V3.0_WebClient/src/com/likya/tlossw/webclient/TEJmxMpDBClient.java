package com.likya.tlossw.webclient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import org.ogf.schemas.rns.x2009.x12.rns.RNSEntryType;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.alarm.AlarmDocument.Alarm;
import com.likya.tlos.model.xmlbeans.alarmhistory.AlarmReportDocument.AlarmReport;
import com.likya.tlos.model.xmlbeans.calendar.CalendarPropertiesDocument.CalendarProperties;
import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument.TlosConfigInfo;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlos.model.xmlbeans.programprovision.LicenseDocument.License;
import com.likya.tlos.model.xmlbeans.report.ReportDocument.Report;
import com.likya.tlos.model.xmlbeans.sla.SLADocument.SLA;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlos.model.xmlbeans.webservice.UserAccessProfileDocument.UserAccessProfile;
import com.likya.tlos.model.xmlbeans.webservice.WebServiceDefinitionDocument.WebServiceDefinition;
import com.likya.tlossw.model.AlarmInfoTypeClient;
import com.likya.tlossw.model.auth.ResourcePermission;
import com.likya.tlossw.model.client.resource.MonitorAgentInfoTypeClient;
import com.likya.tlossw.model.client.resource.NrpeDataInfoTypeClient;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.model.jmx.JmxAppUser;
import com.likya.tlossw.model.jmx.JmxUser;

public class TEJmxMpDBClient extends TEJmxMpClientBase {

	private TEJmxMpDBClient() {
		// initCommanderInstance();
	}

	/*
	 * private static TEJmxMpDBClient initInstance() {
	 * //TODO bu instance olayi soru isareti irdelemek gerekli
	 * if (getSelfInstance() == null || !(getSelfInstance()instanceof TEJmxMpDBClient) ) {
	 * setSelfInstance(new TEJmxMpDBClient());
	 * }
	 * 
	 * return (TEJmxMpDBClient) getSelfInstance();
	 * }
	 */
	public static long dateDiffWithNow(long sDate) {

		Date now = Calendar.getInstance().getTime();
		long timeDiff = now.getTime() - sDate;

		return timeDiff;
	}

	public static Object checkUser(JMXConnector jmxConnector, JmxAppUser jmxUser) {

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxAppUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "checkUser", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static int getNextUserId(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getNextUserId", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Integer) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Person> searchUser(JmxUser jmxUser, String personXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, personXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "searchUser", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<Person>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<SWAgent> searchAgent(JmxUser jmxUser, String agentXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, agentXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "searchAgent", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<SWAgent>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Alarm> searchAlarm(JmxUser jmxUser, String alarmXML) {

		System.out.println("Search Alarm.................");
		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, alarmXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "searchAlarm", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<Alarm>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Person> users(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "users", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<Person>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Alarm> alarms(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "alarms", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<Alarm>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Person searchUserByUsername(JmxUser jmxUser, String username) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, username };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "searchUserByUsername", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Person) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static SWAgent searchAgentByAgentname(JmxUser jmxUser, String agentname) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, agentname };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "searchAgentByAgentname", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (SWAgent) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean insertUser(JmxUser jmxUser, String personXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, personXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "insertUser", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean insertAgent(JmxUser jmxUser, String agentXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, agentXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "insertAgent", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean insertAlarm(JmxUser jmxUser, String alarmXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, alarmXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "insertAlarm", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean updateUser(JmxUser jmxUser, String personXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, personXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "updateUser", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean updateAgent(JmxUser jmxUser, String agentXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, agentXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "updateAgent", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean updateAlarm(JmxUser jmxUser, String alarmXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, alarmXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "updateAlarm", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteUser(JmxUser jmxUser, String personXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, personXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "deleteUser", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteAgent(JmxUser jmxUser, String agentXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, agentXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "deleteAgent", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteAlarm(JmxUser jmxUser, String alarmXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, alarmXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "deleteAlarm", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static int getNextCalendarId(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getNextCalendarId", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Integer) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static boolean insertCalendar(JmxUser jmxUser, String calendarPropertiesXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, calendarPropertiesXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "insertCalendar", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<String> calendarNames(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "calendarNames", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<String>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getNextScenarioId(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getNextScenarioId", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Integer) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static boolean insertScenario(JmxUser jmxUser, String documentName, String scenarioXML, String scenarioPath) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, documentName, scenarioXML, scenarioPath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "insertScenario", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static int getNextJobId(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getNextJobId", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Integer) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static boolean insertJob(JmxUser jmxUser, String documentName, String jobPropertiesXML, String jobPath) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, documentName, jobPropertiesXML, jobPath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "insertJob", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static TlosProcessData getTlosDataXml(JmxUser jmxUser, String documentName) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, documentName };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getTlosDataXml", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (TlosProcessData) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<CalendarProperties> searchCalendar(JmxUser jmxUser, String calendarPropertiesXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, calendarPropertiesXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "searchCalendar", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<CalendarProperties>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<CalendarProperties> calendars(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "calendars", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<CalendarProperties>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean updateCalendar(JmxUser jmxUser, String calendarPropertiesXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, calendarPropertiesXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "updateCalendar", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteCalendar(JmxUser jmxUser, String calendarPropertiesXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, calendarPropertiesXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "deleteCalendar", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/* Kullanilmiyor ise kaldir. hs 13.12.2012 */
	public static int getMaxScenarioId(JmxUser jmxUser, String documentName) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, documentName };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getMaxScenarioId", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Integer) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static boolean deleteScenario(JmxUser jmxUser, String documentName, String scenarioXML, String scenarioPath) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, documentName, scenarioXML, scenarioPath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "deleteScenario", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteJob(JmxUser jmxUser, String documentName, String jobPropertiesXML, String jobPath) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, documentName, jobPropertiesXML, jobPath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "deleteJob", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean updateJob(JmxUser jmxUser, String documentName, String jobPropertiesXML, String jobPath) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, documentName, jobPropertiesXML, jobPath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "updateJob", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static JobProperties getJob(JmxUser jmxUser, String documentName, String jobPath, String jobName) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, documentName, jobPath, jobName };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getJob", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (JobProperties) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JobProperties getJobFromId(JmxUser jmxUser, String documentName, int jobId) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, documentName, jobId };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "int" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getJobFromId", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (JobProperties) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Scenario getScenario(JmxUser jmxUser, String documentName, String scenarioPath, String scenarioName) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, documentName, scenarioPath, scenarioName };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getScenario", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Scenario) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Scenario getScenarioFromId(JmxUser jmxUser, String documentName, int scenarioId) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, documentName, scenarioId };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "int" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getScenarioFromId", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Scenario) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static TlosConfigInfo getTlosConfig(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getTlosConfig", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (TlosConfigInfo) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean updateTlosConfig(JmxUser jmxUser, String tlosConfigInfoXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, tlosConfigInfoXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "updateTlosConfig", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<ResourcePermission> getPermissions(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getPermissions", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<ResourcePermission>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean updatePermissions(JmxUser jmxUser, String permissionsXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, permissionsXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "updatePermissions", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static boolean updateScenario(JmxUser jmxUser, String documentName, String scenarioPath, String scenarioXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, documentName, scenarioPath, scenarioXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "updateScenario", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<JobProperties> getReportJobs(JmxUser jmxUser, String jobPropertiesXML, String jobPath) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, jobPropertiesXML, jobPath };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getReportJobs", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<JobProperties>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] getPdfDoc(JmxUser jmxUser, String str) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, str };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getPdfDoc", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (byte[]) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] getHtmlDoc(JmxUser jmxUser, String str) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, str };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getHtmlDoc", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (byte[]) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getNextTraceId(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getNextTraceId", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Integer) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static boolean insertTrace(JmxUser jmxUser, String traceXML) {
		// long startTime = System.currentTimeMillis();
		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, traceXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			// System.err.println(" TEJmxMpDBClient.insertTrace :1 " + dateDiffWithNow(startTime) + "ms");
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			// System.err.println(" TEJmxMpDBClient.insertTrace :2 " + dateDiffWithNow(startTime) + "ms");
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "insertTrace", paramList, signature);
			// System.err.println(" TEJmxMpDBClient.insertTrace :3 " + dateDiffWithNow(startTime) + "ms");
			TEJmxMpClient.disconnect(jmxConnector);
			// System.err.println(" TEJmxMpDBClient.insertTrace :4 " + dateDiffWithNow(startTime) + "ms");
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.err.println(" TEJmxMpDBClient.insertTrace : FALSE " + dateDiffWithNow(startTime) + "ms");
		return false;
	}

	// Web ekranindaki kaynak listesi agacinda herhangi bir Monitor Agent secildiginde buraya gelip makine bilgilerini aliyor
	/**
	 * Sunucudan makinenin kullanim bilgilerini istiyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param tlosAgentId
	 *            Bilgileri istenen Tlos Agent'in id numarasi
	 * @return Sunucudan aldigi islemci, disk ve bellek bilgilerini donuyor
	 */
	public static NrpeDataInfoTypeClient retrieveMonitorAgentInfo(JmxUser jmxUser, MonitorAgentInfoTypeClient monitorAgentInfoTypeClient) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, monitorAgentInfoTypeClient };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "com.likya.tlossw.model.client.resource.MonitorAgentInfoTypeClient" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "retrieveMonitorAgentInfo", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (NrpeDataInfoTypeClient) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Ekrandan girilen arama kriterlerine gore sunucudan SSA listesini istiyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param slaXML
	 *            Icinde arama kriterlerinin bulundugu SSA xmli
	 * @return SSA listesi
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<SLA> searchSla(JmxUser jmxUser, String slaXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, slaXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "searchSla", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<SLA>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * SSA tanimini silmesi icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param slaXML
	 *            Icinde silinecek SSA'nin bilgilerinin bulundugu xml
	 * @return islem basariliysa true degilse false donuyor
	 */
	public static boolean deleteSla(JmxUser jmxUser, String slaXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, slaXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "deleteSla", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Yeni SSA tanimini kaydetmesi icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param slaXML
	 *            Icinde eklenecek SSA'nin bilgilerinin bulundugu xml
	 * @return islem basariliysa true degilse false donuyor
	 */
	public static boolean insertSla(JmxUser jmxUser, String slaXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, slaXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "insertSla", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Degistirilen SSA tanimini guncellemesi icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param slaXML
	 *            Icinde guncellenecek SSA'nin bilgilerinin bulundugu xml
	 * @return islem basariliysa true degilse false donuyor
	 */
	public static boolean updateSla(JmxUser jmxUser, String slaXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, slaXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "updateSla", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Ekrandan girilen arama kriterlerine gore sunucudan provizyon listesini istiyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param provisionXML
	 *            Icinde arama kriterlerinin bulundugu provizyon xmli
	 * @return provizyon listesi
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<License> searchProvision(JmxUser jmxUser, String provisionXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, provisionXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "searchProvision", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<License>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Provizyon tanimini silmesi icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param provisionXML
	 *            Icinde silinecek provizyon bilgilerinin bulundugu xml
	 * @return islem basariliysa true degilse false donuyor
	 */
	public static boolean deleteProvision(JmxUser jmxUser, String provisionXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, provisionXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "deleteProvision", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Yeni provizyon tanimini kaydetmesi icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param provisionXML
	 *            Icinde eklenecek provizyon bilgilerinin bulundugu xml
	 * @return islem basariliysa true degilse false donuyor
	 */
	public static boolean insertProvision(JmxUser jmxUser, String provisionXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, provisionXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "insertProvision", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Degistirilen provizyon tanimini guncellemesi icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param provisionXML
	 *            Icinde guncellenecek provizyon bilgilerinin bulundugu xml
	 * @return islem basariliysa true degilse false donuyor
	 */
	public static boolean updateProvision(JmxUser jmxUser, String provisionXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, provisionXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "updateProvision", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Veri tabaninda tanimli olan kaynaklarin listesini sunucudan istiyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @return sunucudan aldigi kaynak listesini donuyor
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<RNSEntryType> resources(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "resources", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<RNSEntryType>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Veri tabaninda tanimli olan yazilim listesini sunucudan istiyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @return sunucudan aldigi yazilim listesini donuyor
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<String> softwares(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "softwares", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);

			return (ArrayList<String>) o;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Veri tabaninda tanimli olan job listesini sunucudan istiyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @return sunucudan aldigi yazilim listesini donuyor
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<JobProperties> jobList(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "jobList", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);

			return (ArrayList<JobProperties>) o;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Veri tabaninda tanimli olan SLA listesini sunucudan istiyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @return sunucudan aldigi SLA listesini donuyor
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<SLA> slaList(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "slaList", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);

			return (ArrayList<SLA>) o;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Veri tabaninda tanimli olan alarm listesini sunucudan istiyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @return sunucudan aldigi alarm listesini donuyor
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Alarm> alarmList(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "alarmList", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);

			return (ArrayList<Alarm>) o;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Report getDashboardReport(JmxUser jmxUser, int derinlik) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, derinlik };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "int" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "dashboardReport", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Report) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static Calendar getGundonumu(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getGundonumu", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Calendar) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	public static ArrayList<AlarmInfoTypeClient> jobAlarmHistory(JmxUser jmxUser, String jobId, boolean transformToLocalTime) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, jobId, transformToLocalTime };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.Boolean" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "jobAlarmHistory", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);

			return (ArrayList<AlarmInfoTypeClient>) o;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<JobInfoTypeClient> getJobResultList(JmxUser jmxUser, String documentName, String jobId, int runNumber, boolean transformToLocalTime) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, documentName, jobId, runNumber, transformToLocalTime };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String", "int", "java.lang.Boolean" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getJobResultList", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<JobInfoTypeClient>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<JobInfoTypeClient> getJobResultListByDates(JmxUser jmxUser, String documentName, String jobId, String date1, String date2, boolean transformToLocalTime) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, documentName, jobId, date1, date2, transformToLocalTime };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String", "java.lang.Boolean" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getJobResultListByDates", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<JobInfoTypeClient>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<AlarmReport> getAlarmReportList(JmxUser jmxUser, String date1, String date2, String alarmLevel, String alarmName, String alarmUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, date1, date2, alarmLevel, alarmName, alarmUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getAlarmReportList", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<AlarmReport>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<JobProperties> getJobResultListByDates2(JmxUser jmxUser, String documentName, String jobId, String date1, String date2) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, documentName, jobId, date1, date2 };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "java.lang.String", "java.lang.String", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getJobResultListByDates2", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<JobProperties>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm getAlarmHistoryById(JmxUser jmxUser, int alarmHistoryId) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, alarmHistoryId };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "int" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getAlarmHistoryById", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static SLA getSlaBySlaId(JmxUser jmxUser, int slaId) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, slaId };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "int" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getSlaBySlaId", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (SLA) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Ekrandan girilen arama kriterlerine gore sunucudan veri tabani erisim tanimlari listesini istiyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param dbAccessPropertiesXML
	 *            Icinde arama kriterlerinin bulundugu veri tabani erisim tanimi xmli
	 * @return veri tabani erisim tanimlari listesi
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<DbProperties> searchDBAccessConnection(JmxUser jmxUser, String dbAccessPropertiesXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, dbAccessPropertiesXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "searchDBAccessConnection", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<DbProperties>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Secilen veri tabani erisim taniminin silinmesi icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param dbAccessPropertiesXML
	 *            Silinecek veri tabani erisim tanimi xmli
	 * @return islem basariliysa true degilse false donuyor
	 */
	public static boolean deleteDBAccessConnection(JmxUser jmxUser, String dbAccessPropertiesXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, dbAccessPropertiesXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "deleteDBAccessConnection", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Girisi yapilan veri tabani erisim taniminin kaydedilmesi icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param dbAccessPropertiesXML
	 *            Kaydedilecek veri tabani erisim tanimi xmli
	 * @return islem basariliysa true degilse false donuyor
	 */
	public static boolean insertDBAccessConnection(JmxUser jmxUser, String dbAccessPropertiesXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, dbAccessPropertiesXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "insertDBAccessConnection", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Veri tabani erisim taniminin guncellenmesi icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param dbAccessPropertiesXML
	 *            Guncellenecek veri tabani erisim tanimi xmli
	 * @return islem basariliysa true degilse false donuyor
	 */
	public static boolean updateDBAccessConnection(JmxUser jmxUser, String dbAccessPropertiesXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, dbAccessPropertiesXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "updateDBAccessConnection", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean checkDBConnectionName(JmxUser jmxUser, String dbAccessPropertiesXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, dbAccessPropertiesXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "checkDBConnectionName", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static int getNextDbConnectionId(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getNextDbConnectionId", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Integer) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Veri tabaninda tanimli olan ftp tanimlari listesini sunucudan istiyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @return sunucudan aldigi ftp tanimlari listesini donuyor
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<FtpProperties> ftpConnectionList(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "ftpConnectionList", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);

			return (ArrayList<FtpProperties>) o;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Ekrandan girilen arama kriterlerine gore sunucudan ftp erisim tanimlari listesini istiyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param ftpAccessPropertiesXML
	 *            Icinde arama kriterlerinin bulundugu ftp erisim tanimi xmli
	 * @return ftp erisim tanimlari listesi
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<FtpProperties> searchFTPAccessConnection(JmxUser jmxUser, String ftpAccessPropertiesXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, ftpAccessPropertiesXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "searchFTPAccessConnection", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<FtpProperties>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Secilen ftp erisim taniminin silinmesi icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param ftpAccessPropertiesXML
	 *            Silinecek ftp erisim tanimi xmli
	 * @return islem basariliysa true degilse false donuyor
	 */
	public static boolean deleteFTPAccessConnection(JmxUser jmxUser, String ftpPropertiesXML) {
		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, ftpPropertiesXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "deleteFTPAccessConnection", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Girilen ftp baglanti adinin daha once tanimlanip tanimlanmadigini ogrenmek icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param ftpAccessPropertiesXML
	 *            Silinecek ftp erisim tanimi xmli
	 * @return baglanti adi kullanilabilir durumdaysa true degilse false donuyor
	 */
	public static boolean checkFTPConnectionName(JmxUser jmxUser, String ftpAccessPropertiesXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, ftpAccessPropertiesXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "checkFTPConnectionName", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Girisi yapilan ftp erisim taniminin kaydedilmesi icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param ftpAccessPropertiesXML
	 *            Kaydedilecek ftp erisim tanimi xmli
	 * @return islem basariliysa true degilse false donuyor
	 */
	public static boolean insertFTPAccessConnection(JmxUser jmxUser, String ftpAccessPropertiesXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, ftpAccessPropertiesXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "insertFTPAccessConnection", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * FTP erisim taniminin guncellenmesi icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param ftpAccessPropertiesXML
	 *            Guncellenecek ftp erisim tanimi xmli
	 * @return islem basariliysa true degilse false donuyor
	 */
	public static boolean updateFTPAccessConnection(JmxUser jmxUser, String ftpAccessPropertiesXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, ftpAccessPropertiesXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "updateFTPAccessConnection", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static int getNextFTPConnectionId(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getNextFTPConnectionId", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Integer) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static int getNextWSDefinitionId(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getNextWSDefinitionId", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Integer) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Web servis taniminin kaydedilmesi icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param wsPropertiesXML
	 *            Kaydedilecek web servis tanimi xmli
	 * @return islem basariliysa true degilse false donuyor
	 */
	public static boolean insertWSDefinition(JmxUser jmxUser, String wsPropertiesXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, wsPropertiesXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "insertWSDefinition", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Veri tabaninda tanimli olan web servis tanimlari listesini sunucudan istiyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @return sunucudan aldigi web servis tanimlari listesini donuyor
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<WebServiceDefinition> webServiceList(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "webServiceList", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);

			return (ArrayList<WebServiceDefinition>) o;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Ekrandan girilen arama kriterlerine gore sunucudan web servisler icin tanimli kullanici erisim profillerini istiyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param userAccessProfileXML
	 *            Icinde arama kriterlerinin bulundugu kullanici erisim profili xmli
	 * @return kullanici erisim profilleri listesi
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<UserAccessProfile> searchWSAccessProfiles(JmxUser jmxUser, String userAccessProfileXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, userAccessProfileXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "searchWSAccessProfiles", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (ArrayList<UserAccessProfile>) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getNextWSUserProfileId(JmxUser jmxUser) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "getNextWSUserProfileId", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Integer) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Web kullanici erisim profilinin kaydedilmesi icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param userAccessProfileXML
	 *            Kaydedilecek web servis erisim profili xmli
	 * @return islem basariliysa true degilse false donuyor
	 */
	public static boolean insertWSAccessProfile(JmxUser jmxUser, String userAccessProfileXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, userAccessProfileXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "insertWSAccessProfile", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Secilen kullanici erisim profilinin silinmesi icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param userAccessProfileXML
	 *            Silinecek web servis erisim profili xmli
	 * @return islem basariliysa true degilse false donuyor
	 */
	public static boolean deleteWSAccessProfile(JmxUser jmxUser, String userAccessProfileXML) {
		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, userAccessProfileXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "deleteWSAccessProfile", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Kullanici erisim profilinin guncellenmesi icin sunucudan istekte bulunuyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param userAccessProfileXML
	 *            Guncellenecek web servis erisim profili xmli
	 * @return islem basariliysa true degilse false donuyor
	 */
	public static boolean updateWSAccessProfile(JmxUser jmxUser, String userAccessProfileXML) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, userAccessProfileXML };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String" };
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "updateWSAccessProfile", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (Boolean) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Veri tabaninda tanimli olan web servis tanimlarindan login olan kullanicinin yetkisinin olduklarini sunucudan istiyor
	 * 
	 * @param jmxUser
	 *            Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param userId
	 *            Login olan kullanicinin id degeri
	 * @return sunucudan aldigi web servis tanimlari listesini donuyor
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<WebServiceDefinition> webServiceListForActiveUser(JmxUser jmxUser, int userId) {
		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, userId };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "int" };
		Object o;

		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=4"), "webServiceListForActiveUser", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);

			return (ArrayList<WebServiceDefinition>) o;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

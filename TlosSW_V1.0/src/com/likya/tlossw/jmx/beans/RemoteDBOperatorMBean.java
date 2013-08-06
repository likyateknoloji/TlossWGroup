/*
 * @(#)file      SimpleStandardMBean.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.1
 * @(#)lastedit  03/04/22
 * @(#)build     jmxremote-1_0_1_04-b58 2005.11.23_16:04:12_MET
 *
 * Copyright 2005 Sun Microsystems, Inc.  All rights reserved. Use is subject to license terms.
 */

package com.likya.tlossw.jmx.beans;

import java.util.ArrayList;
import java.util.Calendar;

import org.ogf.schemas.rns.x2009.x12.rns.RNSEntryType;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.alarm.AlarmDocument.Alarm;
import com.likya.tlos.model.xmlbeans.alarmhistory.AlarmReportDocument.AlarmReport;
import com.likya.tlos.model.xmlbeans.calendar.CalendarPropertiesDocument.CalendarProperties;
import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument.TlosConfigInfo;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlos.model.xmlbeans.programprovision.LicenseDocument.License;
import com.likya.tlos.model.xmlbeans.report.ReportDocument.Report;
import com.likya.tlos.model.xmlbeans.sla.SLADocument.SLA;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlos.model.xmlbeans.webservice.UserAccessProfileDocument.UserAccessProfile;
import com.likya.tlos.model.xmlbeans.webservice.WebServiceDefinitionDocument.WebServiceDefinition;
import com.likya.tlossw.model.AlarmInfoTypeClient;
import com.likya.tlossw.model.DBAccessInfoTypeClient;
import com.likya.tlossw.model.auth.ResourcePermission;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.model.jmx.JmxUser;

/**
 * This is the management interface explicitly defined for the
 * "SimpleStandard" standard MBean.
 * 
 * The "SimpleStandard" standard MBean implements this interface
 * in order to be manageable through a JMX agent.
 * 
 * The "SimpleStandardMBean" interface shows how to expose for management:
 * - a read/write attribute (named "State") through its getter and setter
 * methods,
 * - a read-only attribute (named "NbChanges") through its getter method,
 * - an operation (named "reset").
 */
public interface RemoteDBOperatorMBean {

	/**
	 * Getter: set the "State" attribute of the "SimpleStandard" standard
	 * MBean.
	 * 
	 * @return the current value of the "State" attribute.
	 */
	public String getState();

	/**
	 * Setter: set the "State" attribute of the "SimpleStandard" standard
	 * MBean.
	 * 
	 * @param <VAR>s</VAR> the new value of the "State" attribute.
	 */
	public void setState(String s);

	/**
	 * Getter: get the "NbChanges" attribute of the "SimpleStandard" standard
	 * MBean.
	 * 
	 * @return the current value of the "NbChanges" attribute.
	 */
	public int getNbChanges();

	/**
	 * Operation: reset to their initial values the "State" and "NbChanges"
	 * attributes of the "SimpleStandard" standard MBean.
	 */
	public void reset();

	//public Object checkUser(JmxUser jmxUser) throws XMLDBException;

	public ArrayList<Person> searchUser(JmxUser jmxUser, String personXML) throws XMLDBException;

	public ArrayList<Person> users(JmxUser jmxUser) throws XMLDBException;

	public ArrayList<Alarm> alarms(JmxUser jmxUser) throws XMLDBException;

	public Object insertUser(JmxUser jmxUser, String personXML);

	public Object updateUser(JmxUser jmxUser, String personXML);

	public Object deleteUser(JmxUser jmxUser, String personXML);

	public Person searchUserByUsername(JmxUser jmxUser, String username) throws XMLDBException;

	public Object insertCalendar(JmxUser jmxUser, String calendarPropertiesXML);

	public ArrayList<String> calendarNames(JmxUser jmxUser) throws XMLDBException;

	public Object insertScenario(JmxUser jmxUser, String documentName, String scenarioXML, String scenarioPath);

	public Object insertJob(JmxUser jmxUser, String documentName, String jobPropertiesXML, String jobPath);

	public int getMaxScenarioId(JmxUser jmxUser, String documentName) throws XMLDBException;

	public ArrayList<CalendarProperties> searchCalendar(JmxUser jmxUser, String calendarPropertiesXML) throws XMLDBException;

	public ArrayList<CalendarProperties> calendars(JmxUser jmxUser) throws XMLDBException;

	public Object updateCalendar(JmxUser jmxUser, String calendarPropertiesXML);

	public Object deleteCalendar(JmxUser jmxUser, String calendarPropertiesXML);

	public Object deleteScenario(JmxUser jmxUser, String documentName, String scenarioXML, String scenarioPath);

	public Object deleteJob(JmxUser jmxUser, String documentName, String jobPropertiesXML, String jobPath);

	public Object updateJob(JmxUser jmxUser, String documentName, String jobPropertiesXML, String jobPath);

	public JobProperties getJobFromId(JmxUser jmxUser, String documentName, String jobPath, String jobId) throws XMLDBException;

	public Scenario getScenario(JmxUser jmxUser, String documentName, String scenariPath, String scenarioName) throws XMLDBException;

	public TlosConfigInfo getTlosConfig(JmxUser jmxUser);

	public Object updateTlosConfig(JmxUser jmxUser, String tlosConfigInfoXML);

	public ArrayList<ResourcePermission> getPermissions(JmxUser jmxUser);

	public Object updatePermissions(JmxUser jmxUser, String permissionsXML);

	public Object updateScenario(JmxUser jmxUser, String documentName, String scenarioPath, String scenarioXML) throws XMLDBException;

	public ArrayList<JobProperties> getReportJobs(JmxUser jmxUser, String jobPath, String jobName) throws XMLDBException;

	public TlosProcessData getTlosDataXml(JmxUser jmxUser, String documentName);

//	public byte[] getPdfDoc(JmxUser jmxUser, String str) throws XMLDBException;

//	public byte[] getHtmlDoc(JmxUser jmxUser, String str) throws XMLDBException;

	public Object insertTrace(JmxUser jmxUser, String traceXML);

//	public NrpeDataInfoTypeClient retrieveNagiosAgentInfo(JmxUser jmxUser, MonitorAgentInfoTypeClient nagiosAgentInfoTypeClient);

	public ArrayList<SWAgent> searchAgent(JmxUser jmxUser, String agentXML) throws XMLDBException;

	public ArrayList<SLA> searchSla(JmxUser jmxUser, String slaXML) throws XMLDBException;

	public Object deleteSla(JmxUser jmxUser, String slaXML);

	public Object insertSla(JmxUser jmxUser, String slaXML);

	public Object updateSla(JmxUser jmxUser, String slaXML);

	public ArrayList<License> searchProvision(JmxUser jmxUser, String provisionXML) throws XMLDBException;

	public Object deleteProvision(JmxUser jmxUser, String provisionXML);

	public Object insertProvision(JmxUser jmxUser, String provisionXML);

	public Object updateProvision(JmxUser jmxUser, String provisionXML);

	public Object insertAgent(JmxUser jmxUser, String agentXML);

	public Object updateAgent(JmxUser jmxUser, String agentXML);

	public Object deleteAgent(JmxUser jmxUser, String agentXML);

	public ArrayList<RNSEntryType> resources(JmxUser jmxUser) throws XMLDBException;

	public ArrayList<String> softwares(JmxUser jmxUser) throws XMLDBException;

	public ArrayList<Alarm> searchAlarm(JmxUser jmxUser, String alarmXML) throws XMLDBException;

	public ArrayList<JobProperties> jobList(JmxUser jmxUser) throws XMLDBException;

	public ArrayList<SLA> slaList(JmxUser jmxUser) throws XMLDBException;

	public ArrayList<Alarm> alarmList(JmxUser jmxUser) throws XMLDBException;

	public Object insertAlarm(JmxUser jmxUser, String alarmXML);

	public Object deleteAlarm(JmxUser jmxUser, String alarmXML);

	public Object updateAlarm(JmxUser jmxUser, String alarmXML);

	public Report dashboardReport(JmxUser jmxUser, int derinlik) throws XMLDBException;

	public Calendar getGundonumu(JmxUser jmxUser) throws XMLDBException;

	public JobProperties getJobFromId(JmxUser jmxUser, String documentName, int jobId);

	public ArrayList<AlarmInfoTypeClient> jobAlarmHistory(JmxUser jmxUser, String jobId, Boolean transformToLocalTime) throws XMLDBException;

	public ArrayList<JobInfoTypeClient> getJobResultList(JmxUser jmxUser, String documentName, String jobId, int runNumber, Boolean transformToLocalTime) throws XMLDBException;

	public Scenario getScenarioFromId(JmxUser jmxUser, String documentName, int scenarioId) throws XMLDBException;

	public com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm getAlarmHistoryById(JmxUser jmxUser, int alarmHistoryId) throws XMLDBException;

	public SLA getSlaBySlaId(JmxUser jmxUser, int slaId) throws XMLDBException;

	public ArrayList<DbProperties> searchDBAccessConnection(JmxUser jmxUser, String dbAccessPropertiesXML) throws XMLDBException;

	public Object deleteDBAccessConnection(JmxUser jmxUser, String dbAccessPropertiesXML);

	public Object insertDBAccessConnection(JmxUser jmxUser, String dbAccessPropertiesXML);

	public Object updateDBAccessConnection(JmxUser jmxUser, String dbAccessPropertiesXML);

	public boolean checkDBConnectionName(JmxUser jmxUser, String dbAccessPropertiesXML) throws XMLDBException;

	public ArrayList<JobInfoTypeClient> getJobResultListByDates(JmxUser jmxUser, String documentName, String jobId, String date1, String date2, Boolean transformToLocalTime) throws XMLDBException;

	public ArrayList<JobProperties> getJobResultListByDates2(JmxUser jmxUser, String documentName, String jobId, String date1, String date2) throws XMLDBException;

	public ArrayList<FtpProperties> ftpConnectionList(JmxUser jmxUser) throws XMLDBException;

	public ArrayList<FtpProperties> searchFTPAccessConnection(JmxUser jmxUser, String ftpAccessPropertiesXML) throws XMLDBException;

	public boolean checkFTPConnectionName(JmxUser jmxUser, String ftpAccessPropertiesXML) throws XMLDBException;

	public Object deleteFTPAccessConnection(JmxUser jmxUser, String ftpAccessPropertiesXML);

	public Object insertFTPAccessConnection(JmxUser jmxUser, String ftpAccessPropertiesXML);

	public Object updateFTPAccessConnection(JmxUser jmxUser, String ftpAccessPropertiesXML);

	public Object insertWSDefinition(JmxUser jmxUser, String wsPropertiesXML);

	public ArrayList<WebServiceDefinition> webServiceList(JmxUser jmxUser) throws XMLDBException;

	public ArrayList<AlarmReport> getAlarmReportList(JmxUser jmxUser, String date1, String date2, String alarmLevel, String alarmName, String alarmUser) throws XMLDBException;

	public ArrayList<UserAccessProfile> searchWSAccessProfiles(JmxUser jmxUser, String userAccessProfileXML) throws XMLDBException;

	public Object insertWSAccessProfile(JmxUser jmxUser, String userAccessProfileXML);

	public Object deleteWSAccessProfile(JmxUser jmxUser, String userAccessProfileXML);

	public Object updateWSAccessProfile(JmxUser jmxUser, String userAccessProfileXML);

	public ArrayList<WebServiceDefinition> webServiceListForActiveUser(JmxUser jmxUser, int userId) throws XMLDBException;

	public ArrayList<DbProperties> dbList(JmxUser jmxUser) throws XMLDBException;

	public ArrayList<DBAccessInfoTypeClient> searchDBAccessProfile(JmxUser jmxUser, String dbAccessProfileXML) throws XMLDBException;

	public Object deleteDBAccessProfile(JmxUser jmxUser, String dbAccessProfileXML);

	public Object insertDBAccessProfile(JmxUser jmxUser, String dbAccessProfileXML);

	public Object updateDBAccessProfile(JmxUser jmxUser, String dbAccessProfileXML);

	public ArrayList<DbConnectionProfile> dbProfileList(JmxUser jmxUser) throws XMLDBException;
}

package com.likya.tlossw.web.utils;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.ogf.schemas.rns.x2009.x12.rns.RNSEntryType;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.alarm.AlarmDocument;
import com.likya.tlos.model.xmlbeans.calendar.CalendarPropertiesDocument.CalendarProperties;
import com.likya.tlos.model.xmlbeans.sla.SLADocument.SLA;
import com.likya.tlossw.web.appmng.TraceBean;

public class ComboListUtils {

	public static void logTimeInfo(String header, long timeInfo) {
		if (timeInfo == 0) {
			System.out.println(header);
		} else {
			System.out.println(header + TraceBean.dateDiffWithNow(timeInfo) + "ms");
		}

	}

	public static void logTimeInfo(String header) {
		logTimeInfo(header, 0);
	}

	public static Collection<SelectItem> constructJobStateList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> jobStateList = WebInputUtils.fillJobStateList();
		logTimeInfo("JobBaseBean.WebInputUtils.fillJobStateList Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return jobStateList;
	}

	public static Collection<SelectItem> constructJobSubStateList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> jobSubstateList = WebInputUtils.fillJobSubstateList();
		logTimeInfo("JobBaseBean.WebInputUtils.fillJobSubstateList Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return jobSubstateList;
	}

	public static Collection<SelectItem> constructJobStatusNameList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> jobStatusNameList = WebInputUtils.fillJobStatusList();
		logTimeInfo("JobBaseBean.WebInputUtils.fillJobStatusList fill things Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return jobStatusNameList;
	}

	public static Collection<SelectItem> constructOSystemList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> oSystemList = WebInputUtils.fillOSystemList();
		logTimeInfo("JobBaseBean.WebInputUtils.fillOSystemList Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return oSystemList;
	}

	public static Collection<SelectItem> constructTzList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> tzList = WebInputUtils.fillTZList();
		logTimeInfo("JobBaseBean.WebInputUtils.fillTZList Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return tzList;
	}

	public static Collection<SelectItem> constructJsCalendarList(ArrayList<CalendarProperties> calendarList) {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> tzList = WebInputUtils.fillCalendarList(calendarList);
		logTimeInfo("BaseJSPanelMBean.WebInputUtils.fillCalendarList Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return tzList;
	}

	public static Collection<SelectItem> constructTypeOfTimeList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> typeOfTimeList = WebInputUtils.fillTypesOfTimeList();
		logTimeInfo("JobBaseBean.WebInputUtils.fillTypesOfTimeList Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return typeOfTimeList;
	}

	public static Collection<SelectItem> constructAlarmList(ArrayList<AlarmDocument.Alarm> alarmQueryList) {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> alarmList = WebInputUtils.fillAlarmList(alarmQueryList);
		logTimeInfo("JobBaseBean.WebInputUtils.fillAlarmList Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return alarmList;
	}

	public static Collection<SelectItem> constructDefinedAgentList(ArrayList<SWAgent> agentList) {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> definedAgentList = WebInputUtils.fillAgentList(agentList);
		logTimeInfo("JobBaseBean.WebInputUtils.fillAgentList Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return definedAgentList;
	}

	public static Collection<SelectItem> constructJsSLAList(ArrayList<SLA> slaList) {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> jsSLAList = WebInputUtils.fillSLAList(slaList);
		logTimeInfo("JobBaseBean.WebInputUtils.fillSLAList Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return jsSLAList;
	}

	public static Collection<SelectItem> constructResourceNameList(ArrayList<RNSEntryType> rnsList) {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> resourceNameList = WebInputUtils.fillResourceNameList(rnsList);
		logTimeInfo("JobBaseBean.WebInputUtils.fillResourceNameList fill things Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return resourceNameList;
	}

	public static Collection<SelectItem> constructJobBaseTypeList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> jobBaseTypeList = WebInputUtils.fillJobBaseTypeList();
		logTimeInfo("JobBaseBean.WebInputUtils.fillJobBaseTypeList fill things Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return jobBaseTypeList;
	}

	public static Collection<SelectItem> constructEventTypeDefList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> eventTypeDefList = WebInputUtils.fillEventTypeDefList();
		logTimeInfo("JobBaseBean.WebInputUtils.fillEventTypeDefList fill things Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return eventTypeDefList;
	}

	public static Collection<SelectItem> constructJobTypeDefList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> jobTypeDefList = WebInputUtils.fillJobTypeDefList();
		logTimeInfo("JobBaseBean.WebInputUtils.fillJobTypeDefList fill things Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return jobTypeDefList;
	}

	public static Collection<SelectItem> constructRelativeTimeOptionList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> relativeTimeOptionList = WebInputUtils.fillRelativeTimeOptionList();
		logTimeInfo("JobBaseBean.WebInputUtils.fillRelativeTimeOptionList fill things Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return relativeTimeOptionList;
	}

	public static Collection<SelectItem> constructUnitTypeList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> unitTypeList = WebInputUtils.fillUnitTypeList();
		logTimeInfo("JobBaseBean.WebInputUtils.fillUnitTypeList fill things Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return unitTypeList;
	}

	public static Collection<SelectItem> constructAgentChoiceMethodList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> agentChoiceMethodList = WebInputUtils.fillAgentChoiceMethodList();
		logTimeInfo("JobBaseBean.WebInputUtils.fillAgentChoiceMethodList fill things Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return agentChoiceMethodList;
	}

	public static SelectItem[] createFilterOptions(ArrayList<String> data) {
		SelectItem[] options = new SelectItem[data.size() + 1];

		options[0] = new SelectItem("", "Select");
		for (int i = 0; i < data.size(); i++) {
			options[i + 1] = new SelectItem(data.get(i), data.get(i));
		}

		return options;
	}

	public static Collection<SelectItem> constructOrderList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> orderList = WebInputUtils.fillOrderList();
		logTimeInfo("constructOrderList fill things Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return orderList;
	}

	public static Collection<SelectItem> constructOrderByList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> orderByList = WebInputUtils.fillOrderByList();
		logTimeInfo("constructOrderByList fill things Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return orderByList;
	}

	public static Collection<SelectItem> constructIncludedJobsTypeList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> includedJobsTypeList = WebInputUtils.fillIncludedJobsTypeList();
		logTimeInfo("constructIncludedJobsTypeList fill things Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return includedJobsTypeList;
	}
	
	public static Collection<SelectItem> constructRoleTypeList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> roleTypeList = WebInputUtils.fillRoleTypeList();
		logTimeInfo("constructRoleTypeList fill things Süre : ", startTime);
		startTime = System.currentTimeMillis();

		return roleTypeList;
	}
}

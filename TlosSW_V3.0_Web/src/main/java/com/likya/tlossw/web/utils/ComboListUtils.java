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
	
	public static Collection<SelectItem> constructOSystemList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> oSystemList = WebInputUtils.fillOSystemList();
		System.out.println("JobBaseBean.WebInputUtils.fillOSystemList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();

		return oSystemList;
	}

	public static Collection<SelectItem> constructTzList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> tzList = WebInputUtils.fillTZList();
		System.out.println("JobBaseBean.WebInputUtils.fillTZList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();

		return tzList;
	}

	public static Collection<SelectItem> constructJsCalendarList(ArrayList<CalendarProperties> calendarList) {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> tzList = WebInputUtils.fillCalendarList(calendarList);
		System.out.println("BaseJSPanelMBean.WebInputUtils.fillCalendarList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();

		return tzList;
	}

	public static Collection<SelectItem> constructTypeOfTimeList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> typeOfTimeList = WebInputUtils.fillTypesOfTimeList();
		System.out.println("JobBaseBean.WebInputUtils.fillTypesOfTimeList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();

		return typeOfTimeList;
	}

	public static Collection<SelectItem> constructAlarmList(ArrayList<AlarmDocument.Alarm> alarmQueryList) {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> alarmList = WebInputUtils.fillAlarmList(alarmQueryList);
		System.out.println("JobBaseBean.WebInputUtils.fillAlarmList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();

		return alarmList;
	}

	public static Collection<SelectItem> constructDefinedAgentList(ArrayList<SWAgent> agentList) {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> definedAgentList = WebInputUtils.fillAgentList(agentList);
		System.out.println("JobBaseBean.WebInputUtils.fillAgentList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();

		return definedAgentList;
	}

	public static Collection<SelectItem> constructJsSLAList(ArrayList<SLA> slaList) {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> jsSLAList = WebInputUtils.fillSLAList(slaList);
		System.out.println("JobBaseBean.WebInputUtils.fillSLAList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();

		return jsSLAList;
	}

	public static Collection<SelectItem> constructResourceNameList(ArrayList<RNSEntryType> rnsList) {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> resourceNameList = WebInputUtils.fillResourceNameList(rnsList);
		System.out.println("JobBaseBean.WebInputUtils.fillResourceNameList fill things Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();

		return resourceNameList;
	}

	public static Collection<SelectItem> constructJobBaseTypeList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> jobBaseTypeList = WebInputUtils.fillJobBaseTypeList();
		System.out.println("JobBaseBean.WebInputUtils.fillJobBaseTypeList fill things Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();

		return jobBaseTypeList;
	}

	public static Collection<SelectItem> constructEventTypeDefList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> eventTypeDefList = WebInputUtils.fillEventTypeDefList();
		System.out.println("JobBaseBean.WebInputUtils.fillEventTypeDefList fill things Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();

		return eventTypeDefList;
	}

	public static Collection<SelectItem> constructJobTypeDefList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> jobTypeDefList = WebInputUtils.fillJobTypeDefList();
		System.out.println("JobBaseBean.WebInputUtils.fillJobTypeDefList fill things Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();

		return jobTypeDefList;
	}

	public static Collection<SelectItem> constructRelativeTimeOptionList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> relativeTimeOptionList = WebInputUtils.fillRelativeTimeOptionList();
		System.out.println("JobBaseBean.WebInputUtils.fillRelativeTimeOptionList fill things Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();

		return relativeTimeOptionList;
	}

	public static Collection<SelectItem> constructUnitTypeList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> unitTypeList = WebInputUtils.fillUnitTypeList();
		System.out.println("JobBaseBean.WebInputUtils.fillUnitTypeList fill things Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();

		return unitTypeList;
	}

	public static Collection<SelectItem> constructJobStatusNameList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> jobStatusNameList = WebInputUtils.fillJobStatusList();
		System.out.println("JobBaseBean.WebInputUtils.fillJobStatusList fill things Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();

		return jobStatusNameList;
	}

	public static Collection<SelectItem> constructAgentChoiceMethodList() {

		long startTime = System.currentTimeMillis();
		Collection<SelectItem> agentChoiceMethodList = WebInputUtils.fillAgentChoiceMethodList();
		System.out.println("JobBaseBean.WebInputUtils.fillAgentChoiceMethodList fill things Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();

		return agentChoiceMethodList;
	}

}

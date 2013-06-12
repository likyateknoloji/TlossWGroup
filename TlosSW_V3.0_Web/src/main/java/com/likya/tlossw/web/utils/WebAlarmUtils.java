package com.likya.tlossw.web.utils;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.ogf.schemas.rns.x2009.x12.rns.RNSEntryType;

import com.likya.tlos.model.xmlbeans.alarm.AlarmDocument.Alarm;
import com.likya.tlos.model.xmlbeans.alarm.AlarmTypeDocument.AlarmType;
import com.likya.tlos.model.xmlbeans.alarm.SubscriptionTypeDocument.SubscriptionType;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.sla.BirimAttribute.Birim;
import com.likya.tlos.model.xmlbeans.sla.ConditionAttribute.Condition;
import com.likya.tlos.model.xmlbeans.sla.ForWhatAttribute.ForWhat;
import com.likya.tlos.model.xmlbeans.sla.TimeinAttribute.Timein;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;

public class WebAlarmUtils {

	public static Collection<SelectItem> fillAlarmUserList(ArrayList<Person> userList) {

		Collection<SelectItem> calUserList = new ArrayList<SelectItem>();

		for (Person person : userList) {
			SelectItem item = new SelectItem();
			item.setValue(person.getId() + "");
			item.setLabel(person.getUserName());
			calUserList.add(item);
		}

		return calUserList;
	}

	public static Collection<SelectItem> fillAlarmNameList(ArrayList<Alarm> alarmList) {

		Collection<SelectItem> calNameList = new ArrayList<SelectItem>();

		for (Alarm alarm : alarmList) {
			SelectItem item = new SelectItem();
			item.setValue(alarm.getID() + "");
			item.setLabel(alarm.getName());
			calNameList.add(item);
		}

		return calNameList;
	}

	public static Collection<SelectItem> fillAlarmRoleList(ArrayList<Person> userList) {

		Collection<SelectItem> calRoleList = new ArrayList<SelectItem>();

		for (Person person : userList) {
			SelectItem item = new SelectItem();
			item.setValue(person.getRole().intValue() + "");
			item.setLabel(person.getRole().toString());
			calRoleList.add(item);
		}

		return calRoleList;
	}
 
	public static Collection<SelectItem> fillJobsNameList(ArrayList<JobProperties> jobList) {

		Collection<SelectItem> jobNameList = new ArrayList<SelectItem>();

		for (JobProperties jobProperties : jobList) {
			SelectItem item = new SelectItem();
			item.setValue(jobProperties.getID());
			item.setLabel(jobProperties.getBaseJobInfos().getJsName() + " | " + jobProperties.getID());
			jobNameList.add(item);
		}

		return jobNameList;
	}

	public static Collection<SelectItem> fillResourceList(ArrayList<RNSEntryType> rnsTypeList) {

		Collection<SelectItem> resourceList = new ArrayList<SelectItem>();
		SelectItem item;

		for (RNSEntryType resource : rnsTypeList) {
			item = new SelectItem();
			item.setValue(resource.getEntryName());
			item.setLabel(resource.getEntryName());
			resourceList.add(item);
		}

		return resourceList;
	}

	public static Collection<SelectItem> fillUnitList() {

		String unit = null;

		Collection<SelectItem> unitList = new ArrayList<SelectItem>();

		for (int i = 0; i < Birim.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			unit = Birim.Enum.table.forInt(i + 1).toString();
			item.setValue(unit);
			item.setLabel(unit);
			unitList.add(item);
		}

		return unitList;
	}

	public static Collection<SelectItem> fillTimeinList() {

		String timein = null;

		Collection<SelectItem> timeinList = new ArrayList<SelectItem>();

		for (int i = 0; i < Timein.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			timein = Timein.Enum.table.forInt(i + 1).toString();
			item.setValue(timein);
			item.setLabel(timein);
			timeinList.add(item);
		}

		return timeinList;
	}

	public static Collection<SelectItem> fillConditionList() {

		String condition = null;

		Collection<SelectItem> conditionList = new ArrayList<SelectItem>();

		for (int i = 0; i < Condition.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			condition = Condition.Enum.table.forInt(i + 1).toString();
			item.setValue(condition);
			item.setLabel(condition);
			conditionList.add(item);
		}

		return conditionList;
	}

	public static Collection<SelectItem> fillForList() {

		String part = null;

		Collection<SelectItem> forList = new ArrayList<SelectItem>();

		for (int i = 0; i < ForWhat.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			part = ForWhat.Enum.table.forInt(i + 1).toString();
			item.setValue(part);
			item.setLabel(part);
			forList.add(item);
		}

		return forList;
	}

	public static Collection<SelectItem> fillStateList() {

		String state = null;

		Collection<SelectItem> stateNameList = new ArrayList<SelectItem>();
		SelectItem item = new SelectItem();

		for (int i = 1; i <= StateName.Enum.table.lastInt(); i++) {
			item = new SelectItem();
			state = StateName.Enum.table.forInt(i).toString();
			item.setValue(state);
			item.setLabel(state);
			stateNameList.add(item);
		}

		return stateNameList;
	}

	public static Collection<SelectItem> fillAlarmTypeList() {

		String alarmType = null;

		Collection<SelectItem> alarmTypeList = new ArrayList<SelectItem>();
		SelectItem item = new SelectItem();

		for (int i = 0; i < AlarmType.Enum.table.lastInt(); i++) {
			item = new SelectItem();
			alarmType = AlarmType.Enum.table.forInt(i + 1).toString();
			item.setValue(alarmType);
			item.setLabel(alarmType);
			alarmTypeList.add(item);
		}

		return alarmTypeList;
	}
	
	public static Collection<SelectItem> fillSubscriptionTypeList() {

		String subsType = null;

		Collection<SelectItem> subsTypeList = new ArrayList<SelectItem>();
		SelectItem item = new SelectItem();

		for (int i = 0; i < SubscriptionType.Enum.table.lastInt(); i++) {
			item = new SelectItem();
			subsType = SubscriptionType.Enum.table.forInt(i + 1).toString();
			item.setValue(subsType);
			item.setLabel(subsType);
			subsTypeList.add(item);
		}

		return subsTypeList;
	}
		
	public static Collection<SelectItem> fillSubStateList() {

		String substate = null;

		Collection<SelectItem> subStateNameList = new ArrayList<SelectItem>();
		SelectItem item = new SelectItem();

		for (int i = 0; i < SubstateName.Enum.table.lastInt(); i++) {
			item = new SelectItem();
			substate = SubstateName.Enum.table.forInt(i + 1).toString();
			item.setValue(substate);
			item.setLabel(substate);
			subStateNameList.add(item);
		}

		return subStateNameList;
	}

	public static Collection<SelectItem> fillStateStatusList() {

		String statestatus = null;

		Collection<SelectItem> stateStatusNameList = new ArrayList<SelectItem>();
		SelectItem item = new SelectItem();

		for (int i = 0; i < StatusName.Enum.table.lastInt(); i++) {
			item = new SelectItem();
			statestatus = StatusName.Enum.table.forInt(i + 1).toString();
			item.setValue(statestatus);
			item.setLabel(statestatus);
			stateStatusNameList.add(item);
		}

		return stateStatusNameList;
	}

	public static Collection<SelectItem> fillWarnByList() {

		Collection<SelectItem> warnByList = new ArrayList<SelectItem>();
		SelectItem item;

		item = new SelectItem();
		item.setValue("1");
		item.setLabel("E-mail");
		warnByList.add(item);
		item = new SelectItem();
		item.setValue("2");
		item.setLabel("SMS");
		warnByList.add(item);
		item = new SelectItem();
		item.setValue("3");
		item.setLabel("GUI");
		warnByList.add(item);

		// setAlarmWarnByList(warnByList);

		return warnByList;
	}

	public static Collection<SelectItem> fillScenariosNameList(ArrayList<Scenario> ScenarioList) {

		Collection<SelectItem> scenarioNameList = new ArrayList<SelectItem>();

		for (Scenario scenario : ScenarioList) {
			SelectItem item = new SelectItem();
			item.setValue(scenario.getID());
			item.setLabel(scenario.getBaseScenarioInfos().getJsName() + " | " + scenario.getID());
			scenarioNameList.add(item);
		}

		return scenarioNameList;
	}
	
}

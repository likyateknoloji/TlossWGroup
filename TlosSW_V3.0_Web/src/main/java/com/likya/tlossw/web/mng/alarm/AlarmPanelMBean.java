package com.likya.tlossw.web.mng.alarm;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.primefaces.event.FlowEvent;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.alarm.AlarmChannelTypesDocument.AlarmChannelTypes;
import com.likya.tlos.model.xmlbeans.alarm.AlarmDocument.Alarm;
import com.likya.tlos.model.xmlbeans.alarm.AlarmTypeDocument.AlarmType;
import com.likya.tlos.model.xmlbeans.alarm.CaseManagementDocument.CaseManagement;
import com.likya.tlos.model.xmlbeans.alarm.FocusDocument.Focus;
import com.likya.tlos.model.xmlbeans.alarm.JobDocument.Job;
import com.likya.tlos.model.xmlbeans.alarm.JobsDocument.Jobs;
import com.likya.tlos.model.xmlbeans.alarm.PersonDocument.Person;
import com.likya.tlos.model.xmlbeans.alarm.SLAManagementDocument.SLAManagement;
import com.likya.tlos.model.xmlbeans.alarm.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.alarm.ScenariosDocument.Scenarios;
import com.likya.tlos.model.xmlbeans.alarm.StateManagementDocument.StateManagement;
import com.likya.tlos.model.xmlbeans.alarm.SubscriberDocument.Subscriber;
import com.likya.tlos.model.xmlbeans.alarm.SubscriptionTypeDocument.SubscriptionType;
import com.likya.tlos.model.xmlbeans.alarm.SystemDocument.System;
import com.likya.tlos.model.xmlbeans.alarm.SystemManagementDocument.SystemManagement;
import com.likya.tlos.model.xmlbeans.alarm.SystemsDocument.Systems;
import com.likya.tlos.model.xmlbeans.alarm.TimeManagementDocument.TimeManagement;
import com.likya.tlos.model.xmlbeans.alarm.WarnByDocument.WarnBy;
import com.likya.tlos.model.xmlbeans.common.RoleDocument.Role;
import com.likya.tlos.model.xmlbeans.common.TypeOfTimeDocument.TypeOfTime;
import com.likya.tlos.model.xmlbeans.sla.BirimAttribute.Birim;
import com.likya.tlos.model.xmlbeans.sla.ConditionAttribute.Condition;
import com.likya.tlos.model.xmlbeans.sla.CpuDocument.Cpu;
import com.likya.tlos.model.xmlbeans.sla.DiskDocument.Disk;
import com.likya.tlos.model.xmlbeans.sla.ForAttribute.For;
import com.likya.tlos.model.xmlbeans.sla.HardwareDocument.Hardware;
import com.likya.tlos.model.xmlbeans.sla.MemDocument.Mem;
import com.likya.tlos.model.xmlbeans.sla.TimeinAttribute.Timein;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.web.utils.DefinitionUtils;
import com.likya.tlossw.web.utils.FacesUtils;
import com.likya.tlossw.web.utils.WebAlarmUtils;
import com.likya.tlossw.web.utils.WebInputUtils;

@ManagedBean(name = "alarmPanelMBean")
@ViewScoped
public class AlarmPanelMBean extends AlarmBaseBean {

	private static final Logger logger = Logger.getLogger(AlarmPanelMBean.class);

	private static final long serialVersionUID = -7436267818850177642L;

	@ManagedProperty("param.selectedAlarmName")
	private String selectedAlarmName;

	@ManagedProperty("param.insertCheck")
	private String insertCheck;

	private String iCheck;

	private BigInteger alarmId;

	private boolean insertButton = false;

	private String insertValue;

	private boolean skip;

	private Collection<SelectItem> tZList;

	@PostConstruct
	public void init() {

		logger.info("begin : init");

		setAlarmType("");
		setCaseType("");
		setUserType(SubscriptionType.USER.toString());
		setAlarmDepth("1");

		setTimeOutControl(false);
		setTolerancePercentage(false);
		setMinPercentage(false);
		setUseSlaManagement(true);

		setSelectedResourceList(null);

		setCpuValue("0");
		setCpuUnit("%");
		setMemoryValue("0");
		setDiskValue("0");

		setStateList(new ArrayList<SelectItem>());

		selectedAlarmName = String.valueOf(FacesUtils.getRequestParameter("selectedAlarmName"));
		insertCheck = String.valueOf(FacesUtils.getRequestParameter("insertCheck"));
		iCheck = String.valueOf(FacesUtils.getRequestParameter("iCheck"));

		setSelectedTZone(new String("Europe/Istanbul"));
		setSelectedTypeOfTime(new String("Actual"));

		settZList(WebInputUtils.fillTZList());
		setTypeOfTimeList(WebInputUtils.fillTypesOfTimeList());

		setAlarmUserList(WebAlarmUtils.fillAlarmUserList(getDbOperations().getUsers()));
		setAlarmNameList(WebAlarmUtils.fillAlarmNameList(getDbOperations().getAlarms()));
		setAlarmRoleList(WebInputUtils.fillRoleList());
		setResourceNameList(WebInputUtils.fillResourceNameList(getDbOperations().getResources()));

		try {
			// ilk 20 iş ekranda görünecek
			setAlarmJobNameList(WebAlarmUtils.fillJobsNameList(getDbOperations().getJobList(20)));

			setAlarmScenarioNameList(WebAlarmUtils.fillScenariosNameList(getDbOperations().getScenarioList()));
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		setAlarmTypeList(WebAlarmUtils.fillAlarmTypeList());
		setSubscriptionTypeList(WebAlarmUtils.fillSubscriptionTypeList());
		setAlarmWarnByList(WebAlarmUtils.fillWarnByList());
		fillUnitList();
		fillTimeinList();
		fillConditionList();
		fillForList();
		fillStateList();
		fillSubStateList();
		fillStateStatusList();

		if (iCheck != null && iCheck.equals("insert"))
			insertButton = true;
		if (insertCheck != null) {

			if (insertCheck.equals("update")) {
				insertButton = false;
				setAlarm(Alarm.Factory.newInstance());

				setAlarm(getDbOperations().searchAlarmByName(selectedAlarmName));

				if (getAlarm() != null) {
					fillPanelFromAlarm();
					setAlarmId(getAlarm().getID());
				}
			} else {
				insertButton = true;

			}
		}

		logger.info("end : init");

	}

	public String onFlowProcess(FlowEvent event) {

		if (skip) {
			skip = false; // reset in case user goes back
			return "confirm";
		} else {
			return event.getNewStep();
		}
	}

	public void fillPanelFromAlarm() {

		setAlarmName(getAlarm().getName());
		setAlarmDesc(getAlarm().getDesc());
		setAlarmType(getAlarm().getAlarmType().toString());
		setUserType(getAlarm().getSubscriptionType().toString());

		Date startDate = DefinitionUtils.dateToDate(getAlarm().getStartDate().getTime(), getSelectedTZone());
		Date endDate = DefinitionUtils.dateToDate(getAlarm().getEndDate().getTime(), getSelectedTZone());
		Date creationDate = DefinitionUtils.dateToDate(getAlarm().getCreationDate().getTime(), getSelectedTZone());

		setStartDate(startDate);
		setEndDate(endDate);
		setCreationDate(creationDate);

		if (getAlarm().getTimeZone() != null)
			setSelectedTZone(getAlarm().getTimeZone());
		else
			setSelectedTZone(new String("Europe/Istanbul"));
		if (getAlarm().getTypeOfTime() != null)
			setSelectedTypeOfTime(getAlarm().getTypeOfTime().toString());
		else
			setSelectedTypeOfTime(new String("Broadcast"));

		setAlarmLevel(getAlarm().getLevel().toString());

		setSelectedWarnByList(new String[getAlarm().getSubscriber().getAlarmChannelTypes().getWarnByArray().length]);
		for (int i = 0; i < getAlarm().getSubscriber().getAlarmChannelTypes().getWarnByArray().length; i++) {
			getSelectedWarnByList()[i] = getAlarm().getSubscriber().getAlarmChannelTypes().getWarnByArray(i).getId().toString();
		}

		if (getAlarm().getSubscriptionType().equals(SubscriptionType.USER)) {
			setUserType(SubscriptionType.USER.toString());

			setAlarmUserList(WebAlarmUtils.fillAlarmUserList(getDbOperations().getUsers()));

			setAlarmUser(getAlarm().getSubscriber().getPerson().getId().toString());
		} else {
			setUserType(SubscriptionType.ROLE.toString());
			setAlarmRole(Integer.toString(getAlarm().getSubscriber().getRole().intValue()));
		}

		if (getAlarm().getAlarmType().equals(AlarmType.JOB)) {
			setSelectedJobNameList(new String[getAlarm().getFocus().getJobs().getJobArray().length]);

			for (int i = 0; i < getAlarm().getFocus().getJobs().getJobArray().length; i++) {
				getSelectedJobNameList()[i] = getAlarm().getFocus().getJobs().getJobArray(i).getId().toString();
			}
			setAlarmDepth(getAlarm().getFocus().getJobs().getJobArray(0).getDepth().toString());

		} else if (getAlarm().getAlarmType().equals(AlarmType.SCENARIO)) {
			setSelectedScenarioNameList(new String[getAlarm().getFocus().getScenarios().getScenarioArray().length]);

			for (int i = 0; i < getAlarm().getFocus().getScenarios().getScenarioArray().length; i++) {
				getSelectedScenarioNameList()[i] = getAlarm().getFocus().getScenarios().getScenarioArray(i).getId().toString();
			}
			setAlarmDepth(getAlarm().getFocus().getScenarios().getScenarioArray(0).getDepth().toString());

		} else if (getAlarm().getAlarmType().equals(AlarmType.SYSTEM)) {
			setSelectedResourceList(new String[getAlarm().getFocus().getSystems().getSystemArray().length]);

			for (int i = 0; i < getAlarm().getFocus().getSystems().getSystemArray().length; i++) {
				getSelectedResourceList()[i] = getAlarm().getFocus().getSystems().getSystemArray(i).getEntryName();
			}
		}

		if (getAlarm().getCaseManagement().getStateManagement() != null) {
			setCaseType(STATE_CASE_TYPE);

			for (int i = 0; i < getAlarm().getCaseManagement().getStateManagement().getLiveStateInfoArray().length; i++) {

				String stateName = "";
				if (getAlarm().getCaseManagement().getStateManagement().getLiveStateInfoArray(i).getStateName() != null) {
					stateName = getAlarm().getCaseManagement().getStateManagement().getLiveStateInfoArray(i).getStateName().toString();
					setStateDepth("State");

				}
				if (getAlarm().getCaseManagement().getStateManagement().getLiveStateInfoArray(i).getSubstateName() != null) {
					stateName += "|" + getAlarm().getCaseManagement().getStateManagement().getLiveStateInfoArray(i).getSubstateName().toString();
					setStateDepth("SubState");

				}
				if (getAlarm().getCaseManagement().getStateManagement().getLiveStateInfoArray(i).getStatusName() != null) {
					stateName += "|" + getAlarm().getCaseManagement().getStateManagement().getLiveStateInfoArray(i).getStatusName().toString();
					setStateDepth("Status");
				}

				getStateList().add(new SelectItem(stateName));
			}
		} else if (getAlarm().getCaseManagement().getTimeManagement() != null) {
			setCaseType(TIME_CASE_TYPE);
			TimeManagement timeManagement = getAlarm().getCaseManagement().getTimeManagement();

			if (timeManagement != null && (getAlarmType().equals(AlarmType.JOB.toString()) || getAlarmType().equals(AlarmType.SCENARIO.toString()))) {
				setTimeOutControl(timeManagement.getTimeOutControl());
				setTolerancePercentage(timeManagement.getTolerancePercentage());
				setMinPercentage(timeManagement.getMinPercentage());
			}
		} else if (getAlarm().getCaseManagement().getSLAManagement() != null) {
			setCaseType(SLA_CASE_TYPE);
			if (getAlarm().getCaseManagement().getSLAManagement().equals(SLAManagement.YES)) {
				setUseSlaManagement(true);
			} else {
				setUseSlaManagement(false);
			}
		} else if (getAlarm().getCaseManagement().getSystemManagement() != null) {
			setCaseType(SYSTEM_CASE_TYPE);
			Hardware tmpHardware = getAlarm().getCaseManagement().getSystemManagement().getHardware();

			setCpuTimein(tmpHardware.getCpu().getTimein().toString());
			setCpuUnit(tmpHardware.getCpu().getBirim().toString());
			setCpuCondition(tmpHardware.getCpu().getCondition().toString());
			setCpuValue(tmpHardware.getCpu().getStringValue());

			setDiskPart(tmpHardware.getDisk().getFor().toString());
			setDiskUnit(tmpHardware.getDisk().getBirim().toString());
			setDiskCondition(tmpHardware.getDisk().getCondition().toString());
			setDiskValue(tmpHardware.getDisk().getStringValue());

			setMemoryPart(tmpHardware.getMem().getFor().toString());
			setMemoryUnit(tmpHardware.getMem().getBirim().toString());
			setMemoryCondition(tmpHardware.getMem().getCondition().toString());
			setMemoryValue(tmpHardware.getMem().getStringValue());
		}
	}

	public void updateAlarmAction(ActionEvent e) {
		fillAlarmProperties();
		getAlarm().setID(getAlarmId());

		if (getDbOperations().updateAlarm(getAlarmXML())) {
			addMessage("yeniAlarm", FacesMessage.SEVERITY_INFO, "tlos.success.alarm.update", null);
		} else {
			addMessage("yeniAlarm", FacesMessage.SEVERITY_ERROR, "tlos.error.alarm.update", null);
		}

	}

	public void insertAlarmAction(ActionEvent e) {
		fillAlarmProperties();

		if (getDbOperations().insertAlarm(getAlarmXML())) {
			addMessage("yeniAlarm", FacesMessage.SEVERITY_INFO, "tlos.success.alarm.insert", null);
		} else {
			addMessage("yeniAlarm", FacesMessage.SEVERITY_ERROR, "tlos.error.alarm.insert", null);
		}
	}

	public void addStateAction() {
		if (getStateList() == null) {
			setStateList(new ArrayList<SelectItem>());
		}

		String stateName = getState();

		if (getStateDepth().equals("SubState") || getStateDepth().equals("Status")) {
			stateName += "|" + getSubstate();

			if (getStateDepth().equals("Status")) {
				stateName += "|" + getStatus();
			}
		}

		getStateList().add(new SelectItem(stateName, stateName));

		setState(null);
		setSubstate(null);
		setStatus(null);
	}

	public void deleteStateAction() {

		List<SelectItem> selectitems = new ArrayList<SelectItem>();

		for (int i = 0; i < getSelectedStateList().length; i++) {
			for (SelectItem item : getStateList()) {
				if (item.getValue().equals(getSelectedStateList()[i] + "")) {
					selectitems.add(item);
				}
			}
		}
		for (SelectItem item : selectitems) {
			getStateList().remove(item);
		}
	}

	private void fillAlarmProperties() {

		setAlarm(Alarm.Factory.newInstance());

		getAlarm().setName(getAlarmName());
		getAlarm().setDesc(getAlarmDesc());
		getAlarm().setAlarmType(AlarmType.Enum.forString(getAlarmType()));
		getAlarm().setSubscriptionType(SubscriptionType.Enum.forString(getUserType()));
		getAlarm().setCreationDate(Calendar.getInstance());
		getAlarm().setStartDate(DefinitionUtils.dateToXmlDate(getStartDate()));
		getAlarm().setEndDate(DefinitionUtils.dateToXmlDate(getEndDate()));
		getAlarm().setTimeZone(getSelectedTZone());
		getAlarm().setTypeOfTime(TypeOfTime.Enum.forString(getSelectedTypeOfTime()));
		getAlarm().setLevel(new BigInteger(getAlarmLevel()));

		Subscriber subscriber = Subscriber.Factory.newInstance();
		if (getUserType().equals(SubscriptionType.USER.toString())) {
			Person person = Person.Factory.newInstance();
			person.setId(new BigInteger(getAlarmUser()));
			subscriber.addNewPerson();
			subscriber.setPerson(person);
		} else if (getUserType().equals(SubscriptionType.ROLE.toString())) {
			java.util.Iterator<SelectItem> rolesItem = getAlarmRoleList().iterator();
			while (rolesItem.hasNext()) {
				SelectItem selectItem = rolesItem.next();
				if (selectItem.getValue().toString().equals(getAlarmRole())) {
					subscriber.setRole(Role.Enum.forString(selectItem.getLabel().toString()));
				}
			}
		}

		AlarmChannelTypes alarmChannelTypes = AlarmChannelTypes.Factory.newInstance();
		if (getSelectedWarnByList() != null) {
			for (int i = 0; i < getSelectedWarnByList().length; i++) {
				WarnBy warnBy = WarnBy.Factory.newInstance();
				warnBy.setId(new BigInteger(getSelectedWarnByList()[i]));
				alarmChannelTypes.addNewWarnBy();
				alarmChannelTypes.setWarnByArray(i, warnBy);
			}
			subscriber.setAlarmChannelTypes(alarmChannelTypes);
		}

		getAlarm().setSubscriber(subscriber);

		Focus focus = Focus.Factory.newInstance();
		if (getAlarmType().equals(AlarmType.JOB.toString())) {

			Jobs jobs = Jobs.Factory.newInstance();
			if (getSelectedJobNameList() != null) {

				for (int i = 0; i < getSelectedJobNameList().length; i++) {
					Job job = Job.Factory.newInstance();
					job.setId(new BigInteger(getSelectedJobNameList()[i].toString()));

					if (getAlarmDepth() != null) {
						job.setDepth(new BigInteger(getAlarmDepth()));
					}

					jobs.addNewJob();
					jobs.setJobArray(i, job);
				}
				focus.setJobs(jobs);
			}
		} else if (getAlarmType().equals(AlarmType.SCENARIO.toString())) {

			Scenarios scenarios = Scenarios.Factory.newInstance();
			if (getSelectedScenarioNameList() != null) {

				for (int i = 0; i < getSelectedScenarioNameList().length; i++) {
					Scenario scenario = Scenario.Factory.newInstance();
					scenario.setId(new BigInteger(getSelectedScenarioNameList()[i].toString()));

					if (getAlarmDepth() != null) {
						scenario.setDepth(new BigInteger(getAlarmDepth()));
					}

					scenarios.addNewScenario();
					scenarios.setScenarioArray(i, scenario);
				}
				focus.setScenarios(scenarios);
			}
		} else if (getAlarmType().equals(AlarmType.SYSTEM.toString())) {

			Systems systems = Systems.Factory.newInstance();

			for (int i = 0; i < getSelectedResourceList().length; i++) {
				System system = System.Factory.newInstance();
				system.setEntryName(getSelectedResourceList()[i]);

				systems.addNewSystem();
				systems.setSystemArray(i, system);
			}
			focus.setSystems(systems);

			setCaseType(SYSTEM_CASE_TYPE);
		}

		getAlarm().setFocus(focus);

		CaseManagement caseManagement = CaseManagement.Factory.newInstance();
		if (getCaseType().equals(SYSTEM_CASE_TYPE)) {
			SystemManagement systemManagement = SystemManagement.Factory.newInstance();
			Hardware hardTmp = Hardware.Factory.newInstance();
			Mem memory = Mem.Factory.newInstance();
			Cpu cpu = Cpu.Factory.newInstance();
			Disk disk = Disk.Factory.newInstance();

			if (getHardwareName() != null && !getHardwareName().equals("")) {
				hardTmp.setEntryName(getHardwareName());
			}
			if (getDiskValue() != null && !getDiskValue().equals("")) {
				disk.setStringValue(getDiskValue());
				hardTmp.setDisk(disk);
			}
			if (getDiskCondition() != null && !getDiskCondition().equals("")) {
				disk.setCondition(Condition.Enum.forString(getDiskCondition()));
				hardTmp.setDisk(disk);
			}
			if (getDiskPart() != null && !getDiskPart().equals("")) {
				disk.setFor(For.Enum.forString(getDiskPart()));
				hardTmp.setDisk(disk);
			}
			if (getDiskUnit() != null && !getDiskUnit().equals("")) {
				disk.setBirim(Birim.Enum.forString(getDiskUnit()));
				hardTmp.setDisk(disk);
			}
			if (getMemoryValue() != null && getMemoryValue() != null && !getMemoryValue().equals("")) {
				memory.setStringValue(getMemoryValue());
				hardTmp.setMem(memory);
			}
			if (getMemoryUnit() != null && !getMemoryUnit().equals("")) {
				memory.setBirim(Birim.Enum.forString(getMemoryUnit()));
				hardTmp.setMem(memory);
			}
			if (getMemoryCondition() != null && !getMemoryCondition().equals("")) {
				memory.setCondition(Condition.Enum.forString(getMemoryCondition()));
				hardTmp.setMem(memory);
			}
			if (getMemoryPart() != null && !getMemoryPart().equals("")) {
				memory.setFor(For.Enum.forString(getMemoryPart()));
				hardTmp.setMem(memory);
			}
			if (getCpuValue() != null && !getCpuValue().equals("")) {
				cpu.setStringValue(getCpuValue());
				hardTmp.setCpu(cpu);
			}
			if (getCpuUnit() != null && !getCpuUnit().equals("")) {
				cpu.setBirim(Birim.Enum.forString(getCpuUnit()));
				hardTmp.setCpu(cpu);
			}
			if (getCpuCondition() != null && !getCpuCondition().equals("")) {
				cpu.setCondition(Condition.Enum.forString(getCpuCondition()));
				hardTmp.setCpu(cpu);
			}
			if (getCpuTimein() != null && !getCpuTimein().equals("")) {
				cpu.setTimein(Timein.Enum.forString(getCpuTimein()));
				hardTmp.setCpu(cpu);
			}
			systemManagement.setHardware(hardTmp);
			caseManagement.setSystemManagement(systemManagement);

		} else if (getCaseType().equals(STATE_CASE_TYPE)) {
			StateManagement stateManagement = StateManagement.Factory.newInstance();
			if (getStateList() != null) {
				for (int i = 0; i < getStateList().size(); i++) {
					LiveStateInfo liveStateInfo = LiveStateInfo.Factory.newInstance();

					StringTokenizer stateTokenizer = new StringTokenizer(getStateList().get(i).getValue().toString(), "|");

					liveStateInfo.setStateName(StateName.Enum.forString(stateTokenizer.nextToken()));

					if (stateTokenizer.hasMoreTokens()) {
						liveStateInfo.setSubstateName(SubstateName.Enum.forString(stateTokenizer.nextToken()));

						if (stateTokenizer.hasMoreTokens()) {
							liveStateInfo.setStatusName(StatusName.Enum.forString(stateTokenizer.nextToken()));
						}
					}

					stateManagement.addNewLiveStateInfo();

					liveStateInfo.setLSIDateTime(DefinitionUtils.getW3CDateTime(getSelectedTZone()));
					stateManagement.setLiveStateInfoArray(stateManagement.getLiveStateInfoArray().length - 1, liveStateInfo);
				}
			}
			caseManagement.setStateManagement(stateManagement);

		} else if (getCaseType().equals(SLA_CASE_TYPE)) {
			if (isUseSlaManagement()) {
				caseManagement.setSLAManagement(SLAManagement.YES);
			} else {
				caseManagement.setSLAManagement(SLAManagement.NO);
			}

		} else if (getCaseType().equals(TIME_CASE_TYPE)) {
			TimeManagement timeManagement = TimeManagement.Factory.newInstance();
			timeManagement.setTimeOutControl(isTimeOutControl());
			timeManagement.setTolerancePercentage(isTolerancePercentage());
			timeManagement.setMinPercentage(isMinPercentage());

			caseManagement.setTimeManagement(timeManagement);
		}

		getAlarm().setCaseManagement(caseManagement);
	}

	public String getSelectedAlarmName() {
		return selectedAlarmName;
	}

	public void setSelectedAlarmName(String selectedAlarmName) {
		this.selectedAlarmName = selectedAlarmName;
	}

	public String getInsertCheck() {
		return insertCheck;
	}

	public void setInsertCheck(String insertCheck) {
		this.insertCheck = insertCheck;
	}

	public String getiCheck() {
		return iCheck;
	}

	public void setiCheck(String iCheck) {
		this.iCheck = iCheck;
	}

	public BigInteger getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(BigInteger alarmId) {
		this.alarmId = alarmId;
	}

	public boolean isInsertButton() {
		return insertButton;
	}

	public void setInsertButton(boolean insertButton) {
		this.insertButton = insertButton;
	}

	public String getInsertValue() {
		return insertValue;
	}

	public void setInsertValue(String insertValue) {
		this.insertValue = insertValue;
	}

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	public Collection<SelectItem> gettZList() {
		return tZList;
	}

	public void settZList(Collection<SelectItem> tZList) {
		this.tZList = tZList;
	}

}

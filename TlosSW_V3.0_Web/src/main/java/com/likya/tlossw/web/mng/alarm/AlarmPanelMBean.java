package com.likya.tlossw.web.mng.alarm;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
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
import com.likya.tlos.model.xmlbeans.alarm.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.alarm.ScenariosDocument.Scenarios;
import com.likya.tlos.model.xmlbeans.alarm.StateManagementDocument.StateManagement;
import com.likya.tlos.model.xmlbeans.alarm.SubscriberDocument.Subscriber;
import com.likya.tlos.model.xmlbeans.alarm.SubscriptionTypeDocument.SubscriptionType;
import com.likya.tlos.model.xmlbeans.alarm.SystemManagementDocument.SystemManagement;
import com.likya.tlos.model.xmlbeans.alarm.TimeManagementDocument.TimeManagement;
import com.likya.tlos.model.xmlbeans.alarm.WarnByDocument.WarnBy;
import com.likya.tlos.model.xmlbeans.common.RoleDocument.Role;
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

	@PostConstruct
	public void init() {

		logger.info("begin : init");

		setAlarmType(AlarmType.JOB.toString());
		setUserType(SubscriptionType.USER.toString());

		selectedAlarmName = String.valueOf(FacesUtils.getRequestParameter("selectedAlarmName"));
		insertCheck = String.valueOf(FacesUtils.getRequestParameter("insertCheck"));
		iCheck = String.valueOf(FacesUtils.getRequestParameter("iCheck"));

		try {
			setAlarmUserList(WebAlarmUtils.fillAlarmUserList(getDbOperations().getUsers()));
			setAlarmNameList(WebAlarmUtils.fillAlarmNameList(getDbOperations().getAlarms()));
			setAlarmRoleList(WebAlarmUtils.fillAlarmRoleList(getDbOperations().getUsers()));
			setAlarmJobNameList(WebAlarmUtils.fillJobsNameList(getDbOperations().getJobList()));

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

		setSelectedWarnByList(new String[getAlarm().getSubscriber().getAlarmChannelTypes().getWarnByArray().length]);
		for (int i = 0; i < getAlarm().getSubscriber().getAlarmChannelTypes().getWarnByArray().length; i++) {
			getSelectedWarnByList()[i] = getAlarm().getSubscriber().getAlarmChannelTypes().getWarnByArray(i).getId().toString();
		}

		setAlarmType(getAlarm().getAlarmType().toString());
		setUserType(getAlarm().getSubscriptionType().toString());
		setAlarmDesc(getAlarm().getDesc());
		setAlarmName(getAlarm().getName());
		setStartDate(getAlarm().getStartDate().getTime());
		setEndDate(getAlarm().getEndDate().getTime());
		setAlarmLevel(getAlarm().getLevel().toString());

		try {
			if (getAlarm().getSubscriber().getPerson() != null) {
				setUserType(SubscriptionType.USER.toString());

				setAlarmUserList(WebAlarmUtils.fillAlarmUserList(getDbOperations().getUsers()));

				setAlarmUser(getAlarm().getSubscriber().getPerson().getId().toString());
			} else {
				setUserType(SubscriptionType.ROLE.toString());
				setAlarmRoleList(WebAlarmUtils.fillAlarmRoleList(getDbOperations().getUsers()));
				setAlarmRole(Integer.toString(getAlarm().getSubscriber().getRole().intValue()));
			}

		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		if (getAlarm().getFocus().getJobs() != null) {

			if (getAlarm().getFocus().getJobs().getJobArray() != null) {

				setSelectedJobNameList(new String[getAlarm().getFocus().getJobs().getJobArray().length]);

				for (int i = 0; i < getAlarm().getFocus().getJobs().getJobArray().length; i++) {
					getSelectedJobNameList()[i] = getAlarm().getFocus().getJobs().getJobArray(i).getId().toString();
				}
			}

			setAlarmDepth(getAlarm().getFocus().getJobs().getJobArray(0).getDepth().toString());
		}
		if (getAlarm().getFocus().getScenarios() != null) {

			if (getAlarm().getFocus().getScenarios().getScenarioArray() != null) {

				setSelectedScenarioNameList(new String[getAlarm().getFocus().getScenarios().getScenarioArray().length]);

				for (int i = 0; i < getAlarm().getFocus().getScenarios().getScenarioArray().length; i++) {
					getSelectedScenarioNameList()[i] = getAlarm().getFocus().getScenarios().getScenarioArray(i).getId().toString();
				}
			}

			setAlarmDepth(getAlarm().getFocus().getScenarios().getScenarioArray(0).getDepth().toString());
		}

		if (getAlarmType().equals(AlarmType.SYSTEM.toString()) && getAlarm().getCaseManagement().getSystemManagement().getHardware() != null) {

			Hardware tmpHardware = getAlarm().getCaseManagement().getSystemManagement().getHardware();

			setHardwareName(tmpHardware.getEntryName().toString());
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

		if (getAlarmType().equals(AlarmType.JOB.toString()) && getAlarm().getCaseManagement().getStateManagement() != null) {
			fillStateList();
			fillSubStateList();
			fillStateStatusList();

			if (getStateList() == null) {
				setStateList(new ArrayList<SelectItem>());
			}

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

		}

		TimeManagement timeManagement = getAlarm().getCaseManagement().getTimeManagement();

		if (timeManagement != null && (getAlarmType().equals(AlarmType.JOB.toString()) || getAlarmType().equals(AlarmType.SCENARIO.toString()))) {
			setTimeOutControl(timeManagement.getTimeOutControl() + "");
			setTolerancePercentage(timeManagement.getTolerancePercentage() + "");
			setMinPercentage(timeManagement.getMinPercentage() + "");
		}

	}

	public void updateAlarmAction(ActionEvent e) {
		fillAlarmProperties();

		if (getDbOperations().updateAlarm(getAlarmXML())) {
			addMessage("yeniAlarm", FacesMessage.SEVERITY_INFO, "tlos.success.alarm.update", null);
		} else {
			addMessage("yeniAlarm", FacesMessage.SEVERITY_ERROR, "tlos.error.alarm.update", null);
		}

	}

	public void insertAlarmAction(ActionEvent e) {

		setAlarm(Alarm.Factory.newInstance());

		fillAlarmProperties();

		if (getDbOperations().insertAlarm(getAlarmXML())) {
			addMessage("yeniAlarm", FacesMessage.SEVERITY_INFO, "tlos.success.alarm.insert", null);
		} else {
			addMessage("yeniAlarm", FacesMessage.SEVERITY_ERROR, "tlos.error.alarm.insert", null);
		}

		// try {
		// FacesContext.getCurrentInstance().getExternalContext().redirect("alarmSearchPanel.xhtml");
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }

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

		getAlarm().setName(getAlarmName());
		getAlarm().setDesc(getAlarmDesc());
		getAlarm().setLevel(new BigInteger(getAlarmLevel()));

		getAlarm().setAlarmType(AlarmType.Enum.forString(getAlarmType()));
		getAlarm().setSubscriptionType(SubscriptionType.Enum.forString(getUserType()));

		if (getStartDate() != null) {
			getAlarm().setStartDate(DefinitionUtils.dateToXmlDate(getStartDate()));
		}

		Calendar creationDate = Calendar.getInstance();
		getAlarm().setCreationDate(creationDate);

		if (getEndDate() != null) {
			getAlarm().setEndDate(DefinitionUtils.dateToXmlDate(getEndDate()));
		}

		// girilen stateleri alarm icine set ediyor

		CaseManagement caseManagement = CaseManagement.Factory.newInstance();
		getAlarm().setCaseManagement(caseManagement);

		if (getAlarm().getAlarmType().toString().equals(AlarmType.SYSTEM.toString())) {
			SystemManagement systemManagement = SystemManagement.Factory.newInstance();
			getAlarm().getCaseManagement().setSystemManagement(systemManagement);
		}

		TimeManagement timeManagement = TimeManagement.Factory.newInstance();
		getAlarm().getCaseManagement().setTimeManagement(timeManagement);

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

				liveStateInfo.setLSIDateTime(DefinitionUtils.getW3CDateTime());
				stateManagement.setLiveStateInfoArray(stateManagement.getLiveStateInfoArray().length - 1, liveStateInfo);
			}
		}

		getAlarm().getCaseManagement().setStateManagement(stateManagement);

		if (getAlarm().getCaseManagement().getStateManagement().getLiveStateInfoArray().length == 0) {
			XmlCursor xmlCursor = getAlarm().getCaseManagement().getStateManagement().newCursor();
			xmlCursor.removeXml();
		}

		Subscriber subscriber = Subscriber.Factory.newInstance();

		if (getAlarmUser() != null && !getAlarmUser().equals("")) {
			Person apers = Person.Factory.newInstance();
			apers.setId(new BigInteger(getAlarmUser()));
			subscriber.addNewPerson();
			subscriber.setPerson(apers);
		}
		if (getAlarmRole() != null && !getAlarmRole().equals("")) {
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
		getAlarm().setFocus(focus);
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

			getAlarm().getFocus().setJobs(jobs);

		}

		Scenarios scenarios = Scenarios.Factory.newInstance();

		if (getSelectedScenarioNameList() != null) {
			for (int i = 0; i < getSelectedScenarioNameList().length; i++) {
				Scenario scenario = Scenario.Factory.newInstance();
				if (getAlarmDepth() != null) {
					scenario.setDepth(new BigInteger(getAlarmDepth()));
				}
				scenario.setId(new BigInteger(getSelectedScenarioNameList()[i].toString()));
				scenarios.addNewScenario();
				scenarios.setScenarioArray(i, scenario);
			}

			getAlarm().getFocus().setScenarios(scenarios);

		}

		Hardware hardTmp = Hardware.Factory.newInstance();
		Mem memory = Mem.Factory.newInstance();
		Cpu cpu = Cpu.Factory.newInstance();
		Disk disk = Disk.Factory.newInstance();

		if (getHardwareName() != null && !getHardwareName().equals("")) {
			hardTmp.setEntryName(getHardwareName());
			getAlarm().getCaseManagement().getSystemManagement().setHardware(hardTmp);

		}

		if (getDiskValue() != null && !getDiskValue().equals("")) {
			disk.setStringValue(getDiskValue());
			getAlarm().getCaseManagement().getSystemManagement().getHardware().setDisk(disk);
		}
		if (getDiskCondition() != null && !getDiskCondition().equals("")) {
			disk.setCondition(Condition.Enum.forString(getDiskCondition()));
			getAlarm().getCaseManagement().getSystemManagement().getHardware().setDisk(disk);
		}
		if (getDiskPart() != null && !getDiskPart().equals("")) {
			disk.setFor(For.Enum.forString(getDiskPart()));
			getAlarm().getCaseManagement().getSystemManagement().getHardware().setDisk(disk);
		}
		if (getDiskUnit() != null && !getDiskUnit().equals("")) {
			disk.setBirim(Birim.Enum.forString(getDiskUnit()));
			getAlarm().getCaseManagement().getSystemManagement().getHardware().setDisk(disk);
		}

		if (getMemoryValue() != null && getMemoryValue() != null && !getMemoryValue().equals("")) {
			memory.setStringValue(getMemoryValue());
			getAlarm().getCaseManagement().getSystemManagement().getHardware().setMem(memory);
		}
		if (getMemoryUnit() != null && !getMemoryUnit().equals("")) {
			memory.setBirim(Birim.Enum.forString(getMemoryUnit()));
			getAlarm().getCaseManagement().getSystemManagement().getHardware().setMem(memory);
		}
		if (getMemoryCondition() != null && !getMemoryCondition().equals("")) {
			memory.setCondition(Condition.Enum.forString(getMemoryCondition()));
			getAlarm().getCaseManagement().getSystemManagement().getHardware().setMem(memory);
		}
		if (getMemoryPart() != null && !getMemoryPart().equals("")) {
			memory.setFor(For.Enum.forString(getMemoryPart()));
			getAlarm().getCaseManagement().getSystemManagement().getHardware().setMem(memory);
		}
		if (getCpuValue() != null && !getCpuValue().equals("")) {
			cpu.setStringValue(getCpuValue());
			getAlarm().getCaseManagement().getSystemManagement().getHardware().setCpu(cpu);
		}
		if (getCpuUnit() != null && !getCpuUnit().equals("")) {
			cpu.setBirim(Birim.Enum.forString(getCpuUnit()));
			getAlarm().getCaseManagement().getSystemManagement().getHardware().setCpu(cpu);
		}
		if (getCpuCondition() != null && !getCpuCondition().equals("")) {
			cpu.setCondition(Condition.Enum.forString(getCpuCondition()));
			getAlarm().getCaseManagement().getSystemManagement().getHardware().setCpu(cpu);
		}
		if (getCpuTimein() != null && !getCpuTimein().equals("")) {
			cpu.setTimein(Timein.Enum.forString(getCpuTimein()));
			getAlarm().getCaseManagement().getSystemManagement().getHardware().setCpu(cpu);
		}

		getAlarm().getCaseManagement().getTimeManagement().setTimeOutControl(new Boolean(getTimeOutControl()));
		getAlarm().getCaseManagement().getTimeManagement().setTolerancePercentage(new Boolean(getTolerancePercentage()));
		getAlarm().getCaseManagement().getTimeManagement().setMinPercentage(new Boolean(getMinPercentage()));

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

}

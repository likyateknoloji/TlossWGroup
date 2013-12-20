package com.likya.tlossw.web.mng.alarm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;
import org.primefaces.component.datatable.DataTable;

import com.likya.tlos.model.xmlbeans.alarm.AlarmDocument.Alarm;
import com.likya.tlos.model.xmlbeans.data.JsRealTimeDocument.JsRealTime;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.WebAlarmUtils;

public abstract class AlarmBaseBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = -7436267818850177642L;

	private String alarmType;
	private String caseType;

	private String userType;

	private String alarmLevel;
	private String alarmDesc;

	private String hardwareName;

	private String cpuTimein;
	private String cpuCondition;
	private String cpuValue;
	private String cpuUnit;

	private String memoryPart;
	private String memoryCondition;
	private String memoryValue;
	private String memoryUnit;

	private String diskPart;
	private String diskCondition;
	private String diskValue;
	private String diskUnit;

	private String alarmName;
	private Collection<SelectItem> alarmNameList = null;

	private String selectedTZone;

	private Collection<SelectItem> typeOfTimeList;
	private String selectedTypeOfTime;

	private Alarm alarm;
	private Date startDate;
	private Date endDate;
	private Date creationDate;

	private Date alarmReportStartDate;
	private Date alarmReportFinishDate;

	private Collection<SelectItem> unitList = null;

	private Collection<SelectItem> timeinList = null;
	private Collection<SelectItem> conditionList = null;
	private Collection<SelectItem> forList = null;

	private Collection<SelectItem> resourceNameList = null;
	private String[] selectedResourceList;
	private String selectedResourceForHardware;

	private Collection<SelectItem> alarmUserList = null;
	private String alarmUser;
	private Collection<SelectItem> alarmRoleList = null;
	private String alarmRole;

	private String alarmWarnBy = null;
	private Collection<SelectItem> alarmWarnByList = null;
	private String[] selectedWarnByList;

	private Collection<SelectItem> alarmJobNameList = null;
	private String[] selectedJobNameList;
	private String alarmReportJob;

	private Collection<SelectItem> alarmScenarioNameList = null;
	private String[] selectedScenarioNameList;

	private Collection<SelectItem> alarmStateList = null;
	private String[] selectedStateList;

	private Collection<SelectItem> alarmSubStateList = null;
	private String[] selectedSubStateList;

	private Collection<SelectItem> alarmTypeList = null;
	private Collection<SelectItem> subscriptionTypeList = null;

	private Collection<SelectItem> alarmStateStatusList = null;
	private String[] selectedStateStatusList;

	private String stateDepth = "State";
	private String state;
	private String substate;
	private String status;

	private List<SelectItem> stateList;

	private boolean timeOutControl = false;
	private boolean tolerancePercentage = false;
	private boolean minPercentage = false;

	private ArrayList<Alarm> searchAlarmList;
	private transient DataTable searchAlarmTable;
	private Alarm selectedRow;

	private JsRealTime jsRealTime;

	private boolean useSlaManagement = true;
	
	public static final String SYSTEM_CASE_TYPE = "system";
	public static final String STATE_CASE_TYPE = "state";
	public static final String SLA_CASE_TYPE = "sla";
	public static final String TIME_CASE_TYPE = "time";

	public String getAlarmXML() {

		QName qName = Alarm.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String alarmXML = getAlarm().xmlText(xmlOptions);

		return alarmXML;
	}

	public void fillStateList() {
		setAlarmStateList(WebAlarmUtils.fillStateList());
	}

	public void fillSubStateList() {
		setAlarmSubStateList(WebAlarmUtils.fillSubStateList());
	}

	public void fillStateStatusList() {
		setAlarmStateStatusList(WebAlarmUtils.fillStateStatusList());
	}

	public void fillUnitList() {
		if (getUnitList() == null) {
			setUnitList(WebAlarmUtils.fillUnitList());
		}
	}

	public void fillTimeinList() {
		if (getTimeinList() == null) {
			setTimeinList(WebAlarmUtils.fillTimeinList());
		}
	}

	public void fillConditionList() {
		if (getConditionList() == null) {
			setConditionList(WebAlarmUtils.fillConditionList());
		}
	}

	public void fillForList() {
		if (getForList() == null) {
			setForList(WebAlarmUtils.fillForList());
		}
	}

	public String getHardwareName() {
		return hardwareName;
	}

	public void setHardwareName(String hardwareName) {
		this.hardwareName = hardwareName;
	}

	public String getCpuTimein() {
		return cpuTimein;
	}

	public void setCpuTimein(String cpuTimein) {
		this.cpuTimein = cpuTimein;
	}

	public String getCpuCondition() {
		return cpuCondition;
	}

	public void setCpuCondition(String cpuCondition) {
		this.cpuCondition = cpuCondition;
	}

	public String getCpuValue() {
		return cpuValue;
	}

	public void setCpuValue(String cpuValue) {
		this.cpuValue = cpuValue;
	}

	public String getCpuUnit() {
		return cpuUnit;
	}

	public void setCpuUnit(String cpuUnit) {
		this.cpuUnit = cpuUnit;
	}

	public String getMemoryPart() {
		return memoryPart;
	}

	public void setMemoryPart(String memoryPart) {
		this.memoryPart = memoryPart;
	}

	public String getMemoryCondition() {
		return memoryCondition;
	}

	public void setMemoryCondition(String memoryCondition) {
		this.memoryCondition = memoryCondition;
	}

	public String getMemoryValue() {
		return memoryValue;
	}

	public void setMemoryValue(String memoryValue) {
		this.memoryValue = memoryValue;
	}

	public String getMemoryUnit() {
		return memoryUnit;
	}

	public void setMemoryUnit(String memoryUnit) {
		this.memoryUnit = memoryUnit;
	}

	public String getDiskPart() {
		return diskPart;
	}

	public void setDiskPart(String diskPart) {
		this.diskPart = diskPart;
	}

	public String getDiskCondition() {
		return diskCondition;
	}

	public void setDiskCondition(String diskCondition) {
		this.diskCondition = diskCondition;
	}

	public String getDiskValue() {
		return diskValue;
	}

	public void setDiskValue(String diskValue) {
		this.diskValue = diskValue;
	}

	public String getDiskUnit() {
		return diskUnit;
	}

	public void setDiskUnit(String diskUnit) {
		this.diskUnit = diskUnit;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(String s) {
		this.alarmLevel = s;
		// if (alarmLevel != null && !alarmLevel.equals("")) {
		// alarm.setLevel(new BigInteger(alarmLevel));
		// }
	}

	public String getAlarmDesc() {
		return alarmDesc;
	}

	public void setAlarmDesc(String alarmDesc) {
		this.alarmDesc = alarmDesc;
	}

	public String getAlarmName() {
		return alarmName;
	}

	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}

	public Collection<SelectItem> getAlarmNameList() {
		return alarmNameList;
	}

	public void setAlarmNameList(Collection<SelectItem> alarmNameList) {
		this.alarmNameList = alarmNameList;
	}

	public Alarm getAlarm() {
		return alarm;
	}

	public void setAlarm(Alarm alarm) {
		this.alarm = alarm;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getAlarmReportStartDate() {
		return alarmReportStartDate;
	}

	public void setAlarmReportStartDate(Date alarmReportStartDate) {
		this.alarmReportStartDate = alarmReportStartDate;
	}

	public Date getAlarmReportFinishDate() {
		return alarmReportFinishDate;
	}

	public void setAlarmReportFinishDate(Date alarmReportFinishDate) {
		this.alarmReportFinishDate = alarmReportFinishDate;
	}

	public Collection<SelectItem> getUnitList() {
		return unitList;
	}

	public void setUnitList(Collection<SelectItem> unitList) {
		this.unitList = unitList;
	}

	public Collection<SelectItem> getTimeinList() {
		return timeinList;
	}

	public void setTimeinList(Collection<SelectItem> timeinList) {
		this.timeinList = timeinList;
	}

	public Collection<SelectItem> getConditionList() {
		return conditionList;
	}

	public void setConditionList(Collection<SelectItem> conditionList) {
		this.conditionList = conditionList;
	}

	public Collection<SelectItem> getForList() {
		return forList;
	}

	public void setForList(Collection<SelectItem> forList) {
		this.forList = forList;
	}

	public Collection<SelectItem> getResourceNameList() {
		return resourceNameList;
	}

	public void setResourceNameList(Collection<SelectItem> resourceNameList) {
		this.resourceNameList = resourceNameList;
	}

	public String[] getSelectedResourceList() {
		return selectedResourceList;
	}

	public void setSelectedResourceList(String[] selectedResourceList) {
		this.selectedResourceList = selectedResourceList;
	}

	public String getSelectedResourceForHardware() {
		return selectedResourceForHardware;
	}

	public void setSelectedResourceForHardware(String selectedResourceForHardware) {
		this.selectedResourceForHardware = selectedResourceForHardware;
	}

	public Collection<SelectItem> getAlarmUserList() {
		return alarmUserList;
	}

	public void setAlarmUserList(Collection<SelectItem> alarmUserList) {
		this.alarmUserList = alarmUserList;
	}

	public String getAlarmUser() {
		return alarmUser;
	}

	public void setAlarmUser(String alarmUser) {
		this.alarmUser = alarmUser;
	}

	public Collection<SelectItem> getAlarmRoleList() {
		return alarmRoleList;
	}

	public void setAlarmRoleList(Collection<SelectItem> alarmRoleList) {
		this.alarmRoleList = alarmRoleList;
	}

	public String getAlarmRole() {
		return alarmRole;
	}

	public void setAlarmRole(String alarmRole) {
		this.alarmRole = alarmRole;
	}

	public String getAlarmWarnBy() {
		return alarmWarnBy;
	}

	public void setAlarmWarnBy(String alarmWarnBy) {
		this.alarmWarnBy = alarmWarnBy;
	}

	public Collection<SelectItem> getAlarmWarnByList() {
		return alarmWarnByList;
	}

	public void setAlarmWarnByList(Collection<SelectItem> alarmWarnByList) {
		this.alarmWarnByList = alarmWarnByList;
	}

	public String[] getSelectedWarnByList() {
		return selectedWarnByList;
	}

	public void setSelectedWarnByList(String[] selectedWarnByList) {
		this.selectedWarnByList = selectedWarnByList;
	}

	public Collection<SelectItem> getAlarmJobNameList() {
		return alarmJobNameList;
	}

	public void setAlarmJobNameList(Collection<SelectItem> alarmJobNameList) {
		this.alarmJobNameList = alarmJobNameList;
	}

	public String[] getSelectedJobNameList() {
		return selectedJobNameList;
	}

	public void setSelectedJobNameList(String[] selectedJobNameList) {
		this.selectedJobNameList = selectedJobNameList;
	}

	public String getAlarmReportJob() {
		return alarmReportJob;
	}

	public void setAlarmReportJob(String alarmReportJob) {
		this.alarmReportJob = alarmReportJob;
	}

	public Collection<SelectItem> getAlarmScenarioNameList() {
		return alarmScenarioNameList;
	}

	public void setAlarmScenarioNameList(Collection<SelectItem> alarmScenarioNameList) {
		this.alarmScenarioNameList = alarmScenarioNameList;
	}

	public String[] getSelectedScenarioNameList() {
		return selectedScenarioNameList;
	}

	public void setSelectedScenarioNameList(String[] selectedScenarioNameList) {
		this.selectedScenarioNameList = selectedScenarioNameList;
	}

	public Collection<SelectItem> getAlarmStateList() {
		return alarmStateList;
	}

	public void setAlarmStateList(Collection<SelectItem> alarmStateList) {
		this.alarmStateList = alarmStateList;
	}

	public String[] getSelectedStateList() {
		return selectedStateList;
	}

	public void setSelectedStateList(String[] selectedStateList) {
		this.selectedStateList = selectedStateList;
	}

	public Collection<SelectItem> getAlarmSubStateList() {
		return alarmSubStateList;
	}

	public void setAlarmSubStateList(Collection<SelectItem> alarmSubStateList) {
		this.alarmSubStateList = alarmSubStateList;
	}

	public String[] getSelectedSubStateList() {
		return selectedSubStateList;
	}

	public void setSelectedSubStateList(String[] selectedSubStateList) {
		this.selectedSubStateList = selectedSubStateList;
	}

	public Collection<SelectItem> getAlarmStateStatusList() {
		return alarmStateStatusList;
	}

	public void setAlarmStateStatusList(Collection<SelectItem> alarmStateStatusList) {
		this.alarmStateStatusList = alarmStateStatusList;
	}

	public String[] getSelectedStateStatusList() {
		return selectedStateStatusList;
	}

	public void setSelectedStateStatusList(String[] selectedStateStatusList) {
		this.selectedStateStatusList = selectedStateStatusList;
	}

	public String getStateDepth() {
		return stateDepth;
	}

	public void setStateDepth(String stateDepth) {
		this.stateDepth = stateDepth;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSubstate() {
		return substate;
	}

	public void setSubstate(String substate) {
		this.substate = substate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<SelectItem> getStateList() {
		return stateList;
	}

	public void setStateList(List<SelectItem> stateList) {
		this.stateList = stateList;
	}

	public ArrayList<Alarm> getSearchAlarmList() {
		return searchAlarmList;
	}

	public void setSearchAlarmList(ArrayList<Alarm> searchAlarmList) {
		this.searchAlarmList = searchAlarmList;
	}

	public DataTable getSearchAlarmTable() {
		return searchAlarmTable;
	}

	public void setSearchAlarmTable(DataTable searchAlarmTable) {
		this.searchAlarmTable = searchAlarmTable;
	}

	public JsRealTime getJsRealTime() {
		return jsRealTime;
	}

	public void setJsRealTime(JsRealTime jsRealTime) {
		this.jsRealTime = jsRealTime;
	}

	public Collection<SelectItem> getAlarmTypeList() {
		return alarmTypeList;
	}

	public void setAlarmTypeList(Collection<SelectItem> alarmTypeList) {
		this.alarmTypeList = alarmTypeList;
	}

	public Collection<SelectItem> getSubscriptionTypeList() {
		return subscriptionTypeList;
	}

	public void setSubscriptionTypeList(Collection<SelectItem> subscriptionTypeList) {
		this.subscriptionTypeList = subscriptionTypeList;
	}

	public String getSelectedTZone() {
		return selectedTZone;
	}

	public void setSelectedTZone(String selectedTZone) {
		this.selectedTZone = selectedTZone;
	}

	public Collection<SelectItem> getTypeOfTimeList() {
		return typeOfTimeList;
	}

	public void setTypeOfTimeList(Collection<SelectItem> typeOfTimeList) {
		this.typeOfTimeList = typeOfTimeList;
	}

	public String getSelectedTypeOfTime() {
		return selectedTypeOfTime;
	}

	public void setSelectedTypeOfTime(String selectedTypeOfTime) {
		this.selectedTypeOfTime = selectedTypeOfTime;
	}

	public Alarm getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(Alarm selectedRow) {
		this.selectedRow = selectedRow;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getCaseType() {
		return caseType;
	}

	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}

	public boolean isUseSlaManagement() {
		return useSlaManagement;
	}

	public void setUseSlaManagement(boolean useSlaManagement) {
		this.useSlaManagement = useSlaManagement;
	}

	public boolean isTimeOutControl() {
		return timeOutControl;
	}

	public void setTimeOutControl(boolean timeOutControl) {
		this.timeOutControl = timeOutControl;
	}

	public boolean isTolerancePercentage() {
		return tolerancePercentage;
	}

	public void setTolerancePercentage(boolean tolerancePercentage) {
		this.tolerancePercentage = tolerancePercentage;
	}

	public boolean isMinPercentage() {
		return minPercentage;
	}

	public void setMinPercentage(boolean minPercentage) {
		this.minPercentage = minPercentage;
	}

}

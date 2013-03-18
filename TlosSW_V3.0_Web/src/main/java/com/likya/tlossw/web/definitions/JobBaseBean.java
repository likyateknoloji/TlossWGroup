package com.likya.tlossw.web.definitions;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

import com.likya.tlos.model.xmlbeans.common.AgentChoiceMethodDocument.AgentChoiceMethod;
import com.likya.tlos.model.xmlbeans.common.ChoiceType;
import com.likya.tlos.model.xmlbeans.common.EventTypeDefDocument.EventTypeDef;
import com.likya.tlos.model.xmlbeans.common.InParamDocument.InParam;
import com.likya.tlos.model.xmlbeans.common.JobBaseTypeDocument.JobBaseType;
import com.likya.tlos.model.xmlbeans.common.JobTypeDefDocument.JobTypeDef;
import com.likya.tlos.model.xmlbeans.common.JobTypeDetailsDocument.JobTypeDetails;
import com.likya.tlos.model.xmlbeans.common.JsTypeDocument.JsType;
import com.likya.tlos.model.xmlbeans.common.LocalParametersDocument.LocalParameters;
import com.likya.tlos.model.xmlbeans.common.SpecialParametersDocument.SpecialParameters;
import com.likya.tlos.model.xmlbeans.common.UnitDocument.Unit;
import com.likya.tlos.model.xmlbeans.data.AdvancedJobInfosDocument.AdvancedJobInfos;
import com.likya.tlos.model.xmlbeans.data.AlarmPreferenceDocument.AlarmPreference;
import com.likya.tlos.model.xmlbeans.data.BaseJobInfosDocument.BaseJobInfos;
import com.likya.tlos.model.xmlbeans.data.BaseScenarioInfosDocument.BaseScenarioInfos;
import com.likya.tlos.model.xmlbeans.data.CascadingConditionsDocument.CascadingConditions;
import com.likya.tlos.model.xmlbeans.data.ConcurrencyManagementDocument.ConcurrencyManagement;
import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.ExpectedTimeDocument.ExpectedTime;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.data.JobAutoRetryDocument.JobAutoRetry;
import com.likya.tlos.model.xmlbeans.data.JobInfosDocument.JobInfos;
import com.likya.tlos.model.xmlbeans.data.JobListDocument.JobList;
import com.likya.tlos.model.xmlbeans.data.JobPriorityDocument.JobPriority;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.JobSafeToRestartDocument.JobSafeToRestart;
import com.likya.tlos.model.xmlbeans.data.JsIsActiveDocument.JsIsActive;
import com.likya.tlos.model.xmlbeans.data.JsPlannedTimeDocument.JsPlannedTime;
import com.likya.tlos.model.xmlbeans.data.JsRelativeTimeOptionDocument.JsRelativeTimeOption;
import com.likya.tlos.model.xmlbeans.data.JsTimeOutDocument.JsTimeOut;
import com.likya.tlos.model.xmlbeans.data.OSystemDocument.OSystem;
import com.likya.tlos.model.xmlbeans.data.ResourceRequirementDocument.ResourceRequirement;
import com.likya.tlos.model.xmlbeans.data.RunEvenIfFailedDocument.RunEvenIfFailed;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.data.StartTimeDocument.StartTime;
import com.likya.tlos.model.xmlbeans.data.StateInfosDocument.StateInfos;
import com.likya.tlos.model.xmlbeans.data.StopTimeDocument.StopTime;
import com.likya.tlos.model.xmlbeans.data.TimeManagementDocument.TimeManagement;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.parameters.PreValueDocument.PreValue;
import com.likya.tlos.model.xmlbeans.sla.BirimAttribute.Birim;
import com.likya.tlos.model.xmlbeans.sla.ConditionAttribute.Condition;
import com.likya.tlos.model.xmlbeans.sla.CpuDocument.Cpu;
import com.likya.tlos.model.xmlbeans.sla.DiskDocument.Disk;
import com.likya.tlos.model.xmlbeans.sla.ForAttribute.For;
import com.likya.tlos.model.xmlbeans.sla.HardwareDocument.Hardware;
import com.likya.tlos.model.xmlbeans.sla.MemDocument.Mem;
import com.likya.tlos.model.xmlbeans.sla.TimeinAttribute.Timein;
import com.likya.tlos.model.xmlbeans.state.JobStatusListDocument.JobStatusList;
import com.likya.tlos.model.xmlbeans.state.JsDependencyRuleDocument.JsDependencyRule;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfosDocument.LiveStateInfos;
import com.likya.tlos.model.xmlbeans.state.ReturnCodeDocument.ReturnCode;
import com.likya.tlos.model.xmlbeans.state.ReturnCodeListDocument.ReturnCodeList;
import com.likya.tlos.model.xmlbeans.state.ReturnCodeListDocument.ReturnCodeList.OsType;
import com.likya.tlos.model.xmlbeans.state.ScenarioStatusListDocument.ScenarioStatusList;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.Status;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.tree.JSTree;
import com.likya.tlossw.web.utils.ConstantDefinitions;
import com.likya.tlossw.web.utils.DefinitionUtils;
import com.likya.tlossw.web.utils.WebInputUtils;

public abstract class JobBaseBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = -3792738737288576190L;

	@ManagedProperty(value = "#{jSTree}")
	private JSTree jSTree;

	private JobProperties jobProperties;

	private Scenario scenario;
	private boolean isScenario = false;

	private boolean jobInsertButton = false;

	public final static String PERIOD_TIME_PARAM = "Period Time";

	public final static String CONFIRM = "confirm";

	public final static String NONE = "none";

	public final static String STATE = "State";
	public final static String SUBSTATE = "SubState";
	public final static String STATUS = "Status";

	public final static String SERBEST = "serbest";

	public final static String FULLTEXT = "fullText";
	public final static String REGEX = "regex";
	public final static String REGEX_WITH_EXCLUDE = "regexWithExclude";
	public final static String WILDCARD = "wildcard";
	public final static String WILDCARD_WITH_EXCLUDE = "wildcardWithExclude";

	public final static String DOM = "1";
	public final static String SAX = "2";
	public final static String OBJECT = "3";

	private String jobPathInScenario;

	private boolean jsActiveDialogShow = false;
	private boolean jsActive = false;

	// baseJobInfos
	private Collection<SelectItem> jsCalendarList = null;
	private String jobCalendar;

	private Collection<SelectItem> oSystemList = null;
	private String oSystem;

	private String jobPriority;

	private Collection<SelectItem> jobBaseTypeList = null;
	private String jobBaseType = JobBaseType.NON_PERIODIC.toString();

	private Collection<SelectItem> jobTypeDefList = null;
	private String jobTypeDef = JobTypeDef.TIME_BASED.toString();

	private Collection<SelectItem> eventTypeDefList = null;
	private String eventTypeDef = EventTypeDef.FILE.toString();

	private Collection<SelectItem> jobCommandTypeList = null;
	private String jobCommandType;

	/* periodic job */
	private String periodTime;

	// time management
	private boolean useTimeManagement = false;

	private String startTime;

	private boolean defineStopTime = false;
	private String stopTime;

	private int gmt;
	private boolean dst;

	private Collection<SelectItem> relativeTimeOptionList = null;
	private String relativeTimeOption;

	private String jobTimeOutValue;
	private String jobTimeOutUnit;
	private Collection<SelectItem> unitTypeList = null;

	private String expectedTime;
	private String expectedTimeUnit;

	private String tolerancePercentage;

	private String minPercentage;

	// dependencyDefinitions
	private String draggedJobName;
	private String draggedJobPath;

	private String dependencyTreePath;

	private List<SelectItem> manyJobDependencyList = new ArrayList<SelectItem>();
	private String[] selectedJobDependencyList;

	private String dependencyExpression;

	/* dependency popup */
	private boolean dependencyDialogShow = false;

	private Item dependencyItem;

	private String dependencyType = STATE;

	private Collection<SelectItem> depStateNameList = null;
	private String depStateName;

	private Collection<SelectItem> depSubstateNameList = null;
	private String depSubstateName;

	private String depStatusName;

	private boolean dependencyInsertButton = true;

	// cascadingConditions
	private boolean runEvenIfFailed;
	private boolean jobSafeToRestart;
	private boolean jobAutoRetry;

	// stateInfos
	private Collection<SelectItem> jobStatusNameList = null;
	private String jobStatusName;

	private List<SelectItem> manyJobStatusList;
	private String[] selectedJobStatusList;

	/* live state info */
	private String stateName;
	private String subStateName;

	/* jobStatusPopup */
	private boolean statusDialogShow = false;

	private JobStatusList jobStatusList;
	private Status jobStatus;
	private ReturnCode returnCode;

	private ScenarioStatusList scenarioStatusList;

	private Collection<SelectItem> osTypeList = null;
	private String osType;

	private List<SelectItem> manyReturnCodeList;
	private String[] selectedReturnCodeList;

	// concurrencyManagement
	private boolean concurrent;

	// alarmPreference
	private Collection<SelectItem> alarmList = null;
	private String[] selectedAlarmList;

	// localParameters
	private String paramName;
	private String paramDesc;
	private String paramType;
	private String paramPreValue;

	private String selectedParamName;

	private ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
	private transient DataTable parameterTable;

	private boolean renderUpdateParamButton = false;

	// advancedJobInfos
	private Collection<SelectItem> agentChoiceMethodList = null;
	private String agentChoiceMethod;

	private Collection<SelectItem> definedAgentList = null;
	private String selectedAgent;

	private String jobSLA;
	private Collection<SelectItem> jsSLAList = null;

	private boolean useResourceReq = false;

	private boolean resourceBasedDef = false;

	private Collection<SelectItem> resourceNameList = null;
	private String selectedResourceForHardware;

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

	public void fillJobPanel() {
		fillBaseInfosTab();
		fillTimeManagementTab();
		fillDependencyDefinitionsTab();
		fillCascadingConditionsTab();
		fillStateInfosTab();
		fillConcurrencyManagementTab();
		fillAlarmPreferenceTab();
		fillLocalParametersTab();
		fillAdvancedJobInfosTab();
	}

	private void fillBaseInfosTab() {
		BaseJobInfos baseJobInfos = jobProperties.getBaseJobInfos();
		jobCalendar = baseJobInfos.getCalendarId() + "";
		oSystem = baseJobInfos.getOSystem().toString();
		jobPriority = baseJobInfos.getJobPriority().toString();
		jobBaseType = baseJobInfos.getJobInfos().getJobBaseType().toString();
		jobTypeDef = baseJobInfos.getJobInfos().getJobTypeDef().toString();

		if (jobTypeDef.equals(JobTypeDef.EVENT_BASED.toString())) {
			eventTypeDef = baseJobInfos.getJobInfos().getJobTypeDetails().getEventTypeDef().toString();
		}

		if (baseJobInfos.getJsIsActive().equals(JsIsActive.YES)) {
			jsActive = true;
		} else {
			jsActive = false;
		}
	}

	private void fillTimeManagementTab() {
		TimeManagement timeManagement = jobProperties.getTimeManagement();
		if (timeManagement.getJsPlannedTime() != null && timeManagement.getJsPlannedTime().getStartTime() != null) {
			Calendar jobCalendar = timeManagement.getJsPlannedTime().getStartTime().getTime();

			startTime = DefinitionUtils.calendarToStringTimeFormat(jobCalendar);
			gmt = DefinitionUtils.calendarToGMT(jobCalendar);

			if (DefinitionUtils.calendarToDST(jobCalendar) == 1) {
				dst = true;
			}

			if (timeManagement.getJsPlannedTime().getStopTime() != null) {
				defineStopTime = true;
				stopTime = DefinitionUtils.calendarToStringTimeFormat(timeManagement.getJsPlannedTime().getStopTime().getTime());
			}
		}

		if (timeManagement.getJsRelativeTimeOption() != null) {
			relativeTimeOption = timeManagement.getJsRelativeTimeOption().toString();
		}

		if (timeManagement.getJsTimeOut() != null) {
			JsTimeOut timeOut = timeManagement.getJsTimeOut();

			if (timeOut.getValueInteger() != null) {
				jobTimeOutValue = timeOut.getValueInteger() + "";
			}
			if (timeOut.getUnit() != null) {
				jobTimeOutUnit = timeOut.getUnit().toString();
			}
		}

		if (timeManagement.getExpectedTime() != null) {
			ExpectedTime jobExpectedTime = timeManagement.getExpectedTime();

			if (jobExpectedTime.getValueInteger() != null) {
				expectedTime = jobExpectedTime.getValueInteger() + "";
			}
			if (jobExpectedTime.getUnit() != null) {
				expectedTimeUnit = jobExpectedTime.getUnit().toString();
			}
		}

		// job taniminda bu alan yoksa degeri sifir geliyor
		if (timeManagement.getTolerancePercentage() > 0) {
			tolerancePercentage = timeManagement.getTolerancePercentage() + "";
		}

		// job taniminda bu alan yoksa degeri sifir geliyor
		if (timeManagement.getMinPercentage() > 0) {
			minPercentage = timeManagement.getMinPercentage() + "";
		}
	}

	private void fillDependencyDefinitionsTab() {
		if (jobProperties.getDependencyList() != null && jobProperties.getDependencyList().sizeOfItemArray() > 0) {

			for (Item item : jobProperties.getDependencyList().getItemArray()) {
				String depPathAndName = item.getJsPath() + "." + item.getJsName();

				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(item.getJsName());
				selectItem.setValue(depPathAndName);

				manyJobDependencyList.add(selectItem);
			}

			dependencyExpression = jobProperties.getDependencyList().getDependencyExpression();
		}
	}

	private void fillCascadingConditionsTab() {
		CascadingConditions cascadingConditions = jobProperties.getCascadingConditions();

		if (cascadingConditions.getJobAutoRetry().equals(JobAutoRetry.YES)) {
			jobAutoRetry = true;
		} else {
			jobAutoRetry = false;
		}

		if (cascadingConditions.getJobSafeToRestart().equals(JobSafeToRestart.YES)) {
			jobSafeToRestart = true;
		} else {
			jobSafeToRestart = false;
		}

		if (cascadingConditions.getRunEvenIfFailed().equals(RunEvenIfFailed.YES)) {
			runEvenIfFailed = true;
		} else {
			runEvenIfFailed = false;
		}
	}

	private void fillStateInfosTab() {
		if (!jobInsertButton) {
			stateName = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().toString();
			subStateName = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName().toString();
		}

		// durum tanimi yapildiysa alanlari dolduruyor
		if (jobProperties.getStateInfos() != null && jobProperties.getStateInfos().getJobStatusList() != null) {

			manyJobStatusList = new ArrayList<SelectItem>();
			for (Status jobStatus : jobProperties.getStateInfos().getJobStatusList().getJobStatusArray()) {
				String statusName = jobStatus.getStatusName().toString();
				manyJobStatusList.add(new SelectItem(statusName, statusName));
			}
		}
	}

	private void fillConcurrencyManagementTab() {
		concurrent = jobProperties.getConcurrencyManagement().getConcurrent();
	}

	private void fillAlarmPreferenceTab() {
		if (jobProperties.getAlarmPreference() != null && jobProperties.getAlarmPreference().getAlarmIdArray() != null && jobProperties.getAlarmPreference().getAlarmIdArray().length > 0) {

			int length = jobProperties.getAlarmPreference().getAlarmIdArray().length;
			selectedAlarmList = new String[length];

			for (int i = 0; i < length; i++) {
				selectedAlarmList[i] = jobProperties.getAlarmPreference().getAlarmIdArray(i) + "";
			}
		}
	}

	private void fillLocalParametersTab() {
		if (jobProperties.getLocalParameters() != null && jobProperties.getLocalParameters().getInParam() != null) {
			InParam inParam = jobProperties.getLocalParameters().getInParam();

			for (Parameter parameter : inParam.getParameterArray()) {
				parameterList.add(parameter);
			}
		} else {
			parameterList = new ArrayList<Parameter>();
		}
	}

	private void fillAdvancedJobInfosTab() {
		if (jobProperties.getAdvancedJobInfos() == null) {
			return;
		}

		AdvancedJobInfos advancedJobInfos = jobProperties.getAdvancedJobInfos();

		// sla tanimi
		if (advancedJobInfos.getSLAId() > 0) {
			jobSLA = advancedJobInfos.getSLAId() + "";
		}

		// agent secme metodu
		if (advancedJobInfos.getAgentChoiceMethod() != null) {
			agentChoiceMethod = advancedJobInfos.getAgentChoiceMethod().getStringValue();

			if (agentChoiceMethod.equals(ChoiceType.USER_MANDATORY_PREFERENCE.toString())) {
				selectedAgent = advancedJobInfos.getAgentChoiceMethod().getAgentId();
			}
		}

		// kaynak gereksinimi tanimi
		if (advancedJobInfos.getResourceRequirement() != null) {
			useResourceReq = true;

			Hardware hardware = advancedJobInfos.getResourceRequirement().getHardware();

			if (hardware.getEntryName() != null) {
				selectedResourceForHardware = hardware.getEntryName();
			}

			Cpu cpu = hardware.getCpu();
			cpuTimein = cpu.getTimein().toString();
			cpuCondition = cpu.getCondition().toString();
			cpuValue = cpu.getStringValue();
			cpuUnit = cpu.getBirim().toString();

			Mem mem = hardware.getMem();
			memoryPart = mem.getFor().toString();
			memoryCondition = mem.getCondition().toString();
			memoryValue = mem.getStringValue();
			memoryUnit = mem.getBirim().toString();

			Disk disk = hardware.getDisk();
			diskPart = disk.getFor().toString();
			diskCondition = disk.getCondition().toString();
			diskValue = disk.getStringValue();
			diskUnit = disk.getBirim().toString();
		}
	}

	public void initScenarioPanel() {
		fillAllLists();

		scenario = Scenario.Factory.newInstance();

		BaseScenarioInfos baseScenarioInfos = BaseScenarioInfos.Factory.newInstance();
		scenario.setBaseScenarioInfos(baseScenarioInfos);

		JobList jobList = JobList.Factory.newInstance();
		scenario.setJobList(jobList);

		TimeManagement timeManagement = TimeManagement.Factory.newInstance();
		JsTimeOut jobTimeOut = JsTimeOut.Factory.newInstance();
		timeManagement.setJsTimeOut(jobTimeOut);
		scenario.setTimeManagement(timeManagement);

		ConcurrencyManagement concurrencyManagement = ConcurrencyManagement.Factory.newInstance();
		scenario.setConcurrencyManagement(concurrencyManagement);

		resetPanelInputs();
	}

	public void initJobPanel() {
		fillAllLists();

		jobProperties = JobProperties.Factory.newInstance();

		BaseJobInfos baseJobInfos = BaseJobInfos.Factory.newInstance();
		JobInfos jobInfos = JobInfos.Factory.newInstance();
		JobTypeDetails jobTypeDetails = JobTypeDetails.Factory.newInstance();
		jobInfos.setJobTypeDetails(jobTypeDetails);
		baseJobInfos.setJobInfos(jobInfos);
		jobProperties.setBaseJobInfos(baseJobInfos);

		TimeManagement timeManagement = TimeManagement.Factory.newInstance();
		JsTimeOut jobTimeOut = JsTimeOut.Factory.newInstance();
		timeManagement.setJsTimeOut(jobTimeOut);
		jobProperties.setTimeManagement(timeManagement);

		CascadingConditions cascadingConditions = CascadingConditions.Factory.newInstance();
		jobProperties.setCascadingConditions(cascadingConditions);

		ConcurrencyManagement concurrencyManagement = ConcurrencyManagement.Factory.newInstance();
		jobProperties.setConcurrencyManagement(concurrencyManagement);

		resetPanelInputs();
	}

	private void resetPanelInputs() {
		jobCalendar = "0";
		oSystem = OSystem.WINDOWS.toString();
		jobPriority = "1";
		jobBaseType = JobBaseType.NON_PERIODIC.toString();
		periodTime = "";
		jobTypeDef = JobTypeDef.TIME_BASED.toString();
		eventTypeDef = EventTypeDef.FILE.toString();

		startTime = "";
		defineStopTime = false;
		stopTime = "";
		gmt = 0;
		dst = false;
		relativeTimeOption = JsRelativeTimeOption.NO.toString();
		jobTimeOutValue = "";
		jobTimeOutUnit = Unit.HOURS.toString();
		expectedTime = "";
		expectedTimeUnit = Unit.HOURS.toString();
		tolerancePercentage = "10";
		minPercentage = "10";

		jobStatus = Status.Factory.newInstance();
		returnCode = ReturnCode.Factory.newInstance();

		agentChoiceMethod = AgentChoiceMethod.SIMPLE_METASCHEDULER.toString();
		jobSLA = NONE;
		useResourceReq = false;
		cpuValue = "0";
		cpuUnit = "%";
		memoryValue = "0";
		diskValue = "0";

		dependencyItem = Item.Factory.newInstance();
		dependencyItem.setJsDependencyRule(JsDependencyRule.Factory.newInstance());

		manyJobDependencyList = new ArrayList<SelectItem>();
		dependencyExpression = "";
	}

	public void fillAllLists() {
		fillOSystemList();
		fillJobBaseTypeList();
		fillEventTypeDefList();
		fillJobTypeDefList();
		fillAgentChoiceMethodList();
		fillRelativeTimeOptionList();
		fillUnitTypeList();
		fillJobStatusList();
		fillJobStateList();
		fillJobSubtateList();

		setJsCalendarList(WebInputUtils.fillCalendarList(getDbOperations().getCalendars()));
		setAlarmList(WebInputUtils.fillAlarmList(getDbOperations().getAlarms()));
		setDefinedAgentList(WebInputUtils.fillAgentList(getDbOperations().getAgents()));
		setJsSLAList(WebInputUtils.fillSLAList(getDbOperations().getSlaList()));
		setResourceNameList(WebInputUtils.fillResourceNameList(getDbOperations().getResources()));
	}

	// ekrandan girilen degerler jobProperties icine dolduruluyor
	public void fillJobProperties() {
		fillBaseJobInfos();
		fillTimeManagement();
		fillDependencyDefinitions();
		fillCascadingConditions();
		fillStateInfos();
		fillConcurrencyManagement();
		fillAlarmPreference();
		fillLocalParameters();
		fillAdvancedJobInfos();
	}

	private void fillBaseJobInfos() {
		BaseJobInfos baseJobInfos = jobProperties.getBaseJobInfos();

		baseJobInfos.setCalendarId(Integer.parseInt(jobCalendar));
		baseJobInfos.setOSystem(OSystem.Enum.forString(oSystem));
		baseJobInfos.setJobPriority(JobPriority.Enum.forString(jobPriority));

		if (jsActive) {
			baseJobInfos.setJsIsActive(JsIsActive.YES);
		} else {
			baseJobInfos.setJsIsActive(JsIsActive.NO);
		}

		JobInfos jobInfos = baseJobInfos.getJobInfos();
		jobInfos.setJobBaseType(JobBaseType.Enum.forString(jobBaseType));

		// periyodik is ise onunla ilgili alanlari dolduruyor
		if (jobBaseType.equals(JobBaseType.PERIODIC.toString())) {
			SpecialParameters specialParameters = SpecialParameters.Factory.newInstance();

			InParam inParam = InParam.Factory.newInstance();

			Parameter parameter = Parameter.Factory.newInstance();
			parameter.setName(PERIOD_TIME_PARAM);
			parameter.setValueTime(DefinitionUtils.dateToXmlTime(periodTime));
			parameter.setId(new BigInteger("1"));

			inParam.addNewParameter();
			inParam.setParameterArray(0, parameter);

			specialParameters.setInParam(inParam);

			jobInfos.getJobTypeDetails().setSpecialParameters(specialParameters);
		}

		jobInfos.setJobTypeDef(JobTypeDef.Enum.forString(jobTypeDef));

		// event tabanli bir is ise event turunu set ediyor
		if (jobTypeDef.equals(JobTypeDef.EVENT_BASED.toString())) {
			jobInfos.getJobTypeDetails().setEventTypeDef(EventTypeDef.Enum.forString(eventTypeDef));
		}

		// TODO login ekrani olmadigi icin simdilik 1 id'li kullaniciyi
		// ayarladim
		baseJobInfos.setUserId(1);
	}

	protected void fillTimeManagement() {
		TimeManagement timeManagement;

		if (isScenario) {
			timeManagement = scenario.getTimeManagement();
		} else {
			timeManagement = jobProperties.getTimeManagement();
		}

		// ekrandan starttime girildiyse onu set ediyor
		if (startTime != null && !startTime.equals("")) {

			if (timeManagement.getJsPlannedTime() == null) {
				JsPlannedTime jsPlannedTime = JsPlannedTime.Factory.newInstance();
				StartTime startTime = StartTime.Factory.newInstance();
				jsPlannedTime.setStartTime(startTime);
				timeManagement.setJsPlannedTime(jsPlannedTime);
			}

			JsPlannedTime jsPlannedTime = timeManagement.getJsPlannedTime();
			jsPlannedTime.getStartTime().setTime(DefinitionUtils.dateToXmlTime(startTime, gmt, dst));

			// ekrandan stoptime girildiyse onu set ediyor, bunu starttime
			// girildiyse kontrol ediyor cunku start time olmadan stop time
			// tanimi yapilmiyor
			if (defineStopTime) {
				StopTime jsStopTime = StopTime.Factory.newInstance();
				jsStopTime.setTime(DefinitionUtils.dateToXmlTime(stopTime, gmt, dst));

				jsPlannedTime.setStopTime(jsStopTime);

			} else if (jsPlannedTime.getStopTime() != null) {
				XmlCursor xmlCursor = jsPlannedTime.getStopTime().newCursor();
				xmlCursor.removeXml();
			}

		} else if (timeManagement.getJsPlannedTime() != null) {
			XmlCursor xmlCursor = timeManagement.getJsPlannedTime().newCursor();
			xmlCursor.removeXml();
		}

		timeManagement.setJsRelativeTimeOption(JsRelativeTimeOption.Enum.forString(relativeTimeOption));

		if (jobTimeOutValue != null && !jobTimeOutValue.equals("")) {
			timeManagement.getJsTimeOut().setValueInteger(new BigInteger(jobTimeOutValue));
			timeManagement.getJsTimeOut().setUnit(Unit.Enum.forString(jobTimeOutUnit));
		}

		if (tolerancePercentage != null && !tolerancePercentage.equals("")) {
			timeManagement.setTolerancePercentage(Integer.parseInt(tolerancePercentage));
		}

		if (minPercentage != null && !minPercentage.equals("")) {
			timeManagement.setMinPercentage(Integer.parseInt(minPercentage));
		}

		// expectedTime girildiyse ilgili alanlar set ediliyor
		if (expectedTime != null && !expectedTime.equals("")) {
			ExpectedTime jsExpectedTime = ExpectedTime.Factory.newInstance();
			jsExpectedTime.setValueInteger(new BigInteger(expectedTime));
			jsExpectedTime.setUnit(Unit.Enum.forString(expectedTimeUnit));

			timeManagement.setExpectedTime(jsExpectedTime);

		} else if (timeManagement.getExpectedTime() != null) {
			XmlCursor xmlCursor = timeManagement.getExpectedTime().newCursor();
			xmlCursor.removeXml();
		}
	}

	private void fillDependencyDefinitions() {
		// periyodik olmayan isler icin bagimlilik tanimlarina bakiyor, son
		// durumda bagimlik tanimlanmamissa jobproperties icindeki ilgili kismi
		// kaldiriyor
		if (jobProperties.getBaseJobInfos().getJobInfos().getJobBaseType().equals(JobBaseType.NON_PERIODIC)) {

			if (jobProperties.getDependencyList() != null && jobProperties.getDependencyList().getItemArray().length == 0) {
				XmlCursor xmlCursor = jobProperties.getDependencyList().newCursor();
				xmlCursor.removeXml();
			}
		}
	}

	private void fillCascadingConditions() {
		if (runEvenIfFailed) {
			jobProperties.getCascadingConditions().setRunEvenIfFailed(RunEvenIfFailed.YES);
		} else {
			jobProperties.getCascadingConditions().setRunEvenIfFailed(RunEvenIfFailed.NO);
		}

		if (jobSafeToRestart) {
			jobProperties.getCascadingConditions().setJobSafeToRestart(JobSafeToRestart.YES);
		} else {
			jobProperties.getCascadingConditions().setJobSafeToRestart(JobSafeToRestart.NO);
		}

		if (jobAutoRetry) {
			jobProperties.getCascadingConditions().setJobAutoRetry(JobAutoRetry.YES);
		} else {
			jobProperties.getCascadingConditions().setJobAutoRetry(JobAutoRetry.NO);
		}
	}

	private void fillStateInfos() {
		if (jobProperties.getStateInfos() == null) {
			StateInfos stateInfos = StateInfos.Factory.newInstance();
			jobProperties.setStateInfos(stateInfos);
		} else {
			// son durumda statu kodu tanimlanmamissa jobproperties icindeki
			// ilgili kismi kaldiriyor
			if (jobProperties.getStateInfos().getJobStatusList() != null && jobProperties.getStateInfos().getJobStatusList().sizeOfJobStatusArray() == 0) {
				XmlCursor xmlCursor = jobProperties.getStateInfos().getJobStatusList().newCursor();
				xmlCursor.removeXml();
			}
		}

		if (jobInsertButton) {
			LiveStateInfos liveStateInfos = LiveStateInfos.Factory.newInstance();
			jobProperties.getStateInfos().setLiveStateInfos(liveStateInfos);

			// ilk live state bilgisini burada ekliyor
			LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_PENDING, SubstateName.INT_CREATED);

		} else {
			if (stateName != null && !stateName.equals("") && subStateName != null && !subStateName.equals("")) {
				int stateIntValue = StateName.Enum.forString(stateName).intValue();
				int substateIntValue = SubstateName.Enum.forString(subStateName).intValue();

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, stateIntValue, substateIntValue);
			}
		}
	}

	protected void fillConcurrencyManagement() {
		if (isScenario) {
			scenario.getConcurrencyManagement().setConcurrent(concurrent);
		} else {
			jobProperties.getConcurrencyManagement().setConcurrent(concurrent);
		}
	}

	protected void fillAlarmPreference() {
		if (selectedAlarmList != null && selectedAlarmList.length > 0) {
			AlarmPreference alarmPreference = AlarmPreference.Factory.newInstance();

			for (int i = 0; i < selectedAlarmList.length; i++) {
				String selectedId = selectedAlarmList[i].toString();

				Iterator<SelectItem> alarmIterator = alarmList.iterator();

				while (alarmIterator.hasNext()) {
					SelectItem alarm = alarmIterator.next();

					if (alarm.getValue().equals(selectedId)) {
						alarmPreference.addNewAlarmId();
						alarmPreference.setAlarmIdArray(alarmPreference.sizeOfAlarmIdArray() - 1, Integer.parseInt(selectedId));
					}
				}
			}

			if (isScenario) {
				scenario.setAlarmPreference(alarmPreference);
			} else {
				jobProperties.setAlarmPreference(alarmPreference);
			}
		} else {
			if (isScenario) {
				if (scenario.getAlarmPreference() != null) {
					XmlCursor xmlCursor = scenario.getAlarmPreference().newCursor();
					xmlCursor.removeXml();
				}
			} else if (jobProperties.getAlarmPreference() != null) {
				XmlCursor xmlCursor = jobProperties.getAlarmPreference().newCursor();
				xmlCursor.removeXml();
			}
		}
	}

	protected void fillLocalParameters() {
		if (parameterList.size() > 0) {
			LocalParameters localParameters = LocalParameters.Factory.newInstance();

			InParam inParam = InParam.Factory.newInstance();
			localParameters.setInParam(inParam);

			for (int i = 0; i < parameterList.size(); i++) {
				Parameter parameter = localParameters.getInParam().addNewParameter();
				parameter.set(parameterList.get(i));
			}

			if (isScenario) {
				scenario.setLocalParameters(localParameters);
			} else {
				jobProperties.setLocalParameters(localParameters);
			}

		} else {
			if (isScenario) {
				if (scenario.getLocalParameters() != null) {
					XmlCursor xmlCursor = scenario.getLocalParameters().newCursor();
					xmlCursor.removeXml();
				}
			} else if (jobProperties.getLocalParameters() != null) {
				XmlCursor xmlCursor = jobProperties.getLocalParameters().newCursor();
				xmlCursor.removeXml();
			}
		}
	}

	private void fillAdvancedJobInfos() {
		AdvancedJobInfos advancedJobInfos = AdvancedJobInfos.Factory.newInstance();

		// sla tanimi
		if (!jobSLA.equals(NONE)) {
			advancedJobInfos.setSLAId(Integer.valueOf(jobSLA));
		}

		AgentChoiceMethod choiceMethod = AgentChoiceMethod.Factory.newInstance();
		choiceMethod.setStringValue(agentChoiceMethod);

		if (agentChoiceMethod.equals(ChoiceType.USER_MANDATORY_PREFERENCE.toString())) {
			choiceMethod.setAgentId(selectedAgent);
		}
		advancedJobInfos.setAgentChoiceMethod(choiceMethod);

		// kaynak gereksinimi tanimi
		if (useResourceReq) {
			ResourceRequirement resourceRequirement;

			if (advancedJobInfos.getResourceRequirement() == null) {
				resourceRequirement = ResourceRequirement.Factory.newInstance();
			} else {
				resourceRequirement = advancedJobInfos.getResourceRequirement();
			}

			Hardware hardware;

			if (resourceRequirement.getHardware() == null) {
				hardware = Hardware.Factory.newInstance();
			} else {
				hardware = resourceRequirement.getHardware();
			}

			if (resourceBasedDef) {
				hardware.setEntryName(selectedResourceForHardware);
			}

			Cpu cpu = Cpu.Factory.newInstance();
			cpu.setTimein(Timein.Enum.forString(cpuTimein));
			cpu.setCondition(Condition.Enum.forString(cpuCondition));
			cpu.setStringValue(cpuValue);
			cpu.setBirim(Birim.Enum.forString(cpuUnit));

			Mem mem = Mem.Factory.newInstance();
			mem.setFor(For.Enum.forString(memoryPart));
			mem.setCondition(Condition.Enum.forString(memoryCondition));
			mem.setStringValue(memoryValue);
			mem.setBirim(Birim.Enum.forString(memoryUnit));

			Disk disk = Disk.Factory.newInstance();
			disk.setFor(For.Enum.forString(diskPart));
			disk.setCondition(Condition.Enum.forString(diskCondition));
			disk.setStringValue(diskValue);
			disk.setBirim(Birim.Enum.forString(diskUnit));

			hardware.setCpu(cpu);
			hardware.setMem(mem);
			hardware.setDisk(disk);

			resourceRequirement.setHardware(hardware);
			advancedJobInfos.setResourceRequirement(resourceRequirement);
		}

		jobProperties.setAdvancedJobInfos(advancedJobInfos);
	}

	public void insertJobDefinition() {
		if (!jobCheckUp() & getJobId()) {
			return;
		}

		if (getDbOperations().insertJob(JSDefinitionMBean.JOB_DEFINITION_DATA, getJobPropertiesXML(), getTreePath(jobPathInScenario))) {
			// senaryoya yeni dugumu ekliyor
			jSTree.addJobNodeToScenarioPath(jobProperties, jobPathInScenario);

			addMessage("jobInsert", FacesMessage.SEVERITY_INFO, "tlos.success.job.insert", null);
		} else {
			// surukle birak ile getirilen isin agactan kalidrilmasi icin agaci
			// guncelliyor
			RequestContext context = RequestContext.getCurrentInstance();
			context.update("jsTreeForm:tree");

			addMessage("jobInsert", FacesMessage.SEVERITY_ERROR, "tlos.error.job.insert", null);
		}

		jsActiveDialogShow = false;
	}

	private boolean getJobId() {
		int jobId = getDbOperations().getNextId(ConstantDefinitions.JOB_ID);

		if (jobId < 0) {
			addMessage("jobInsert", FacesMessage.SEVERITY_ERROR, "tlos.error.job.getId", null);
			return false;
		}

		jobProperties.setID(jobId + "");
		return true;
	}

	private String getTreePath(String treePath) {
		String path = "/dat:TlosProcessData";

		StringTokenizer pathTokenizer = new StringTokenizer(treePath, "/");

		// ilk gelen isim senaryo agacinin koku oldugu icin onu cikariyoruz
		if (pathTokenizer.hasMoreTokens()) {
			pathTokenizer.nextToken();
		}

		while (pathTokenizer.hasMoreTokens()) {
			String scenarioName = pathTokenizer.nextToken();

			if (scenarioName.contains("|")) {
				scenarioName = removeIdFromName(scenarioName);
			}

			path = path + "/dat:scenario/dat:baseScenarioInfos[com:jsName = '" + scenarioName + "']/..";
		}

		path = path + "/dat:jobList";

		return path;
	}

	private String getDependencyTreePath(String treePath) {
		String path = "";

		StringTokenizer pathTokenizer = new StringTokenizer(treePath, "/");

		while (pathTokenizer.hasMoreTokens()) {
			String scenarioName = pathTokenizer.nextToken();

			StringTokenizer nameTokenizer = new StringTokenizer(scenarioName, "|");
			scenarioName = nameTokenizer.nextToken().trim();
			String scenarioId = nameTokenizer.nextToken().trim();

			if (path.equals("")) {
				path = scenarioId;
			} else {
				path += "." + scenarioId;
			}
		}

		if (path.equals("")) {
			path = SERBEST;
		}

		return path;
	}

	public String removeIdFromName(String nameAndId) {
		StringTokenizer nameTokenizer = new StringTokenizer(nameAndId, "|");
		String name = nameTokenizer.nextToken().trim();

		return name;
	}

	private boolean jobCheckUp() {
		JobProperties job = getDbOperations().getJob(JSDefinitionMBean.JOB_DEFINITION_DATA, getTreePath(jobPathInScenario), jobProperties.getBaseJobInfos().getJsName());

		// ayni isimli iki is kaydedilmiyor
		if (job != null && job.getBaseJobInfos().getJsName().equals(jobProperties.getBaseJobInfos().getJsName()) && !job.getID().equals(jobProperties.getID())) {
			addMessage("jobInsert", FacesMessage.SEVERITY_ERROR, "tlos.info.job.name.duplicate", null);
			return false;
		}

		return true;
	}

	public void dependencyDropAction() {
		if (!checkDependencyValidation()) {
			return;
		}

		dependencyItem = Item.Factory.newInstance();
		dependencyItem.setJsDependencyRule(JsDependencyRule.Factory.newInstance());
		dependencyItem.setJsName(removeIdFromName(draggedJobName));

		depStateName = "";
		depSubstateName = "";
		depStatusName = "";

		dependencyInsertButton = true;
		dependencyDialogShow = true;
	}

	private boolean checkDependencyValidation() {
		JobProperties draggedJobProperties = getDbOperations().getJob(JSDefinitionMBean.JOB_DEFINITION_DATA, getTreePath(draggedJobPath), removeIdFromName(draggedJobName));

		if (jobProperties.getBaseJobInfos().getCalendarId() != draggedJobProperties.getBaseJobInfos().getCalendarId()) {
			addMessage("addDependency", FacesMessage.SEVERITY_ERROR, "tlos.info.job.dependency.calendar", null);
			return false;
		}

		return true;
	}

	public void saveDependencyAction() {
		if (!checkDependencyPopupValidation()) {
			return;
		}

		dependencyItem.setJsType(JsType.JOB);

		dependencyTreePath = getDependencyTreePath(draggedJobPath);

		dependencyItem.setJsPath(dependencyTreePath);

		dependencyItem.getJsDependencyRule().setStateName(StateName.Enum.forString(depStateName));

		if (dependencyType.equals(SUBSTATE) || dependencyType.equals(STATUS)) {
			dependencyItem.getJsDependencyRule().setSubstateName(SubstateName.Enum.forString(depSubstateName));
		}

		if (dependencyType.equals(STATUS)) {
			dependencyItem.getJsDependencyRule().setStatusName(StatusName.Enum.forString(depStatusName));
		}

		if (jobProperties.getDependencyList() == null || jobProperties.getDependencyList().sizeOfItemArray() == 0) {
			jobProperties.setDependencyList(DependencyList.Factory.newInstance());
		}

		Item newItem = jobProperties.getDependencyList().addNewItem();
		newItem.set(dependencyItem);

		composeDependencyExpression();

		String depJobName = removeIdFromName(draggedJobName);

		SelectItem item = new SelectItem();
		item.setLabel(depJobName);
		item.setValue(dependencyTreePath + "." + depJobName);

		manyJobDependencyList.add(item);

		dependencyDialogShow = false;
	}

	private void composeDependencyExpression() {
		String depExpression = "";

		for (Item item : jobProperties.getDependencyList().getItemArray()) {
			if (depExpression.equals("")) {
				depExpression = item.getDependencyID().toUpperCase(Locale.ENGLISH);
			} else {
				depExpression = depExpression + " AND " + item.getDependencyID().toUpperCase(Locale.ENGLISH);
			}
		}
		jobProperties.getDependencyList().setDependencyExpression(depExpression);
		dependencyExpression = depExpression;
	}

	private boolean checkDependencyPopupValidation() {
		boolean validationValue = true;

		if (dependencyItem.getDependencyID().equals("")) {
			addMessage("addDependency", FacesMessage.SEVERITY_ERROR, "tlos.validation.job.dependency.id", null);
			validationValue = false;
		}

		if (dependencyItem.getComment().equals("")) {
			addMessage("addDependency", FacesMessage.SEVERITY_ERROR, "tlos.validation.job.dep.comment", null);
			validationValue = false;
		}

		if (depStateName == null || depStateName.equals("")) {
			addMessage("addDependency", FacesMessage.SEVERITY_ERROR, "tlos.validation.job.state.choose", null);
			validationValue = false;
		}

		if (dependencyType.equals(SUBSTATE) || dependencyType.equals(STATUS)) {
			if (depSubstateName == null || depSubstateName.equals("")) {
				addMessage("addDependency", FacesMessage.SEVERITY_ERROR, "tlos.validation.job.subState.choose", null);
				validationValue = false;
			}
		}

		if (dependencyType.equals(STATUS)) {
			if (depStatusName == null || depStatusName.equals("")) {
				addMessage("addDependency", FacesMessage.SEVERITY_ERROR, "tlos.validation.job.status.choose", null);
				validationValue = false;
			}
		}

		return validationValue;
	}

	public void updateDependencyAction() {
		if (!checkDependencyPopupValidation()) {
			return;
		}

		dependencyItem.setJsDependencyRule(JsDependencyRule.Factory.newInstance());

		if (dependencyType.equals(STATE)) {
			dependencyItem.getJsDependencyRule().setStateName(StateName.Enum.forString(depStateName));
		} else if (dependencyType.equals(SUBSTATE)) {
			dependencyItem.getJsDependencyRule().setStateName(StateName.Enum.forString(depStateName));
			dependencyItem.getJsDependencyRule().setSubstateName(SubstateName.Enum.forString(depSubstateName));
		} else {
			dependencyItem.getJsDependencyRule().setStateName(StateName.Enum.forString(depStateName));
			dependencyItem.getJsDependencyRule().setSubstateName(SubstateName.Enum.forString(depSubstateName));
			dependencyItem.getJsDependencyRule().setStatusName(StatusName.Enum.forString(depStatusName));
		}

		composeDependencyExpression();

		dependencyDialogShow = false;
	}

	public void closeDependencyDialogAction() {
		dependencyDialogShow = false;
	}

	public void deleteJobDependencyAction() {
		if (selectedJobDependencyList == null || selectedJobDependencyList.length == 0) {
			addMessage("deleteDependency", FacesMessage.SEVERITY_ERROR, "tlos.info.job.dependency.select", null);
			return;
		}

		for (int i = 0; i < selectedJobDependencyList.length; i++) {
			for (int j = 0; j < manyJobDependencyList.size(); j++) {
				if (manyJobDependencyList.get(j).getValue().equals(selectedJobDependencyList[i])) {
					manyJobDependencyList.remove(j);
					j = manyJobDependencyList.size();
				}
			}

			for (int k = 0; k < jobProperties.getDependencyList().getItemArray().length; k++) {
				String depPathAndName = jobProperties.getDependencyList().getItemArray(k).getJsPath() + "." + jobProperties.getDependencyList().getItemArray(k).getJsName();

				if (selectedJobDependencyList[i].equals(depPathAndName)) {
					jobProperties.getDependencyList().removeItem(k);
					k = jobProperties.getDependencyList().sizeOfItemArray();
				}
			}
		}
		composeDependencyExpression();
	}

	public void editJobDependencyAction() {
		if (selectedJobDependencyList == null || selectedJobDependencyList.length == 0) {
			addMessage("deleteDependency", FacesMessage.SEVERITY_ERROR, "tlos.info.job.dependency.select", null);
			return;
		} else if (selectedJobDependencyList.length > 1) {
			addMessage("deleteDependency", FacesMessage.SEVERITY_ERROR, "tlos.info.job.dependency.select.one", null);
			return;
		}

		DependencyList dependencyList = jobProperties.getDependencyList();

		for (int i = 0; i < dependencyList.sizeOfItemArray(); i++) {
			String depPathAndName = dependencyList.getItemArray(i).getJsPath() + "." + dependencyList.getItemArray(i).getJsName();

			if (depPathAndName.equals(selectedJobDependencyList[0])) {
				dependencyItem = dependencyList.getItemArray(i);
				break;
			}
		}

		JsDependencyRule jsDependencyRule = dependencyItem.getJsDependencyRule();

		depStateName = jsDependencyRule.getStateName().toString();

		if (jsDependencyRule.getSubstateName() != null) {
			depSubstateName = dependencyItem.getJsDependencyRule().getSubstateName().toString();
			dependencyType = SUBSTATE;

			if (jsDependencyRule.getStatusName() != null) {
				depStatusName = jsDependencyRule.getStatusName().toString();
				dependencyType = STATUS;
			} else {
				depStatusName = "";
			}
		} else {
			dependencyType = STATE;
			depSubstateName = "";
			depStatusName = "";
		}

		dependencyInsertButton = false;
		dependencyDialogShow = true;
	}

	public void addInputParameter() {
		if (paramName == null || paramName.equals("") || paramDesc == null || paramDesc.equals("") || paramPreValue == null || paramPreValue.equals("") || paramType == null || paramType.equals("")) {

			addMessage("addInputParam", FacesMessage.SEVERITY_ERROR, "tlos.workspace.pannel.job.paramValidationError", null);

			return;
		}

		Parameter parameter = Parameter.Factory.newInstance();
		parameter.setName(paramName);
		parameter.setDesc(paramDesc);

		PreValue preValue = PreValue.Factory.newInstance();
		preValue.setStringValue(paramPreValue);
		preValue.setType(new BigInteger(paramType));
		parameter.setPreValue(preValue);

		parameterList.add(parameter);

		resetInputParameterFields();
	}

	private void resetInputParameterFields() {
		paramName = "";
		paramDesc = "";
		paramPreValue = "";
		paramType = "";
	}

	public void deleteInputParamAction(ActionEvent e) {
		int parameterIndex = parameterTable.getRowIndex();
		parameterList.remove(parameterIndex);

		renderUpdateParamButton = false;

		RequestContext context = RequestContext.getCurrentInstance();
		context.update("jobDefinitionForm:tabView:parametersPanel");
	}

	public void editInputParamAction(ActionEvent e) {
		Parameter inParam = (Parameter) parameterTable.getRowData();

		paramName = new String(inParam.getName());
		paramDesc = new String(inParam.getDesc());
		paramPreValue = new String(inParam.getPreValue().getStringValue());
		paramType = new String(inParam.getPreValue().getType().toString());

		selectedParamName = paramName;

		renderUpdateParamButton = true;

		RequestContext context = RequestContext.getCurrentInstance();
		context.update("jobDefinitionForm:tabView:parametersPanel");
	}

	public void updateInputParameter() {
		for (int i = 0; i < parameterList.size(); i++) {

			if (selectedParamName.equals(parameterList.get(i).getName())) {
				parameterList.get(i).setName(paramName);
				parameterList.get(i).setDesc(paramDesc);

				PreValue preValue = PreValue.Factory.newInstance();
				preValue.setStringValue(paramPreValue);
				preValue.setType(new BigInteger(paramType));
				parameterList.get(i).setPreValue(preValue);

				break;
			}
		}

		resetInputParameterFields();

		renderUpdateParamButton = false;
	}

	private boolean checkDuplicateStateName() {
		if (manyJobStatusList != null) {

			for (int i = 0; i < manyJobStatusList.size(); i++) {

				if (manyJobStatusList.get(i).getValue().equals(jobStatusName)) {
					addMessage("addReturnCode", FacesMessage.SEVERITY_ERROR, "tlos.info.job.status.duplicate", null);

					return true;
				}
			}
		}

		return false;
	}

	public void initJobStatusPopup(ActionEvent e) {
		statusDialogShow = !checkDuplicateStateName();

		osType = OSystem.WINDOWS.toString();
		jobStatus = Status.Factory.newInstance();
		returnCode = ReturnCode.Factory.newInstance();
		manyReturnCodeList = new ArrayList<SelectItem>();
	}

	public void addJReturnCodeAction() {
		// TODO donus kodu eklerken ayni is donus statusu icin ayni isletim
		// sistemi secilerek
		// ayni kod birden fazla tanimlanabiliyor
		// bu kontrol yapilip ayni kodun eklenmesi engellenecek

		// guncelleme icin acildiginda duplicate kontrolunu yapmiyor
		if (jobStatus.getStsId() == null || jobStatus.getStsId().equals("")) {
			if (!checkDuplicateStateName()) {
				statusDialogShow = true;
			} else {
				statusDialogShow = false;

				return;
			}
		}

		jobStatus.setStatusName(StatusName.Enum.forString(jobStatusName));

		ReturnCode tmpReturnCode = WebInputUtils.cloneReturnCode(returnCode);

		// girilen statu icin onceden kayit yapilmamissa gerekli bilesenler
		// olusturuluyor
		if (jobStatus.getReturnCodeListArray() == null || jobStatus.sizeOfReturnCodeListArray() == 0) {
			ReturnCodeList returnCodeList = jobStatus.addNewReturnCodeList();
			returnCodeList.setOsType(OsType.Enum.forString(osType));

			tmpReturnCode.setCdId("1");

			ReturnCode returnCode = (jobStatus.getReturnCodeListArray()[0]).addNewReturnCode();
			returnCode.set(tmpReturnCode);

			// girilen statu icin onceden kayit yapilmissa, girilen isletim
			// sistemi icin onceden kayit yapilmis mi diye kontrol ediyor
		} else {
			boolean osIsDefined = false;

			for (int j = 0; j < jobStatus.sizeOfReturnCodeListArray(); j++) {
				ReturnCodeList returnCodeList = jobStatus.getReturnCodeListArray()[j];

				if (returnCodeList.getOsType().toString().toLowerCase().equals(osType.toLowerCase())) {
					int lastElementIndex = returnCodeList.getReturnCodeArray().length - 1;

					String maxId = returnCodeList.getReturnCodeArray()[lastElementIndex].getCdId();

					tmpReturnCode.setCdId((Integer.parseInt(maxId) + 1) + "");

					returnCodeList.setOsType(OsType.Enum.forString(osType));

					ReturnCode returnCode = returnCodeList.addNewReturnCode();
					returnCode.set(tmpReturnCode);

					osIsDefined = true;
				}
			}

			// girilen isletim sistemi tanimlanmamissa gerekli bilesenler
			// olusturuluyor
			if (!osIsDefined) {
				ReturnCodeList returnCodeList = jobStatus.addNewReturnCodeList();
				returnCodeList.setOsType(OsType.Enum.forString(osType));

				tmpReturnCode.setCdId("1");

				ReturnCode returnCode = returnCodeList.addNewReturnCode();
				returnCode.set(tmpReturnCode);
			}

			if (isScenario) {
				// hazirlanan status nesnesi scenariostatusList icine koyuluyor
				for (int i = 0; i < scenarioStatusList.sizeOfScenarioStatusArray(); i++) {
					if (scenarioStatusList.getScenarioStatusArray(i).getStatusName().equals(jobStatus.getStatusName())) {
						scenarioStatusList.getScenarioStatusArray(i).set(jobStatus);
					}
				}
			} else {
				// hazirlanan job status nesnesi jobstatusList icine koyuluyor
				for (int i = 0; i < jobStatusList.sizeOfJobStatusArray(); i++) {
					if (jobStatusList.getJobStatusArray(i).getStatusName().equals(jobStatus.getStatusName())) {
						jobStatusList.getJobStatusArray(i).set(jobStatus);
					}
				}
			}
		}

		if (manyReturnCodeList == null) {
			manyReturnCodeList = new ArrayList<SelectItem>();
		}

		// islem yapilan job icin onceden herhangi bir statu tanimi yapilmis mi
		// diye kontrol ediyor, yapilmamissa job tanimindaki gerekli bilesenleri
		// ekliyor
		if (isScenario) {
			if (scenarioStatusList == null || scenarioStatusList.sizeOfScenarioStatusArray() == 0) {
				scenarioStatusList = ScenarioStatusList.Factory.newInstance();

				Status status = scenarioStatusList.addNewScenarioStatus();
				status.set(jobStatus);
			}
		} else {
			if (jobStatusList == null || jobStatusList.sizeOfJobStatusArray() == 0) {
				jobStatusList = JobStatusList.Factory.newInstance();

				Status status = jobStatusList.addNewJobStatus();
				status.set(jobStatus);
			}
		}

		SelectItem item = new SelectItem();
		item.setValue(tmpReturnCode.getCode());
		item.setLabel(osType + " : " + tmpReturnCode.getCode() + " -> " + jobStatusName);

		manyReturnCodeList.add(item);
	}

	public void saveJobStatusAction() {
		if (manyReturnCodeList == null || manyReturnCodeList.size() == 0) {
			addMessage("addReturnCode", FacesMessage.SEVERITY_ERROR, "tlos.validation.job.codeList", null);

			return;
		}

		Status tmpJobStatus = WebInputUtils.cloneJobStatus(jobStatus);

		if (isScenario) {
			addToScenarioStatusList(tmpJobStatus);
		} else {
			addToJobStatusList(tmpJobStatus);
		}

		if (manyJobStatusList == null) {
			manyJobStatusList = new ArrayList<SelectItem>();
		}

		manyJobStatusList.add(new SelectItem(jobStatusName, jobStatusName));

		addMessage("addReturnCode", FacesMessage.SEVERITY_INFO, "tlos.info.job.code.add", null);

		statusDialogShow = false;
	}

	private void addToJobStatusList(Status tmpJobStatus) {
		JobStatusList jobStatusList = null;

		if (jobProperties.getStateInfos().getJobStatusList() == null || jobProperties.getStateInfos().getJobStatusList().sizeOfJobStatusArray() == 0) {
			jobProperties.getStateInfos().setJobStatusList(JobStatusList.Factory.newInstance());

			jobStatusList = jobProperties.getStateInfos().getJobStatusList();

			tmpJobStatus.setStsId("1");
		} else {
			jobStatusList = jobProperties.getStateInfos().getJobStatusList();

			int lastStatusIndex = jobStatusList.sizeOfJobStatusArray() - 1;
			String id = jobStatusList.getJobStatusArray(lastStatusIndex).getStsId();

			tmpJobStatus.setStsId((Integer.parseInt(id) + 1) + "");
		}

		Status newStatus = jobStatusList.addNewJobStatus();
		newStatus.set(tmpJobStatus);
	}

	private void addToScenarioStatusList(Status tmpJobStatus) {
		ScenarioStatusList scenarioStatusList = null;

		if (scenario.getScenarioStatusList() == null || scenario.getScenarioStatusList().sizeOfScenarioStatusArray() == 0) {
			scenario.setScenarioStatusList(ScenarioStatusList.Factory.newInstance());

			scenarioStatusList = scenario.getScenarioStatusList();

			tmpJobStatus.setStsId("1");
		} else {
			scenarioStatusList = scenario.getScenarioStatusList();

			int lastStatusIndex = scenarioStatusList.sizeOfScenarioStatusArray() - 1;
			String id = scenarioStatusList.getScenarioStatusArray(lastStatusIndex).getStsId();

			tmpJobStatus.setStsId((Integer.parseInt(id) + 1) + "");
		}

		Status newStatus = scenarioStatusList.addNewScenarioStatus();
		newStatus.set(tmpJobStatus);
	}

	public void closeJobStatusDialogAction() {
		statusDialogShow = false;
	}

	public void deleteJReturnCodeAction() {
		if (selectedReturnCodeList.length == 0) {
			addMessage("addReturnCode", FacesMessage.SEVERITY_ERROR, "tlos.info.job.code.delete", null);
			return;
		}

		for (int i = 0; i < selectedReturnCodeList.length; i++) {
			for (int j = 0; j < manyReturnCodeList.size(); j++) {
				if (manyReturnCodeList.get(j).getValue().toString().equals(selectedReturnCodeList[i])) {

					// TODO job tanimi ya da senaryo tanimi icinden silme isi
					// burada yapilacak,
					// asagida sadece goruntu olarak ekrandaki listeden siliyor

					manyReturnCodeList.remove(j);
					j = manyReturnCodeList.size();
				}
			}
		}
	}

	public void deleteJobStatusAction() {
		for (int i = 0; i < selectedJobStatusList.length; i++) {
			for (int j = 0; j < manyJobStatusList.size(); j++) {
				if (manyJobStatusList.get(j).getValue().equals(selectedJobStatusList[i])) {

					JobStatusList jobStatusList = jobProperties.getStateInfos().getJobStatusList();

					for (int k = 0; k < jobStatusList.sizeOfJobStatusArray(); k++) {
						if (manyJobStatusList.get(j).getValue().equals(jobStatusList.getJobStatusArray(k).getStatusName().toString())) {
							jobStatusList.removeJobStatus(k);
							k = jobStatusList.sizeOfJobStatusArray();
						}
					}
					manyJobStatusList.remove(j);
					j = manyJobStatusList.size();
				}
			}
		}
	}

	public void deleteScenarioStatusAction() {
		for (int i = 0; i < selectedJobStatusList.length; i++) {
			for (int j = 0; j < manyJobStatusList.size(); j++) {
				if (manyJobStatusList.get(j).getValue().equals(selectedJobStatusList[i])) {

					ScenarioStatusList scenarioStatusList = scenario.getScenarioStatusList();

					for (int k = 0; k < scenarioStatusList.sizeOfScenarioStatusArray(); k++) {
						if (manyJobStatusList.get(j).getValue().equals(scenarioStatusList.getScenarioStatusArray(k).getStatusName().toString())) {
							scenarioStatusList.removeScenarioStatus(k);
							k = scenarioStatusList.sizeOfScenarioStatusArray();
						}
					}
					manyJobStatusList.remove(j);
					j = manyJobStatusList.size();
				}
			}
		}
	}

	public void jobStatusEditAction() {
		if (selectedJobStatusList == null || selectedJobStatusList.length == 0) {
			addMessage("addReturnCode", FacesMessage.SEVERITY_ERROR, "tlos.info.job.status.choose", null);
			return;
		} else if (selectedJobStatusList.length > 1) {
			addMessage("addReturnCode", FacesMessage.SEVERITY_ERROR, "tlos.info.job.status.choose.one", null);
			return;
		}

		Status[] statusArray = null;

		if (isScenario) {
			statusArray = scenario.getScenarioStatusList().getScenarioStatusArray();
		} else {
			statusArray = jobProperties.getStateInfos().getJobStatusList().getJobStatusArray();
		}

		for (Status jStatus : statusArray) {
			if (jStatus.getStatusName().toString().equals(selectedJobStatusList[0])) {
				jobStatus = WebInputUtils.cloneJobStatus(jStatus);
				jobStatusName = selectedJobStatusList[0];

				break;
			}
		}

		manyReturnCodeList = new ArrayList<SelectItem>();

		for (int i = 0; i < jobStatus.getReturnCodeListArray().length; i++) {
			ReturnCodeList returnCodeList = jobStatus.getReturnCodeListArray(i);

			for (int j = 0; j < returnCodeList.getReturnCodeArray().length; j++) {
				ReturnCode returnCode = returnCodeList.getReturnCodeArray(j);

				SelectItem item = new SelectItem();
				item.setValue(returnCode.getCode());
				item.setLabel(returnCodeList.getOsType().toString() + " : " + returnCode.getCode() + " -> " + jobStatusName);

				manyReturnCodeList.add(item);
			}
		}

		osType = OSystem.WINDOWS.toString();
		returnCode = ReturnCode.Factory.newInstance();

		statusDialogShow = true;
	}

	public void updateJobStatusAction() {
		Status[] statusArray = null;

		if (isScenario) {
			statusArray = scenario.getScenarioStatusList().getScenarioStatusArray();
		} else {
			statusArray = jobProperties.getStateInfos().getJobStatusList().getJobStatusArray();
		}

		for (Status jStatus : statusArray) {
			if (jobStatus.getStatusName().toString().equals(jStatus.getStatusName().toString())) {
				jStatus = WebInputUtils.cloneJobStatus(jobStatus);

				addMessage("addReturnCode", FacesMessage.SEVERITY_INFO, "tlos.info.job.code.update", null);

				break;
			}
		}

		statusDialogShow = false;
	}

	public void openJSDialogAction(ActionEvent e) {
		jsActiveDialogShow = true;
	}

	public String getJobPropertiesXML() {
		QName qName = JobProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String jobPropertiesXML = jobProperties.xmlText(xmlOptions);

		return jobPropertiesXML;
	}

	public void fillAgentChoiceMethodList() {
		if (getAgentChoiceMethodList() == null) {
			setAgentChoiceMethodList(WebInputUtils.fillAgentChoiceMethodList());
		}
	}

	public void fillJobTypeDefList() {
		if (getJobTypeDefList() == null) {
			setJobTypeDefList(WebInputUtils.fillJobTypeDefList());
		}
	}

	public void fillJobBaseTypeList() {
		if (getJobBaseTypeList() == null) {
			setJobBaseTypeList(WebInputUtils.fillJobBaseTypeList());
		}
	}

	public void fillEventTypeDefList() {
		if (getEventTypeDefList() == null) {
			setEventTypeDefList(WebInputUtils.fillEventTypeDefList());
		}
	}

	public void fillOSystemList() {
		if (getoSystemList() == null) {
			setoSystemList(WebInputUtils.fillOSystemList());
		}
	}

	public void fillRelativeTimeOptionList() {
		if (getRelativeTimeOptionList() == null) {
			setRelativeTimeOptionList(WebInputUtils.fillRelativeTimeOptionList());
		}
	}

	public void fillUnitTypeList() {
		if (getUnitTypeList() == null) {
			setUnitTypeList(WebInputUtils.fillUnitTypeList());
		}
	}

	public void fillJobStateList() {
		if (getDepStateNameList() == null) {
			setDepStateNameList(WebInputUtils.fillJobStateList());
		}
	}

	public void fillJobSubtateList() {
		if (getDepSubstateNameList() == null) {
			setDepSubstateNameList(WebInputUtils.fillJobSubstateList());
		}
	}

	public void fillJobStatusList() {
		if (getJobStatusNameList() == null) {
			setJobStatusNameList(WebInputUtils.fillJobStatusList());
		}
	}

	public JobProperties getJobProperties() {
		return jobProperties;
	}

	public void setJobProperties(JobProperties jobProperties) {
		this.jobProperties = jobProperties;
	}

	public boolean isJobInsertButton() {
		return jobInsertButton;
	}

	public void setJobInsertButton(boolean jobInsertButton) {
		this.jobInsertButton = jobInsertButton;
	}

	public Collection<SelectItem> getJsCalendarList() {
		return jsCalendarList;
	}

	public void setJsCalendarList(Collection<SelectItem> jsCalendarList) {
		this.jsCalendarList = jsCalendarList;
	}

	public String getJobCalendar() {
		return jobCalendar;
	}

	public void setJobCalendar(String jobCalendar) {
		this.jobCalendar = jobCalendar;
	}

	public Collection<SelectItem> getoSystemList() {
		return oSystemList;
	}

	public void setoSystemList(Collection<SelectItem> oSystemList) {
		this.oSystemList = oSystemList;
	}

	public String getoSystem() {
		return oSystem;
	}

	public void setoSystem(String oSystem) {
		this.oSystem = oSystem;
	}

	public String getJobPriority() {
		return jobPriority;
	}

	public void setJobPriority(String jobPriority) {
		this.jobPriority = jobPriority;
	}

	public Collection<SelectItem> getJobBaseTypeList() {
		return jobBaseTypeList;
	}

	public void setJobBaseTypeList(Collection<SelectItem> jobBaseTypeList) {
		this.jobBaseTypeList = jobBaseTypeList;
	}

	public String getJobBaseType() {
		return jobBaseType;
	}

	public void setJobBaseType(String jobBaseType) {
		this.jobBaseType = jobBaseType;
	}

	public Collection<SelectItem> getJobTypeDefList() {
		return jobTypeDefList;
	}

	public void setJobTypeDefList(Collection<SelectItem> jobTypeDefList) {
		this.jobTypeDefList = jobTypeDefList;
	}

	public String getJobTypeDef() {
		return jobTypeDef;
	}

	public void setJobTypeDef(String jobTypeDef) {
		this.jobTypeDef = jobTypeDef;
	}

	public Collection<SelectItem> getEventTypeDefList() {
		return eventTypeDefList;
	}

	public void setEventTypeDefList(Collection<SelectItem> eventTypeDefList) {
		this.eventTypeDefList = eventTypeDefList;
	}

	public String getEventTypeDef() {
		return eventTypeDef;
	}

	public void setEventTypeDef(String eventTypeDef) {
		this.eventTypeDef = eventTypeDef;
	}

	public Collection<SelectItem> getJobCommandTypeList() {
		return jobCommandTypeList;
	}

	public void setJobCommandTypeList(Collection<SelectItem> jobCommandTypeList) {
		this.jobCommandTypeList = jobCommandTypeList;
	}

	public String getJobCommandType() {
		return jobCommandType;
	}

	public void setJobCommandType(String jobCommandType) {
		this.jobCommandType = jobCommandType;
	}

	public String getPeriodTime() {
		return periodTime;
	}

	public void setPeriodTime(String periodTime) {
		this.periodTime = periodTime;
	}

	public Collection<SelectItem> getAlarmList() {
		return alarmList;
	}

	public void setAlarmList(Collection<SelectItem> alarmList) {
		this.alarmList = alarmList;
	}

	public String[] getSelectedAlarmList() {
		return selectedAlarmList;
	}

	public void setSelectedAlarmList(String[] selectedAlarmList) {
		this.selectedAlarmList = selectedAlarmList;
	}

	public Collection<SelectItem> getAgentChoiceMethodList() {
		return agentChoiceMethodList;
	}

	public void setAgentChoiceMethodList(Collection<SelectItem> agentChoiceMethodList) {
		this.agentChoiceMethodList = agentChoiceMethodList;
	}

	public String getAgentChoiceMethod() {
		return agentChoiceMethod;
	}

	public void setAgentChoiceMethod(String agentChoiceMethod) {
		this.agentChoiceMethod = agentChoiceMethod;
	}

	public Collection<SelectItem> getDefinedAgentList() {
		return definedAgentList;
	}

	public void setDefinedAgentList(Collection<SelectItem> definedAgentList) {
		this.definedAgentList = definedAgentList;
	}

	public String getSelectedAgent() {
		return selectedAgent;
	}

	public void setSelectedAgent(String selectedAgent) {
		this.selectedAgent = selectedAgent;
	}

	public String getJobSLA() {
		return jobSLA;
	}

	public void setJobSLA(String jobSLA) {
		this.jobSLA = jobSLA;
	}

	public Collection<SelectItem> getJsSLAList() {
		return jsSLAList;
	}

	public void setJsSLAList(Collection<SelectItem> jsSLAList) {
		this.jsSLAList = jsSLAList;
	}

	public boolean isDefineStopTime() {
		return defineStopTime;
	}

	public void setDefineStopTime(boolean defineStopTime) {
		this.defineStopTime = defineStopTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStopTime() {
		return stopTime;
	}

	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}

	public Collection<SelectItem> getRelativeTimeOptionList() {
		return relativeTimeOptionList;
	}

	public void setRelativeTimeOptionList(Collection<SelectItem> relativeTimeOptionList) {
		this.relativeTimeOptionList = relativeTimeOptionList;
	}

	public String getRelativeTimeOption() {
		return relativeTimeOption;
	}

	public void setRelativeTimeOption(String relativeTimeOption) {
		this.relativeTimeOption = relativeTimeOption;
	}

	public String getJobTimeOutValue() {
		return jobTimeOutValue;
	}

	public void setJobTimeOutValue(String jobTimeOutValue) {
		this.jobTimeOutValue = jobTimeOutValue;
	}

	public Collection<SelectItem> getUnitTypeList() {
		return unitTypeList;
	}

	public void setUnitTypeList(Collection<SelectItem> unitTypeList) {
		this.unitTypeList = unitTypeList;
	}

	public String getJobTimeOutUnit() {
		return jobTimeOutUnit;
	}

	public void setJobTimeOutUnit(String jobTimeOutUnit) {
		this.jobTimeOutUnit = jobTimeOutUnit;
	}

	public String getExpectedTime() {
		return expectedTime;
	}

	public void setExpectedTime(String expectedTime) {
		this.expectedTime = expectedTime;
	}

	public String getExpectedTimeUnit() {
		return expectedTimeUnit;
	}

	public void setExpectedTimeUnit(String expectedTimeUnit) {
		this.expectedTimeUnit = expectedTimeUnit;
	}

	public String getTolerancePercentage() {
		return tolerancePercentage;
	}

	public void setTolerancePercentage(String tolerancePercentage) {
		this.tolerancePercentage = tolerancePercentage;
	}

	public String getMinPercentage() {
		return minPercentage;
	}

	public void setMinPercentage(String minPercentage) {
		this.minPercentage = minPercentage;
	}

	public int getGmt() {
		return gmt;
	}

	public void setGmt(int gmt) {
		this.gmt = gmt;
	}

	public boolean isDst() {
		return dst;
	}

	public void setDst(boolean dst) {
		this.dst = dst;
	}

	public String getJobPathInScenario() {
		return jobPathInScenario;
	}

	public void setJobPathInScenario(String jobPathInScenario) {
		this.jobPathInScenario = jobPathInScenario;
	}

	public boolean isResourceBasedDef() {
		return resourceBasedDef;
	}

	public void setResourceBasedDef(boolean resourceBasedDef) {
		this.resourceBasedDef = resourceBasedDef;
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

	public void setUseResourceReq(boolean useResourceReq) {
		this.useResourceReq = useResourceReq;
	}

	public boolean isUseResourceReq() {
		return useResourceReq;
	}

	public Collection<SelectItem> getResourceNameList() {
		return resourceNameList;
	}

	public void setResourceNameList(Collection<SelectItem> resourceNameList) {
		this.resourceNameList = resourceNameList;
	}

	public String getSelectedResourceForHardware() {
		return selectedResourceForHardware;
	}

	public void setSelectedResourceForHardware(String selectedResourceForHardware) {
		this.selectedResourceForHardware = selectedResourceForHardware;
	}

	public boolean isConcurrent() {
		return concurrent;
	}

	public void setConcurrent(boolean concurrent) {
		this.concurrent = concurrent;
	}

	public boolean isRunEvenIfFailed() {
		return runEvenIfFailed;
	}

	public void setRunEvenIfFailed(boolean runEvenIfFailed) {
		this.runEvenIfFailed = runEvenIfFailed;
	}

	public boolean isJobSafeToRestart() {
		return jobSafeToRestart;
	}

	public void setJobSafeToRestart(boolean jobSafeToRestart) {
		this.jobSafeToRestart = jobSafeToRestart;
	}

	public boolean isJobAutoRetry() {
		return jobAutoRetry;
	}

	public void setJobAutoRetry(boolean jobAutoRetry) {
		this.jobAutoRetry = jobAutoRetry;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamDesc() {
		return paramDesc;
	}

	public void setParamDesc(String paramDesc) {
		this.paramDesc = paramDesc;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getParamPreValue() {
		return paramPreValue;
	}

	public void setParamPreValue(String paramPreValue) {
		this.paramPreValue = paramPreValue;
	}

	public ArrayList<Parameter> getParameterList() {
		return parameterList;
	}

	public void setParameterList(ArrayList<Parameter> parameterList) {
		this.parameterList = parameterList;
	}

	public DataTable getParameterTable() {
		return parameterTable;
	}

	public void setParameterTable(DataTable parameterTable) {
		this.parameterTable = parameterTable;
	}

	public boolean isRenderUpdateParamButton() {
		return renderUpdateParamButton;
	}

	public void setRenderUpdateParamButton(boolean renderUpdateParamButton) {
		this.renderUpdateParamButton = renderUpdateParamButton;
	}

	public String getSelectedParamName() {
		return selectedParamName;
	}

	public void setSelectedParamName(String selectedParamName) {
		this.selectedParamName = selectedParamName;
	}

	public Collection<SelectItem> getJobStatusNameList() {
		return jobStatusNameList;
	}

	public void setJobStatusNameList(Collection<SelectItem> jobStatusNameList) {
		this.jobStatusNameList = jobStatusNameList;
	}

	public String getJobStatusName() {
		return jobStatusName;
	}

	public void setJobStatusName(String jobStatusName) {
		this.jobStatusName = jobStatusName;
	}

	public List<SelectItem> getManyJobStatusList() {
		return manyJobStatusList;
	}

	public void setManyJobStatusList(List<SelectItem> manyJobStatusList) {
		this.manyJobStatusList = manyJobStatusList;
	}

	public String[] getSelectedJobStatusList() {
		return selectedJobStatusList;
	}

	public void setSelectedJobStatusList(String[] selectedJobStatusList) {
		this.selectedJobStatusList = selectedJobStatusList;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getSubStateName() {
		return subStateName;
	}

	public void setSubStateName(String subStateName) {
		this.subStateName = subStateName;
	}

	public JobStatusList getJobStatusList() {
		return jobStatusList;
	}

	public void setJobStatusList(JobStatusList jobStatusList) {
		this.jobStatusList = jobStatusList;
	}

	public Status getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(Status jobStatus) {
		this.jobStatus = jobStatus;
	}

	public ReturnCode getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(ReturnCode returnCode) {
		this.returnCode = returnCode;
	}

	public Collection<SelectItem> getOsTypeList() {
		return osTypeList;
	}

	public void setOsTypeList(Collection<SelectItem> osTypeList) {
		this.osTypeList = osTypeList;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public List<SelectItem> getManyReturnCodeList() {
		return manyReturnCodeList;
	}

	public void setManyReturnCodeList(List<SelectItem> manyReturnCodeList) {
		this.manyReturnCodeList = manyReturnCodeList;
	}

	public String[] getSelectedReturnCodeList() {
		return selectedReturnCodeList;
	}

	public void setSelectedReturnCodeList(String[] selectedReturnCodeList) {
		this.selectedReturnCodeList = selectedReturnCodeList;
	}

	public boolean isStatusDialogShow() {
		return statusDialogShow;
	}

	public void setStatusDialogShow(boolean statusDialogShow) {
		this.statusDialogShow = statusDialogShow;
	}

	public List<SelectItem> getManyJobDependencyList() {
		return manyJobDependencyList;
	}

	public void setManyJobDependencyList(List<SelectItem> manyJobDependencyList) {
		this.manyJobDependencyList = manyJobDependencyList;
	}

	public String[] getSelectedJobDependencyList() {
		return selectedJobDependencyList;
	}

	public void setSelectedJobDependencyList(String[] selectedJobDependencyList) {
		this.selectedJobDependencyList = selectedJobDependencyList;
	}

	public String getDependencyExpression() {
		return dependencyExpression;
	}

	public void setDependencyExpression(String dependencyExpression) {
		this.dependencyExpression = dependencyExpression;
	}

	public String getDraggedJobName() {
		return draggedJobName;
	}

	public void setDraggedJobName(String draggedJobName) {
		this.draggedJobName = draggedJobName;
	}

	public boolean isDependencyDialogShow() {
		return dependencyDialogShow;
	}

	public void setDependencyDialogShow(boolean dependencyDialogShow) {
		this.dependencyDialogShow = dependencyDialogShow;
	}

	public String getDependencyType() {
		return dependencyType;
	}

	public void setDependencyType(String dependencyType) {
		this.dependencyType = dependencyType;
	}

	public String getDepStateName() {
		return depStateName;
	}

	public void setDepStateName(String depStateName) {
		this.depStateName = depStateName;
	}

	public Collection<SelectItem> getDepStateNameList() {
		return depStateNameList;
	}

	public void setDepStateNameList(Collection<SelectItem> depStateNameList) {
		this.depStateNameList = depStateNameList;
	}

	public String getDepSubstateName() {
		return depSubstateName;
	}

	public void setDepSubstateName(String depSubstateName) {
		this.depSubstateName = depSubstateName;
	}

	public String getDepStatusName() {
		return depStatusName;
	}

	public void setDepStatusName(String depStatusName) {
		this.depStatusName = depStatusName;
	}

	public Collection<SelectItem> getDepSubstateNameList() {
		return depSubstateNameList;
	}

	public void setDepSubstateNameList(Collection<SelectItem> depSubstateNameList) {
		this.depSubstateNameList = depSubstateNameList;
	}

	public boolean isDependencyInsertButton() {
		return dependencyInsertButton;
	}

	public void setDependencyInsertButton(boolean dependencyInsertButton) {
		this.dependencyInsertButton = dependencyInsertButton;
	}

	public String getDraggedJobPath() {
		return draggedJobPath;
	}

	public void setDraggedJobPath(String draggedJobPath) {
		this.draggedJobPath = draggedJobPath;
	}

	public Item getDependencyItem() {
		return dependencyItem;
	}

	public void setDependencyItem(Item dependencyItem) {
		this.dependencyItem = dependencyItem;
	}

	public String getDependencyTreePath() {
		return dependencyTreePath;
	}

	public void setDependencyTreePath(String dependencyTreePath) {
		this.dependencyTreePath = dependencyTreePath;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public boolean isScenario() {
		return isScenario;
	}

	public void setScenario(boolean isScenario) {
		this.isScenario = isScenario;
	}

	public boolean isUseTimeManagement() {
		return useTimeManagement;
	}

	public void setUseTimeManagement(boolean useTimeManagement) {
		this.useTimeManagement = useTimeManagement;
	}

	public ScenarioStatusList getScenarioStatusList() {
		return scenarioStatusList;
	}

	public void setScenarioStatusList(ScenarioStatusList scenarioStatusList) {
		this.scenarioStatusList = scenarioStatusList;
	}

	public boolean isJsActiveDialogShow() {
		return jsActiveDialogShow;
	}

	public void setJsActiveDialogShow(boolean jsActiveDialogShow) {
		this.jsActiveDialogShow = jsActiveDialogShow;
	}

	public boolean isJsActive() {
		return jsActive;
	}

	public void setJsActive(boolean jsActive) {
		this.jsActive = jsActive;
	}

	public JSTree getjSTree() {
		return jSTree;
	}

	public void setjSTree(JSTree jSTree) {
		this.jSTree = jSTree;
	}

}

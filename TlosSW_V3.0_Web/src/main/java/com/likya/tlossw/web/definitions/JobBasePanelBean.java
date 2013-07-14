package com.likya.tlossw.web.definitions;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;

import com.likya.tlos.model.xmlbeans.common.AgentChoiceMethodDocument.AgentChoiceMethod;
import com.likya.tlos.model.xmlbeans.common.ChoiceType;
import com.likya.tlos.model.xmlbeans.common.EventTypeDefDocument.EventTypeDef;
import com.likya.tlos.model.xmlbeans.common.InParamDocument.InParam;
import com.likya.tlos.model.xmlbeans.common.JobBaseTypeDocument.JobBaseType;
import com.likya.tlos.model.xmlbeans.common.JobTypeDefDocument.JobTypeDef;
import com.likya.tlos.model.xmlbeans.common.JobTypeDetailsDocument.JobTypeDetails;
import com.likya.tlos.model.xmlbeans.common.JsTypeDocument.JsType;
import com.likya.tlos.model.xmlbeans.common.SpecialParametersDocument.SpecialParameters;
import com.likya.tlos.model.xmlbeans.common.UnitDocument.Unit;
import com.likya.tlos.model.xmlbeans.data.AdvancedJobInfosDocument.AdvancedJobInfos;
import com.likya.tlos.model.xmlbeans.data.BaseJobInfosDocument.BaseJobInfos;
import com.likya.tlos.model.xmlbeans.data.CascadingConditionsDocument.CascadingConditions;
import com.likya.tlos.model.xmlbeans.data.ConcurrencyManagementDocument.ConcurrencyManagement;
import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.data.JobAutoRetryDocument.JobAutoRetry;
import com.likya.tlos.model.xmlbeans.data.JobInfosDocument.JobInfos;
import com.likya.tlos.model.xmlbeans.data.JobPriorityDocument.JobPriority;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.JobSafeToRestartDocument.JobSafeToRestart;
import com.likya.tlos.model.xmlbeans.data.JsIsActiveDocument.JsIsActive;
import com.likya.tlos.model.xmlbeans.data.JsTimeOutDocument.JsTimeOut;
import com.likya.tlos.model.xmlbeans.data.OSystemDocument.OSystem;
import com.likya.tlos.model.xmlbeans.data.ResourceRequirementDocument.ResourceRequirement;
import com.likya.tlos.model.xmlbeans.data.RunEvenIfFailedDocument.RunEvenIfFailed;
import com.likya.tlos.model.xmlbeans.data.StateInfosDocument.StateInfos;
import com.likya.tlos.model.xmlbeans.data.TimeManagementDocument.TimeManagement;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.sla.BirimAttribute.Birim;
import com.likya.tlos.model.xmlbeans.sla.ConditionAttribute.Condition;
import com.likya.tlos.model.xmlbeans.sla.CpuDocument.Cpu;
import com.likya.tlos.model.xmlbeans.sla.DiskDocument.Disk;
import com.likya.tlos.model.xmlbeans.sla.ForWhatAttribute.ForWhat;
import com.likya.tlos.model.xmlbeans.sla.HardwareDocument.Hardware;
import com.likya.tlos.model.xmlbeans.sla.MemDocument.Mem;
import com.likya.tlos.model.xmlbeans.sla.TimeinAttribute.Timein;
import com.likya.tlos.model.xmlbeans.state.JobStatusListDocument.JobStatusList;
import com.likya.tlos.model.xmlbeans.state.JsDependencyRuleDocument.JsDependencyRule;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfosDocument.LiveStateInfos;
import com.likya.tlos.model.xmlbeans.state.ReturnCodeDocument.ReturnCode;
import com.likya.tlos.model.xmlbeans.state.ScenarioStatusListDocument.ScenarioStatusList;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.Status;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.model.tree.WsNode;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.appmng.TraceBean;
import com.likya.tlossw.web.tree.JSTree;
import com.likya.tlossw.web.utils.DefinitionUtils;
import com.likya.tlossw.web.utils.WebInputUtils;

public abstract class JobBasePanelBean extends BaseJSPanelMBean implements Serializable {

	private static final long serialVersionUID = -3792738737288576190L;

	@ManagedProperty(value = "#{jSTree}")
	private JSTree jSTree;

	private JobProperties jobProperties;

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

	// baseJsInfos
	private String jsName;

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

	private Collection<SelectItem> typeOfTimeList;

	private String jobTimeOutValue;
	private String jobTimeOutUnit;
	private Collection<SelectItem> unitTypeList = null;

	// dependencyDefinitions
	private WsNode draggedWsNode;
	private String draggedJobPath;

	private String dependencyTreePath;

	private String[] selectedJobDependencyList;

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
	private String jobStatusName;

	/* live state info */
	private String stateName;
	private String subStateName;

	private JobStatusList jobStatusList;

	private ScenarioStatusList scenarioStatusList;

	private Collection<SelectItem> osTypeList = null;

	private String[] selectedReturnCodeList;

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

	abstract public void fillTabs();

	abstract public void fillJobPropertyDetails();

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
		if (jobProperties != null) {
			BaseJobInfos baseJobInfos = jobProperties.getBaseJobInfos();
			setJsCalendar(baseJobInfos.getCalendarId() + "");
			oSystem = baseJobInfos.getOSystem().toString();
			jobPriority = baseJobInfos.getJobPriority().toString();
			jobTypeDef = baseJobInfos.getJobInfos().getJobTypeDef().toString();
			jobBaseType = baseJobInfos.getJobInfos().getJobBaseType().toString();

			if (jobBaseType.equals(JobBaseType.PERIODIC.toString())) {
				for (Parameter param : baseJobInfos.getJobInfos().getJobTypeDetails().getSpecialParameters().getInParam().getParameterArray()) {
					if (param.getName().equals(PERIOD_TIME_PARAM)) {
						// periodTime = DefinitionUtils.calendarToStringTimeFormat(param.getValueTime());
						String timeOutputFormat = new String("HH:mm:ss");
						periodTime = DefinitionUtils.calendarToStringTimeFormat(param.getValueTime(), getSelectedTZone(), timeOutputFormat);
					}
				}
			}

			if (jobTypeDef.equals(JobTypeDef.EVENT_BASED.toString())) {
				eventTypeDef = baseJobInfos.getJobInfos().getJobTypeDetails().getEventTypeDef().toString();
			}

			if (baseJobInfos.getJsIsActive().equals(JsIsActive.YES)) {
				setJsActive(true);
			} else {
				setJsActive(false);
			}
		} else {
			System.out.println("jobProperties is NULL in fillBaseInfosTab !!");
		}
	}

	public void fillTimeManagementTab() {

		TimeManagement timeManagement = null;

		if (jobProperties != null) {
			timeManagement = jobProperties.getTimeManagement();
			super.fillTimeManagementTab(timeManagement);
		}

	}

	public void fillDependencyDefinitionsTab() {

		DependencyList dependencyList = null;

		if (jobProperties != null && jobProperties.getDependencyList() != null) {
			dependencyList = jobProperties.getDependencyList();
			super.fillDependencyDefinitionsTab(dependencyList);
		}

	}

	private void fillCascadingConditionsTab() {
		if (jobProperties != null) {
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
		} else {
			System.out.println("jobProperties is NULL in fillCascadingConditionsTab !!");
		}
	}

	private void fillStateInfosTab() {
		if (jobProperties != null) {
			if (!isJsInsertButton()) {
				stateName = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().toString();
				subStateName = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName().toString();
			}

			// durum tanimi yapildiysa alanlari dolduruyor
			if (jobProperties.getStateInfos() != null && jobProperties.getStateInfos().getJobStatusList() != null) {
				setManyJobStatusList(new ArrayList<SelectItem>());

				for (Status jobStatus : jobProperties.getStateInfos().getJobStatusList().getJobStatusArray()) {
					String statusName = jobStatus.getStatusName().toString();
					getManyJobStatusList().add(new SelectItem(statusName, statusName));
				}
			} else {
				setManyJobStatusList(null);
			}
		} else {
			System.out.println("jobProperties is NULL in fillStateInfosTab !!");
		}
	}

	private void fillConcurrencyManagementTab() {
		if (jobProperties != null) {
			setConcurrent(jobProperties.getConcurrencyManagement().getConcurrent());
		} else {
			System.out.println("jobProperties is NULL in fillConcurrencyManagementTab !!");
		}
	}

	public void fillAlarmPreferenceTab() {
		super.fillAlarmPreferenceTab(false, jobProperties);
	}

	public void fillLocalParametersTab() {
		super.fillLocalParametersTab(false, jobProperties);
	}

	private void fillAdvancedJobInfosTab() {
		if (jobProperties != null) {
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
				setAgentChoiceMethod(advancedJobInfos.getAgentChoiceMethod().getStringValue());

				if (getAgentChoiceMethod().equals(ChoiceType.USER_MANDATORY_PREFERENCE.toString())) {
					setSelectedAgent(advancedJobInfos.getAgentChoiceMethod().getAgentId());
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
				memoryPart = mem.getForWhat().toString();
				memoryCondition = mem.getCondition().toString();
				memoryValue = mem.getStringValue();
				memoryUnit = mem.getBirim().toString();

				Disk disk = hardware.getDisk();
				diskPart = disk.getForWhat().toString();
				diskCondition = disk.getCondition().toString();
				diskValue = disk.getStringValue();
				diskUnit = disk.getBirim().toString();
			}
		} else {
			System.out.println("jobProperties is NULL in fillAdvancedJobInfosTab !!");
		}
	}

	public void initJobPanel() {
		
		super.init();

		long startTime = System.currentTimeMillis();

		System.out.println("");
		System.out.println("JobBaseBean.initJobPanel");
		
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

		System.out.println("JobBaseBean.initJobPanel Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");

		System.out.println(getClass().getName());

	}

	public void fillAllLists() {
		
		super.fillAllLists();
		
		long startTime = System.currentTimeMillis();
		
		fillOSystemList();
		fillJobBaseTypeList();
		fillEventTypeDefList();
		fillJobTypeDefList();
		fillRelativeTimeOptionList();
		fillUnitTypeList();
		fillJobStatusList();
		fillJobStateList();
		fillJobSubtateList();

		System.out.println("JobBaseBean.WebInputUtils.fillAllLists Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		setTzList(WebInputUtils.fillTZList());
		System.out.println("JobBaseBean.WebInputUtils.fillTZList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		setTypeOfTimeList(WebInputUtils.fillTypesOfTimeList());
		System.out.println("JobBaseBean.WebInputUtils.fillTypesOfTimeList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		setAlarmList(WebInputUtils.fillAlarmList(getDbOperations().getAlarms()));
		System.out.println("JobBaseBean.WebInputUtils.fillAlarmList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		setDefinedAgentList(WebInputUtils.fillAgentList(getDbOperations().getAgents()));
		System.out.println("JobBaseBean.WebInputUtils.fillAgentList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		setJsSLAList(WebInputUtils.fillSLAList(getDbOperations().getSlaList()));
		System.out.println("JobBaseBean.WebInputUtils.fillSLAList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		setResourceNameList(WebInputUtils.fillResourceNameList(getDbOperations().getResources()));
		System.out.println("JobBaseBean.WebInputUtils.fillResourceNameList fill things Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
	}

	// bir ise ya baslayacagi zaman verilmeli ya da bagimlilik tanimlanmali
	// ikisi de yoksa validasyondan gecemiyor
	public boolean validateTimeManagement() {
		if (getStartTime() == null || getStartTime().equals("")) {
			if (jobProperties.getDependencyList() == null || jobProperties.getDependencyList().getItemArray().length == 0) {
				addMessage("jobInsert", FacesMessage.SEVERITY_ERROR, "tlos.validation.job.timeOrDependency", null);
				return false;
			}
		}
		return true;
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

		baseJobInfos.setCalendarId(Integer.parseInt(getJsCalendar()));
		baseJobInfos.setOSystem(OSystem.Enum.forString(oSystem));
		if (jobPriority.isEmpty())
			jobPriority = "1"; // default değer
		baseJobInfos.setJobPriority(JobPriority.Enum.forString(jobPriority));

		if (isJsActive()) {
			baseJobInfos.setJsIsActive(JsIsActive.YES);
		} else {
			baseJobInfos.setJsIsActive(JsIsActive.NO);
		}

		JobInfos jobInfos = baseJobInfos.getJobInfos();
		jobInfos.setJobBaseType(JobBaseType.Enum.forString(jobBaseType));

		// periyodik is ise onunla ilgili alanlari dolduruyor
		if (jobBaseType.equals(JobBaseType.PERIODIC.toString())) {

			SpecialParameters specialParameters;
			if (baseJobInfos.getJobInfos().getJobTypeDetails().getSpecialParameters() == null) {
				specialParameters = SpecialParameters.Factory.newInstance();
			} else {
				specialParameters = baseJobInfos.getJobInfos().getJobTypeDetails().getSpecialParameters();
			}

			InParam inParam = InParam.Factory.newInstance();

			Parameter parameter = Parameter.Factory.newInstance();
			parameter.setName(PERIOD_TIME_PARAM);
			parameter.setValueTime(DefinitionUtils.dateToXmlTime(periodTime, getSelectedTZone()));
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

		baseJobInfos.setUserId(getWebAppUser().getId());
	}

	protected void fillTimeManagement() {

		TimeManagement timeManagement;

		if (!isUseTimeManagement() || jobProperties == null) {
			return;
		}

		timeManagement = jobProperties.getTimeManagement();
		super.fillTimeManagement(timeManagement);

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

		if (isJsInsertButton() || isJsUpdateButton()) {
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

	public void resetPanelInputs() {

		oSystem = OSystem.WINDOWS.toString();
		jobPriority = "1";
		jobBaseType = JobBaseType.NON_PERIODIC.toString();
		periodTime = "";
		jobTypeDef = JobTypeDef.TIME_BASED.toString();
		eventTypeDef = EventTypeDef.FILE.toString();

		jobTimeOutValue = "";
		jobTimeOutUnit = Unit.HOURS.toString();

		jobStatusName = "";
		jobSLA = NONE;
		useResourceReq = false;
		cpuValue = "0";
		cpuUnit = "%";
		memoryValue = "0";
		diskValue = "0";

		dependencyItem = Item.Factory.newInstance();
		dependencyItem.setJsDependencyRule(JsDependencyRule.Factory.newInstance());

		super.resetPanelInputs();
	}

	protected void fillConcurrencyManagement() {
		jobProperties.getConcurrencyManagement().setConcurrent(isConcurrent());
	}

	protected void fillAlarmPreference() {
		super.fillAlarmPreference(false, jobProperties);
	}

	protected void fillLocalParameters() {
		super.fillLocalParameters(false, jobProperties);
	}

	private void fillAdvancedJobInfos() {
		AdvancedJobInfos advancedJobInfos = AdvancedJobInfos.Factory.newInstance();

		// sla tanimi
		if (!jobSLA.equals(NONE)) {
			advancedJobInfos.setSLAId(Integer.valueOf(jobSLA));
		}

		AgentChoiceMethod choiceMethod = AgentChoiceMethod.Factory.newInstance();
		choiceMethod.setStringValue(getAgentChoiceMethod());

		if (getAgentChoiceMethod().equals(ChoiceType.USER_MANDATORY_PREFERENCE.toString())) {
			choiceMethod.setAgentId(getSelectedAgent());
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
			mem.setForWhat(ForWhat.Enum.forString(memoryPart));
			mem.setCondition(Condition.Enum.forString(memoryCondition));
			mem.setStringValue(memoryValue);
			mem.setBirim(Birim.Enum.forString(memoryUnit));

			Disk disk = Disk.Factory.newInstance();
			disk.setForWhat(ForWhat.Enum.forString(diskPart));
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

	public void cancelInsertOrUpdateJsAction(ActionEvent actionEvent) {
		setJsNameConfirmDialog(false);
	}

	public void insertJsWithDuplicateName(ActionEvent actionEvent) {
		insertJobDefinition();
	}

	public void updateJsWithDuplicateName(ActionEvent actionEvent) {
		updateJobDefinition();
	}

	public void insertJobDefinition() {
		if (!isJsNameConfirmDialog()) {
			if (!jobCheckUp() & getJobId()) {
				return;
			}
		} else {
			setJsNameConfirmDialog(false);
		}

		if (getDbOperations().insertJob(getWebAppUser().getId(), getDocumentId(), getJobPropertiesXML(), DefinitionUtils.getTreePath(jobPathInScenario))) {
			// senaryoya yeni dugumu ekliyor
			// addJobNodeToScenarioPath();

			addMessage("jobInsert", FacesMessage.SEVERITY_INFO, "tlos.success.job.insert", null);

			switchInsertUpdateButtons();

		} else {
			addMessage("jobInsert", FacesMessage.SEVERITY_ERROR, "tlos.error.job.insert", null);
		}
	}

	public void updateJobDefinition() {
		if (!isJsNameConfirmDialog()) {
			if (!jobCheckUpForUpdate()) {
				return;
			}
		} else {
			setJsNameConfirmDialog(false);
		}

		if (getDbOperations().updateJob(getWebAppUser().getId(), getDocumentId(), getJobPropertiesXML(), DefinitionUtils.getTreePath(jobPathInScenario))) {
			addMessage("jobUpdate", FacesMessage.SEVERITY_INFO, "tlos.success.job.update", null);

			// isin adi degistirildiyse agactaki adini degistiriyor
			// merve: TreeNode uzerinde datasini degistirmeye calistim, ama adini degistirme ile ilgili bir fonksiyonu yok. javascript kullanarak yapanlar var bu isi.
			// Simdilik agaci bastan olusturarak bu problemi gectim. Adini degistirecegim zaman jsName alanindan eski adini kullanacagim.
			if (!jsName.equals(jobProperties.getBaseJobInfos().getJsName())) {
				jSTree.initJSTree();
			}

		} else {
			addMessage("jobUpdate", FacesMessage.SEVERITY_ERROR, "tlos.error.job.update", null);
		}
	}

	private boolean getJobId() {
		int jobId = getDbOperations().getNextId(CommonConstantDefinitions.JOB_ID);

		if (jobId < 0) {
			addMessage("jobInsert", FacesMessage.SEVERITY_ERROR, "tlos.error.job.getId", null);
			return false;
		}

		jobProperties.setID(jobId + "");
		return true;
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

	private boolean jobCheckUpForUpdate() {
		String jobCheckResult = getDbOperations().getJobExistence(getWebAppUser().getId(), getDocumentId(), DefinitionUtils.getTreePath(jobPathInScenario), jobProperties.getBaseJobInfos().getJsName());

		// bu isimde bir iş yoksa 0
		// ayni path de aynı isimde bir iş varsa 1
		// iç senaryolarda aynı isimde bir iş varsa 2
		// senaryonun dışında aynı isimde bir iş varsa 3
		if (jobCheckResult != null) {
			if (jobCheckResult.equalsIgnoreCase(DUPLICATE_NAME_AND_PATH)) {

				JobProperties job = getDbOperations().getJob(getWebAppUser().getId(), getDocumentId(), DefinitionUtils.getTreePath(jobPathInScenario), jobProperties.getBaseJobInfos().getJsName());

				// id aynı ise kendi adını değiştirmeden güncellediği için uyarı vermiyor
				if (!job.getID().equals(jobProperties.getID())) {
					addMessage("jobUpdate", FacesMessage.SEVERITY_ERROR, "tlos.info.job.name.duplicate", null);
					return false;
				}
			} else if (jobCheckResult.equalsIgnoreCase(INNER_DUPLICATE_NAME)) {
				setJsNameConfirmDialog(true);
				setInnerJsNameDuplicate(true);

				return false;

			} else if (jobCheckResult.equalsIgnoreCase(OUTER_DUPLICATE_NAME)) {
				setJsNameConfirmDialog(true);
				setInnerJsNameDuplicate(false);

				return false;
			}
		}

		return true;
	}

	private boolean jobCheckUp() {
		String jobCheckResult = getDbOperations().getJobExistence(getWebAppUser().getId(), getDocumentId(), DefinitionUtils.getTreePath(jobPathInScenario), jobProperties.getBaseJobInfos().getJsName());

		// bu isimde bir iş yoksa 0
		// ayni path de aynı isimde bir iş varsa 1
		// iç senaryolarda aynı isimde bir iş varsa 2
		// senaryonun dışında aynı isimde bir iş varsa 3
		if (jobCheckResult != null) {
			if (jobCheckResult.equalsIgnoreCase(DUPLICATE_NAME_AND_PATH)) {
				addMessage("jobInsert", FacesMessage.SEVERITY_ERROR, "tlos.info.job.name.duplicate", null);
				return false;
			} else if (jobCheckResult.equalsIgnoreCase(INNER_DUPLICATE_NAME)) {
				setJsNameConfirmDialog(true);
				setInnerJsNameDuplicate(true);

				return false;

			} else if (jobCheckResult.equalsIgnoreCase(OUTER_DUPLICATE_NAME)) {
				setJsNameConfirmDialog(true);
				setInnerJsNameDuplicate(false);

				return false;
			}
		}
		return true;
	}

	public void dependencyDropAction() {
		if (!checkDependencyValidation()) {
			return;
		}

		dependencyItem = Item.Factory.newInstance();
		dependencyItem.setJsDependencyRule(JsDependencyRule.Factory.newInstance());
		dependencyItem.setJsName(draggedWsNode.getName());

		depStateName = "";
		depSubstateName = "";
		depStatusName = "";

		dependencyInsertButton = true;
		dependencyDialogShow = true;
	}

	private boolean checkDependencyValidation() {
		JobProperties draggedJobProperties = getDbOperations().getJobFromId(getWebAppUser().getId(), getDocumentId(), draggedWsNode.getId());

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

		dependencyItem.setDependencyID(dependencyItem.getDependencyID().toUpperCase());

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

		String depJobName = draggedWsNode.getName();

		SelectItem item = new SelectItem();
		item.setLabel(depJobName);
		item.setValue(dependencyTreePath + "." + depJobName);

		getManyJobDependencyList().add(item);

		dependencyDialogShow = false;
	}

	private void composeDependencyExpression() {
		String depExpression = "";

		for (Item item : jobProperties.getDependencyList().getItemArray()) {
			if (depExpression.equals("")) {
				depExpression = item.getDependencyID().toUpperCase();
			} else {
				depExpression = depExpression + " AND " + item.getDependencyID().toUpperCase();
			}
		}
		jobProperties.getDependencyList().setDependencyExpression(depExpression);
		setDependencyExpression(depExpression);
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
			for (int j = 0; j < getManyJobDependencyList().size(); j++) {
				if (getManyJobDependencyList().get(j).getValue().equals(selectedJobDependencyList[i])) {
					getManyJobDependencyList().remove(j);
					j = getManyJobDependencyList().size();
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

	public void initJobStatusPopup(ActionEvent e) {
		setStatusDialogShow(!checkDuplicateStateName());

		setOsType(OSystem.WINDOWS.toString());
		setJobStatus(Status.Factory.newInstance());
		setReturnCode(ReturnCode.Factory.newInstance());
		setManyReturnCodeList(new ArrayList<SelectItem>());
	}

	public void addJReturnCodeAction() {
		super.addJReturnCodeAction(false, jobStatusList);
	}

	public void saveJobStatusAction() {

		if (getManyReturnCodeList() == null || getManyReturnCodeList().size() == 0) {
			addMessage("addReturnCode", FacesMessage.SEVERITY_ERROR, "tlos.validation.job.codeList", null);

			return;
		}

		Status tmpJobStatus = WebInputUtils.cloneJobStatus(getJobStatus());

		/**
		 * @author serkan taş
		 * @date 14.07.2013
		 * Aşağıdaki kımsa gerek yok gibi geldi bana
		 */
//		if (isScenario) {
//			addToScenarioStatusList(tmpJobStatus);
//		} else {
			addToJobStatusList(tmpJobStatus);
//		}

		if (getManyJobStatusList() == null) {
			setManyJobStatusList(new ArrayList<SelectItem>());
		}

		getManyJobStatusList().add(new SelectItem(jobStatusName, jobStatusName));

		addMessage("addReturnCode", FacesMessage.SEVERITY_INFO, "tlos.info.job.code.add", null);

		setStatusDialogShow(false);
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

//	private void addToScenarioStatusList(Status tmpJobStatus) {
//		ScenarioStatusList scenarioStatusList = null;
//
//		if (scenario.getScenarioStatusList() == null || scenario.getScenarioStatusList().sizeOfScenarioStatusArray() == 0) {
//			scenario.setScenarioStatusList(ScenarioStatusList.Factory.newInstance());
//
//			scenarioStatusList = scenario.getScenarioStatusList();
//
//			tmpJobStatus.setStsId("1");
//		} else {
//			scenarioStatusList = scenario.getScenarioStatusList();
//
//			int lastStatusIndex = scenarioStatusList.sizeOfScenarioStatusArray() - 1;
//			String id = scenarioStatusList.getScenarioStatusArray(lastStatusIndex).getStsId();
//
//			tmpJobStatus.setStsId((Integer.parseInt(id) + 1) + "");
//		}
//
//		Status newStatus = scenarioStatusList.addNewScenarioStatus();
//		newStatus.set(tmpJobStatus);
//	}

	public void closeJobStatusDialogAction() {
		setStatusDialogShow(false);
	}

	public void deleteJReturnCodeAction() {
		if (selectedReturnCodeList.length == 0) {
			addMessage("addReturnCode", FacesMessage.SEVERITY_ERROR, "tlos.info.job.code.delete", null);
			return;
		}

		for (int i = 0; i < selectedReturnCodeList.length; i++) {
			for (int j = 0; j < getManyReturnCodeList().size(); j++) {
				if (getManyReturnCodeList().get(j).getValue().toString().equals(selectedReturnCodeList[i])) {

					// TODO job tanimi ya da senaryo tanimi icinden silme isi
					// burada yapilacak,
					// asagida sadece goruntu olarak ekrandaki listeden siliyor

					getManyReturnCodeList().remove(j);
					j = getManyReturnCodeList().size();
				}
			}
		}
	}

	public void deleteJobStatusAction() {
		for (int i = 0; i < getSelectedJobStatusList().length; i++) {
			for (int j = 0; j < getManyJobStatusList().size(); j++) {
				if (getManyJobStatusList().get(j).getValue().equals(getSelectedJobStatusList()[i])) {

					JobStatusList jobStatusList = jobProperties.getStateInfos().getJobStatusList();

					for (int k = 0; k < jobStatusList.sizeOfJobStatusArray(); k++) {
						if (getManyJobStatusList().get(j).getValue().equals(jobStatusList.getJobStatusArray(k).getStatusName().toString())) {
							jobStatusList.removeJobStatus(k);
							k = jobStatusList.sizeOfJobStatusArray();
						}
					}
					getManyJobStatusList().remove(j);
					j = getManyJobStatusList().size();
				}
			}
		}
	}

	public void jobStatusEditAction() {

		Status[] statusArray = null;

		if (jobProperties != null) {
			statusArray = jobProperties.getStateInfos().getJobStatusList().getJobStatusArray();
			super.jobStatusEditAction(statusArray);
		}

	}

	public void updateJobStatusAction() {

		Status[] statusArray = null;

		if (jobProperties != null) {
			statusArray = jobProperties.getStateInfos().getJobStatusList().getJobStatusArray();
			super.updateJobStatusAction(statusArray);
		}

	}

	// işe sağ tıklayarak sil dediğimizde buraya geliyor
	public boolean deleteJob() {
		boolean result = true;
		if (getDbOperations().deleteJob(getWebAppUser().getId(), getDocumentId(), DefinitionUtils.getTreePath(jobPathInScenario), getJobPropertiesXML())) {
			jSTree.removeJobNode(jobPathInScenario, jobProperties.getBaseJobInfos().getJsName());
			addMessage("jobDelete", FacesMessage.SEVERITY_INFO, "tlos.success.job.delete", null);
		} else {
			addMessage("jobDelete", FacesMessage.SEVERITY_ERROR, "tlos.error.job.delete", null);
			result = false;
		}

		return result;
	}

	public void addJobNodeToScenarioPath() {
		jSTree.addJobNodeToScenarioPath(jobProperties, jobPathInScenario);
	}

	public String getJobPropertiesXML() {
		QName qName = JobProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String jobPropertiesXML = jobProperties.xmlText(xmlOptions);

		return jobPropertiesXML;
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

	public String getJobStatusName() {
		return jobStatusName;
	}

	public void setJobStatusName(String jobStatusName) {
		this.jobStatusName = jobStatusName;
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

	public Collection<SelectItem> getOsTypeList() {
		return osTypeList;
	}

	public void setOsTypeList(Collection<SelectItem> osTypeList) {
		this.osTypeList = osTypeList;
	}

	public String[] getSelectedReturnCodeList() {
		return selectedReturnCodeList;
	}

	public void setSelectedReturnCodeList(String[] selectedReturnCodeList) {
		this.selectedReturnCodeList = selectedReturnCodeList;
	}

	public String[] getSelectedJobDependencyList() {
		return selectedJobDependencyList;
	}

	public void setSelectedJobDependencyList(String[] selectedJobDependencyList) {
		this.selectedJobDependencyList = selectedJobDependencyList;
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

	public ScenarioStatusList getScenarioStatusList() {
		return scenarioStatusList;
	}

	public void setScenarioStatusList(ScenarioStatusList scenarioStatusList) {
		this.scenarioStatusList = scenarioStatusList;
	}

	public JSTree getjSTree() {
		return jSTree;
	}

	public void setjSTree(JSTree jSTree) {
		this.jSTree = jSTree;
	}

	public Collection<SelectItem> getTypeOfTimeList() {
		return typeOfTimeList;
	}

	public void setTypeOfTimeList(Collection<SelectItem> typeOfTimeList) {
		this.typeOfTimeList = typeOfTimeList;
	}

	public String getJsName() {
		return jsName;
	}

	public void setJsName(String jsName) {
		this.jsName = jsName;
	}

	public WsNode getDraggedWsNode() {
		return draggedWsNode;
	}

	public void setDraggedWsJobNode(WsNode draggedWsNode) {
		this.draggedWsNode = draggedWsNode;
	}

}

package com.likya.tlossw.web.definitions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;

import com.likya.tlos.model.xmlbeans.common.JobBaseTypeDocument.JobBaseType;
import com.likya.tlos.model.xmlbeans.common.JobTypeDetailsDocument.JobTypeDetails;
import com.likya.tlos.model.xmlbeans.common.JsTypeDocument.JsType;
import com.likya.tlos.model.xmlbeans.data.AdvancedJobInfosDocument.AdvancedJobInfos;
import com.likya.tlos.model.xmlbeans.data.BaseJobInfosDocument.BaseJobInfos;
import com.likya.tlos.model.xmlbeans.data.CascadingConditionsDocument.CascadingConditions;
import com.likya.tlos.model.xmlbeans.data.ConcurrencyManagementDocument.ConcurrencyManagement;
import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.data.JobAutoRetryDocument.JobAutoRetry;
import com.likya.tlos.model.xmlbeans.data.JobInfosDocument.JobInfos;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.JobSafeToRestartDocument.JobSafeToRestart;
import com.likya.tlos.model.xmlbeans.data.JsTimeOutDocument.JsTimeOut;
import com.likya.tlos.model.xmlbeans.data.LogAnalysisDocument.LogAnalysis;
import com.likya.tlos.model.xmlbeans.data.RunEvenIfFailedDocument.RunEvenIfFailed;
import com.likya.tlos.model.xmlbeans.data.StateInfosDocument.StateInfos;
import com.likya.tlos.model.xmlbeans.data.TimeManagementDocument.TimeManagement;
import com.likya.tlos.model.xmlbeans.state.JobStatusListDocument.JobStatusList;
import com.likya.tlos.model.xmlbeans.state.JsDependencyRuleDocument.JsDependencyRule;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfosDocument.LiveStateInfos;
import com.likya.tlos.model.xmlbeans.state.ScenarioStatusListDocument.ScenarioStatusList;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.Status;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.model.engine.EngineeConstants;
import com.likya.tlossw.model.tree.WsNode;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.definitions.helpers.BaseJobInfosTabBean;
import com.likya.tlossw.web.definitions.helpers.EnvVariablesTabBean;
import com.likya.tlossw.web.definitions.helpers.StateInfosTabBean;
import com.likya.tlossw.web.model.JSBuffer;
import com.likya.tlossw.web.tree.JSTree;
import com.likya.tlossw.web.utils.BeanUtils;
import com.likya.tlossw.web.utils.ComboListUtils;
import com.likya.tlossw.web.utils.ConstantDefinitions;
import com.likya.tlossw.web.utils.DefinitionUtils;

public abstract class JobBasePanelBean extends JSBasePanelMBean implements Serializable {

	private static final long serialVersionUID = -3792738737288576190L;

	@ManagedProperty(value = "#{jSTree}")
	private JSTree jSTree;

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

	private String scenarioId;
	private JobProperties jobProperties;

	private String jobPathInScenario;

	// baseJsInfos
	private String jsName;
	private String jsId;
	private Collection<SelectItem> jobCommandTypeList = null;
	private String jobCommandType;

	// dependencyDefinitions
	private WsNode draggedWsNode;
	private String draggedJobPath;

	private String dependencyTreePath;

	private String[] selectedJobDependencyList;

	/* dependency popup */
	private boolean dependencyDialogShow = false;

	private Item dependencyItem;

	private String dependencyType = STATUS;

	private String depStateName;

	private String depSubstateName;

	private String depStatusName = StatusName.SUCCESS.toString();

	private boolean dependencyInsertButton = true;

	// cascadingConditions
	private boolean runEvenIfFailed;
	private boolean jobSafeToRestart;
	private boolean jobAutoRetry;

	/* live state info */
	private String stateName;
	private String subStateName;

	private JobStatusList jobStatusList;

	private ScenarioStatusList scenarioStatusList;

	private Collection<SelectItem> osTypeList = null;

	private HashMap<String, String> statusToSubstate = new HashMap<String, String>();
	private HashMap<String, String> substateToState = new HashMap<String, String>();

	abstract public void fillTabs();

	abstract public void fillJobPropertyDetails();

	private BaseJobInfosTabBean baseJobInfosTabBean;
	private EnvVariablesTabBean envVariablesTabBean;
	private StateInfosTabBean stateInfosTabBean;

	public void initJobPanel() {

		long startTime = System.currentTimeMillis();
		ComboListUtils.logTimeInfo("JobBasePanelBean.initJobPanel", startTime);

		super.init();

		baseJobInfosTabBean = new BaseJobInfosTabBean(this, getJobBaseType());
		stateInfosTabBean = new StateInfosTabBean(this);
		envVariablesTabBean = new EnvVariablesTabBean();

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

		System.out.println(getClass().getName());

		ComboListUtils.logTimeInfo("JobBasePanelBean.initJobPanel Süre : ", startTime);

		fill3S();
	}

	private void fill3S() {
		statusToSubstate.put(StatusName.SUCCESS.toString(), SubstateName.COMPLETED.toString());
		statusToSubstate.put(StatusName.WARNING.toString(), SubstateName.COMPLETED.toString());
		statusToSubstate.put(StatusName.FAILED.toString(), SubstateName.COMPLETED.toString());
		statusToSubstate.put(StatusName.TIME_IN.toString(), SubstateName.ON_RESOURCE.toString());
		statusToSubstate.put(StatusName.TIME_OUT.toString(), SubstateName.ON_RESOURCE.toString());
		statusToSubstate.put(StatusName.LOOKFOR_RESOURCE.toString(), SubstateName.READY.toString());
		statusToSubstate.put(StatusName.WAITING.toString(), SubstateName.READY.toString());
		statusToSubstate.put(StatusName.TRANSFERING.toString(), SubstateName.READY.toString());

		substateToState.put(SubstateName.COMPLETED.toString(), StateName.FINISHED.toString());
		substateToState.put(SubstateName.SKIPPED.toString(), StateName.FINISHED.toString());
		substateToState.put(SubstateName.STOPPED.toString(), StateName.FINISHED.toString());
		substateToState.put(SubstateName.STAGE_OUT.toString(), StateName.RUNNING.toString());
		substateToState.put(SubstateName.MIGRATING.toString(), StateName.RUNNING.toString());
		substateToState.put(SubstateName.ON_RESOURCE.toString(), StateName.RUNNING.toString());
		substateToState.put(SubstateName.HELD.toString(), StateName.RUNNING.toString());
		substateToState.put(SubstateName.STAGE_IN.toString(), StateName.RUNNING.toString());
		substateToState.put(SubstateName.PAUSED.toString(), StateName.PENDING.toString());
		substateToState.put(SubstateName.READY.toString(), StateName.PENDING.toString());
		substateToState.put(SubstateName.IDLED.toString(), StateName.PENDING.toString());
		substateToState.put(SubstateName.VALIDATED.toString(), StateName.PENDING.toString());
		substateToState.put(SubstateName.CREATED.toString(), StateName.PENDING.toString());
		substateToState.put(SubstateName.DEACTIVATED.toString(), StateName.PENDING.toString());
	}

	public void fillJobPanel() {
		fillBaseInfosTab();
		fillTimeManagementTab();
		fillDependencyDefinitionsTab();
		fillCascadingConditionsTab();
		fillStateInfosTab();
		fillConcurrencyManagementTab();
		fillAlarmPreferenceTab();
		fillLocalParametersTab();
		fillEnvVariablesTab();
		fillAdvancedJobInfosTab();
		fillLogAnalysisTab();
	}

	public void fillEnvVariablesTab() {
		getEnvVariablesTabBean().fillEnvVariablesTab(jobProperties);
	}

	private void fillBaseInfosTab() {
		getBaseJobInfosTabBean().fillBaseInfosTab();
	}

	public void fillLogAnalysisTab() {

		LogAnalysis logAnalysis = null;

		if (jobProperties != null) {
			logAnalysis = jobProperties.getLogAnalysis();
			getLogAnalyzingTabBean().fillLogAnalysisTab(logAnalysis);
		}

	}

	public void fillTimeManagementTab() {

		TimeManagement timeManagement = null;

		if (jobProperties != null) {
			timeManagement = jobProperties.getTimeManagement();
			getTimeManagementTabBean().fillTimeManagementTab(timeManagement);
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
				getStateInfosTabBean().setManyJobStatusList(new ArrayList<SelectItem>());

				for (Status jobStatus : jobProperties.getStateInfos().getJobStatusList().getJobStatusArray()) {
					String statusName = jobStatus.getStatusName().toString();
					getStateInfosTabBean().getManyJobStatusList().add(new SelectItem(statusName, statusName));
				}
			} else {
				getStateInfosTabBean().setManyJobStatusList(null);
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
		getAlarmPreferencesTabBean().fillAlarmPreferenceTab(false, jobProperties);
	}

	public void fillLocalParametersTab() {
		getLocalParametersTabBean().fillLocalParametersTab(false, jobProperties);
	}

	private void fillAdvancedJobInfosTab() {
		if (jobProperties != null) {
			if (jobProperties.getAdvancedJobInfos() == null) {
				return;
			}

			getAdvancedJobInfosTab().fillAdvancedJobInfosTab(jobProperties.getAdvancedJobInfos());
		} else {
			System.out.println("jobProperties is NULL in fillAdvancedJobInfosTab !!");
		}
	}

	// bir ise ya baslayacagi zaman verilmeli ya da bagimlilik tanimlanmali
	// ikisi de yoksa validasyondan gecemiyor
	public boolean validateTimeManagement() {
		if (getTimeManagementTabBean().getStartTime() == null || getTimeManagementTabBean().getStartTime().equals("")) {
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
		fillEnvVariables();
		fillTimeManagement();
		fillDependencyDefinitions();
		fillCascadingConditions();
		fillStateInfos();
		fillConcurrencyManagement();
		fillAlarmPreference();
		fillLocalParameters();
		fillAdvancedJobInfos();
		fillLogAnalysis();
	}

	private void fillBaseJobInfos() {
		getBaseJobInfosTabBean().fillBaseJobInfos();
	}

	protected void fillLogAnalysis() {

		LogAnalysis logAnalysis;

		if (!getLogAnalyzingTabBean().isUseLogAnalyzer() || jobProperties == null) {
			return;
		}

		if (jobProperties.getLogAnalysis() == null) {
			logAnalysis = LogAnalysis.Factory.newInstance();
		} else {
			logAnalysis = jobProperties.getLogAnalysis();
		}
		jobProperties.setLogAnalysis(getLogAnalyzingTabBean().fillLogAnalysis(logAnalysis));

	}

	protected void fillTimeManagement() {

		TimeManagement timeManagement;

		if (!getTimeManagementTabBean().isUseTimeManagement() || jobProperties == null) {
			return;
		}

		timeManagement = jobProperties.getTimeManagement();
		getTimeManagementTabBean().fillTimeManagement(timeManagement);

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

		jobProperties.getCascadingConditions().setRunEvenIfFailed(RunEvenIfFailed.Enum.forString(BeanUtils.boolToWord(runEvenIfFailed)));

		jobProperties.getCascadingConditions().setJobSafeToRestart(JobSafeToRestart.Enum.forString(BeanUtils.boolToWord(jobSafeToRestart)));

		jobProperties.getCascadingConditions().setJobAutoRetry(JobAutoRetry.Enum.forString(BeanUtils.boolToWord(jobAutoRetry)));

		/*
		 * if (runEvenIfFailed) {
		 * jobProperties.getCascadingConditions().setRunEvenIfFailed(RunEvenIfFailed.YES); } else {
		 * jobProperties.getCascadingConditions().setRunEvenIfFailed(RunEvenIfFailed.NO); }
		 * 
		 * if (jobSafeToRestart) {
		 * jobProperties.getCascadingConditions().setJobSafeToRestart(JobSafeToRestart.YES); } else
		 * { jobProperties.getCascadingConditions().setJobSafeToRestart(JobSafeToRestart.NO); }
		 * 
		 * if (jobAutoRetry) {
		 * jobProperties.getCascadingConditions().setJobAutoRetry(JobAutoRetry.YES); } else {
		 * jobProperties.getCascadingConditions().setJobAutoRetry(JobAutoRetry.NO); }
		 */
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

		dependencyItem = Item.Factory.newInstance();
		dependencyItem.setJsDependencyRule(JsDependencyRule.Factory.newInstance());

		dependencyType = STATUS;
		depStatusName = StatusName.SUCCESS.toString();

		getBaseJobInfosTabBean().resetTab();
		getStateInfosTabBean().resetTab();
		getEnvVariablesTabBean().resetTab(true);

		super.resetPanelInputs();

	}

	protected void fillConcurrencyManagement() {
		jobProperties.getConcurrencyManagement().setConcurrent(isConcurrent());
	}

	protected void fillAlarmPreference() {
		getAlarmPreferencesTabBean().fillAlarmPreference(false, jobProperties);
	}

	protected void fillEnvVariables() {
		getEnvVariablesTabBean().fillEnvVariables(jobProperties);
	}

	protected void fillLocalParameters() {
		getLocalParametersTabBean().fillLocalParameters(false, jobProperties);
	}

	private void fillAdvancedJobInfos() {
		AdvancedJobInfos advancedJobInfos = getAdvancedJobInfosTab().fillAdvancedJobInfos();
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
			addJobNodeToScenarioPath();

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

			/*
			 * Bu iki satırı TSW-793'ten dolayı ekledim. İleride daha uygun bir çözüm bulunduğunda
			 * kaldırılabilir. Merve
			 */
			resetPanelInputs();
			fillTabs();
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

		StringTokenizer pathTokenizer = new StringTokenizer(treePath, ".");

		while (pathTokenizer.hasMoreTokens()) {
			scenarioId = pathTokenizer.nextToken();

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
			if (jobCheckResult.equalsIgnoreCase(ConstantDefinitions.DUPLICATE_NAME_AND_PATH)) {

				JobProperties job = getDbOperations().getJob(getWebAppUser().getId(), getDocumentId(), DefinitionUtils.getTreePath(jobPathInScenario), jobProperties.getBaseJobInfos().getJsName());

				// id aynı ise kendi adını değiştirmeden güncellediği için uyarı vermiyor
				if (!job.getID().equals(jobProperties.getID())) {
					addMessage("jobUpdate", FacesMessage.SEVERITY_ERROR, "tlos.info.job.name.duplicate", null);
					return false;
				}
			} else if (jobCheckResult.equalsIgnoreCase(ConstantDefinitions.INNER_DUPLICATE_NAME)) {
				setJsNameConfirmDialog(true);
				setInnerJsNameDuplicate(true);

				return false;

			} else if (jobCheckResult.equalsIgnoreCase(ConstantDefinitions.OUTER_DUPLICATE_NAME)) {
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
			if (jobCheckResult.equalsIgnoreCase(ConstantDefinitions.DUPLICATE_NAME_AND_PATH)) {
				addMessage("jobInsert", FacesMessage.SEVERITY_ERROR, "tlos.info.job.name.duplicate", null);
				return false;
			} else if (jobCheckResult.equalsIgnoreCase(ConstantDefinitions.INNER_DUPLICATE_NAME)) {
				setJsNameConfirmDialog(true);
				setInnerJsNameDuplicate(true);

				return false;

			} else if (jobCheckResult.equalsIgnoreCase(ConstantDefinitions.OUTER_DUPLICATE_NAME)) {
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
		dependencyItem.setJsId(draggedWsNode.getId());
		dependencyItem.setDependencyID("ID" + jsId + "_DEP_ID" + draggedWsNode.getId());
		dependencyItem.setComment("JobID=" + jsId + " is dependent to jobID=" + draggedWsNode.getId());
		depStateName = "";
		depSubstateName = "";

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

		dependencyTreePath = getDependencyTreePath(EngineeConstants.LONELY_JOBS + '.' + draggedJobPath);

		if (dependencyType.equals(STATUS)) {
			dependencyItem.getJsDependencyRule().setStatusName(StatusName.Enum.forString(depStatusName));

			depSubstateName = statusToSubstate.get(depStatusName);
			dependencyItem.getJsDependencyRule().setSubstateName(SubstateName.Enum.forString(depSubstateName));

			depStateName = substateToState.get(depSubstateName);
			dependencyItem.getJsDependencyRule().setStateName(StateName.Enum.forString(depStateName));
		}

		if (dependencyType.equals(SUBSTATE)) {
			dependencyItem.getJsDependencyRule().setSubstateName(SubstateName.Enum.forString(depSubstateName));

			depStateName = substateToState.get(depSubstateName);
			dependencyItem.getJsDependencyRule().setStateName(StateName.Enum.forString(depStateName));
		}

		if (dependencyType.equals(STATE)) {
			dependencyItem.getJsDependencyRule().setStateName(StateName.Enum.forString(depStateName));
		}

		if (jobProperties.getDependencyList() == null || jobProperties.getDependencyList().sizeOfItemArray() == 0) {
			jobProperties.setDependencyList(DependencyList.Factory.newInstance());
		}

		Item newItem = jobProperties.getDependencyList().addNewItem();
		
		String depJobName = draggedWsNode.getName();
		String depJobId = draggedWsNode.getId();
		SelectItem item = new SelectItem();
		item.setLabel(depJobName);
		
		String treePathOfCurrentJob = jobPathInScenario.replace('/', '.');
		
		if(treePathOfCurrentJob != null && treePathOfCurrentJob.endsWith(".")) {
			treePathOfCurrentJob = treePathOfCurrentJob.substring(0, treePathOfCurrentJob.length() - 1);
		}
		
		if(!(dependencyTreePath.equals(treePathOfCurrentJob))) {
			// Global dependency
			dependencyItem.setJsPath(dependencyTreePath);
			item.setValue(dependencyTreePath + "." + depJobId);
		} else {
			item.setValue(depJobId);
		}

		newItem.set(dependencyItem);

		composeDependencyExpression();
		
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

		if (dependencyType.equals(STATUS)) {
			if (depStatusName == null || depStatusName.equals("")) {
				addMessage("addDependency", FacesMessage.SEVERITY_ERROR, "tlos.validation.job.status.choose", null);
				validationValue = false;
			}

		} else if (dependencyType.equals(SUBSTATE)) {
			if (depSubstateName == null || depSubstateName.equals("")) {
				addMessage("addDependency", FacesMessage.SEVERITY_ERROR, "tlos.validation.job.subState.choose", null);
				validationValue = false;
			}

		} else if (dependencyType.equals(STATE)) {
			if (depStateName == null || depStateName.equals("")) {
				addMessage("addDependency", FacesMessage.SEVERITY_ERROR, "tlos.validation.job.state.choose", null);
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

		if (dependencyType.equals(STATUS)) {
			dependencyItem.getJsDependencyRule().setStatusName(StatusName.Enum.forString(depStatusName));

			depSubstateName = statusToSubstate.get(depStatusName);
			dependencyItem.getJsDependencyRule().setSubstateName(SubstateName.Enum.forString(depSubstateName));

			depStateName = substateToState.get(depSubstateName);
			dependencyItem.getJsDependencyRule().setStateName(StateName.Enum.forString(depStateName));
		}

		if (dependencyType.equals(SUBSTATE)) {
			dependencyItem.getJsDependencyRule().setSubstateName(SubstateName.Enum.forString(depSubstateName));

			depStateName = substateToState.get(depSubstateName);
			dependencyItem.getJsDependencyRule().setStateName(StateName.Enum.forString(depStateName));
		}

		if (dependencyType.equals(STATE)) {
			dependencyItem.getJsDependencyRule().setStateName(StateName.Enum.forString(depStateName));
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
				String depPathAndName = jobProperties.getDependencyList().getItemArray(k).getJsPath() + "." + jobProperties.getDependencyList().getItemArray(k).getJsId();

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
			String depPathAndName = dependencyList.getItemArray(i).getJsPath() + "." + dependencyList.getItemArray(i).getJsId();

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

	// private void addToScenarioStatusList(Status tmpJobStatus) {
	// ScenarioStatusList scenarioStatusList = null;
	//
	// if (scenario.getScenarioStatusList() == null || scenario.getScenarioStatusList().sizeOfScenarioStatusArray() == 0) {
	// scenario.setScenarioStatusList(ScenarioStatusList.Factory.newInstance());
	//
	// scenarioStatusList = scenario.getScenarioStatusList();
	//
	// tmpJobStatus.setStsId("1");
	// } else {
	// scenarioStatusList = scenario.getScenarioStatusList();
	//
	// int lastStatusIndex = scenarioStatusList.sizeOfScenarioStatusArray() - 1;
	// String id = scenarioStatusList.getScenarioStatusArray(lastStatusIndex).getStsId();
	//
	// tmpJobStatus.setStsId((Integer.parseInt(id) + 1) + "");
	// }
	//
	// Status newStatus = scenarioStatusList.addNewScenarioStatus();
	// newStatus.set(tmpJobStatus);
	// }

	public void closeJobStatusDialogAction() {
		getStateInfosTabBean().setStatusDialogShow(false);
	}

	public void deleteStatusAction() {
		for (int i = 0; i < getStateInfosTabBean().getSelectedJobStatusList().length; i++) {
			for (int j = 0; j < getStateInfosTabBean().getManyJobStatusList().size(); j++) {
				if (getStateInfosTabBean().getManyJobStatusList().get(j).getValue().equals(getStateInfosTabBean().getSelectedJobStatusList()[i])) {

					JobStatusList jobStatusList = jobProperties.getStateInfos().getJobStatusList();

					for (int k = 0; k < jobStatusList.sizeOfJobStatusArray(); k++) {
						if (getStateInfosTabBean().getManyJobStatusList().get(j).getValue().equals(jobStatusList.getJobStatusArray(k).getStatusName().toString())) {
							jobStatusList.removeJobStatus(k);
							k = jobStatusList.sizeOfJobStatusArray();
						}
					}
					getStateInfosTabBean().getManyJobStatusList().remove(j);
					j = getStateInfosTabBean().getManyJobStatusList().size();
				}
			}
		}
	}

	public void jobStatusEditAction() {

		Status[] statusArray = null;

		if (jobProperties != null) {
			statusArray = jobProperties.getStateInfos().getJobStatusList().getJobStatusArray();
			getStateInfosTabBean().jobStatusEditAction(statusArray);
		}

	}

	public void updateJobStatusAction() {

		Status[] statusArray = null;

		if (jobProperties != null) {
			statusArray = jobProperties.getStateInfos().getJobStatusList().getJobStatusArray();
			getStateInfosTabBean().updateJobStatusAction(statusArray);
		}

	}

	// işe sağ tıklayarak sil dediğimizde buraya geliyor
	public boolean deleteJob() {
		boolean result = true;
		if (getDbOperations().deleteJob(getWebAppUser().getId(), getDocumentId(), DefinitionUtils.getTreePath(jobPathInScenario), getJobPropertiesXML())) {
			jSTree.removeJobNode(jobPathInScenario, jobProperties.getID());
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

	public void copyJob(String fromTree) {
		JSBuffer jsBuffer = new JSBuffer();
		jsBuffer.setJob(true);
		jsBuffer.setFromTree(fromTree);
		jsBuffer.setJsId(jobProperties.getID());
		jsBuffer.setJsName(jobProperties.getBaseJobInfos().getJsName());

		getSessionMediator().setJsBuffer(jsBuffer);
	}

	public JobProperties getJobProperties() {
		return jobProperties;
	}

	public void setJobProperties(JobProperties jobProperties) {
		this.jobProperties = jobProperties;
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

	public String getJobPathInScenario() {
		return jobPathInScenario;
	}

	public void setJobPathInScenario(String jobPathInScenario) {
		this.jobPathInScenario = jobPathInScenario;
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
		return getJobStateNameList();
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
		return getJobSubStateNameList();
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

	public BaseJobInfosTabBean getBaseJobInfosTabBean() {
		return baseJobInfosTabBean;
	}

	public EnvVariablesTabBean getEnvVariablesTabBean() {
		return envVariablesTabBean;
	}

	public StateInfosTabBean getStateInfosTabBean() {
		return stateInfosTabBean;
	}

	public String getJsId() {
		return jsId;
	}

	public void setJsId(String jsId) {
		this.jsId = jsId;
	}

	public String getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(String scenarioId) {
		this.scenarioId = scenarioId;
	}

	public HashMap<String, String> getStatusToSubstate() {
		return statusToSubstate;
	}

	public void setStatusToSubstate(HashMap<String, String> statusToSubstate) {
		this.statusToSubstate = statusToSubstate;
	}

	public HashMap<String, String> getSubstateToState() {
		return substateToState;
	}

	public void setSubstateToState(HashMap<String, String> substateToState) {
		this.substateToState = substateToState;
	}

}

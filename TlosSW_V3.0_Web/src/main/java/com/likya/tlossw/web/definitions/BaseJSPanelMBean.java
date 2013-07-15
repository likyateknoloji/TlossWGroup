package com.likya.tlossw.web.definitions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;

import org.apache.xmlbeans.XmlCursor;
import org.primefaces.component.datatable.DataTable;

import com.likya.tlos.model.xmlbeans.common.InParamDocument.InParam;
import com.likya.tlos.model.xmlbeans.common.LocalParametersDocument.LocalParameters;
import com.likya.tlos.model.xmlbeans.data.AlarmPreferenceDocument.AlarmPreference;
import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.OSystemDocument.OSystem;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.state.JobStatusListDocument.JobStatusList;
import com.likya.tlos.model.xmlbeans.state.ReturnCodeDocument.ReturnCode;
import com.likya.tlos.model.xmlbeans.state.ReturnCodeListDocument.ReturnCodeList;
import com.likya.tlos.model.xmlbeans.state.ReturnCodeListDocument.ReturnCodeList.OsType;
import com.likya.tlos.model.xmlbeans.state.ScenarioStatusListDocument.ScenarioStatusList;
import com.likya.tlos.model.xmlbeans.state.Status;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.appmng.TraceBean;
import com.likya.tlossw.web.definitions.helpers.AdvancedJobInfosTab;
import com.likya.tlossw.web.definitions.helpers.LocalParametersTabBean;
import com.likya.tlossw.web.definitions.helpers.LogAnalyzingTabBean;
import com.likya.tlossw.web.definitions.helpers.TimeManagementTabBean;
import com.likya.tlossw.web.utils.WebInputUtils;

public class BaseJSPanelMBean extends TlosSWBaseBean {

	/**
	 * Anlamları ile ilgili kısa bir açıklamada fayda var.
	 */
	public static final String NEW_NAME = "0";
	public static final String DUPLICATE_NAME_AND_PATH = "1";
	public static final String INNER_DUPLICATE_NAME = "2";
	public static final String OUTER_DUPLICATE_NAME = "3";

	private boolean isScenario = false;
	private boolean jsActive = false;

	private boolean jsNameConfirmDialog = false;
	private boolean innerJsNameDuplicate = false;

	private boolean jsInsertButton = false;
	private boolean jsUpdateButton = false;

	private String jsCalendar;

	private Status jobStatus;
	private String jobStatusName;
	private Collection<SelectItem> jobStatusNameList = null;

	private Collection<SelectItem> oSystemList = null;

	/* jsStatusPopup */
	private boolean statusDialogShow = false;

	private List<SelectItem> manyJobStatusList;
	private String[] selectedJobStatusList;

	// concurrencyManagement
	private boolean concurrent;

	// alarmPreference
	private Collection<SelectItem> alarmList = null;
	private String[] selectedAlarmList;

	private ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
	private transient DataTable parameterTable;

	private List<SelectItem> manyJobDependencyList = new ArrayList<SelectItem>();
	private String dependencyExpression;

	private List<SelectItem> manyReturnCodeList;

	private String osType;

	private ReturnCode returnCode;

	private Collection<SelectItem> jsCalendarList = null;

	private TimeManagementTabBean timeManagementTabBean;
	private LocalParametersTabBean localParametersTabBean;
	private LogAnalyzingTabBean logAnalyzingTabBean;
	private AdvancedJobInfosTab advancedJobInfosTab;

	public void init() {
		timeManagementTabBean = new TimeManagementTabBean(isScenario);
		logAnalyzingTabBean = new LogAnalyzingTabBean();
		localParametersTabBean = new LocalParametersTabBean(this);
		advancedJobInfosTab = new AdvancedJobInfosTab(getDbOperations());
	}

	public void switchInsertUpdateButtons() {
		jsInsertButton = !jsInsertButton;
		jsUpdateButton = !jsUpdateButton;
	}

	public void fillAllLists() {

		long startTime = System.currentTimeMillis();

		getTimeManagementTabBean().fillTab();

		setJsCalendarList(WebInputUtils.fillCalendarList(getDbOperations().getCalendars()));
		System.out.println("BaseJSPanelMBean.WebInputUtils.fillCalendarList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");

		getTimeManagementTabBean().setTypeOfTimeList(WebInputUtils.fillTypesOfTimeList());
		System.out.println("JobBaseBean.WebInputUtils.fillTypesOfTimeList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();

		getAdvancedJobInfosTab().fillTab();

		System.out.println();

	}

	/**
	 * xsd yapsında da scenaryo ve job nesnelerinin ortak alanları bir üst nesene de tanımlı olsaydı
	 * isScenario bilgisnin geçmesine gerek olmayacak.
	 * 
	 * @date 14.07.2013
	 * @author serkan taş
	 * @param isScenario
	 * @param refObject
	 */
	protected void fillAlarmPreference(boolean isScenario, Object refObject) {

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
				((Scenario) refObject).setAlarmPreference(alarmPreference);
			} else {
				((JobProperties) refObject).setAlarmPreference(alarmPreference);
			}
		} else {
			if (isScenario) {
				if (((Scenario) refObject).getAlarmPreference() != null) {
					XmlCursor xmlCursor = ((Scenario) refObject).getAlarmPreference().newCursor();
					xmlCursor.removeXml();
				}
			} else if (((JobProperties) refObject).getAlarmPreference() != null) {
				XmlCursor xmlCursor = ((JobProperties) refObject).getAlarmPreference().newCursor();
				xmlCursor.removeXml();
			}
		}
	}

	public void fillAlarmPreferenceTab(boolean isScenario, Object refObject) {

		AlarmPreference alarmPreference = null;

		if (isScenario) {

			Scenario scenario = ((Scenario) refObject);

			if (scenario != null && scenario.getAlarmPreference() != null) {
				alarmPreference = scenario.getAlarmPreference();
			}

		} else {

			JobProperties jobProperties = ((JobProperties) refObject);

			if (jobProperties != null && jobProperties.getAlarmPreference() != null) {
				alarmPreference = jobProperties.getAlarmPreference();
			}

		}

		if (alarmPreference != null && alarmPreference.getAlarmIdArray() != null && alarmPreference.getAlarmIdArray().length > 0) {
			int length = alarmPreference.getAlarmIdArray().length;
			selectedAlarmList = new String[length];

			for (int i = 0; i < length; i++) {
				selectedAlarmList[i] = alarmPreference.getAlarmIdArray(i) + "";
			}
		}
	}

	protected void fillLocalParameters(boolean isScenario, Object refObject) {

		if (parameterList.size() > 0) {
			LocalParameters localParameters = LocalParameters.Factory.newInstance();

			InParam inParam = InParam.Factory.newInstance();
			localParameters.setInParam(inParam);

			for (int i = 0; i < parameterList.size(); i++) {
				Parameter parameter = localParameters.getInParam().addNewParameter();
				parameter.set(parameterList.get(i));
			}

			if (isScenario) {
				((Scenario) refObject).setLocalParameters(localParameters);
			} else {
				((JobProperties) refObject).setLocalParameters(localParameters);
			}

		} else {
			if (isScenario) {
				Scenario scenario = ((Scenario) refObject);
				if (scenario.getLocalParameters() != null) {
					XmlCursor xmlCursor = scenario.getLocalParameters().newCursor();
					xmlCursor.removeXml();
				}
			} else {
				JobProperties jobProperties = ((JobProperties) refObject);
				if (jobProperties.getLocalParameters() != null) {
					XmlCursor xmlCursor = jobProperties.getLocalParameters().newCursor();
					xmlCursor.removeXml();
				}
			}
		}
	}

	public void fillLocalParametersTab(boolean isScenario, Object refObject) {

		LocalParameters localParameters = null;

		if (isScenario) {
			Scenario scenario = ((Scenario) refObject);
			if (scenario != null && scenario.getLocalParameters() != null) {
				localParameters = scenario.getLocalParameters();
			}
		} else {
			JobProperties jobProperties = ((JobProperties) refObject);
			if (jobProperties != null && jobProperties.getLocalParameters() != null) {
				localParameters = jobProperties.getLocalParameters();
			}
		}

		if (localParameters != null) {
			if (localParameters != null && localParameters.getInParam() != null) {
				InParam inParam = localParameters.getInParam();

				for (Parameter parameter : inParam.getParameterArray()) {
					parameterList.add(parameter);
				}
			} else {
				parameterList = new ArrayList<Parameter>();
			}
		}
	}

	public void fillDependencyDefinitionsTab(DependencyList dependencyList) {

		if (dependencyList != null && dependencyList.sizeOfItemArray() > 0) {
			for (Item item : dependencyList.getItemArray()) {
				String depPathAndName = item.getJsPath() + "." + item.getJsName();

				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(item.getJsName());
				selectItem.setValue(depPathAndName);

				/*
				 * bir iş aynı isimli ama farklı pathlerdeki iki işe bağımlı olarak
				 * tanımlanabiliyor, db ye de kaydediliyor ama aşağıdaki kontrol olunca güncellemek
				 * istendiğinde sadece bir tanesi görüntülendiği için commentledim.
				 * 
				 * boolean var = false; for (SelectItem temp : manyJobDependencyList) { if
				 * (temp.getLabel().equals(selectItem.getLabel())) var = true; }
				 * 
				 * if (!var) manyJobDependencyList.add(selectItem);
				 */

				manyJobDependencyList.add(selectItem);
			}

			dependencyExpression = dependencyList.getDependencyExpression();
		}
	}

	public void updateJobStatusAction(Status[] statusArray) {

		for (Status jStatus : statusArray) {
			if (jobStatus.getStatusName().toString().equals(jStatus.getStatusName().toString())) {
				jStatus = WebInputUtils.cloneJobStatus(jobStatus);

				addMessage("addReturnCode", FacesMessage.SEVERITY_INFO, "tlos.info.job.code.update", null);

				break;
			}
		}

		statusDialogShow = false;
	}

	public void jobStatusEditAction(Status[] statusArray) {

		if (selectedJobStatusList == null || selectedJobStatusList.length == 0) {
			addMessage("addReturnCode", FacesMessage.SEVERITY_ERROR, "tlos.info.job.status.choose", null);
			return;
		} else if (selectedJobStatusList.length > 1) {
			addMessage("addReturnCode", FacesMessage.SEVERITY_ERROR, "tlos.info.job.status.choose.one", null);
			return;
		}

		for (Status status : statusArray) {
			if (status.getStatusName().toString().equals(selectedJobStatusList[0])) {
				jobStatus = WebInputUtils.cloneJobStatus(status);
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

	public boolean checkDuplicateStateName() {
		if (getManyJobStatusList() != null) {

			for (int i = 0; i < getManyJobStatusList().size(); i++) {

				if (getManyJobStatusList().get(i).getValue().equals(jobStatusName)) {
					addMessage("addReturnCode", FacesMessage.SEVERITY_ERROR, "tlos.info.job.status.duplicate", null);

					return true;
				}
			}
		}

		return false;
	}

	public void addJReturnCodeAction(boolean isScenario, Object refObject) {

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

		ReturnCode tmpReturnCode = WebInputUtils.cloneReturnCode(getReturnCode());

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
				ScenarioStatusList scenarioStatusList = (ScenarioStatusList) refObject;
				// hazirlanan status nesnesi scenariostatusList icine koyuluyor
				for (int i = 0; i < scenarioStatusList.sizeOfScenarioStatusArray(); i++) {
					if (scenarioStatusList.getScenarioStatusArray(i).getStatusName().equals(jobStatus.getStatusName())) {
						scenarioStatusList.getScenarioStatusArray(i).set(jobStatus);
					}
				}
			} else {
				JobStatusList jobStatusList = (JobStatusList) refObject;
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
			ScenarioStatusList scenarioStatusList = (ScenarioStatusList) refObject;
			if (scenarioStatusList == null || scenarioStatusList.sizeOfScenarioStatusArray() == 0) {
				scenarioStatusList = ScenarioStatusList.Factory.newInstance();

				Status status = scenarioStatusList.addNewScenarioStatus();
				status.set(jobStatus);
			}
		} else {
			JobStatusList jobStatusList = (JobStatusList) refObject;
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

	public void resetPanelInputs() {

		jsCalendar = "0";

		getTimeManagementTabBean().resetTab();

		returnCode = ReturnCode.Factory.newInstance();

		jobStatus = Status.Factory.newInstance();
		jobStatusName = "";
		manyJobStatusList = new ArrayList<SelectItem>();

		getAdvancedJobInfosTab().resetTab();

		manyJobDependencyList = new ArrayList<SelectItem>();
		dependencyExpression = "";

		selectedAlarmList = null;

		parameterList = new ArrayList<Parameter>();
	}

	public boolean isScenario() {
		return isScenario;
	}

	public void setScenario(boolean isScenario) {
		this.isScenario = isScenario;
	}

	public boolean isJsInsertButton() {
		return jsInsertButton;
	}

	public void setJsInsertButton(boolean jsInsertButton) {
		this.jsInsertButton = jsInsertButton;
	}

	public boolean isJsUpdateButton() {
		return jsUpdateButton;
	}

	public void setJsUpdateButton(boolean jsUpdateButton) {
		this.jsUpdateButton = jsUpdateButton;
	}

	public boolean isJsActive() {
		return jsActive;
	}

	public void setJsActive(boolean jsActive) {
		this.jsActive = jsActive;
	}

	public String getJsCalendar() {
		return jsCalendar;
	}

	public void setJsCalendar(String jsCalendar) {
		this.jsCalendar = jsCalendar;
	}

	public boolean isConcurrent() {
		return concurrent;
	}

	public void setConcurrent(boolean concurrent) {
		this.concurrent = concurrent;
	}

	public boolean isJsNameConfirmDialog() {
		return jsNameConfirmDialog;
	}

	public void setJsNameConfirmDialog(boolean jsNameConfirmDialog) {
		this.jsNameConfirmDialog = jsNameConfirmDialog;
	}

	public List<SelectItem> getManyJobStatusList() {
		return manyJobStatusList;
	}

	public void setManyJobStatusList(List<SelectItem> manyJobStatusList) {
		this.manyJobStatusList = manyJobStatusList;
	}

	public boolean isInnerJsNameDuplicate() {
		return innerJsNameDuplicate;
	}

	public void setInnerJsNameDuplicate(boolean innerJsNameDuplicate) {
		this.innerJsNameDuplicate = innerJsNameDuplicate;
	}

	public String[] getSelectedJobStatusList() {
		return selectedJobStatusList;
	}

	public void setSelectedJobStatusList(String[] selectedJobStatusList) {
		this.selectedJobStatusList = selectedJobStatusList;
	}

	public void setAlarmList(Collection<SelectItem> alarmList) {
		this.alarmList = alarmList;
	}

	public String getJobStatusName() {
		return jobStatusName;
	}

	public void setJobStatusName(String jobStatusName) {
		this.jobStatusName = jobStatusName;
	}

	public boolean isStatusDialogShow() {
		return statusDialogShow;
	}

	public void setStatusDialogShow(boolean statusDialogShow) {
		this.statusDialogShow = statusDialogShow;
	}

	public String[] getSelectedAlarmList() {
		return selectedAlarmList;
	}

	public void setSelectedAlarmList(String[] selectedAlarmList) {
		this.selectedAlarmList = selectedAlarmList;
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

	public List<SelectItem> getManyJobDependencyList() {
		return manyJobDependencyList;
	}

	public void setManyJobDependencyList(List<SelectItem> manyJobDependencyList) {
		this.manyJobDependencyList = manyJobDependencyList;
	}

	public String getDependencyExpression() {
		return dependencyExpression;
	}

	public void setDependencyExpression(String dependencyExpression) {
		this.dependencyExpression = dependencyExpression;
	}

	public List<SelectItem> getManyReturnCodeList() {
		return manyReturnCodeList;
	}

	public void setManyReturnCodeList(List<SelectItem> manyReturnCodeList) {
		this.manyReturnCodeList = manyReturnCodeList;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public ReturnCode getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(ReturnCode returnCode) {
		this.returnCode = returnCode;
	}

	public Collection<SelectItem> getAlarmList() {
		return alarmList;
	}

	public Status getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(Status jobStatus) {
		this.jobStatus = jobStatus;
	}

	public Collection<SelectItem> getJsCalendarList() {
		return jsCalendarList;
	}

	public void setJsCalendarList(Collection<SelectItem> jsCalendarList) {
		this.jsCalendarList = jsCalendarList;
	}

	public Collection<SelectItem> getJobStatusNameList() {
		return jobStatusNameList;
	}

	public void setJobStatusNameList(Collection<SelectItem> jobStatusNameList) {
		this.jobStatusNameList = jobStatusNameList;
	}

	public Collection<SelectItem> getoSystemList() {
		return oSystemList;
	}

	public void setoSystemList(Collection<SelectItem> oSystemList) {
		this.oSystemList = oSystemList;
	}

	public LogAnalyzingTabBean getLogAnalyzingTabBean() {
		return logAnalyzingTabBean;
	}

	public TimeManagementTabBean getTimeManagementTabBean() {
		return timeManagementTabBean;
	}

	public LocalParametersTabBean getLocalParametersTabBean() {
		return localParametersTabBean;
	}

	public AdvancedJobInfosTab getAdvancedJobInfosTab() {
		return advancedJobInfosTab;
	}

	// public int getGmt() {
	// return gmt;
	// }
	//
	// public void setGmt(int gmt) {
	// this.gmt = gmt;
	// }
	//
	// public boolean isDst() {
	// return dst;
	// }
	//
	// public void setDst(boolean dst) {
	// this.dst = dst;
	// }

}

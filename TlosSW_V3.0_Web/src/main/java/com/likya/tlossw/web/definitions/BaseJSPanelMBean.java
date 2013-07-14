package com.likya.tlossw.web.definitions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.xmlbeans.XmlCursor;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

import com.likya.tlos.model.xmlbeans.common.AgentChoiceMethodDocument.AgentChoiceMethod;
import com.likya.tlos.model.xmlbeans.common.InParamDocument.InParam;
import com.likya.tlos.model.xmlbeans.common.LocalParametersDocument.LocalParameters;
import com.likya.tlos.model.xmlbeans.common.TypeOfTimeDocument.TypeOfTime;
import com.likya.tlos.model.xmlbeans.common.UnitDocument.Unit;
import com.likya.tlos.model.xmlbeans.data.AlarmPreferenceDocument.AlarmPreference;
import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.ExpectedTimeDocument.ExpectedTime;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.JsPlannedTimeDocument.JsPlannedTime;
import com.likya.tlos.model.xmlbeans.data.JsRelativeTimeOptionDocument.JsRelativeTimeOption;
import com.likya.tlos.model.xmlbeans.data.JsTimeOutDocument.JsTimeOut;
import com.likya.tlos.model.xmlbeans.data.OSystemDocument.OSystem;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.data.StartTimeDocument.StartTime;
import com.likya.tlos.model.xmlbeans.data.StopTimeDocument.StopTime;
import com.likya.tlos.model.xmlbeans.data.TimeManagementDocument.TimeManagement;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.parameters.PreValueDocument.PreValue;
import com.likya.tlos.model.xmlbeans.state.JobStatusListDocument.JobStatusList;
import com.likya.tlos.model.xmlbeans.state.ReturnCodeDocument.ReturnCode;
import com.likya.tlos.model.xmlbeans.state.ReturnCodeListDocument.ReturnCodeList;
import com.likya.tlos.model.xmlbeans.state.ReturnCodeListDocument.ReturnCodeList.OsType;
import com.likya.tlos.model.xmlbeans.state.ScenarioStatusListDocument.ScenarioStatusList;
import com.likya.tlos.model.xmlbeans.state.Status;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.appmng.TraceBean;
import com.likya.tlossw.web.definitions.helpers.LogAnalyzingTabBean;
import com.likya.tlossw.web.utils.DefinitionUtils;
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

	// time management
	private boolean useTimeManagement = false;

	private String startTime;

	private boolean defineStopTime = false;
	private String stopTime;

	private Collection<SelectItem> tzList;
	private String selectedTZone;

	private Collection<SelectItem> relativeTimeOptionList = null;
	private String relativeTimeOption;

	private String jobTimeOutValue;
	private String jobTimeOutUnit;

	private String expectedTime;
	private String expectedTimeUnit;

	private String tolerancePercentage;
	private String minPercentage;

	private String selectedTypeOfTime;

	// alarmPreference
	private Collection<SelectItem> alarmList = null;
	private String[] selectedAlarmList;

	private ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
	private transient DataTable parameterTable;

	// advancedJobInfos
	private Collection<SelectItem> agentChoiceMethodList = null;
	private String agentChoiceMethod;

	private Collection<SelectItem> definedAgentList = null;
	private String selectedAgent;

	private List<SelectItem> manyJobDependencyList = new ArrayList<SelectItem>();
	private String dependencyExpression;
	
	private List<SelectItem> manyReturnCodeList;
	
	private String osType;
	
	private ReturnCode returnCode;
	
	private Collection<SelectItem> jsCalendarList = null;
	
	
	// localParameters
	private String paramName;
	private String paramDesc;
	private String paramType;
	private String paramPreValue;
	private String selectedParamName;
	
	private boolean renderUpdateParamButton = false;

	private LogAnalyzingTabBean logAnalyzingTabBean;
	
	
	public void init() {
		logAnalyzingTabBean = new LogAnalyzingTabBean();
	}
	
	public void switchInsertUpdateButtons() {
		jsInsertButton = !jsInsertButton;
		jsUpdateButton = !jsUpdateButton;
	}
	
	private void resetInputParameterFields() {
		paramName = "";
		paramDesc = "";
		paramPreValue = "";
		paramType = "";
	}
	
	public void editInputParamAction(ActionEvent e) {
		Parameter inParam = (Parameter) getParameterTable().getRowData();

		paramName = new String(inParam.getName());
		paramDesc = new String(inParam.getDesc());
		paramPreValue = new String(inParam.getPreValue().getStringValue());
		paramType = new String(inParam.getPreValue().getType().toString());

		selectedParamName = paramName;

		renderUpdateParamButton = true;

		RequestContext context = RequestContext.getCurrentInstance();
		context.update("jobDefinitionForm:tabView:parametersPanel");
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

		getParameterList().add(parameter);

		resetInputParameterFields();
	}
	
	public void deleteInputParamAction(ActionEvent e) {
		int parameterIndex = getParameterTable().getRowIndex();
		getParameterList().remove(parameterIndex);

		renderUpdateParamButton = false;

		RequestContext context = RequestContext.getCurrentInstance();
		context.update("jobDefinitionForm:tabView:parametersPanel");
	}

	public void updateInputParameter() {
		for (int i = 0; i < getParameterList().size(); i++) {

			if (selectedParamName.equals(getParameterList().get(i).getName())) {
				getParameterList().get(i).setName(paramName);
				getParameterList().get(i).setDesc(paramDesc);

				PreValue preValue = PreValue.Factory.newInstance();
				preValue.setStringValue(paramPreValue);
				preValue.setType(new BigInteger(paramType));
				getParameterList().get(i).setPreValue(preValue);

				break;
			}
		}

		resetInputParameterFields();

		renderUpdateParamButton = false;
	}
	
	public void fillAllLists() {
		
		long startTime = System.currentTimeMillis();
		
		setJsCalendarList(WebInputUtils.fillCalendarList(getDbOperations().getCalendars()));
		System.out.println("BaseJSPanelMBean.WebInputUtils.fillCalendarList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		
		System.out.println();
		
	}

	protected void fillTimeManagement(TimeManagement timeManagement) {

		// ekrandan starttime girildiyse onu set ediyor
		if (startTime != null && !startTime.equals("")) {

			if (timeManagement.getJsPlannedTime() == null) {
				JsPlannedTime jsPlannedTime = JsPlannedTime.Factory.newInstance();
				StartTime startTime = StartTime.Factory.newInstance();
				jsPlannedTime.setStartTime(startTime);
				timeManagement.setJsPlannedTime(jsPlannedTime);
			}

			JsPlannedTime jsPlannedTime = timeManagement.getJsPlannedTime();
			jsPlannedTime.getStartTime().setTime(DefinitionUtils.dateToXmlTime(startTime, selectedTZone));

			// ekrandan stoptime girildiyse onu set ediyor, bunu starttime
			// girildiyse kontrol ediyor cunku start time olmadan stop time
			// tanimi yapilmiyor
			if (defineStopTime) {
				StopTime jsStopTime = StopTime.Factory.newInstance();
				jsStopTime.setTime(DefinitionUtils.dateToXmlTime(stopTime, selectedTZone));

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
			if (timeManagement.getJsTimeOut() == null) {
				JsTimeOut jsTimeOut = JsTimeOut.Factory.newInstance();
				timeManagement.setJsTimeOut(jsTimeOut);
			}
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

		timeManagement.setTimeZone(selectedTZone);
		timeManagement.setTypeOfTime(TypeOfTime.Enum.forString(selectedTypeOfTime));

		System.out.println("nedir" + timeManagement.toString());
	}

	public void fillTimeManagementTab(TimeManagement timeManagement) {

		if (timeManagement != null && timeManagement.getJsTimeOut() != null) {
			useTimeManagement = true;

			if (timeManagement.getJsPlannedTime() != null && timeManagement.getJsPlannedTime().getStartTime() != null) {
				Calendar jobStartTime = timeManagement.getJsPlannedTime().getStartTime().getTime();

				// DateTimeZone zone = DateTimeZone.forID(selectedTZone);
				// DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss.SSSZZ");
				// LocalTime localStartTime = dtf.parseLocalTime(jobStartTime);
				//
				// DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm:ss");
				// String jobLocalStartTime = localStartTime.toDateTimeToday(zone).toString(formatter);

				String timeOutputFormat = new String("HH:mm:ss.SSS");
				startTime = DefinitionUtils.calendarToStringTimeFormat(jobStartTime, selectedTZone, timeOutputFormat);
				// startTime = jobLocalTime.toString();

				if (timeManagement.getJsPlannedTime().getStopTime() != null) {
					defineStopTime = true;

					Calendar jobStopTime = timeManagement.getJsPlannedTime().getStopTime().getTime();
					// LocalTime jobLocalStopTime = new LocalTime( jobStopTime);
					stopTime = DefinitionUtils.calendarToStringTimeFormat(jobStopTime, selectedTZone, timeOutputFormat);
					// stopTime = jobLocalStopTime.toString();
					// LocalTime localStopTime = dtf.parseLocalTime(jobStopTime);
					//
					// String jobLocalStopTime = localStopTime.toDateTimeToday(zone).toString(formatter);

				}
			}

			if (timeManagement.getJsRelativeTimeOption() != null) {
				relativeTimeOption = timeManagement.getJsRelativeTimeOption().toString();
			}

			JsTimeOut timeOut = timeManagement.getJsTimeOut();

			if (timeOut.getValueInteger() != null) {
				jobTimeOutValue = timeOut.getValueInteger() + "";
			}
			if (timeOut.getUnit() != null) {
				jobTimeOutUnit = timeOut.getUnit().toString();
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

			if (timeManagement.getTimeZone() != null)
				selectedTZone = timeManagement.getTimeZone();

			if (timeManagement.getTypeOfTime() != null)
				selectedTypeOfTime = timeManagement.getTypeOfTime().toString();

		} else {
			useTimeManagement = false;
		}
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

		startTime = "";
		defineStopTime = false;
		stopTime = "";
		// gmt = 0;
		// dst = false;
		selectedTZone = new String("Europe/Istanbul");
		selectedTypeOfTime = new String("Actual");
		relativeTimeOption = JsRelativeTimeOption.NO.toString();
		jobTimeOutValue = "";
		jobTimeOutUnit = Unit.HOURS.toString();
		expectedTime = "";
		expectedTimeUnit = Unit.HOURS.toString();
		tolerancePercentage = "10";
		minPercentage = "10";

		returnCode = ReturnCode.Factory.newInstance();

		jobStatus = Status.Factory.newInstance();
		jobStatusName = "";
		manyJobStatusList = new ArrayList<SelectItem>();

		agentChoiceMethod = AgentChoiceMethod.SIMPLE_METASCHEDULER.toString();

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

	public boolean isUseTimeManagement() {
		return useTimeManagement;
	}

	public void setUseTimeManagement(boolean useTimeManagement) {
		this.useTimeManagement = useTimeManagement;
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

	public String getSelectedAgent() {
		return selectedAgent;
	}

	public void setSelectedAgent(String selectedAgent) {
		this.selectedAgent = selectedAgent;
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

	public Collection<SelectItem> getRelativeTimeOptionList() {
		return relativeTimeOptionList;
	}

	public void setRelativeTimeOptionList(Collection<SelectItem> relativeTimeOptionList) {
		this.relativeTimeOptionList = relativeTimeOptionList;
	}

	public String[] getSelectedJobStatusList() {
		return selectedJobStatusList;
	}

	public void setSelectedJobStatusList(String[] selectedJobStatusList) {
		this.selectedJobStatusList = selectedJobStatusList;
	}

	public String getSelectedTZone() {
		return selectedTZone;
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

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public boolean isDefineStopTime() {
		return defineStopTime;
	}

	public void setDefineStopTime(boolean defineStopTime) {
		this.defineStopTime = defineStopTime;
	}

	public String getStopTime() {
		return stopTime;
	}

	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
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

	public String getSelectedTypeOfTime() {
		return selectedTypeOfTime;
	}

	public void setSelectedTypeOfTime(String selectedTypeOfTime) {
		this.selectedTypeOfTime = selectedTypeOfTime;
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

	public Collection<SelectItem> getDefinedAgentList() {
		return definedAgentList;
	}

	public void setDefinedAgentList(Collection<SelectItem> definedAgentList) {
		this.definedAgentList = definedAgentList;
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

	public void setSelectedTZone(String selectedTZone) {
		this.selectedTZone = selectedTZone;
	}

	public Collection<SelectItem> getTzList() {
		return tzList;
	}

	public void setTzList(Collection<SelectItem> tzList) {
		this.tzList = tzList;
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

	public String getSelectedParamName() {
		return selectedParamName;
	}

	public void setSelectedParamName(String selectedParamName) {
		this.selectedParamName = selectedParamName;
	}

	public boolean isRenderUpdateParamButton() {
		return renderUpdateParamButton;
	}

	public void setRenderUpdateParamButton(boolean renderUpdateParamButton) {
		this.renderUpdateParamButton = renderUpdateParamButton;
	}

	public boolean getAgentChoiceMethodUserMandatoryPreference() {
		return "UserMandatoryPreference".equals(agentChoiceMethod);
	}

	public LogAnalyzingTabBean getLogAnalyzingTabBean() {
		return logAnalyzingTabBean;
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

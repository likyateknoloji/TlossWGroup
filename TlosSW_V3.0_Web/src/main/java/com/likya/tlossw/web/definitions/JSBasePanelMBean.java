package com.likya.tlossw.web.definitions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import com.likya.tlos.model.xmlbeans.common.JobBaseTypeDocument.JobBaseType;
import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.data.OSystemDocument.OSystem;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.appmng.TraceBean;
import com.likya.tlossw.web.definitions.helpers.AdvancedJobInfosTab;
import com.likya.tlossw.web.definitions.helpers.AlarmPreferencesTabBean;
import com.likya.tlossw.web.definitions.helpers.LocalParametersTabBean;
import com.likya.tlossw.web.definitions.helpers.LogAnalyzingTabBean;
import com.likya.tlossw.web.definitions.helpers.TimeManagementTabBean;
import com.likya.tlossw.web.utils.WebInputUtils;

public class JSBasePanelMBean extends TlosSWBaseBean {

	private Collection<SelectItem> tzList;
	private Collection<SelectItem> typeOfTimeList;
	private Collection<SelectItem> alarmList;
	private Collection<SelectItem> definedAgentList;
	private Collection<SelectItem> jsSLAList;
	private Collection<SelectItem> resourceNameList;
	private Collection<SelectItem> jobBaseTypeList;
	private Collection<SelectItem> eventTypeDefList;
	private Collection<SelectItem> jobTypeDefList;
	private Collection<SelectItem> relativeTimeOptionList;
	private Collection<SelectItem> unitTypeList;
	private Collection<SelectItem> jobStatusNameList;
	
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
	private Collection<SelectItem> jsCalendarList = null;

	// concurrencyManagement
	private boolean concurrent;


	private List<SelectItem> manyJobDependencyList = new ArrayList<SelectItem>();
	private String dependencyExpression;

	private String oSystem;
	private Collection<SelectItem> oSystemList = null;

	private String jobBaseType = JobBaseType.NON_PERIODIC.toString();

	private TimeManagementTabBean timeManagementTabBean;
	private LocalParametersTabBean localParametersTabBean;
	private LogAnalyzingTabBean logAnalyzingTabBean;
	private AdvancedJobInfosTab advancedJobInfosTab;
	private AlarmPreferencesTabBean alarmPreferencesTabBean;

	public void init() {
		timeManagementTabBean = new TimeManagementTabBean(this, isScenario);
		logAnalyzingTabBean = new LogAnalyzingTabBean();
		localParametersTabBean = new LocalParametersTabBean();
		advancedJobInfosTab = new AdvancedJobInfosTab(getDbOperations());
		alarmPreferencesTabBean = new AlarmPreferencesTabBean();
	}

	public void switchInsertUpdateButtons() {
		jsInsertButton = !jsInsertButton;
		jsUpdateButton = !jsUpdateButton;
	}

	public void fillAllLists() {

		getAdvancedJobInfosTab().fillTab();

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

	public void resetPanelInputs() {

		timeManagementTabBean.resetTab();

		advancedJobInfosTab.resetTab();

		manyJobDependencyList = new ArrayList<SelectItem>();
		dependencyExpression = "";

		jsCalendar = "0";
		oSystem = OSystem.WINDOWS.toString();

		localParametersTabBean.resetTab(true);
		alarmPreferencesTabBean.resetTab();
		
	}

	private Collection<SelectItem> constructOSystemList() {
		
		long startTime = System.currentTimeMillis();
		Collection<SelectItem> oSystemList = WebInputUtils.fillOSystemList();
		System.out.println("JobBaseBean.WebInputUtils.fillOSystemList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		return oSystemList;
	}
	
	private Collection<SelectItem> constructTzList() {
		
		long startTime = System.currentTimeMillis();
		Collection<SelectItem> tzList = WebInputUtils.fillTZList();
		System.out.println("JobBaseBean.WebInputUtils.fillTZList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		return tzList;
	}

	private Collection<SelectItem> constructJsCalendarList() {
		
		long startTime = System.currentTimeMillis();
		Collection<SelectItem> tzList = WebInputUtils.fillCalendarList(getDbOperations().getCalendars());
		System.out.println("BaseJSPanelMBean.WebInputUtils.fillCalendarList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		return tzList;
	}

	private Collection<SelectItem> constructTypeOfTimeList() {
		
		long startTime = System.currentTimeMillis();
		Collection<SelectItem> typeOfTimeList = WebInputUtils.fillTypesOfTimeList();
		System.out.println("JobBaseBean.WebInputUtils.fillTypesOfTimeList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		return typeOfTimeList;
	}

	private Collection<SelectItem> constructAlarmList() {
		
		long startTime = System.currentTimeMillis();
		Collection<SelectItem> alarmList = WebInputUtils.fillAlarmList(getDbOperations().getAlarms()); 
		System.out.println("JobBaseBean.WebInputUtils.fillAlarmList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		return alarmList;
	}
	
	private Collection<SelectItem> constructDefinedAgentList() {
		
		long startTime = System.currentTimeMillis();
		Collection<SelectItem> definedAgentList = WebInputUtils.fillAgentList(getDbOperations().getAgents()); 
		System.out.println("JobBaseBean.WebInputUtils.fillAgentList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		return definedAgentList;
	}
	
	private Collection<SelectItem> constructJsSLAList() {
		
		long startTime = System.currentTimeMillis();
		Collection<SelectItem> jsSLAList = WebInputUtils.fillSLAList(getDbOperations().getSlaList()); 
		System.out.println("JobBaseBean.WebInputUtils.fillSLAList Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		return jsSLAList;
	}
	

	private Collection<SelectItem> constructResourceNameList() {
		
		long startTime = System.currentTimeMillis();
		Collection<SelectItem> resourceNameList = WebInputUtils.fillResourceNameList(getDbOperations().getResources()); 
		System.out.println("JobBaseBean.WebInputUtils.fillResourceNameList fill things Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		return resourceNameList;
	}
	
	private Collection<SelectItem> constructJobBaseTypeList() {
		
		long startTime = System.currentTimeMillis();
		Collection<SelectItem> jobBaseTypeList = WebInputUtils.fillJobBaseTypeList(); 
		System.out.println("JobBaseBean.WebInputUtils.fillJobBaseTypeList fill things Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		return jobBaseTypeList;
	}

	private Collection<SelectItem> constructEventTypeDefList() {
		
		long startTime = System.currentTimeMillis();
		Collection<SelectItem> eventTypeDefList = WebInputUtils.fillEventTypeDefList(); 
		System.out.println("JobBaseBean.WebInputUtils.fillEventTypeDefList fill things Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		return eventTypeDefList;
	}
	
	private Collection<SelectItem> constructJobTypeDefList() {
		
		long startTime = System.currentTimeMillis();
		Collection<SelectItem> jobTypeDefList = WebInputUtils.fillJobTypeDefList(); 
		System.out.println("JobBaseBean.WebInputUtils.fillJobTypeDefList fill things Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		return jobTypeDefList;
	}
	
	private Collection<SelectItem> constructRelativeTimeOptionList() {
		
		long startTime = System.currentTimeMillis();
		Collection<SelectItem> relativeTimeOptionList = WebInputUtils.fillRelativeTimeOptionList(); 
		System.out.println("JobBaseBean.WebInputUtils.fillRelativeTimeOptionList fill things Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		return relativeTimeOptionList;
	}

	private Collection<SelectItem> constructUnitTypeList() {
		
		long startTime = System.currentTimeMillis();
		Collection<SelectItem> unitTypeList = WebInputUtils.fillUnitTypeList(); 
		System.out.println("JobBaseBean.WebInputUtils.fillUnitTypeList fill things Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		return unitTypeList;
	}

	private Collection<SelectItem> constructJobStatusNameList() {
		
		long startTime = System.currentTimeMillis();
		Collection<SelectItem> jobStatusNameList = WebInputUtils.fillJobStatusList(); 
		System.out.println("JobBaseBean.WebInputUtils.fillJobStatusList fill things Süre : " + TraceBean.dateDiffWithNow(startTime) + "ms");
		startTime = System.currentTimeMillis();
		
		return jobStatusNameList;
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

	public boolean isInnerJsNameDuplicate() {
		return innerJsNameDuplicate;
	}

	public void setInnerJsNameDuplicate(boolean innerJsNameDuplicate) {
		this.innerJsNameDuplicate = innerJsNameDuplicate;
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

	public Collection<SelectItem> getJsCalendarList() {
		if(jsCalendarList == null) {
			jsCalendarList = constructJsCalendarList();
		}
		return jsCalendarList;
	}

	public void setJsCalendarList(Collection<SelectItem> jsCalendarList) {
		this.jsCalendarList = jsCalendarList;
	}

	public String getJobBaseType() {
		return jobBaseType;
	}

	public void setJobBaseType(String jobBaseType) {
		this.jobBaseType = jobBaseType;
	}

	public String getoSystem() {
		return oSystem;
	}

	public void setoSystem(String oSystem) {
		this.oSystem = oSystem;
	}

	public Collection<SelectItem> getoSystemList() {
		if(oSystemList == null) {
			oSystemList = constructOSystemList();
		}
		return oSystemList;
	}

	public void setoSystemList(Collection<SelectItem> oSystemList) {
		this.oSystemList = oSystemList;
	}

	public AlarmPreferencesTabBean getAlarmPreferencesTabBean() {
		return alarmPreferencesTabBean;
	}

	public void setAlarmPreferencesTabBean(AlarmPreferencesTabBean alarmPreferencesTabBean) {
		this.alarmPreferencesTabBean = alarmPreferencesTabBean;
	}

	public Collection<SelectItem> getTzList() {
		if(tzList == null) {
			tzList = constructTzList();
		}
		return tzList;
	}

	public void setTzList(Collection<SelectItem> tzList) {
		this.tzList = tzList;
	}

	public Collection<SelectItem> getTypeOfTimeList() {
		if(typeOfTimeList == null) {
			typeOfTimeList = constructTypeOfTimeList();
		}
		return typeOfTimeList;
	}

	public void setTypeOfTimeList(Collection<SelectItem> typeOfTimeList) {
		this.typeOfTimeList = typeOfTimeList;
	}

	public Collection<SelectItem> getAlarmList() {
		if(alarmList == null) {
			alarmList = constructAlarmList();
		}		
		return alarmList;
	}

	public void setAlarmList(Collection<SelectItem> alarmList) {
		this.alarmList = alarmList;
	}

	public Collection<SelectItem> getDefinedAgentList() {
		if(definedAgentList == null) {
			definedAgentList = constructDefinedAgentList();
		}	
		return definedAgentList;
	}

	public void setDefinedAgentList(Collection<SelectItem> definedAgentList) {
		this.definedAgentList = definedAgentList;
	}

	public Collection<SelectItem> getJsSLAList() {
		if(jsSLAList == null) {
			jsSLAList = constructJsSLAList();
		}	
		return jsSLAList;
	}

	public void setJsSLAList(Collection<SelectItem> jsSLAList) {
		this.jsSLAList = jsSLAList;
	}

	public Collection<SelectItem> getResourceNameList() {
		if(resourceNameList == null) {
			resourceNameList = constructResourceNameList();
		}	
		return resourceNameList;
	}

	public void setResourceNameList(Collection<SelectItem> resourceNameList) {
		this.resourceNameList = resourceNameList;
	}

	public Collection<SelectItem> getJobBaseTypeList() {
		if(jobBaseTypeList == null) {
			jobBaseTypeList = constructJobBaseTypeList();
		}	
		return jobBaseTypeList;
	}

	public void setJobBaseTypeList(Collection<SelectItem> jobBaseTypeList) {
		this.jobBaseTypeList = jobBaseTypeList;
	}

	public Collection<SelectItem> getEventTypeDefList() {
		if(eventTypeDefList == null) {
			eventTypeDefList = constructEventTypeDefList();
		}	
		return eventTypeDefList;
	}

	public void setEventTypeDefList(Collection<SelectItem> eventTypeDefList) {
		this.eventTypeDefList = eventTypeDefList;
	}

	public Collection<SelectItem> getJobTypeDefList() {
		if(jobTypeDefList == null) {
			jobTypeDefList = constructJobTypeDefList();
		}
		return jobTypeDefList;
	}

	public void setJobTypeDefList(Collection<SelectItem> jobTypeDefList) {
		this.jobTypeDefList = jobTypeDefList;
	}

	public Collection<SelectItem> getRelativeTimeOptionList() {
		if(relativeTimeOptionList == null) {
			relativeTimeOptionList = constructRelativeTimeOptionList();
		}
		return relativeTimeOptionList;
	}

	public void setRelativeTimeOptionList(Collection<SelectItem> relativeTimeOptionList) {
		this.relativeTimeOptionList = relativeTimeOptionList;
	}

	public Collection<SelectItem> getUnitTypeList() {
		if(unitTypeList == null) {
			unitTypeList = constructUnitTypeList();
		}
		return unitTypeList;
	}

	public void setUnitTypeList(Collection<SelectItem> unitTypeList) {
		this.unitTypeList = unitTypeList;
	}

	public Collection<SelectItem> getJobStatusNameList() {
		if(jobStatusNameList == null) {
			jobStatusNameList = constructJobStatusNameList();
		}
		return jobStatusNameList;
	}

	public void setJobStatusNameList(Collection<SelectItem> jobStatusNameList) {
		this.jobStatusNameList = jobStatusNameList;
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

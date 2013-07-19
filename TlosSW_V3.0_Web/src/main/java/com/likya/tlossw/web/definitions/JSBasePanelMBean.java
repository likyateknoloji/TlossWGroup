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
import com.likya.tlossw.web.definitions.helpers.StateInfosTabBean;
import com.likya.tlossw.web.definitions.helpers.TimeManagementTabBean;
import com.likya.tlossw.web.utils.WebInputUtils;

public class JSBasePanelMBean extends TlosSWBaseBean {

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
	private StateInfosTabBean stateInfosTabBean; 

	public void init() {
		timeManagementTabBean = new TimeManagementTabBean(isScenario);
		logAnalyzingTabBean = new LogAnalyzingTabBean();
		localParametersTabBean = new LocalParametersTabBean();
		advancedJobInfosTab = new AdvancedJobInfosTab(getDbOperations());
		alarmPreferencesTabBean = new AlarmPreferencesTabBean();
		stateInfosTabBean = new StateInfosTabBean();
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
		stateInfosTabBean.resetTab();
	}

	public void fillOSystemList() {
		if (getoSystemList() == null) {
			setoSystemList(WebInputUtils.fillOSystemList());
		}
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

	public StateInfosTabBean getStateInfosTabBean() {
		return stateInfosTabBean;
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

package com.likya.tlossw.web.mng.reports;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

import javax.faces.model.SelectItem;

import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.mng.reports.helpers.ReportsParameters;
import com.likya.tlossw.web.utils.ComboListUtils;
import com.likya.tlossw.web.utils.ConstantDefinitions;
import com.likya.tlossw.web.utils.DefinitionUtils;

public class ReportBase extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 1626638877653601841L;

	private ReportsParameters reportParameters = null;

	private Collection<SelectItem> orderList;
	private Collection<SelectItem> orderByList;
	private Collection<SelectItem> includedJobsTypeList;
	private Collection<SelectItem> stateNameList;
	private Collection<SelectItem> substateNameList;
	private Collection<SelectItem> statusNameList;

	private String activeReportPanel = ConstantDefinitions.JOB_DURATION_REPORT;

	private boolean enterTimeInterval = false;

	private String stateDepthType = ConstantDefinitions.STATUS;
	private String stateName;
	private String substateName;
	private String statusName;

	private HashMap<String, String> statusToSubstate;
	private HashMap<String, String> substateToState;

	public ReportsParameters getReportParameters() {
		return reportParameters;
	}

	public void setReportParameters(ReportsParameters reportParameters) {
		this.reportParameters = reportParameters;
	}

	public Collection<SelectItem> getOrderList() {
		if (orderList == null) {
			orderList = ComboListUtils.constructOrderList();
		}
		return orderList;
	}

	public void setOrderList(Collection<SelectItem> orderList) {
		this.orderList = orderList;
	}

	public Collection<SelectItem> getOrderByList() {
		if (orderByList == null) {
			orderByList = ComboListUtils.constructOrderByList();
		}
		return orderByList;
	}

	public void setOrderByList(Collection<SelectItem> orderByList) {
		this.orderByList = orderByList;
	}

	public String getActiveReportPanel() {
		return activeReportPanel;
	}

	public void setActiveReportPanel(String activeReportPanel) {
		this.activeReportPanel = activeReportPanel;
	}

	public Collection<SelectItem> getIncludedJobsTypeList() {
		if (includedJobsTypeList == null) {
			includedJobsTypeList = ComboListUtils.constructIncludedJobsTypeList();
		}
		return includedJobsTypeList;
	}

	public void setIncludedJobsTypeList(Collection<SelectItem> includedJobsTypeList) {
		this.includedJobsTypeList = includedJobsTypeList;
	}

	public boolean isEnterTimeInterval() {
		return enterTimeInterval;
	}

	public void setEnterTimeInterval(boolean enterTimeInterval) {
		this.enterTimeInterval = enterTimeInterval;
	}

	public String getStateDepthType() {
		return stateDepthType;
	}

	public void setStateDepthType(String stateDepthType) {
		this.stateDepthType = stateDepthType;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getSubstateName() {
		return substateName;
	}

	public void setSubstateName(String substateName) {
		this.substateName = substateName;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public HashMap<String, String> getStatusToSubstate() {
		if (statusToSubstate == null) {
			statusToSubstate = DefinitionUtils.fillStatusToSubstateList();
		}
		return statusToSubstate;
	}

	public void setStatusToSubstate(HashMap<String, String> statusToSubstate) {
		this.statusToSubstate = statusToSubstate;
	}

	public HashMap<String, String> getSubstateToState() {
		if (substateToState == null) {
			substateToState = DefinitionUtils.fillSubstateToStateList();
		}
		return substateToState;
	}

	public void setSubstateToState(HashMap<String, String> substateToState) {
		this.substateToState = substateToState;
	}

	public Collection<SelectItem> getStateNameList() {
		if (stateNameList == null) {
			stateNameList = ComboListUtils.constructJobStateList();
		}
		return stateNameList;
	}

	public void setStateNameList(Collection<SelectItem> stateNameList) {
		this.stateNameList = stateNameList;
	}

	public Collection<SelectItem> getSubstateNameList() {
		if (substateNameList == null) {
			substateNameList = ComboListUtils.constructJobSubStateList();
		}
		return substateNameList;
	}

	public void setSubstateNameList(Collection<SelectItem> substateNameList) {
		this.substateNameList = substateNameList;
	}

	public Collection<SelectItem> getStatusNameList() {
		if (statusNameList == null) {
			statusNameList = ComboListUtils.constructJobStatusNameList();
		}
		return statusNameList;
	}

	public void setStatusNameList(Collection<SelectItem> statusNameList) {
		this.statusNameList = statusNameList;
	}

}

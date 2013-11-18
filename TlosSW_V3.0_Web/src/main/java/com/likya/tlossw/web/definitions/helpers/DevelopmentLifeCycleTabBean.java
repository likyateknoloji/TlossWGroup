package com.likya.tlossw.web.definitions.helpers;

import java.util.Collection;

import javax.faces.model.SelectItem;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.web.definitions.JSBasePanelMBean;
import com.likya.tlossw.web.utils.ComboListUtils;

public class DevelopmentLifeCycleTabBean extends BaseTabBean {

	private static final long serialVersionUID = 6372788338934430477L;

	/* live state info */
	private String stateName;
	private String subStateName;
	private String statusName;

	private Collection<SelectItem> jobSubStateNameList;
	private Collection<SelectItem> jobStatusNameList;

	public JSBasePanelMBean jsBasePanelMBean;

	public DevelopmentLifeCycleTabBean(JSBasePanelMBean jsBasePanelMBean) {
		super();
		this.jsBasePanelMBean = jsBasePanelMBean;
	}

	public void resetTab() {

		stateName = "";
		subStateName = "";
		statusName = "";

		if (jobSubStateNameList == null) {
			jobSubStateNameList = ComboListUtils.constructJobSubStateList();
		}
		
		if (jobStatusNameList == null) {
			jobStatusNameList = ComboListUtils.constructJobStatusNameList();
		}
	}

	public void fillDevelopmentLifeCycleTab(JobProperties jobProperties) {

		LiveStateInfo liveStateInfo = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0);
		stateName = liveStateInfo.getStateName().toString();
		subStateName = liveStateInfo.getSubstateName().toString();

		if (liveStateInfo.getStatusName() != null) {
			statusName = liveStateInfo.getStatusName().toString();
		}
	}

	public void fillLiveStateInfo(JobProperties jobProperties) {

		if (!subStateName.equals("")) {
			int stateIntValue = StateName.Enum.forString(stateName).intValue();
			int substateIntValue = SubstateName.Enum.forString(subStateName).intValue();

			if (substateIntValue == SubstateName.INT_CREATED) {
				int statusIntValue = StatusName.INT_REQUEST;

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, stateIntValue, substateIntValue, statusIntValue);
			} else if (substateIntValue != SubstateName.INT_CREATED) {
				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, stateIntValue, substateIntValue);
			}
		}
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

	public Collection<SelectItem> getJobSubStateNameList() {
		return jobSubStateNameList;
	}

	public void setJobSubStateNameList(Collection<SelectItem> jobSubStateNameList) {
		this.jobSubStateNameList = jobSubStateNameList;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public JSBasePanelMBean getJsBasePanelMBean() {
		return jsBasePanelMBean;
	}

	public void setJsBasePanelMBean(JSBasePanelMBean jsBasePanelMBean) {
		this.jsBasePanelMBean = jsBasePanelMBean;
	}

	public Collection<SelectItem> getJobStatusNameList() {
		return jobStatusNameList;
	}

	public void setJobStatusNameList(Collection<SelectItem> jobStatusNameList) {
		this.jobStatusNameList = jobStatusNameList;
	}

}

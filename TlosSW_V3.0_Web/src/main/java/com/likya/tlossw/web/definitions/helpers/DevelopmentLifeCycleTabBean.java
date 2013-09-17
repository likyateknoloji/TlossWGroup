package com.likya.tlossw.web.definitions.helpers;

import java.util.Collection;

import javax.faces.model.SelectItem;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlossw.web.definitions.JSBasePanelMBean;
import com.likya.tlossw.web.utils.ComboListUtils;

public class DevelopmentLifeCycleTabBean extends BaseTabBean {

	private static final long serialVersionUID = 6372788338934430477L;

	/* live state info */
	private String stateName;
	private String subStateName;

	private Collection<SelectItem> jobSubStateNameList;

	public JSBasePanelMBean jsBasePanelMBean;

	public DevelopmentLifeCycleTabBean(JSBasePanelMBean jsBasePanelMBean) {
		super();
		this.jsBasePanelMBean = jsBasePanelMBean;
	}

	public void resetTab() {

		stateName = "";
		subStateName = "";

		if (jobSubStateNameList == null) {
			jobSubStateNameList = ComboListUtils.constructJobSubStateList();
		}
	}

	public void fillDevelopmentLifeCycleTab(JobProperties jobProperties) {
		
		stateName = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().toString();
		subStateName = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName().toString();
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

}

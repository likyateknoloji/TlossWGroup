package com.likya.tlossw.web.live;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;

import com.likya.tlossw.model.client.spc.SpcInfoTypeClient;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.model.tree.ScenarioNode;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.definitions.BatchProcessPanelMBean;
import com.likya.tlossw.web.utils.ConstantDefinitions;
import com.likya.tlossw.webclient.TEJmxMpClient;

@ManagedBean(name = "liveMBean")
@ViewScoped
public class LiveMBean extends TlosSWBaseBean implements Serializable{

	private static final long serialVersionUID = -72513231786102609L;

	@ManagedProperty(value = "#{scenarioMBean}")
	private ScenarioMBean scenarioMBean;
	
	private String liveJSTable = SCENARIO_PAGE;
	
	public final static String SCENARIO_PAGE = "/inc/livePanels/scenarioLiveTree.xhtml";

	private boolean transformToLocalTime = false;
	
	public void onNodeSelect(NodeSelectEvent event) {
		//addMessage("jobTree", FacesMessage.SEVERITY_INFO, event.getTreeNode().getType() + " selected", null);
		String selectedNode = event.getTreeNode().toString();
		
		if (event.getTreeNode().getType().equals(ConstantDefinitions.TREE_SCENARIO)) {
			ScenarioNode scenarioNode = (ScenarioNode)event.getTreeNode().getData();
			String spcId = scenarioNode.getSpcInfoTypeClient().getSpcId();
			String scenarioId = spcId; //spcId.substring(spcId.lastIndexOf('.') + 1, spcId.length());
			getScenarioMBean().getJobList(scenarioId, transformToLocalTime);
		}
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.update("scenarioLiveTreeForm");
		
//		jobProperties = JobProperties.Factory.newInstance();
//		
//		BaseJobInfos baseJobInfos = BaseJobInfos.Factory.newInstance();
//		baseJobInfos.setJsName("hakan");
//		
//		JobInfos jobInfos = JobInfos.Factory.newInstance();
//		JobTypeDetails jobTypeDetails = JobTypeDetails.Factory.newInstance();
//		jobInfos.setJobTypeDetails(jobTypeDetails);
//		baseJobInfos.setJobInfos(jobInfos);
//		jobProperties.setBaseJobInfos(baseJobInfos);
		/*String selectedJob = event.getTreeNode().toString();
		String jobId = selectedJob.substring(selectedJob.lastIndexOf("|") + 1);
		String jobAbsolutePath = selectedJob.substring(0, selectedJob.lastIndexOf("|")-1);
		
		jobProperties = getDbOperations().getJob(JOB_DEFINITION_DATA, "/dat:TlosProcessData", jobAbsolutePath);
		
		getBatchProcessPanelMBean().setJobProperties(jobProperties);
		getBatchProcessPanelMBean().setJobInsertButton(true);
		getBatchProcessPanelMBean().fillTabs();
		jobDefCenterPanel = BATCH_PROCESS_PAGE;
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.update("jobDefinitionForm");*/
	}
	
	public String getLiveJSTable() {
		return liveJSTable;
	}

	public void setLiveJSTable(String liveJSTable) {
		this.liveJSTable = liveJSTable;
	}



	public ScenarioMBean getScenarioMBean() {
		return scenarioMBean;
	}



	public void setScenarioMBean(ScenarioMBean scenarioMBean) {
		this.scenarioMBean = scenarioMBean;
	}

	public boolean isTransformToLocalTime() {
		return transformToLocalTime;
	}

	public void setTransformToLocalTime(boolean transformToLocalTime) {
		this.transformToLocalTime = transformToLocalTime;
	}
	
}

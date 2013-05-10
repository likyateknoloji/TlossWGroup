package com.likya.tlossw.web.live;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;

import com.likya.tlossw.model.tree.JobNode;
import com.likya.tlossw.model.tree.ScenarioNode;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.ConstantDefinitions;

@ManagedBean(name = "liveMBean")
@ViewScoped
public class LiveMBean extends TlosSWBaseBean implements Serializable{

	private static final long serialVersionUID = -72513231786102609L;

	@ManagedProperty(value = "#{scenarioMBean}")
	private ScenarioMBean scenarioMBean;
	
	@ManagedProperty(value = "#{jobMBean}")
	private JobMBean jobMBean;
	
	private String liveJSTable = SCENARIO_PAGE;
	
	public final static String SCENARIO_PAGE = "/inc/livePanels/scenarioLiveTree.xhtml";
	public final static String JOB_PAGE = "/inc/livePanels/jobLiveTree.xhtml";

	private boolean transformToLocalTime = false;
	
	@PostConstruct
	public void init() {
		getScenarioMBean().setTransformToLocalTime(transformToLocalTime);
		getJobMBean().setTransformToLocalTime(transformToLocalTime);
	}
	
	public void onNodeSelect(NodeSelectEvent event) {
		//addMessage("jobTree", FacesMessage.SEVERITY_INFO, event.getTreeNode().getType() + " selected", null);
		//String selectedNode = event.getTreeNode().toString();
		
		RequestContext context = RequestContext.getCurrentInstance();
		
		if (event.getTreeNode().getType().equals(ConstantDefinitions.TREE_SCENARIO)) {
			ScenarioNode scenarioNode = (ScenarioNode)event.getTreeNode().getData();
			String spcId = scenarioNode.getSpcInfoTypeClient().getSpcId();
			getScenarioMBean().getJobList(spcId);
			
			liveJSTable = SCENARIO_PAGE;
		} else if (event.getTreeNode().getType().equals(ConstantDefinitions.TREE_JOB)) {
			JobNode jobNode = (JobNode)event.getTreeNode().getData();
			String jobName = jobNode.getJobName();
			String groupId = jobNode.getJobPath();
			getJobMBean().fillJobLivePanel(groupId, jobName);
			
			liveJSTable = JOB_PAGE;
		}
		
		context.update("liveForm");
	}
	
	//job taniminda agentChoiceMethod: userInteractionPreference ise ekrandan agent listesini goruntule deyince buraya geliyor
	public void showAvailableResourcesForJob(ActionEvent e) {
		//TODO merve : eskisinde ayrı bir panele geçiyordu (agentSelectionPanel.xhtml),
		// şimdiki duruma göre eklenecek
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

	public JobMBean getJobMBean() {
		return jobMBean;
	}

	public void setJobMBean(JobMBean jobMBean) {
		this.jobMBean = jobMBean;
	}
	
}

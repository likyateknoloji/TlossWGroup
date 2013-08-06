package com.likya.tlossw.web.live;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;

import com.likya.tlossw.model.tree.JobNode;
import com.likya.tlossw.model.tree.ScenarioNode;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.ConstantDefinitions;

@ManagedBean(name = "liveMBean")
@ViewScoped
public class LiveMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = -72513231786102609L;

	@ManagedProperty(value = "#{scenarioMBean}")
	private ScenarioMBean scenarioMBean;

	@ManagedProperty(value = "#{jobMBean}")
	private JobMBean jobMBean;

	private String liveJSTable = SCENARIO_PAGE;

	public final static String SCENARIO_PAGE = "scenarioLiveTree.xhtml";
	public final static String JOB_PAGE = "jobLiveTree.xhtml";
	public final static String GRAPH_PAGE = "scenarioGraphTreePanel.xhtml";

	private boolean transformToLocalTime = false;

	@PostConstruct
	public void init() {
		getScenarioMBean().setTransformToLocalTime(transformToLocalTime);
		getJobMBean().setTransformToLocalTime(transformToLocalTime);
	}

	public void onNodeSelect(NodeSelectEvent event) {
		// addMessage("jobTree", FacesMessage.SEVERITY_INFO,
		// event.getTreeNode().getType() + " selected", null);
		// String selectedNode = event.getTreeNode().toString();

		if (event.getTreeNode().getType().equals(ConstantDefinitions.TREE_SCENARIO)) {
			ScenarioNode scenarioNode = (ScenarioNode) event.getTreeNode().getData();
			String spcId = scenarioNode.getSpcInfoTypeClient().getSpcId();
			getScenarioMBean().getJobList(spcId);

			liveJSTable = SCENARIO_PAGE;
		} else if (event.getTreeNode().getType().equals(ConstantDefinitions.TREE_JOB)) {
			JobNode jobNode = (JobNode) event.getTreeNode().getData();
			String jobId = jobNode.getId();
			String groupId = jobNode.getPath();
			getJobMBean().fillJobLivePanel(groupId, jobId);

			liveJSTable = JOB_PAGE;
		}

	}

	public void viewScenarioTree() {
		liveJSTable = GRAPH_PAGE;
		getScenarioMBean().setSelectedPanel(ConstantDefinitions.LIVE_TREE);
		getScenarioMBean().setSelectedScenarioId(getScenarioMBean().getSpcInfoTypeClient().getSpcId());

		RequestContext context = RequestContext.getCurrentInstance();
		context.update("liveForm");
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

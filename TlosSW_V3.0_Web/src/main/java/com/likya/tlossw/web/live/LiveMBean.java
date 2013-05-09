package com.likya.tlossw.web.live;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;

import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.model.tree.JobNode;
import com.likya.tlossw.model.tree.ScenarioNode;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.appmng.TraceBean;
import com.likya.tlossw.web.utils.ConstantDefinitions;
import com.likya.tlossw.web.utils.LiveUtils;
import com.likya.tlossw.webclient.TEJmxMpClient;

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
			JobNode jobNode =(JobNode)event.getTreeNode().getData();
			String jobId = jobNode.getJobInfoTypeClient().getJobId();
			String groupId = jobNode.getJobPath();
			getJobMBean().setJobInfo(groupId, jobId);
			
			liveJSTable = JOB_PAGE;
		}
		
		context.update("liveForm");
	}
	
	public void pauseJobAction(ActionEvent e) {
		JobInfoTypeClient job = (JobInfoTypeClient) getScenarioMBean().getJobDataTable().getRowData();
		pauseJob(job);
		
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.pause");*/
	}
	
	public void pauseJob(JobInfoTypeClient job) {
		TEJmxMpClient.pauseJob(new JmxUser(), LiveUtils.jobPath(job));
		refreshLivePanel(job.getTreePath());
	}
	
	//user based islerde kullanici ekrandan baslati sectiginde buraya geliyor
	public void startUserBasedJobAction(ActionEvent e) {
		JobInfoTypeClient job = (JobInfoTypeClient) getScenarioMBean().getJobDataTable().getRowData();
		TEJmxMpClient.startUserBasedJob(new JmxUser(), LiveUtils.jobPath(job));
		refreshLivePanel(job.getTreePath());
		
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.start");*/
	}
		
	public void startJobAction(ActionEvent e) {
		JobInfoTypeClient job = (JobInfoTypeClient) getScenarioMBean().getJobDataTable().getRowData();
		startJob(job);
		
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.start");*/
	}
	
	public void startJob(JobInfoTypeClient job) {
		TEJmxMpClient.startJob(new JmxUser(), LiveUtils.jobPath(job));
		refreshLivePanel(job.getTreePath());
	}
	
	public void retryJobAction(ActionEvent e) {
		JobInfoTypeClient job = (JobInfoTypeClient) getScenarioMBean().getJobDataTable().getRowData();
		retryJob(job);
		
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.retry");*/
	}
	
	public void retryJob(JobInfoTypeClient job) {
		TEJmxMpClient.retryJob(new JmxUser(), LiveUtils.jobPath(job));
		refreshLivePanel(job.getTreePath());
	}
	
	public void doSuccessJobAction(ActionEvent e) {
		JobInfoTypeClient job = (JobInfoTypeClient) getScenarioMBean().getJobDataTable().getRowData();
		doSuccessJob(job);
		
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.doSuccess");*/
	}
	
	public void doSuccessJob(JobInfoTypeClient job) {
		TEJmxMpClient.doSuccess(new JmxUser(), LiveUtils.jobPath(job));
		refreshLivePanel(job.getTreePath());
	}
	
	public void skipJobAction(ActionEvent e) {
		JobInfoTypeClient job = (JobInfoTypeClient) getScenarioMBean().getJobDataTable().getRowData();
		skipJob(job);
		
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.skip");*/
	}
	
	public void skipJob(JobInfoTypeClient job) {
		TEJmxMpClient.skipJob(new JmxUser(), LiveUtils.jobPath(job));
		refreshLivePanel(job.getTreePath());
	}
	
	public void stopJobAction(ActionEvent e) {
		JobInfoTypeClient job = (JobInfoTypeClient) getScenarioMBean().getJobDataTable().getRowData();
		stopJob(job);
		
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.stop");*/
	}
	
	public void stopJob(JobInfoTypeClient job) {
		TEJmxMpClient.stopJob(new JmxUser(), LiveUtils.jobPath(job));
		refreshLivePanel(job.getTreePath());
	}
	
	public void resumeJobAction(ActionEvent e) {
		JobInfoTypeClient job = (JobInfoTypeClient) getScenarioMBean().getJobDataTable().getRowData();
		resumeJob(job);
		
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.resume");*/
	}
	
	public void resumeJob(JobInfoTypeClient job) {
		TEJmxMpClient.resumeJob(new JmxUser(), LiveUtils.jobPath(job));
		refreshLivePanel(job.getTreePath());
	}
	
	//job taniminda agentChoiceMethod: userInteractionPreference ise ekrandan agent listesini goruntule deyince buraya geliyor
	public void showAvailableResourcesForJob(ActionEvent e) {
		//TODO merve : eskisinde ayrı bir panele geçiyordu (agentSelectionPanel.xhtml),
		// şimdiki duruma göre eklenecek
	}
	
	private void refreshLivePanel(String scenarioPath) {
		getScenarioMBean().getJobList(scenarioPath);
		
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

package com.likya.tlossw.web.live;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceDocument.Resource;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.model.client.spc.SpcInfoTypeClient;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.live.helpers.LiveJobManagementBean;
import com.likya.tlossw.web.utils.ComboListUtils;
import com.likya.tlossw.web.utils.DecorationUtils;
import com.likya.tlossw.web.utils.LiveUtils;
import com.likya.tlossw.webclient.TEJmxMpClient;

@ManagedBean(name = "scenarioMBean")
@ViewScoped
public class ScenarioMBean extends TlosSWBaseBean implements JobManagementInterface, Serializable {

	private static final long serialVersionUID = 4922375731088848331L;

	private SpcInfoTypeClient spcInfoTypeClient;

	private ArrayList<JobInfoTypeClient> jobInfoList;

	private transient DataTable jobDataTable;
	private List<JobInfoTypeClient> filteredJobs;
	private JobInfoTypeClient selectedRow;
	private JobInfoTypeClient[] selectedRows;

	ArrayList<String> oSList = new ArrayList<String>();

	private SelectItem[] oSSelectItem;

	private String selectedPanel;
	private String selectedScenarioId;

	private Collection<SelectItem> resourceListForJob;
	private String selectedResource;

	private boolean transformToLocalTime;

	private LiveJobManagementBean liveJobManagementBean;
	
	private HashMap<String, String> jobIcons = null;
	
	@PostConstruct
	public void init() {

		DecorationUtils.jobCssSetter();
		
		setLiveJobManagementBean(new LiveJobManagementBean(this));
	}
	
	public void getJobList(String scenarioId) {
		
		SpcInfoTypeClient spcInfoTypeClient = TEJmxMpClient.retrieveSpcInfo(getWebAppUser(), scenarioId);

		spcInfoTypeClient.setSpcId(spcInfoTypeClient.getSpcId());
		spcInfoTypeClient.setNumOfActiveJobs(spcInfoTypeClient.getNumOfActiveJobs());
		spcInfoTypeClient.setNumOfJobs(spcInfoTypeClient.getNumOfJobs());
		spcInfoTypeClient.setPausable(spcInfoTypeClient.getPausable());
		spcInfoTypeClient.setResumable(spcInfoTypeClient.getResumable());
		spcInfoTypeClient.setStopable(spcInfoTypeClient.getStopable());
		spcInfoTypeClient.setStartable(spcInfoTypeClient.getStartable());

		setSpcInfoTypeClient(spcInfoTypeClient);

		jobInfoList = (ArrayList<JobInfoTypeClient>) TEJmxMpClient.getJobInfoTypeClientList(getWebAppUser(), getSpcInfoTypeClient().getSpcId(), transformToLocalTime);
		System.out.println("");
		oSList.add("Windows");
		oSList.add("Unix");
		oSSelectItem = ComboListUtils.createFilterOptions(oSList);
	}

	// job taniminda agentChoiceMethod: userInteractionPreference ise ekrandan
	// agent listesini goruntule deyince buraya geliyor
	public void showAvailableResourcesForJob(ActionEvent e) {
		selectedRow = (JobInfoTypeClient) jobDataTable.getRowData();

		ArrayList<Resource> resourceAgentList = TEJmxMpClient.getAvailableResourcesForJob(getWebAppUser(), LiveUtils.jobPath(selectedRow));

		resourceListForJob = new ArrayList<SelectItem>();

		if (resourceAgentList != null && resourceAgentList.size() > 0) {

			for (Resource agent : resourceAgentList) {
				SelectItem item = new SelectItem();

				item.setValue(agent.getAgentid());
				item.setLabel(agent.getEntryName() + "." + agent.getAgentid());
				resourceListForJob.add(item);
			}

		} else {
			addMessage("showAvailableResourcesForJob", FacesMessage.SEVERITY_WARN, "tlos.trace.jobAvailableAgentListNull", null);
		}
	}

	public void assignAgentForJob(ActionEvent e) {
		boolean assigned = TEJmxMpClient.assignAgentForJob(getWebAppUser(), selectedRow.getTreePath() + "." + selectedRow.getJobName(), selectedResource);

		if (assigned) {
			addMessage("assignAgentForJob", FacesMessage.SEVERITY_INFO, "tlos.trace.agentAssignedForJob", null);
			/* TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + selectedRow.getJobKey(), e.getComponent().getId(), "tlos.trace.agentAssignedForJob"); */
		} else {
			addMessage("assignAgentForJob", FacesMessage.SEVERITY_ERROR, "tlos.trace.agentCannotBeAssignedForJob", null);
		}

		refreshLivePanel(selectedRow.getTreePath());
	}

	public void stopScenarioNormalAction(ActionEvent e) {
		TEJmxMpClient.stopScenario(getWebAppUser(), getSpcInfoTypeClient().getSpcId(), false);
		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + getSpcInfoTypeClient().getSpcId(), e.getComponent().getId(), "tlos.trace.live.scenario.stop.normal");
		 */
	}

	public void stopScenarioForcedAction(ActionEvent e) {
		TEJmxMpClient.stopScenario(getWebAppUser(), getSpcInfoTypeClient().getSpcId(), true);
		getJobList(getSpcInfoTypeClient().getSpcId());
		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + getSpcInfoTypeClient().getSpcId(), e.getComponent().getId(), "tlos.trace.live.scenario.stop.force");
		 */
	}

	public void pauseScenarioAction(ActionEvent e) {
		TEJmxMpClient.suspendScenario(getWebAppUser(), getSpcInfoTypeClient().getSpcId());
		getJobList(getSpcInfoTypeClient().getSpcId());
		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + getSpcInfoTypeClient().getSpcId(), e.getComponent().getId(), "tlos.trace.live.scenario.pause");
		 */
	}

	public void resumeScenarioAction(ActionEvent e) {
		TEJmxMpClient.resumeScenario(getWebAppUser(), getSpcInfoTypeClient().getSpcId());
		getJobList(getSpcInfoTypeClient().getSpcId());
		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + getSpcInfoTypeClient().getSpcId(), e.getComponent().getId(), "tlos.trace.live.scenario.resume");
		 */
	}

	public void startScenarioAction(ActionEvent e) {
		TEJmxMpClient.restartScenario(getWebAppUser(), getSpcInfoTypeClient().getSpcId());
		getJobList(getSpcInfoTypeClient().getSpcId());
		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + getSpcInfoTypeClient().getSpcId(), e.getComponent().getId(), "tlos.trace.live.scenario.start");
		 */
	}

	public void refreshLivePanel(String scenarioPath) {
		getJobList(scenarioPath);

		RequestContext context = RequestContext.getCurrentInstance();
		context.update("liveForm");
	}
	
	@Override
	public void refreshTlosAgentPanel() {
	}

	@Override
	public JobInfoTypeClient getJobInTyCl() {
		return null;
	}

	public ArrayList<JobInfoTypeClient> getJobInfoList() {
		return jobInfoList;
	}

	public void setJobInfoList(ArrayList<JobInfoTypeClient> jobInfoList) {
		this.jobInfoList = jobInfoList;
	}

	public DataTable getJobDataTable() {
		return jobDataTable;
	}

	public void setJobDataTable(DataTable jobDataTable) {
		this.jobDataTable = jobDataTable;
	}

	public List<JobInfoTypeClient> getFilteredJobs() {
		return filteredJobs;
	}

	public void setFilteredJobs(List<JobInfoTypeClient> filteredJobs) {
		this.filteredJobs = filteredJobs;
	}

	public SpcInfoTypeClient getSpcInfoTypeClient() {
		return spcInfoTypeClient;
	}

	public void setSpcInfoTypeClient(SpcInfoTypeClient spcInfoTypeClient) {
		this.spcInfoTypeClient = spcInfoTypeClient;
	}

	public SelectItem[] getOsSelectItem() {
		return oSSelectItem;
	}

	public void setOsSelectItem(SelectItem[] oSSelectItem) {
		this.oSSelectItem = oSSelectItem;
	}

	public JobInfoTypeClient getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(JobInfoTypeClient selectedRow) {
		this.selectedRow = selectedRow;
	}

	public JobInfoTypeClient[] getSelectedRows() {
		return selectedRows;
	}

	public void setSelectedRows(JobInfoTypeClient[] selectedRows) {
		this.selectedRows = selectedRows;
	}

	public boolean isTransformToLocalTime() {
		return transformToLocalTime;
	}

	public void setTransformToLocalTime(boolean transformToLocalTime) {
		this.transformToLocalTime = transformToLocalTime;
	}

	public String getSelectedPanel() {
		return selectedPanel;
	}

	public void setSelectedPanel(String selectedPanel) {
		this.selectedPanel = selectedPanel;
	}

	public String getSelectedScenarioId() {
		return selectedScenarioId;
	}

	public void setSelectedScenarioId(String selectedScenarioId) {
		this.selectedScenarioId = selectedScenarioId;
	}

	public Collection<SelectItem> getResourceListForJob() {
		return resourceListForJob;
	}

	public void setResourceListForJob(Collection<SelectItem> resourceListForJob) {
		this.resourceListForJob = resourceListForJob;
	}

	public String getSelectedResource() {
		return selectedResource;
	}

	public void setSelectedResource(String selectedResource) {
		this.selectedResource = selectedResource;
	}

	public LiveJobManagementBean getLiveJobManagementBean() {
		return liveJobManagementBean;
	}

	public void setLiveJobManagementBean(LiveJobManagementBean liveJobManagementBean) {
		this.liveJobManagementBean = liveJobManagementBean;
	}

	public String getJobStateColorCss(LiveStateInfo jobState) {
		
		String result;
		
		result = DecorationUtils.jobStateColorMappings(jobState);
 
		return result;

	}
	
	public String getJobStateIconCss(LiveStateInfo jobState) {
		
		String result;
		
		result = DecorationUtils.jobStateIconMappings(jobState);
 
		return result;
	}
	
	public String getJobIconsElement(String key) {
		String result;
		
		jobIcons = DecorationUtils.getJobIconsMappings();
		result = jobIcons.get(key);
		
		if(result == null) {
			System.out.println("Job : " + key);
			addMessage("getJobIconsElement", FacesMessage.SEVERITY_WARN, "Job Icon Undefined for : " + key, null);
		}
		
		return result == null ? "default" : result;
	}
	
	public HashMap<String, String> getJobIcons() {
		return jobIcons;
	}

	public void setJobIcons(HashMap<String, String> jobIcons) {
		this.jobIcons = jobIcons;
	}

}

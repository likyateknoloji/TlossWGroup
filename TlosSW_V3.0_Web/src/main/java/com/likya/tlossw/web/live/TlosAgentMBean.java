package com.likya.tlossw.web.live;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.primefaces.component.datatable.DataTable;

import com.likya.tlos.model.xmlbeans.agent.AgentTypeDocument.AgentType;
import com.likya.tlos.model.xmlbeans.agent.OsTypeDocument.OsType;
import com.likya.tlossw.model.client.resource.TlosAgentInfoTypeClient;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.live.helpers.LiveJobManagementBean;
import com.likya.tlossw.web.utils.ComboListUtils;
import com.likya.tlossw.web.utils.ConstantDefinitions;
import com.likya.tlossw.webclient.TEJmxMpClient;

@ManagedBean(name = "tlosAgentMBean")
@ViewScoped
public class TlosAgentMBean extends TlosSWBaseBean implements JobManagementInterface, Serializable {

	private static final long serialVersionUID = -1955672983700174175L;

	private TlosAgentInfoTypeClient tlosAgentInfoTypeClient;

	private ArrayList<JobInfoTypeClient> jobInfoList;

	private transient DataTable jobDataTable;
	private List<JobInfoTypeClient> filteredJobs;
	private JobInfoTypeClient selectedRow;

	private ArrayList<String> oSList = new ArrayList<String>();

	private SelectItem[] oSSelectItem;

	private Collection<SelectItem> resourceListForJob;
	private String selectedResource;

	private boolean transformToLocalTime;

	private LiveJobManagementBean liveJobManagementBean;

	@PostConstruct
	public void init() {
		setLiveJobManagementBean(new LiveJobManagementBean(this));
	}

	public void initializeTlosAgentPanel(int agentId) {

		tlosAgentInfoTypeClient = TEJmxMpClient.retrieveTlosAgentInfo(getWebAppUser(), agentId);
		if (tlosAgentInfoTypeClient.getAgentType().toLowerCase().equals(AgentType.SERVER.toString().toLowerCase())) {
			tlosAgentInfoTypeClient.setAgentName(ConstantDefinitions.SERVER_NAME);
		} else {
			tlosAgentInfoTypeClient.setAgentName(ConstantDefinitions.AGENT_NAME + tlosAgentInfoTypeClient.getAgentId());
		}

		jobInfoList = (ArrayList<JobInfoTypeClient>) TEJmxMpClient.getAgentsJobList(getWebAppUser(), agentId, transformToLocalTime);

		oSList.add(OsType.WINDOWS.toString());
		oSList.add(OsType.UNIX.toString());
		oSSelectItem = ComboListUtils.createFilterOptions(oSList);
	}

	public void forcedDeactivateAgentAction(ActionEvent e) {
		TEJmxMpClient.deactivateTlosAgent(getWebAppUser(), getTlosAgentInfoTypeClient().getAgentId(), true);
		refreshTlosAgentPanel();
	}

	public void normalDeactivateAgentAction(ActionEvent e) {
		TEJmxMpClient.deactivateTlosAgent(getWebAppUser(), getTlosAgentInfoTypeClient().getAgentId(), false);
		refreshTlosAgentPanel();
	}

	public void activateAgentAction(ActionEvent e) {
		TEJmxMpClient.activateTlosAgent(getWebAppUser(), getTlosAgentInfoTypeClient().getAgentId());
		refreshTlosAgentPanel();
	}

	public void refreshTlosAgentPanel() {
		initializeTlosAgentPanel(tlosAgentInfoTypeClient.getAgentId());
	}
	
	@Override
	public void refreshLivePanel(String scenarioPath) {
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

	public boolean isTransformToLocalTime() {
		return transformToLocalTime;
	}

	public void setTransformToLocalTime(boolean transformToLocalTime) {
		this.transformToLocalTime = transformToLocalTime;
	}

	public TlosAgentInfoTypeClient getTlosAgentInfoTypeClient() {
		return tlosAgentInfoTypeClient;
	}

	public void setTlosAgentInfoTypeClient(TlosAgentInfoTypeClient tlosAgentInfoTypeClient) {
		this.tlosAgentInfoTypeClient = tlosAgentInfoTypeClient;
	}

	public ArrayList<String> getoSList() {
		return oSList;
	}

	public void setoSList(ArrayList<String> oSList) {
		this.oSList = oSList;
	}

	public SelectItem[] getosSelectItem() {
		return oSSelectItem;
	}

	public void setosSelectItem(SelectItem[] oSSelectItem) {
		this.oSSelectItem = oSSelectItem;
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

	public JobInfoTypeClient getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(JobInfoTypeClient selectedRow) {
		this.selectedRow = selectedRow;
	}

	@Override
	public JobInfoTypeClient getJobInTyCl() {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.likya.tlossw.web.live;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.primefaces.component.datatable.DataTable;

import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.model.client.spc.SpcInfoTypeClient;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.webclient.TEJmxMpClient;

@ManagedBean(name = "scenarioMBean")
@ViewScoped
public class ScenarioMBean extends TlosSWBaseBean implements Serializable{

	private static final long serialVersionUID = 4922375731088848331L;

	private SpcInfoTypeClient spcInfoTypeClient;

	private ArrayList<JobInfoTypeClient> jobInfoList;

	private transient DataTable jobDataTable;
	private List<JobInfoTypeClient> filteredJobs;  
	private JobInfoTypeClient selectedRow; 
	private JobInfoTypeClient[] selectedRows;

	ArrayList<String> oSList = new ArrayList<String>(); 

	private SelectItem[] oSSelectItem;

	private boolean transformToLocalTime;
	
	public void getJobList(String scenarioId) {
		SpcInfoTypeClient spcInfoTypeClient = TEJmxMpClient.retrieveSpcInfo(new JmxUser(), scenarioId);

		spcInfoTypeClient.setSpcId(spcInfoTypeClient.getSpcId());
		spcInfoTypeClient.setNumOfActiveJobs(spcInfoTypeClient.getNumOfActiveJobs());
		spcInfoTypeClient.setNumOfJobs(spcInfoTypeClient.getNumOfJobs());
		spcInfoTypeClient.setPausable(spcInfoTypeClient.getPausable());
		spcInfoTypeClient.setResumable(spcInfoTypeClient.getResumable());
		spcInfoTypeClient.setStopable(spcInfoTypeClient.getStopable());
		spcInfoTypeClient.setStartable(spcInfoTypeClient.getStartable());

		setSpcInfoTypeClient(spcInfoTypeClient);

		jobInfoList = (ArrayList<JobInfoTypeClient>) TEJmxMpClient.getJobInfoTypeClientList(new JmxUser(), getSpcInfoTypeClient().getSpcId(), transformToLocalTime);
		System.out.println("");
		oSList.add("Windows");
		oSList.add("Unix");
		oSSelectItem = createFilterOptions(oSList);

	}

	private SelectItem[] createFilterOptions(ArrayList<String> data)  {  
		SelectItem[] options = new SelectItem[data.size() + 1];  

		options[0] = new SelectItem("", "Select");  
		for(int i = 0; i < data.size(); i++) {  
			options[i + 1] = new SelectItem(data.get(i), data.get(i));  
		}  

		return options;  
	}

	public void stopScenarioNormalAction(ActionEvent e) {
		TEJmxMpClient.stopScenario(new JmxUser(), getSpcInfoTypeClient().getSpcId(), false);
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + getSpcInfoTypeClient().getSpcId(), e.getComponent().getId(), 
				"tlos.trace.live.scenario.stop.normal");*/
	}

	public void stopScenarioForcedAction(ActionEvent e) {
		TEJmxMpClient.stopScenario(new JmxUser(), getSpcInfoTypeClient().getSpcId(), true);
		getJobList(getSpcInfoTypeClient().getSpcId());
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + getSpcInfoTypeClient().getSpcId(), e.getComponent().getId(), 
				"tlos.trace.live.scenario.stop.force");*/
	}

	public void pauseScenarioAction(ActionEvent e) {
		TEJmxMpClient.suspendScenario(new JmxUser(), getSpcInfoTypeClient().getSpcId());
		getJobList(getSpcInfoTypeClient().getSpcId());
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + getSpcInfoTypeClient().getSpcId(), e.getComponent().getId(), 
				"tlos.trace.live.scenario.pause");*/
	}

	public void resumeScenarioAction(ActionEvent e) {
		TEJmxMpClient.resumeScenario(new JmxUser(), getSpcInfoTypeClient().getSpcId());
		getJobList(getSpcInfoTypeClient().getSpcId());
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + getSpcInfoTypeClient().getSpcId(), e.getComponent().getId(), 
				"tlos.trace.live.scenario.resume");*/
	}

	public void startScenarioAction(ActionEvent e) {
		TEJmxMpClient.restartScenario(new JmxUser(), getSpcInfoTypeClient().getSpcId());
		getJobList(getSpcInfoTypeClient().getSpcId());
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + getSpcInfoTypeClient().getSpcId(), e.getComponent().getId(), 
				"tlos.trace.live.scenario.start");*/
	}

	public void viewScenarioTree() {
		//TODO merve : eskisinde ayrı bir panele geçiyordu (scenarioTreePanel.xhtml),
		// şimdiki duruma göre eklenecek
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

}

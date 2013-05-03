package com.likya.tlossw.web.live;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
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
	
	ArrayList<String> oSList = new ArrayList<String>(); 

	private SelectItem[] oSSelectItem;
	
	public void getJobList(String scenarioId, boolean transformToLocalTime) {
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
	
    private SelectItem[] createFilterOptions(ArrayList<String> data)  {  
        SelectItem[] options = new SelectItem[data.size() + 1];  
  
        options[0] = new SelectItem("", "Select");  
        for(int i = 0; i < data.size(); i++) {  
            options[i + 1] = new SelectItem(data.get(i), data.get(i));  
        }  
  
        return options;  
    }

	public SelectItem[] getOsSelectItem() {
		return oSSelectItem;
	}

	public void setOsSelectItem(SelectItem[] oSSelectItem) {
		this.oSSelectItem = oSSelectItem;
	}  
	
}

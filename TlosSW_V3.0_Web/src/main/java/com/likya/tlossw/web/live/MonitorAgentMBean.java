package com.likya.tlossw.web.live;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.likya.tlossw.model.client.resource.CpuInfoTypeClient;
import com.likya.tlossw.model.client.resource.DiskInfoTypeClient;
import com.likya.tlossw.model.client.resource.MemoryInfoTypeClient;
import com.likya.tlossw.model.client.resource.MonitorAgentInfoTypeClient;
import com.likya.tlossw.model.client.resource.NrpeDataInfoTypeClient;
import com.likya.tlossw.web.TlosSWBaseBean;

@ManagedBean(name = "monitorAgentMBean")
@ViewScoped
public class MonitorAgentMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 666268045819934293L;

	private String resourceName;
	private String nrpePort;

	private List<CpuInfoTypeClient> cpuInfoTypeClientList;
	private List<DiskInfoTypeClient> diskInfoTypeClientList;
	private List<MemoryInfoTypeClient> memoryInfoTypeClientList;

	// kaynak makinelerin oldugu agacta herhangi bir nagios agent secildiginde buraya geliyor
	// panelde gosterilmek uzere makinenin kullanim bilgilerini set ediyor
	public void fillMonitoringAgentInfo(MonitorAgentInfoTypeClient monitorAgentInfoTypeClient) {

		resourceName = monitorAgentInfoTypeClient.getResourceName();
		nrpePort = monitorAgentInfoTypeClient.getNrpePort() + "";

		NrpeDataInfoTypeClient nrpeDataInfoTypeClient = getDbOperations().retrieveNagiosAgentInfo(getWebAppUser(), monitorAgentInfoTypeClient);

		cpuInfoTypeClientList = nrpeDataInfoTypeClient.getCpuInfoTypeClientList();
		diskInfoTypeClientList = nrpeDataInfoTypeClient.getDiskInfoTypeClientList();
		memoryInfoTypeClientList = nrpeDataInfoTypeClient.getMemoryInfoTypeClientList();

		// bos alan yuzde degerleri hesaplaniyor
		//calculatePercentages();
	}

	private void calculatePercentages() {
		DecimalFormat twoDForm = new DecimalFormat("#.##");

		if (diskInfoTypeClientList != null) {
			// diskin bos kisminin yuzdesi hesaplaniyor
			for (int i = 0; i < diskInfoTypeClientList.size(); i++) {
				double freeDisk = Double.parseDouble(diskInfoTypeClientList.get(i).getFreeDisk());
				double usedDisk = Double.parseDouble(diskInfoTypeClientList.get(i).getUsedDisk());

				diskInfoTypeClientList.get(i).setFreePercentage(twoDForm.format(freeDisk * 100 / (freeDisk + usedDisk)) + "");
			}
		}

		if (memoryInfoTypeClientList != null) {
			// bellegin bos kisminin yuzdesi hesaplaniyor
			for (int i = 0; i < memoryInfoTypeClientList.size(); i++) {
				double freeMemory = Double.parseDouble(memoryInfoTypeClientList.get(i).getFreeMemory());
				double usedMemory = Double.parseDouble(memoryInfoTypeClientList.get(i).getUsedMemory());

				memoryInfoTypeClientList.get(i).setFreePercentage(twoDForm.format(freeMemory * 100 / (freeMemory + usedMemory)) + "");
			}
		}
	}

	public List<CpuInfoTypeClient> getCpuInfoTypeClientList() {
		return cpuInfoTypeClientList;
	}

	public void setCpuInfoTypeClientList(List<CpuInfoTypeClient> cpuInfoTypeClientList) {
		this.cpuInfoTypeClientList = cpuInfoTypeClientList;
	}

	public List<DiskInfoTypeClient> getDiskInfoTypeClientList() {
		return diskInfoTypeClientList;
	}

	public void setDiskInfoTypeClientList(List<DiskInfoTypeClient> diskInfoTypeClientList) {
		this.diskInfoTypeClientList = diskInfoTypeClientList;
	}

	public List<MemoryInfoTypeClient> getMemoryInfoTypeClientList() {
		return memoryInfoTypeClientList;
	}

	public void setMemoryInfoTypeClientList(List<MemoryInfoTypeClient> memoryInfoTypeClientList) {
		this.memoryInfoTypeClientList = memoryInfoTypeClientList;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getNrpePort() {
		return nrpePort;
	}

	public void setNrpePort(String nrpePort) {
		this.nrpePort = nrpePort;
	}

}

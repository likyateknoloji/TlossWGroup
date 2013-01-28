/*
 * TlosSW_V1.0_Model
 * com.likya.tlossw.model.client.resource : NrpeDataInfoTypeClient.java
 * @author Merve Ozbey
 * Tarih : 29.Sub.2012 09:55:35
 */

package com.likya.tlossw.model.client.resource;

import java.io.Serializable;
import java.util.ArrayList;

//sunucudan makinenin kullanim bilgisi istendiginde bu data tipiyle donus yapacak

public class NrpeDataInfoTypeClient implements Serializable {

	private static final long serialVersionUID = -92807784857153311L;
	
	private ArrayList<CpuInfoTypeClient> cpuInfoTypeClientList = new ArrayList<CpuInfoTypeClient>();
	
	private ArrayList<DiskInfoTypeClient> diskInfoTypeClientList = new ArrayList<DiskInfoTypeClient>();
	
	private ArrayList<MemoryInfoTypeClient> memoryInfoTypeClientList = new ArrayList<MemoryInfoTypeClient>();
	
	//sorgulandigi tarih,zaman
	private String time;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public ArrayList<CpuInfoTypeClient> getCpuInfoTypeClientList() {
		return cpuInfoTypeClientList;
	}

	public void setCpuInfoTypeClientList(
			ArrayList<CpuInfoTypeClient> cpuInfoTypeClientList) {
		this.cpuInfoTypeClientList = cpuInfoTypeClientList;
	}

	public ArrayList<DiskInfoTypeClient> getDiskInfoTypeClientList() {
		return diskInfoTypeClientList;
	}

	public void setDiskInfoTypeClientList(
			ArrayList<DiskInfoTypeClient> diskInfoTypeClientList) {
		this.diskInfoTypeClientList = diskInfoTypeClientList;
	}

	public ArrayList<MemoryInfoTypeClient> getMemoryInfoTypeClientList() {
		return memoryInfoTypeClientList;
	}

	public void setMemoryInfoTypeClientList(
			ArrayList<MemoryInfoTypeClient> memoryInfoTypeClientList) {
		this.memoryInfoTypeClientList = memoryInfoTypeClientList;
	}

}

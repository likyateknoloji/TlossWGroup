/*
 * TlosSW_V1.0_Model
 * com.likya.tlossw.model.client.resource : DiskInfoTypeClient.java
 * @author Merve Ozbey
 * Tarih : 29.Sub.2012 09:55:35
 */

package com.likya.tlossw.model.client.resource;

import java.io.Serializable;

public class DiskInfoTypeClient implements Serializable {

	private static final long serialVersionUID = -7874500528279124060L;

	private String diskUnit;
	
	private String usedDisk;
	private String freeDisk;
	private String freePercentage;
	
	public String getDiskUnit() {
		return diskUnit;
	}
	public void setDiskUnit(String diskUnit) {
		this.diskUnit = diskUnit;
	}
	public String getUsedDisk() {
		return usedDisk;
	}
	public void setUsedDisk(String usedDisk) {
		this.usedDisk = usedDisk;
	}
	public String getFreeDisk() {
		return freeDisk;
	}
	public void setFreeDisk(String freeDisk) {
		this.freeDisk = freeDisk;
	}
	public void setFreePercentage(String freePercentage) {
		this.freePercentage = freePercentage;
	}
	public String getFreePercentage() {
		return freePercentage;
	}
	
}
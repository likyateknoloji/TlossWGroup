/*
 * TlosSW_V1.0_Model
 * com.likya.tlossw.model.client.resource : MemoryInfoTypeClient.java
 * @author Merve Ozbey
 * Tarih : 29.Sub.2012 09:55:35
 */

package com.likya.tlossw.model.client.resource;

import java.io.Serializable;

public class MemoryInfoTypeClient implements Serializable {

	private static final long serialVersionUID = -1277961455236619192L;

	private String memoryUnit;
	
	private String usedMemory;
	private String freeMemory;
	private String freePercentage;
	
	public void setMemoryUnit(String memoryUnit) {
		this.memoryUnit = memoryUnit;
	}
	public String getMemoryUnit() {
		return memoryUnit;
	}
	public void setUsedMemory(String usedMemory) {
		this.usedMemory = usedMemory;
	}
	public String getUsedMemory() {
		return usedMemory;
	}
	public void setFreeMemory(String freeMemory) {
		this.freeMemory = freeMemory;
	}
	public String getFreeMemory() {
		return freeMemory;
	}
	public void setFreePercentage(String freePercentage) {
		this.freePercentage = freePercentage;
	}
	public String getFreePercentage() {
		return freePercentage;
	}
	
}
/*
 * TlosSW_V1.0_Model
 * com.likya.tlossw.model.client.resource : CpuInfoTypeClient.java
 * @author Merve Ozbey
 * Tarih : 29.Sub.2012 09:55:35
 */

package com.likya.tlossw.model.client.resource;

import java.io.Serializable;

public class CpuInfoTypeClient implements Serializable {

	private static final long serialVersionUID = -5515964343669496851L;

	private String cpuUnit;
	
	private String usedCpuOneMin;
	private String usedCpuFiveMin;
	private String usedCpuFifteenMin;
	
	public String getCpuUnit() {
		return cpuUnit;
	}
	public void setCpuUnit(String cpuUnit) {
		this.cpuUnit = cpuUnit;
	}
	public String getUsedCpuOneMin() {
		return usedCpuOneMin;
	}
	public void setUsedCpuOneMin(String usedCpuOneMin) {
		this.usedCpuOneMin = usedCpuOneMin;
	}
	public String getUsedCpuFiveMin() {
		return usedCpuFiveMin;
	}
	public void setUsedCpuFiveMin(String usedCpuFiveMin) {
		this.usedCpuFiveMin = usedCpuFiveMin;
	}
	public String getUsedCpuFifteenMin() {
		return usedCpuFifteenMin;
	}
	public void setUsedCpuFifteenMin(String usedCpuFifteenMin) {
		this.usedCpuFifteenMin = usedCpuFifteenMin;
	}
	
}
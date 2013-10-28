/*
 * TlosFaz_V2.0
 * com.likya.tlos.core.cpc.model : InstanceType.java
 * @author Serkan Ta�
 * Tarih : 13.Nis.2010 10:56:13
 */

package com.likya.tlossw.core.cpc.model;

import java.io.Serializable;

import com.likya.tlossw.model.SpcLookupTable;

public class RunInfoType implements Serializable {

	private static final long serialVersionUID = -6440698971800044548L;

	private String runId;
	private SpcLookupTable spcLookupTable;

	public SpcLookupTable getSpcLookupTable() {
		return spcLookupTable;
	}

	public void setSpcLookupTable(SpcLookupTable spcLookupTable) {
		this.spcLookupTable = spcLookupTable;
	}

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

}

package com.likya.tlossw.model;

import java.io.Serializable;
import java.util.HashMap;

import com.likya.tlossw.core.cpc.model.SpcInfoType;

public class SpcLookupTable implements Serializable {

	private static final long serialVersionUID = 4725772513971248573L;
	
	private HashMap<String, SpcInfoType> table;

	public SpcLookupTable() {
		super();
		table = new HashMap<String, SpcInfoType>();
	}
	
	public SpcLookupTable(HashMap<String, SpcInfoType> table) {
		super();
		this.table = table;
	}

	public HashMap<String, SpcInfoType> getTable() {
		return table;
	}

	public void setTable(HashMap<String, SpcInfoType> table) {
		this.table = table;
	}
	
}

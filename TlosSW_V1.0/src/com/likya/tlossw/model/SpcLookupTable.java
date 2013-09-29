package com.likya.tlossw.model;

import java.io.Serializable;
import java.util.HashMap;

import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.model.path.ScenarioPathType;

public class SpcLookupTable implements Serializable {

	private static final long serialVersionUID = 4725772513971248573L;
	
	private HashMap<ScenarioPathType, SpcInfoType> table;

	public SpcLookupTable() {
		super();
		table = new HashMap<ScenarioPathType, SpcInfoType>();
	}
	
	public SpcLookupTable(HashMap<ScenarioPathType, SpcInfoType> table) {
		super();
		this.table = table;
	}

	public HashMap<ScenarioPathType, SpcInfoType> getTable() {
		return table;
	}

	public void setTable(HashMap<ScenarioPathType, SpcInfoType> table) {
		this.table = table;
	}
	
}

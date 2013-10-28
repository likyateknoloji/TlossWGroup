/*
 * TlosFaz_V2.0
 * com.likya.tlos.core.spc.helpers : PlanMapHelper.java
 * @author Serkan Taş
 * Tarih : 13.Nis.2010 11:34:26
 */

package com.likya.tlossw.core.spc.helpers;

import java.util.HashMap;

import com.likya.tlossw.core.cpc.model.RunInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;


public class RunMapHelper {

	public static SpcInfoType findSpc(String spcFullPath, HashMap<String, RunInfoType> runLookUpTable) {
		
		for (String runId : runLookUpTable.keySet()) {
			RunInfoType runInfoType = runLookUpTable.get(runId);

			HashMap<String, SpcInfoType> spcLookupTable = runInfoType.getSpcLookupTable().getTable();
			
			if(spcLookupTable.containsKey(spcFullPath)) {
				return spcLookupTable.get(spcFullPath);
			}
			
		}
		
		return null;
	}
	
	public static SpcInfoType findSpc(String runId, String spcId, HashMap<String, RunInfoType> runLookUpTable) {
		
		if (runId != null && runLookUpTable.get(runId) != null) {
			
			RunInfoType runInfoType = runLookUpTable.get(runId);

			HashMap<String, SpcInfoType> spcLookupTable = runInfoType.getSpcLookupTable().getTable();
			
			if(spcLookupTable.containsKey(spcId)) {
				return spcLookupTable.get(spcId);
			}
			
		}
		
		return null;
	}

}

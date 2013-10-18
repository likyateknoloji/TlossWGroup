/*
 * TlosFaz_V2.0
 * com.likya.tlos.core.spc.helpers : PlanMapHelper.java
 * @author Serkan Taş
 * Tarih : 13.Nis.2010 11:34:26
 */

package com.likya.tlossw.core.spc.helpers;

import java.util.HashMap;

import com.likya.tlossw.core.cpc.model.PlanInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;


public class PlanMapHelper {

	public static SpcInfoType findSpc(String spcId, HashMap<String, PlanInfoType> planLookUpTable) {
		
		for (String planId : planLookUpTable.keySet()) {
			PlanInfoType planInfoType = planLookUpTable.get(planId);

			HashMap<String, SpcInfoType> spcLookupTable = planInfoType.getSpcLookupTable().getTable();
			
			if(spcLookupTable.containsKey(spcId)) {
				return spcLookupTable.get(spcId);
			}
			
		}
		
		return null;
	}
	
	public static SpcInfoType findSpc(String planId, String spcId, HashMap<String, PlanInfoType> planLookUpTable) {
		
		if (planId != null && planLookUpTable.get(planId) != null) {
			
			PlanInfoType planInfoType = planLookUpTable.get(planId);

			HashMap<String, SpcInfoType> spcLookupTable = planInfoType.getSpcLookupTable().getTable();
			
			if(spcLookupTable.containsKey(spcId)) {
				return spcLookupTable.get(spcId);
			}
			
		}
		
		return null;
	}

}

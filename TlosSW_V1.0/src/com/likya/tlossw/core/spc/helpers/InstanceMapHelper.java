/*
 * TlosFaz_V2.0
 * com.likya.tlos.core.spc.helpers : InstanceMapHelper.java
 * @author Serkan Taş
 * Tarih : 13.Nis.2010 11:34:26
 */

package com.likya.tlossw.core.spc.helpers;

import java.util.HashMap;

import com.likya.tlossw.core.cpc.model.PlanInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;


public class InstanceMapHelper {

	public static SpcInfoType findSpc(String spcId, HashMap<String, PlanInfoType> instanceLookUpTable) {
		
		for (String planId : instanceLookUpTable.keySet()) {
			PlanInfoType instanceInfoType = instanceLookUpTable.get(planId);

			HashMap<String, SpcInfoType> spcLookupTable = instanceInfoType.getSpcLookupTable().getTable();
			
			if(spcLookupTable.containsKey(spcId)) {
				return spcLookupTable.get(spcId);
			}
			
		}
		
		return null;
	}
	
	public static SpcInfoType findSpc(String planId, String spcId, HashMap<String, PlanInfoType> instanceLookUpTable) {
		
		if (planId != null && instanceLookUpTable.get(planId) != null) {
			
			PlanInfoType instanceInfoType = instanceLookUpTable.get(planId);

			HashMap<String, SpcInfoType> spcLookupTable = instanceInfoType.getSpcLookupTable().getTable();
			
			if(spcLookupTable.containsKey(spcId)) {
				return spcLookupTable.get(spcId);
			}
			
		}
		
		return null;
	}

}

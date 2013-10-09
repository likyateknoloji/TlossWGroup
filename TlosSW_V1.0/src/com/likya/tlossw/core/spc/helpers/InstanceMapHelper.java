/*
 * TlosFaz_V2.0
 * com.likya.tlos.core.spc.helpers : InstanceMapHelper.java
 * @author Serkan Ta�
 * Tarih : 13.Nis.2010 11:34:26
 */

package com.likya.tlossw.core.spc.helpers;

import java.util.HashMap;

import com.likya.tlossw.core.cpc.model.PlanInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;


public class InstanceMapHelper {

	public static SpcInfoType findSpc(String spcId, HashMap<String, PlanInfoType> instanceLookUpTable) {
		
		for (String instanceId : instanceLookUpTable.keySet()) {
			PlanInfoType instanceInfoType = instanceLookUpTable.get(instanceId);

			HashMap<String, SpcInfoType> spcLookupTable = instanceInfoType.getSpcLookupTable().getTable();
			
			if(spcLookupTable.containsKey(spcId)) {
				return spcLookupTable.get(spcId);
			}
			
		}
		
		return null;
	}
	
	public static SpcInfoType findSpc(String instanceId, String spcId, HashMap<String, PlanInfoType> instanceLookUpTable) {
		
		if (instanceId != null && instanceLookUpTable.get(instanceId) != null) {
			
			PlanInfoType instanceInfoType = instanceLookUpTable.get(instanceId);

			HashMap<String, SpcInfoType> spcLookupTable = instanceInfoType.getSpcLookupTable().getTable();
			
			if(spcLookupTable.containsKey(spcId)) {
				return spcLookupTable.get(spcId);
			}
			
		}
		
		return null;
	}

}

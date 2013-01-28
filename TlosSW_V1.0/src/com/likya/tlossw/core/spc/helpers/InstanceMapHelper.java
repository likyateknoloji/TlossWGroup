/*
 * TlosFaz_V2.0
 * com.likya.tlos.core.spc.helpers : InstanceMapHelper.java
 * @author Serkan Taþ
 * Tarih : 13.Nis.2010 11:34:26
 */

package com.likya.tlossw.core.spc.helpers;

import java.util.HashMap;

import com.likya.tlossw.core.cpc.model.InstanceInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;


public class InstanceMapHelper {

	public static SpcInfoType findSpc(String spcId, HashMap<String, InstanceInfoType> instanceLookUpTable) {
		
		for (String instanceId : instanceLookUpTable.keySet()) {
			InstanceInfoType instanceInfoType = instanceLookUpTable.get(instanceId);

			HashMap<String, SpcInfoType> spcLookupTable = instanceInfoType.getSpcLookupTable();
			
			if(spcLookupTable.containsKey(spcId)) {
				return spcLookupTable.get(spcId);
			}
			
		}
		
		return null;
	}
	
	public static SpcInfoType findSpc(String instanceId, String spcId, HashMap<String, InstanceInfoType> instanceLookUpTable) {
		
		if (instanceId != null && instanceLookUpTable.get(instanceId) != null) {
			
			InstanceInfoType instanceInfoType = instanceLookUpTable.get(instanceId);

			HashMap<String, SpcInfoType> spcLookupTable = instanceInfoType.getSpcLookupTable();
			
			if(spcLookupTable.containsKey(spcId)) {
				return spcLookupTable.get(spcId);
			}
			
		}
		
		return null;
	}

}

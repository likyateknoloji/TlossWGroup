package com.likya.tlossw.utils;

import java.util.HashMap;

import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.cpc.model.PlanInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;

public class PlanUtils {
	
	public static boolean checkRecovery(HashMap<String, PlanInfoType> planLookupTable) {
		
		for (String planId : planLookupTable.keySet()) {
			PlanInfoType planInfoType = planLookupTable.get(planId);

			HashMap<String, SpcInfoType> spcLookupTable = planInfoType.getSpcLookupTable().getTable();

			for (String spcId : spcLookupTable.keySet()) {
				SpcInfoType mySpcInfoType = spcLookupTable.get(spcId);
				Spc spc = mySpcInfoType.getSpcReferance();
//				if(spc == null) System.out.println("InstanceUtils---------------NULL------------------------------>" + spcId);
				if (spc == null || !spc.isRecovered()) {
//					System.out.println("InstanceUtils---------------!RECOVERED------------------------------>" + spcId);
					return false;
				}
//				System.out.println("InstanceUtils---------------RECOVERED------------------------------>" + spcId);
			}
		}
		
		return true;
		
	}
	
	public static boolean runningPlanExists(HashMap<String, PlanInfoType> planLookupTable) {
		boolean exist = false;
		
		for (String planId : planLookupTable.keySet()) {
			PlanInfoType planInfoType = planLookupTable.get(planId);

			HashMap<String, SpcInfoType> spcLookupTable = planInfoType.getSpcLookupTable().getTable();

			for (String spcId : spcLookupTable.keySet()) {
				SpcInfoType mySpcInfoType = spcLookupTable.get(spcId);
				Spc spc = mySpcInfoType.getSpcReferance();
				
				if(!(spc.getLiveStateInfo().getStateName().equals(StateName.FINISHED) && spc.getLiveStateInfo().getSubstateName().equals(SubstateName.COMPLETED))) {
					exist = true;
				}
			}
		}
		return exist;
	}

}

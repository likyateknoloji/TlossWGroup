package com.likya.tlossw.core.agents;

import java.util.HashMap;

import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.cpc.model.InstanceInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;

public class AgentOperations {

	public static void failJobsForAgent(int agentId) {
		for (String instanceId : TlosSpaceWide.getSpaceWideRegistry().getInstanceLookupTable().keySet()) {

			InstanceInfoType instanceInfoType = TlosSpaceWide.getSpaceWideRegistry().getInstanceLookupTable().get(instanceId);
			HashMap<String, SpcInfoType> spcLookupTable = instanceInfoType.getSpcLookupTable();

			for (String spcId : spcLookupTable.keySet()) {
				Spc spc = spcLookupTable.get(spcId).getSpcReferance();
				spc.failAgentJobs(agentId);
			}
		}
	}

}

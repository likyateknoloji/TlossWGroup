package com.likya.tlossw.core.agents;

import java.util.HashMap;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.cpc.model.InstanceInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.model.client.resource.ResourceInfoTypeClient;
import com.likya.tlossw.model.tree.resource.ResourceNode;
import com.likya.tlossw.utils.ConstantDefinitions;

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
	
	public static HashMap<String, ResourceNode> getResources(HashMap<String, SWAgent> agentCache) {
		
		HashMap<String, ResourceNode> resourceMap = new HashMap<String, ResourceNode>();
		
		for (String agentId : agentCache.keySet()) {
		
			SWAgent swAgent = agentCache.get(agentId);
			
			String resourceName = swAgent.getResource().getStringValue();
			
			if (swAgent.getAgentType().toString().equals(ConstantDefinitions.AGENT_ON_SERVER)) {
				resourceMap.get(resourceName).getResourceInfoTypeClient().setIncludesServer(true);
			}
			
			if(resourceMap.containsKey(resourceName)) {
				continue;
			}
			
			ResourceNode resourceNode = new ResourceNode();

			ResourceInfoTypeClient resourceInfoTypeClient = new ResourceInfoTypeClient();
			resourceInfoTypeClient.setResourceName(swAgent.getResource().getStringValue());
			resourceInfoTypeClient.setOsType(swAgent.getOsType().toString());

			if (swAgent.getOutJmxAvailable()) {
				resourceInfoTypeClient.setActive(true);
			}
			
			resourceNode.setResourceInfoTypeClient(resourceInfoTypeClient);
			
			resourceMap.put(resourceName, resourceNode);
		}

		
		return resourceMap;
	}

}

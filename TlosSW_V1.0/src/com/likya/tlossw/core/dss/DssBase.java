package com.likya.tlossw.core.dss;

import java.util.Calendar;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceType;
import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceAgentListDocument.ResourceAgentList;
import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceDocument.Resource;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.agents.AgentManager;
import com.likya.tlossw.perfmng.PerformanceManager;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.date.DateUtils;

public abstract class DssBase {

	private static Logger myLogger = Logger.getLogger(DssBase.class);

	protected static void dumpResourceAvailability(String jobId, ResourceAgentList resourceAgentList) {

		String allResourcesText = "";

		int kacKaynakVar = 0;

		for (Resource resource : resourceAgentList.getResourceArray()) {
			if (resource.enumValue().equals(ResourceType.FALSE)) {
				myLogger.info("     > JOB " + jobId + " icin " + ResourceType.FALSE + resource);
			} else {
				if (kacKaynakVar > 0) {
					allResourcesText = allResourcesText + " ve " + resource.getAgentid();
				} else {
					allResourcesText = "" + resource.getAgentid();
				}
				kacKaynakVar++;
			}

		}

		myLogger.info("     > JOB " + jobId + " icin " + kacKaynakVar + " adet uygun kaynak var;" + allResourcesText);
        if(SpaceWideRegistry.isDebug) {
        	System.out.println("     > JOB " + jobId + " icin " + kacKaynakVar + " adet uygun kaynak var;" + allResourcesText);
        }
	}

	public static boolean isResourceListExpired(Calendar agentListTime, long amountOfTimeToExpire) {

		boolean isExpired = true;

		long diffInSeconds = DateUtils.dateDiffWithNow(agentListTime) / 1000;

		if (diffInSeconds < amountOfTimeToExpire) {
			isExpired = false;
		}

		return isExpired;
	}
	
	protected static ResourceAgentList getAvailableResources(String jobId, ResourceAgentList resourceAgentList) {
		
		PerformanceManager performanceManager = TlosSpaceWide.getSpaceWideRegistry().getPerformanceManagerReference();
		AgentManager agentManagerReference = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference();
		
		ResourceAgentList availableResourceList = ResourceAgentList.Factory.newInstance();
		
		for (Resource resource : resourceAgentList.getResourceArray()) {
			
			if (resource.enumValue().equals(ResourceType.FALSE)) {
				myLogger.info("     > JOB " + jobId + " icin " + ResourceType.FALSE + " " + resource);
				if(SpaceWideRegistry.isDebug) {
					System.out.println("     > JOB " + jobId + " icin " + ResourceType.FALSE + " " + resource);
				}
				continue;
			}
			
			int agentId = resource.getAgentid();
			
			/*
			 * Kaynakta cok fazla is calistirilmasina engel olmak gerekiyor. Bu
			 * nedenle Config.xml deki performance/treshold/high ve low degerleri
			 * kullanilacak
			 */
			if (performanceManager == null || !performanceManager.checkThresholdOverflow(agentManagerReference.getSwAgentCache(agentId + "").getIsPermitted(), agentId)) {
				agentManagerReference.getSwAgentCache(agentId).setIsPermitted(true);
			} else {
				agentManagerReference.getSwAgentCache(agentId).setIsPermitted(false);
			}
			
			if(!agentManagerReference.getSwAgentCache(agentId).getIsPermitted()) {
				
				int numberOfRunningJobs = agentManagerReference.numberOfRunningJobs(agentId);
				int lowerThresholdValue = TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo().getPerformance().getOverAllThreshold().getLow();
				
				myLogger.info("     > JOB " + jobId + " icin AgentId = " + agentId + " cok fazla sayida is ile (" + numberOfRunningJobs + ") yuklu oldugundan su anda kullanilamaz !" + "kaynagin kullanilabilmesi icin islerin set edilen " + lowerThresholdValue + " degerinin altina dusmesi gerekir.");
				if(SpaceWideRegistry.isDebug) {
					System.out.println("     > JOB " + jobId + " icin AgentId = " + agentId + " cok fazla sayida is ile (" + numberOfRunningJobs + ") yuklu oldugundan su anda kullanilamaz !" + "kaynagin kullanilabilmesi icin islerin set edilen " + lowerThresholdValue + " degerinin altina dusmesi gerekir.");
				}
				continue;
			
			}
			
			Resource newResource = availableResourceList.addNewResource();
			newResource.set(resource);
			
			myLogger.info("     > JOB " + jobId + " icin " + ResourceType.TRUE + " " + newResource);
			if(SpaceWideRegistry.isDebug) {
				System.out.println("     > JOB " + jobId + " icin " + ResourceType.TRUE + " " + newResource);
			}
		}
		
		myLogger.info("     > JOB " + jobId + " icin " + availableResourceList.sizeOfResourceArray() + " adet uygun kaynak var.");
		if(SpaceWideRegistry.isDebug) {
			System.out.println("     > JOB " + jobId + " icin " + availableResourceList.sizeOfResourceArray() + " adet uygun kaynak var.");
		}
		
		return availableResourceList;
	}
	
	protected static Resource find(String agentId, ResourceAgentList resourceAgentList) {
		
		for (Resource resource : resourceAgentList.getResourceArray()) {
			if(agentId.equals("" + resource.getAgentid())) {
				return resource;
			}
		}
		
		return null;
	}

}

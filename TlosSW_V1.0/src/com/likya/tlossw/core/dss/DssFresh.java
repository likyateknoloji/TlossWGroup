package com.likya.tlossw.core.dss;

import java.util.Random;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.common.AgentChoiceMethodDocument.AgentChoiceMethod;
import com.likya.tlos.model.xmlbeans.common.ChoiceType;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceAgentListDocument.ResourceAgentList;
import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceDocument.Resource;
import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceType;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.agents.AgentManager;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.db.utils.DBUtils;
import com.likya.tlossw.db.utils.DssDbUtils;
import com.likya.tlossw.perfmng.PerformanceManager;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.date.DateUtils;

public class DssFresh {

	private static Logger myLogger = Logger.getLogger(DssFresh.class);

	public static boolean transferPermission(Job job) {

		boolean transferPermission = false;

		LiveStateInfo liveStateInfo = job.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0);
		JobProperties jobProperties = job.getJobRuntimeProperties().getJobProperties();
		String jobId = jobProperties.getID();

		if (!(LiveStateInfoUtils.equalStates(liveStateInfo, StateName.PENDING, SubstateName.READY, StatusName.LOOKFOR_RESOURCE))) {
			/* LOOKFOR-RESOURCE state i ekle */
			LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_PENDING, SubstateName.INT_READY, StatusName.INT_LOOKFOR_RESOURCE);
			job.sendStatusChangeInfo();
		}

		ResourceAgentList resourceAgentList = job.getResourceAgentList();

		if (resourceAgentList == null || isResourceListExpired(resourceAgentList)) {
			resourceAgentList = DssDbUtils.swFindResourcesForAJob(jobProperties);
		}

		int numberOfResources = resourceAgentList.sizeOfResourceArray();

		if (numberOfResources == 0) {

			myLogger.info("     > JOB " + jobId + " icin kaynak listesi bo� oldu�undan !! KAYNAK ATANAMADI !!");

			return false;

		} else {
			job.setResourceAgentList(resourceAgentList);
			dumpResourceAvailability(jobId, resourceAgentList);

		}

		/*
		 * Kaynakta cok fazla is calistirilmasina engel olmak gerekiyor. Bu
		 * nedenle Config.xml deki performance/treshold/high ve low degerleri
		 * kullanilacak
		 */

		/* Kaynak listesi, uygun olmayanlardan temizlensin */

		ResourceAgentList resourceAgentListTrue = cleanUnavailableResoucesFromList(jobId, resourceAgentList);
		int numberOfAvailableResources = resourceAgentListTrue.sizeOfResourceArray();

		if (numberOfAvailableResources == 0) {

			System.out.println("JOB:" + jobProperties.getID() + "---> Kaynaklar aras�nda uygun kaynak bulunamad���ndan !! KAYNAK YINE ATANAMADI !!");

			return false;

		}

		transferPermission = selectResource(job, resourceAgentListTrue);

		return transferPermission;
	}

	public static boolean isResourceListExpired(ResourceAgentList resourceAgentList) {
		boolean isStale = false;

		long creationTime = resourceAgentList.getTime();
		long currentTime = System.currentTimeMillis();
		long diffInSeconds = (currentTime - creationTime) / 1000;

		long timeToStale = TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo().getSettings().getAgentOptions().getResourceListDuration().getDurationValue();

		if (diffInSeconds >= timeToStale) {
			isStale = true;
		}

		return isStale;
	}

	private static ResourceAgentList cleanUnavailableResoucesFromList(String jobId, ResourceAgentList resourceAgentList) {

		/* Kaynak listesi, uygun olmayanlardan temizlensin */
		ResourceAgentList resourceAgentListTrue = ResourceAgentList.Factory.newInstance();

		int upperThresholdValue = TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo().getPerformance().getThreshold().getHigh();
		int lowerThresholdValue = TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo().getPerformance().getThreshold().getLow();

		PerformanceManager performanceManager = TlosSpaceWide.getSpaceWideRegistry().getPerformanceManagerReference();
		AgentManager agentManagerRef = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference();

		for (Resource resource : resourceAgentList.getResourceArray()) {

			// Kaynaklardan uygun olmayanlari <FALSE> ayri tutalim.

			int agentId = resource.getAgentid();

			if (performanceManager == null || performanceManager.checkThresholdOverflow(agentManagerRef.getSwAgentCache(agentId + "").getIsPermitted(), agentId)) {
				agentManagerRef.getSwAgentCache(agentId + "").setIsPermitted(true);
			} else {
				agentManagerRef.getSwAgentCache(agentId + "").setIsPermitted(false);
			}

			boolean overFlow = agentManagerRef.getSwAgentCache(agentId + "").getIsPermitted();

			int numberOfRunningJobsByThisAgent = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference().numberOfRunningJobs(agentId);

			if (resource.enumValue().equals(ResourceType.TRUE) && !overFlow) {

				resourceAgentListTrue.addNewResource();
				resourceAgentListTrue.setResourceArray(resourceAgentListTrue.sizeOfResourceArray() - 1, resource);

				myLogger.info("     > JOB " + jobId + " icin " + ResourceType.TRUE + resourceAgentListTrue.getResourceArray(resourceAgentListTrue.sizeOfResourceArray() - 1));

			} else {

				if (overFlow) {
					int deger;
					if (numberOfRunningJobsByThisAgent > upperThresholdValue) {
						deger = upperThresholdValue;
					} else {
						deger = lowerThresholdValue;
					}
					myLogger.info("     > JOB " + jobId + " icin AgentId = " + agentId + " cok fazla sayida is ile (" + numberOfRunningJobsByThisAgent + ") yuklu oldugundan su anda kullanilamaz !" + "kaynagin kullanilabilmesi icin islerin set edilen " + deger + " degerinin altina dusmesi gerekir.");
				}

			}

			myLogger.info("     > JOB " + jobId + " icin " + resourceAgentListTrue.sizeOfResourceArray() + " adet uygun kaynak var.");
		}

		return resourceAgentListTrue;
	}

	private static int applySelectionAlgorithm(Job job, AgentChoiceMethod agentChoiceMethod, ResourceAgentList resourceAgentListTrue, int counter) {

		Random randomGenerator = new Random();

		int selectedResource = -1;

		boolean userChooseResourceStateFlag = false;

		int numberOfAvailableResources = resourceAgentListTrue.sizeOfResourceArray();

		switch (agentChoiceMethod.enumValue().intValue()) {

		case ChoiceType.INT_SIMPLE_METASCHEDULER:

			selectedResource = randomGenerator.nextInt(numberOfAvailableResources);
			myLogger.info("     > Kaynak atamasi uygun olan " + numberOfAvailableResources + " kaynak icinden rastgele secimle yapildi (SimpleMetascheduler)");
			myLogger.info("       > " + selectedResource + " ile " + resourceAgentListTrue.getResourceArray(selectedResource) + " kaynak olarak secildi.");
			break;

		case ChoiceType.INT_USER_INTERACTION_PREFERENCE:

			if (job.getSelectedAgentId() == null && !userChooseResourceStateFlag) {

				LiveStateInfoUtils.insertNewLiveStateInfo(job.getJobRuntimeProperties().getJobProperties(), StateName.INT_PENDING, SubstateName.INT_READY, StatusName.INT_USER_CHOOSE_RESOURCE);

				job.sendStatusChangeInfo();

				job.setResourceAgentListTrue(resourceAgentListTrue);

				myLogger.info("     > Kaynak atamasi uygun olan " + numberOfAvailableResources + " kaynak icinden kullanici secsin (UserInteractionPreference).");
				userChooseResourceStateFlag = true;

			} else if (job.getSelectedAgentId() != null) {

				// is, kullanicinin ekrandan girdigi kaynaga ataniyor
				for (int agentIndex = 0; agentIndex < resourceAgentListTrue.sizeOfResourceArray(); agentIndex++) {
					if (job.getSelectedAgentId().equals(resourceAgentListTrue.getResourceArray(agentIndex).getAgentid() + "")) {
						selectedResource = agentIndex;

						myLogger.info("     > Kaynak atamasi uygun olan " + numberOfAvailableResources + " kaynak icinden kullanici secimi ile yapildi");
						myLogger.info("       >" + selectedResource + " ile " + resourceAgentListTrue.getResourceArray(selectedResource) + " kaynak olarak secildi.");

						break;
					}
				}

			}

			break;

		case ChoiceType.INT_USER_MANDATORY_PREFERENCE:

			myLogger.info("     > Kaynak atamasi kullanicinin sectigi kaynak olacak (UserMandatoryPreference).");
			if (agentChoiceMethod.getAgentId().equals("" + resourceAgentListTrue.getResourceArray(counter).getAgentid())) {
				myLogger.info("     > Kaynak atamasi uygun olan kullanici secimi ile yapildi. " + agentChoiceMethod.getAgentId());
				selectedResource = counter;
			}
			// myLogger.info("     > Kaynak atamasi uygun olan kullanici secimi ile yapiliyor...");
			break;

		case ChoiceType.INT_ADVANCED_METASCHEDULER:
			myLogger.info("     > Kaynak atamasi ileri teknikler kullanilarak yapilacak(AdvancedMetascheduler).");
			break;

		default:
			myLogger.info("     > Kaynak atamasi icin ek yontem gerekiyor.");
			break;
		}

		return selectedResource;
	}

	private static boolean selectResource(Job job, ResourceAgentList resourceAgentListTrue) {

		boolean returnValue = false;

		JobProperties jobProperties = job.getJobRuntimeProperties().getJobProperties();

		AgentChoiceMethod agentChoiceMethod = job.getJobRuntimeProperties().getJobProperties().getAdvancedJobInfos().getAgentChoiceMethod();

		for (int i = 0; i < 100; i = i + 1) { // 100 kere dene

			int selectedResource = applySelectionAlgorithm(job, agentChoiceMethod, resourceAgentListTrue, i);

			if (selectedResource < 0) {

				if (agentChoiceMethod.enumValue().intValue() != ChoiceType.INT_USER_MANDATORY_PREFERENCE) {
					/**
					 * agentChoiceMethod de�i�emeyen bir de�er oldu�undan, loop i�inde
					 * ne kadar d�nerse d�ns�n, e�er uygun algoritma yok ise s�rekli -1 gelecektir.
					 * Bu nedenle sonlanmas� uygundur.
					 * 
					 * @author serkan ta�
					 *         26.09.2012
					 */
					// selectedResource gerekli loglamay� yapmaktad�r.
					// Ba�ka loga gerek yok.
					break;
				} else {
					continue;
				}

			}

			Resource resource = resourceAgentListTrue.getResourceArray(selectedResource);
			SWAgent agent = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference().getSwAgentCache(resource.getAgentid() + "");

			if (resource.enumValue().equals(ResourceType.TRUE) && agent.getInJmxAvailable()) {

				// Job in hangi agent da calisacagi belli oldu. Tanimdaki agentId ye atayalim.
				jobProperties.setAgentId(resource.getAgentid());
				returnValue = true;
				/*
				 * Bu noktada ise kaynak ayrildi, artik gercek olarak
				 * var. O yuzden DailyScenarios a yeni bir is olarak
				 * kaydini yapalim.
				 */
				myLogger.info("     > " + jobProperties.getBaseJobInfos().getJsName() + " DB ye insert ediliyor !");

				job.getJobRuntimeProperties().getJobProperties().setLSIDateTime(DateUtils.getW3CDateTime());

				DBUtils.insertJob(jobProperties, ParsingUtils.getJobXPath(job.getJobRuntimeProperties().getTreePath()));

				/* TRANSFERING state i ekle */
				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_PENDING, SubstateName.INT_READY, StatusName.INT_TRANSFERING);
				job.sendStatusChangeInfo();

				break;
			}
		}

		return returnValue;
	}

	private static void dumpResourceAvailability(String jobId, ResourceAgentList resourceAgentList) {

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

	}
}

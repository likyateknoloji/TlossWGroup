package com.likya.tlossw.core.dss;

import java.util.Random;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceAgentListDocument.ResourceAgentList;
import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceDocument.Resource;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.agents.AgentManager;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.db.utils.DBUtils;
import com.likya.tlossw.db.utils.DssDbUtils;
import com.likya.tlossw.perfmng.PerformanceManager;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.date.DateUtils;

public class Dss {

	public static boolean transferPermission(Job job) {

		boolean transferPermission = false;

		LiveStateInfo liveStateInfo = job.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0);
		JobProperties jobProperties = job.getJobRuntimeProperties().getJobProperties();

		if (!(LiveStateInfoUtils.equalStates(liveStateInfo, StateName.PENDING, SubstateName.READY, StatusName.LOOKFOR_RESOURCE))) {
			/* LOOKFOR-RESOURCE state i ekle */
			LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_PENDING, SubstateName.INT_READY, StatusName.INT_LOOKFOR_RESOURCE);
			job.sendStatusChangeInfo();
		}

		if (job.getResourceAgentList() == null || isResourceListExpired(job.getResourceAgentList())) {
			getResourcesForJob(job);
		}

		/*
		 * Kaynakta cok fazla is calistirilmasina engel olmak gerekiyor. Bu
		 * nedenle Config.xml deki performance/treshold/high ve low degerleri
		 * kullanilacak
		 */

		/* Kaynak listesi, uygun olmayanlardan temizlensin */
		ResourceAgentList resourceAgentListTrue = ResourceAgentList.Factory.newInstance();
		if (job.getResourceAgentList() != null) {
			int counter = job.getResourceAgentList().sizeOfResourceArray();
			int kacKaynakVar = 0;

			if (counter > 0) {
				// job.setResourceAgentList(job.getResourceAgentList());
				String TrueType = "TRUE";

				int upperThresholdValue = TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo().getPerformance().getThreshold().getHigh();
				int lowerThresholdValue = TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo().getPerformance().getThreshold().getLow();

				PerformanceManager performanceManager = TlosSpaceWide.getSpaceWideRegistry().getPerformanceManagerReference();
				AgentManager agentManagerRef = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference();

				while (counter > 0) {
					// Kaynaklardan uygun olmayanlari <FALSE> ayri tutalim.

					int agentId = job.getResourceAgentList().getResourceArray(counter - 1).getAgentid();

					if (performanceManager == null || performanceManager.checkThresholdOverflow(agentManagerRef.getSwAgentCache(agentId + "").getIsPermitted(), agentId)) {
						agentManagerRef.getSwAgentCache(agentId + "").setIsPermitted(true);
					} else {
						agentManagerRef.getSwAgentCache(agentId + "").setIsPermitted(false);
					}

					boolean overFlow = agentManagerRef.getSwAgentCache(agentId + "").getIsPermitted();

					int numberOfRunningJobsByThisAgent = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference().numberOfRunningJobs(agentId);

					if (job.getResourceAgentList().getResourceArray(counter - 1).getStringValue().equals(TrueType) && !overFlow) {
						resourceAgentListTrue.addNewResource();
						resourceAgentListTrue.setResourceArray(kacKaynakVar, job.getResourceAgentList().getResourceArray(counter - 1));
						Logger.getLogger(Dss.class).info("     > JOB " + jobProperties.getID() + " icin TRUE " + resourceAgentListTrue.getResourceArray(kacKaynakVar));
						kacKaynakVar++;
					} else {
						if (overFlow) {
							int deger;
							if (numberOfRunningJobsByThisAgent > upperThresholdValue)
								deger = upperThresholdValue;
							else
								deger = lowerThresholdValue;
							Logger.getLogger(Dss.class).info("     > JOB " + jobProperties.getID() + " icin AgentId = " + agentId + " cok fazla sayida is ile (" + numberOfRunningJobsByThisAgent + ") yuklu oldugundan su anda kullanilamaz !" + "kaynagin kullanilabilmesi icin islerin set edilen " + deger + " degerinin altina dusmesi gerekir.");
						}
					}
					counter--;
					Logger.getLogger(Dss.class).info("     > JOB " + jobProperties.getID() + " icin " + kacKaynakVar + " adet uygun kaynak var.");
				}

				// Logger.getLogger(Dss.class).info("     > " + resourceAgentList.toString());
			} else {
				Logger.getLogger(Dss.class).info("     > JOB " + jobProperties.getID() + " icin !! KAYNAK ATANAMADI !!");
			}
			/****************************************************/

			Random randomGenerator = new Random();

			if (kacKaynakVar == 0) {

				System.out.println("JOB:" + jobProperties.getID() + "---> !! KAYNAK YINE ATANAMADI !!");

			} else {
				// int arraySize = job.getResourceAgentList().getResourceArray().length;

				boolean userChooseResourceStateFlag = false;

				for (int i = 0; i < 100; i = i + 1) { // 100 kere dene

					int hangiKaynak = -1;

					if (job.getJobRuntimeProperties().getJobProperties().getAdvancedJobInfos().getAgentChoiceMethod().getStringValue().equals((String) "SimpleMetascheduler")) {

						hangiKaynak = randomGenerator.nextInt(kacKaynakVar);
						Logger.getLogger(Dss.class).info("     > Kaynak atamasi uygun olan " + kacKaynakVar + " kaynak icinden rastgele secimle yapildi (SimpleMetascheduler). " + hangiKaynak + " ile " + resourceAgentListTrue.getResourceArray(hangiKaynak) + " kaynak olarak secildi.");

					} else {

						if (job.getJobRuntimeProperties().getJobProperties().getAdvancedJobInfos().getAgentChoiceMethod().getStringValue().equals((String) "UserInteractionPreference")) {

							if (job.getSelectedAgentId() == null && !userChooseResourceStateFlag) {

								LiveStateInfoUtils.insertNewLiveStateInfo(job.getJobRuntimeProperties().getJobProperties(), StateName.INT_PENDING, SubstateName.INT_READY, StatusName.INT_USER_CHOOSE_RESOURCE);
								job.sendStatusChangeInfo();
								job.setResourceAgentListTrue(resourceAgentListTrue);

								Logger.getLogger(Dss.class).info("     > Kaynak atamasi uygun olan " + kacKaynakVar + " kaynak icinden kullanici secsin (UserInteractionPreference).");
								userChooseResourceStateFlag = true;

							} else if (job.getSelectedAgentId() != null) {
								// is, kullanicinin ekrandan girdigi kaynaga ataniyor
								for (int agentIndex = 0; agentIndex < resourceAgentListTrue.sizeOfResourceArray(); agentIndex++) {
									if (job.getSelectedAgentId().equals(resourceAgentListTrue.getResourceArray(agentIndex).getAgentid() + "")) {
										hangiKaynak = agentIndex;

										Logger.getLogger(Dss.class).info("     > Kaynak atamasi uygun olan " + kacKaynakVar + " kaynak icinden kullanici secimi ile yapildi. " + hangiKaynak + " ile " + resourceAgentListTrue.getResourceArray(hangiKaynak) + " kaynak olarak secildi.");

										break;
									}
								}

							}

						} else {

							if (job.getJobRuntimeProperties().getJobProperties().getAdvancedJobInfos().getAgentChoiceMethod().getStringValue().equals((String) "UserMandatoryPreference")) {
								Logger.getLogger(Dss.class).info("     > Kaynak atamasi kullanicinin sectigi kaynak olacak (UserMandatoryPreference).");
								if (job.getJobRuntimeProperties().getJobProperties().getAdvancedJobInfos().getAgentChoiceMethod().getAgentId().equals("" + resourceAgentListTrue.getResourceArray(i).getAgentid())) {
									Logger.getLogger(Dss.class).info("     > Kaynak atamasi uygun olan kullanici secimi ile yapildi. " + job.getJobRuntimeProperties().getJobProperties().getAdvancedJobInfos().getAgentChoiceMethod().getAgentId());
									hangiKaynak = i;
								}
								// Logger.getLogger(Dss.class).info("     > Kaynak atamasi uygun olan kullanici secimi ile yapiliyor...");
							} else {
								if (job.getJobRuntimeProperties().getJobProperties().getAdvancedJobInfos().getAgentChoiceMethod().getStringValue().equals((String) "AdvancedMetascheduler")) {
									Logger.getLogger(Dss.class).info("     > Kaynak atamasi ileri teknikler kullanilarak yapilacak(AdvancedMetascheduler).");
								} else
									Logger.getLogger(Dss.class).info("     > Kaynak atamasi icin ek yontem gerekiyor.");
							}
						}

					}

					if (hangiKaynak >= 0) {

						Resource resource = resourceAgentListTrue.getResourceArray(hangiKaynak);
						SWAgent agent = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference().getSwAgentCache(resource.getAgentid() + "");

						if ((Boolean.parseBoolean(resource.getStringValue())) && agent.getInJmxAvailable()) {

							// Job in hangi agent da calisacagi belli oldu. Tanimdaki agentId ye atayalim.
							jobProperties.setAgentId(resource.getAgentid());
							transferPermission = true;
							/*
							 * Bu noktada ise kaynak ayrildi, artik gercek olarak
							 * var. O yuzden DailyScenarios a yeni bir is olarak
							 * kaydini yapalim.
							 */
							Logger.getLogger(Dss.class).info("     > " + jobProperties.getBaseJobInfos().getJsName() + " DB ye insert ediliyor !");
							job.getJobRuntimeProperties().getJobProperties().setLSIDateTime(DateUtils.getW3CDateTime());
							DBUtils.insertJob(jobProperties, ParsingUtils.getJobXPath(job.getJobRuntimeProperties().getTreePath()));
							// DBUtils.insertJobInTheBeginning(jobProperties,
							// ParsingUtils.getJobXFullPath(job.getJobRuntimeProperties().getTreePath(),jobProperties.getID(),""+resource.getAgentid(),job.getJobRuntimeProperties().getJobProperties().getLSIDateTime()));

							/* TRANSFERING state i ekle */
							LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_PENDING, SubstateName.INT_READY, StatusName.INT_TRANSFERING);
							job.sendStatusChangeInfo();
							break;
						}
					}
				}
			}
		}
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

	public static void getResourcesForJob(Job job) {

		JobProperties jobProperties = job.getJobRuntimeProperties().getJobProperties();

		ResourceAgentList resourceAgentList = DssDbUtils.swFindResourcesForAJob(jobProperties);

		// Logger.getLogger(Dss.class).info("     > ########################################################");
		int counter = resourceAgentList.sizeOfResourceArray();

		if (counter > 0) {
			job.setResourceAgentList(resourceAgentList);
			String allResources = "", TrueType = "FALSE";
			int kacKaynakVar = 0;
			while (counter > 0) {
				// Kaynaklardan uygun olmayanlari <FALSE> ayri tutalim.
				if (resourceAgentList.getResourceArray(counter - 1).getStringValue().equals(TrueType)) {
					Logger.getLogger(Dss.class).info("     > JOB " + jobProperties.getID() + " icin FALSE " + resourceAgentList.getResourceArray(counter - 1));
				} else {
					// allResources = allResources + resourceAgentList.getResourceArray(counter-1).getAgentid();
					if (kacKaynakVar > 0)
						allResources = allResources + " ve " + resourceAgentList.getResourceArray(counter - 1).getAgentid();
					else
						allResources = "" + resourceAgentList.getResourceArray(counter - 1).getAgentid();
					kacKaynakVar++;
				}
				counter--;
				Logger.getLogger(Dss.class).info("     > JOB " + jobProperties.getID() + " icin " + kacKaynakVar + " adet uygun kaynak var;" + allResources);
			}

			// Logger.getLogger(Dss.class).info("     > " + resourceAgentList.toString());
		} else {
			Logger.getLogger(Dss.class).info("     > JOB " + jobProperties.getID() + " icin !! KAYNAK ATANAMADI !!");
		}
		// Logger.getLogger(Dss.class).info("     > ########################################################");
	}

}

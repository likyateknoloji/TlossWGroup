package com.likya.tlossw.core.cpc.helper;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.JobBaseTypeDocument.JobBaseType;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;

public class Consolidator {

	private static Logger logger = Logger.getLogger(Consolidator.class);

	public static void compareAndConsolidateTwoTables(String planIdOld, HashMap<String, SpcInfoType> spcLookupTableNew, HashMap<String, SpcInfoType> spcLookupTableOld) {
		
		for (String spcIdNew : spcLookupTableNew.keySet()) {

			String spcIdOld = ConcurrencyAnalyzer.containsScenario(spcIdNew, planIdOld, spcLookupTableOld);

			// Dünün listesinde de var ise 
			if (spcIdOld != null) {
				
				SpcInfoType spcInfoTypeOld = spcLookupTableOld.get(spcIdOld);
				
				if(isScenaroCompletedWithSuccess(spcInfoTypeOld.getSpcReferance())/* Completed */) {
					// Eskisini sil, yenisi listede var...
				} else /* Running */ {
					if(spcInfoTypeOld.getSpcReferance().isConcurrent()) {
						// Yenisinin instance id sini bir arttır ve öylece listeye ekle, 
						// eskisini yeni listeye taşı, eski listeden sil.
						// Örnek : scenarioId = 3245:13
						// Yeni scenarioId = 3245:14
						spcLookupTableNew.put(spcIdOld, spcInfoTypeOld);
					} else {
						// Bitince kendini VT'den yenilesin değerini set et. sonra yeni listeye ekle
					}
				} 
				spcLookupTableOld.remove(spcIdOld);
			}
		}
		
		if(!spcLookupTableOld.isEmpty()) {
			// Neden acaba, kimsenin kalmaması lazım ?
			spcLookupTableOld.clear();
		}
		
	}

	public static void checkAndPerformStabilityConditionsOfJobsInScenario(SpcInfoType spcInfoTypeNew, SpcInfoType spcInfoTypeOld) {
 
		HashMap<String, Job> jobQueueNew = spcInfoTypeNew.getSpcReferance().getJobQueue();
		HashMap<String, Job> jobQueueOld = spcInfoTypeOld.getSpcReferance().getJobQueue();

		if (jobQueueNew != null && jobQueueOld != null) {

			Iterator<Job> jobsIteratorOld = jobQueueOld.values().iterator();

			while (jobsIteratorOld.hasNext()) {

				Job jobOld = jobsIteratorOld.next();

				JobProperties jobPropertiesOld = jobOld.getJobRuntimeProperties().getJobProperties();

				String jobIdOld = jobPropertiesOld.getID();
				
				String jobBaseType = jobPropertiesOld.getBaseJobInfos().getJobInfos().getJobBaseType().toString();
				
				if (jobQueueNew.containsKey(jobIdOld)) {
					if (JobBaseType.PERIODIC.equals(jobBaseType)) {
						if (LiveStateInfoUtils.equalStates(jobPropertiesOld, StateName.RUNNING)) {
							// iş bittikten sonra aşağıdaki adımları yapacaz
							if (!identical(jobQueueNew.get(jobIdOld), jobOld)) {
								// iş bitince yenisini devreye al, güncelleme yok !//güncelleme yapacak şekilde ayarla ama nasıl ????
							} 
							jobQueueNew.put(jobIdOld, jobQueueOld.get(jobIdOld));
						} else {
							// eskisini bırak yenisi ile devam et
						}
						
					} else {
						if (LiveStateInfoUtils.equalStates(jobPropertiesOld, StateName.RUNNING)) {
							// Eğer önceki iş ile aynı anda çalışma izni var ise,
							// eskisini taşıyıp, yanına yenisini eklemek gerekecek
							// Eğer aynı anda çalışma izni yok ise,
							// Eskisinin bitmesini bekleyip sonra çalışacak ama nasıl ????
							
							// 1. yol : İki iş arasına sanal bağımlılık tanımlama ... daha önce yapıldı
							// 2. yol : Eski işe işini bitirdikten sonra yeni sürümünü de devreye aldırma.... ama nasıl ?
							// yeni kuyruğa taşıyoruz
							jobQueueNew.put(jobIdOld, jobQueueOld.get(jobIdOld));
						} else {
							// eskisini bırak yenisi ile devam et
						}
					}
				
				} else {
					if (LiveStateInfoUtils.equalStates(jobPropertiesOld, StateName.RUNNING)) {
						if (JobBaseType.PERIODIC.equals(jobBaseType)) {
							// set old job to terminate after execution
							jobQueueNew.get(jobIdOld).setStopRepeatativity(true);
						}
						// yeni kuyruğa taşıyoruz
						jobQueueNew.put(jobIdOld, jobQueueOld.get(jobIdOld));
					} else {
						// Eğer çalışmıyorsa beklemede ise
						// yeni kuyruğa taşıma
					}
				}

			}

		}

		return;

	}

	public static boolean checkStabilityConditions01(SpcInfoType spcInfoTypeNew, SpcInfoType spcInfoTypeOld) {

		boolean isScenaroFinished = true;

		HashMap<String, Job> jobQueueNew = spcInfoTypeNew.getSpcReferance().getJobQueue();
		HashMap<String, Job> jobQueueOld = spcInfoTypeOld.getSpcReferance().getJobQueue();

		if (jobQueueNew != null && jobQueueOld != null) {

			Iterator<Job> jobsIteratorOld = jobQueueOld.values().iterator();

			while (jobsIteratorOld.hasNext()) {

				Job jobOld = jobsIteratorOld.next();

				JobProperties jobPropertiesOld = jobOld.getJobRuntimeProperties().getJobProperties();

				if (JobBaseType.PERIODIC.equals(jobPropertiesOld.getBaseJobInfos().getJobInfos().getJobBaseType())) {
					String jobIdOld = jobOld.getJobKey();
					if (jobQueueNew.containsKey(jobIdOld)) {
						if (!identical(jobQueueNew.get(jobIdOld), jobOld)) {
							// Aynı yap, yani eşelleştir
						}
						// yeni kuyruğa taşıyoruz
						jobQueueNew.put(jobIdOld, jobQueueOld.get(jobIdOld));
					} else {
						// Eğer çalışıyorsa
						// set old job to terminate self remove after execution
						// Eğer çalışmıyorsa beklemede ise
						// yeni kuyruğa taşıma
					}

					isScenaroFinished = false;
				} else {

					try {
						if (jobPropertiesOld.getStateInfos() != null) {
							if (!LiveStateInfoUtils.equalStates(jobPropertiesOld, StateName.FINISHED)) {
								isScenaroFinished = false;
								break;
							}
						} else {
							SpaceWideRegistry.getGlobalLogger().error("  > isJobQueueOver : jobProperties.getStateInfos() is null !");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}

		return isScenaroFinished;

	}

	// private static boolean checkStabilityConditions(SpcInfoType spcInfoType) {
	//
	// if(!isScenaroCompletedWithSuccess(spcInfoType.getSpcReferance())) {
	// return false;
	// }
	//
	// return true;
	// }

	public static boolean isScenaroCompletedWithSuccess(Spc spcReferance) {

		if (!spcReferance.getLiveStateInfo().getStateName().equals(StateName.FINISHED)) {
			logger.info("     > SPC Lookup Table da bir onceki calistirmadan kalan " + spcReferance.getSpcId() + " isimli senaryo bitirilmemis.");
			return false;
		}

		return true;
	}

	public static boolean isJobQueueOver(HashMap<String, Job> jobQueue) {

		if (jobQueue != null) {
			Iterator<Job> jobsIterator = jobQueue.values().iterator();
			while (jobsIterator.hasNext()) {
				Job scheduledJob = jobsIterator.next();

				JobProperties jobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();
				if (JobBaseType.PERIODIC.equals(jobProperties.getBaseJobInfos().getJobInfos().getJobBaseType())) {
					return false;
				}
				// SpaceWideRegistry.getSpaceWideLogger().info("   > JobQueue element jobsIterator: " + jobsIterator);
				// SpaceWideRegistry.getSpaceWideLogger().info("   > JobQueue element scheduledJob: " + scheduledJob.getJobRuntimeProperties());
				try {
					if (jobProperties.getStateInfos() != null) {
						if (!jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(StateName.FINISHED)) {
							return false;
						}
					} else {
						SpaceWideRegistry.getGlobalLogger().error("  > isJobQueueOver fonksiyonunda problem2 : " + scheduledJob.getJobRuntimeProperties().getJobProperties());
					}
				} catch (Exception e) {
					SpaceWideRegistry.getGlobalLogger().error("  > isJobQueueOver fonksiyonunda problem : " + scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos());
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	private static boolean identical(Job jobNew, Job jobOld) {
		return jobNew.getJobRuntimeProperties().getJobProperties().toString().equals(jobOld.getJobRuntimeProperties().getJobProperties().toString());
	}
}

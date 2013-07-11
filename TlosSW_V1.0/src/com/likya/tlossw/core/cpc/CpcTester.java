package com.likya.tlossw.core.cpc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobListDocument.JobList;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.core.spc.helpers.JobQueueOperations;
import com.likya.tlossw.exceptions.TlosException;
import com.likya.tlossw.model.engine.EngineeConstants;
import com.likya.tlossw.utils.CpcUtils;
import com.likya.tlossw.utils.PersistenceUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;

/**
 * @author serkan taş
 * 
 */
public class CpcTester extends CpcBase {

	public boolean isUserSelectedRecover = false;

	private Logger logger = Logger.getLogger(CpcTester.class);

	private HashMap<String, SpcInfoType> spcLookupTable = new HashMap<String, SpcInfoType>();

	private boolean isRegular = true;

	public CpcTester(SpaceWideRegistry spaceWideRegistry) {
		super(spaceWideRegistry);
	}

	public synchronized void addTestData(TlosProcessData tlosProcessData) throws TlosException {
		spcLookupTable.putAll(prepareTestTable(tlosProcessData));
	}

	public void run() {

		Thread.currentThread().setName(this.getClass().getName());

		while (isExecutionPermission()) {

			try {

				handleNormalExecution();

				// Her bir instance icin senaryolari aktive et !

				if (spcLookupTable == null || spcLookupTable.size() == 0) {
					logger.warn("   >>> UYARI : Senaryo isleme agaci SPC bos !!");
					break;
				}

				logger.info("");
				logger.info(" 10 - Butun senaryolar calismaya hazir, islem baslasin !");

				for (String spcId : spcLookupTable.keySet()) {

					logger.info("   > Senaryo " + spcId + " calistiriliyor !");

					SpcInfoType mySpcInfoType = spcLookupTable.get(spcId);
					Spc spc = mySpcInfoType.getSpcReferance();

					if (mySpcInfoType.isVirgin() && !spc.getExecuterThread().isAlive()) {

						mySpcInfoType.setVirgin(false); 
						
						spc.getLiveStateInfo().setStateName(StateName.RUNNING);
						spc.getLiveStateInfo().setSubstateName(SubstateName.STAGE_IN);

						spc.getExecuterThread().start();

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Cpc failed, terminating !");
				break;
			}

			logger.info("");

			try {

				if (TlosSpaceWide.isPersistent()) {
					if (!PersistenceUtils.persistSWRegistry()) {
						logger.error("Cpc persist failed, terminating !");
						break;
					}
				}
				// Cpc.dumpSpcLookupTables(getSpaceWideRegistry());
				synchronized (this.getExecuterThread()) {
					this.getExecuterThread().wait();
				}

				logger.info(" >> Cpc notified !");

			} catch (InterruptedException e) {
				logger.error("Cpc failed, terminating !");
				break;
			}
		}

		terminateAllJobs(CpcTester.FORCED);

		logger.info("Exited Cpc notified !");

	}

	private void handleNormalExecution() throws TlosException {

		ArrayList<Parameter> myPramList = prepareParameterList();

		arrangeParameters(myPramList);

		logger.info("   > Hayir, ilk eleman olacak !");

		return;
	}

	protected HashMap<String, SpcInfoType> prepareTestTable(TlosProcessData tlosProcessData) throws TlosException {

		HashMap<String, SpcInfoType> scpLookupTable = new HashMap<String, SpcInfoType>();

		HashMap<String, Scenario> tmpScenarioList = new HashMap<String, Scenario>();

		String userId = "" + tlosProcessData.getBaseScenarioInfos().getUserId();

		if (userId == null || userId.equals("")) {
			userId = "" + Calendar.getInstance().getTimeInMillis();
		}
		
		logger.info("   > InstanceID = " + userId + " olarak belirlenmistir.");
		String localRoot = getRootPath() + "." + userId;
		logger.info("   > is agacinin islenmekte olan dali " + localRoot + " olarak belirlenmistir.");

		JobList lonelyJobList = tlosProcessData.getJobList();

		if (lonelyJobList != null && lonelyJobList.getJobPropertiesArray().length > 0) {
			
			Scenario myScenario = CpcUtils.getScenario(tlosProcessData);

			tmpScenarioList.put(localRoot + "." + EngineeConstants.LONELY_JOBS, myScenario);

			logger.info("   > Serbest isler " + localRoot + "." + EngineeConstants.LONELY_JOBS + " olarak Senaryo listesine eklendiler.");
		}


		linearizeScenarios(localRoot, tlosProcessData.getScenarioArray(), tmpScenarioList);
		
		Iterator<String> keyIterator = tmpScenarioList.keySet().iterator();

		logger.info("");
		logger.info(" 8 - TlosProcessData icindeki Senaryolardaki islerin listesi cikarilacak.");

		while (keyIterator.hasNext()) {

			String scenarioId = keyIterator.next();

			logger.info("");
			logger.info("  > Senaryo ismi : " + scenarioId);

			JobList jobList = tmpScenarioList.get(scenarioId).getJobList();

			if (!validateJobList(jobList)) {
				continue; 
			}
			
			if(jobList.getJobPropertiesArray().length == 0) {
				logger.error(scenarioId + " isimli senaryo bilgileri yüklenemedi ya da iş listesi bos geldi !");
				logger.error(scenarioId + " isimli senaryo için spc başlatılmıyor !");
				continue;
			}

			Spc spc = new Spc(scenarioId, getSpaceWideRegistry(), transformJobList(jobList));

			SpcInfoType spcInfoType = CpcUtils.getSpcInfo(spc, userId, tlosProcessData.getInstanceId(), tmpScenarioList.get(scenarioId));

			scpLookupTable.put(scenarioId, spcInfoType);

			if (!getSpaceWideRegistry().getServerConfig().getServerParams().getIsPersistent().getValueBoolean() || !JobQueueOperations.recoverJobQueue(spcInfoType.getSpcReferance().getSpcId(), spc.getJobQueue(), spc.getJobQueueIndex())) {
				if (!spc.initScenarioInfo()) {
					logger.warn(scenarioId + " isimli senaryo bilgileri yüklenemedi ya da is listesi bos geldi !");
					Logger.getLogger(CpcBase.class).warn(" WARNING : " + scenarioId + " isimli senaryo bilgileri yüklenemedi ya da is listesi bos geldi !");

					System.exit(-1);
				}
			}

			logger.info("  > Senaryo yuklendi !");

		}

		logger.info("");
		logger.info(" > Senaryolarin ve islerin SPC (spcLookUpTable) senaryo agacina yuklenme islemi bitti !");

		return scpLookupTable;
	}
	
	public boolean isRegular() {
		return isRegular;
	}

	public void setRegular(boolean isRegular) {
		this.isRegular = isRegular;
	}
}

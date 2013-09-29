package com.likya.tlossw.core.cpc;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobListDocument.JobList;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.exceptions.TlosException;
import com.likya.tlossw.model.SpcLookupTable;
import com.likya.tlossw.model.path.ScenarioPathType;
import com.likya.tlossw.utils.CpcUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;

/**
 * @author serkan taş
 * 
 */
public class CpcTester extends CpcBase {

	public boolean isUserSelectedRecover = false;

	private Logger logger = Logger.getLogger(CpcTester.class);

	SpcLookupTable spcLookupTable = new SpcLookupTable();

	private boolean loop = true;

	public CpcTester(SpaceWideRegistry spaceWideRegistry) {
		super(spaceWideRegistry);
	}

	public synchronized void addTestData(TlosProcessData tlosProcessData) throws TlosException {
		spcLookupTable = prepareTestTable(tlosProcessData);
	}

	public void run() {

		Thread.currentThread().setName(this.getClass().getName());

		while (loop) {

			try {

				logger.info(" 1 - İşlem başlasın !");

				initParameters();

				HashMap<ScenarioPathType, SpcInfoType> table = spcLookupTable.getTable();
				
				if (table == null || table.size() == 0) {
					logger.warn("   >>> UYARI : Senaryo isleme agaci SPC bos !!");
				} else {

					logger.info("");
					logger.info(" 10 - Butun senaryolar calismaya hazir, islem baslasin !");

					for (ScenarioPathType spcId : table.keySet()) {

						logger.info("   > Senaryo " + spcId.getFullPath() + " calistiriliyor !");

						SpcInfoType mySpcInfoType = table.get(spcId);
						Spc spc = mySpcInfoType.getSpcReferance();

						if (spc == null) {
							// No spc defined for this scenario, it is NOT a BUG !
							continue;
						}

						if (mySpcInfoType.isVirgin() && !spc.getExecuterThread().isAlive()) {

							mySpcInfoType.setVirgin(false);

							spc.getLiveStateInfo().setStateName(StateName.RUNNING);
							spc.getLiveStateInfo().setSubstateName(SubstateName.STAGE_IN);

							logger.info("     > Senaryo " + spcId.getFullPath() + " aktive edildi !");
							
							spc.getExecuterThread().start();

						}
					}
				}

				try {
					synchronized (this.getExecuterThread()) {
						this.getExecuterThread().wait();
					}
					logger.info(" >> Cpc notified !");
				} catch (InterruptedException e) {
					logger.error("Cpc failed, terminating !");
					break;
				}

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Cpc failed, terminating !");
				break;
			}

		}

		logger.info("Exited CpcTester notified !");

	}

	protected SpcLookupTable prepareTestTable(TlosProcessData tlosProcessData) throws TlosException {

		SpcLookupTable spcLookupTable = new SpcLookupTable();

		HashMap<ScenarioPathType, SpcInfoType> table = spcLookupTable.getTable();
		
		// Using userId as instanceId for test routine
		String userId = getInstanceId(tlosProcessData, false);
		
		HashMap<ScenarioPathType, Scenario> tmpScenarioList = performLinearization(userId, tlosProcessData);

		Iterator<ScenarioPathType> keyIterator = tmpScenarioList.keySet().iterator();

		logger.info("");
		logger.info(" 8 - TlosProcessData icindeki Senaryolardaki islerin listesi cikarilacak.");

		while (keyIterator.hasNext()) {

			ScenarioPathType scenarioId = keyIterator.next();

			logger.info("");
			logger.info("  > Senaryo ismi : " + scenarioId);

			JobList jobList = tmpScenarioList.get(scenarioId).getJobList();

			if (!validateJobList(jobList)) {
				continue;
			}

			if (jobList.getJobPropertiesArray().length == 0 && tmpScenarioList.get(scenarioId).getScenarioArray().length == 0) {
				logger.error(scenarioId + " isimli senaryo bilgileri yüklenemedi ya da iş listesi bos geldi !");
				logger.error(scenarioId + " isimli senaryo için spc başlatılmıyor !");
				continue;
			}

			SpcInfoType spcInfoType = null;

			if (/*!scenarioId.equals(CpcUtils.getRootScenarioPath(userId)) &&*/ jobList.getJobPropertiesArray().length == 0) {
				spcInfoType = CpcUtils.getSpcInfo(userId, tlosProcessData.getInstanceId(), tmpScenarioList.get(scenarioId));
				spcInfoType.setSpcId(scenarioId);
			} else {
				Spc spc = new Spc(scenarioId, getSpaceWideRegistry(), transformJobList(jobList), false, true);
				
				spcInfoType = CpcUtils.getSpcInfo(spc, userId, userId, tmpScenarioList.get(scenarioId));
				spcInfoType.setSpcId(scenarioId);
				
				if (!spc.initScenarioInfo()) {
					logger.warn(scenarioId.getFullPath() + " isimli senaryo bilgileri yüklenemedi ya da iş listesi boş geldi !");
					continue;
				}
			}

			table.put(new ScenarioPathType(scenarioId), spcInfoType);

			logger.info("  > Senaryo yuklendi !");

		}

		logger.info("");
		logger.info(" > Senaryolarin ve islerin SPC (spcLookUpTable) senaryo agacina yuklenme islemi bitti !");

		return spcLookupTable;
	}

	public boolean isLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	public SpcLookupTable getSpcLookupTable(String userId) {
		
		SpcLookupTable tmpLookupTable = new SpcLookupTable();
		HashMap<ScenarioPathType, SpcInfoType> table = spcLookupTable.getTable();

		HashMap<ScenarioPathType, SpcInfoType> tmpMap = new HashMap<ScenarioPathType, SpcInfoType>();

		for (ScenarioPathType key : table.keySet()) {
			if (userId.equals(table.get(key).getUserId())) {
				tmpMap.put(key, table.get(key));
			}
		}
		
		tmpLookupTable.setTable(tmpMap);

		return tmpLookupTable;
	}

}

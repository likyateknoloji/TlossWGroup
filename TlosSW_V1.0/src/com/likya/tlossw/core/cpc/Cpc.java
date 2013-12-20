package com.likya.tlossw.core.cpc;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.cpc.helper.ConcurrencyAnalyzer;
import com.likya.tlossw.core.cpc.helper.Consolidator;
import com.likya.tlossw.core.cpc.model.AppState;
import com.likya.tlossw.core.cpc.model.RunInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.exceptions.TlosException;
import com.likya.tlossw.exceptions.TlosFatalException;
import com.likya.tlossw.model.SpcLookupTable;
import com.likya.tlossw.model.path.TlosSWPathType;
import com.likya.tlossw.utils.CpcUtils;
import com.likya.tlossw.utils.PersistenceUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;

/**
 * @author Serkan Taş
 * 
 */
public class Cpc extends CpcBase {

	public boolean isUserSelectedRecover = false;

	private Logger logger = SpaceWideRegistry.getGlobalLogger();

	// private boolean isRegular = true;

	public Cpc(SpaceWideRegistry spaceWideRegistry) {
		super(spaceWideRegistry);
		this.isUserSelectedRecover = spaceWideRegistry.isUserSelectedRecover();
	}

	public void run() {

		Thread.currentThread().setName("Cpc");

		while (isExecutionPermission()) {

			try {

				logger.info("");
				logger.info(" 2 - Recover işlemi gerekli mi?");

				if (isUserSelectedRecover) {
					logger.info("   > Evet, recover işlemi gerekli !");
					handleRecoverdExecution();
				} else {
					logger.info("   > Hayır, recover işlemi gerekli değil !");
					handleDailyExecution();
				}

				// Her bir run icin senaryolari aktive et !

				for (String runId : getSpaceWideRegistry().getRunLookupTable().keySet()) {

					RunInfoType runInfoType = getSpaceWideRegistry().getRunLookupTable().get(runId);

					HashMap<String, SpcInfoType> spcLookupTable = runInfoType.getSpcLookupTable().getTable();

					if (spcLookupTable == null) {
						logger.warn("   >>> UYARI : Senaryo isleme agaci SPC bos !!");
						logger.debug(" DEBUG : spcLookupTable is null, check getSpaceWideRegistry().getPlanLookupTable().get(" + runId + ")! ");
						break;
					}

					logger.info("");
					logger.info(" 10 - Butun senaryolar calismaya hazir, islem baslasin !");

					for (String spcId : spcLookupTable.keySet()) {

						SpcInfoType mySpcInfoType = spcLookupTable.get(spcId);
						Spc spc = mySpcInfoType.getSpcReferance();

						if (spc == null) {
							// No spc defined for this scenario, it is NOT a BUG !
							continue;
						}

						logger.info("   > Senaryo " + spcId + " calistiriliyor !");
						CpcUtils.startSpc(mySpcInfoType, logger);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Cpc failed, terminating !");
				break;
			}

			logger.info("");
			logger.info("   > CPC nin durumu : " + getSpaceWideRegistry().getCpcReference().getExecuterThread().getState());

			try {

				if (TlosSpaceWide.isPersistent()) {
					if (!PersistenceUtils.persistSWRegistry()) {
						logger.error("Cpc persist failed, terminating !");
						break;
					}
				}
				
				TlosSpaceWide.changeApplicationState(AppState.INT_RUNNING);
				// Cpc.dumpSpcLookupTables(getSpaceWideRegistry());
				logger.info("   > CPC nin durumu : getExecuterThread().wait() : " + getSpaceWideRegistry().getCpcReference().getExecuterThread().getState());
				synchronized (this.getExecuterThread()) {
					this.getExecuterThread().wait();
				}

				logger.info(" >> Cpc notified !");

			} catch (InterruptedException e) {
				logger.error("Cpc failed, terminating !");
				break;
			}
		}

		TlosSpaceWide.changeApplicationState(AppState.INT_STOPPING);
		
		terminateAllJobs(Cpc.FORCED);

		logger.info("Exited Cpc notified !");

	}

	private void freshDataLoad(TlosProcessData tlosProcessData) throws TlosException {

		logger.info("   > Hayır, ilk eleman olacak !");

		/**
		 * Senaryo ve isler spcLookUpTable a yani senaryo agacina
		 * yerlestirilecek. Bunun icin islerin validasyonu da
		 * gerceklestirilecek.
		 * 
		 **/

		SpcLookupTable spcLookUpTable = prepareSpcLookupTable(tlosProcessData, logger);
		/*
		 * scpLookUpTable olusturuldu. Olusan bu tablo PlanID ile
		 * iliskilendirilecek.
		 */

		logger.info("");
		logger.info(" 9 - SPC (spcLookUpTable) senaryo ağacı, PlanID = " + tlosProcessData.getRunId() + " ile ilişkilendirilecek.");

		RunInfoType runInfoType = new RunInfoType();
		runInfoType.setRunId(tlosProcessData.getRunId());
		runInfoType.setSpcLookupTable(spcLookUpTable);

		getSpaceWideRegistry().getRunLookupTable().put(runInfoType.getRunId(), runInfoType);

		logger.info("   > OK ilişkilendirildi.");

		if (spcLookUpTable == null) {
			logger.warn("   >>> SPC (spcLookUpTable) senaryo agaci BOS !!");
			logger.info("   >>> SPC (spcLookUpTable) senaryo agaci BOS !!");
		}
	}

	private void loadOnLiveSystem(TlosProcessData tlosProcessData) throws TlosException {

		SpcLookupTable spcLookupTableNew = prepareSpcLookupTable(tlosProcessData, logger);

		if(getSpaceWideRegistry().getRunLookupTable().size() < -1 || getSpaceWideRegistry().getRunLookupTable().size() > 1) {
			// Ne yapmalı ??
			return;
		}
		
		RunInfoType runInfoType = (RunInfoType) getSpaceWideRegistry().getRunLookupTable().values().toArray()[0];
		HashMap<String, SpcInfoType> spcLookupTableOld = runInfoType.getSpcLookupTable().getTable();
		
		runInfoType.setSpcLookupTable(null);
		
		String oldRunId = runInfoType.getRunId();
		String newRunId = tlosProcessData.getRunId();
		
		Consolidator.compareAndConsolidateTwoTables(oldRunId, spcLookupTableNew.getTable(), spcLookupTableOld);


		logger.info("");
		logger.info(" 9 - SPC (spcLookUpTable) senaryo agaci, RunID = " + newRunId + " ile iliskilendirilecek.");

		logger.info("   > RunID = " + newRunId + " olarak belirlendi.");

		runInfoType.setRunId(newRunId);
		runInfoType.setSpcLookupTable(spcLookupTableNew);

		getSpaceWideRegistry().getRunLookupTable().clear();
		getSpaceWideRegistry().getRunLookupTable().put(runInfoType.getRunId(), runInfoType);
		logger.info("   > OK iliskilendirildi.");
		
		TlosSpaceWide.changeApplicationState(AppState.INT_RUNNING);
	}
	
	public void loadOnLiveSystemOld(TlosProcessData tlosProcessData) throws TlosException {

		ConcurrencyAnalyzer.checkAndCleanSpcLookUpTables(getSpaceWideRegistry().getRunLookupTable(), logger);

		logger.info("   > Evet, " + getSpaceWideRegistry().getRunLookupTable().size() + ". eleman olacak !");

		SpcLookupTable spcLookupTableNew = prepareSpcLookupTable(tlosProcessData, logger);

		for (String runId : getSpaceWideRegistry().getRunLookupTable().keySet()) {
			RunInfoType runInfoType = getSpaceWideRegistry().getRunLookupTable().get(runId);
			HashMap<String, SpcInfoType> spcLookupTableOld = runInfoType.getSpcLookupTable().getTable();
			ConcurrencyAnalyzer.checkConcurrency(runId, spcLookupTableNew.getTable(), spcLookupTableOld);
		}

		logger.info("");
		logger.info(" 9 - SPC (spcLookUpTable) senaryo agaci, PlanID = " + tlosProcessData.getRunId() + " ile iliskilendirilecek.");

		RunInfoType runInfoType = new RunInfoType();
		logger.info("   > Instance ID = " + tlosProcessData.getRunId() + " olarak belirlendi.");

		runInfoType.setRunId(tlosProcessData.getRunId());
		runInfoType.setSpcLookupTable(spcLookupTableNew);

		getSpaceWideRegistry().getRunLookupTable().put(runInfoType.getRunId(), runInfoType);
		logger.info("   > OK iliskilendirildi.");
	}

	private void handleDailyExecution() throws TlosException {

		TlosProcessData tlosProcessData = getSpaceWideRegistry().getTlosProcessData();

		initParameters();

		if (getSpaceWideRegistry().getRunLookupTable().size() == 0) {
			freshDataLoad(tlosProcessData);
		} else {
			loadOnLiveSystem(tlosProcessData);
		}

		return;
	}

	private void handleRecoverdExecution() throws TlosFatalException {

		for (String runId : getSpaceWideRegistry().getRunLookupTable().keySet()) {

			RunInfoType runInfoType = getSpaceWideRegistry().getRunLookupTable().get(runId);

			HashMap<String, SpcInfoType> spcLookupTable = runInfoType.getSpcLookupTable().getTable();

			for (String spcId : spcLookupTable.keySet()) {
				TlosSWPathType tlosSWPathType = new TlosSWPathType(spcId);
				Spc spc = new Spc(tlosSWPathType.getRunId(), tlosSWPathType.getAbsolutePath(), getSpaceWideRegistry(), null, isUserSelectedRecover, false);
				LiveStateInfo myLiveStateInfo = LiveStateInfo.Factory.newInstance();

				myLiveStateInfo.setStateName(StateName.PENDING);
				myLiveStateInfo.setSubstateName(SubstateName.IDLED);
				myLiveStateInfo.setStatusName(StatusName.BYTIME);

				spc.setLiveStateInfo(myLiveStateInfo);

				Thread thread = new Thread(spc);

				spc.setExecuterThread(thread);

				SpcInfoType spcInfoType = spcLookupTable.get(spcId);

				spcInfoType.getScenario().getManagement().getConcurrencyManagement().setRunningId(runInfoType.getRunId());

				spc.setBaseScenarioInfos(spcInfoType.getScenario().getBaseScenarioInfos());
				spc.setDependencyList(spcInfoType.getScenario().getDependencyList());
				spc.setScenarioStatusList(spcInfoType.getScenario().getScenarioStatusList());
				spc.setAlarmPreference(spcInfoType.getScenario().getAlarmPreference());
				spc.setManagement(spcInfoType.getScenario().getManagement());
				spc.setAdvancedScenarioInfos(spcInfoType.getScenario().getAdvancedScenarioInfos());
				spc.setLocalParameters(spcInfoType.getScenario().getLocalParameters());

				spc.setJsName(spcInfoType.getJsName());
				spc.setConcurrent(spcInfoType.isConcurrent());
				spc.setComment(spcInfoType.getComment());
				spc.setUserId(spcInfoType.getUserId());

				spcInfoType.setSpcReferance(spc);
				spcLookupTable.put(spcId, spcInfoType);
			}
		}

		return;
	}

	// public boolean isRegular() {
	// return isRegular;
	// }
	//
	// public void setRegular(boolean isRegular) {
	// this.isRegular = isRegular;
	// }

}

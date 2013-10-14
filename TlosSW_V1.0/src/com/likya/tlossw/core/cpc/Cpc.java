package com.likya.tlossw.core.cpc;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.cpc.helper.ConcurrencyAnalyzer;
import com.likya.tlossw.core.cpc.helper.Consolidator;
import com.likya.tlossw.core.cpc.model.AppState;
import com.likya.tlossw.core.cpc.model.PlanInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.exceptions.TlosException;
import com.likya.tlossw.exceptions.TlosFatalException;
import com.likya.tlossw.model.SpcLookupTable;
import com.likya.tlossw.model.path.TlosSWPathType;
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

				// Her bir instance icin senaryolari aktive et !

				for (String planId : getSpaceWideRegistry().getPlanLookupTable().keySet()) {

					PlanInfoType planInfoType = getSpaceWideRegistry().getPlanLookupTable().get(planId);

					HashMap<String, SpcInfoType> spcLookupTable = planInfoType.getSpcLookupTable().getTable();

					if (spcLookupTable == null) {
						logger.warn("   >>> UYARI : Senaryo isleme agaci SPC bos !!");
						logger.debug(" DEBUG : spcLookupTable is null, check getSpaceWideRegistry().getPlanLookupTable().get(" + planId + ")! ");
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
						/**
						 * Bu thread daha once calistirildi mi? Degilse thread i
						 * baslatabiliriz !!
						 **/
						if (mySpcInfoType.isVirgin() && !spc.getExecuterThread().isAlive()) {

							mySpcInfoType.setVirgin(false); /* Artik baslattik */
							/** Statuleri set edelim **/
							spc.getLiveStateInfo().setStateName(StateName.RUNNING);
							spc.getLiveStateInfo().setSubstateName(SubstateName.STAGE_IN);

							logger.info("     > Senaryo " + spcId + " aktive edildi !");

							/** Senaryonun thread lerle calistirildigi yer !! **/
							spc.getExecuterThread().start();

							logger.info("     > OK");

						}
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

		SpcLookupTable spcLookUpTable = prepareSpcLookupTable(tlosProcessData);
		/*
		 * scpLookUpTable olusturuldu. Olusan bu tablo PlanID ile
		 * iliskilendirilecek.
		 */

		logger.info("");
		logger.info(" 9 - SPC (spcLookUpTable) senaryo ağacı, PlanID = " + tlosProcessData.getPlanId() + " ile ilişkilendirilecek.");

		PlanInfoType planInfoType = new PlanInfoType();
		planInfoType.setPlanId(tlosProcessData.getPlanId());
		planInfoType.setSpcLookupTable(spcLookUpTable);

		getSpaceWideRegistry().getPlanLookupTable().put(planInfoType.getPlanId(), planInfoType);

		logger.info("   > OK ilişkilendirildi.");

		if (spcLookUpTable == null) {
			logger.warn("   >>> SPC (spcLookUpTable) senaryo agaci BOS !!");
			logger.info("   >>> SPC (spcLookUpTable) senaryo agaci BOS !!");
		}
	}

	private void loadOnLiveSystem(TlosProcessData tlosProcessData) throws TlosException {

		SpcLookupTable spcLookupTableNew = prepareSpcLookupTable(tlosProcessData);

		if(getSpaceWideRegistry().getPlanLookupTable().size() < -1 || getSpaceWideRegistry().getPlanLookupTable().size() > 1) {
			// Ne yapmalı ??
			return;
		}
		
		PlanInfoType planInfoType = (PlanInfoType) getSpaceWideRegistry().getPlanLookupTable().values().toArray()[0];
		HashMap<String, SpcInfoType> spcLookupTableOld = planInfoType.getSpcLookupTable().getTable();
		
		planInfoType.setSpcLookupTable(null);
		
		String oldPlanId = planInfoType.getPlanId();
		String newPlanId = tlosProcessData.getPlanId();
		
		Consolidator.compareAndConsolidateTwoTables(oldPlanId, spcLookupTableNew.getTable(), spcLookupTableOld);


		logger.info("");
		logger.info(" 9 - SPC (spcLookUpTable) senaryo agaci, PlanID = " + newPlanId + " ile iliskilendirilecek.");

		logger.info("   > Plan ID = " + newPlanId + " olarak belirlendi.");

		planInfoType.setPlanId(newPlanId);
		planInfoType.setSpcLookupTable(spcLookupTableNew);

		getSpaceWideRegistry().getPlanLookupTable().clear();
		getSpaceWideRegistry().getPlanLookupTable().put(planInfoType.getPlanId(), planInfoType);
		logger.info("   > OK iliskilendirildi.");
		
		TlosSpaceWide.changeApplicationState(AppState.INT_RUNNING);
	}
	
	public void loadOnLiveSystemOld(TlosProcessData tlosProcessData) throws TlosException {

		ConcurrencyAnalyzer.checkAndCleanSpcLookUpTables(getSpaceWideRegistry().getPlanLookupTable(), logger);

		logger.info("   > Evet, " + getSpaceWideRegistry().getPlanLookupTable().size() + ". eleman olacak !");

		SpcLookupTable spcLookupTableNew = prepareSpcLookupTable(tlosProcessData);

		for (String planId : getSpaceWideRegistry().getPlanLookupTable().keySet()) {
			PlanInfoType instanceInfoType = getSpaceWideRegistry().getPlanLookupTable().get(planId);
			HashMap<String, SpcInfoType> spcLookupTableOld = instanceInfoType.getSpcLookupTable().getTable();
			ConcurrencyAnalyzer.checkConcurrency(planId, spcLookupTableNew.getTable(), spcLookupTableOld);
		}

		logger.info("");
		logger.info(" 9 - SPC (spcLookUpTable) senaryo agaci, PlanID = " + tlosProcessData.getPlanId() + " ile iliskilendirilecek.");

		PlanInfoType planInfoType = new PlanInfoType();
		logger.info("   > Instance ID = " + tlosProcessData.getPlanId() + " olarak belirlendi.");

		planInfoType.setPlanId(tlosProcessData.getPlanId());
		planInfoType.setSpcLookupTable(spcLookupTableNew);

		getSpaceWideRegistry().getPlanLookupTable().put(planInfoType.getPlanId(), planInfoType);
		logger.info("   > OK iliskilendirildi.");
	}

	private void handleDailyExecution() throws TlosException {

		TlosProcessData tlosProcessData = getSpaceWideRegistry().getTlosProcessData();

		initParameters();

		if (getSpaceWideRegistry().getPlanLookupTable().size() == 0) {
			freshDataLoad(tlosProcessData);
		} else {
			loadOnLiveSystem(tlosProcessData);
		}

		return;
	}

	private void handleRecoverdExecution() throws TlosFatalException {

		for (String planId : getSpaceWideRegistry().getPlanLookupTable().keySet()) {

			PlanInfoType planInfoType = getSpaceWideRegistry().getPlanLookupTable().get(planId);

			HashMap<String, SpcInfoType> spcLookupTable = planInfoType.getSpcLookupTable().getTable();

			for (String spcId : spcLookupTable.keySet()) {
				TlosSWPathType tlosSWPathType = new TlosSWPathType(spcId);
				Spc spc = new Spc(tlosSWPathType.getPlanId(), tlosSWPathType.getAbsolutePath(), getSpaceWideRegistry(), null, isUserSelectedRecover, false);
				LiveStateInfo myLiveStateInfo = LiveStateInfo.Factory.newInstance();

				myLiveStateInfo.setStateName(StateName.PENDING);
				myLiveStateInfo.setSubstateName(SubstateName.IDLED);

				spc.setLiveStateInfo(myLiveStateInfo);

				Thread thread = new Thread(spc);

				spc.setExecuterThread(thread);

				SpcInfoType spcInfoType = spcLookupTable.get(spcId);

				spcInfoType.getScenario().getConcurrencyManagement().setPlanId(planInfoType.getPlanId());

				spc.setBaseScenarioInfos(spcInfoType.getScenario().getBaseScenarioInfos());
				spc.setDependencyList(spcInfoType.getScenario().getDependencyList());
				spc.setScenarioStatusList(spcInfoType.getScenario().getScenarioStatusList());
				spc.setAlarmPreference(spcInfoType.getScenario().getAlarmPreference());
				spc.setTimeManagement(spcInfoType.getScenario().getTimeManagement());
				spc.setAdvancedScenarioInfos(spcInfoType.getScenario().getAdvancedScenarioInfos());
				spc.setConcurrencyManagement(spcInfoType.getScenario().getConcurrencyManagement());
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

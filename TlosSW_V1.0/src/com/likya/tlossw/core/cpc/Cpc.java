package com.likya.tlossw.core.cpc;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.JsTypeDocument.JsType;
import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlos.model.xmlbeans.state.JsDependencyRuleDocument.JsDependencyRule;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.cpc.model.InstanceInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.exceptions.TlosException;
import com.likya.tlossw.exceptions.TlosFatalException;
import com.likya.tlossw.model.SpcLookupTable;
import com.likya.tlossw.model.path.ScenarioPathType;
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

				for (String instanceId : getSpaceWideRegistry().getInstanceLookupTable().keySet()) {

					InstanceInfoType instanceInfoType = getSpaceWideRegistry().getInstanceLookupTable().get(instanceId);

					HashMap<String, SpcInfoType> spcLookupTable = instanceInfoType.getSpcLookupTable().getTable();

					if (spcLookupTable == null) {
						logger.warn("   >>> UYARI : Senaryo isleme agaci SPC bos !!");
						logger.debug(" DEBUG : spcLookupTable is null, check getSpaceWideRegistry().getInstanceLookupTable().get(" + instanceId + ")! ");
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
		 * scpLookUpTable olusturuldu. Olusan bu tablo InstanceID ile
		 * iliskilendirilecek.
		 */

		logger.info("");
		logger.info(" 9 - SPC (spcLookUpTable) senaryo ağacı, InstanceID = " + tlosProcessData.getInstanceId() + " ile ilişkilendirilecek.");

		InstanceInfoType instanceInfoType = new InstanceInfoType();
		instanceInfoType.setInstanceId(tlosProcessData.getInstanceId());
		instanceInfoType.setSpcLookupTable(spcLookUpTable);

		getSpaceWideRegistry().getInstanceLookupTable().put(instanceInfoType.getInstanceId(), instanceInfoType);

		logger.info("   > OK ilişkilendirildi.");

		if (spcLookUpTable == null) {
			logger.warn("   >>> SPC (spcLookUpTable) senaryo agaci BOS !!");
			logger.info("   >>> SPC (spcLookUpTable) senaryo agaci BOS !!");
		}
	}

	private void loadOnLiveSystem(TlosProcessData tlosProcessData) throws TlosException {

		checkAndCleanSpcLookUpTables();

		logger.info("   > Evet, " + getSpaceWideRegistry().getInstanceLookupTable().size() + ". eleman olacak !");

		SpcLookupTable spcLookupTableNew = prepareSpcLookupTable(tlosProcessData);

		for (String instanceId : getSpaceWideRegistry().getInstanceLookupTable().keySet()) {
			InstanceInfoType instanceInfoType = getSpaceWideRegistry().getInstanceLookupTable().get(instanceId);
			HashMap<String, SpcInfoType> spcLookupTable = instanceInfoType.getSpcLookupTable().getTable();
			checkConcurrency(spcLookupTableNew.getTable(), spcLookupTable);
		}

		logger.info("");
		logger.info(" 9 - SPC (spcLookUpTable) senaryo agaci, InstanceID = " + tlosProcessData.getInstanceId() + " ile iliskilendirilecek.");

		InstanceInfoType instanceInfoType = new InstanceInfoType();
		logger.info("   > Instance ID = " + tlosProcessData.getInstanceId() + " olarak belirlendi.");

		instanceInfoType.setInstanceId(tlosProcessData.getInstanceId());
		instanceInfoType.setSpcLookupTable(spcLookupTableNew);

		getSpaceWideRegistry().getInstanceLookupTable().put(instanceInfoType.getInstanceId(), instanceInfoType);
		logger.info("   > OK iliskilendirildi.");
	}

	private void handleDailyExecution() throws TlosException {

		TlosProcessData tlosProcessData = getSpaceWideRegistry().getTlosProcessData();

		initParameters();

		if (getSpaceWideRegistry().getInstanceLookupTable().size() == 0) {
			freshDataLoad(tlosProcessData);
		} else {
			loadOnLiveSystem(tlosProcessData);
		}

		return;
	}

	private void handleRecoverdExecution() throws TlosFatalException {

		for (String instanceId : getSpaceWideRegistry().getInstanceLookupTable().keySet()) {

			InstanceInfoType instanceInfoType = getSpaceWideRegistry().getInstanceLookupTable().get(instanceId);

			HashMap<String, SpcInfoType> spcLookupTable = instanceInfoType.getSpcLookupTable().getTable();

			for (String spcId : spcLookupTable.keySet()) {

				Spc spc = new Spc(new ScenarioPathType(spcId), getSpaceWideRegistry(), null, isUserSelectedRecover, false);
				LiveStateInfo myLiveStateInfo = LiveStateInfo.Factory.newInstance();

				myLiveStateInfo.setStateName(StateName.PENDING);
				myLiveStateInfo.setSubstateName(SubstateName.IDLED);

				spc.setLiveStateInfo(myLiveStateInfo);

				Thread thread = new Thread(spc);

				spc.setExecuterThread(thread);

				SpcInfoType spcInfoType = spcLookupTable.get(spcId);

				spcInfoType.getScenario().getConcurrencyManagement().setInstanceId(instanceInfoType.getInstanceId());

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
				spc.setInstanceId(instanceId);
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

	private void checkAndCleanSpcLookUpTables() {

		boolean checkValue = true;

		for (String instanceId : getSpaceWideRegistry().getInstanceLookupTable().keySet()) {

			InstanceInfoType instanceInfoType = getSpaceWideRegistry().getInstanceLookupTable().get(instanceId);
			HashMap<String, SpcInfoType> spcLookupTable = instanceInfoType.getSpcLookupTable().getTable();

			for (String spcId : spcLookupTable.keySet()) {
				Spc spc = spcLookupTable.get(spcId).getSpcReferance();

				if (!spc.getLiveStateInfo().getStateName().equals(StateName.FINISHED)) {
					checkValue = false;
					logger.info("     > SPC Lookup Table da bir onceki calistirmadan kalan " + spcId + " isimli isler bitirilmemis.");
					break;
				}
			}

			if (checkValue) {
				logger.info("     > SPC Lookup Table da bir onceki calistirmadan kalan isler tamamen bitirilmis, tablodan temizleniyor ...");
				spcLookupTable.clear();
				getSpaceWideRegistry().getInstanceLookupTable().remove(instanceId);
				checkValue = true;
				logger.info("     > Temizlendi.");
			} else {
				logger.info("     > SPC Lookup Table da bir onceki calistirmadan kalan bazi isler bitirilmemis.");
			}
		}

		return;
	}

	private void checkConcurrency(HashMap<String, SpcInfoType> spcLookupTableNew, HashMap<String, SpcInfoType> spcLookupTableMaster) throws TlosException {

		for (String spcId : spcLookupTableNew.keySet()) {

			String keyStr = containsScenario(spcId, spcLookupTableMaster);

			if (keyStr != null) {

				SpcInfoType spcInfoTypeMaster = spcLookupTableMaster.get(keyStr);
				SpcInfoType spcInfoTypeNew = spcLookupTableNew.get(spcId);

				if (!spcInfoTypeNew.getSpcReferance().getConcurrencyManagement().getConcurrent()) {
					// if (!spcInfoTypeNew.getSpcReferance().isConcurrent()) {
					if ((spcInfoTypeNew.getSpcReferance().getDependencyList() != null) && spcInfoTypeNew.getSpcReferance().getDependencyList().getItemArray().length > 0) {
						// ** Biraz parsing laz�m :( *//*
						Item mydependencyItem = Item.Factory.newInstance();
						mydependencyItem.setDependencyID("VD1");
						mydependencyItem.setComment("Bu sanal bir bagimlilik tanimidir. Bagli oldugu senaryo  ise budur : " + spcInfoTypeMaster.getSpcReferance().getSpcId());

						JsDependencyRule myDependencyRule = JsDependencyRule.Factory.newInstance();
						myDependencyRule.setStateName(StateName.FINISHED);
						mydependencyItem.setJsDependencyRule(myDependencyRule);

						mydependencyItem.setJsName(spcInfoTypeMaster.getSpcReferance().getBaseScenarioInfos().getJsName());
						mydependencyItem.setJsPath(spcInfoTypeMaster.getSpcReferance().getSpcId().getFullPath());
						mydependencyItem.setJsType(JsType.SCENARIO);

						DependencyList myDependencyList = DependencyList.Factory.newInstance();
						myDependencyList.addNewItem();
						myDependencyList.setItemArray(myDependencyList.sizeOfItemArray() - 1, mydependencyItem);

						String dependecyExpression = spcInfoTypeNew.getSpcReferance().getDependencyList().getDependencyExpression();
						dependecyExpression = mydependencyItem.getDependencyID() + " and ( " + dependecyExpression + " )";

						myDependencyList.setDependencyExpression(dependecyExpression);
						spcInfoTypeNew.getSpcReferance().setDependencyList(myDependencyList);

					} else {
						Item mydependencyItem = Item.Factory.newInstance();
						mydependencyItem.setDependencyID("VD1");
						mydependencyItem.setComment("Bu sanal bir bagimlilik tanimidir. Bagli oldugu senaryo  ise budur : " + spcInfoTypeMaster.getSpcReferance().getSpcId());

						JsDependencyRule myDependencyRule = JsDependencyRule.Factory.newInstance();
						myDependencyRule.setStateName(StateName.FINISHED);
						mydependencyItem.setJsDependencyRule(myDependencyRule);

						mydependencyItem.setJsName(spcInfoTypeMaster.getSpcReferance().getBaseScenarioInfos().getJsName());
						mydependencyItem.setJsPath(spcInfoTypeMaster.getSpcReferance().getSpcId().getFullPath());
						mydependencyItem.setJsType(JsType.SCENARIO);

						String dependecyExpression = mydependencyItem.getDependencyID();

						DependencyList myDependencyList = DependencyList.Factory.newInstance();
						myDependencyList.addNewItem();
						myDependencyList.setItemArray(myDependencyList.sizeOfItemArray() - 1, mydependencyItem);

						myDependencyList.setDependencyExpression(dependecyExpression);

						spcInfoTypeNew.getSpcReferance().setDependencyList(myDependencyList);
					}
				}
			}
		}

	}

	private String containsScenario(String scenarioId, HashMap<String, SpcInfoType> spcLookupTableMaster) {

		Iterator<String> keyIterator = spcLookupTableMaster.keySet().iterator();

		ScenarioPathType tmpPath = new ScenarioPathType(scenarioId);
		
		while (keyIterator.hasNext()) {
			String masterScenarioId = keyIterator.next();
			ScenarioPathType masterPath = new ScenarioPathType(masterScenarioId);
			if (tmpPath.getAbsolutePath().equals(masterPath.getAbsolutePath())) {
				return masterScenarioId;
			}
		}

		return null;
	}
}

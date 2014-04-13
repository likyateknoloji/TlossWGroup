package com.likya.tlossw.core.cpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.data.JobListDocument.JobList;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.agents.AgentManager;
import com.likya.tlossw.core.cpc.model.RunInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.core.spc.helpers.JobQueueOperations;
import com.likya.tlossw.db.utils.DBUtils;
import com.likya.tlossw.exceptions.GlobalParameterLoadException;
import com.likya.tlossw.exceptions.TlosException;
import com.likya.tlossw.model.SpcLookupTable;
import com.likya.tlossw.model.engine.EngineeConstants;
import com.likya.tlossw.model.path.JSPathId;
import com.likya.tlossw.model.path.TlosSWPathType;
import com.likya.tlossw.utils.CpcUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;

public abstract class CpcBase implements Runnable {

	public static boolean FORCED = true;
	public static boolean NORMAL = false;

	private boolean executionPermission = true;

	private SpaceWideRegistry spaceWideRegistry;
	private Logger myLogger;

	transient private Thread executerThread;

	public CpcBase(SpaceWideRegistry spaceWideRegistry) {
		this.spaceWideRegistry = spaceWideRegistry;
		myLogger = SpaceWideRegistry.getGlobalLogger();
	}

	protected void terminateAllJobs(boolean isForced) {

		for (String runId : getSpaceWideRegistry().getRunLookupTable().keySet()) {
			RunInfoType runInfoType = getSpaceWideRegistry().getRunLookupTable().get(runId);

			HashMap<String, SpcInfoType> spcMap = runInfoType.getSpcLookupTable().getTable();

			Iterator<String> keyIterator = spcMap.keySet().iterator();

			while (keyIterator.hasNext()) {

				String key = keyIterator.next();
				Spc spcreferance = spcMap.get(key).getSpcReferance();

				if (spcreferance == null) {
					// No spc defined for this scenario, it is NOT a BUG !
					continue;
				}

				spcreferance.setExecutionPermission(false, isForced);

				while ((spcreferance.getExecuterThread() != null) && spcreferance.getExecuterThread().isAlive()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				spcreferance.setExecuterThread(null);

			}
		}

	}

	protected void linearizeScenarios(TlosSWPathType path, Scenario[] scenarios, HashMap<String, Scenario> scenarioList) {

		ArrayIterator scenaryoListIterator = new ArrayIterator(scenarios);

		while (scenaryoListIterator.hasNext()) {

			Scenario scenario = (Scenario) (scenaryoListIterator.next());

			TlosSWPathType scenarioId = new TlosSWPathType(path);
			scenarioId.add(scenario.getID().toString());

			myLogger.info("   > " + scenarioId.getFullPath() + " senaryosu yani scenario ID = " + scenario.getID().toString() + " işleniyor.");

			scenarioList.put(scenarioId.getFullPath(), scenario);

			if (scenario.getScenarioArray().length != 0) {
				linearizeScenarios(scenarioId, scenario.getScenarioArray(), scenarioList);
			}

		}
	}

	public static void dumpSpcLookupTable(String runId, SpcLookupTable spcLookupTable) {

		HashMap<String, SpcInfoType> table = spcLookupTable.getTable();

		System.out.println("**************************Dumping SpcLookupTable ***************************************");
		System.out.println("sizo of spcLookupTable for runId : " + runId + " is " + table.size());

		for (String spcKey : table.keySet()) {
			System.out.println("Spc ID : " + table.get(spcKey).getSpcReferance().getSpcAbsolutePath());
		}

		System.out.println("***************************************************************************************");

	}

	public static void dumpSpcLookupTables(SpaceWideRegistry spaceWideRegistry) {

		System.out.println("**************************Dumping SpcLookupTables ***************************************");
		Logger.getLogger(CpcBase.class).info("  >>> **************************Dumping SpcLookupTables ***************************************");

		for (String runId : spaceWideRegistry.getRunLookupTable().keySet()) {

			RunInfoType runInfoType = spaceWideRegistry.getRunLookupTable().get(runId);
			HashMap<String, SpcInfoType> spcLookupTable = runInfoType.getSpcLookupTable().getTable();

			if (spcLookupTable == null) {
				System.out.println("Current run have no scenarios ! runId : " + runId);
				Logger.getLogger(CpcBase.class).warn("  >>> WARNING : Current run have no scenarios ! InstanceId : " + runId);
				return;
			}
			System.out.println("size of spcLookupTable for runId : " + runId + " is " + spcLookupTable.size());
			Logger.getLogger(CpcBase.class).debug("  >>> size of spcLookupTable for runId : " + runId + " is " + spcLookupTable.size());

			for (String spcKey : spcLookupTable.keySet()) {
				System.out.println("Spc ID : " + spcLookupTable.get(spcKey).getSpcReferance().getSpcAbsolutePath());
				Logger.getLogger(CpcBase.class).debug("  >>> Spc ID : " + spcLookupTable.get(spcKey).getSpcReferance().getSpcAbsolutePath());
			}
		}

		System.out.println("***************************************************************************************");
		Logger.getLogger(CpcBase.class).info("***************************************************************************************");

	}

	protected HashMap<String, Scenario> performLinearization(String runId, TlosProcessData tlosProcessData) {

		HashMap<String, Scenario> tmpScenarioList = new HashMap<String, Scenario>();

		TlosSWPathType scenarioPathType = new TlosSWPathType();

		scenarioPathType.setRunId(runId);
		scenarioPathType.setId(new JSPathId(EngineeConstants.LONELY_JOBS));

		myLogger.info("   > iş ağacının işlenmekte olan dalı " + scenarioPathType.getFullPath() + " olarak belirlenmiştir.");

		Scenario myScenario = CpcUtils.getScenario(tlosProcessData, runId);
		myScenario.setID(EngineeConstants.LONELY_JOBS);

		// *** root sonrasina runid eklendi. *//*

		tmpScenarioList.put(scenarioPathType.getFullPath(), myScenario);

		myLogger.info("");
		myLogger.info(" 7 - Senaryolar, lineerleştirilme işlemine tabi tutulacak.");

		linearizeScenarios(scenarioPathType, tlosProcessData.getScenarioArray(), tmpScenarioList);

		myLogger.info("   > Lineerleştirilme işlemi OK.");

		return tmpScenarioList;

	}

	protected SpcLookupTable prepareSpcLookupTable(TlosProcessData tlosProcessData, Logger myLogger) throws TlosException {

		SpcLookupTable spcLookupTable = new SpcLookupTable();

		HashMap<String, SpcInfoType> table = spcLookupTable.getTable();

		String runId = CpcUtils.getRunId(tlosProcessData, false, myLogger);

		HashMap<String, Scenario> tmpScenarioList = performLinearization(runId, tlosProcessData);

		Iterator<String> keyIterator = tmpScenarioList.keySet().iterator();

		myLogger.info("");
		myLogger.info(" 8 - TlosProcessData içindeki senaryolardaki işlerin listesi çıkarılacak.");

		while (keyIterator.hasNext()) {

			String scenarioFullPath = keyIterator.next();
			
			Scenario myScenario = tmpScenarioList.get(scenarioFullPath);

			SpcInfoType spcInfoType = CpcUtils.prepareScenario(runId, new TlosSWPathType(scenarioFullPath), myScenario, myLogger);
			if(spcInfoType == null) {
				continue;
			}

			table.put(scenarioFullPath, spcInfoType);

			myLogger.info("  > Senaryo yuklendi !");

		}

		myLogger.info("");
		myLogger.info(" > Senaryolarin ve islerin SPC (spcLookUpTable) senaryo agacina yuklenme islemi bitti !");

		return spcLookupTable;
	}

	protected SpcLookupTable prepareSpcLookupTableOrj(TlosProcessData tlosProcessData, Logger myLogger) throws TlosException {

		SpcLookupTable spcLookupTable = new SpcLookupTable();

		HashMap<String, SpcInfoType> table = spcLookupTable.getTable();

		String runId = CpcUtils.getRunId(tlosProcessData, false, myLogger);

		HashMap<String, Scenario> tmpScenarioList = performLinearization(runId, tlosProcessData);

		Iterator<String> keyIterator = tmpScenarioList.keySet().iterator();

		myLogger.info("");
		myLogger.info(" 8 - TlosProcessData içindeki senaryolardaki işlerin listesi çıkarılacak.");

		while (keyIterator.hasNext()) {

			String scenarioId = keyIterator.next();

			myLogger.info("");
			myLogger.info("  > Senaryo ismi : " + scenarioId);

			JobList jobList = tmpScenarioList.get(scenarioId).getJobList();

			if (!CpcUtils.validateJobList(jobList, myLogger)) {
				// TODO WAITING e nasil alacagiz?
				myLogger.info("     > is listesi validasyonunda problem oldugundan WAITING e alinarak problemin giderilmesi beklenmektedir.");
				myLogger.error("Cpc Scenario jobs validation failed, process state changed to WAITING !");

				continue; // 08.07.2013 Serkan
				// throw new TlosException("Cpc Job List validation failed, process state changed to WAITING !");
			}

			if (jobList.getJobPropertiesArray().length == 0 && tmpScenarioList.get(scenarioId).getScenarioArray().length == 0) {
				myLogger.error(scenarioId + " isimli senaryo bilgileri yüklenemedi ya da iş listesi bos geldi !");
				myLogger.error(scenarioId + " isimli senaryo için spc başlatılmıyor !");
				continue;
			}

			SpcInfoType spcInfoType = null;
			// TODO Henüz ayarlanmadı !
			String userId = null;

			if (jobList.getJobPropertiesArray().length == 0) {
				spcInfoType = CpcUtils.getSpcInfo(userId, tlosProcessData.getRunId(), tmpScenarioList.get(scenarioId));
				spcInfoType.setSpcId(new TlosSWPathType(scenarioId));
			} else {
				TlosSWPathType tlosSWPathType = new TlosSWPathType(scenarioId);
				Spc spc = new Spc(tlosSWPathType.getRunId(), tlosSWPathType.getAbsolutePath(), getSpaceWideRegistry(), CpcUtils.transformJobList(jobList, myLogger));

				spcInfoType = CpcUtils.getSpcInfo(spc, userId, tlosProcessData.getRunId(), tmpScenarioList.get(scenarioId));
				spcInfoType.setSpcId(new TlosSWPathType(scenarioId));

				if (!getSpaceWideRegistry().getServerConfig().getServerParams().getIsPersistent().getUse() || !JobQueueOperations.recoverJobQueue(spcInfoType.getSpcReferance().getSpcAbsolutePath(), spc.getJobQueue(), spc.getJobQueueIndex())) {
					if (!spc.initScenarioInfo()) {
						myLogger.warn(scenarioId + " isimli senaryo bilgileri yüklenemedi ya da iş listesi boş geldi !");
						Logger.getLogger(CpcBase.class).warn(" WARNING : " + scenarioId + " isimli senaryo bilgileri yüklenemedi ya da iş listesi boş geldi !");

						System.exit(-1);
					}
				}
			}

			table.put(scenarioId, spcInfoType);

			myLogger.info("  > Senaryo yuklendi !");

		}

		myLogger.info("");
		myLogger.info(" > Senaryolarin ve islerin SPC (spcLookUpTable) senaryo agacina yuklenme islemi bitti !");

		return spcLookupTable;
	}

	protected ArrayList<Parameter> prepareParameterList() throws GlobalParameterLoadException {

		myLogger.info(" 3,5 - Global Parametreler Yukleniyor..");

		ArrayList<Parameter> parameterList = DBUtils.getTlosParameters();

		if (parameterList != null) {

			for (int i = 0; i < parameterList.size(); i++) {
				String paramName = parameterList.get(i).getName();
				// String paramValueString = parameterList.get(i).getValueString();
				String paramPreValueString = parameterList.get(i).getPreValue().getStringValue();
				short paramPreValueType = parameterList.get(i).getPreValue().getType();
				String paramDesc = parameterList.get(i).getDesc();

				System.out.println(paramName + paramPreValueString + paramPreValueType + paramDesc);
			}

			myLogger.info("   > Yuklendi !");

		} else {
			myLogger.info("   > YukleneMEdi  parameterList = null ! ");
			throw new GlobalParameterLoadException("YukleneMEdi  parameterList = null ! ");
		}

		return parameterList;
	}

	protected void arrangeParameters(ArrayList<Parameter> myPramList) {

		AgentManager agentManagerRef = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference();

		HashMap<Integer, ArrayList<Parameter>> allParameter = new HashMap<Integer, ArrayList<Parameter>>();

		HashMap<String, SWAgent> SwAgentsCache = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference().getSwAgentsCache();

		for (String agentId : SwAgentsCache.keySet()) {

			// LOCAL
			// SWAgent swAgent = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference().getSwAgentCache(agentId);
			ArrayList<Parameter> parameterListLocal = new ArrayList<Parameter>();

			// if (!agentManagerRef.isServer(Integer.parseInt(agentId))) {
			boolean varmi;
			for (Parameter parameterGlobalElement : myPramList) {
				varmi = false;
				if (agentManagerRef.getSwAgentCache(agentId + "").getLocals() != null)
					for (Parameter parameterLocalElement : agentManagerRef.getSwAgentCache(agentId + "").getLocals().getParameterArray()) {
						if (parameterLocalElement.getName().toLowerCase().equals(parameterGlobalElement.getName().toLowerCase())) {
							// Globaldekinin aynisi var mi?
							varmi = true;
							parameterListLocal.add(parameterLocalElement);
							break;
						}
					}
				if (!varmi)
					parameterListLocal.add(parameterGlobalElement);
			}
			if (agentManagerRef.getSwAgentCache(agentId + "").getLocals() != null)
				for (Parameter parameterLocalElement : agentManagerRef.getSwAgentCache(agentId + "").getLocals().getParameterArray()) {
					varmi = false;
					for (Parameter parameterGlobalElement : myPramList) {
						if (parameterLocalElement.getName().toLowerCase().equals(parameterGlobalElement.getName().toLowerCase())) {
							// Globaldekinin aynisi var mi?
							varmi = true;
							break;
						}
					}
					if (!varmi)
						parameterListLocal.add(parameterLocalElement);
				}
			allParameter.put(Integer.parseInt(agentId), parameterListLocal);
			// }
		}
		TlosSpaceWide.getSpaceWideRegistry().setAllParameters(allParameter);
	}

	protected void initParameters() throws GlobalParameterLoadException {

		ArrayList<Parameter> myPramList = prepareParameterList();

		if (myPramList.size() > 0) {
			getSpaceWideRegistry().setParameters(myPramList);
			arrangeParameters(getSpaceWideRegistry().getParameters());
		}

	}

	public Thread getExecuterThread() {
		return executerThread;
	}

	public void setExecuterThread(Thread executerThread) {
		this.executerThread = executerThread;
	}

	public boolean isExecutionPermission() {
		return executionPermission;
	}

	public void setExecutionPermission(boolean executionPermission) {
		this.executionPermission = executionPermission;
	}

	public SpaceWideRegistry getSpaceWideRegistry() {
		return spaceWideRegistry;
	}
}

package com.likya.tlossw.core.cpc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.data.JobListDocument.JobList;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.agents.AgentManager;
import com.likya.tlossw.core.cpc.model.InstanceInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.core.spc.helpers.JobQueueOperations;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.db.utils.DBUtils;
import com.likya.tlossw.exceptions.GlobalParameterLoadException;
import com.likya.tlossw.exceptions.TlosException;
import com.likya.tlossw.model.SpcLookupTable;
import com.likya.tlossw.model.engine.EngineeConstants;
import com.likya.tlossw.model.path.ScenarioPathType;
import com.likya.tlossw.utils.CpcUtils;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.validation.XMLValidations;

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

		for (String instanceId : getSpaceWideRegistry().getInstanceLookupTable().keySet()) {
			InstanceInfoType instanceInfoType = getSpaceWideRegistry().getInstanceLookupTable().get(instanceId);

			HashMap<String, SpcInfoType> spcMap = instanceInfoType.getSpcLookupTable().getTable();

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

	protected void linearizeScenarios(ScenarioPathType path, Scenario[] scenarios, HashMap<String, Scenario> scenarioList) {

		ArrayIterator scenaryoListIterator = new ArrayIterator(scenarios);

		while (scenaryoListIterator.hasNext()) {

			Scenario scenario = (Scenario) (scenaryoListIterator.next());

			ScenarioPathType scenarioId = new ScenarioPathType(path);
			scenarioId.add(scenario.getID().toString());

			myLogger.info("   > " + scenarioId.getFullPath() + " senaryosu yani scenario ID = " + scenario.getID().toString() + " işleniyor.");

			scenarioList.put(scenarioId.getFullPath(), scenario);

			if (scenario.getScenarioArray().length != 0) {
				linearizeScenarios(scenarioId, scenario.getScenarioArray(), scenarioList);
			}

		}
	}

	protected boolean validateJobList(JobList jobList) {

		XMLValidations.validateWithCode(jobList, myLogger);

		return true;
	}

	protected ArrayList<JobRuntimeProperties> transformJobList(JobList jobList) {

		myLogger.debug("start:transformJobList");

		ArrayList<JobRuntimeProperties> transformTable = new ArrayList<JobRuntimeProperties>();

		ArrayIterator jobListIterator = new ArrayIterator(jobList.getJobPropertiesArray());

		while (jobListIterator.hasNext()) {

			JobProperties jobProperties = (JobProperties) (jobListIterator.next());
			JobRuntimeProperties jobRuntimeProperties = new JobRuntimeProperties();

			/* IDLED state i ekle */
			LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_PENDING, SubstateName.INT_IDLED);
			jobRuntimeProperties.setJobProperties(jobProperties);
			// TODO infoBusInfo Manager i bilgilendir.

			transformTable.add(jobRuntimeProperties);
		}

		myLogger.debug("end:transformJobList");

		return transformTable;
	}

	public static void dumpSpcLookupTable(String instanceId, SpcLookupTable spcLookupTable) {

		HashMap<String, SpcInfoType> table = spcLookupTable.getTable();

		System.out.println("**************************Dumping SpcLookupTable ***************************************");
		System.out.println("sizo of spcLookupTable for instanceId : " + instanceId + " is " + table.size());

		
		for (String spcKey : table.keySet()) {
			System.out.println("Spc ID : " + table.get(spcKey).getSpcReferance().getSpcId());
		}

		System.out.println("***************************************************************************************");

	}

	public static void dumpSpcLookupTables(SpaceWideRegistry spaceWideRegistry) {

		System.out.println("**************************Dumping SpcLookupTables ***************************************");
		Logger.getLogger(CpcBase.class).info("  >>> **************************Dumping SpcLookupTables ***************************************");

		for (String instanceId : spaceWideRegistry.getInstanceLookupTable().keySet()) {

			InstanceInfoType instanceInfoType = spaceWideRegistry.getInstanceLookupTable().get(instanceId);
			HashMap<String, SpcInfoType> spcLookupTable = instanceInfoType.getSpcLookupTable().getTable();

			if (spcLookupTable == null) {
				System.out.println("Current instance have no scenarios ! InstanceId : " + instanceId);
				Logger.getLogger(CpcBase.class).warn("  >>> WARNING : Current instance have no scenarios ! InstanceId : " + instanceId);
				return;
			}
			System.out.println("size of spcLookupTable for instanceId : " + instanceId + " is " + spcLookupTable.size());
			Logger.getLogger(CpcBase.class).debug("  >>> size of spcLookupTable for instanceId : " + instanceId + " is " + spcLookupTable.size());

			for (String spcKey : spcLookupTable.keySet()) {
				System.out.println("Spc ID : " + spcLookupTable.get(spcKey).getSpcReferance().getSpcId());
				Logger.getLogger(CpcBase.class).debug("  >>> Spc ID : " + spcLookupTable.get(spcKey).getSpcReferance().getSpcId());
			}
		}

		System.out.println("***************************************************************************************");
		Logger.getLogger(CpcBase.class).info("***************************************************************************************");

	}
	
	protected String getInstanceId(TlosProcessData tlosProcessData, boolean isTest) {
		
		String instanceId = null;
		
		if(isTest) {
			String userId = "" + tlosProcessData.getBaseScenarioInfos().getUserId();
			if (userId == null || userId.equals("")) {
				userId = "" + Calendar.getInstance().getTimeInMillis();
			}
			myLogger.info("   > InstanceID = " + userId + " olarak belirlenmistir.");
			instanceId = userId;
		} else {
			instanceId = tlosProcessData.getInstanceId();
			if (instanceId == null) {
				instanceId = "" + Calendar.getInstance().getTimeInMillis();
			}
			myLogger.info("   > InstanceID = " + instanceId + " olarak belirlenmiştir.");
		}
		
		return instanceId;
	}

	protected HashMap<String, Scenario> performLinearization(String instanceId, TlosProcessData tlosProcessData) {

		HashMap<String, Scenario> tmpScenarioList = new HashMap<String, Scenario>();

		ScenarioPathType scenarioPathType = new ScenarioPathType();

		scenarioPathType.setInstanceId(instanceId);
		scenarioPathType.setId(EngineeConstants.LONELY_JOBS);

		myLogger.info("   > iş ağacının işlenmekte olan dalı " + scenarioPathType.getFullPath() + " olarak belirlenmiştir.");

		// Bu joblari, serbest olarak ekliyoruz listeye

		Scenario myScenario = CpcUtils.getScenario(tlosProcessData, instanceId);
		myScenario.setID(EngineeConstants.LONELY_JOBS);
		
		// *** root sonrasina instanceid eklendi. *//*

		tmpScenarioList.put(scenarioPathType.getFullPath(), myScenario);

		myLogger.info("");
		myLogger.info(" 7 - Senaryolar, lineerleştirilme işlemine tabi tutulacak.");
		
		linearizeScenarios(scenarioPathType, tlosProcessData.getScenarioArray(), tmpScenarioList);
		
		myLogger.info("   > Lineerleştirilme işlemi OK.");
		
		return tmpScenarioList;
		
	}

	protected SpcLookupTable prepareSpcLookupTable(TlosProcessData tlosProcessData) throws TlosException {

		SpcLookupTable spcLookupTable = new SpcLookupTable();

		HashMap<String, SpcInfoType> table = spcLookupTable.getTable();
		
		String instanceId = getInstanceId(tlosProcessData, false);

		HashMap<String, Scenario> tmpScenarioList = performLinearization(instanceId, tlosProcessData);

		Iterator<String> keyIterator = tmpScenarioList.keySet().iterator();

		myLogger.info("");
		myLogger.info(" 8 - TlosProcessData içindeki senaryolardaki işlerin listesi çıkarılacak.");

		while (keyIterator.hasNext()) {

			String scenarioId = keyIterator.next();

			myLogger.info("");
			myLogger.info("  > Senaryo ismi : " + scenarioId);

			JobList jobList = tmpScenarioList.get(scenarioId).getJobList();

			if (!validateJobList(jobList)) {
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
			String userId = null; // Henüz ayarlanmadı !
			
			if (/*!scenarioId.equals(CpcUtils.getRootScenarioPath(instanceId)) &&*/ jobList.getJobPropertiesArray().length == 0) {
				spcInfoType = CpcUtils.getSpcInfo(userId, tlosProcessData.getInstanceId(), tmpScenarioList.get(scenarioId));
				spcInfoType.setSpcId(new ScenarioPathType(scenarioId));
			} else {
				Spc spc = new Spc(new ScenarioPathType(scenarioId), getSpaceWideRegistry(), transformJobList(jobList));

				spcInfoType = CpcUtils.getSpcInfo(spc, userId, tlosProcessData.getInstanceId(), tmpScenarioList.get(scenarioId));
				spcInfoType.setSpcId(new ScenarioPathType(scenarioId));

				if (!getSpaceWideRegistry().getServerConfig().getServerParams().getIsPersistent().getValueBoolean() || !JobQueueOperations.recoverJobQueue(spcInfoType.getSpcReferance().getSpcId(), spc.getJobQueue(), spc.getJobQueueIndex())) {
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
				BigInteger paramPreValueType = parameterList.get(i).getPreValue().getType();
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

		// if (getSpaceWideRegistry().getParameters() == null) {

		ArrayList<Parameter> myPramList = prepareParameterList();

		getSpaceWideRegistry().setParameters(myPramList);
		// }

		arrangeParameters(getSpaceWideRegistry().getParameters());
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

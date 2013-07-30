package com.likya.tlossw.core.cpc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.common.JobCommandTypeDocument.JobCommandType;
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
import com.likya.tlossw.model.engine.EngineeConstants;
import com.likya.tlossw.utils.CpcUtils;
import com.likya.tlossw.utils.FileUtils;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;

public abstract class CpcBase implements Runnable {

	public static boolean FORCED = true;
	public static boolean NORMAL = false;

	private boolean executionPermission = true;

	private SpaceWideRegistry spaceWideRegistry;
	private Logger myLogger;

	private final static String rootPath = "root";

	transient private Thread executerThread;

	public CpcBase(SpaceWideRegistry spaceWideRegistry) {
		this.spaceWideRegistry = spaceWideRegistry;
		myLogger = SpaceWideRegistry.getGlobalLogger();
	}

	protected void terminateAllJobs(boolean isForced) {

		for (String instanceId : getSpaceWideRegistry().getInstanceLookupTable().keySet()) {
			InstanceInfoType instanceInfoType = getSpaceWideRegistry().getInstanceLookupTable().get(instanceId);

			HashMap<String, SpcInfoType> spcMap = instanceInfoType.getSpcLookupTable();

			Iterator<String> keyIterator = spcMap.keySet().iterator();

			while (keyIterator.hasNext()) {
				String key = keyIterator.next();
				Spc spcreferance = spcMap.get(key).getSpcReferance();
				spcreferance.setExecutionPermission(false, isForced);
				while (spcreferance.getExecuterThread().isAlive()) {
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

	protected void linearizeScenarios(String path, Scenario[] scenarios, HashMap<String, Scenario> scenarioList) {

		ArrayIterator scenaryoListIterator = new ArrayIterator(scenarios);

		while (scenaryoListIterator.hasNext()) {

			String tmpPath = path + ".";
			Scenario scenario = (Scenario) (scenaryoListIterator.next());

			String scenarioId = tmpPath + scenario.getID().toString();

			myLogger.info("   > " + scenarioId + " senaryosu yani scenario ID=" + scenario.getID().toString() + " isleniyor.");

			scenarioList.put(scenarioId, scenario);

			if (scenario.getScenarioArray().length != 0) {
				linearizeScenarios(scenarioId, scenario.getScenarioArray(), scenarioList);
			}

		}
	}

	protected boolean validateJobList(JobList jobList) {

		Hashtable<String, String> testTable = new Hashtable<String, String>();

		// for (int index = 0; index < jobList.sizeOfJobPropertiesArray(); index++) {

		// jobList.setJobPropertiesArray(index,
		// ApplyXslt.transform(jobList.getJobPropertiesArray(index)));
		// try {
		// applyXPath.queryJobWithXPath(jobList.getJobPropertiesArray(index),
		// "/dat:jobProperties/dat:baseJobInfos/dat:jobInfos/com:jobTypeDetails/com:specialParameters");
		// applyXPath.queryJobWithXPath(jobList.getJobPropertiesArray(index),
		// "/dat:jobProperties[@ID=\"2\" and @agentId=\"0\"]/dat:baseJobInfos/dat:jobInfos/com:jobTypeDetails/com:specialParameters");
		// System.out.println("xpath");
		// applyXPath.queryJobWithXPath(jobList.getJobPropertiesArray(index),
		// "//dat:baseJobInfos/dat:jobInfos/com:jobTypeDetails/com:specialParameters");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// }

		ArrayIterator jobListIterator = new ArrayIterator(jobList.getJobPropertiesArray());
		String validationRequired = "NO";
		
		while (jobListIterator.hasNext()) {
			
			JobProperties jobPropertiesType = (JobProperties) (jobListIterator.next());

			// String validationRequired =
			// jobPropertiesType.getBaseJobInfos().getOSystem().toString();
			// if(validationRequired.equalsIg<noreCase("windows")) break;

			String jobId = jobPropertiesType.getID();
			String jobName = jobPropertiesType.getBaseJobInfos().getJsName();

			// if (jobPropertiesType.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommandType().toString().equals("BATCH PROCESS")) {
			// jobPropertiesType.getJobDescription().getApplication().getPOSIXApplication().getArgumentArray(1);
			// jobPropertiesType.getSweep().getAssignmentArray(0).getParameterArray(0);
			// String inputs[] = null;
			// String xpath = "//jsdl-posix:Argument[3]";
			// String xpath =
			// "substring(//jsdl-posix:Argument[3]/text(), 2, 3)";
			// try {
			// inputs = ApplyXPath.queryXmlWithXPath(jobPropertiesType,
			// xpath);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }

			if (validationRequired.equalsIgnoreCase("NO")) {
				break;

			}

			myLogger.info("   > Is Id si: " + jobId);
			myLogger.info("   > Is Adı : " + jobName);
			myLogger.info("   > Listeye eklemek icin validasyon yapiyorum. ");

			if (!testTable.containsKey(jobId)) {

				testTable.put(jobId, jobId);
				myLogger.info("     > OK isim validated.");

			} else {

				myLogger.error("Ayni Id ile birden fazla anahtar kullanilamaz ! => " + jobId);
				myLogger.info("     > Hayir, serbest joblar icinde ayni Id ile birden fazla is kullanilamaz.");

				return false;
			}

			if (jobPropertiesType.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommandType().toString().equals(JobCommandType.SHELL_SCRIPT)) {

				myLogger.debug("  Verilan shell komutunun kontrolu icin buraya birseyler eklemek lazim !!");
				// TODO verilan shell komutunun kontrolu icin buraya birseyler
				// eklemek lazim.

				return false;
			}

			// TODO jobpath in sonunda / olup olmamasi kontrol edilmeli, yoksa
			// eklenmeli.

			String jobCommandType = jobPropertiesType.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommandType().toString();
			String fileNameWtihPath = jobPropertiesType.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobPath() + jobCommandType;

			if (jobCommandType != null && jobCommandType.equals(JobCommandType.SYSTEM_COMMAND) && !FileUtils.checkFile(fileNameWtihPath)) {
				myLogger.debug("     > HATA : Belirtilen dosya " + fileNameWtihPath + " bulunamadi. ");
				myLogger.fatal("HATA : id : " + jobId + " name : " + jobName + " için belirtilen is dosyasi bulunamadi -> " + jobCommandType);

				return false;
			} else if (jobCommandType.equals(JobCommandType.BATCH_PROCESS) && (!FileUtils.checkFile(fileNameWtihPath) && jobPropertiesType.getBaseJobInfos().getOSystem().toString().equals("Windows"))) {
				myLogger.debug("     > HATA : Belirtilen dosya " + fileNameWtihPath + " bulunamadi. ");
				myLogger.fatal("HATA : id : " + jobId + " name : " + jobName + " için belirtilen is dosyasi bulunamadi -> " + jobCommandType);

				return false;
			} else {
				if (jobCommandType.equals("FTP")) {
					myLogger.debug("     > FTP : Validasyon icin bir kontrol koy! ");
				} else {
					myLogger.info("     > OK Belirtilen dosya " + fileNameWtihPath + " bulundu. ");
				}
			}

			/* VALIDATED state i ekle */
			LiveStateInfoUtils.insertNewLiveStateInfo(jobPropertiesType, StateName.INT_PENDING, SubstateName.INT_VALIDATED);

			// TODO infoBusManager i bilgilendir.

		}

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

	public static void dumpSpcLookupTable(String instanceId, HashMap<String, SpcInfoType> spcLookupTable) {

		System.out.println("**************************Dumping SpcLookupTable ***************************************");
		System.out.println("sizo of spcLookupTable for instanceId : " + instanceId + " is " + spcLookupTable.size());

		for (String spcKey : spcLookupTable.keySet()) {
			System.out.println("Spc ID : " + spcLookupTable.get(spcKey).getSpcReferance().getSpcId());
		}

		System.out.println("***************************************************************************************");

	}

	public static void dumpSpcLookupTables(SpaceWideRegistry spaceWideRegistry) {

		// Set<String> myMap = spcLookupTable.keySet();
		// Iterator<String> myIterator = myMap.iterator();
		System.out.println("**************************Dumping SpcLookupTables ***************************************");
		Logger.getLogger(CpcBase.class).info("  >>> **************************Dumping SpcLookupTables ***************************************");
		// while(myIterator.hasNext()) {
		// System.out.println("Spc ID : " +
		// spcLookupTable.get(myIterator.next()).getSpcReferance().getSpcId());
		// }
		for (String instanceId : spaceWideRegistry.getInstanceLookupTable().keySet()) {

			InstanceInfoType instanceInfoType = spaceWideRegistry.getInstanceLookupTable().get(instanceId);
			HashMap<String, SpcInfoType> spcLookupTable = instanceInfoType.getSpcLookupTable();
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

	protected HashMap<String, SpcInfoType> prepareSpcLookupTable(TlosProcessData tlosProcessData) throws TlosException {

		HashMap<String, SpcInfoType> scpLookupTable = new HashMap<String, SpcInfoType>();

		HashMap<String, Scenario> tmpScenarioList = new HashMap<String, Scenario>();

		String instanceId = tlosProcessData.getInstanceId();

		if (instanceId == null) {
			instanceId = "" + Calendar.getInstance().getTimeInMillis();
		}
		myLogger.info("   > InstanceID = " + instanceId + " olarak belirlenmistir.");
		String localRoot = getRootPath() + "." + instanceId;
		myLogger.info("   > is agacinin islenmekte olan dali " + localRoot + " olarak belirlenmistir.");

		// Bir senaryoya ait olmayan is listesi

		JobList lonelyJobList = tlosProcessData.getJobList();

		if (lonelyJobList != null && lonelyJobList.getJobPropertiesArray().length > 0) {
			myLogger.info("");
			myLogger.info(" 6 - TlosProcessData icinde bir Senaryoya ait olmayan serbest isler listesi cikarilacak.");
			/*
			 * if (!validateJobList(lonelyJobList)) { myLogger.info(
			 * "     > is listesi validasyonunda problem oldugundan WAITING e alinarak problemin giderilmesi beklenmektedir."
			 * ); myLogger.error(
			 * "Cpc Job List validation failed, process state changed to WAITING !"
			 * ); throw new TlosException(
			 * "Cpc Job List validation failed, process state changed to WAITING !"
			 * ); }
			 */
			// Bu joblari, serbest olarak ekliyoruz listeye

			Scenario myScenario = CpcUtils.getScenario(tlosProcessData, instanceId);

			// *** root sonrasina instanceid eklendi. *//*

			tmpScenarioList.put(localRoot + "." + EngineeConstants.LONELY_JOBS, myScenario);

			// System.out.println("   > Validasyonda problem olmadigindan "+localRoot
			// + "." + Cpc.LONELY_JOBS+" olarak Senaryo listesine eklendiler.");
			myLogger.info("   > Serbest isler " + localRoot + "." + EngineeConstants.LONELY_JOBS + " olarak Senaryo listesine eklendiler.");
		}

		// Senaryo listesi i�indeki senaryolar ## LINEERLESTIRME ##
		// System.out.println(" 7 - Senaryolar, lineerlestirilme islemine tabi tutulacak.");
		myLogger.info("");
		myLogger.info(" 7 - Senaryolar, lineerlestirilme islemine tabi tutulacak.");
		linearizeScenarios(localRoot, tlosProcessData.getScenarioArray(), tmpScenarioList);
		// System.out.println("   > Lineerlestirilme islemi OK.");
		myLogger.info("   > Lineerlestirilme islemi OK.");
		Iterator<String> keyIterator = tmpScenarioList.keySet().iterator();

		// System.out.println(" 8 - TlosProcessData icindeki Senaryolardaki islerin listesi cikarilacak.");
		myLogger.info("");
		myLogger.info(" 8 - TlosProcessData icindeki Senaryolardaki islerin listesi cikarilacak.");

		while (keyIterator.hasNext()) {

			String scenarioId = keyIterator.next();

			myLogger.info("");
			myLogger.info("  > Senaryo ismi : " + scenarioId);

			JobList jobList = tmpScenarioList.get(scenarioId).getJobList();

			if (!validateJobList(jobList)) {
				// TODO WAITING e nasil alacagiz?
				// System.out.println("     > is listesi validasyonunda problem oldugundan WAITING e alinarak problemin giderilmesi beklenmektedir.");
				myLogger.info("     > is listesi validasyonunda problem oldugundan WAITING e alinarak problemin giderilmesi beklenmektedir.");
				myLogger.error("Cpc Scenario jobs validation failed, process state changed to WAITING !");

				continue; // 08.07.2013 Serkan
				// throw new TlosException("Cpc Job List validation failed, process state changed to WAITING !");
				/*
				 * eski hali buydu. hakan
				 * myLogger.error("Cpc failed, terminating !"); break;
				 */
			}

			if (jobList.getJobPropertiesArray().length == 0) {
				myLogger.error(scenarioId + " isimli senaryo bilgileri yüklenemedi ya da iş listesi bos geldi !");
				myLogger.error(scenarioId + " isimli senaryo için spc başlatılmıyor !");
				continue;
			}

			Spc spc = new Spc(scenarioId, getSpaceWideRegistry(), transformJobList(jobList));
			
			SpcInfoType spcInfoType = CpcUtils.getSpcInfo(spc, null, tlosProcessData.getInstanceId(), tmpScenarioList.get(scenarioId));

			scpLookupTable.put(scenarioId, spcInfoType);

			if (!getSpaceWideRegistry().getServerConfig().getServerParams().getIsPersistent().getValueBoolean() || !JobQueueOperations.recoverJobQueue(spcInfoType.getSpcReferance().getSpcId(), spc.getJobQueue(), spc.getJobQueueIndex())) {
				if (!spc.initScenarioInfo()) {
					myLogger.warn(scenarioId + " isimli senaryo bilgileri y�klenemedi ya da is listesi bos geldi !");
					Logger.getLogger(CpcBase.class).warn(" WARNING : " + scenarioId + " isimli senaryo bilgileri y�klenemedi ya da is listesi bos geldi !");

					System.exit(-1);
				}
			}

			myLogger.info("  > Senaryo yuklendi !");

		}

		myLogger.info("");
		myLogger.info(" > Senaryolarin ve islerin SPC (spcLookUpTable) senaryo agacina yuklenme islemi bitti !");

		return scpLookupTable;
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

	public Thread getExecuterThread() {
		return executerThread;
	}

	public void setExecuterThread(Thread executerThread) {
		this.executerThread = executerThread;
	}

	public static String getRootPath() {
		return rootPath;
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

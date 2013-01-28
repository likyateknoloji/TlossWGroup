package com.likya.tlosswagent.core.cpc;

import org.apache.log4j.Logger;

import com.likya.tlosswagent.TlosSWAgent;
import com.likya.tlosswagent.utils.SWAgentRegistry;

public abstract class CpcBase implements Runnable {

	public static boolean FORCED = true;
	public static boolean NORMAL = false;

	private boolean executionPermission = true;

	private SWAgentRegistry swAgentRegistry;

//	private final static String rootPath = "root";

//	public final static String LONELY_JOBS = "serbest";

	transient private Thread executerThread;
	
	transient private Logger cpcLogger;
	

	
	public CpcBase(SWAgentRegistry swAgentRegistry) {
		this.swAgentRegistry = swAgentRegistry;
		this.cpcLogger = SWAgentRegistry.getsWAgentLogger();
	}

	public boolean isExecutionPermission() {
		return executionPermission;
	}

	public void setExecutionPermission(boolean executionPermission) {
		this.executionPermission = executionPermission;
	}

	public SWAgentRegistry getSwAgentRegistry() {
		return swAgentRegistry;
	}
	
	public Logger getCpcLogger() {
		return cpcLogger;
	}


/*	protected void terminateAllJobs(boolean isForced) {

		for (String instanceId : getSwAgentRegistry().getInstanceLookupTable().keySet()) {
			InstanceInfoType instanceInfoType = getSwAgentRegistry().getInstanceLookupTable().get(instanceId);

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
*/
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */

/*	protected void linearizeScenarios(String path, Scenario[] scenarios, HashMap<String, Scenario> scenarioList) {

		ArrayIterator scenaryoListIterator = new ArrayIterator(scenarios);

		while (scenaryoListIterator.hasNext()) {

			String tmpPath = path + ".";
			Scenario scenario = (Scenario) (scenaryoListIterator.next());

			String scenarioId = tmpPath + scenario.getID().toString();

			scenarioList.put(scenarioId, scenario);

			if (scenario.getScenarioArray().length != 0) {
				linearizeScenarios(scenarioId, scenario.getScenarioArray(), scenarioList);
			}

		}
	}*/

/*	protected boolean validateJobList(JobList jobList) {

		Hashtable<String, String> testTable = new Hashtable<String, String>();

		ArrayIterator jobListIterator = new ArrayIterator(jobList.getJobPropertiesArray());
		while (jobListIterator.hasNext()) {
			JobProperties jobPropertiesType = (JobProperties) (jobListIterator.next());
			String jobKey = jobPropertiesType.getJsName();
			SWAgentRegistry.getsWAgentLogger().info("jobKey : " + jobKey);
			if (!testTable.containsKey(jobKey)) {
				testTable.put(jobKey, jobKey);
			} else {
				SWAgentRegistry.getsWAgentLogger().error("Ayný isimde birden fazla anahtar kullanýlamaz ! => " + jobKey);
				return false;
			}
			if (jobPropertiesType.getJobCommandType().equals("script") && !FileUtils.checkFile(jobPropertiesType.getJobCommand())) {
				SWAgentRegistry.getsWAgentLogger().fatal("HATA : " + jobKey + " için belirtilen iþ dosyasý bulunamadý -> " + jobPropertiesType.getJobCommand());
				return false;
			}
		}

		return true;
	}*/

/*	protected ArrayList<JobRuntimeProperties> transformJobList(JobList jobList) {

		SWAgentRegistry.getsWAgentLogger().debug("start:transformJobList");

		ArrayList<JobRuntimeProperties> transformTable = new ArrayList<JobRuntimeProperties>();

		ArrayIterator jobListIterator = new ArrayIterator(jobList.getJobPropertiesArray());

		while (jobListIterator.hasNext()) {
			JobProperties jobProperties = (JobProperties) (jobListIterator.next());
			JobRuntimeProperties jobRuntimeProperties = new JobRuntimeProperties();
			jobProperties.getLiveStateInfo().setStateName(StateName.IDLED);
			jobRuntimeProperties.setJobProperties(jobProperties);
			transformTable.add(jobRuntimeProperties);
		}

		SWAgentRegistry.getsWAgentLogger().debug("end:transformJobList");

		return transformTable;
	}
*/
	public Thread getExecuterThread() {
		return executerThread;
	}

	public void setExecuterThread(Thread executerThread) {
		this.executerThread = executerThread;
	}

//	public static String getRootPath() {
//		return rootPath;
//	}

/*	public static void dumpSpcLookupTable(String instanceId, HashMap<String, SpcInfoType> spcLookupTable) {

		System.out.println("**************************Dumping SpcLookupTable ***************************************");
		System.out.println("sizo of spcLookupTable for instanceId : " + instanceId + " is " + spcLookupTable.size());

		for (String spcKey : spcLookupTable.keySet()) {
			System.out.println("Spc ID : " + spcLookupTable.get(spcKey).getSpcReferance().getSpcId());
		}

		System.out.println("***************************************************************************************");

	}
*/
/*	public static void dumpSpcLookupTables(SWAgentRegistry swAgentRegistry) {

		System.out.println("**************************Dumping SpcLookupTables ***************************************");
		for (String instanceId : swAgentRegistry.getInstanceLookupTable().keySet()) {

			InstanceInfoType instanceInfoType = swAgentRegistry.getInstanceLookupTable().get(instanceId);
			HashMap<String, SpcInfoType> spcLookupTable = instanceInfoType.getSpcLookupTable();
			if(spcLookupTable == null) {
				System.out.println("Current instance have no scenarios ! InstanceId : " + instanceId);
				return;
			}
			System.out.println("size of spcLookupTable for instanceId : " + instanceId + " is " + spcLookupTable.size());

			for (String spcKey : spcLookupTable.keySet()) {
				System.out.println("Spc ID : " + spcLookupTable.get(spcKey).getSpcReferance().getSpcId());
			}
		}

		System.out.println("***************************************************************************************");

	}
*/
/*	protected HashMap<String, SpcInfoType> prepareSpcLookupTable(TlosProcessData tlosProcessData) throws TlosException {

		HashMap<String, SpcInfoType> scpLookupTable = new HashMap<String, SpcInfoType>();

		HashMap<String, Scenario> tmpScenarioList = new HashMap<String, Scenario>();

		String instanceId = tlosProcessData.getInstanceId();
		if (instanceId == null) {
			instanceId = "" + Calendar.getInstance().getTimeInMillis();
		}

		String localRoot = getRootPath() + "." + instanceId;

		// Bir senaryoya ait olmayan iþ listesi
		JobList lonelyJobList = tlosProcessData.getJobList();

		if (lonelyJobList != null && lonelyJobList.getJobPropertiesArray().length > 0) {
			if (!validateJobList(lonelyJobList)) {
				SWAgentRegistry.getsWAgentLogger().error("Cpc Job List validation failed, process state changed to WAITING !");
				throw new TlosException("Cpc Job List validation failed, process state changed to WAITING !");
			}

			// Bu joblarý, serbest olarak ekliyoruz listeye

			Scenario myScenario = Scenario.Factory.newInstance();
			myScenario.setJobList(lonelyJobList);

			*/ 
	         //**
			 //* root sonrasýna instanceid eklendi.
			 //*/
	/*

			tmpScenarioList.put(localRoot + "." + Cpc.LONELY_JOBS, myScenario);
		}
		// Senaryo listesi içindeki senaryolar

		linearizeScenarios(localRoot, tlosProcessData.getScenarioArray(), tmpScenarioList);

		Iterator<String> keyIterator = tmpScenarioList.keySet().iterator();

		while (keyIterator.hasNext()) {
			String scenarioId = keyIterator.next();
			SWAgentRegistry.getsWAgentLogger().info("Loading scenario " + scenarioId);
			System.out.print("Loading scenario " + scenarioId + "\n");
			JobList jobList = tmpScenarioList.get(scenarioId).getJobList();
			if (!validateJobList(jobList)) {
				SWAgentRegistry.getsWAgentLogger().error("Cpc failed, terminating !");
				break;
			}

			Spc spc = new Spc(scenarioId, getSwAgentRegistry(), transformJobList(jobList));
			LiveStateInfo myLiveStateInfo = LiveStateInfo.Factory.newInstance();
			myLiveStateInfo.setStateName(StateName.IDLED);
			spc.setLiveStateInfo(myLiveStateInfo);
			Thread thread = new Thread(spc);
			spc.setExecuterThread(thread);

			Scenario tmpScenario = tmpScenarioList.get(scenarioId);
			spc.setJsName(tmpScenario.getJsName());
			spc.setConcurrent(tmpScenario.getConcurrent());
			spc.setComment(tmpScenario.getComment());
			spc.setInstanceId(instanceId);
			spc.setDependencyList(tmpScenario.getDependencyList());
			spc.setScenarioStatusList(tmpScenario.getScenarioStatusList());
			spc.setUserName(tmpScenario.getID());

			SpcInfoType spcInfoType = new SpcInfoType();
			
			spcInfoType.setJsName(spc.getJsName());
			spcInfoType.setConcurrent(spc.isConcurrent());
			spcInfoType.setComment(spc.getComment());
			spcInfoType.setDependencyList(spc.getDependencyList());
			spcInfoType.setScenarioStatusList(spc.getScenarioStatusList());
			spcInfoType.setUserName(spc.getUserName());
			
			spcInfoType.setSpcReferance(spc);
			scpLookupTable.put(scenarioId, spcInfoType);

			if (!spc.initScenarioInfo() || spc.getJobQueue().size() == 0) {
				SWAgentRegistry.getsWAgentLogger().fatal(scenarioId + " isimli senaryo bilgileri yüklenemedi ya da iþ listesi boþ geldi ! Program sona erdi.");
				return null;
			}
			System.out.println("Loaded !");
		}

		return scpLookupTable;
	}
*/
	
	public static void cleanAllTasks() {
		TlosSWAgent.getSwAgentRegistry().getTaskQueManagerRef().resetTaskQueue();
		TlosSWAgent.getSwAgentRegistry().getOutputQueManagerRef().resetOutputQueue();
		//TODO server cleanAll successful gibi bir mesaj don!
	}
	
}

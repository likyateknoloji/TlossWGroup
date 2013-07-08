package com.likya.tlossw.test.cpc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.likya.tlos.model.xmlbeans.common.JobCommandTypeDocument.JobCommandType;
import com.likya.tlos.model.xmlbeans.data.JobListDocument.JobList;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.cpc.CpcBase;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.core.spc.helpers.JobQueueOperations;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.exceptions.TlosException;
import com.likya.tlossw.model.engine.EngineeConstants;
import com.likya.tlossw.utils.FileUtils;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;

/**
 * Tests for {@link www.tlos.com.tr}.
 * 
 * @author serkan.tas@likyateknoloji.com (Serkan Taş)
 */
@RunWith(JUnit4.class)
public class CpcTester {

	private Logger myLogger = Logger.getLogger(CpcTester.class);

	public static String filePath;

	@BeforeClass
	public static void setUp() {
	}

	@Test
	public void initPrep() throws Exception {
		
		TlosProcessData tlosProcessData = getTlosProcessData();
		
		SpaceWideRegistry spaceWideRegistry = SpaceWideRegistry.getInstance();
		
		prepareSpcLookupTable(tlosProcessData, spaceWideRegistry, "root");
	}
	
	public String getFile(String fileName) {
		return ParsingUtils.getConcatenatedPathAndFileName("moduleTest" + File.separator, fileName);
	}

	public TlosProcessData getTlosProcessData() throws XmlException, IOException {
		
		String fileName = getFile("moduleManagementOperations.getTlosConfig.xquery");

		File tlosDataFile = new File(fileName);
		
		TlosProcessData tlosProcessData = TlosProcessDataDocument.Factory.parse(tlosDataFile).getTlosProcessData();
		
		return tlosProcessData;
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
	
	protected boolean validateJobList(JobList jobList) {

		Hashtable<String, String> testTable = new Hashtable<String, String>();

		ArrayIterator jobListIterator = new ArrayIterator(jobList.getJobPropertiesArray());

		while (jobListIterator.hasNext()) {
			JobProperties jobPropertiesType = (JobProperties) (jobListIterator.next());

			String jobKey = jobPropertiesType.getBaseJobInfos().getJsName();


			myLogger.info("   > Is ismi : " + jobKey);
			myLogger.info("   > Listeye eklemek icin validasyon yapiyorum. ");

			if (!testTable.containsKey(jobKey)) {

				testTable.put(jobKey, jobKey);
				myLogger.info("     > OK isim validated.");

			} else {

				myLogger.error("Ayni isimde birden fazla anahtar kullanilamaz ! => " + jobKey);
				myLogger.info("     > Hayir, serbest joblar icinde ayni isimde birden fazla is kullanilamaz.");

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
				myLogger.fatal("HATA : " + jobKey + " için belirtilen is dosyasi bulunamadi -> " + jobCommandType);

				return false;
			} else if (jobCommandType.equals(JobCommandType.BATCH_PROCESS) && (!FileUtils.checkFile(fileNameWtihPath) && jobPropertiesType.getBaseJobInfos().getOSystem().toString().equals("Windows"))) {
				myLogger.debug("     > HATA : Belirtilen dosya " + fileNameWtihPath + " bulunamadi. ");
				myLogger.fatal("HATA : " + jobKey + " için belirtilen is dosyasi bulunamadi -> " + jobCommandType);

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
	
	protected HashMap<String, SpcInfoType> prepareSpcLookupTable(TlosProcessData tlosProcessData, SpaceWideRegistry spaceWideRegistry, String rootPath) throws TlosException {
		
		HashMap<String, SpcInfoType> scpLookupTable = new HashMap<String, SpcInfoType>();

		HashMap<String, Scenario> tmpScenarioList = new HashMap<String, Scenario>();

		String instanceId = tlosProcessData.getInstanceId();

		if (instanceId == null) {
			instanceId = "" + Calendar.getInstance().getTimeInMillis();
		}
		myLogger.info("   > InstanceID = " + instanceId + " olarak belirlenmistir.");
		String localRoot = rootPath + "." + instanceId;
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

			Scenario myScenario = Scenario.Factory.newInstance();
			myScenario.setJobList(lonelyJobList);

			// myScenario.getConcurrencyManagement().setInstanceId(instanceId.toString());
			tlosProcessData.getConcurrencyManagement().setInstanceId(instanceId);

			myScenario.setBaseScenarioInfos(tlosProcessData.getBaseScenarioInfos());
			myScenario.setDependencyList(tlosProcessData.getDependencyList());
			myScenario.setScenarioStatusList(tlosProcessData.getScenarioStatusList());
			myScenario.setAlarmPreference(tlosProcessData.getAlarmPreference());
			myScenario.setTimeManagement(tlosProcessData.getTimeManagement());
			myScenario.setAdvancedScenarioInfos(tlosProcessData.getAdvancedScenarioInfos());
			myScenario.setConcurrencyManagement(tlosProcessData.getConcurrencyManagement());
			myScenario.setLocalParameters(tlosProcessData.getLocalParameters());

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
				throw new TlosException("Cpc Job List validation failed, process state changed to WAITING !");
				/*
				 * eski hali buydu. hakan
				 * myLogger.error("Cpc failed, terminating !"); break;
				 */
			}
			// if(addToJobLookupTable(scenarioId, jobList,jobLookupTable)) {
			// }

			Spc spc = new Spc(scenarioId, spaceWideRegistry, transformJobList(jobList));

			LiveStateInfo myLiveStateInfo = LiveStateInfo.Factory.newInstance();
			myLiveStateInfo.setStateName(StateName.PENDING);
			myLiveStateInfo.setSubstateName(SubstateName.IDLED);
			spc.setLiveStateInfo(myLiveStateInfo);
			Thread thread = new Thread(spc);
			/* thread.setName("SPC"); */
			spc.setExecuterThread(thread);

			Scenario tmpScenario = tmpScenarioList.get(scenarioId);

			spc.setJsName(tmpScenario.getBaseScenarioInfos().getJsName());
			spc.setConcurrent(tmpScenario.getConcurrencyManagement().getConcurrent());
			spc.setComment(tmpScenario.getBaseScenarioInfos().getComment());
			spc.setInstanceId(instanceId);
			// spc.setDependencyList(tmpScenario.getDependencyList());
			// spc.setScenarioStatusList(tmpScenario.getScenarioStatusList());
			spc.setUserName(null);
			// spc.setUserName(tmpScenario.getID());

			tmpScenario.getConcurrencyManagement().setInstanceId(spaceWideRegistry.getTlosProcessData().getInstanceId());

			spc.setBaseScenarioInfos(tmpScenario.getBaseScenarioInfos());
			spc.setDependencyList(tmpScenario.getDependencyList());
			spc.setScenarioStatusList(tmpScenario.getScenarioStatusList());
			spc.setAlarmPreference(tmpScenario.getAlarmPreference());
			spc.setTimeManagement(tmpScenario.getTimeManagement());
			spc.setAdvancedScenarioInfos(tmpScenario.getAdvancedScenarioInfos());
			spc.setConcurrencyManagement(tmpScenario.getConcurrencyManagement());
			spc.setLocalParameters(tmpScenario.getLocalParameters());

			SpcInfoType spcInfoType = new SpcInfoType();

			spcInfoType.setJsName(spc.getBaseScenarioInfos().getJsName());
			spcInfoType.setConcurrent(spc.getConcurrencyManagement().getConcurrent());
			spcInfoType.setComment(spc.getBaseScenarioInfos().getComment());
			spcInfoType.setUserName(null);
			// spcInfoType.setUserName(spc.getUserName());

			Scenario scenario = Scenario.Factory.newInstance();

			scenario.setBaseScenarioInfos(spc.getBaseScenarioInfos());
			scenario.setDependencyList(spc.getDependencyList());
			scenario.setScenarioStatusList(spc.getScenarioStatusList());
			scenario.setAlarmPreference(spc.getAlarmPreference());
			scenario.setTimeManagement(spc.getTimeManagement());
			scenario.setAdvancedScenarioInfos(spc.getAdvancedScenarioInfos());
			scenario.setConcurrencyManagement(spc.getConcurrencyManagement());
			scenario.setLocalParameters(spc.getLocalParameters());

			spcInfoType.setScenario(scenario);
			spcInfoType.setSpcReferance(spc);

			scpLookupTable.put(scenarioId, spcInfoType);

			if (!spaceWideRegistry.getServerConfig().getServerParams().getIsPersistent().getValueBoolean() || !JobQueueOperations.recoverJobQueue(spcInfoType.getSpcReferance().getSpcId(), spc.getJobQueue(), spc.getJobQueueIndex())) {
				if (!spc.initScenarioInfo() /*|| spc.getJobQueue().size() == 0*/) {
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

	
}
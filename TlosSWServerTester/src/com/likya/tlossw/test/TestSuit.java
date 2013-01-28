package com.likya.tlossw.test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import com.likya.tlos.model.xmlbeans.config.AgentOptionsDocument;
import com.likya.tlos.model.xmlbeans.config.DebugModeDocument;
import com.likya.tlos.model.xmlbeans.config.InOutJmxDurationForUnavailabilityDocument;
import com.likya.tlos.model.xmlbeans.config.InfoBusOptionsDocument;
import com.likya.tlos.model.xmlbeans.config.IsPersistentDocument;
import com.likya.tlos.model.xmlbeans.config.PerformanceDocument;
import com.likya.tlos.model.xmlbeans.config.PeriodDocument;
import com.likya.tlos.model.xmlbeans.config.ResourceListDurationDocument;
import com.likya.tlos.model.xmlbeans.config.SettingsDocument;
import com.likya.tlos.model.xmlbeans.config.ThresholdDocument;
import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument;
import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument.TlosConfigInfo;
import com.likya.tlos.model.xmlbeans.config.TlosFrequencyDocument;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.agents.AgentManager;
import com.likya.tlossw.db.utils.DBUtils;
import com.likya.tlossw.exceptions.GlobalParameterLoadException;
import com.likya.tlossw.exceptions.TlosFatalException;
import com.likya.tlossw.infobus.InfoBusManager;
import com.likya.tlossw.infobus.helper.JobInfo;
import com.likya.tlossw.perfmng.PerformanceManager;
import com.likya.tlossw.utils.FileUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.date.DateUtils;
import com.likya.tlossw.utils.i18n.ResourceMapper;

public abstract class TestSuit {

	public static Logger testLogger = Logger.getLogger(TestSuit.class);

	private SpaceWideRegistry spaceWideRegistry;

	public TestSuit() {

		spaceWideRegistry = SpaceWideRegistry.getInstance();
		SpaceWideRegistry.setGlobalLogger(TestSuit.testLogger);

		/**
		 * TlosConfig nesnesi oluﬂturup Global Kay›t defterine
		 * tan›maln›yor
		 */

		TlosConfigInfoDocument tlosConfigInfoDocument = TlosConfigInfoDocument.Factory.newInstance();
		tlosConfigInfoDocument.addNewTlosConfigInfo();

		TlosConfigInfo tlosConfigInfo = tlosConfigInfoDocument.getTlosConfigInfo();

		/**
		 * InfoBus için gerekli olan bilgiler tan›ml› TlosConfig nesnesine ekleniyor
		 */

		ThresholdDocument thresholdDocument = ThresholdDocument.Factory.newInstance();
		thresholdDocument.addNewThreshold();
		thresholdDocument.getThreshold().setHigh((short) 10);
		thresholdDocument.getThreshold().setLow((short) 5);

		PerformanceDocument performanceDocument = PerformanceDocument.Factory.newInstance();
		performanceDocument.addNewPerformance();
		performanceDocument.getPerformance().setThreshold(thresholdDocument.getThreshold());

		SettingsDocument settingsDocument = SettingsDocument.Factory.newInstance();
		settingsDocument.addNewSettings();

		InfoBusOptionsDocument infoBusOptionsDocument = InfoBusOptionsDocument.Factory.newInstance();
		infoBusOptionsDocument.addNewInfoBusOptions();

		PeriodDocument periodDocument = PeriodDocument.Factory.newInstance();
		periodDocument.addNewPeriod();
		periodDocument.getPeriod().setPeriodValue(BigInteger.valueOf(1));

		DebugModeDocument debugModeDocument = DebugModeDocument.Factory.newInstance();
		debugModeDocument.addNewDebugMode();
		debugModeDocument.getDebugMode().setValueBoolean(false);

		IsPersistentDocument isPersistentDocument = IsPersistentDocument.Factory.newInstance();
		isPersistentDocument.addNewIsPersistent();
		isPersistentDocument.getIsPersistent().setValueBoolean(false);

		TlosFrequencyDocument tlosFrequencyDocument = TlosFrequencyDocument.Factory.newInstance();
		tlosFrequencyDocument.addNewTlosFrequency();
		tlosFrequencyDocument.getTlosFrequency().setFrequency(1);

		tlosConfigInfo.setPerformance(performanceDocument.getPerformance());

		infoBusOptionsDocument.getInfoBusOptions().setPeriod(periodDocument.getPeriod());

		settingsDocument.getSettings().setInfoBusOptions(infoBusOptionsDocument.getInfoBusOptions());
		settingsDocument.getSettings().setDebugMode(debugModeDocument.getDebugMode());
		settingsDocument.getSettings().setIsPersistent(isPersistentDocument.getIsPersistent());
		settingsDocument.getSettings().setTlosFrequency(tlosFrequencyDocument.getTlosFrequency());

		tlosConfigInfoDocument.getTlosConfigInfo().setSettings(settingsDocument.getSettings());

		spaceWideRegistry.setTlosSWConfigInfo(tlosConfigInfo);

		TlosSpaceWide.setSpaceWideRegistry(spaceWideRegistry);

	}

	public JobProperties getJobPropertiesFromExist() {

		System.out.println("Connecting DB...");

		Collection col;
		JobProperties jobProperties = null;

		System.out.println("Connection ok ! Retrieving jobProperties ...");

		try {

			col = DBConnection.startExistDBSystem();

			String result = DBConnection.executeExistQuery("fl:getListenerJob(2)", col);

			jobProperties = JobPropertiesDocument.Factory.parse(result).getJobProperties();
		} catch (XmlException e) {
			e.printStackTrace();
		} catch (XMLDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("jobProperties ok !");

		return jobProperties;
	}

	public JobProperties getJobPropertiesFromFile(String fileName) {

		JobProperties jobProperties = null;

		StringBuffer jobPropertiesXMLString = FileUtils.readFile(fileName);

		try {

			jobProperties = JobPropertiesDocument.Factory.parse(jobPropertiesXMLString.toString()).getJobProperties();
		} catch (XmlException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jobProperties;
	}

	public Collection geteXistCollection() throws Exception {

		Collection collection = DBConnection.startExistDBSystem();

		if (collection == null) {
			System.out.println("Collection is null, check your eXist DB if it is running !");
			System.exit(-1);
		}

		return collection;

	}

	public HashMap<String, DbProperties> getDbPropertiesFromExist() throws Exception {

		HashMap<String, DbProperties> poolParams = new HashMap<String, DbProperties>();

		XPathQueryService service = (XPathQueryService) geteXistCollection().getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace db=\"http://db.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleDBConnectionsOperations.xquery\";" + "db:getDbConnectionAll()";

		ResourceSet result = service.query(xQueryStr);
		ResourceIterator i = result.getIterator();
		DbProperties dbProperties = null;

		while (i.hasMoreResources()) {
			Resource r = i.nextResource();
			String xmlContent = (String) r.getContent();

			try {

				dbProperties = DbPropertiesDocument.Factory.parse(xmlContent).getDbProperties();
				poolParams.put(dbProperties.getID().toString(), dbProperties);
			} catch (XmlException e) {
				e.printStackTrace();
			}
		}

		return poolParams;
	}

	public DbProperties getDbPropertiesFromFile(String fileName) {

		DbProperties dbProperties = null;

		StringBuffer dbPropertiesXMLString = FileUtils.readFile(fileName);

		try {

			dbProperties = DbPropertiesDocument.Factory.parse(dbPropertiesXMLString.toString()).getDbProperties();
		} catch (XmlException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dbProperties;
	}

	public DbConnectionProfile getDbConnectionProfileFromFile(String fileName) {

		DbConnectionProfile dbConnectionProfile = null;

		StringBuffer dbConnectionProfileXMLString = FileUtils.readFile(fileName);

		try {

			dbConnectionProfile = DbConnectionProfileDocument.Factory.parse(dbConnectionProfileXMLString.toString()).getDbConnectionProfile();
		} catch (XmlException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dbConnectionProfile;
	}

	public FtpProperties getFtpPropertiesFromFile(String fileName) {

		FtpProperties ftpProperties = null;

		StringBuffer ftpPropertiesXMLString = FileUtils.readFile(fileName);

		try {
			ftpProperties = FtpPropertiesDocument.Factory.parse(ftpPropertiesXMLString.toString()).getFtpProperties();
		} catch (XmlException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ftpProperties;
	}

	public void startInfoBusSystem(Logger myLogger) throws Exception {

		myLogger.info("");
		myLogger.info("############# infoBus Manager  ##################");
		myLogger.info("Start the infoBus manager...");

		InfoBusManager infoBusManager = new InfoBusManager();

		Thread infoBusManagerService = new Thread(infoBusManager);

		infoBusManagerService.setName(InfoBusManager.class.getName());
		getSpaceWideRegistry().setInfoBus(infoBusManager);

		infoBusManagerService.start();

		myLogger.info("Started !");
		myLogger.info("Waiting for incoming messages ...");
		myLogger.info("#############################################");
		myLogger.info("");
	}

	public void shutDownInfoBusSystem() {

		if (getSpaceWideRegistry().getInfoBus() == null) {
			return;
		}

		try {
			getSpaceWideRegistry().getInfoBus().terminate(true);
			getSpaceWideRegistry().setInfoBus(null);
		} catch (RuntimeException e) {
		}
	}

	public JobInfo getStatusChangeInfo() {

		JobInfo jobInfo = new JobInfo();
		// jobInfo.setTreePath(getJobRuntimeProperties().getTreePath());

		jobInfo.setTreePath("/TlosProcessDataAll/RUN[@id='1119']/dat:TlosProcessData/dat:jobList/dat:jobProperties[@ID='1' and @agentId='1' and not(exists(@LSIDateTime))]");
		jobInfo.setJobKey("JobKey");
		jobInfo.setJobID("1");
		jobInfo.setUserID(1);
		jobInfo.setAgentID(1);

		LiveStateInfo myLiveStateInfo = LiveStateInfo.Factory.newInstance();

		myLiveStateInfo.setStateName(StateName.RUNNING);
		myLiveStateInfo.setSubstateName(SubstateName.COMPLETED);
		myLiveStateInfo.setStatusName(StatusName.SUCCESS);

		jobInfo.setLiveLiveStateInfo(myLiveStateInfo);
		jobInfo.getLiveLiveStateInfo().setLSIDateTime(DateUtils.getW3CDateTime());
		Date infoTime = Calendar.getInstance().getTime();
		jobInfo.setInfoDate(infoTime);

		return jobInfo;
	}

	public void startPerformanceManager() {

		testLogger.info("");
		testLogger.info("############# Performance Manager #################");

		PerformanceManager performanceManager = null;
		try {
			performanceManager = new PerformanceManager(getSpaceWideRegistry());
		} catch (TlosFatalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread starterThread = new Thread(performanceManager);

		getSpaceWideRegistry().setPerformanceManagerReference(performanceManager);
		getSpaceWideRegistry().getPerformanceManagerReference().setExecuterThread(starterThread);

		// performanceManager.setExecuterThread(starterThread);

		starterThread.start();

		testLogger.info("  OK");
		testLogger.info(ResourceMapper.SECTION_DIVISON_KARE);
		testLogger.info("");

	}

	public SpaceWideRegistry getSpaceWideRegistry() {
		return spaceWideRegistry;
	}

	public void startAgentManager() {
		try {
			testLogger.info("");
			testLogger.info("############# Agent Manager ################");
			// testLogger.info(getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.HBLISTENER_STARTING_INFO));

			InOutJmxDurationForUnavailabilityDocument inOutJmxDurationForUnavailabilityDocument = InOutJmxDurationForUnavailabilityDocument.Factory.newInstance();
			inOutJmxDurationForUnavailabilityDocument.addNewInOutJmxDurationForUnavailability();
			inOutJmxDurationForUnavailabilityDocument.getInOutJmxDurationForUnavailability().setDurationValue(15);

			ResourceListDurationDocument resourceListDurationDocument = ResourceListDurationDocument.Factory.newInstance();
			resourceListDurationDocument.addNewResourceListDuration();
			resourceListDurationDocument.getResourceListDuration().setDurationValue(900);

			AgentOptionsDocument agentOptionsDocument = AgentOptionsDocument.Factory.newInstance();
			agentOptionsDocument.addNewAgentOptions();
			agentOptionsDocument.getAgentOptions().setInOutJmxDurationForUnavailability(inOutJmxDurationForUnavailabilityDocument.getInOutJmxDurationForUnavailability());
			agentOptionsDocument.getAgentOptions().setResourceListDuration(resourceListDurationDocument.getResourceListDuration());

			TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo().getSettings().setAgentOptions(agentOptionsDocument.getAgentOptions());

			AgentManager agentManager = new AgentManager();
			Thread executerThread = new Thread(agentManager);

			getSpaceWideRegistry().setAgentManagerReference(agentManager);
			getSpaceWideRegistry().getAgentManagerReference().setExecuterThread(executerThread);

			executerThread.setName("agentManager");
			executerThread.start();

			// testLogger.info(getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.HBLISTENER_STARTED_INFO));
			testLogger.info(ResourceMapper.SECTION_DIVISON_KARE);
			testLogger.info("");
		} catch (Exception e) {
			testLogger.info("   > Close application reason : cannot recover AgentCache !");
			testLogger.info("   > Clean tmp folder or set persistent to false and restart the application !");
			System.exit(-1);
		}
	}

	public ArrayList<Parameter> prepareParameterList() throws GlobalParameterLoadException {

		testLogger.info(" 3,5 - Global Parametreler Yukleniyor..");

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

			testLogger.info("   > Yuklendi !");

		} else {
			testLogger.info("   > YukleneMEdi  parameterList = null ! ");
			throw new GlobalParameterLoadException("YukleneMEdi  parameterList = null ! ");
		}

		return parameterList;
	}
}

package com.likya.tlossw;

import java.lang.Thread.State;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import javax.xml.transform.OutputKeys;

import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.exist.xmldb.DatabaseInstanceManager;
import org.mortbay.jetty.Server;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument.TlosConfigInfo;
import com.likya.tlos.model.xmlbeans.data.JobListDocument.JobList;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlos.model.xmlbeans.state.GlobalStateDefinitionDocument.GlobalStateDefinition;
import com.likya.tlossw.core.agents.AgentManager;
import com.likya.tlossw.core.cpc.Cpc;
import com.likya.tlossw.core.cpc.CpcTester;
import com.likya.tlossw.core.spc.helpers.LikyaDayKeeper;
import com.likya.tlossw.db.utils.DBUtils;
import com.likya.tlossw.exceptions.TlosFatalException;
import com.likya.tlossw.exceptions.TlosRecoverException;
import com.likya.tlossw.infobus.InfoBusManager;
import com.likya.tlossw.infobus.servers.MailServer;
import com.likya.tlossw.jmx.JMXServer;
import com.likya.tlossw.jmx.JMXTLSServer;
import com.likya.tlossw.model.engine.EngineeConstants;
import com.likya.tlossw.nagios.NagiosServer;
import com.likya.tlossw.perfmng.PerformanceManager;
import com.likya.tlossw.utils.ConfigLoader;
import com.likya.tlossw.utils.FileUtils;
import com.likya.tlossw.utils.InfoBus;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.PersistenceUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.ValidPlatforms;
import com.likya.tlossw.utils.date.DateUtils;
import com.likya.tlossw.utils.i18n.ResourceMapper;
import com.likya.tlossw.utils.i18n.ResourceReader;
import com.likya.tlossw.utils.validation.XMLValidations;
import com.likyateknoloji.xmlServerConfigTypes.ServerConfigDocument.ServerConfig;

public class TlosSpaceWideBase {

	private static final String version = "1.2 Beta";
	protected static final Logger logger = Logger.getLogger(TlosSpaceWide.class);
	private static SpaceWideRegistry spaceWideRegistry;

	private static boolean isPersistent = false;
	private static boolean isRecoverable = false;

	private boolean isMainPersistFileAbsent = false;

	protected void startExistDBSystem() {

		logger.info("");
		logger.info("##### Veritabani sistemi ile baglanti ######");
		logger.info(getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.REMOTEDB_STARTING_INFO));

		String driver = "org.exist.xmldb.DatabaseImpl";
		Collection collection = null;

		try {

			// initialize database driver
			Class<?> cl = Class.forName(driver);
			Database database = (Database) cl.newInstance();
			// database.setProperty("create-database", "true");
			DatabaseManager.registerDatabase(database);

			String dbType = getSpaceWideRegistry().getServerConfig().getDbparams().getType();
			String dbId = getSpaceWideRegistry().getServerConfig().getDbparams().getId();
			String dbIp = getSpaceWideRegistry().getServerConfig().getDbparams().getIpAddress();
			int dbPort = getSpaceWideRegistry().getServerConfig().getDbparams().getPortNumber();
			String dbXmlRpcPath = getSpaceWideRegistry().getServerConfig().getDbparams().getXmlrpcpath();
			String rootCollectionName = getSpaceWideRegistry().getServerConfig().getDbparams().getRootcollection();
			String userCollectionName = getSpaceWideRegistry().getServerConfig().getDbparams().getUsercollection();

			String dbUri = ParsingUtils.getDbUri(dbType, dbId, dbIp, dbPort, dbXmlRpcPath, rootCollectionName, userCollectionName);

			getSpaceWideRegistry().setDbUri(dbUri);

			String userName = getSpaceWideRegistry().getServerConfig().getDbparams().getUsername();
			String password = getSpaceWideRegistry().getServerConfig().getDbparams().getPassword();

			collection = DatabaseManager.getCollection(dbUri, userName, password);

			if (collection == null) {
				errprintln("Collection name : " + userCollectionName);
				errprintln("db connection uri : " + dbUri);
				errprintln("Collection is null, check your eXist DB if it is running !");
				errprintln(getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.TERMINATE_APPLICATION));
				System.exit(-1);
			}

			collection.setProperty(OutputKeys.INDENT, "no");
			getSpaceWideRegistry().setEXistColllection(collection);

			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			String xQueryModuleUrl = ParsingUtils.getXQueryModuleUrl(dbUri);
			getSpaceWideRegistry().setxQueryModuleUrl(xQueryModuleUrl);

			String xmlsUrl = ParsingUtils.getXmlsPath(dbUri);
			getSpaceWideRegistry().setXmlsUrl(xmlsUrl);

			// TlosConfigInfo tlosConfigInfo = DBUtils.getTlosConfigInfo();
			TlosConfigInfo tlosConfigInfo = DBUtils.getTlosConfig();
			
			if (tlosConfigInfo == null || !XMLValidations.validateWithLogs(logger, tlosConfigInfo)) {
				throw new TlosFatalException("DBUtils.getTlosConfig : getTlosConfig is null or tlosConfigInfo xml is damaged !");
			}

			getSpaceWideRegistry().setTlosSWConfigInfo(tlosConfigInfo);

		} catch (Exception e) {
			e.printStackTrace();
			errprintln(getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.TERMINATE_APPLICATION));
			System.out.println("Code : 1235");
			System.exit(-1);
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
			errprintln(getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.TERMINATE_APPLICATION));
			System.out.println("Code : 1236");
			System.exit(-1);
		}

		logger.info(getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.REMOTEDB_STARTED_INFO));
		logger.info("############################################");

		logger.info("");

	}

	protected void initApplication() {

		ResourceBundle resourceBaundle = null;
		try {
			resourceBaundle = ResourceReader.getResourceBundle();
			// getSpaceWideRegistry().setApplicationResources();
		} catch (RuntimeException runtimeException) {
			runtimeException.printStackTrace();
			errprintln(ResourceMapper.ERROR_INVALID_RESOURCE + ResourceReader.getResourcePath() + ResourceReader.getResourceName());
			System.exit(-1);
		}

		String appName = resourceBaundle.getString(ResourceMapper.APPLICATON_NAME_KEY);
		String versionName = resourceBaundle.getString(ResourceMapper.VERSION_KEY);
		String rights = resourceBaundle.getString(ResourceMapper.RIGHTS_KEY);

		logger.info("************************************************************");
		logger.info("*** Likya Bilgi Teknolojileri ve Iletisim Hiz. Ltd. Sti. ***");
		logger.info("***      " + appName + " " + versionName + " " + getVersion() + "       ***");
		logger.info("***           (c) 2013 " + rights + "                ***");
		logger.info("***                 Istanbul - Turkiye                   ***");
		logger.info("************************************************************");
		logger.info("");

		if (!ValidPlatforms.isOSValid()) {
			errprintln(ResourceMapper.ERROR_UNSUPPORTED_OS + " => " + System.getProperty("os.name"));
			logger.warn(ResourceMapper.ERROR_UNSUPPORTED_OS + " => " + System.getProperty("os.name"));
		}

		/**
		 * Read configuration properties
		 */

		// TlosConfigInfo tlosSWConfigInfo = ConfigLoader.readTlosConfig(resourceBaundle);

		ServerConfig serverConfig = ConfigLoader.readServerConfig(resourceBaundle);

		/**
		 * Redirect logging to appropriate destinations according to the config
		 * values
		 */
		if (serverConfig.getServerParams().getLogFile() != null) {
			RollingFileAppender appndr = (RollingFileAppender) Logger.getLogger("com.likya.tlos").getAppender("dosya");
			if (appndr != null) {
				appndr.setFile(serverConfig.getServerParams().getLogFile());
				appndr.activateOptions();
			}
		}

		getSpaceWideRegistry().setServerConfig(serverConfig);
		getSpaceWideRegistry().setApplicationResources(resourceBaundle);

		setPersistRecoverRules(serverConfig, resourceBaundle);

	}

	private void setPersistRecoverRules(ServerConfig serverConfig, ResourceBundle resourceBaundle) {

		boolean isPers = serverConfig.getServerParams().getIsPersistent().getValueBoolean();

		setPersistent(isPers);

		if (isPersistent()) {
			/**
			 * If is persisten == true, should chek if the temp folder is
			 * defined as jvm command line argument !
			 */
			String tempDir = EngineeConstants.tempDir;
			if (System.getProperty(tempDir) == null) {
				errprintln(ResourceMapper.TERMINATE_APPLICATION + " => \"" + tempDir + "\" should be defined as jvm command line argument !");
				errprintln("Eg : -D" + tempDir + "=\"../" + tempDir + "\"");
				System.exit(-1);
			}
		}

		/**
		 * Ref Flow DocId = 0098
		 */
		if (isPersistent() && PersistenceUtils.isMainPersistFilesExists()) {
			setRecoverable(true);
		} else {
			setRecoverable(false);
		}
	}

	protected void startJmxServer() {
		if (useJmx) {
			JMXServer.initialize();
		}
	}

	protected void startJmxTLSServer() {
		if (useJmx) {
			JMXTLSServer.initialize();
		}
	}

	protected void startMailSystem() {
		MailServer mailServer;
		try {
			mailServer = new MailServer(getSpaceWideRegistry().getTlosSWConfigInfo());
			getSpaceWideRegistry().setMailServer(mailServer);
			Thread mailServerService = new Thread(mailServer);
			mailServerService.setName(MailServer.class.getName());
			mailServerService.start();
		} catch (Exception e) {
			e.printStackTrace();
			errprintln(getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.TERMINATE_APPLICATION));
			System.out.println("Code : 1237");
			System.exit(-1);
		}
	}

	protected void shutDownMailServer() {

		if (getSpaceWideRegistry().getMailServer() == null) {
			return;
		}

		try {
			getSpaceWideRegistry().getMailServer().terminate(true);
			getSpaceWideRegistry().setMailServer(null);
		} catch (RuntimeException e) {
		}
	}

	protected void startInfoBusSystem() throws TlosRecoverException {

		logger.info("");
		logger.info("############# infoBus Manager  ##################");
		logger.info("Start the infoBus manager...");

		InfoBus infoBus = new InfoBusManager();
		Thread infoBusManagerService = new Thread(infoBus);

		infoBusManagerService.setName(InfoBus.class.getName());
		getSpaceWideRegistry().setInfoBus(infoBus);

		infoBusManagerService.start();

		logger.info("Started !");
		logger.info("Waiting for incoming messages ...");
		logger.info("#############################################");
		logger.info("");
	}

	protected void shutDownInfobusManager() {

		if (getSpaceWideRegistry().getInfoBus() == null) {
			return;
		}

		try {
			getSpaceWideRegistry().getInfoBus().terminate(true);
			getSpaceWideRegistry().setInfoBus(null);
		} catch (RuntimeException e) {
		}
	}

	public LikyaDayKeeper initDayKeeper() {

		if (getSpaceWideRegistry().getDayKeeperReference() == null) {
			LikyaDayKeeper myDayKeeper = LikyaDayKeeper.getInstance(getSpaceWideRegistry());
			getSpaceWideRegistry().setDayKeeperReference(myDayKeeper);
		}

		return getSpaceWideRegistry().getDayKeeperReference();
	}

	public void startDayKeeper() {

		/*
		 * Timer timer = new Timer(); DayKeeper myDayKeeper =
		 * DayKeeper.getInstance(getEnterpriseRegistery());
		 * //EnterpriseRegistery
		 * .getEnterpriseLogger().info("Before Scheduling DayKeeper : " +
		 * DateUtils.getDate(new Date(myDayKeeper.scheduledExecutionTime())));
		 * //
		 * System.out.println(DateUtils.getDate(myDayKeeper.getNextWorkTime()));
		 * timer.scheduleAtFixedRate(myDayKeeper, myDayKeeper.getNextWorkTime(),
		 * DayKeeper.getPeriod());
		 * //System.out.println(DateUtils.getDate(myDayKeeper
		 * .getNextWorkTime()));
		 * //EnterpriseRegistery.getEnterpriseLogger().info
		 * ("After Scheduling DayKeeper : " + DateUtils.getDate(new
		 * Date(myDayKeeper.scheduledExecutionTime())));
		 */

		LikyaDayKeeper myDayKeeper = initDayKeeper();
		Thread dayKeeperThread = new Thread(myDayKeeper);
		dayKeeperThread.setName("LikyaDayKeeper");

		getSpaceWideRegistry().getDayKeeperReference().setMyExecuter(dayKeeperThread);
		getSpaceWideRegistry().getDayKeeperReference().getMyExecuter().start();
		logger.info("   > Gundonumu Takipcisi baslatildi.");

	}

	protected void shutDownDayKeeper() {

		if (getSpaceWideRegistry().getDayKeeperReference() == null) {
			return;
		}

		try {

			getSpaceWideRegistry().getDayKeeperReference().setExecutePermission(false);

			synchronized (getSpaceWideRegistry().getDayKeeperReference().getMyExecuter()) {
				getSpaceWideRegistry().getDayKeeperReference().getMyExecuter().notifyAll();
			}

			while (getSpaceWideRegistry().getDayKeeperReference().getMyExecuter().isAlive()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		} catch (RuntimeException e) {
		}
		getSpaceWideRegistry().setDayKeeperReference(null);
	}

	protected void startNagiosServer() {

		logger.info("");
		logger.info("############# Nagios Server #################");

		NagiosServer nagiosServer = new NagiosServer(getSpaceWideRegistry(), System.currentTimeMillis());
		Thread starterThread = new Thread(nagiosServer);

		starterThread.setName("nagiosserver");
		nagiosServer.setMyExecuter(starterThread);

		starterThread.start();
		getSpaceWideRegistry().setNagiosServer(nagiosServer);

		logger.info("  Bilgi toplama periyodu " + getSpaceWideRegistry().getTlosSWConfigInfo().getMonitoringAgentParams().getCleanUpTimer() + "sn");
		logger.info("  Geriye dogru " + getSpaceWideRegistry().getTlosSWConfigInfo().getMonitoringAgentParams().getFrequency() + " saatlik bilgiyi tutuyor.");
		logger.info("#############################################");
		logger.info("");

	}

	protected void shutDownDBServer() {

		boolean isEmbedded = false;

		if (getSpaceWideRegistry().getServerConfig().getDbparams().getIpAddress() == null) {
			isEmbedded = true;
		}

		if (!isEmbedded) {
			return;
		}

		if (getSpaceWideRegistry().getEXistColllection() == null) {
			return;
		}

		// shut down the database
		try {
			DatabaseInstanceManager manager;
			try {
				manager = (DatabaseInstanceManager) getSpaceWideRegistry().getEXistColllection().getService("DatabaseInstanceManager", "1.0");
				manager.shutdown();
			} catch (XMLDBException e2) {
				e2.printStackTrace();
			}
		} catch (RuntimeException e) {
		}
		getSpaceWideRegistry().setEXistColllection(null);
	}

	public void startCpc() {

		logger.info("");
		logger.info("################ Merkezi Surec Yoneticisi ####################");

		if (getSpaceWideRegistry().getCpcReference() != null && getSpaceWideRegistry().getCpcReference().getExecuterThread().getState().equals(State.RUNNABLE)) {
			logger.info("Cpc is working, can not accept notify command !!!!!!");
			return;
		}

		if (!getSpaceWideRegistry().isUserSelectedRecover()) {

			logger.info("   > is listesi KDS nden sorgulaniyor ...");

			try {

				TlosProcessData tlosProcessData = DBUtils.getTlosDailyData(0, 0);

				if (tlosProcessData == null || !XMLValidations.validateWithLogs(logger, tlosProcessData)) {
					throw new TlosFatalException("DBUtils.getTlosDailyData : TlosProcessData is null or tlosProcessData xml is damaged !");
				}
				getSpaceWideRegistry().setTlosProcessData(tlosProcessData);

			} catch (TlosFatalException e) {
				if (getSpaceWideRegistry().getCpcReference() == null) {
					errprintln(getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.TERMINATE_APPLICATION));
					//System.out.println("Code : 1238 : Data.xml valide edilemedi veya null ");
					System.exit(-1);
				} else {
					errprintln("Gün dönümü sonrası çalışma listesi alınamadı !");
					return;
				}
			}

			int numOfScenarios = getSpaceWideRegistry().getTlosProcessData().getScenarioArray().length;
			JobList jobList = getSpaceWideRegistry().getTlosProcessData().getJobList();

			if (numOfScenarios == 0 && (jobList == null || jobList.getJobPropertiesArray().length == 0)) {
				logger.info("Bugünün is listesi herhangi bir job veya senaryo içermiyor.Liste bos !!!");
				return;
			} else {
				logger.info("   > is listesi KDS nden sorgulandi ve islenmeye hazir !");
			}
		}

		/** Central process manager is starting **/

		if (getSpaceWideRegistry().getCpcReference() == null) {

			logger.info("     >> CPC nin durumu : NULL ");
			logger.info("   > Daha onceden calisan herhangi bir Instance (verilmis isyuku) yok. ");
			logger.info("   > " + getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.CPC_STARTING_INFO));

			Cpc cpc = new Cpc(getSpaceWideRegistry());
			Thread cpcExecuterThread = new Thread(cpc);

			getSpaceWideRegistry().setCpcReference(cpc);
			getSpaceWideRegistry().getCpcReference().setExecuterThread(cpcExecuterThread);

			cpcExecuterThread.setDaemon(true);
			/**
			 * Otomatik olarak başlatılmayacak, ekrandan başlat emrinin gelmesi
			 * beklenecek.
			 */

			/** Central process manager is started ! **/

			cpcExecuterThread.start();

			logger.info("   > " + getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.CPC_STARTED_INFO));

		} else {

			logger.info("     >> CPC nin durumu : " + getSpaceWideRegistry().getCpcReference().getExecuterThread().getState());

			if (getSpaceWideRegistry().getCpcReference().getExecuterThread().getState().equals(State.WAITING)) {
				synchronized (getSpaceWideRegistry().getCpcReference().getExecuterThread()) {
					getSpaceWideRegistry().getCpcReference().getExecuterThread().notifyAll();
				}
			} else {
				logger.fatal("Expected Cpc state : " + State.WAITING + " Current Cpc State " + getSpaceWideRegistry().getCpcReference().getExecuterThread().getState());
				errprintln(getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.TERMINATE_APPLICATION));
				// System.exit(-1);
			}
		}

		logger.info(ResourceMapper.SECTION_DIVISON_KARE);
		logger.info("");

	}

	public void startCpcTester() {

		if (getSpaceWideRegistry().getCpcTesterReference() != null && getSpaceWideRegistry().getCpcTesterReference().getExecuterThread().getState().equals(State.RUNNABLE)) {
			logger.info("CpcTester is working, can not accept notify command !!!!!!");
			return;
		}

		if (getSpaceWideRegistry().getCpcReference() == null) {

			CpcTester cpcTester = new CpcTester(getSpaceWideRegistry());
			Thread cpcTesterExecuterThread = new Thread(cpcTester);

			getSpaceWideRegistry().setCpcTesterReference(cpcTester);
			getSpaceWideRegistry().getCpcTesterReference().setExecuterThread(cpcTesterExecuterThread);

			cpcTesterExecuterThread.setDaemon(true);

			cpcTesterExecuterThread.start();

		} 

		logger.info(ResourceMapper.SECTION_DIVISON_KARE);
		logger.info("");

	}

	public boolean loadGlobalstateDefinitions() throws TlosRecoverException {

		logger.info("");
		logger.info("### Çalışma Durum Makinesi Tanımları Yöneticisi ####");

		if (getSpaceWideRegistry().getGlobalStateDefinition() == null) {

			logger.info("Global durum tanımlarını yüklüyor...");

			String dbData = null;
			GlobalStateDefinition globalStateDefinition = DBUtils.getGlobalStateDefinitions();

			/**
			 * @author serkan Eğer persistence == true ise ve persisten edilen
			 *         dosya yok ise, sistem sanki persistent değilmiş gibi
			 *         davranıp veritabanından aldığı bilgiler ile yoluna devam
			 *         eder. GlobalState tanımları için bu şekilde bir davranış
			 *         görülmüştür.
			 */
			if (isRecoverable() && FileUtils.checkTempFile(PersistenceUtils.persistGlobalStatesFile, EngineeConstants.tempDir)) {
				dbData = globalStateDefinition.xmlText();
				if (PersistenceUtils.recoverGlobalStateDefinition()) {
					String persistenData = globalStateDefinition.xmlText();
					if (!persistenData.equals(dbData)) {
						throw new TlosRecoverException();
					}
				} else {
					throw new TlosRecoverException();
				}
			}

			getSpaceWideRegistry().setGlobalStateDefinition(globalStateDefinition);

			if (isPersistent()) {
				PersistenceUtils.persistGlobalStateDefinition(globalStateDefinition);
			}

			logger.info("Global durum tanımlarını yükledi !");
		}

		logger.info(ResourceMapper.SECTION_DIVISON_KARE);
		logger.info("");

		return true;

	}

	protected void shutDownCpcServer() {

		if (getSpaceWideRegistry().getCpcReference() == null) {
			return;
		}

		try {

			getSpaceWideRegistry().getCpcReference().setExecutionPermission(false);
			synchronized (getSpaceWideRegistry().getCpcReference().getExecuterThread()) {
				getSpaceWideRegistry().getCpcReference().getExecuterThread().notifyAll();
			}

			while (getSpaceWideRegistry().getCpcReference().getExecuterThread().isAlive()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		} catch (RuntimeException e) {
		}
		getSpaceWideRegistry().setCpcReference(null);
	}

	protected void initGunDonumuPeryodPassed() {

		long currentTime = Calendar.getInstance().getTimeInMillis();
		long diff = currentTime - getSpaceWideRegistry().getScenarioReadTime();

		if ((diff / (1000 * 60 * 60 * 24)) > getSpaceWideRegistry().getTlosSWConfigInfo().getSettings().getPeriod().getPeriodValue().longValue()) {
			getSpaceWideRegistry().setGunDonumuPeryodPassed(true);
		}

		return;
	}

	protected void initSolsticePassed() {

		Date currentDateTime = Calendar.getInstance().getTime();
		Calendar solsticeCalendar = DateUtils.normalizeDate(getSpaceWideRegistry().getTlosSWConfigInfo().getSettings().getSolstice().getTime());

		if (solsticeCalendar.getTime().before(currentDateTime)) {
			getSpaceWideRegistry().setSolsticePassed(true);
		}

		logger.info("");
		logger.info("#################### INFO ###################");

		logger.info("Su an " + currentDateTime + " dir.");
		logger.info("Gundonumu " + solsticeCalendar.getTime() + " dir.");

		logger.info(ResourceMapper.SECTION_DIVISON_KARE);
		logger.info("");

		return;
	}

	protected void startAgentManager() throws TlosRecoverException {

		logger.info("");
		logger.info("############# Agent Manager ################");
		logger.info(getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.HBLISTENER_STARTING_INFO));

		AgentManager agentManager = new AgentManager();
		Thread executerThread = new Thread(agentManager);

		getSpaceWideRegistry().setAgentManagerReference(agentManager);
		getSpaceWideRegistry().getAgentManagerReference().setExecuterThread(executerThread);

		executerThread.setName("agentManager");
		executerThread.start();

		logger.info(getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.HBLISTENER_STARTED_INFO));
		logger.info(ResourceMapper.SECTION_DIVISON_KARE);
		logger.info("");

	}

	protected void startPerformanceManager() {

		logger.info("");
		logger.info("############# Performance Manager #################");

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

		logger.info("  OK");
		logger.info(ResourceMapper.SECTION_DIVISON_KARE);
		logger.info("");

	}

	/*
	 * public void startWebSystem() {
	 * 
	 * try {
	 * Server server = new Server();
	 * getSpaceWideRegistry().setHttpServer(server);
	 * 
	 * Connector connector = new SelectChannelConnector();
	 * int portNum = TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo().getSettings().getHttpManagerProperties().getPortNumber();
	 * 
	 * String hostName = TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo().getSettings().getHttpManagerProperties().getIpAddress();
	 * 
	 * try {
	 * InetAddress addr = InetAddress.getLocalHost();
	 * println("************************************************************");
	 * println("***         Kullanici Arabirimi Parametreleri            ***");
	 * println("************************************************************");
	 * // Get IP Address
	 * print("Getting ip address : ");
	 * String ipAddr = addr.getHostAddress();
	 * print(ipAddr);
	 * println("");
	 * // Get hostname
	 * print("Getting hostname : ");
	 * hostName = addr.getHostName();
	 * println("" + hostName);
	 * } catch (UnknownHostException e) {
	 * errprintln(getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.TERMINATE_APPLICATION));
	 * System.exit(-1);
	 * }
	 * 
	 * connector.setHost(hostName);
	 * 
	 * if (portNum <= 0) {
	 * portNum = 8080;
	 * }
	 * 
	 * connector.setPort(Integer.getInteger("jetty.port", portNum).intValue());
	 * 
	 * println("Getting portNumber : " + connector.getPort());
	 * println("************************************************************");
	 * println();
	 * 
	 * server.setConnectors(new Connector[] { connector });
	 * WebAppContext webapp = new WebAppContext();
	 * webapp.setContextPath("/");
	 * 
	 * if (System.getProperty("tlos.webapp") == null) {
	 * errprintln("System property \"tlos.webapp\" is not defined ! " + getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.TERMINATE_APPLICATION));
	 * System.exit(-1);
	 * }
	 * webapp.setWar(System.getProperty("tlos.base") + "/webapp/" + System.getProperty("tlos.webapp"));
	 * webapp.setDefaultsDescriptor(System.getProperty("tlos.base") + "/webapp/etc/webdefault.xml");
	 * webapp.setAttribute("JmxUser", getSpaceWideRegistry().getJmxUser());
	 * 
	 * server.setHandler(webapp);
	 * server.start();
	 * 
	 * } catch (RuntimeException re) {
	 * re.printStackTrace();
	 * System.exit(-1);
	 * } catch (NoClassDefFoundError re) {
	 * re.printStackTrace();
	 * System.exit(-1);
	 * } catch (BindException be1) {
	 * be1.printStackTrace();
	 * errprintln(getSpaceWideRegistry().getApplicationResources().getString(ResourceMapper.TERMINATE_APPLICATION));
	 * System.exit(-1);
	 * } catch (NoSuchMethodError nsme) {
	 * nsme.printStackTrace();
	 * } catch (Exception e1) {
	 * e1.printStackTrace();
	 * System.exit(-1);
	 * }
	 * 
	 * }
	 */

	public void shutDownHttpServer() {

		if (getSpaceWideRegistry().getHttpServer() == null) {
			return;
		}

		System.out.println("****************************************");
		System.out.println("Handling http server shutdown process...");

		Server server = getSpaceWideRegistry().getHttpServer();
		try {
			/*
			 * server.setStopAtShutdown(true); server.setGracefulShutdown(0);
			 * System.out.print("Closing child handlers "); Handler[] handler =
			 * server.getChildHandlers(); for (int i = 0; i < handler.length;
			 * i++) { handler[i].stop(); System.out.print("."); }
			 * System.out.println(" Done!");
			 * 
			 * System.out.print("Closing handler "); WebAppContext webAppContext
			 * = (WebAppContext) server.getHandler();
			 * webAppContext.setShutdown(true); webAppContext.stop();
			 * System.out.println(" Done!");
			 * 
			 * Connector[] connectors = server.getConnectors();
			 * System.out.print("Closing connectors "); for (int i = 0; i <
			 * connectors.length; i++) { connectors[i].close();
			 * connectors[i].stop(); } System.out.println(" Done!");
			 * 
			 * System.out.print("Stopping server ");
			 */
			System.out.print("Waiting server to die...");
			/**
			 * Aşağıdaki süre, http server üzerinden gelen kapatma isteğinin
			 * cevabını http serverın hazmetmesi için gereken bir süre. Bu süre
			 * beklenmez ise, http server hatalar veriyor.
			 */
			Thread.sleep(500);
			server.stop();
			System.out.println(" Done!");
			// server.destroy();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		getSpaceWideRegistry().setHttpServer(null);
		System.out.println("Http Server terminated !");

	}

	protected boolean useJmx = true;

	public static String getVersion() {
		return version;
	}

	public static void println() {
		System.out.println();
	}

	public static void println(String message) {
		System.out.println(message);
	}

	public static void print(String message) {
		System.out.print(message);
	}

	public static void errprintln() {
		System.err.println();
	}

	public static void errprintln(String message) {
		System.err.println(message);
	}

	public static void errprint(String message) {
		System.err.print(message);
	}

	public static SpaceWideRegistry getSpaceWideRegistry() {
		return spaceWideRegistry;
	}

	public static void setSpaceWideRegistry(SpaceWideRegistry spaceWideRegistry) {
		TlosSpaceWideBase.spaceWideRegistry = spaceWideRegistry;
	}

	public boolean isMainPersistFileAbsent() {
		return isMainPersistFileAbsent;
	}

	public void setMainPersistFileAbsent(boolean isMainPersistFileAbsent) {
		this.isMainPersistFileAbsent = isMainPersistFileAbsent;
	}

	public static boolean isPersistent() {
		return isPersistent;
	}

	public static void setPersistent(boolean isPersistent) {
		TlosSpaceWideBase.isPersistent = isPersistent;
	}

	public static boolean isRecoverable() {
		return isRecoverable;
	}

	public static void setRecoverable(boolean isRecoverable) {
		TlosSpaceWideBase.isRecoverable = isRecoverable;
	}

}

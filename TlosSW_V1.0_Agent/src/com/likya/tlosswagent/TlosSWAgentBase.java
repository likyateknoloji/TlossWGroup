package com.likya.tlosswagent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamCorruptedException;
import java.lang.Thread.State;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.agentconfig.AgentConfigInfoDocument.AgentConfigInfo;
import com.likya.tlos.model.xmlbeans.agentconfig.ServerInfoDocument.ServerInfo;
import com.likya.tlos.model.xmlbeans.state.GlobalStateDefinitionDocument;
import com.likya.tlos.model.xmlbeans.state.GlobalStateDefinitionDocument.GlobalStateDefinition;
import com.likya.tlossw.model.jmx.JmxAgentUser;
import com.likya.tlossw.utils.FileUtils;
import com.likya.tlossw.utils.ValidPlatforms;
import com.likya.tlosswagent.core.cpc.Cpc;
import com.likya.tlosswagent.core.cpc.HeartBeater;
import com.likya.tlosswagent.jmx.JMXServer;
import com.likya.tlosswagent.outputqueue.OutputQueueManager;
import com.likya.tlosswagent.serverclient.TSWServerJmxClient;
import com.likya.tlosswagent.taskqueue.TaskQueueManager;
import com.likya.tlosswagent.utils.ConfigLoader;
import com.likya.tlosswagent.utils.PersistenceUtils;
import com.likya.tlosswagent.utils.SWAgentRegistry;
import com.likya.tlosswagent.utils.XmlUtils;
import com.likya.tlosswagent.utils.i18n.ResourceMapper;
import com.likya.tlosswagent.utils.i18n.ResourceReader;

public class TlosSWAgentBase {

	private static final String version = "1.0 Alpha";
	private static final Logger swAgentLogger = Logger.getLogger(TlosSWAgent.class);
	private static SWAgentRegistry swAgentRegistry = new SWAgentRegistry(swAgentLogger);

	public static final String tempDirPropertyName = "tlosAgent.tmpdir";

	protected boolean useJmx = true;

	public void startOutputQueueManager() {

		swAgentLogger.info("");
		swAgentLogger.info(" > Cikis Kuyrugu Yoneticisi baslatiliyor.");
		swAgentLogger.info("   > " + getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.OUTPUTQUEUEMANAGER_STARTING_INFO));

		OutputQueueManager outputQueueManager = new OutputQueueManager(getSwAgentRegistry());
		Thread outputQueueExecuterThread = new Thread(outputQueueManager);

		getSwAgentRegistry().setOutputQueManagerRef(outputQueueManager);
		getSwAgentRegistry().getOutputQueManagerRef().setExecuterThread(outputQueueExecuterThread);

		outputQueueExecuterThread.start();
		swAgentLogger.info("   > " + getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.OUTPUTQUEUEMANAGER_STARTED_INFO));
		swAgentLogger.info("   > " + ResourceMapper.SECTION_DIVISON_STAR);
	}

	public void startTaskQueueManager() {

		swAgentLogger.info("");
		swAgentLogger.info(" > Gorev Kuyrugu Yoneticisi baslatiliyor.");

		swAgentLogger.info("   > " + getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.TASKQUEUEMANAGER_STARTING_INFO));

		TaskQueueManager taskQueueManager = new TaskQueueManager(getSwAgentRegistry());
		Thread taskQueExecuterThread = new Thread(taskQueueManager);

		getSwAgentRegistry().setTaskQueManagerRef(taskQueueManager);
		getSwAgentRegistry().getTaskQueManagerRef().setExecuterThread(taskQueExecuterThread);

		taskQueExecuterThread.start();

		swAgentLogger.info("   > " + getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.TASKQUEUEMANAGER_STARTED_INFO));
		swAgentLogger.info("   > " + ResourceMapper.SECTION_DIVISON_STAR);

	}

	// TODO State.BLOCKED test edilmedi
	public void startCpc() {

		swAgentLogger.info("");
		swAgentLogger.info("################ Merkezi Surec Yoneticisi ####################");

		if (getSwAgentRegistry().getCpcReference() != null) {
			if (getSwAgentRegistry().getCpcReference().getExecuterThread().getState().equals(State.RUNNABLE)) {

				swAgentLogger.info("   > Cpc calisiyor. Biraz bekleyiniz !!!");
				// TODO burasi calisilacak !!!
				return;
			}
		}

		if (getSwAgentRegistry().getCpcReference() == null) {
			swAgentLogger.info("   > Daha onceden calisan herhangi bir verilmis isyuku yok.");
			swAgentLogger.info("   > " + getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.CPC_STARTING_INFO));

			Cpc cpc = new Cpc(getSwAgentRegistry());
			Thread cpcExecuterThread = new Thread(cpc);

			getSwAgentRegistry().setCpcReference(cpc);
			getSwAgentRegistry().getCpcReference().setExecuterThread(cpcExecuterThread);

			cpcExecuterThread.start();
			swAgentLogger.info("   > " + getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.CPC_STARTED_INFO));

		} else {
			if (getSwAgentRegistry().getCpcReference().getExecuterThread().getState().equals(State.WAITING)) {
				synchronized (getSwAgentRegistry().getCpcReference().getExecuterThread()) {
					getSwAgentRegistry().getCpcReference().getExecuterThread().notifyAll();
				}
			} else {
				swAgentLogger.fatal("Expected Cpc state : " + State.WAITING + " Current Cpc State " + getSwAgentRegistry().getCpcReference().getExecuterThread().getState());
				errprintln(getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.TERMINATE_APPLICATION));
			}
		}
		swAgentLogger.info(ResourceMapper.SECTION_DIVISON_STAR);
		swAgentLogger.info("");
	}

	protected void startHeartBeater() {
		if (SWAgentRegistry.TEST) {
			return;
		}
		swAgentLogger.info("");
		swAgentLogger.info("  > ############# HeartBeater Yoneticisi ################");
		swAgentLogger.info("");
		swAgentLogger.info("   > " + getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.HEARTBEATER_STARTING_INFO));

		HeartBeater heartBeater = new HeartBeater(getSwAgentRegistry());
		Thread heartBeaterExecuterThread = new Thread(heartBeater);

		getSwAgentRegistry().setHeartBeaterRef(heartBeater);
		getSwAgentRegistry().getHeartBeaterRef().setExecuterThread(heartBeaterExecuterThread);

		heartBeaterExecuterThread.start();
		swAgentLogger.info("   > " + getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.HEARTBEATER_STARTED_INFO));
		swAgentLogger.info("   > " + ResourceMapper.SECTION_DIVISON_STAR);
	}

	protected void initApplication() {

		try {
			getSwAgentRegistry().setApplicationResources(ResourceReader.getResourceBundle());
		} catch (RuntimeException runtimeException) {
			runtimeException.printStackTrace();
			errprintln(ResourceMapper.ERROR_INVALID_RESOURCE + ResourceReader.getResourcePath() + ResourceReader.getResourceName());
			errprintln(ResourceMapper.TERMINATE_APPLICATION);
			System.exit(-1);
		}

		String appName = getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.APPLICATON_NAME_KEY);
		String versionName = getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.VERSION_KEY);
		String rights = getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.RIGHTS_KEY);

		swAgentLogger.info("************************************************************");
		swAgentLogger.info("*** Likya Bilgi Teknolojileri ve Iletisim Hiz. Ltd. Sti. ***");
		swAgentLogger.info("***      " + appName + " " + versionName + " " + getVersion() + "       ***");
		swAgentLogger.info("***           (c) 2012 " + rights + "                ***");
		swAgentLogger.info("***                 Istanbul - Turkiye                   ***");
		swAgentLogger.info("************************************************************");
		swAgentLogger.info("");

		if (!ValidPlatforms.isOSValid()) {
			errprintln(getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.ERROR_UNSUPPORTED_OS) + " => " + System.getProperty("os.name"));
			swAgentLogger.error(getSwAgentRegistry().getApplicationResources().getString(ResourceMapper.ERROR_UNSUPPORTED_OS) + " => " + System.getProperty("os.name"));
		}

		/**
		 * Read configuration properties
		 */
		swAgentLogger.info(" > Tlos Agent Konfigurasyon dosyasi okunuyor." + "Property Name : " + ConfigLoader.configFilePropertyName);
		AgentConfigInfo agentConfigInfo = ConfigLoader.readTlosConfig();
		getSwAgentRegistry().setAgentConfigInfo(agentConfigInfo);
		swAgentLogger.info("  > Okundu.");

		boolean isAuthenticationPersistent = getSwAgentRegistry().getAgentConfigInfo().getSettings().getIsAuthenticationPersistent().getValueBoolean();
		boolean isPersistent = getSwAgentRegistry().getAgentConfigInfo().getSettings().getIsPersistent().getValueBoolean();

		String tempDir = System.getProperty(tempDirPropertyName);

		if ((isAuthenticationPersistent || isPersistent) && (tempDir == null || !FileUtils.checkFileExist(tempDir))) {
			errprintln("Sistem dosyalarýný saklamak için tanýmlanmýþ \"" + tempDir + "\" adlý dizin bulunamadý, böyle bir dizin yok ya da tanýmlý deðil !");
			errprintln("Sistem ayarlarýný deðiþtirin ya da dizin tanýmýný yapýn, Örnek : -D" + tempDirPropertyName + "=" + tempDir);
			errprintln("Agent Sonlandý !");
			System.exit(-1);
		}

	}

	protected boolean checkJmxUser() {

		boolean isAuthenticationPersistent = getSwAgentRegistry().getAgentConfigInfo().getSettings().getIsAuthenticationPersistent().getValueBoolean();

		if (isAuthenticationPersistent) {

			boolean recoverJmxUser = false;

			try {
				recoverJmxUser = PersistenceUtils.recoverJmxUser("jmxUser");
				if (recoverJmxUser) {
					swAgentLogger.info("  > JmxUser daha once onaylanmisti! Eger degistirdiyseniz cache i temizleyin.");
					return true;
				}
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			}

			swAgentLogger.info("  > Daha önce tanýmlý kullanýcý bilgileri sürücüde tutulan tampon bellekten okunamadý !");

		}

		boolean checkJmx = false;

		String jmxUserName = null;
		String jmxPassword = null;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		if (!SWAgentRegistry.TEST) {
			try {
				System.out.print("TlosSpaceWide jmx kullanýcý adýný giriniz : ");
				jmxUserName = br.readLine();
				System.out.println("TlosSpaceWide jmx kullanýcý þifresini giriniz : ");
				jmxPassword = br.readLine();
			} catch (IOException ioe) {
				swAgentLogger.error("IO error while trying to read jmxusername or jmxpassword!");
				return false;
			}
		} else {
			jmxUserName = "jmxuser";
			jmxPassword = "jmxpaswd";
		}

		SWAgent swAgent = XmlUtils.initSwAgent();

		if (swAgent == null) {
			return false;
		}

		swAgent.setJmxPassword(jmxPassword);
		swAgent.setJmxUser(jmxUserName);

		String swAgentXML = XmlUtils.getSWAgentXML(swAgent);

		swAgentLogger.info("  > AgentXML " + swAgentXML);
		JmxAgentUser jmxAgentUser = new JmxAgentUser(swAgentXML);

		int agentId = 0;

		if (!SWAgentRegistry.TEST) {

			ServerInfo serverInfo =  getSwAgentRegistry().getAgentConfigInfo().getSettings().getServerInfo();
			String resourceId = serverInfo.getResource().getStringValue();
			
			int portNumber = serverInfo.getPortNumber();

			swAgentLogger.info(resourceId + " için server tarafýnda þifre tanýmý kontrol ediliyor...");
			agentId = TSWServerJmxClient.checkJmxUser(jmxAgentUser, resourceId, portNumber);

			if (agentId > 0) {
				checkJmx = true;
			}

		} else {
			checkJmx = true;
		}

		if (checkJmx) {
			jmxAgentUser.setAgentId(agentId);
			getSwAgentRegistry().setJmxAgentUser(jmxAgentUser);
			if (isAuthenticationPersistent) {
				PersistenceUtils.persistJmxUser("jmxUser", jmxAgentUser);
			}
		}

		return checkJmx;
	}

	protected void startJmxServer() {
		if (useJmx) {
			JMXServer.initializeNormal();
		}
	}

	protected void startJmxTlsServer() {
		if (useJmx) {
			JMXServer.initializeTls();
		}
	}

	protected void retrieveTlosSWData() {

		swAgentLogger.info("");
		swAgentLogger.info("  > Global State tanimlari ");
		swAgentLogger.info("");

		if (PersistenceUtils.recoverGlobalStateDefinition("globalStateDef")) {
			swAgentLogger.info("  > GlobalState Tanimlari diskden recover edildi!");
			swAgentLogger.info(ResourceMapper.SECTION_DIVISON_STAR);
			return;
		}

		String serverIp = swAgentRegistry.getAgentConfigInfo().getSettings().getServerInfo().getResource().getStringValue();
		int serverJmxPort = swAgentRegistry.getAgentConfigInfo().getSettings().getServerInfo().getPortNumber();

		Object globalStates = null;

		if (SWAgentRegistry.TEST) {
			globalStates = FileUtils.readFile("/Users/serkan/Documents/workspace/TlosSWAgentTester/globalStates.xml").toString();
			if (globalStates == null) {
				errprintln("GlobalState /Users/serkan/Documents/workspace/TlosSWAgentTester/globalStates.xml'dan alinamadi!!!");
				errprintln("Agent Sonlandý !");
				System.exit(-1);			}
		} else {
			globalStates = TSWServerJmxClient.retrieveGlobalStates(getSwAgentRegistry().getJmxAgentUser(), serverIp, serverJmxPort);
		}

		if (globalStates == null) {
			errprintln("GlobalState TlosSW Server'dan alinamadi!!!");
			swAgentLogger.info("  > HATA : GlobalState TlosSW Server'dan alinamadi!!!!");
			swAgentLogger.error("  > GlobalState TlosSW Server'dan alinamadi!!!!");
			errprintln("Agent Sonlandý !");
			System.exit(-1);
		}

		GlobalStateDefinition globalStateDefinition = null;

		try {
			globalStateDefinition = GlobalStateDefinitionDocument.Factory.parse(globalStates.toString()).getGlobalStateDefinition();
		} catch (XmlException e) {
			e.printStackTrace();
		}

		if (globalStateDefinition == null) {
			errprintln("TlosSW Server'dan alinan GlobalState parse edilemedi !!!");
			swAgentLogger.info("  > HATA : TlosSW Server'dan alinan GlobalState parse edilemedi !!!");
			swAgentLogger.error("  > TlosSW Server'dan alinan GlobalState parse edilemedi !!!");
			errprintln("Agent Sonlandý !");
			System.exit(-1);
		} else {
			swAgentRegistry.setGlobalStateDefinition(globalStateDefinition);
			PersistenceUtils.persistGlobalStateDefinition("globalStateDef", globalStateDefinition);
			swAgentLogger.info("  > GlobalState Tanimlari TlosSW Server'dan alindi!");
		}
		swAgentLogger.info(ResourceMapper.SECTION_DIVISON_STAR);
	}

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

	public static SWAgentRegistry getSwAgentRegistry() {
		return swAgentRegistry;
	}

	public static void setSwAgentRegistry(SWAgentRegistry swAgentRegistry) {
		TlosSWAgentBase.swAgentRegistry = swAgentRegistry;
	}

}

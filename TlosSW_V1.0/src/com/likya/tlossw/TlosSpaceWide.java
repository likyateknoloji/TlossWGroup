package com.likya.tlossw;

import org.apache.log4j.Logger;

import com.likya.tlossw.core.cpc.model.AppState;
import com.likya.tlossw.exceptions.TlosRecoverException;
import com.likya.tlossw.jmx.JMXTLSServer;
import com.likya.tlossw.utils.SpaceWideRegistry;

public class TlosSpaceWide extends TlosSpaceWideBase {

	private static String assassinFlag = "";

	private static Logger logger = SpaceWideRegistry.getGlobalLogger();

	public TlosSpaceWide() {
		super();
	}

	public static void main(String[] args) {
		setSpaceWideRegistry(SpaceWideRegistry.getInstance());
		logger.info("********************** Start of main *********************");
		/*
		 * logger.trace("TRACE"); logger.debug("DEBUG"); logger.info("INFO");
		 * logger.warn("WARN"); logger.error("ERROR"); logger.fatal("FATAL");
		 */
		parseArguments(args);

		TlosSpaceWide tlosSpaceWide = new TlosSpaceWide();
		getSpaceWideRegistry().setSpaceWideReference(tlosSpaceWide);
		tlosSpaceWide.startAssassin();
		tlosSpaceWide.startTlosSpaceWide();
		logger.info("");
		logger.info("********************** End of main *********************");

	}

	private static void parseArguments(String[] args) {

		String USAGE_MSG = "Kullanım: TlosSpaceWide [-normalize] [-standby ]";

		String arg = "";
		int i = 0;

		while (i < args.length && args[i].startsWith("-")) {

			arg = args[i++];

			// use this type of check for "wordy" arguments
			if (arg.equals("-normalize")) {

			} else if (arg.equals("-standby")) {

			} else {
				System.err.println(USAGE_MSG);
				System.exit(0);
			}

		}

	}

	private Runnable assassin = new Runnable() {
		public void run() {
			synchronized (assassinFlag) {
				try {
					assassinFlag.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				shutdownTlosSpaceWide();
				System.exit(0);
			}
		}
	};

	public void startAssassin() {
		Thread t = new Thread(assassin);
		t.setName("assassin");
		t.setDaemon(true);
		t.start();
	}

	public void processQueueStarters() {

		try {
			if (!loadGlobalstateDefinitions()) {
				/**
				 * Close application reason : persisted states and db states
				 * does not match !!!!
				 */
				logger.info("   > Close application reason : persisted states and db states does not match !!!!");
				logger.error("   >  Close application reason : persisted states and db states does not match !!! (ERROR NO:2102)");
				System.exit(-1);
			}
		} catch (TlosRecoverException e1) {
			e1.printStackTrace();
			logger.info("   > Close application reason : cannot recover GlobalstateDefinitions !");
			logger.info("   > Clean tmp folder or set persistent to false and restart the application !");
			System.exit(-1);
		}

		/**
		 * isAgentEnabled değişkeni test çalıştırlması yanında
		 * lisans ile de ilgili olarak seçimlik olmalıdır.
		 * şimdilik el yordamı ile değişiklik yapılsın.
		 * 
		 * @author serkan taş
		 *         22.09.2012
		 */

		boolean isAgentEnabled = true;
		if (isAgentEnabled) {
			try {
				startAgentManager();
			} catch (Exception e) {
				logger.info("   > Close application reason : cannot recover AgentCache !");
				logger.info("   > Clean tmp folder or set persistent to false and restart the application !");
				System.exit(-1);
			}
		}

		/**
		 * isNagisoEnabled değişkeni test çalıştırılması yanında
		 * lisans ile de ilgili olarak seçimlik olmalıdır.
		 * şimdilik el yordamı ile değişiklik yapılsın.
		 * 
		 * @author serkan taş
		 *         22.09.2012
		 */

		boolean isNagisoEnabled = true;
		if (isNagisoEnabled) {
			/** Start Nagios Server */
			startNagiosServer();
		}

		/**
		 * isPerformanceManagerEnabled değişkeni test çalıştırlması yanında
		 * lisans ile de ilgili olarak seçimlik olmalıdır.
		 * şimdilik el yordamı ile değişiklik yapılsın.
		 * 
		 * @author serkan taş
		 *         22.09.2012
		 */

		boolean isPerformanceManagerEnabled = true;
		if (isPerformanceManagerEnabled) {
			/** Start Performance Manager */
			startPerformanceManager();
		}

		/** License Server */
		// startLicenseManager();

		
		/** Start alert servers (Log, Mail, SMS, ...) */
		boolean isEmailEnabled = getSpaceWideRegistry().getTlosSWConfigInfo().getSettings().getMailOptions().getUseMail().getValueBoolean();
		if (isEmailEnabled) {
			/** Mail Server */
			startMailSystem();
		}
		
		/**************************************************************************************/

		/** Start Info bus manager */
		try {
			startInfoBusSystem();
		} catch (TlosRecoverException e) {
			logger.error("   > Close application reason : cannot recover InfoBusQueue !");
			logger.error("   > Clean tmp folder or set persistent to false and restart the application !");
			System.exit(-1);
		}

		/**************************************************************************************/
	}

	private void startTlosSpaceWide() {

		changeApplicationState(AppState.INT_STARTING);
		
		/** Initialize startup conditions **/
		initApplication();

		/** Start database server */
		startExistDBSystem();

		/** Start access and user manager */

		/** Start Comm Interface */

		/** Start GUI Manager */

		/**
		 * programın normal olmayan bir şekilde sonlanması durumunda, son halini
		 * diskten yükleyip tekrar baslayabilir. ValueBoolean in default degeri
		 * #false#
		 */
		/**
		 * @author serkan gun donumunun üzerinden bir peryod zaman geçip
		 *         geçmediğini kontrol edelim Sistemde persistent = true ise
		 *         veya bütün persist dosyaları temp dizine önceden yazılmış ise
		 *         sistem daha önce çalışmış olduğundan recover edilmelidir bu
		 *         nedenle isFIRST_TIME = false olur.
		 * 
		 *         Ancak persisten = true ise ve en az bir tane persistence
		 *         dosyası mevcut değil ise bu durumda sistem gerçekten ilk defa
		 *         çalışmış olduğundan ve geçmişi olmadığından persistent
		 *         değilmiş gibi çalışır ve bu nedenle de FIRST_TIME = true
		 *         olarak kabul edilebilir. Aşağıdaki if kontrolüne girmez.
		 **/

		if (TlosSpaceWide.isRecoverable()) {
			getSpaceWideRegistry().setFIRST_TIME(false);
			initGunDonumuPeryodPassed();
		}

		/** gun donumunun gecip gecmedigini kontrol edelim **/
		initSolsticePassed();

		/**
		 * @author serkan recover etme durumunda kullanıcı onayı sonrası
		 *         çalışması gerekiyor.
		 * 
		 */
		if (!isRecoverable() || getSpaceWideRegistry().isSolsticePassed()) {
			processQueueStarters();
		}
		
		/** Start Jmx & Tls Servers */
		startJmxTlsServers();

		/** Start HTTP server */
		//startWebSystem();

		/** Baslatma Yoneticisi : Start Central Process Controller (CPC) */
		logger.info("");
		logger.info(" Baslatma kosullari kontrol ediliyor ...");
		logger.info(" Kosullar;");

		if (getSpaceWideRegistry().isFIRST_TIME()) {

			logger.info(" 1 - ilk kez calisiyor ...");

			if (!getSpaceWideRegistry().isSolsticePassed()) {

				/**
				 * Uygulama ilk defa çalıştığından ya da süreç bilgilerini
				 * saklamayacak şekilde çalıştırlıdığından, ve de henüz gün
				 * dönümü saati geçmediğinden uygulama beklemeye geçer ve gün
				 * dönümü saaati gelince çalışır.
				 */
				logger.info(" 2 - Gundonumu gecmedi.");
				logger.info("");
				logger.info("Aksiyon;");
				logger.info(" 1 - Gundonumu Takipcisini baslat .");

				startDayKeeper();
				logger.info("");
				logger.info("   CPC STATE : GUNDONUMUNU BEKLIYORUM ...");
				logger.info("");

				changeApplicationState(AppState.INT_RUNNING);
				
				getSpaceWideRegistry().setWaitConfirmOfGUI(false);

			} else {

				logger.info(" 2 - Gundonumu gecti.");
				logger.info("");
				logger.info("   KULLANICIYA SORUYORUM (HEMEN CALISTIR / BIR SONRAKI GUNDONUMUNU BEKLE) ");

				/**
				 * Ne yapılacağı ekran üzerinden kullanıcıya sorulacak :
				 * 
				 * 1. DURUM : Hemen işlemler çalıştırılmaya başlayabilir
				 * 
				 * 2. DURUM : Bir sonraki gün dönümüne uyması istenebilir.
				 */
				getSpaceWideRegistry().setWaitConfirmOfGUI(true);
				
				changeApplicationState(AppState.INT_SUSPENDED);

			}

		} else {

			logger.info(" 1 - ilk calismasi degil.");

			if (!getSpaceWideRegistry().isSolsticePassed()) {
				logger.info(" 2 - Gundonumu gecmedi.");
				logger.info("");
				logger.info("   RECOVERY YAPIYORUM (GUNDONUMU GECMEDI) kullaniciya soruyorum ...");
				logger.info("");

				/**
				 * Ne yapılacağı ekran üzerinden kullanıcıya sorulacak :
				 * 
				 * 1. DURUM süreci kaldığı yerden devam ettirebilir (RECOVER).
				 * 
				 * 2. DURUM Eski işleri (raporlayıp veya raporlamayıp) Süreci
				 * yeninde başlatabilir.
				 * 
				 * 3. DURUM Eski işleri (raporlayıp veya raporlamayıp) uygulama
				 * beklemeye geçer ve gün dönümü saaati gelince çalışır
				 */
				getSpaceWideRegistry().setWaitConfirmOfGUI(true);
				
				changeApplicationState(AppState.INT_SUSPENDED);


			} else if (getSpaceWideRegistry().isSolsticePassed()) {

				logger.info(" 2 - Gundonumu gecti. Son gun donumu okumasi uzerinden bir peryod gecti.");
				logger.info("");
				logger.info("Aksiyon;");
				logger.info(" 1 - Gundonumu Takipcisini baslat .");
				logger.info(" 2 - islerin guncel durumunu VT nina sakla.");

				/**
				 * Uygulama daha önce çalışmış olduğundan ve uygulamanın son gün
				 * dönümü okuduğu zaman üzerinden bir peryod zamanından fazla
				 * zaman geçtiğinden, eldeki islerin bilgileri VT'na raporlanıp,
				 * uygulama beklemeye geçer ve gün dönümü saaati gelince
				 * çalışır.
				 * 
				 * NOT : Burada gün dönümü geçmiş ise çalışma bir sonraki gün
				 * dönümüne göre oluşur, geçmemiş ise o gün içindeki gün
				 * dönümüne göre oluşur, kısaca en yakın gün dönümüne göre
				 * planlanır.
				 */

				logger.info("TODO Report recovered info to db");
				startDayKeeper();
				logger.info("");
				logger.info("   CPC STATE : RECOVERY YAPIYORUM ...");
				logger.info("");

				getSpaceWideRegistry().setWaitConfirmOfGUI(false);
				
				changeApplicationState(AppState.INT_RUNNING);

			} else {
				changeApplicationState(AppState.INT_NOT_STARTED);
				
				logger.fatal("Baslatma Yoneticisi icin beklenmeyen bir durum olustu... !! Kod: 0001");

				System.exit(-1);
			}

		}

		logger.info("#############################################");
		logger.info("Startign CpcTester...");
		startCpcTester();
		logger.info("Startign CpcTester...Done");
		logger.info("#############################################");

		logger.info("#############################################");
		logger.info("");

	}

	public static void stopSpacewide() {
		synchronized (assassinFlag) {
			assassinFlag.notifyAll();
		}
	}

	private void shutdownTlosSpaceWide() {
		/**
		 * stop HTTP server
		 */

		// shutDownHttpServer();

		/**
		 * Stop alert servers (Log, Mail, SMS, ...)
		 */

		/**
		 * Stop Mail Server
		 */
		shutDownMailServer();

		/**
		 * Stop Info bus manager
		 */
		shutDownInfobusManager();

		/**
		 * Stop database server, connection pool and manager
		 */

		shutDownDBServer();

		/**
		 * stop persistence manager
		 */

		/**
		 * stop performance manager
		 */

		/**
		 * stop event manager
		 */

		/**
		 * stop grid manager
		 */

		/**
		 * Stop access and user manager
		 */

		/**
		 * BEYİN Stop CPC (Central process controller)
		 */
		shutDownDayKeeper();

		shutDownCpcServer();

		/**
		 * Stop Comm Interface
		 */

		/*
		 * tlosCommInterface = new TlosCommInterface(this);
		 * 
		 * schedulerLogger.info("�leti�im arabirimi ba�lat�l�yor...");
		 * tlosCommInterface = new TlosCommInterface(this);
		 */

		/**
		 * Stop Remote Manager
		 */

		/*
		 * schedulerLogger.info("Web arabirimi ba�lat�l�yor ...");
		 * TlosWebConsole tlosWebConsole = new TlosWebConsole(this);
		 * schedulerLogger.info("Hostname : " + tlosWebConsole.getHostName() +
		 * " " + "Port : " + tlosWebConsole.getHttpPort());
		 * tlosWebConsole.initServer();
		 * schedulerLogger.info("Web arabirimi ba�lat�ld� !");
		 * 
		 * schedulerLogger.info("Y�netim Konsolu ba�lat�l�yor..."); try {
		 * managementConsoleHandler =
		 * ManagementConsole.initComm(tlosCommInterface,
		 * tlosParameters.getManagementPort(),
		 * tlosParameters.getManagementBufferSize()); new
		 * Thread(managementConsoleHandler).start(); } catch (SocketException e)
		 * { schedulerLogger.fatal("Y�netim Konsolu ba�lat�lamad� !");
		 * schedulerLogger.fatal("Program sona erdi."); e.printStackTrace();
		 * System.exit(-1); }
		 * schedulerLogger.info("Y�netim Konsolu ba�lat�ld� !");
		 */

		/**
		 * Stop GUI Manager
		 */

		/**
		 * Stop Jmx Server
		 */
		if (useJmx) {
			JMXTLSServer.disconnect();
		}

		println("TlosSpaceWide terminated successfully !");
	}

	public static void turnToPreviousState() {
		getSpaceWideRegistry().turnPreviousState();
		logger.error("   > Application state : " + AppState.getString(getSpaceWideRegistry().getCurrentState()));
	}
	
	public static void changeApplicationState(int newState) {
		getSpaceWideRegistry().setCurrentState(newState);
		logger.error("   > Application state : " + AppState.getString(newState));
	}
}

package com.likya.tlosswagent;

import com.likya.tlosswagent.jmx.JMXServer;
import com.likya.tlosswagent.serverclient.TSWServerJmxClient;
import com.likya.tlosswagent.utils.SWAgentRegistry;

public class TlosSWAgent extends TlosSWAgentBase {

	private static String assassinFlag = "";

	public TlosSWAgent() {
		super();
	}

	public static void main(String[] args) {

		SWAgentRegistry.getsWAgentLogger().info("********************** Start of main *********************");

		/**
		 * @author serkan TlosSWManager olmadan da swagent geliþtirme
		 *         yapýlabilmesi amacý ile eklendi
		 */

		if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equals("YES")) {
			SWAgentRegistry.TEST = true;
		}

		parseArguments(args);

		TlosSWAgent tlosSWAgent = new TlosSWAgent();
		getSwAgentRegistry().setSwAgentReference(tlosSWAgent);
		tlosSWAgent.startAssassin();
		tlosSWAgent.startTlosSpaceWideAgent();
		System.out.println();
		SWAgentRegistry.getsWAgentLogger().info("********************** End of main *********************");

	}

	private static void parseArguments(String[] args) {

		String USAGE_MSG = "Kullaným: TlosSWAgent [-normalize] [-standby ]";

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
				shutdownTlosSpaceWideAgent();
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

	private void checkJmxUserStatus() {

		SWAgentRegistry.getsWAgentLogger().info(" > JMX icin kullanici yetkileri kontrol ediliyor.");
		if (checkJmxUser()) {
			SWAgentRegistry.getsWAgentLogger().info("  > JmxUser Onaylandi!");
		} else {
			if(TSWServerJmxClient.tryReconnect) {
				SWAgentRegistry.getsWAgentLogger().info("  > JmxUser onaylanmadi veya DB Hatasi!");
				SWAgentRegistry.getsWAgentLogger().error("  > JmxUser onaylanmadi veya DB Hatasi!");
				errprintln("JmxUser onaylanmadi veya DB Hatasi!");
			}
			errprintln("Agent Sonlandý !");
			System.exit(-1);
		}
	}

	private void startTlosSpaceWideAgent() {

		/** Initialize startup conditions **/
		initApplication();

		/** Start Jmx Server */
		startJmxServer();

		startJmxTlsServer();

		checkJmxUserStatus();

		/** Retrieve necessary data from TlosSW Engine **/
		retrieveTlosSWData();

		/** Cikti Kuyrugu Yoneticisini baslat **/
		startOutputQueueManager();

		/** Gorev Kuyrugu Yoneticisini baslat **/
		startTaskQueueManager();

		/** Kalpatisini balat. **/
		startHeartBeater();

		/** Merkezi Yoneticiyi baslat **/
		startCpc();
	}

	public static void stopAgent() {
		synchronized (assassinFlag) {
			assassinFlag.notifyAll();
		}
	}

	private void shutdownTlosSpaceWideAgent() {

		if (getSwAgentRegistry().getTaskQueManagerRef() != null) {
			
			getSwAgentRegistry().getTaskQueManagerRef().setExecutionPermission(false);

			while (getSwAgentRegistry().getTaskQueManagerRef().getExecuterThread().isAlive()) {
				try {
					System.out.println("Waiting for TaskQueManager to die...");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			System.out.println("TaskQueManager is dead !");
		}

		/******/

		if (getSwAgentRegistry().getOutputQueManagerRef() != null) {
			
			getSwAgentRegistry().getOutputQueManagerRef().setExecutionPermission(false);

			while (getSwAgentRegistry().getOutputQueManagerRef().getExecuterThread().isAlive()) {
				try {
					System.out.println("Waiting for OutputQueManager to die...");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			System.out.println("OutputQueManager is dead !");

		}

		/******/

		if (useJmx) {

			TSWServerJmxClient.tryReconnect = false;

			TSWServerJmxClient.releaseJMXTLSConnectionForComm();
			TSWServerJmxClient.releaseJMXTLSConnectionForHeartBeat();
			JMXServer.disconnectNormal();
			JMXServer.disconnectTls();
		}

		/******/

		if (getSwAgentRegistry().getHeartBeaterRef() != null) {

			getSwAgentRegistry().getHeartBeaterRef().setExecutionPermission(false);

			while (getSwAgentRegistry().getHeartBeaterRef().getExecuterThread().isAlive()) {
				try {
					System.out.println("Waiting for HeartBeater to die...");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			System.out.println("HeartBeater is dead !");

		}
		/******/

		shutDownCpcServer();

		println("TlosSWAgent terminated successfully !");

	}

	protected void shutDownCpcServer() {

		if (getSwAgentRegistry().getCpcReference() == null) {
			return;
		}

		try {

			getSwAgentRegistry().getCpcReference().setExecutionPermission(false);

			while (getSwAgentRegistry().getCpcReference().getExecuterThread().isAlive()) {
				try {
					System.out.println("Waiting for Cpc to die...");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		} catch (RuntimeException e) {
		}
	}

}

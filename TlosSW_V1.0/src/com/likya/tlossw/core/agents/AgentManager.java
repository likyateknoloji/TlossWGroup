package com.likya.tlossw.core.agents;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.agent.AgentTypeDocument.AgentType;
import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.agent.SWAgentsDocument.SWAgents;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.agentclient.TSWAgentJmxClient;
import com.likya.tlossw.core.cpc.model.RunInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.core.spc.helpers.JobQueueOperations;
import com.likya.tlossw.core.spc.helpers.SWErrorOperations;
import com.likya.tlossw.db.utils.AgentDbUtils;
import com.likya.tlossw.db.utils.DBUtils;
import com.likya.tlossw.exceptions.TlosRecoverException;
import com.likya.tlossw.model.engine.EngineeConstants;
import com.likya.tlossw.model.jmx.JmxAgentUser;
import com.likya.tlossw.utils.FileUtils;
import com.likya.tlossw.utils.PersistenceUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.XmlUtils;

public class AgentManager implements Runnable {

	private boolean executionPermission = true;

	private boolean firstRun = true;

	HashMap<String, SWAgent> swAgentsCache = null;

	private SpaceWideRegistry spaceWideRegistry;

	transient private Thread executerThread;

	public AgentManager() throws TlosRecoverException {
		super();

		this.spaceWideRegistry = SpaceWideRegistry.getInstance();

		if (TlosSpaceWide.isRecoverable() && FileUtils.checkTempFile(PersistenceUtils.persistAgentCacheFile, EngineeConstants.tempDir)) {
			swAgentsCache = PersistenceUtils.recoverAgentCache();
			if (swAgentsCache == null) {
				throw new TlosRecoverException();
			}
		} else {
			initAgentList();
		}

		if (TlosSpaceWide.isPersistent()) {
			PersistenceUtils.persistAgentCache(getSwAgentsCache());
		}
	}

	@Override
	public void run() {

		Thread.currentThread().setName("AgentManager");

		while (isExecutionPermission()) {

			try {

				scanAgentsForAvailability();
				Thread.sleep(10000);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		// System.out.println("Exited AgentManager notified !");
		SpaceWideRegistry.getGlobalLogger().info("Exited AgentManager notified !");

	}

	protected void initAgentList() {

		SWAgents swAgents = null;

		SpaceWideRegistry.getGlobalLogger().info("Etmen listesini db'den alıyor...");
		swAgents = DBUtils.initAgentList();
		SpaceWideRegistry.getGlobalLogger().info("Agent listesini yükledi!");

		if (swAgents == null) {
			/**
			 * @author serkan TODO Yapılacak işler notuna bakın !!
			 */
			// SpaceWideRegistry.getSpaceWideLogger().info("Etmen listesi veri tabanından okunamadı ya da tanımlı etmen yok !!!");
			// SpaceWideRegistry.getSpaceWideLogger().info("TlosSW Etmensiz yapıda çalışacak !");
			System.exit(-1);
		} else {
			setSwAgentsCache(XmlUtils.generateSWAgentCache(swAgents));
		}

		for (int i = 0; i < swAgents.sizeOfSWAgentArray(); i++) {
			SpaceWideRegistry.getGlobalLogger().info(" Agent : " + swAgents.getSWAgentArray(i).getResource().getStringValue() + " |Tip : " + swAgents.getSWAgentArray(i).getAgentType() + " |NrpePort : " + swAgents.getSWAgentArray(i).getNrpePort() + " |JMXTlsPort : " + swAgents.getSWAgentArray(i).getJmxTlsPort() + " |JMXuser : " + swAgents.getSWAgentArray(i).getJmxUser());
		}

	}

	public synchronized void updateHeartBeatTime(JmxAgentUser jmxAgentUser) {

		SWAgent agent = XmlUtils.convertToSwAgent(jmxAgentUser.getSwAgentXML());
		SWAgent cacheAgent = getSwAgentCache(jmxAgentUser.getAgentId() + "");

		if (cacheAgent == null) {
			System.err.println("Agent bilgisi saklandığı yerde bulunamadı : AgentID : " + jmxAgentUser.getAgentId());
			System.err.println("Saklanan bilgiler : " + cacheAgent);
			System.err.println("Beklenmedik bir hata oluştu, server kapandı !");
			System.exit(-1);
		}

		// TODO
		// ilk ayaga kalkıs anında (agentların) burada hallet
		// lastheartbeattime = 0 ise bu ilk andir
		// soru agentlar çalışıyor konumda olabilirlermi
		// yeni ayağa kalktıklarında sıfır ayağa kalktık veya
		// dolu ayağa kalktık gibi bir şey geçseler!! recover olabilir

		if (!cacheAgent.getOutJmxAvailable()) {
			cacheAgent.setOutJmxAvailable(true);
			cacheAgent.setInJmxAvailable(true);
			if (!cacheAgent.getJmxAvailable()) {
				TSWAgentJmxClient.resetAgent(cacheAgent.getResource().getStringValue(), (int) cacheAgent.getJmxTlsPort(), XmlUtils.getJmxAgentUser(cacheAgent));
				// TODO sahin
				// Agent'da calisan isleri kill et kuyrukları bosalt
				// resetagent ustte bunu yapiyor fakat JmxAvailable(true) isini
				// geriye kendi
				// ben hazirim diye birsey donup yapsin
				cacheAgent.setJmxAvailable(true);
			}

			AgentDbUtils.updateAgentToAvailable(cacheAgent.getId());
		}

		cacheAgent.setLastHeartBeatTime(System.currentTimeMillis());

		Logger.getLogger(AgentManager.class).info("Agent ID = " + cacheAgent.getId() + " HEARTBEAT:" + agent.getResource().getStringValue() + "." + cacheAgent.getJmxTlsPort());

	}

	public boolean checkDestIfServer(int agentId) {
		boolean isServer = false;

		SWAgent swAgent = swAgentsCache.get(agentId + "");
		// if (swAgent.getAgentType().toString().toUpperCase().equals(AgentType.SERVER.toString().toUpperCase())) {
		if (swAgent.getAgentType().intValue() == AgentType.SERVER.intValue()) {
			isServer = true;
		}

		return isServer;
	}

	public int numberOfRunningJobs(int agentId) {

		int totNumberOfRunningJobs = 0;

		for (String runId : TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable().keySet()) {

			int numOfWorkingJobs = 0;

			RunInfoType runInfoType = TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable().get(runId);
			HashMap<String, SpcInfoType> spcLookupTable = runInfoType.getSpcLookupTable().getTable();

			for (String spcId : spcLookupTable.keySet()) {
				Spc spc = spcLookupTable.get(spcId).getSpcReferance();
				if(spc == null) {
					// No spc defined for this scenario, it is NOT a BUG !
					continue;
				}
				
				JobQueueOperations.getNumOfJobsByAgent(spc.getJobQueue(), agentId);
				totNumberOfRunningJobs = totNumberOfRunningJobs + numOfWorkingJobs;
			}
			Logger.getLogger(AgentManager.class).info("  > AgentId = " + agentId + " icin toplam calisan is sayisi = " + totNumberOfRunningJobs);
//			if(SpaceWideRegistry.isDebug) {
//				System.out.println("  > AgentId = " + agentId + " icin toplam calisan is sayisi = " + totNumberOfRunningJobs);
//			}
		}

		return totNumberOfRunningJobs;
	}

	private void scanAgentsForAvailability() {

		/*
		 * hata durumunda agent inJmxAvailable, outJmxAvailable degerlerinin
		 * false olmasi icin beklenecek sure (sn) default 15
		 */
		long timeForInOutUnavailability = TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo().getSettings().getAgentOptions().getInOutJmxDurationForUnavailability().getDurationValue();

		for (String agentId : getSwAgentsCache().keySet()) {
			SWAgent agent = getSwAgentCache(agentId);
			/**
			 * Agent a ulasilamama durumunda her bir agent icin ayri olarak
			 * belirlenen Agent.xml deki inJmxAvailable, outJmxAvailable
			 * degerlerinin false olmasi icin beklenecek sure (sn) default 900
			 */
			long timeForAgentUnavailability = agent.getDurationForUnavailability();

			long currentTime = System.currentTimeMillis();

			/* Agent ile son irtibatin kuruldugu zaman */
			long lastHeartBeatTime;

			if (firstRun) {
				agent.setLastHeartBeatTime(currentTime - (timeForInOutUnavailability * 1000 + 1));
			}
			lastHeartBeatTime = agent.getLastHeartBeatTime();

			/* Agent ile son temas kurulduktan sonra ne kadar zaman (sn) gectigi */
			long passedTimeInSecSinceLastHeartBeat = (currentTime - lastHeartBeatTime) / 1000;

			/* Agent unavailable oldugundan beri ne kadar zaman gecti. */
			long passedTimeInSecForTotalUnavailability = Math.max(agent.getJobTransferFailureTime() / 1000, passedTimeInSecSinceLastHeartBeat);

			Logger.getLogger(AgentManager.class).info("  > UserStopRequest = " + agent.getUserStopRequest());
			/*
			 * Agent ile uzun zamandir baglanti kurulamadi ise, DB de tekrarli
			 * guncelleme yapmaya gerek yok, kontrolleri pass gecebiliriz.
			 */
			if (timeForAgentUnavailability > passedTimeInSecForTotalUnavailability) {

				boolean availableLogic;

				if (agent.getAgentType().toString().toUpperCase().equals((String) "SERVER")) {
					availableLogic = true;
					passedTimeInSecSinceLastHeartBeat = 0;
				} else { // Agent
					availableLogic = false;

					/*
					 * Eger Agent ile son temas kurulduktan sonra bekleme suresi
					 * gecti ve agent icin outJMXAvailable = true ise
					 */
					if (agent.getOutJmxAvailable() && passedTimeInSecSinceLastHeartBeat >= timeForInOutUnavailability) {
						agent.setOutJmxAvailable(false);
						AgentDbUtils.updateAgentJmxValue(agent.getId(), false, "outJMX");
						SWErrorOperations.sendErrMsgForOutJmxAvailable(agent);
						Logger.getLogger(AgentManager.class).info("  > outJMX Error for Agent ID = " + agent.getId());
					}
					/*
					 * kullanici forced stop yapmissa Agent bizim icin yok
					 * gibidir.
					 */
					if (agent.getUserStopRequest().toString().toLowerCase().equals((String) "forced")) {

						agent.setJmxAvailable(false);
						AgentDbUtils.updateAgentJmxValue(agent.getId(), false, "JMX");

						// Agent in uzerindeki tum isleri fail'e cekmemiz
						// gerekiyor.
						AgentOperations.failJobsForAgent(agent.getId());

						// SWErrorOperations.sendErrMsgForJmxAvailable(agent);
						Logger.getLogger(AgentManager.class).info("  > User Requested forced STOP for Agent ID = " + agent.getId());

					}
					/*
					 * Agent dan bilgi alamiyor ve bilgi veremiyor isek
					 * AgentUnavailability suresinin gecmesini beklemek uzere
					 * bilgileri set edelim.
					 */
					if ((agent.getJmxAvailable() && !agent.getInJmxAvailable() && !agent.getOutJmxAvailable())) {
						agent.setJmxAvailable(false);
						AgentDbUtils.updateAgentJmxValue(agent.getId(), false, "JMX");

						// Agent in uzerindeki tum isleri fail e cekmemiz icin
						// AgentUnavailability suresinin gecmesi gerekiyor.

						SWErrorOperations.sendErrMsgForJmxAvailable(agent);
						Logger.getLogger(AgentManager.class).info("  > JMX Error for Agent ID = " + agent.getId());
					}
				}
				if (firstRun) {
					agent.setOutJmxAvailable(availableLogic);
					agent.setInJmxAvailable(availableLogic);
					agent.setJmxAvailable(availableLogic);
					AgentDbUtils.updateAgentJmxValue(agent.getId(), availableLogic, "outJMX");
					AgentDbUtils.updateAgentJmxValue(agent.getId(), availableLogic, "inJMX");
					AgentDbUtils.updateAgentJmxValue(agent.getId(), availableLogic, "JMX");
					Logger.getLogger(AgentManager.class).info("  > JMX OK");
				}
			} else { // Set edilen sure gecmis, Agent baska kosula bakmaksizin
				// devre disi yapilmali
				if (agent.getJmxAvailable() && !agent.getAgentType().toString().toUpperCase().equals((String) "SERVER")) {
					agent.setJmxAvailable(false);
					AgentDbUtils.updateAgentJmxValue(agent.getId(), false, "JMX");

					// Agent in uzerindeki tum isleri fail'e cekmemiz gerekiyor.
					AgentOperations.failJobsForAgent(agent.getId());

					SWErrorOperations.sendErrMsgForJmxAvailable(agent);

					Logger.getLogger(AgentManager.class).info("  > JMX Error for Agent ID = " + agent.getId());
				}
			}
		} /* for */
		firstRun = false;
	}

	public void setLastJobTransfer(String agentKey, boolean transfer, long failureTime) {
		long timeForInOutUnavailability = TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo().getSettings().getAgentOptions().getInOutJmxDurationForUnavailability().getDurationValue();

		if (!transfer) {// transfer basarisiz

			// bir oncekide basarisiz ise
			if (!getSwAgentCache(agentKey).getLastJobTransfer()) {

				long currentTime = System.currentTimeMillis();
				long lastJobTransferFailureTime = getSwAgentCache(agentKey).getJobTransferFailureTime();
				long diffInSeconds = (currentTime - lastJobTransferFailureTime) / 1000;

				if (diffInSeconds >= timeForInOutUnavailability) {
					// surede asildiysa artik is gonderilmemeli
					getSwAgentCache(agentKey).setInJmxAvailable(false);
					AgentDbUtils.updateAgentJmxValue(getSwAgentCache(agentKey).getId(), false, "inJMX");
					SWErrorOperations.sendErrMsgForInJmxAvailable(getSwAgentCache(agentKey));
				}
			} else {// bir onceki basarili ise
				getSwAgentCache(agentKey).setJobTransferFailureTime(failureTime);
			}
		}

		getSwAgentCache(agentKey).setLastJobTransfer(transfer);
	}

	public SWAgent getServer() {

		for (String agentId : getSwAgentsCache().keySet()) {
			SWAgent agent = getSwAgentCache(agentId);
			if (agent.getAgentType().equals(AgentType.SERVER)) {
				return agent;
			}
		}

		return null;
	}

	public SWAgent getSwAgentCache(int agentKey) {
		return swAgentsCache.get(agentKey + "");
	}

	public SWAgent getSwAgentCache(String agentKey) {
		return swAgentsCache.get(agentKey);
	}

	public boolean isExecutionPermission() {
		return executionPermission;
	}

	public void setExecutionPermission(boolean executionPermission) {
		this.executionPermission = executionPermission;
	}

	public HashMap<String, SWAgent> getSwAgentsCache() {
		return swAgentsCache;
	}

	public void setSwAgentsCache(HashMap<String, SWAgent> swAgentsCache) {
		this.swAgentsCache = swAgentsCache;
	}

	public SpaceWideRegistry getSpaceWideRegistry() {
		return spaceWideRegistry;
	}

	public void setSpaceWideRegistry(SpaceWideRegistry spaceWideRegistry) {
		this.spaceWideRegistry = spaceWideRegistry;
	}

	public Thread getExecuterThread() {
		return executerThread;
	}

	public void setExecuterThread(Thread executerThread) {
		this.executerThread = executerThread;
	}

}

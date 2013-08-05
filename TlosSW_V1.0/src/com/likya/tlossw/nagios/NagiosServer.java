package com.likya.tlossw.nagios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.agent.SWAgentsDocument.SWAgents;
import com.likya.tlossw.db.utils.AgentDbUtils;
import com.likya.tlossw.db.utils.NrpeDbUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.date.DateUtils;

public class NagiosServer implements Runnable {

	private boolean executePermission = true;

	private Thread myExecuter;
	private SpaceWideRegistry spaceWideRegistry;

	private SWAgents nrpeHostList = null;
	private ArrayList<NrpeCommand> nrpeCommanList = null;

	private final String[] winCommandList = { "alias_cpu", "alias_disk", "alias_mem" };

	private final String[] unixCommandList = { "check_moad", "check_disk", "check_mem" };

	private HashMap<String, NrpeCommander> nrpeCommanderHashMap = new HashMap<String, NrpeCommander>();

	private ExecutorService executor = null;

	private int messageCleanupPeriod;
	private long lastMessageCleanupTime;

	public NagiosServer(SpaceWideRegistry spaceWideRegistry, long lastMessageCleanupTime) {
		super();
		this.messageCleanupPeriod = spaceWideRegistry.getTlosSWConfigInfo().getMonitoringAgentParams().getFrequency().intValue();
		this.lastMessageCleanupTime = lastMessageCleanupTime;
		this.spaceWideRegistry = spaceWideRegistry;
		initialize();
	}

	private void initialize() {
		initHosts();
		initCommands();
		initNrpeCommanderHashMap();

		executor = Executors.newFixedThreadPool(nrpeHostList.getSWAgentArray().length);

		for (String id : nrpeCommanderHashMap.keySet()) {
			executor.execute(nrpeCommanderHashMap.get(id));
		}
	}

	public Thread getMyExecuter() {
		return myExecuter;
	}

	public void setMyExecuter(Thread myExecuter) {
		this.myExecuter = myExecuter;
	}

	@Override
	public void run() {

		while (executePermission) {

			if (isDbCleanupTimeArrived()) {
				cleanupNrpeMessages();
			}

			try {
				Thread.sleep(spaceWideRegistry.getTlosSWConfigInfo().getMonitoringAgentParams().getCleanUpTimer().intValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void initHosts() {
		nrpeHostList = AgentDbUtils.getResorces();
	}

	private void initCommands() {
		NrpeCommand nrpeCommand = null;
		nrpeCommanList = new ArrayList<NrpeCommand>();

		for (int i = 0; i < unixCommandList.length; i++) {
			nrpeCommand = new NrpeCommand();
			nrpeCommand.setId(i);
			nrpeCommand.setOsType(OsType.Unix);
			nrpeCommand.setCommand(unixCommandList[i]);
			nrpeCommanList.add(nrpeCommand);
		}

		for (int j = 0; j < winCommandList.length; j++) {
			nrpeCommand = new NrpeCommand();
			nrpeCommand.setId(j);
			nrpeCommand.setOsType(OsType.Windows);
			nrpeCommand.setCommand(winCommandList[j]);
			nrpeCommanList.add(nrpeCommand);
		}
	}

	private void initNrpeCommanderHashMap() {
		for (SWAgent agent : nrpeHostList.getSWAgentArray()) {
			NrpeCommander nrpeCommander = new NrpeCommander(nrpeCommanList, agent);
			nrpeCommanderHashMap.put(agent.getResource().getStringValue(), nrpeCommander);
		}
	}

	public void stopNrpeThread(String threadId) {
		nrpeCommanderHashMap.get(threadId).setNrpePermission(false);
	}

	public int getMessageCleanupPeriod() {
		return messageCleanupPeriod;
	}

	public long getLastMessageCleanupTime() {
		return lastMessageCleanupTime;
	}

	public boolean isDbCleanupTimeArrived() {

		if (System.currentTimeMillis() - lastMessageCleanupTime >= messageCleanupPeriod * 1000 * 60 * 60) {
			return true;
		}

		return false;
	}

	public void cleanupNrpeMessages() {
		NrpeDbUtils.deleteExpiredNrpeMessages(DateUtils.getServerW3CDateTime(), messageCleanupPeriod);
	}

	public HashMap<String, NrpeCommander> getNrpeCommanderHashMap() {
		return nrpeCommanderHashMap;
	}
}

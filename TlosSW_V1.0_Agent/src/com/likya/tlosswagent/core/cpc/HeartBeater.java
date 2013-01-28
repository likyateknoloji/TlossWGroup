package com.likya.tlosswagent.core.cpc;

import com.likya.tlosswagent.TlosSWAgent;
import com.likya.tlosswagent.serverclient.TSWServerJmxClient;
import com.likya.tlosswagent.utils.SWAgentRegistry;

public class HeartBeater implements Runnable {
	
	private boolean executionPermission = true;
	
	private SWAgentRegistry swAgentRegistry;

	transient private Thread executerThread;
	

	public HeartBeater(SWAgentRegistry swAgentRegistry) {
		super();
		this.swAgentRegistry = swAgentRegistry;
	}


	@Override
	public void run() {
		
		Thread.currentThread().setName("HeartBeater");
		
		while (isExecutionPermission()) {

			try {		
				pulse();
				Thread.sleep(1000);		
				System.out.print(".");
			} catch (Exception e) {System.out.println("HeartBeater Hata aldi. !!");
				e.printStackTrace();
			}

		}
		System.out.println("Exited HeartBeater notified !");

	}

	
	public void pulse() {
		String serverHost = TlosSWAgent.getSwAgentRegistry().getAgentConfigInfo().getSettings().getServerInfo().getResource().getStringValue();
		int serverJmxPort = TlosSWAgent.getSwAgentRegistry().getAgentConfigInfo().getSettings().getServerInfo().getPortNumber();

		TSWServerJmxClient.pulse(TlosSWAgent.getSwAgentRegistry().getJmxAgentUser(), serverHost, serverJmxPort);
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


	public void setSwAgentRegistry(SWAgentRegistry swAgentRegistry) {
		this.swAgentRegistry = swAgentRegistry;
	}


	public Thread getExecuterThread() {
		return executerThread;
	}


	public void setExecuterThread(Thread executerThread) {
		this.executerThread = executerThread;
	}
	

}

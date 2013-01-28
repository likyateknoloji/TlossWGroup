package com.likya.tlossw.perfmng;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.exceptions.TlosFatalException;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.core.agents.*;

/**
 * @author vista
 * 
 */
public class PerformanceManager extends PerformanceManagerBase {

	public PerformanceManager(SpaceWideRegistry spaceWideRegistry) throws TlosFatalException {

		super(spaceWideRegistry);

		SpaceWideRegistry.getGlobalLogger().info("PerformanceManager baþlatýlýyor !");

	}

	AgentManager agentManagerRef = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference();

	/*
	 * private void setUpForTest() {
	 * 
	 * schedulerLogger.info("Test için kuyruk yenileniyor !");
	 * 
	 * Calendar calendar = Calendar.getInstance();
	 * Iterator<Job> jobsIterator = jobQueue.values().iterator();
	 * 
	 * while (jobsIterator.hasNext()) {
	 * Job scheduledJob = jobsIterator.next();
	 * Date executeDate = scheduledJob.getJobProperties().getTime();
	 * calendar.setTime(executeDate);
	 * calendar.add(Calendar.DATE, -1);
	 * executeDate = calendar.getTime();
	 * schedulerLogger.debug(executeDate);
	 * scheduledJob.getJobProperties().setTime(executeDate);
	 * }
	 * 
	 * long totalMemory = Runtime.getRuntime().totalMemory();
	 * long freeMemory = Runtime.getRuntime().freeMemory();
	 * long memoryInUse = totalMemory - freeMemory;
	 * 
	 * schedulerLogger.info("Kullanýlan memory = " + memoryInUse);
	 * schedulerLogger.info("Yeniden baþlýyor !");
	 * return;
	 * }
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Thread.currentThread().setName("PerformanceManager_" + Thread.currentThread().getId());

		// EnterpriseRegistery.getEnterpriseLogger().debug("run : Starting main thread...");
		// EnterpriseRegistery.getEnterpriseLogger().info("Toplam Ýþ Sayýsý : " + getJobQueue().size());

		SWAgent server = agentManagerRef.getServer();

		// int agentId = TlosSpaceWide.getSpaceWideRegistry();
		// int numberOfRunningJobsByThisAgent = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference().numberOfRunningJobs(agentId);
		while (executionPermission) {

			if (checkThresholdOverflow(server.getIsPermitted(), server.getId())) {
				server.setIsPermitted(false);
			} else {
				server.setIsPermitted(true);
			}
			try {
				int bekle = (getSpaceWideRegistry().getTlosSWConfigInfo().getSettings().getTlosFrequency().getFrequency() <= 1) ? 2000 : getSpaceWideRegistry().getTlosSWConfigInfo().getSettings().getTlosFrequency().getFrequency() * 2000;
				//SpaceWideRegistry.getGlobalLogger().info(" > Performance Manager beklemede " + bekle);
				Thread.sleep(bekle);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Aþaðýdaki deðerler agent bazýnda tutulmalý, agent'in veri modeline eklenmeli, zira
	 * bir agent'ýn sýnýrlarýný aþtýðýný kendinin belirlemesi gerekir, fazla istek olduðunda ise reject etmesi
	 * gerekir.
	 * 
	 * Bu bilgi agentManeger den alýnmalý !!!!!!!!!
	 * 
	 * TODO 
	 * @author serkan taþ
	 *         01.10.2012
	 */
	
	public synchronized boolean checkThresholdOverflow(boolean isPermitted, int swagentid) {

		int lowerLimit = TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo().getPerformance().getThreshold().getLow();
		int higherLimit = TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo().getPerformance().getThreshold().getHigh();

		int numOfActiveJobs = agentManagerRef.numberOfRunningJobs(swagentid);

		if (numOfActiveJobs >= higherLimit) {
			if (isPermitted)
				SpaceWideRegistry.getGlobalLogger().info("Tlos Scheduler overloaded ! # of jobs working :" + numOfActiveJobs + " Waiting for lower limit : " + lowerLimit);
			isPermitted = false;
		} else if (numOfActiveJobs <= lowerLimit) {
			if (!isPermitted)
				SpaceWideRegistry.getGlobalLogger().info("Tlos Scheduler overload restored ! # of jobs  working :" + numOfActiveJobs);
			isPermitted = true;
		}

		return !isPermitted;
	}

}

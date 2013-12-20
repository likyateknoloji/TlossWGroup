package com.likya.tlosswagent.core.cpc;

import java.util.Calendar;
import java.util.Iterator;

import com.likya.tlossw.core.spc.helpers.SortType;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlosswagent.exceptions.TlosFatalException;
import com.likya.tlosswagent.taskqueue.TaskQueueManager;
import com.likya.tlosswagent.taskqueue.TaskQueueOperations;
import com.likya.tlosswagent.utils.SWAgentRegistry;

/**
 * @author vista
 * 
 */
public class Cpc extends CpcBase {

	public boolean isRecoverAction = false;

	private boolean isRegular = true;

	public Cpc(SWAgentRegistry swAgentRegistry) {
		super(swAgentRegistry);
		this.isRecoverAction = swAgentRegistry.getAgentConfigInfo().getSettings().getIsPersistent().getUse();
	}

	public void run() {

		Thread.currentThread().setName("Cpc");
		TaskQueueManager taskQueueManager = getSwAgentRegistry().getTaskQueManagerRef();

		while (isExecutionPermission()) {

			try {

				if (taskQueueManager.getIsIndexExpired()) {
					Iterator<SortType> taskQueueIndexIterator = taskQueueManager.createTaskQueueIndex().iterator();

					while (taskQueueIndexIterator.hasNext()) {

						SortType sortType = taskQueueIndexIterator.next();
						Object task = taskQueueManager.getTask(sortType.getJobId() + "");
						executeTask(task, sortType.getJobId() + "");

					}
				}

				boolean isPersistent = getSwAgentRegistry().getAgentConfigInfo().getSettings().getIsPersistent().getUse();
				try {
					if (isPersistent && taskQueueManager.getTaskInputQueue() != null && taskQueueManager.getTaskInputQueue().size() > 0 && !TaskQueueOperations.persistTaskInputQueue("taskQueue", taskQueueManager.getTaskInputQueue())) {
						getCpcLogger().error("Taskqueue persist error : Queue name : " + "taskQueue");
						throw new TlosFatalException();
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

				Thread.sleep(1000);

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (TlosFatalException tfe) {
				tfe.printStackTrace();
			}

		}

		System.out.println("Cpc is dead !");
		
		System.out.println("TlosSWAgent is successfully terminated !");
	}

	public boolean isRegular() {
		return isRegular;
	}

	public void setRegular(boolean isRegular) {
		this.isRegular = isRegular;
	}

	private void executeTask(Object task, String taskKey) throws InterruptedException {

		if (task instanceof Job) {
			
			Job job = (Job) task;
			
			if (job.getMyExecuter() == null) { // Bu is daha once baslatilmadi ise
				
				SWAgentRegistry.getsWAgentLogger().info("  > Baslatilan is " + job.getJobRuntimeProperties().toString());

				Thread starterThread = new Thread(job);
				starterThread.setName(taskKey);
				job.setMyExecuter(starterThread);
				job.getJobRuntimeProperties().setRealExecutionDate(Calendar.getInstance().getTime());
				SWAgentRegistry.getsWAgentLogger().info("Starting " + (job.getJobRuntimeProperties().getJobProperties().getManagement().getPeriodInfo() != null ? "PERIODIC" : "")+ " job !");
				job.getMyExecuter().start();
				// OutputQueueOperations.addLiveStateInfo(job.getJobRuntimeProperties().getJobProperties().getLiveStateInfos().getLiveStateInfoArray(0),taskKey);
			} else {
				// SWAgentRegistry.getsWAgentLogger().info("   > Zaten baslatilmis. Yeniden baslatmiyorum o yuzden." + job.getJobRuntimeProperties());
				SWAgentRegistry.getsWAgentLogger().info("   > re"); // reexecute anlaminda
			}
		
		} else {
			SWAgentRegistry.getsWAgentLogger().info("   > Su anda sadece Job lar agent larda islenebilir !!");
			SWAgentRegistry.getsWAgentLogger().warn("   > Su anda sadece Job lar agent larda islenebilir !!");
		}

		return;
	}

}

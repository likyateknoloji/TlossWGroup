package com.likya.tlosswagent.taskqueue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;

import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlosswagent.TlosSWAgent;
import com.likya.tlosswagent.utils.AgentStatusChangeInfoSender;
import com.likya.tlosswagent.utils.SWAgentRegistry;

public class TaskQueueOperations {

	public static boolean persistTaskInputQueue(String fileName, HashMap<String, Object> taskInputQueue) throws ClassNotFoundException {

		FileOutputStream fos = null;
		// FileOutputStream fosIdx = null;
		ObjectOutputStream out = null;

		if (taskInputQueue.size() == 0) {
			SWAgentRegistry.getsWAgentLogger().fatal("Ýþ kuyruðu boþ olmamalý !");
			SWAgentRegistry.getsWAgentLogger().fatal("Program sona erdi !");
			return false;
		}
		try {
			File fileTemp = new File(System.getProperty("tlosAgent.tmpdir") + "/" + fileName + ".temp"); //$NON-NLS-1$
			fos = new FileOutputStream(fileTemp);

			// fos = new FileOutputStream(System.getProperty("tlosAgent.tmpdir")
			// + "/" + fileName);

			if (fos != null) {
				out = new ObjectOutputStream(fos);
				if (out != null) {
					out.writeObject(taskInputQueue);
					out.close();
				}
			}

			// fosIdx = new
			// FileOutputStream(System.getProperty("tlosAgent.tmpdir") + "/" +
			// fileName + ".idx");
			// out = new ObjectOutputStream(fosIdx);
			// out.writeObject(taskQueueIndex);
			// out.close();

			File file = new File(System.getProperty("tlosAgent.tmpdir") + "/" + fileName);

			if (file.exists()) {
				file.delete();
			}

			fileTemp.renameTo(file);

		} catch (FileNotFoundException fnf) {
			SWAgentRegistry.getsWAgentLogger().info("taskInputQueue dosyasi bulunamadigindan giris Kuyrugu Yerine konamadi,  (NOT Recovered) !");
			return false;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		} catch (SecurityException se) {
			se.printStackTrace();
			return false;
		}
		return true;

	}

	@SuppressWarnings("unchecked")
	public static boolean recoverTaskInputQueue(String fileName, HashMap<String, Object> taskInputQueue) {

		FileInputStream fis = null;
		// FileInputStream fisIdx = null;
		ObjectInputStream in = null;

		try {

			SWAgentRegistry.getsWAgentLogger().info("Gorev Kuyrugu Yerine konuyor (Recovering) !");
			fis = new FileInputStream(System.getProperty("tlosAgent.tmpdir") + "/" + fileName);
			in = new ObjectInputStream(fis);

			Object input = in.readObject();

			taskInputQueue.putAll((HashMap<String, Object>) input);
			in.close();

			// fisIdx = new
			// FileInputStream(System.getProperty("tlosAgent.tmpdir") + "/" +
			// fileName + ".idx");
			// in = new ObjectInputStream(fisIdx);
			// input = in.readObject();
			// taskQueueIndex.addAll((ArrayList<SortType>) input);
			// in.close();

			resetTaskInputQueue(taskInputQueue);

		} catch (FileNotFoundException fnf) {
			SWAgentRegistry.getsWAgentLogger().info("Recover dosyasi bulunamadigindan Gorev Kuyrugu Yerine konamadi,  (NOT Recovered) !");
			return false;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		} catch (SecurityException se) {
			se.printStackTrace();
			return false;
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			return false;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		SWAgentRegistry.getsWAgentLogger().info("Gorev Kuyrugu Yerine kondu (Recovered) !");
		return true;
	}

	public static void resetTaskInputQueue(HashMap<String, Object> taskInputQueue) {
		Iterator<Object> tasksIterator = taskInputQueue.values().iterator();
		while (tasksIterator.hasNext()) {
			Object object = tasksIterator.next();

			if (object instanceof Job) {
				Job scheduledJob = (Job) object;
				scheduledJob.setGlobalRegistry(TlosSWAgent.getSwAgentRegistry());
				LiveStateInfo myLiveStateInfo = scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0);

				if (!(myLiveStateInfo.getStateName().equals(StateName.FINISHED) && myLiveStateInfo.getSubstateName().equals(SubstateName.COMPLETED) && !myLiveStateInfo.getStatusName().equals(StatusName.FAILED))) {
					myLiveStateInfo.setStateName(StateName.RUNNING);
					myLiveStateInfo.setSubstateName(SubstateName.ON_RESOURCE);
					myLiveStateInfo.setStatusName(StatusName.TIME_IN);
					// scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().setLiveStateInfoArray(0,myLiveStateInfo);
					scheduledJob.setGenericInfoSender(new AgentStatusChangeInfoSender());
					scheduledJob.sendStatusChangeInfo();
					// TODO OutputQueueOperations.addLiveStateInfo metodunu
					// uygulamaya calistim ama test etmek lazim..
				}
				// if(scheduledJob.getJobQueue() == null) {
				// /**
				// * jobQueue transient olduðunudun, serialize etmiyor
				// * Recover ederken, bu alan null geliyor. Bu nedenle null ise
				// yeninde okumak gerekiyor.
				// */
				// scheduledJob.setJobQueue(jobQueue);
				// }
				//
				// jobQueue.get(scheduledJob.getJobProperties().getKey()).getJobProperties().setStatus(JobProperties.READY);
			}
		}

		return;

	}

}

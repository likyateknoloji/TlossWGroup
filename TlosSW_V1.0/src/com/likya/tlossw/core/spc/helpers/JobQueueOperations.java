package com.likya.tlossw.core.spc.helpers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.utils.SpaceWideRegistry;

public class JobQueueOperations {

	/**
	 * Ýþ kuyruðunda bitmeyen bir iþ var mý yok mu ona bakýyor. Eðer yok ise,
	 * bütün iþler baþarý ile bitmiþ sayýlýyor, ve true deðeri dönüyor.
	 * 
	 * @param jobQueue
	 * @return true, false
	 */
	public static boolean isJobQueueOver(HashMap<String, Job> jobQueue) {

		if (jobQueue != null) {
			Iterator<Job> jobsIterator = jobQueue.values().iterator();
			while (jobsIterator.hasNext()) {
				Job scheduledJob = jobsIterator.next();
				//SpaceWideRegistry.getSpaceWideLogger().info("   > JobQueue element jobsIterator: " + jobsIterator);
				//SpaceWideRegistry.getSpaceWideLogger().info("   > JobQueue element scheduledJob: " + scheduledJob.getJobRuntimeProperties());
				try {
					if (scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos() != null) {
						if (!scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(StateName.FINISHED)) {
							return false;
						}
					} else {
						SpaceWideRegistry.getGlobalLogger().error("  > isJobQueueOver fonksiyonunda problem2 : " + scheduledJob.getJobRuntimeProperties().getJobProperties());
					}
				} catch (Exception e) {
					SpaceWideRegistry.getGlobalLogger().error("  > isJobQueueOver fonksiyonunda problem : " + scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos());
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	public static void dumpJobQueue(String spcID, HashMap<String, Job> jobQueue) {

		@SuppressWarnings("unused")
		String queueDumpInfo = " JOB QUEUE >> ";
		String queueDumpDebug = "";
		int allJobCounter = 0, finishedJobCounter = 0, runningJobCounter = 0;

		Iterator<Job> jobsIterator = jobQueue.values().iterator();
		// jobsIterator.next().getJobRuntimeProperties().getJobProperties().getJsName();
		while (jobsIterator.hasNext()) {
			Job scheduledJob = jobsIterator.next();
			// queueDump += "[" + scheduledJob.getCurrentStatus() + " : " +
			// DateUtils.getDate(scheduledJob.getJobProperties().getTime()) + "]
			// ";
			allJobCounter++;
			String currentStatus = scheduledJob.getJobInfo();
			if(scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName()!=null) {
			 if (scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(StateName.RUNNING)) {
				queueDumpInfo += '\n';
				queueDumpDebug += '\n';
				runningJobCounter++;
			 }
			 if (scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(StateName.FINISHED)) {
				finishedJobCounter++;
			 }
			}
			queueDumpInfo += "[" + currentStatus + "]";
			// queueDumpDebug += "[" + currentStatus + ":" +
			// DateUtils.getDate(DateUtils.castorToNative(scheduledJob.getJobRuntimeProperties().getJobProperties().getJobPlannedTime().getStartTime().getTime()))
			// + "]";

			// queueDumpDebug += "[" + currentStatus + ":" +
			// scheduledJob.getJobRuntimeProperties().getJobProperties().getJobPlannedTime().getStartTime().getTime().getTime()
			// + "]";
			queueDumpDebug += "[" + currentStatus + ":" + scheduledJob.getJobRuntimeProperties().getJobProperties().getTimeManagement().getJsPlannedTime().getStartTime().getTime() + "]";
			// SpaceWideRegistry.getSpaceWideLogger().debug(queueDumpDebug);
			// SpaceWideRegistry.getSpaceWideLogger().info(queueDumpDebug);

		}
		if (jobsIterator != null) {
			SpaceWideRegistry.getGlobalLogger().debug(queueDumpDebug);
			SpaceWideRegistry.getGlobalLogger().info(queueDumpDebug);
			// Logger.getLogger(SpcBase.class).info("     > "+ this.getJsName()
			// + " senaryosunda guncel is Sayisi : " + getJobQueue().size());
			SpaceWideRegistry.getGlobalLogger().info("     > " + spcID + " icin guncel is Sayisi (Fin : Run : All): (" + finishedJobCounter + " : " + runningJobCounter + " : " + allJobCounter + ")");
		}

		return;
	}

	/**
	 * Spc : Sub Process Controller a ait iþ listesinin diske yazýlmasý iþlevini
	 * görür
	 * 
	 * @param fileName
	 *            : Diskte tutlacak dosya ismi ki bu isim her Spc için farklý
	 *            olmalý
	 * @param jobQueue
	 *            : Spc'ye ait iþ listesi
	 * @return
	 */
	public static boolean persistJobQueue(String fileName, HashMap<String, Job> jobQueue, ArrayList<SortType> jobQueueIndex) {

		FileOutputStream fos = null;
		FileOutputStream fosIdx = null;
		ObjectOutputStream out = null;

		if (jobQueue.size() == 0) {
			SpaceWideRegistry.getGlobalLogger().fatal("is kuyrugu bos olmamali !");
			SpaceWideRegistry.getGlobalLogger().fatal("Program sona erdi !");
			return false;
		}
		try {
			fos = new FileOutputStream(System.getProperty("tlos.tmpdir") + "/" + fileName);

			out = new ObjectOutputStream(fos);
			out.writeObject(jobQueue);
			out.close();

			fosIdx = new FileOutputStream(System.getProperty("tlos.tmpdir") + "/" + fileName + ".idx");
			out = new ObjectOutputStream(fosIdx);
			out.writeObject(jobQueueIndex);
			out.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return true;

	}

	@SuppressWarnings("unchecked")
	public static boolean recoverJobQueue(String fileName, HashMap<String, Job> jobQueue, ArrayList<SortType> jobQueueIndex) {

		FileInputStream fis = null;
		FileInputStream fisIdx = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(System.getProperty("tlos.tmpdir") + "/" + fileName);
			in = new ObjectInputStream(fis);
			Object input = in.readObject();

			jobQueue.putAll((HashMap<String, Job>) input);
			in.close();

			fisIdx = new FileInputStream(System.getProperty("tlos.tmpdir") + "/" + fileName + ".idx");
			in = new ObjectInputStream(fisIdx);
			input = in.readObject();
			jobQueueIndex.addAll((ArrayList<SortType>) input);
			in.close();

			// TODO Serkan : ???????????????????????
			resetJobQueue(jobQueue);

		} catch (FileNotFoundException fnf) {
			return false;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static void resetJobQueue(HashMap<String, Job> jobQueue) {
		Iterator<Job> jobsIterator = jobQueue.values().iterator();
		while (jobsIterator.hasNext()) {
			Job scheduledJob = jobsIterator.next();
			scheduledJob.setGlobalRegistry(SpaceWideRegistry.getInstance());
			LiveStateInfo myLiveStateInfo = scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0);

			if (!(myLiveStateInfo.getStateName().equals(StateName.FINISHED) && myLiveStateInfo.getSubstateName().equals(SubstateName.COMPLETED) && !myLiveStateInfo.getStatusName().equals(StatusName.FAILED))) {
				myLiveStateInfo.setStateName(StateName.RUNNING);
				myLiveStateInfo.setSubstateName(SubstateName.ON_RESOURCE);
				myLiveStateInfo.setStatusName(StatusName.TIME_IN);
				scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().setLiveStateInfoArray(0, myLiveStateInfo);
				scheduledJob.sendStatusChangeInfo();
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

		return;

	}

	public static ArrayList<Job> getDependencyList(HashMap<String, Job> jobQueue, Object jobKey) {

		ArrayList<Job> jobList = new ArrayList<Job>();

		try {
			Iterator<Job> jobsIterator = jobQueue.values().iterator();
			while (jobsIterator.hasNext()) {
				Job scheduledJob = jobsIterator.next();
				DependencyList dependentJobList = scheduledJob.getJobRuntimeProperties().getJobProperties().getDependencyList();
				if (dependentJobList != null) {
					// ArrayList<Item>itemsDependent = new
					// ArrayList<Item>(Collections.list(dependentJobList.getenumerateItem()));
					List<Item> t = Arrays.asList(dependentJobList.getItemArray());
					ArrayList<Item> itemsDependent = new ArrayList<Item>(t);

					int indexOfJob = itemsDependent.indexOf(jobKey);
					if (indexOfJob > -1) {
						jobList.add(scheduledJob);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobList.size() == 0 ? null : jobList;

	}

	// public static void resetJobQueue(HashMap<String, Job> jobQueue) {
	// // TODO Serkan : ???????????????????????
	// // resetJobQueue(StateInfo.READY, jobQueue);
	// }
	/*
	 * public static String getSimpleFormattedJobProperties(HashMap<String, Job>
	 * jobQueue) {
	 * 
	 * StringBuilder sb = new StringBuilder();
	 * 
	 * Iterator<Job> jobsIterator = jobQueue.values().iterator();
	 * 
	 * while (jobsIterator.hasNext()) {
	 * 
	 * Job scheduledJob = jobsIterator.next();
	 * 
	 * sb.append("\nÝþlem adý:" + scheduledJob.getJobProperties().getKey() +
	 * " => "); sb.append("  [Çalýþtýrma Komutu:" +
	 * scheduledJob.getJobProperties().getJobCommand() + "]");
	 * sb.append("  [Log dizini:" +
	 * scheduledJob.getJobProperties().getLogFilePath() + "]");
	 * sb.append("  [Planlanan:" +
	 * scheduledJob.getJobProperties().getPreviousTime());
	 * sb.append("  [Gerçekleþen:" +
	 * scheduledJob.getJobProperties().getExecutionDate());
	 * sb.append("  [Bitiþ Zamaný:" +
	 * scheduledJob.getJobProperties().getCompletionDate());
	 * sb.append("  [Çalýþma Zamaný:" +
	 * DateUtils.getDate(scheduledJob.getJobProperties().getTime()) + "]");
	 * sb.append("  [Çalýþma Süresi:" +
	 * scheduledJob.getJobProperties().getWorkDuration());
	 * sb.append("  [Sonraki Plan:" +
	 * DateUtils.getDate(scheduledJob.getJobProperties().getTime()));
	 * sb.append("  [Çalýþma Peryodu:" + "Günlük");
	 * sb.append("  [Baðýmlýlýk Listesi:" +
	 * scheduledJob.getJobProperties().getJobDependencyList().toString() + "]");
	 * 
	 * }
	 * 
	 * return sb.toString(); }
	 * 
	 * public static String getHTMLFormattedJobProperties(HashMap<String, Job>
	 * jobQueue) { return getHTMLFormattedJobProperties(jobQueue, null); }
	 * 
	 * public static String getHTMLFormattedJobProperties(HashMap<String, Job>
	 * jobQueue, String localizedMessage) {
	 * 
	 * StringBuilder stringBuilder = new StringBuilder();
	 * 
	 * stringBuilder.append(
	 * "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"");
	 * stringBuilder.append("\"http://www.w3.org/TR/html4/loose.dtd\">");
	 * stringBuilder.append("<html>"); stringBuilder.append("<head>");
	 * stringBuilder.append(
	 * "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-9\">"
	 * ); stringBuilder.append("<title>Tlos Scheduler</title>");
	 * stringBuilder.append("</head>"); stringBuilder.append("<body>");
	 * 
	 * stringBuilder.append(
	 * "<TABLE border=\"0\" summary=\"Tlos Scheduler iþ bilgileri\" align=\"center\">"
	 * ); stringBuilder.append("<tr>"); stringBuilder.append("<td>");
	 * stringBuilder.append(
	 * "<img src=cid:likyajpg10976@likyateknoloji.com align=\"right\" width=100 height=35>"
	 * ); stringBuilder.append("<tr align=\"right\"><td>");
	 * stringBuilder.append("<a href=\"http://" +
	 * TlosServer.getTlosParameters().getHostName() + ":" +
	 * TlosServer.getTlosParameters().getHttpAccessPort() + "/");
	 * stringBuilder.append(
	 * "\" target=\"_blank\" title=\"Tlos Scheduler Web Arabirimi\">Tlos Scheduler Web Arabirimi</a>"
	 * ); stringBuilder.append("</td></tr>"); stringBuilder.append("</td>");
	 * stringBuilder.append("</tr>");
	 * 
	 * stringBuilder.append("<tr>");
	 * stringBuilder.append("<td align=\"center\">");
	 * stringBuilder.append("<h1>TLOS Scheduler</h1>");
	 * 
	 * if (localizedMessage == null) { //
	 * stringBuilder.append("Calismaya basladi !");
	 * stringBuilder.append("Çalýþmaya baþladý !"); } else {
	 * stringBuilder.append(localizedMessage); }
	 * 
	 * stringBuilder.append("</td>"); stringBuilder.append("</tr>");
	 * stringBuilder.append("<tr>"); stringBuilder.append("<td colspan=\"2\">");
	 * stringBuilder.append("<hr/>"); stringBuilder.append("</td>");
	 * stringBuilder.append("</tr>"); stringBuilder.append("<tr>");
	 * stringBuilder.append("<td colspan=\"2\">");
	 * 
	 * stringBuilder.append(
	 * "<TABLE border=\"1\" summary=\"Tlos Scheduler iþ listesi ve çalýþma bilgilieri\" align=\"center\">"
	 * ); stringBuilder.append(
	 * "<CAPTION><EM>Tlos Scheduler iþ listesi ve çalýþma bilgilieri</EM></CAPTION>"
	 * ); stringBuilder.append("<TR>"); stringBuilder.append("<TH>Ýþlem Adý");
	 * stringBuilder.append("<TH>Çalýþtýrma Komutu");
	 * stringBuilder.append("<TH>Log dizini");
	 * stringBuilder.append("<TH>Planlanan");
	 * stringBuilder.append("<TH>Gerçekleþen");
	 * stringBuilder.append("<TH>Bitiþ Zamaný");
	 * stringBuilder.append("<TH>Çalýþma Süresi");
	 * stringBuilder.append("<TH>Sonraki Plan");
	 * stringBuilder.append("<TH>Çalýþma Peryodu");
	 * stringBuilder.append("<TH>Baðýmlýlýk Listesi");
	 * 
	 * Iterator<Job> jobsIterator = jobQueue.values().iterator();
	 * 
	 * while (jobsIterator.hasNext()) {
	 * 
	 * Job scheduledJob = jobsIterator.next(); stringBuilder.append("<TR>");
	 * stringBuilder.append("<TD>" + scheduledJob.getJobProperties().getKey());
	 * stringBuilder.append("<TD>" +
	 * scheduledJob.getJobProperties().getJobCommand());
	 * stringBuilder.append("<TD>" +
	 * scheduledJob.getJobProperties().getLogFilePath());
	 * stringBuilder.append("<TD>" +
	 * scheduledJob.getJobProperties().getPreviousTime());
	 * stringBuilder.append("<TD>" +
	 * scheduledJob.getJobProperties().getExecutionDate());
	 * stringBuilder.append("<TD>" +
	 * scheduledJob.getJobProperties().getCompletionDate());
	 * stringBuilder.append("<TD>" +
	 * scheduledJob.getJobProperties().getWorkDuration());
	 * stringBuilder.append("<TD>" +
	 * DateUtils.getDate(scheduledJob.getJobProperties().getTime()));
	 * stringBuilder.append("<TD>" + "Günlük"); stringBuilder.append("<TD>" +
	 * scheduledJob.getJobProperties().getJobDependencyList().toString()); }
	 * 
	 * stringBuilder.append("</TABLE>"); stringBuilder.append("</td>");
	 * stringBuilder.append("</tr>"); stringBuilder.append("</TABLE>");
	 * stringBuilder.append(pageFooter()); stringBuilder.append("</body>");
	 * stringBuilder.append("</html>");
	 * 
	 * return stringBuilder.toString();
	 * 
	 * }
	 * 
	 * private static StringBuilder pageFooter() {
	 * 
	 * StringBuilder footerValue = new StringBuilder();
	 * 
	 * footerValue.append("<br/><br/>"); if (!TlosServer.isLicensed()) {
	 * footerValue.append(
	 * "<h4 align=\"center\">UYARI : Bu ürün lisanssýz kullanýlmaktadýr.</h4>");
	 * } else { // LicenseInfo licenseInfo = TlosServer.getLicenseInfo(); //
	 * footerValue.append("<h4 align=\"center\">Bu ürün " + //
	 * licenseInfo.getClientName() + " adýna lisanslýdýr. Müþteri No : " // +
	 * licenseInfo.getClientId() + "</h4>"); }
	 * 
	 * footerValue.append("<HR  size=\"1\" COLOR=\"black\" WIDTH=\"80%\">");
	 * footerValue.append("<h6 align=\"center\">" + TlosServer.getVersion() +
	 * "<br>"); footerValue.append(
	 * "Her Hakký saklýdýr (c) 2008 Likya Bilgi Teknolojileri ve Ýlet. Hiz. Ltd.<br>"
	 * ); footerValue.append("Kayýþdaðý, Ýstanbul, Türkiye<br>");
	 * footerValue.append(
	 * "<a href=\"http://www.likyateknoloji.com\" title=\"www.likyateknoloji.com\">www.likyateknoloji.com</a>"
	 * ); footerValue.append(
	 * "&nbsp;&nbsp;<a href=\"mailto:bilgi@likyateknoloji.com\">bilgi@likyateknoloji.com</a></h6>"
	 * );
	 * 
	 * return footerValue; }
	 */
}

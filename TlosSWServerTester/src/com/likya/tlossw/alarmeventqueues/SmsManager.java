package com.likya.tlossw.alarmeventqueues;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlossw.core.spc.helpers.SWErrorOperations;
import com.likya.tlossw.db.utils.DBUtils;
import com.likya.tlossw.db.utils.DssDbUtils;
import com.likya.tlossw.infobus.helper.TlosSWError;
import com.likya.tlossw.infobus.servers.MailServer;
import com.likya.tlossw.model.infobus.InfoType;
import com.likya.tlossw.model.infobus.JobAllInfo;
import com.likya.tlossw.model.infobus.JobInfo;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;

/**
 * infoQueue kuyrugunu yoneten yoneteci class. Bir thread olarak calisir. Job
 * calismasi ile ilgili herturlu bilginin DB ye yazilmasi, gerektiginde mail
 * atilmasi gibi islerden birinci dereceden sorumludur.
 * 
 * @author tlosSW Dev Team
 * @param
 * @return
 * @see
 * @exception .
 * @since v1.0
 * 
 */
public class SmsManager implements Runnable {

	private final int timeout = 1000;
	/**
	 * InfoBusManager in thread lerle kullanimi sirasinda senkronizasyonu
	 * saglamak icin kullanilir. False yapilir ise thread lerin infoQueue
	 * uzerinde islem yapmasina izin verilmez.
	 */
	private boolean executePermission = true;

	// private boolean valueSet = false;
	/**
	 * Sunucu ve agentlarin urettikleri butun durum, mesaj, hata gibi bilgilerin
	 * tutuldugu kuyruk yapisi.
	 */
	private ArrayList<InfoType> infoQueue = new ArrayList<InfoType>();

	private static final Logger logger = Logger.getLogger(SmsManager.class);

	private MailServer mailServer;
	// private SmsServer smsServer;
	// private HistoryServer historyServer;

	public SpaceWideRegistry spaceWideRegistry;

	public SmsManager(SpaceWideRegistry spaceWideRegistry) {
		this.spaceWideRegistry = spaceWideRegistry;
	}

	/**
	 * infoQueue kuyrugunun senkronize bir sekilde temizlenmesi icin kullanilir.
	 * 
	 * @author tlosSW Dev Team
	 * @param forcedTerminate
	 *            True or False
	 * @return nothing
	 * @see
	 * @exception .
	 * @since v1.0
	 * 
	 */
	public void terminate(boolean forcedTerminate) {
		synchronized (this) {
			if (forcedTerminate) {
				infoQueue.clear();
			}
			this.executePermission = false;
		}
	}

	public void run() {
		// while (executePermission || infoQueue.size() > 0) {
		// while (infoQueue.size() > 0) {
		while (executePermission || getQueueSize() > 0) {

			while (getQueueSize() > 0) { /* Kuyrukta bilgi oldugu surece calis */
				// InfoType infoType = (InfoType) infoQueue.get(0);
				InfoType infoType = (InfoType) getInfo(0);
				logger.info("  > InfoBus okunuyor.");
				try {
					/*
					 * infoQueue daki eleman ne ise ona gore islem yapilmasi
					 * gerekiyor.
					 */
					if (infoType instanceof JobAllInfo) {
						synchronized (this) {
							JobAllInfo jobAllInfo = (JobAllInfo) infoType;
							JobProperties jobProperties = jobAllInfo.getJobProperties();
							/*
							 * Burada DB ye statu yazmaya gerek kalmadi. Zaten
							 * infoBusManager surekli DB ye JobInfo olarak state
							 * leri yaziyor. Sonuc olurak job in gercek baslama
							 * bitis zamanlarinin
							 */

							DBUtils.updateJob(jobProperties, ParsingUtils.getJobXFullPath(jobAllInfo.getSpcNativeFullPath(), jobProperties.getID(), "" + jobProperties.getAgentId(), jobProperties.getLSIDateTime()));
							// DBUtils.updateJob(jobProperties,
							// ParsingUtils.getJobXPath(jobAllInfo.getSpcId()));
							logger.info("  > ");
							logger.info("  > DB guncellemesi. " + jobAllInfo.getJobProperties().getBaseJobInfos().getJsName() + " icin baslama bitis zamani ve butun state ler.");
							logger.info("  > " + jobAllInfo.getSpcNativeFullPath() + jobAllInfo.getJobProperties().getBaseJobInfos().getJsName());
							logger.info("  > " + jobProperties.getTimeManagement().getJsRealTime());
							logger.info("  > " + jobProperties.getBaseJobInfos().getJsName() + " " + jobProperties.getStateInfos().getLiveStateInfos());
						}
					} else if (infoType instanceof JobInfo) {
						synchronized (this) {
							JobInfo jobInfo = (JobInfo) infoType;
							LiveStateInfo liveStateInfo = jobInfo.getLiveLiveStateInfo();
							DBUtils.insertJobState(liveStateInfo, jobInfo.getTreePath());
							DssDbUtils.swFindAlarms(jobInfo.getJobID(), jobInfo.getUserID(), jobInfo.getAgentID(), liveStateInfo);

							logger.info("  > ");
							logger.info("  > DB guncellemesi. " + jobInfo.getJobID() + " icin state durumu.");
							logger.info("  > " + jobInfo.getTreePath());
							logger.info("  > " + liveStateInfo);
						}
					} else if (infoType instanceof TlosSWError) {
						synchronized (this) {
							TlosSWError tlosSWError = (TlosSWError) infoType;
							// SWErrorOperations.insertError((TlosSWError)
							// infoType);
							SWErrorOperations.insertError(tlosSWError);
							logger.info("InfoBusManager hata bilgilerini DB ye insert ediyor." + tlosSWError.getSwError());
						}
					}
					logger.info("  > islem tamam.");
					// infoQueue.remove(0);
					deleteInfo(0);
					logger.debug("InfoBusManager sleeping !");
					Thread.sleep(timeout);
				} catch (Exception e) {
					logger.error("InfoBusManager da problem !!!");
					logger.info("InfoBusManager da problem !!!");
					e.printStackTrace();
				}

			}
		}
	}

	public synchronized void addInfo(InfoType infoType) {
		infoQueue.add(infoType);
		/*
		 * if(valueSet) try { wait(); } catch(InterruptedException e) {
		 * System.out.println("InterruptedException caught"); }
		 * infoQueue.add(infoType); valueSet = true; notify();
		 */
	}

	public synchronized InfoType getInfo(int getInfo) {
		return infoQueue.get(getInfo);
	}

	public synchronized void deleteInfo(int deletedElementId) {
		infoQueue.remove(deletedElementId);
		/*
		 * if(!valueSet) try { wait(); } catch(InterruptedException e) {
		 * System.out.println("InterruptedException caught"); }
		 * infoQueue.remove(deletedElementId); valueSet = false; notify();
		 */
	}

	public synchronized int getQueueSize() {
		return infoQueue.size();
	}

	public void setMailServer(MailServer mailServer) {
		this.mailServer = mailServer;
	}

	public MailServer getMailServer() {
		return mailServer;
	}

	/*
	 * public void setSmsServer(SmsServer smsServer) { this.smsServer =
	 * smsServer; }
	 * 
	 * public void setHistoryServer(HistoryServer historyServer) {
	 * this.historyServer = historyServer; }
	 * 
	 * public SmsServer getSmsServer() { return smsServer; }
	 * 
	 * public HistoryServer getHistoryServer() { return historyServer; }
	 */
	/*
	 * if (infoType instanceof JobStart) { JobStart jobStart = (JobStart)
	 * infoType; Connection connection = null; try { MiniConnectionPoolManager
	 * miniConnectionPoolManager =
	 * enterpriseRegistery.getMiniConnectionPoolManager();
	 * if(miniConnectionPoolManager != null) { connection =
	 * enterpriseRegistery.getMiniConnectionPoolManager().getConnection(); }
	 * if(connection == null) { break; } Statement statement =
	 * connection.createStatement();
	 * statement.execute("insert into job_history (jobkey, date) values ('" +
	 * jobStart.getJobKey() + "', '" +
	 * DateUtils.getDate(Calendar.getInstance().getTime()) + "')");
	 * statement.close(); connection.close(); } catch (SQLException e) {
	 * if(connection != null) { try { connection.close(); } catch (SQLException
	 * e1) {} } e.printStackTrace(); } }
	 * 
	 * else if(infoType instanceof TlosSms) { TlosSms tlosSms = (TlosSms)
	 * infoType; this.smsServer.sendSms(tlosSms); } else if(infoType instanceof
	 * TlosHistory) { TlosHistory tlosHistory = (TlosHistory) infoType;
	 * this.historyServer.sendMail(tlosHistory); } else { // error
	 * !!!!!!!!!!!!!!!!!!!!!!!!! }
	 */
}

package com.likya.tlossw.infobus;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.alarm.WarnByDocument.WarnBy;
import com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.spc.helpers.SWErrorOperations;
import com.likya.tlossw.db.utils.DBUtils;
import com.likya.tlossw.db.utils.DssDbUtils;
import com.likya.tlossw.exceptions.TlosRecoverException;
import com.likya.tlossw.infobus.helper.TlosSWError;
import com.likya.tlossw.infobus.servers.MailServer;
import com.likya.tlossw.model.engine.EngineeConstants;
import com.likya.tlossw.model.infobus.InfoType;
import com.likya.tlossw.model.infobus.JobAllInfo;
import com.likya.tlossw.model.infobus.JobInfo;
import com.likya.tlossw.model.infobus.mail.SimpleMail;
import com.likya.tlossw.model.infobus.mail.TlosMail;
import com.likya.tlossw.utils.FileUtils;
import com.likya.tlossw.utils.InfoBus;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.PersistenceUtils;
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
public class InfoBusManager implements InfoBus, Runnable {

	private boolean executePermission = true;

	/**
	 * Sunucu ve agentlarin urettikleri butun durum, mesaj, hata gibi bilgilerin
	 * tutuldugu kuyruk yapisi.
	 */
	private ArrayList<InfoType> infoQueue = new ArrayList<InfoType>();

	private static final Logger logger = Logger.getLogger(InfoBusManager.class);

	private SpaceWideRegistry spaceWideRegistry = SpaceWideRegistry.getInstance();

	private MailServer mailServer;

	private final int timeout;

	private int processedRecordCount = 0;

	private final boolean debug;

	boolean isEmailEnabled = false;

	public InfoBusManager() throws TlosRecoverException {

		timeout = spaceWideRegistry.getTlosSWConfigInfo().getSettings().getInfoBusOptions().getPeriod().getPeriodValue().intValue();
		debug = spaceWideRegistry.getServerConfig().getServerParams().getDebugMode().getValueBoolean();

		isEmailEnabled = spaceWideRegistry.getTlosSWConfigInfo().getSettings().getMailOptions().getUseMail().getValueBoolean();

		mailServer = spaceWideRegistry.getMailServer();

		if (TlosSpaceWide.isRecoverable() && FileUtils.checkTempFile(PersistenceUtils.persistInfoQueueFile, EngineeConstants.tempDir)) {
			infoQueue = PersistenceUtils.recoverInfoQueue();
			if (infoQueue == null) {
				throw new TlosRecoverException();
			}
		}

	}

	/**
	 * infoQueue kuyrugunun sağlıklı bir sekilde temizlenmesi icin kullanilir.
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

		while (executePermission || getQueueSize() > 0) {

			if (getQueueSize() == 0) {
				try {
					Thread.sleep(timeout);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}

			/* Kuyrukta bilgi oldugu surece calis */
			while (getQueueSize() > 0) {

				InfoType infoType = (InfoType) getInfo(0);
				logger.info("  > QueueSize (" + getQueueSize() + ")");

				try {
					/*
					 * infoQueue daki eleman ne ise ona gore islem yapilmasi
					 * gerekiyor.
					 */
					if (infoType instanceof JobAllInfo) {

						JobAllInfo jobAllInfo = (JobAllInfo) infoType;
						JobProperties jobProperties = jobAllInfo.getJobProperties();

						if (jobAllInfo.isFirstJobInfo()) {
							/**
							 * Burasını bu şekilde yaptım ama hakan hoca ile konuşmak lazım.
							 * serkan
							 * TODO 
							 */
							DBUtils.updateFirstJob(jobProperties, ParsingUtils.getJobXPath(jobAllInfo.getSpcAbsolutePath()));
						} else {
							DBUtils.updateJob(jobProperties, ParsingUtils.getJobXFullPath(jobAllInfo.getSpcAbsolutePath(), jobProperties.getID(), "" + jobProperties.getAgentId(), jobProperties.getLSIDateTime()));
						}
						
						if (debug) {
							logger.info("  > ");
							logger.info("  > DB guncellemesi. " + jobAllInfo.getJobProperties().getBaseJobInfos().getJsName() + " icin baslama bitis zamani ve butun state ler.");
							logger.info("  > " + jobAllInfo.getSpcAbsolutePath() + jobAllInfo.getJobProperties().getBaseJobInfos().getJsName());
							logger.info("  > " + jobProperties.getTimeManagement().getJsRealTime());
							logger.info("  > " + jobProperties.getBaseJobInfos().getJsName() + " " + jobProperties.getStateInfos().getLiveStateInfos());
						}

					} else if (infoType instanceof JobInfo) {

						JobInfo jobInfo = (JobInfo) infoType;
						LiveStateInfo liveStateInfo = jobInfo.getLiveLiveStateInfo();
						DBUtils.insertJobState(liveStateInfo, jobInfo.getTreePath());
						Alarm alarm = DssDbUtils.swFindAlarms(jobInfo.getJobID(), jobInfo.getUserID(), jobInfo.getAgentID(), liveStateInfo);

						if (alarm.getSubscriber() != null) {
							handleAlarms(alarm);
						}

						if (debug) {
							logger.info("  > ");
							logger.info("  > DB guncellemesi. id: " + jobInfo.getJobID() + " name : " + jobInfo.getJobName() + " icin state durumu.");
							logger.info("  > " + jobInfo.getTreePath());
							logger.info("  > " + liveStateInfo);
						}

					} else if (infoType instanceof TlosSWError) {

						TlosSWError tlosSWError = (TlosSWError) infoType;
						SWErrorOperations.insertError(tlosSWError);
						logger.info("InfoBusManager hata bilgilerini DB ye insert ediyor." + tlosSWError.getSwError());

					} else if (infoType instanceof TlosMail) {
						addMail(infoType);
					}

					if (debug)
						logger.info("  > islem tamam.");

					deleteInfo(0);
					processedRecordCount++;
					logger.info("  > processedRecordCount (" + processedRecordCount + ")");

					if (TlosSpaceWide.isPersistent()) {
						PersistenceUtils.persistInfoQueue(infoQueue);
					}

					if (debug)
						logger.debug(timeout + "ms sleeping !");
					// Thread.sleep(timeout);

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
		if(infoQueue.size() > 0) infoQueue.remove(deletedElementId);
		/*
		 * if(!valueSet) try { wait(); } catch(InterruptedException e) {
		 * System.out.println("InterruptedException caught"); }
		 * infoQueue.remove(deletedElementId); valueSet = false; notify();
		 */
	}

	public synchronized int getQueueSize() {
		return infoQueue.size();
	}

	private boolean handleAlarms(Alarm alarm) throws Exception {

		String roleN = null;
		int personN = 0;
		if (alarm.getSubscriber().getRole() != null) {
			roleN = alarm.getSubscriber().getRole().toString();
		} else if (alarm.getSubscriber().getPerson() != null) {
			personN = alarm.getSubscriber().getPerson().getId().intValue();
		} else {
			return false;
		}

		Person person = DBUtils.getSubscribers(personN, roleN);

		if (person.getEmailList() == null || person.getEmailList().getEmailArray().length <= 0) {

			logger.info("  > Alarm Yonetimi. " + alarm.getSubscriber().getPerson().getId().intValue() + " nolu ID ye sahip kullanici bilgilerinde sorun var.");
			logger.info("  > Alarm Yonetimi. Bu nedenle e-posta alarmi gonderilemedi !!");

			return false;

		}

		String[] emailArray = person.getEmailList().getEmailArray();

		ArrayList<String> distributionList = new ArrayList<String>();

		Collections.addAll(distributionList, emailArray);

		boolean isEmailAlarm = false;
		boolean isSmsAlarm = false;
		boolean isSmtpAlarm = false;
		boolean isGuiAlarm = false;

		WarnBy[] warnBies = alarm.getSubscriber().getAlarmChannelTypes().getWarnByArray();

		for (int i = 0; i < warnBies.length; i++) {
			if (warnBies[i].getId().intValue() == 1)
				isEmailAlarm = true;
			if (warnBies[i].getId().intValue() == 2)
				isSmsAlarm = true;
			if (warnBies[i].getId().intValue() == 3)
				isGuiAlarm = true;
		}

		if (isEmailAlarm) {
			SimpleMail simpleMail = new SimpleMail("Alarm Id = " + alarm.getAlarmId(), "Merhaba, \n alarm var. Id = " + alarm.getAlarmId(), distributionList);
			addMail(simpleMail);
		}

		if (isSmsAlarm) {
			// TODO Sms Server
		}
		if (isSmtpAlarm) {
			// TODO Smtp Server
		}
		if (isGuiAlarm) {
			// TODO Gui
		}

		return true;
	}

	private void addMail(InfoType infoType) {
		if (isEmailEnabled) {
			mailServer.sendMail((TlosMail) infoType);
		}
	}
}

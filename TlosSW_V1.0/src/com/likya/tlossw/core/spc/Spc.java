package com.likya.tlossw.core.spc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import net.java.dev.eval.Expression;

import org.apache.commons.collections.iterators.ArrayIterator;

import com.likya.tlos.model.xmlbeans.agent.RxMessageDocument.RxMessage;
import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.common.JobCommandTypeDocument.JobCommandType;
import com.likya.tlos.model.xmlbeans.common.JobTypeDefDocument.JobTypeDef;
import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.JsRealTimeDocument.JsRealTime;
import com.likya.tlos.model.xmlbeans.data.StopTimeDocument.StopTime;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.dbjob.DbJobDefinitionDocument.DbJobDefinition;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpAdapterPropertiesDocument.FtpAdapterProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfosDocument.LiveStateInfos;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.agentclient.TSWAgentJmxClient;
import com.likya.tlossw.core.agents.AgentManager;
import com.likya.tlossw.core.cpc.Cpc;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.dss.DssVisionaire;
import com.likya.tlossw.core.spc.helpers.InstanceMapHelper;
import com.likya.tlossw.core.spc.helpers.JobQueueOperations;
import com.likya.tlossw.core.spc.helpers.SortType;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.db.utils.DBUtils;
import com.likya.tlossw.exceptions.TlosException;
import com.likya.tlossw.exceptions.TlosFatalException;
import com.likya.tlossw.exceptions.TransformCodeCreateException;
import com.likya.tlossw.exceptions.UnresolvedDependencyException;
import com.likya.tlossw.infobus.helper.ScenarioMessageFactory;
import com.likya.tlossw.jmx.beans.RemoteDBOperator;
import com.likya.tlossw.model.jmx.JmxAgentUser;
import com.likya.tlossw.transform.InputParameterPassing;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.TypeUtils;
import com.likya.tlossw.utils.XmlUtils;
import com.likya.tlossw.utils.date.DateUtils;
import com.likya.tlossw.utils.xml.ApplyXslt;

/**
 * @author vista
 * 
 */
public class Spc extends SpcBase {

	private static final long serialVersionUID = 4949177188797710064L;

	private boolean isRecovered = false;

	public Spc(String spcId, SpaceWideRegistry spaceWideRegistry, ArrayList<JobRuntimeProperties> taskList) throws TlosFatalException {
		this(spcId, spaceWideRegistry, taskList, false);
	}

	public Spc(String spcId, SpaceWideRegistry spaceWideRegistry, ArrayList<JobRuntimeProperties> taskList, boolean isRecoverAction) throws TlosFatalException {

		super(spcId, spaceWideRegistry, taskList);

		if (isRecoverAction) {
			getGlobalLogger().info("   > " + spcId + " recover islemi yapildi. ");
			if (!JobQueueOperations.recoverJobQueue(spcId, getJobQueue(), getJobQueueIndex())) {
				// TODO Recover edemezse ne yapilacagi konusunda bir karar vermek gerekir.
				getGlobalLogger().error(" ONEMLI : " + spcId + " recover edilemedi. Hata Kodu : 98087 ");
				getGlobalLogger().info(" ONEMLI : " + spcId + " recover edilemedi. Hata Kodu : 98087 ");
				System.exit(-1);
			}
			isRecovered = true;
		}

	}

	public void run() {

		Thread.currentThread().setName("Spc_" + getSpcId());

		getMyLogger().info("     > " + getBaseScenarioInfos().getJsName() + " icin ana thread baslatiliyor. Toplam is Sayisi : " + getJobQueue().size());

		/**
		 * InfoBus null ise kritik bir hata vardýr, muhtemelen yazýlýmda bug vardýr. Koþulsuz olarak uygulama kapanmalýdýr.
		 * 
		 * @author serkan taþ 19.09.2012
		 */
		if (getSpaceWideRegistry().getInfoBus() != null) {
			getSpaceWideRegistry().getInfoBus().addInfo(ScenarioMessageFactory.generateScenarioStart(getSpcId(), getJobQueue().size()));
			getMyLogger().info("     > " + this.getBaseScenarioInfos().getJsName() + " icin islerin baslatildigi bilgisi InfoBusManager a iletildi.");
		} else {
			getMyLogger().info("     > " + this.getBaseScenarioInfos().getJsName() + " senaryosu baslangic bilgilerini ekleme asamasinda, InfoBusManager ile ilgili bir problem var. Bos olmamali !.");
			System.out.println("getSpaceWideRegistry().getInfoBusManager() == null !");
			System.exit(-1);
		}

		// PerformanceManager performanceManager = TlosSpaceWide.getSpaceWideRegistry().getPerformanceManagerReference();

		/**
		 * Senaryo içinde bulunan tüm iþler bitene yahut, bir nedenle senaryo durdurulana kadar aþaðýdaki döngü belli aralýklarla çalýþacaktýr.
		 * 
		 * @author serkan taþ
		 *         22.09.2012
		 */

		SpcMonitor spcMonitor = new SpcMonitor(getJobQueue(), getJobQueueIndex());
		spcMonitor.setMyExecuter(new Thread(spcMonitor));

		// Senaryolarin baslama ve bitis bilgilerini de raporlama amacli dolduralim.
		// ilk aklima gelen job da nasil yapildigina bakip oradan kopya cekmek oldu.
		// daha iyi bir yontem varsa onunla degistirelim. hs

		startTime = Calendar.getInstance();

		scenarioRealTime = JsRealTime.Factory.newInstance();

		com.likya.tlos.model.xmlbeans.data.StartTimeDocument.StartTime startTimeTemp = com.likya.tlos.model.xmlbeans.data.StartTimeDocument.StartTime.Factory.newInstance();
		startTimeTemp.setTime(startTime);
		startTimeTemp.setDate(startTime);
		scenarioRealTime.setStartTime(startTimeTemp);

		this.getTimeManagement().setJsRealTime(scenarioRealTime);

		// TODO job icin boyle ama senaryo icin nasil olacak? hs
		// sendEndInfo(Thread.currentThread().getName(), jobRuntimeProperties.getJobProperties());

		while (executionPermission) { // Senaryonun caslistirilmasi icin gerek sart !

			try {

				passOnJobQueueForExecution(spcMonitor);

				// Bundan sonraki kisim islerin RUNNING durumunda olmasi halinde
				// isler icin yapilacaklari kapsiyor.

				// Persistent icin talep varsa, bunu yerine getirmek icin operasyonu diske bir dosyaya kaydet.
				if (getSpaceWideRegistry().getTlosSWConfigInfo().getSettings().getIsPersistent().getValueBoolean() && !JobQueueOperations.persistJobQueue(getSpcId(), getJobQueue(), getJobQueueIndex())) {
					getMyLogger().error("Jobqueue persist error : scenario id : " + getSpcId());
					getMyLogger().error("Continue the execution with persistency feature disabled !");
				}

				try {

					// is kuyrugunun durumunu dokelim. Calisan, bekleyen ve
					// biten islerin sayisini tespit edelim.
					/**
					 * TODO Burada listenin loglanmasý sýrasýnda, logun sadece ilgili senaryo yöneticisi loguna yönlendirilmesi gerekiyor.
					 * 
					 * @author serkan taþ
					 *         20.09.2012
					 */
					JobQueueOperations.dumpJobQueue(getSpcId(), getJobQueue());

				} catch (Throwable t) {
					t.printStackTrace();
				}

				// Job kuyrugundaki islerin hepsi bitti mi, bitti ise LiveStateInfo yu set et.
				if (JobQueueOperations.isJobQueueOver(getJobQueue())) {
					getLiveStateInfo().setStateName(StateName.FINISHED);
					getLiveStateInfo().setSubstateName(SubstateName.COMPLETED);

					/*
					 * isleri siralama icin deneme hs 23.09.2012
					 * HashMap<String, Job> jobQueue = getJobQueue();
					 * ArrayList<Calendar> a = new ArrayList<Calendar>();
					 * ArrayList<Calendar> b = new ArrayList<Calendar>();
					 * JobCalendarCompare compare = new JobCalendarCompare();
					 * Calendar jobStartTime = Calendar.getInstance();
					 * jobStartTime.clear();
					 * Calendar jobStopTime = Calendar.getInstance(); jobStopTime.clear();
					 * 
					 * if (jobQueue != null) {
					 * Iterator<Job> jobsIterator = jobQueue.values().iterator();
					 * while (jobsIterator.hasNext()) {
					 * Job scheduledJob = jobsIterator.next();
					 * JobProperties jobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();
					 * 
					 * try {
					 * 
					 * if (jobProperties.getStateInfos() != null) {
					 * if (jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(StateName.FINISHED)) {
					 * try {
					 * JsRealTime jsRealTime = jobProperties.getTimeManagement().getJsRealTime();
					 * jobStartTime.set(Calendar.YEAR, jsRealTime.getStartTime().getDate().get(Calendar.YEAR));
					 * jobStartTime.set(Calendar.MONTH, jsRealTime.getStartTime().getDate().get(Calendar.MONTH));
					 * jobStartTime.set(Calendar.DAY_OF_MONTH, jsRealTime.getStartTime().getDate().get(Calendar.DAY_OF_MONTH));
					 * jobStartTime.set(Calendar.HOUR_OF_DAY, jsRealTime.getStartTime().getTime().get(Calendar.HOUR_OF_DAY));
					 * jobStartTime.set(Calendar.MINUTE, jsRealTime.getStartTime().getTime().get(Calendar.MINUTE));
					 * jobStartTime.set(Calendar.SECOND, jsRealTime.getStartTime().getTime().get(Calendar.SECOND));
					 * jobStartTime.set(Calendar.ZONE_OFFSET, jsRealTime.getStartTime().getTime().get(Calendar.ZONE_OFFSET));
					 * jobStartTime.set(Calendar.DST_OFFSET, 0);
					 * 
					 * jobStopTime.set(Calendar.YEAR, jsRealTime.getStopTime().getDate().get(Calendar.YEAR));
					 * jobStopTime.set(Calendar.MONTH, jsRealTime.getStopTime().getDate().get(Calendar.MONTH));
					 * jobStopTime.set(Calendar.DAY_OF_MONTH, jsRealTime.getStopTime().getDate().get(Calendar.DAY_OF_MONTH));
					 * jobStopTime.set(Calendar.HOUR_OF_DAY, jsRealTime.getStopTime().getTime().get(Calendar.HOUR_OF_DAY));
					 * jobStopTime.set(Calendar.MINUTE, jsRealTime.getStopTime().getTime().get(Calendar.MINUTE));
					 * jobStopTime.set(Calendar.SECOND, jsRealTime.getStopTime().getTime().get(Calendar.SECOND));
					 * jobStopTime.set(Calendar.ZONE_OFFSET, jsRealTime.getStopTime().getTime().get(Calendar.ZONE_OFFSET));
					 * jobStopTime.set(Calendar.DST_OFFSET, 0);
					 * 
					 * a.add(jobStartTime);
					 * b.add(jobStopTime);
					 * 
					 * System.out.println(a);
					 * 
					 * } catch (Exception e) {
					 * e.printStackTrace();
					 * }
					 * }
					 * } else {
					 * SpaceWideRegistry.getGlobalLogger().error("  > isJobQueueOver fonksiyonunda problem2 : " + jobProperties);
					 * }
					 * } catch (Exception e) {
					 * SpaceWideRegistry.getGlobalLogger().error("  > isJobQueueOver fonksiyonunda problem : " + jobProperties.getStateInfos()); e.printStackTrace();
					 * }
					 * }
					 * 
					 * System.out.println(a);
					 * Collections.sort(a, compare); System.out.println(a);
					 * }
					 */

					break; // beklemeye gerek yok

				}

				// Gelen deger saniye tipine çevriliyor.
				Thread.sleep(getSpaceWideRegistry().getTlosSWConfigInfo().getSettings().getTlosFrequency().getFrequency() * 1000);

				// myLogger.info("     > "+ this.getBaseScenarioInfos().getJsName() + " icin islerin bitmesini bekliyoruz ...");
			} catch (Exception e) {
				getGlobalLogger().info("   > SPC Exception : " + getJobQueue());
				e.printStackTrace();
				getGlobalLogger().error("Terminating TlosSW due to critical error in Spc !");
				System.exit(-1);
			}
		}

		/**
		 * We should disable monitor
		 */
		spcMonitor.getMyExecuter().interrupt();

		// Bu neden bu sekilde? Buraya zaten isler biterse geliyor.
		// isForced true ise kalan islerin zorla bitirilmesi isteniyor anlamina geliyor. Thread ler terminate ediliyor.
		// Kalan ne varsa temizliyoruz. Normalde kalmamasi lazim.
		/**
		 * Buraya sadece iþiler bitince deðil, executionPermission = false yapýlýnca da giriliyor. isActiveThreads : true : kalan bütün joblar taranýp çalýþanlar kapatýlýyor isActiveThreads : false : kalan bütün joblar taranýyor, eüer en az bir tane çalýþan var ise, bekliyor. Bütün iþler bitene kadar bekliyor.
		 * 
		 * @author serkan taþ
		 *         20.09.2012
		 */

		while (isActiveThreads(isForced)) {
			try {
				TlosSpaceWide.print(".");
				getMyLogger().info(".");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		setExecuterThread(null);

		if (JobQueueOperations.isJobQueueOver(getJobQueue())) {
			getMyLogger().info("     > " + this.getBaseScenarioInfos().getJsName() + " icin isler bitti.");

			// Senaryolarin baslama ve bitis bilgilerini de raporlama amacli dolduralim.
			// ilk aklima gelen job da nasil yapildigina bakip oradan kopya cekmek oldu.
			// daha iyi bir yontem varsa onunla degistirelim. hs

			Calendar endTime = Calendar.getInstance();

			long timeDiff = endTime.getTime().getTime() - startTime.getTime().getTime();

			String endLog = getJsName() + ":Bitis zamani : " + DateUtils.getDate(endTime.getTime());
			String duration = getJsName() + ": islem suresi : " + DateUtils.getFormattedElapsedTime((int) timeDiff / 1000);
			// getJobRuntimeProperties().setCompletionDate(endTime);
			// getJobRuntimeProperties().setWorkDuration(DateUtils.getUnFormattedElapsedTime((int) timeDiff / 1000));

			StopTime stopTimeTemp = StopTime.Factory.newInstance();
			stopTimeTemp.setTime(endTime);
			stopTimeTemp.setDate(endTime);
			// scenarioRealTime.setStopTime(stopTimeTemp);

			this.getTimeManagement().getJsRealTime().setStopTime(stopTimeTemp);

			getMyLogger().info(" >>" + "Spc_" + getSpcId() + ">> " + endLog);
			getMyLogger().info(" >>" + "Spc_" + getSpcId() + ">> " + duration);

			// TODO job icin boyle ama senaryo icin nasil olacak? hs
			// sendEndInfo(Thread.currentThread().getName(), getJobRuntimeProperties().getJobProperties());

		} else {
			getMyLogger().info("     > " + this.getBaseScenarioInfos().getJsName() + " icin süreç durduruldu.");
		}

		// **
		// * Su anda calisan senaryo yöneticisinin yönettigi senaryoya ait tüm
		// * isler bitince, asagidaki deger düzenlenmeli.
		// *
		/**
		 * Bu yorumda ve aþýda yapýlan iþ nedir ???
		 * 
		 * @author serkan taþ 20.09.2012
		 */
		getSpaceWideRegistry().getInstanceLookupTable().get(getInstanceId()).getSpcLookupTable().get(getSpcId()).setJobListStatus(true);

		if (getSpaceWideRegistry().getInfoBus() != null) {
			getSpaceWideRegistry().getInfoBus().addInfo(ScenarioMessageFactory.generateScenarioEnd(getSpcId(), getJobQueue().size()));
			getMyLogger().info("     > " + this.getBaseScenarioInfos().getJsName() + " icin islerin bittigi konusunda InfoBusManager bilgilendirildi.");
		} else {
			getGlobalLogger().error("getSpaceWideRegistry().getInfoBusManager() == null !");
		}

	}

	class JobCalendarCompare implements Comparator<Calendar> {
		public int compare(Calendar one, Calendar two) {
			return one.compareTo(two);
		}
	}

	private void passOnJobQueueForExecution(SpcMonitor spcMonitor) throws TlosFatalException {

		// Senaryodaki herbir isi ele alalim.
		Iterator<SortType> jobQueueIndexIterator = getJobQueueIndex().iterator();

		// Senaryolarin bagimliligi icin burada bir kontrol koyduk ama su anda xml lerde kullanilmadigi icin etkisiz. Herzaman true donecek.
		// Senaryo PENDING statusune alindi ise herhangi bir isi baslatmaya calismamali. Normalde RUNNING de buraya geliyoruz.
		// TODO Performans Yoneticisi nin de fikrini almak lazim. Fakat bu asamada kaynak belli olmadigi icin sadece genel performans kontrolu yapilabilir.
		// Bunu sonraya birakiyoruz.

		while (executionPermission && !getLiveStateInfo().getStateName().equals(StateName.PENDING) && isScenarioDependentAllowsToWork() && jobQueueIndexIterator.hasNext()) {

			// Bu senaryo icin olusturulmus Job kuyrugundaki siradaki Job in temel bilgilerini al.
			SortType sortType = jobQueueIndexIterator.next();

			Job scheduledJob = getJobQueue().get(sortType.getJobKey());
			JobRuntimeProperties jobRuntimeProperties = scheduledJob.getJobRuntimeProperties();
			JobProperties jobProperties = jobRuntimeProperties.getJobProperties();

			DependencyList dependentJobList = jobProperties.getDependencyList();

			// DBUtils.updateFirstJob(scheduledJob.getJobRuntimeProperties().getJobProperties(), ParsingUtils.getJobXPath(getSpcId()));

			// job in son state ini al.
			LiveStateInfo jobLiveStateInfo = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0);

			if (jobLiveStateInfo == null || jobLiveStateInfo.getStateName() == null) {
				getGlobalLogger().info("liveStateInfo = null");
				getGlobalLogger().error("  > HATA : Bir isin state bilgisi tamamen bos olamaz !! Kontrol ediniz. " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName());

				/**
				 * Eðer bu durum gerçekleþirse, ilgili iþ FAIL edilip bir sonraki iþe geçmeli
				 * 
				 * @author serkan taþ 21.09.2012
				 */
				/*
				 * StateName FAILED sadece sistemsel basarisizlik durumlarinda kullanilir. Diger durumlarda asagidaki sekilde kullanilir. HS 24.09.2012
				 */
				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.FINISHED, SubstateName.COMPLETED, StatusName.FAILED);
				scheduledJob.sendStatusChangeInfo();
				continue;
			}

			// is calismaya hazir (PENDING/IDLED), fakat calistirma islemleri baslatilmamis bir job ise islemleri baslat.
			if (!jobLiveStateInfo.getStateName().equals(StateName.PENDING)) {
				/**
				 * Burada yapýlan iþin ne olduüunu anlamadým...
				 * 
				 * @author serkan taþ 21.09.2012
				 */
				scheduledJob.setFirstLoop(false);
				continue;
			}

			try {

				// job in PENDING olmasi halinde yapilacaklarin baþladýðý yer.

				if (jobLiveStateInfo.getSubstateName().equals(SubstateName.IDLED)) {
					/*
					 * InfoQueue ya ilk uc state i koyamadigim icin burada bir kerede guncelleme yapiyorum. Eger infoQueue kullanabilirsek bunu kaldiracagiz ama is gorur bu hali.
					 */
					if (scheduledJob.getFirstLoop()) {
						DBUtils.updateFirstJob("tlosSWDailyScenarios10.xml", jobProperties, ParsingUtils.getJobXPath(getSpcId()));
					}

					String jobStartType = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDef().toString();

					// Time myTime = jobRuntimeProperties.getJobProperties().getJobPlannedTime().getStartTime().getTime();
					// tmpCalendar.setTime(jobRuntimeProperties.getJobProperties().getJobPlannedTime().getStartTime().getTime().getTime());

					if (jobStartType.equals(JobTypeDef.TIME_BASED.toString())) {

						Calendar startTime = jobProperties.getTimeManagement().getJsPlannedTime().getStartTime().getTime();
						Calendar tmpCalendar = Calendar.getInstance();

						tmpCalendar.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
						tmpCalendar.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
						tmpCalendar.set(Calendar.SECOND, startTime.get(Calendar.SECOND));

						Calendar currentTime = Calendar.getInstance();

						// isin planlanan calisma zamani gecti mi?
						if (tmpCalendar.before(currentTime)) { // GECTI, calismasi icin gerekli islemlere baslansin.
							handleTransferRequestsOnDss(scheduledJob, sortType, dependentJobList);
						} else { // Zamani bekliyor ...
							if (scheduledJob.getFirstLoop()) { /* status u ekle */
								LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_PENDING, SubstateName.INT_IDLED, StatusName.INT_BYTIME);
								scheduledJob.sendStatusChangeInfo();
							}
						}

					} else if (jobStartType.equals(JobTypeDef.USER_BASED.toString())) {

						if (scheduledJob.getFirstLoop()) { /* status u ekle */
							LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_PENDING, SubstateName.INT_IDLED, StatusName.INT_BYUSER);
							scheduledJob.sendStatusChangeInfo();
						}

						// Ekrandan kullanici tercihi alinacak. Kullanici tercihi alininca StatusName WAITING yapilacak !!
						/*
						 * Boolean userChoice = true; if (userChoice) { jobRun = true; }
						 */

					} else if (jobStartType.equals(JobTypeDef.EVENT_BASED.toString())) {

						if (scheduledJob.getFirstLoop()) { /* status u ekle */
							LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_PENDING, SubstateName.INT_IDLED, StatusName.INT_BYEVENT);
							scheduledJob.sendStatusChangeInfo();
						}

						Boolean eventOccured = true; // TODO buraya olay kontrolu eklenecek.
						if (eventOccured) {
							handleTransferRequestsOnDss(scheduledJob, sortType, dependentJobList);
						}

					} else {

						getGlobalLogger().error("  > HATA : Bir isin baslama kosulu bilgisi USER/EVENT/TIME disinda birsey bos olamaz !! Kontrol ediniz. " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName());
						/**
						 * Eðer bu durum gerçekleþirse, ilgili iþ FAIL edilip bir sonraki iþe geçmeli
						 * 
						 * @author serkan taþ 21.09.2012
						 */
						LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.FINISHED, SubstateName.COMPLETED, StatusName.FAILED);
						scheduledJob.sendStatusChangeInfo();
					}

				} else if (jobLiveStateInfo.getSubstateName().equals(SubstateName.READY) && (jobLiveStateInfo.getStatusName().equals(StatusName.WAITING) || jobLiveStateInfo.getStatusName().equals(StatusName.LOOKFOR_RESOURCE))) {
					// is calismaya hazir (IDLED) disinda bir statude, kuvvetle muhtemel READY beklemeye gecmis.
					handleTransferRequestsOnDss(scheduledJob, sortType, dependentJobList);

				} else {

					/**
					 * Eðer bu durum gerçekleþirse, ilgili iþ FAIL edilip bir sonraki iþe geçmeli
					 * 
					 * @author serkan taþ 21.09.2012
					 */
					getGlobalLogger().error("  > HATA : Bir isin baslama kosulu bilgisi IDLED ve READY disinda birsey bos olamaz !! Kontrol ediniz. " + jobProperties.getBaseJobInfos().getJsName());
					LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.FINISHED, SubstateName.COMPLETED, StatusName.FAILED);
					scheduledJob.sendStatusChangeInfo();
					continue;
				}

			} catch (TlosException t) {
				getGlobalLogger().error(t.getMessage());
				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.FINISHED, SubstateName.COMPLETED, StatusName.FAILED);
				scheduledJob.sendStatusChangeInfo();
				t.printStackTrace();
			}
			// job in PENDING olmasi halinde yapilacaklarin sonlandigi yer.
			scheduledJob.setFirstLoop(false);

			try {
				if (spcMonitor.getMyExecuter().getState().equals(Thread.State.NEW) && !spcMonitor.getMyExecuter().isAlive()) {
					spcMonitor.getMyExecuter().start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private void handleTransferRequestsOnDss(Job scheduledJob, SortType sortType, DependencyList dependentJobList) throws UnresolvedDependencyException, TransformCodeCreateException {

		JobProperties jobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();
		// job in son state ini al.
		LiveStateInfo jobLiveStateInfo = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0);

		if (dependentJobList != null) {

			String dependencyExpression = dependentJobList.getDependencyExpression().trim().toUpperCase();
			Item[] dependencyArray = jobProperties.getDependencyList().getItemArray();

			if (isJobDependencyResolved(scheduledJob, dependencyExpression, dependencyArray)) {
				if (DssVisionaire.evaluateDss(scheduledJob).getResultCode() >= 0) {
					// if (DssFresh.transferPermission(scheduledJob)) {
					transfer(scheduledJob, sortType.getJobKey());
				}
			} else {
				// Bu durumda job bagimliliklarindan beklenenler var demektir.
				// Son status WAITING degilse eklenmeli
				if (!LiveStateInfoUtils.equalStates(jobLiveStateInfo, StateName.PENDING, SubstateName.READY, StatusName.WAITING)) {
					LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.PENDING, SubstateName.READY, StatusName.WAITING);
					scheduledJob.sendStatusChangeInfo();
				}
			}

		} else { // Herhangi bir bagimliligi yok !!
			if (DssVisionaire.evaluateDss(scheduledJob).getResultCode() >= 0) {
				// if (DssFresh.transferPermission(scheduledJob)) {
				transfer(scheduledJob, sortType.getJobKey());
			}
		}

	}

	private synchronized boolean isJobDependencyResolved(Job ownerJob, String dependencyExpression, Item[] dependencyArray) throws UnresolvedDependencyException {

		dependencyExpression = dependencyExpression.replace("AND", "&&");
		dependencyExpression = dependencyExpression.replace("OR", "||");

		Expression exp = new Expression(dependencyExpression);
		BigDecimal result = new BigDecimal(0);

		ArrayIterator dependencyArrayIterator = new ArrayIterator(dependencyArray);

		Map<String, BigDecimal> variables = new HashMap<String, BigDecimal>();

		while (dependencyArrayIterator.hasNext()) {

			Item item = (Item) (dependencyArrayIterator.next());
			JobRuntimeProperties jobRuntimeProperties = null;
			/*
			 * if (item != null) { myLogger.info("     > bagimlilik var1>" + item.getJsDependencyRule()); } else { myLogger.info("     > item bos !!"); throw new TlosFatalException(); }
			 */
			if (dependencyExpression.indexOf(item.getDependencyID().toUpperCase()) < 0) {
				// getMyLogger().error("Hatalý tanýmlama ! Uygulama sona eriyor !");
				String errorMessage = "     > " + ownerJob.getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJsName() + " isi icin hatali bagimlilik tanimlamasi yapilmis ! (" + dependencyExpression + ") kontrol ediniz.";
				getMyLogger().info(errorMessage);
				getMyLogger().error(errorMessage);
				throw new UnresolvedDependencyException(errorMessage);
			}

			if (item.getJsPath() == null || item.getJsPath() == "") { // Lokal
				// bir
				// bagimlilik
				if (getJobQueue().get(item.getJsName()) == null) {
					getMyLogger().error("     > Yerel bagimlilik tanimi yapilan is bulunamadi : " + item.getJsName());
					getMyLogger().error("     > Ana is adi : " + ownerJob.getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJsName());
					getMyLogger().error("     > Ana senaryo yolu : " + ownerJob.getJobRuntimeProperties().getTreePath());
					getMyLogger().info("     > Bagimlilikla ilgili bir problemden dolayi uygulama sona eriyor !");
					throw new UnresolvedDependencyException("     > Yerel bagimlilik tanimi yapilan is bulunamadi : " + item.getJsName());
				}
				jobRuntimeProperties = getJobQueue().get(item.getJsName()).getJobRuntimeProperties();
			} else { // Global bir bagimlilik

				SpcInfoType spcInfoType = getSpaceWideRegistry().getInstanceLookupTable().get(getInstanceId()).getSpcLookupTable().get(Cpc.getRootPath() + "." + getInstanceId() + "." + item.getJsPath());

				if (spcInfoType == null) {
					getMyLogger().error("     > Genel bagimlilik tanimi yapilan senaryo bulunamadi : " + Cpc.getRootPath() + "." + getInstanceId() + "." + item.getJsPath());
					getMyLogger().error("     > Ana is adi : " + ownerJob.getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJsName());
					getMyLogger().error("     > Ana senaryo yolu : " + ownerJob.getJobRuntimeProperties().getTreePath());
					getMyLogger().error("     > Uygulama sona eriyor !");
					getMyLogger().info("     > Bagimlilikla ilgili bir problemden dolayi uygulama sona eriyor !");
					Cpc.dumpSpcLookupTable(getInstanceId(), getSpaceWideRegistry().getInstanceLookupTable().get(getInstanceId()).getSpcLookupTable());
					throw new UnresolvedDependencyException("     > Genel bagimlilik tanimi yapilan senaryo bulunamadi : " + Cpc.getRootPath() + "." + getInstanceId() + "." + item.getJsPath());
				}

				Job job = spcInfoType.getSpcReferance().getJobQueue().get(item.getJsName());
				if (job == null) {
					getMyLogger().error("     > Genel bagimlilik tanimi yapilan :");
					getMyLogger().error("     > Ana is adi : " + ownerJob.getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJsName());
					getMyLogger().error("     > Bagli is : " + item.getJsName() + " tanimli mi? Tanimli ise bagimlilik ile ilgili bir problem olabilir! (Problem no:1045)");
					getMyLogger().error("     >    Dizin : " + Cpc.getRootPath() + "." + getInstanceId() + "." + item.getJsPath());
					getMyLogger().error("     > 	Yukaridaki is  " + spcInfoType.getSpcReferance().getSpcId() + " adli senaryoda bulunamadi !");
					getMyLogger().error("     > Uygulama sona eriyor !");
					getMyLogger().info("     > Bagimlilikla ilgili bir problemden dolayi uygulama sona eriyor !");
					throw new UnresolvedDependencyException("     > Bagimlilikla ilgili bir problemden dolayi uygulama sona eriyor !");
				}

				jobRuntimeProperties = job.getJobRuntimeProperties();
			}

			if (jobRuntimeProperties.getJobProperties() == null) {
				getMyLogger().info("     > jobRuntimeProperties.getJobProperties() == null !!");
				throw new UnresolvedDependencyException("     > jobRuntimeProperties.getJobProperties() == null !!");
			}

			if (item.getJsDependencyRule().getStateName() != null && item.getJsDependencyRule().getSubstateName() == null && item.getJsDependencyRule().getStatusName() == null) {
				if (jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(item.getJsDependencyRule().getStateName())) {
					variables.put(item.getDependencyID(), new BigDecimal(1)); // true
				} else {
					variables.put(item.getDependencyID(), new BigDecimal(0)); // false
				}
			} else if (item.getJsDependencyRule().getStateName() != null && item.getJsDependencyRule().getSubstateName() != null && item.getJsDependencyRule().getStatusName() == null) {
				if (jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(item.getJsDependencyRule().getStateName()) && jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName().equals(item.getJsDependencyRule().getSubstateName())) {
					variables.put(item.getDependencyID(), new BigDecimal(1)); // true
				} else {
					variables.put(item.getDependencyID(), new BigDecimal(0)); // false
				}
			} else if (item.getJsDependencyRule().getStateName() != null && item.getJsDependencyRule().getSubstateName() != null && item.getJsDependencyRule().getStatusName() != null && jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName() != null && jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName() != null) {
				if (jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(item.getJsDependencyRule().getStateName()) && jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName().equals(item.getJsDependencyRule().getSubstateName())) {
					if (jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStatusName() != null)
						if (jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStatusName().equals(item.getJsDependencyRule().getStatusName())) {
							variables.put(item.getDependencyID(), new BigDecimal(1)); // true
						}
				} else {
					variables.put(item.getDependencyID(), new BigDecimal(0)); // false
				}
			} else {
				return false;
			}

		}

		result = exp.eval(variables);

		return result.intValue() == 0 ? false : true;
	}

	private synchronized void executeJob(Job scheduledJob) {

		JobProperties jobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();

		getMyLogger().info("");
		getMyLogger().info("     > " + jobProperties.getBaseJobInfos().getJsName() + " Calisma zamani gelmis, <Server> da calistirilacak. !");

		getMyLogger().info("");
		getMyLogger().info(scheduledJob.getJobRuntimeProperties().toString());
		getMyLogger().info("");

		// DBUtils.insertJob(scheduledJob.getJobRuntimeProperties().getJobProperties(), ParsingUtils.getJobXPath(getSpcId()));

		/* RUNNING state i STAGE_IN subs ekle */
		LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_RUNNING, SubstateName.INT_STAGE_IN, StatusName.INT_TIME_IN);
		scheduledJob.sendStatusChangeInfo();

		// is, dosya tasimasi gerektiriyorsa burada yapilacak !!

		/* RUNNING state i ON_RESOURCE subs ekle */
		/*
		 * Job in verildigi yerden bu isi yapmak daha dogru geldi. O yuzden kaldirdim ama simdilik dursun. XmlBeansTransformer.insertNewLiveStateInfo(scheduledJob.getJobRuntimeProperties().getJobProperties(), StateName.INT_RUNNING, SubstateName.INT_ON_RESOURCE, StatusName.INT_TIME_IN); scheduledJob.sendStatusChangeInfo();
		 */

		Thread starterThread = new Thread(scheduledJob);
		starterThread.setName(this.getSpcId());
		scheduledJob.setMyExecuter(starterThread);

		getMyLogger().info("     > " + jobProperties.getBaseJobInfos().getJsName() + " isi icin <Server> da bir thread acildi !");
		getMyLogger().info("");

		scheduledJob.getMyExecuter().start();

		return;
	}

	private synchronized JobProperties getJobPropertiesWithSpecialParameters(JobRuntimeProperties jobRuntimeProperties) {

		JobProperties jobProperties = jobRuntimeProperties.getJobProperties();

		int jobType = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommandType().intValue();

		switch (jobType) {
		case JobCommandType.INT_FTP:

			FtpProperties ftpProperties = jobRuntimeProperties.getFtpProperties();

			FtpAdapterProperties adapterProperties = TypeUtils.resolveFtpAdapterProperties(jobProperties);
			adapterProperties.getRemoteTransferProperties().setFtpProperties(ftpProperties);

			break;

		case JobCommandType.INT_DB_JOBS:
			// TODO db joblari ile ilgili ayarlama yapilacak
			
			DbJobDefinition dbJobDefinition = TypeUtils.resolveDbJobDefinition(jobProperties);
			
			 
			DbProperties dbProperties = jobRuntimeProperties.getDbProperties();
			DbConnectionProfile dbConnectionProfile = jobRuntimeProperties.getDbConnectionProfile();
			dbJobDefinition.setDbProperties(dbProperties);
			dbJobDefinition.setDbConnectionProfile(dbConnectionProfile);
	 
		 
			break;

		default:
			break;
		}

		return jobProperties;
	}

	private synchronized boolean transferJobToAgent(Job scheduledJob, String jobKey) {

		// JobProperties jobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();
		// jobProperties.setLSIDateTime(DateUtils.getW3CDateTime());

		// agenta gonderilecek islerde gondermeden once JobRuntimeProperties icinde olup jobproperties icinde olmayan kisimlar jobproperties icine aliniyor
		JobProperties jobProperties = getJobPropertiesWithSpecialParameters(scheduledJob.getJobRuntimeProperties());

		String rxMessageKey = getTransferedJobKey(jobProperties.getAgentId(), jobKey, jobProperties.getLSIDateTime());
		RxMessage rxMessage = XmlUtils.generateRxMessage(jobProperties, rxMessageKey);

		SWAgent agent = getSpaceWideRegistry().getAgentManagerReference().getSwAgentsCache().get(jobProperties.getAgentId() + "");
		JmxAgentUser jmxAgentUser = XmlUtils.getJmxAgentUser(agent);

		/* DB ye isin su ana kadarki kismi insert ediliyor. tlosSWDailyScenarios.xml */
		// DBUtils.insertJobAgentId(agent.getId()+"", jobProperties.getID(), ParsingUtils.getJobXPath(getSpcId()));
		// myLogger.info("     > "+ scheduledJob.getJobRuntimeProperties().getJobProperties().getJsName()+ " DB ye insert ediliyor !");
		// DBUtils.insertJob(jobProperties, ParsingUtils.getJobXPath(getSpcId()));
		// InfoBusManager a bilgilendirme yapilacak.
		// scheduledJob.sendStatusChangeInfo(jobProperties.getLiveStateInfos().getLiveStateInfoArray(0));

		// isin agent a transfer edilmesi sonrasinda is aninda calistiriliyorsa
		// baslamasi ile bitmesi bir olan islerin state lerinde problem oluyor
		// bu nedenle agent a transfer edilmesi islemi oncesi RUNNING e almaya karar verdim. HS

		LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_RUNNING, SubstateName.INT_STAGE_IN, StatusName.INT_TIME_IN);
		scheduledJob.sendStatusChangeInfo();

		// is, dosya tasimasi gerektiriyorsa burada yapilacak !!

		// XmlBeansTransformer.insertNewLiveStateInfo(scheduledJob.getJobRuntimeProperties().getJobProperties(), StateName.INT_RUNNING, SubstateName.INT_ON_RESOURCE, StatusName.INT_TIME_IN);
		// myLogger.info("     2XXX"+scheduledJob.getJobRuntimeProperties().getJobProperties().getJsName()+" > " +scheduledJob.getJobRuntimeProperties().getJobProperties());
		boolean transferSuccess = TSWAgentJmxClient.jobHandle(agent.getResource().getStringValue(), (int) agent.getJmxPort(), XmlUtils.getRxMessageXML(rxMessage), jmxAgentUser);

		if (!transferSuccess) { // Eger agent a transfer basarili olmadi ise onden ekledigimiz state leri silmemiz gerekiyor. HS
			jobProperties.getStateInfos().getLiveStateInfos().removeLiveStateInfo(0);
			jobProperties.getStateInfos().getLiveStateInfos().removeLiveStateInfo(0);
			getMyLogger().info("     > " + jobProperties.getBaseJobInfos().getJsName() + " isi <Agent#" + jobProperties.getAgentId() + "#> da calistirilAMAdi !");
		} else
			getMyLogger().info("     > " + jobProperties.getBaseJobInfos().getJsName() + " isi <Agent#" + jobProperties.getAgentId() + "#> da calistirildi !");

		return transferSuccess;
	}

	private synchronized void transfer(Job scheduledJob, String jobKey) throws UnresolvedDependencyException, TransformCodeCreateException {

		JobProperties jobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();
		int agentId = jobProperties.getAgentId();
		
		int substateName = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName().intValue();

		StreamSource transformCode = null;
		// TODO bir kere registery ye yuklenmesi yeterli. Her seferinde yuklenmesine gerek yok. HS
		try {
			transformCode = RemoteDBOperator.getTransformXslCode("hs:tlosJobTransformXsl()");
		} catch (Exception e) {
			throw new TransformCodeCreateException(e);
		}
		// TODO bir kere registery ye yuklenmesi yeterli. Her seferinde yuklenmesine gerek yok. HS
		try {
			scheduledJob.setRequestedStream(RemoteDBOperator.getTransformXslCode("hs:tlosXMLTransformXsl()"));
		} catch (Exception e) {
			throw new TransformCodeCreateException(e);
		}

		// PARAMETRE atamalari burada yapilir.
		
		//LOCAL VE GLOBAL
		AgentManager agentManagerRef = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference();
		HashMap<Integer, ArrayList<Parameter>> parameterListAll = TlosSpaceWide.getSpaceWideRegistry().getAllParameters();
		ArrayList<Parameter> parameterList = parameterListAll.get(agentId);

		// TODO Statik ve dinamik job ayrimi yapabilirsek burada sadece dinamik joblara bu islem uygulanacak. HS
		JobProperties transformedjobProperties = ApplyXslt.transform(parameterList, jobProperties, transformCode);
		scheduledJob.getJobRuntimeProperties().setJobProperties(transformedjobProperties);

		// parametre gecisi burada yapilir !!

		InputParameterPassing parameterPassing = new InputParameterPassing(getSpaceWideRegistry(), getInstanceId());

		// 1.tip verilen xpath ile aktarim.
		parameterPassing.setInputParameter(scheduledJob.getJobRuntimeProperties().getJobProperties());
		// 2.tip fiziksel bagimlilik ile aktarim
		parameterPassing.setInputParameterViaDependency(getJobQueue(), scheduledJob);

		scheduledJob.sendEndInfo(getSpcId(), scheduledJob.getJobRuntimeProperties().getJobProperties());
		// //////////////////\\\\\\\\\\\\\\\\\

		/* Secilen kaynak server ise server da degilse agent a aktararak calistir. */

		if (agentManagerRef.isServer(agentId)) {
			executeJob(scheduledJob);
		} else {
			scheduledJob.getJobRuntimeProperties().getJobProperties().getTimeManagement().addNewJsRealTime().addNewStartTime().setTime(Calendar.getInstance());
			boolean transferSuccess = transferJobToAgent(scheduledJob, jobKey);
			long transferTime = System.currentTimeMillis();

			if (transferSuccess) {
				agentManagerRef.setLastJobTransfer(agentId + "", true, transferTime);
			} else {
				agentManagerRef.setLastJobTransfer(agentId + "", false, transferTime);
				scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).setSubstateName(SubstateName.Enum.forInt(substateName));
				if (!agentManagerRef.getSwAgentCache(agentId + "").getInJmxAvailable()) {// kaynak injmxavailable false olduysa resource'uda false yap
					scheduledJob.setResourceToFalse();
				}
			}
		}
	}

	public boolean isActiveThreads(boolean isForced) {

		Iterator<Job> jobsIterator = getJobQueue().values().iterator();

		while (jobsIterator.hasNext()) {

			Job scheduledJob = jobsIterator.next();
			Thread myExecuter = scheduledJob.getMyExecuter();

			if (isForced) {
				if (myExecuter != null && myExecuter.isAlive()) {
					scheduledJob.getMyExecuter().interrupt();
					while (myExecuter.isAlive()) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				scheduledJob.setMyExecuter(null);

			} else {
				if ((myExecuter != null) && myExecuter.isAlive()) {
					return true;
				}
			}

		}

		return false;
	}

	public LiveStateInfo getLastStateOfJob(LiveStateInfos liveStateInfos) {

		/*
		 * TODO Burada tarihe gore siralama yapmaya gerek var mi? Varsa asagidakine benzer birsey yapmamiz lazim. tarih cevriminde bir problem var, onu cozmemiz lazim tabii once
		 * 
		 * int boyut = liveStateInfos.sizeOfLiveStateInfoArray(); Date refDate = DateUtils.getDateTime( liveStateInfos.getLiveStateInfoArray(0).getLSIDateTime()); LiveStateInfo lastStateInfo = liveStateInfos.getLiveStateInfoArray(0);
		 * 
		 * for (int i=0; i<boyut; i++) { System.out.println(liveStateInfos.getLiveStateInfoArray(i)); System.out.println(liveStateInfos.getLiveStateInfoArray(i)); //com.likya.tlossw.utils.date.DateUtils String dateTimeInString = liveStateInfos.getLiveStateInfoArray(i).getLSIDateTime(); if(DateUtils.getDateTime(dateTimeInString).after(refDate)) { refDate = DateUtils.getDateTime(dateTimeInString); lastStateInfo = liveStateInfos.getLiveStateInfoArray(i); } }
		 */
		LiveStateInfo lastStateInfo = liveStateInfos.getLiveStateInfoArray(0);

		return lastStateInfo;
	}

	public int getNumOfJobsByAgent(int agentId) {

		int counter = 0;

		Iterator<Job> jobsIterator = getJobQueue().values().iterator();

		while (jobsIterator.hasNext()) {
			Job scheduledJob = jobsIterator.next();
			LiveStateInfo currentStateInfo = null;
			if (scheduledJob.getJobRuntimeProperties().getJobProperties().getAgentId() != 0 && scheduledJob.getJobRuntimeProperties().getJobProperties().getAgentId() == agentId) {
				currentStateInfo = getLastStateOfJob(scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos());
				if (currentStateInfo.getStateName().toString().equalsIgnoreCase(StateName.RUNNING.toString())) {
					// System.out.println("OK !");
					counter += 1;
				}
			}

		}

		return counter;
	}

	public int getNumOfActiveJobsOld() {

		int numOfWorkingJobs = getNumOfJobs(StateName.RUNNING.toString());
		int numOfTimeoutJobs = getNumOfJobs(SubstateName.ON_RESOURCE.toString());

		// TODO burasi duzeltilecek. Tiemout icin kosullu birseyler yapmak lazim.
		return numOfWorkingJobs + numOfTimeoutJobs;
	}

	/*
	 * state yapisinda time-out statusu running statusunun substate i oldugu icin hem state i running olanlari hem de substate i timeout olanlari toplarsak timeout olanlari iki kere saymis olacagiz. bunun icin o kismi kaldirdim
	 */
	public int getNumOfActiveJobs() {

		int numOfWorkingJobs = getNumOfJobs(StateName.RUNNING.toString());

		return numOfWorkingJobs;
	}

	public int getNumOfJobs() {
		return getJobQueue().size();
	}

	public int getNumOfJobs(String stateNameType) {

		int counter = 0;

		Iterator<Job> jobsIterator = getJobQueue().values().iterator();

		while (jobsIterator.hasNext()) {

			Job scheduledJob = jobsIterator.next();
			JobProperties jobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();
			StateName.Enum stateName = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName();

			if (stateName != null && stateName.toString().equals(stateNameType)) {
				counter += 1;
			}

		}

		return counter;
	}

	public int getNumOfJobs(SubstateName substateNameType) {

		int counter = 0;

		Iterator<Job> jobsIterator = getJobQueue().values().iterator();

		while (jobsIterator.hasNext()) {
			Job scheduledJob = jobsIterator.next();
			String tmpSubstateNameType = scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName().toString();
			if (tmpSubstateNameType != null && tmpSubstateNameType.equals(substateNameType)) {
				counter += 1;
			}

		}

		return counter;
	}

	public int getNumOfJobs(StateName stateNameType) {

		int counter = 0;

		Iterator<Job> jobsIterator = getJobQueue().values().iterator();

		while (jobsIterator.hasNext()) {
			Job scheduledJob = jobsIterator.next();
			if (scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(stateNameType)) {
				counter += 1;
			}
		}

		return counter;
	}

	public void pause() {
		if (isPausable()) {
			getMyLogger().info("Spc " + getSpcId() + " Beklemeye alýyor...");
			getLiveStateInfo().setStateName(StateName.PENDING);
			getLiveStateInfo().setSubstateName(SubstateName.PAUSED);
			getMyLogger().info("Spc " + getSpcId() + " Beklemede !");
		}
	}

	public void resume() {
		if (isResumable()) {
			getMyLogger().info("Spc " + getSpcId() + " Bekleme durumundan çýkartýyor...");
			getLiveStateInfo().setStateName(StateName.RUNNING);
			getLiveStateInfo().setSubstateName(null);
			getMyLogger().info("Spc " + getSpcId() + " Bekleme durumundan çýkartýldý !");
		}
	}

	private boolean isScenarioDependentAllowsToWork() throws TlosFatalException {
		if (this.getDependencyList() == null || this.getDependencyList().getItemArray().length == 0) {
			// There is no dependency defined so it is allowed to execute
			return true;
		} else {
			String dependencyExpression = this.getDependencyList().getDependencyExpression();
			Item[] dependencyArray = this.getDependencyList().getItemArray();

			dependencyExpression = dependencyExpression.replace("AND", "&&");
			dependencyExpression = dependencyExpression.replace("OR", "||");

			Expression exp = new Expression(dependencyExpression);
			BigDecimal result = new BigDecimal(0);

			ArrayIterator dependencyArrayIterator = new ArrayIterator(dependencyArray);

			Map<String, BigDecimal> variables = new HashMap<String, BigDecimal>();

			while (dependencyArrayIterator.hasNext()) {

				Item item = (Item) (dependencyArrayIterator.next());
				Spc spc = null;

				if (dependencyExpression.indexOf(item.getDependencyID().toUpperCase()) < 0) {
					getMyLogger().error("Hatalý tanýmlama ! Uygulama sona eriyor !");
					throw new TlosFatalException();
				}

				if (item.getJsPath() == null || item.getJsPath() == "") {
					getMyLogger().error("Hatalý sanal baðýmlýlýk ! Tanýmý yapýlan senaryonun yolu yanlýþ ! Sernaryo adý : " + item.getJsName());
					getMyLogger().error("Ana senaryo adý : " + getSpcId());
					getMyLogger().error("Ana senaryo yolu : " + this.getBaseScenarioInfos().getJsName());
					getMyLogger().error("Uygulama sona eriyor !");
					throw new TlosFatalException();
				} else {

					SpcInfoType spcInfoType = InstanceMapHelper.findSpc(item.getJsPath(), getSpaceWideRegistry().getInstanceLookupTable());

					if (spcInfoType == null) {
						getMyLogger().error("Genel baðýmlýlýk tanýmý yapýlan senaryo bulunamadý : " + Cpc.getRootPath() + "." + getInstanceId() + "." + item.getJsPath());
						getMyLogger().error("Ana senaryo adý : " + getSpcId());
						getMyLogger().error("Ana senaryo yolu : " + this.getBaseScenarioInfos().getJsName());
						getMyLogger().error("Uygulama sona eriyor !");
						Cpc.dumpSpcLookupTable(getInstanceId(), getSpaceWideRegistry().getInstanceLookupTable().get(getInstanceId()).getSpcLookupTable());
						throw new TlosFatalException();
					}

					spc = spcInfoType.getSpcReferance();
				}

				if (item.getJsDependencyRule().getStateName() != null && item.getJsDependencyRule().getSubstateName() == null && item.getJsDependencyRule().getStatusName() == null) {
					if (spc.getLiveStateInfo().getStateName().equals(item.getJsDependencyRule().getStateName())) {
						variables.put(item.getDependencyID(), new BigDecimal(1)); // true
					} else {
						variables.put(item.getDependencyID(), new BigDecimal(0)); // false
					}
				} else if (item.getJsDependencyRule().getStateName() != null && item.getJsDependencyRule().getSubstateName() != null && item.getJsDependencyRule().getStatusName() == null) {
					if (spc.getLiveStateInfo().getStateName().equals(item.getJsDependencyRule().getStateName()) && spc.getLiveStateInfo().getSubstateName().equals(item.getJsDependencyRule().getSubstateName())) {
						variables.put(item.getDependencyID(), new BigDecimal(1)); // true
					} else {
						variables.put(item.getDependencyID(), new BigDecimal(0)); // false
					}
				} else if (item.getJsDependencyRule().getStateName() != null && item.getJsDependencyRule().getSubstateName() != null && item.getJsDependencyRule().getStatusName() != null) {
					if (spc.getLiveStateInfo().getStateName().equals(item.getJsDependencyRule().getStateName()) && spc.getLiveStateInfo().getSubstateName().equals(item.getJsDependencyRule().getSubstateName()) && spc.getLiveStateInfo().getStatusName().equals(item.getJsDependencyRule().getStatusName())) {
						variables.put(item.getDependencyID(), new BigDecimal(1)); // true
					} else {
						variables.put(item.getDependencyID(), new BigDecimal(0)); // false
					}
				} else {
					return false;
				}

			}

			result = exp.eval(variables);

			boolean retValue = (result.intValue() == 0 ? false : true);

			if (!retValue) {
				this.getLiveStateInfo().setStateName(StateName.PENDING);
				this.getLiveStateInfo().setSubstateName(SubstateName.READY);
			} else {
				this.getLiveStateInfo().setStateName(StateName.RUNNING);
			}

			return retValue;
		}

	}

	public boolean isRecovered() {
		return isRecovered;
	}

	public String getTransferedJobKey(int agentId, String jobKey, String LSIDateTime) {
		String transferedJobKey = getInstanceId() + "|" + getSpcId() + "|" + jobKey + "|" + agentId + "|" + LSIDateTime;

		return transferedJobKey;
	}

	public synchronized void failAgentJobs(int agentId) {
		Iterator<Job> jobsIterator = getJobQueue().values().iterator();

		while (jobsIterator.hasNext()) {

			Job scheduledJob = jobsIterator.next();
			JobProperties jobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();

			if (jobProperties.getAgentId() == agentId) {

				if (!TypeUtils.resolveState(jobProperties).equals(StateName.FINISHED)) {
					/* FAILED state i ekle */
					LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.FAILED, null, null, 555, "Agent a ulasilamiyor.");
					scheduledJob.sendStatusChangeInfo();
					LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.PENDING, SubstateName.IDLED, null, 111, "Is yeniden kurgulandi.");
					scheduledJob.sendStatusChangeInfo();
				}

			}
		}
	}

	/**
	 * Belirli bir Tlos Agent'inda calisan joblari bulur
	 * 
	 * @param tlosAgentId
	 *            Tlos Agent'in id
	 * @return job listesi
	 */
	public ArrayList<Job> getJobListForAgent(int tlosAgentId) {
		ArrayList<Job> jobList = new ArrayList<Job>();

		Iterator<Job> jobsIterator = getJobQueue().values().iterator();

		while (jobsIterator.hasNext()) {
			Job scheduledJob = jobsIterator.next();
			if (scheduledJob.getJobRuntimeProperties().getJobProperties().getAgentId() == tlosAgentId) {
				jobList.add(scheduledJob);
			}
		}
		return jobList;
	}

}

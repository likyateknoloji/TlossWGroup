package com.likya.tlossw.core.spc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.transform.stream.StreamSource;

import com.likya.tlos.model.xmlbeans.agent.RxMessageDocument.RxMessage;
import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.common.JobBaseTypeDocument.JobBaseType;
import com.likya.tlos.model.xmlbeans.common.JobTypeDefDocument.JobTypeDef;
import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.agentclient.TSWAgentJmxClient;
import com.likya.tlossw.core.agents.AgentManager;
import com.likya.tlossw.core.dss.DssVisionaire;
import com.likya.tlossw.core.spc.helpers.DependencyResolver;
import com.likya.tlossw.core.spc.helpers.JobQueueOperations;
import com.likya.tlossw.core.spc.helpers.SortType;
import com.likya.tlossw.core.spc.helpers.TimeZoneCalculator;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.db.utils.DBUtils;
import com.likya.tlossw.exceptions.TlosException;
import com.likya.tlossw.exceptions.TlosFatalException;
import com.likya.tlossw.exceptions.TransformCodeCreateException;
import com.likya.tlossw.exceptions.UnresolvedDependencyException;
import com.likya.tlossw.infobus.helper.ScenarioMessageFactory;
import com.likya.tlossw.model.JobQueueResult;
import com.likya.tlossw.model.SpcLookupTable;
import com.likya.tlossw.model.jmx.JmxAgentUser;
import com.likya.tlossw.transform.InputParameterPassing;
import com.likya.tlossw.utils.CpcUtils;
import com.likya.tlossw.utils.JobIndexUtils;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.SpcUtils;
import com.likya.tlossw.utils.TypeUtils;
import com.likya.tlossw.utils.XmlUtils;
import com.likya.tlossw.utils.xml.ApplyXslt;

/**
 * @author vista
 * 
 */
public class Spc extends SpcBase {

	private static final long serialVersionUID = 4949177188797710064L;

	private boolean isRecovered = false;

	public Spc(String nativePlanId, String spcAbsolutePath, SpaceWideRegistry spaceWideRegistry, ArrayList<JobRuntimeProperties> taskList) throws TlosFatalException {
		this(nativePlanId, spcAbsolutePath, spaceWideRegistry, taskList, false, false);
	}

	public Spc(String nativePlanId, String spcAbsolutePath, SpaceWideRegistry spaceWideRegistry, ArrayList<JobRuntimeProperties> taskList, boolean isRecoverAction, boolean isTester) throws TlosFatalException {

		super(nativePlanId, spcAbsolutePath, spaceWideRegistry, taskList, isTester);

		if (isRecoverAction) {
			getGlobalLogger().info("   > " + spcAbsolutePath + " recover islemi yapildi. ");
			if (!JobQueueOperations.recoverJobQueue(spcAbsolutePath, getJobQueue(), getJobQueueIndex())) {
				// TODO Recover edemezse ne yapilacagi konusunda bir karar vermek gerekir.
				getGlobalLogger().error(" ONEMLI : " + spcAbsolutePath + " recover edilemedi. Hata Kodu : 98087 ");
				getGlobalLogger().info(" ONEMLI : " + spcAbsolutePath + " recover edilemedi. Hata Kodu : 98087 ");
				System.exit(-1);
			}
			isRecovered = true;
		}

	}

	private SpcMonitor preRunInit() {

		Thread.currentThread().setName(getCommonName());

		getMyLogger().info("     > " + getBaseScenarioInfos().getJsName() + " icin ana thread baslatiliyor. Toplam is Sayisi : " + getJobQueue().size());

		/**
		 * InfoBus null ise kritik bir hata vardır, muhtemelen yazılımda bug vardır.
		 * Koşulsuz olarak uygulama kapanmalıdır.
		 * 
		 * @author serkan taş 19.09.2012
		 */
		if (getSpaceWideRegistry().getInfoBus() != null) {
			getSpaceWideRegistry().getInfoBus().addInfo(ScenarioMessageFactory.generateScenarioStart(getSpcAbsolutePath(), getJobQueue().size()));
			if (SpaceWideRegistry.isDebug) {
				getMyLogger().info("     > " + this.getBaseScenarioInfos().getJsName() + " icin islerin baslatildigi bilgisi InfoBusManager a iletildi.");
			}
		} else {
			if (SpaceWideRegistry.isDebug) {
				getMyLogger().info("     > " + this.getBaseScenarioInfos().getJsName() + " senaryosu baslangic bilgilerini ekleme asamasinda, InfoBusManager ile ilgili bir problem var. Bos olmamali !.");
			}
			System.out.println("getSpaceWideRegistry().getInfoBusManager() == null !");
			System.exit(-1);
		}

		// PerformanceManager performanceManager = TlosSpaceWide.getSpaceWideRegistry().getPerformanceManagerReference();

		/**
		 * Senaryo içinde bulunan tüm işler bitene yahut, bir nedenle senaryo durdurulana kadar
		 * aşağıdaki döngü belli aralıklarla çalışacaktır.
		 * 
		 * @author serkan taş
		 *         22.09.2012
		 */

		SpcMonitor spcMonitor = new SpcMonitor(getJobQueue(), getJobQueueIndex());
		spcMonitor.setMyExecuter(new Thread(spcMonitor));

		setJSRealTime();

		return spcMonitor;

	}

	public void run() {

		SpcMonitor spcMonitor = preRunInit();

		// TODO job icin boyle ama senaryo icin nasil olacak? hs
		// sendEndInfo(Thread.currentThread().getName(), jobRuntimeProperties.getJobProperties());

		while (executionPermission) { // Senaryonun caslistirilmasi icin gerek sart !

			// Gelen deger saniye tipine çevriliyor.
			long chekInterval = getSpaceWideRegistry().getTlosSWConfigInfo().getSettings().getTlosFrequency().getFrequency() * 1000;

			try {
				// Senaryolarin bagimliligi icin burada bir kontrol koyduk ama su anda xml lerde kullanilmadigi icin etkisiz. Herzaman true donecek.
				// Senaryo PENDING statusune alindi ise herhangi bir isi baslatmaya calismamali. Normalde RUNNING de buraya geliyoruz.
				// TODO Performans Yoneticisi nin de fikrini almak lazim. Fakat bu asamada kaynak belli olmadigi icin sadece genel performans kontrolu yapilabilir.
				// Bunu sonraya birakiyoruz.
				if (!isSpcPermittedToExecute() /* || !isScenarioDependencyResolved() */) {
					Thread.sleep(chekInterval);
					continue;
				}

				passOnJobQueueForExecution(spcMonitor);

				// Bundan sonraki kisim islerin RUNNING durumunda olmasi halinde
				// isler icin yapilacaklari kapsiyor.

				// Persistent icin talep varsa, bunu yerine getirmek icin operasyonu diske bir dosyaya kaydet.
				if (getSpaceWideRegistry().getServerConfig().getServerParams().getIsPersistent().getValueBoolean() && !JobQueueOperations.persistJobQueue(getSpcAbsolutePath(), getJobQueue(), getJobQueueIndex())) {
					getMyLogger().error("Jobqueue persist error : scenario id : " + getSpcAbsolutePath());
					getMyLogger().error("Continue the execution with persistency feature disabled !");
				}

				try {

					// is kuyrugunun durumunu dokelim. Calisan, bekleyen ve
					// biten islerin sayisini tespit edelim.
					/**
					 * TODO Burada listenin loglanması sırasında,
					 * logun sadece ilgili senaryo yöneticisi loguna yönlendirilmesi gerekiyor.
					 * 
					 * @author serkan taş
					 *         20.09.2012
					 */
					if (SpaceWideRegistry.isDebug) {
						JobQueueOperations.dumpJobQueue(getSpcAbsolutePath(), getJobQueue());
					}

				} catch (Throwable t) {
					t.printStackTrace();
				}

				if (hasNewVersionOfJob()) {
					continue;
				}

				JobQueueResult jobQueueResult = JobQueueOperations.isJobQueueOver(getJobQueue());

				// Job kuyrugundaki islerin hepsi bitti mi, bitti ise LiveStateInfo yu set et.
				if (jobQueueResult.isJobQueueOver()) {
					/**
					 * Şimdilik istenen koşulu aşağıdaki şekilde kabul ettim ancak
					 * gerçek hayatta senaryo bitiş koşulu tanım sırasında verilecek
					 * ve burada o koşula uyumluluk kontrol edilecek.
					 * 
					 * @author serkan taş
					 */
					getLiveStateInfo().setStateName(StateName.FINISHED);
					getLiveStateInfo().setSubstateName(SubstateName.COMPLETED);
					getLiveStateInfo().setStatusName(StatusName.SUCCESS);

					break; // beklemeye gerek yok
				} else {
					if (isUpdateMySelfAfterMe()) {
						if (isNOKReasonIsNormalJobs(jobQueueResult)) {
							continue;
						} else {
							break;
						}
					}
				}

				Thread.sleep(chekInterval);

				// myLogger.info("     > "+ this.getBaseScenarioInfos().getJsName() + " icin islerin bitmesini bekliyoruz ...");
			} catch (Exception e) {
				getGlobalLogger().info("   > SPC Exception : " + getJobQueue());
				e.printStackTrace();
				getGlobalLogger().error("Terminating TlosSW due to critical error in Spc !");
				System.exit(-1);
			}
		}

		postRunClean(spcMonitor);
	}

	private void postRunClean(SpcMonitor spcMonitor) {

		/**
		 * We should disable monitor
		 */
		spcMonitor.getMyExecuter().interrupt();

		// Burada her senaryo ve her T < 1 iş kendi başının çaresine bakacak
		if (isUpdateMySelfAfterMe()) {
			handleGDIssues();
		}

		// Bu neden bu sekilde? Buraya zaten isler biterse geliyor.
		// isForced true ise kalan islerin zorla bitirilmesi isteniyor anlamina geliyor. Thread ler terminate ediliyor.
		// Kalan ne varsa temizliyoruz. Normalde kalmamasi lazim.
		/**
		 * Buraya sadece işler bitince değil, executionPermission = false yapılınca da giriliyor.
		 * isActiveThreads : true : kalan bütün joblar taranıp çalışanlar kapatılıyor
		 * isActiveThreads : false : kalan bütün joblar taranıyor, eğer en az bir tane çalışan var ise, bekliyor.
		 * 
		 * Bütün işler bitene kadar bekliyor.
		 * 
		 * @author serkan taş
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

		if (LiveStateInfoUtils.equalStates(getLiveStateInfo(), StateName.FINISHED, SubstateName.COMPLETED, StatusName.SUCCESS)) {
			getMyLogger().info("     > " + getBaseScenarioInfos().getJsName() + " icin isler bitti.");
			setJSRealTimeStopTime();
		} else {
			getMyLogger().info("     > " + getBaseScenarioInfos().getJsName() + " icin süreç durduruldu.");
		}

		if (getSpaceWideRegistry().getInfoBus() != null) {
			getSpaceWideRegistry().getInfoBus().addInfo(ScenarioMessageFactory.generateScenarioEnd(getSpcAbsolutePath(), getJobQueue().size()));
			getMyLogger().info("     > SPC ID : " + this.getSpcAbsolutePath() + ":" + this.getBaseScenarioInfos().getJsName() + " icin islerin bittigi konusunda InfoBusManager bilgilendirildi.");
		} else {
			getGlobalLogger().error("getSpaceWideRegistry().getInfoBusManager() == null !");
		}

	}

	private boolean isNOKReasonIsNormalJobs(JobQueueResult jobQueueResult) {

		if (jobQueueResult.getNumOfDailyJobsNotOver() != 0) {
			return true;
		}

		return false;
	}

	private void handleGDIssues() {

		String runId = getCurrentRunId();

		try {
			SpcUtils.updateSpcLookupTable(runId, getSpcFullPath(), getMyLogger());
			CpcUtils.startSpc(getSpcFullPath(), getMyLogger());
			setUpdateMySelfAfterMe(false);
		} catch (TlosFatalException e) {
			e.printStackTrace();
		} catch (TlosException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Yeni sürümü yüklenecek iş var mı ?
	 * Burada kontrol etmemiz gerekecek.
	 */

	private boolean hasNewVersionOfJob() {

		boolean retValue = false;

		synchronized (this) {

			Iterator<Job> jobsIterator = getJobQueue().values().iterator();

			while (jobsIterator.hasNext()) {
			
				Job scheduledJob = jobsIterator.next();
				JobProperties jobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();
				JobBaseType.Enum jobBaseType = jobProperties.getBaseJobInfos().getJobInfos().getJobBaseType();
				boolean isPeriodic = JobBaseType.PERIODIC.intValue() == jobBaseType.intValue();
				
				if(isPeriodic && (scheduledJob.getMyExecuter() == null || scheduledJob.getMyExecuter().getState() == Thread.State.WAITING) && scheduledJob.isUpdateMySelfAfterMe()) {
					
					// This job should terminate it self
					scheduledJob.setUpdateMySelfAfterMe(false);
					scheduledJob.setStopRepeatativity(true);
					
					if(scheduledJob.getMyExecuter() != null) {
						synchronized (scheduledJob.getMyExecuter()) {
							scheduledJob.getMyExecuter().notify();
						}
						
						while(scheduledJob.getMyExecuter().isAlive()) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					
					// This thread is waiting for SPC to handle GD for itself.
					String jobId = jobProperties.getID();
					JobProperties newJobProperties = DBUtils.getTlosJobPropertiesXml(Integer.parseInt(jobId), 0);
					
					if(newJobProperties == null) {
						getJobQueue().remove(jobId);
					} else {
						
						JobRuntimeProperties newJobRuntimeProperties = new JobRuntimeProperties();

						LiveStateInfoUtils.insertNewLiveStateInfo(newJobProperties, StateName.INT_PENDING, SubstateName.INT_IDLED);
						newJobRuntimeProperties.setJobProperties(newJobProperties);
						
						Job newJob = getMyJob(newJobRuntimeProperties);
						
						getJobQueue().put(jobId, newJob);
						SortType sortType = new SortType(jobId, newJob.getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJobPriority().intValue());
						getJobQueueIndex().add(sortType);
					}
					
					JobIndexUtils.reIndexJobQueue(this);
				}
			
			}
		}

		return retValue;
	}

	private void passOnJobQueueForExecution(SpcMonitor spcMonitor) throws TlosFatalException {

		// Senaryodaki herbir isi ele alalim.
		Iterator<SortType> jobQueueIndexIterator = getJobQueueIndex().iterator();

		while (executionPermission && jobQueueIndexIterator.hasNext()) {

			if (!isSpcPermittedToExecute()) {
				// İş listesi üzerinde dolaşırken senaryo beklemeye alınırsa ya da
				// uygulama state'i RUNNING dışında bir değer alırsa yapılan işlem
				// ansızın yarıda kesilip, sorumluluk üst döngüye bırakılır
				// serkan
				return;
			}

			// Bu senaryo icin olusturulmus Job kuyrugundaki siradaki Job in temel bilgilerini al.
			SortType sortType = jobQueueIndexIterator.next();

			Job scheduledJob = getJobQueue().get(sortType.getJobId());

			if (scheduledJob == null) {
				getGlobalLogger().error("  > HATA : Indexde bulunan " + sortType.getJobId() + " li iş Kuyrukta bulunaMAdı !!");
				getGlobalLogger().error("		İş kontrolden geçememiş olabilir. Lütfen log dosyalarını kontrol ediniz. ");
				getGlobalLogger().error("  > UYARI : Bir sonraki işe geçiyor.");
				continue;
			}

			JobRuntimeProperties jobRuntimeProperties = scheduledJob.getJobRuntimeProperties();
			JobProperties jobProperties = jobRuntimeProperties.getJobProperties();

			DependencyList dependentJobList = jobProperties.getDependencyList();

			// job in son state ini al.
			LiveStateInfo jobLiveStateInfo = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0);

			if (jobLiveStateInfo == null || jobLiveStateInfo.getStateName() == null) {
				getGlobalLogger().info("liveStateInfo = null");
				getGlobalLogger().error("  > HATA : Bir isin state bilgisi tamamen bos olamaz !! Kontrol ediniz. " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName());
				/*
				 * StateName FAILED sadece sistemsel basarisizlik durumlarinda kullanilir. Diger durumlarda asagidaki sekilde kullanilir. HS 24.09.2012
				 */
				insertLastStateInfo(scheduledJob, StateName.FINISHED, SubstateName.COMPLETED, StatusName.FAILED);
				// scheduledJob.sendStatusChangeInfo();
				continue;
			}

			try {

				if (!jobLiveStateInfo.getStateName().equals(StateName.PENDING)) {
					// Already executed
					continue;
				} else if (LiveStateInfoUtils.equalStates(jobLiveStateInfo, StateName.PENDING, SubstateName.IDLED)) {
					// job in PENDING olmasi halinde yapilacaklarin başladığı yer.
					// is calismaya hazir (PENDING/IDLED), fakat calistirma islemleri baslatilmamis bir job ise islemleri baslat.
					/*
					 * InfoQueue ya ilk uc state i koyamadigim icin burada bir kerede guncelleme yapiyorum. Eger infoQueue kullanabilirsek bunu kaldiracagiz ama is gorur bu hali.
					 * serkan : kaldırdım :)
					 */
					// if (scheduledJob.getFirstLoop()) {
					scheduledJob.sendFirstJobInfo(getSpcNativeFullPath().getFullPath(), jobProperties);
					// }

					String jobStartType = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDef().toString();

					if (jobStartType.equals(JobTypeDef.TIME_BASED.toString())) {

						boolean timeHasCome = TimeZoneCalculator.calculateExecutionTime(jobProperties.getTimeManagement());

						// isin planlanan calisma zamani gecti mi?
						if (timeHasCome) { // GECTI, calismasi icin gerekli islemlere baslansin.
							handleTransferRequestsOnDss(scheduledJob, dependentJobList);
						} else { // Zamani bekliyor ...
							// if (scheduledJob.getFirstLoop()) { /* status u ekle */
							insertLastStateInfo(scheduledJob, StateName.PENDING, SubstateName.IDLED, StatusName.BYTIME);
							// LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_PENDING, SubstateName.INT_IDLED, StatusName.INT_BYTIME);
							// scheduledJob.sendStatusChangeInfo();
							// }
						}

					} else if (jobStartType.equals(JobTypeDef.USER_BASED.toString())) {

						// if (scheduledJob.getFirstLoop()) { /* status u ekle */
						insertLastStateInfo(scheduledJob, StateName.PENDING, SubstateName.IDLED, StatusName.BYUSER);
						// LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_PENDING, SubstateName.INT_IDLED, StatusName.INT_BYUSER);
						// scheduledJob.sendStatusChangeInfo();
						// }

						// Ekrandan kullanici tercihi alinacak. Kullanici tercihi alininca StatusName WAITING yapilacak !!
						/*
						 * Boolean userChoice = true; if (userChoice) { jobRun = true; }
						 */

					} else if (jobStartType.equals(JobTypeDef.EVENT_BASED.toString())) {

						// if (scheduledJob.getFirstLoop()) { /* status u ekle */
						insertLastStateInfo(scheduledJob, StateName.PENDING, SubstateName.IDLED, StatusName.BYEVENT);
						// LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_PENDING, SubstateName.INT_IDLED, StatusName.INT_BYEVENT);
						// scheduledJob.sendStatusChangeInfo();
						// }

						boolean eventOccured = true; // TODO buraya olay kontrolu eklenecek.
						if (eventOccured) {
							handleTransferRequestsOnDss(scheduledJob, dependentJobList);
						}

					} else {

						getGlobalLogger().error("  > HATA : Bir isin baslama kosulu bilgisi USER/EVENT/TIME disinda birsey bos olamaz !! Kontrol ediniz. " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName());
						/**
						 * Eğer bu durum gerçekleşirse, ilgili iş FAIL edilip bir sonraki işe geçmeli
						 * 
						 * @author serkan taş 21.09.2012
						 */
						insertLastStateInfo(scheduledJob, StateName.FINISHED, SubstateName.COMPLETED, StatusName.FAILED);
						// LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.FINISHED, SubstateName.COMPLETED, StatusName.FAILED);
						// scheduledJob.sendStatusChangeInfo();
					}

				} else if (jobLiveStateInfo.getSubstateName().equals(SubstateName.READY) && (jobLiveStateInfo.getStatusName().equals(StatusName.WAITING) || jobLiveStateInfo.getStatusName().equals(StatusName.LOOKFOR_RESOURCE))) {
					// is calismaya hazir (IDLED) disinda bir statude, kuvvetle muhtemel READY beklemeye gecmis.
					handleTransferRequestsOnDss(scheduledJob, dependentJobList);

				} else {

					/**
					 * Eğer bu durum gerçekleşirse, ilgili iş FAIL edilip bir sonraki işe geçmeli
					 * 
					 * @author serkan taş 21.09.2012
					 */
					getGlobalLogger().error("  > HATA : Bir isin baslama kosulu bilgisi IDLED ve READY disinda birsey bos olamaz !! Kontrol ediniz. " + jobProperties.getBaseJobInfos().getJsName());
					insertLastStateInfo(scheduledJob, StateName.FINISHED, SubstateName.COMPLETED, StatusName.FAILED);
					// LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.FINISHED, SubstateName.COMPLETED, StatusName.FAILED);
					// scheduledJob.sendStatusChangeInfo();
					continue;
				}

			} catch (TlosException t) {
				getGlobalLogger().error(t.getMessage());
				insertLastStateInfo(scheduledJob, StateName.FINISHED, SubstateName.COMPLETED, StatusName.FAILED);
				// LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.FINISHED, SubstateName.COMPLETED, StatusName.FAILED);
				// scheduledJob.sendStatusChangeInfo();
				t.printStackTrace();
			}
			// job in PENDING olmasi halinde yapilacaklarin sonlandigi yer.
			// scheduledJob.setFirstLoop(false);

			try {
				if (spcMonitor.getMyExecuter().getState().equals(Thread.State.NEW) && !spcMonitor.getMyExecuter().isAlive()) {
					spcMonitor.getMyExecuter().start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private void handleTransferRequestsOnDss(Job scheduledJob, DependencyList dependentJobList) throws UnresolvedDependencyException, TransformCodeCreateException {

		JobProperties jobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();
		// job in son state ini al.
		// LiveStateInfo jobLiveStateInfo = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0);

		if (dependentJobList != null) {

			String dependencyExpression = dependentJobList.getDependencyExpression().trim().toUpperCase();
			Item[] dependencyArray = jobProperties.getDependencyList().getItemArray();

			if (isJobDependencyResolved(scheduledJob, dependencyExpression, dependencyArray)) {
				if (DssVisionaire.evaluateDss(scheduledJob).getResultCode() >= 0) {
					// if (DssFresh.transferPermission(scheduledJob)) {
					prepareAndTransform(scheduledJob);
				}
			} else {
				// Bu durumda job bagimliliklarindan beklenenler var demektir.
				// Son status WAITING degilse eklenmeli
				// if (!LiveStateInfoUtils.equalStates(jobLiveStateInfo, StateName.PENDING, SubstateName.READY, StatusName.WAITING)) {
				// LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.PENDING, SubstateName.READY, StatusName.WAITING);
				insertLastStateInfo(scheduledJob, StateName.PENDING, SubstateName.READY, StatusName.WAITING);
				// ( scheduledJob.sendStatusChangeInfo();
				// }
			}

		} else { // Herhangi bir bagimliligi yok !!
			if (DssVisionaire.evaluateDss(scheduledJob).getResultCode() >= 0) {
				// if (DssFresh.transferPermission(scheduledJob)) {
				prepareAndTransform(scheduledJob);
			}
		}

	}

	private synchronized boolean isJobDependencyResolved(Job ownerJob, String dependencyExpression, Item[] dependencyArray) throws UnresolvedDependencyException {
		return DependencyResolver.isJobDependencyResolved(getMyLogger(), ownerJob, dependencyExpression, dependencyArray, getCurrentRunId(), getJobQueue(), getSpcLookupTable());
	}

	private synchronized void executeJob(Job scheduledJob) {

		JobProperties jobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();

		getMyLogger().info("");
		getMyLogger().info("     > ID : " + jobProperties.getID() + ":" + jobProperties.getBaseJobInfos().getJsName() + " Calisma zamani gelmis, <Server> da calistirilacak. !");

		getMyLogger().info("");
		getMyLogger().info(scheduledJob.getJobRuntimeProperties().toString());
		getMyLogger().info("");

		/* RUNNING state i STAGE_IN subs ekle */
		// LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_RUNNING, SubstateName.INT_STAGE_IN, StatusName.INT_TIME_IN);
		insertLastStateInfo(scheduledJob, StateName.RUNNING, SubstateName.STAGE_IN, StatusName.TIME_IN);
		// scheduledJob.sendStatusChangeInfo();

		// is, dosya tasimasi gerektiriyorsa burada yapilacak !!

		/* RUNNING state i ON_RESOURCE subs ekle */
		/*
		 * Job in verildigi yerden bu isi yapmak daha dogru geldi.
		 * O yuzden kaldirdim ama simdilik dursun.
		 * XmlBeansTransformer.insertNewLiveStateInfo(scheduledJob.getJobRuntimeProperties().getJobProperties(),
		 * StateName.INT_RUNNING, SubstateName.INT_ON_RESOURCE, StatusName.INT_TIME_IN);
		 * scheduledJob.sendStatusChangeInfo();
		 */

		Thread starterThread = new Thread(scheduledJob);
		starterThread.setName(getSpcAbsolutePath());
		scheduledJob.setMyExecuter(starterThread);

		getMyLogger().info("     > ID : " + jobProperties.getID() + ":" + jobProperties.getBaseJobInfos().getJsName() + " isi icin <Server> da bir thread acildi !");
		getMyLogger().info("");

		scheduledJob.getMyExecuter().start();

		return;
	}

	private synchronized JobProperties getJobPropertiesWithSpecialParameters(JobRuntimeProperties jobRuntimeProperties) {
		return SpcUtils.getJobPropertiesWithSpecialParameters(jobRuntimeProperties);
	}

	private synchronized boolean transferJobToAgent(Job scheduledJob) {

		/**
		 * Biri bir diğerini içeiren iki nesen de aynı alanlar olması konusunda
		 * bir problem olablir mi ?
		 * 
		 * @author serkan taş
		 *         24.07.2013
		 *         TODO
		 */
		// agenta gonderilecek islerde gondermeden once JobRuntimeProperties icinde olup jobproperties icinde olmayan kisimlar jobproperties icine aliniyor
		JobProperties jobProperties = getJobPropertiesWithSpecialParameters(scheduledJob.getJobRuntimeProperties());

		String jsId = jobProperties.getID();

		String rxMessageKey = getTransferedJobKey(jobProperties.getAgentId(), jsId, jobProperties.getLSIDateTime());
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
		/**
		 * Neden ?
		 */

		// LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_RUNNING, SubstateName.INT_STAGE_IN, StatusName.INT_TIME_IN);
		insertLastStateInfo(scheduledJob, StateName.RUNNING, SubstateName.STAGE_IN, StatusName.TIME_IN);
		// scheduledJob.sendStatusChangeInfo();

		// is, dosya tasimasi gerektiriyorsa burada yapilacak !!

		boolean transferSuccess = TSWAgentJmxClient.sendJob(agent.getResource().getStringValue(), agent.getJmxTlsPort(), XmlUtils.getRxMessageXML(rxMessage), jmxAgentUser);

		if (!transferSuccess) { // Eger agent a transfer basarili olmadi ise onden ekledigimiz state leri silmemiz gerekiyor. HS
			jobProperties.getStateInfos().getLiveStateInfos().removeLiveStateInfo(0);
			jobProperties.getStateInfos().getLiveStateInfos().removeLiveStateInfo(0);
			getMyLogger().info("     > ID : " + jobProperties.getID() + ":" + jobProperties.getBaseJobInfos().getJsName() + " isi <Agent#" + jobProperties.getAgentId() + "#> da calistirilAMAdi !");
		} else
			getMyLogger().info("     > ID : " + jobProperties.getID() + ":" + jobProperties.getBaseJobInfos().getJsName() + " isi <Agent#" + jobProperties.getAgentId() + "#> da calistirildi !");

		return transferSuccess;
	}

	private synchronized void prepareAndTransform(Job scheduledJob) throws UnresolvedDependencyException, TransformCodeCreateException {

		JobProperties jobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();
		int agentId = jobProperties.getAgentId();

		int substateName = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName().intValue();

		StreamSource transformCode = null;
		// TODO bir kere registery ye yuklenmesi yeterli. Her seferinde yuklenmesine gerek yok. HS
		try {
			transformCode = DBUtils.getTransformXslCode();
		} catch (Exception e) {
			throw new TransformCodeCreateException(e);
		}
		// TODO bir kere registery ye yuklenmesi yeterli. Her seferinde yuklenmesine gerek yok. HS
		try {
			scheduledJob.setRequestedStream(transformCode);
		} catch (Exception e) {
			throw new TransformCodeCreateException(e);
		}

		// PARAMETRE atamalari burada yapilir.

		// LOCAL VE GLOBAL
		SpcLookupTable spcLookupTable = getSpcLookupTable();
		AgentManager agentManagerRef = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference();
		HashMap<Integer, ArrayList<Parameter>> parameterListAll = TlosSpaceWide.getSpaceWideRegistry().getAllParameters();
		ArrayList<Parameter> parameterList = parameterListAll.get(agentId);

		// TODO Statik ve dinamik job ayrimi yapabilirsek burada sadece dinamik joblara bu islem uygulanacak. HS
		JobProperties transformedjobProperties = ApplyXslt.transform(parameterList, jobProperties, transformCode);
		scheduledJob.getJobRuntimeProperties().setJobProperties(transformedjobProperties);

		// parametre gecisi burada yapilir !!

		InputParameterPassing parameterPassing = new InputParameterPassing(getSpaceWideRegistry(), getCurrentRunId());

		// 1.tip verilen xpath ile aktarim.
		parameterPassing.setInputParameter(scheduledJob.getJobRuntimeProperties().getJobProperties());
		// 2.tip fiziksel bagimlilik ile aktarim
		parameterPassing.setInputParameterViaDependency(getJobQueue(), scheduledJob, spcLookupTable);

		scheduledJob.sendEndInfo(getSpcNativeFullPath().getAbsolutePath(), scheduledJob.getJobRuntimeProperties().getJobProperties());
		// //////////////////\\\\\\\\\\\\\\\\\

		/* Secilen kaynak server ise server da degilse agent a aktararak calistir. */

		if (agentManagerRef.checkDestIfServer(agentId)) {
			executeJob(scheduledJob);
		} else {
			scheduledJob.getJobRuntimeProperties().getJobProperties().getTimeManagement().addNewJsRealTime().addNewStartTime().setTime(Calendar.getInstance());

			boolean transferSuccess = transferJobToAgent(scheduledJob);

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

	public void pause() {
		if (isPausable()) {
			getMyLogger().info("Spc " + getSpcAbsolutePath() + " Beklemeye alıyor...");
			getLiveStateInfo().setStateName(StateName.PENDING);
			getLiveStateInfo().setSubstateName(SubstateName.PAUSED);
			getMyLogger().info("Spc " + getSpcAbsolutePath() + " Beklemede !");
		}
	}

	public void resume() {
		if (isResumable()) {
			getMyLogger().info("Spc " + getSpcAbsolutePath() + " Bekleme durumundan çıkartıyor...");
			getLiveStateInfo().setStateName(StateName.RUNNING);
			getLiveStateInfo().setSubstateName(null);
			getMyLogger().info("Spc " + getSpcAbsolutePath() + " Bekleme durumundan çıkartıldı !");
		}
	}

	/**
	 * Henüz hiç bir yerde kullanılmıyor
	 * 
	 * @author Serkan Taş
	 * @return
	 * @throws TlosFatalException
	 */
	/*
	 * private synchronized boolean isScenarioDependencyResolved() throws TlosFatalException {
	 * return DependencyResolver.isScenarioDependencyResolved(getMyLogger(), getDependencyList(), getSpcId(), getBaseScenarioInfos().getJsName(), getInstanceId(), this.getLiveStateInfo(), getSpcLookupTable(), getSpaceWideRegistry().getInstanceLookupTable());
	 * }
	 */
	public boolean isRecovered() {
		return isRecovered;
	}

	public String getTransferedJobKey(int agentId, String jobId, String lsiDateTime) {
		String transferedJobKey = getCurrentRunId() + "|" + getSpcAbsolutePath() + "|" + jobId + "|" + agentId + "|" + lsiDateTime;

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
					scheduledJob.insertNewLiveStateInfo(StateName.FAILED, null, null, 555, "Agent a ulasilamiyor.");
					scheduledJob.insertNewLiveStateInfo(StateName.PENDING, SubstateName.IDLED, null, 111, "Is yeniden kurgulandi.");
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

	public void prepareJobsForManuelScenarioExecution() {
		Iterator<SortType> jobQueueIndexIterator = getJobQueueIndex().iterator();

		while (jobQueueIndexIterator.hasNext()) {

			// Bu senaryo icin olusturulmus Job kuyrugundaki siradaki Job in temel bilgilerini al.
			SortType sortType = jobQueueIndexIterator.next();

			Job scheduledJob = getJobQueue().get(sortType.getJobId());
			// JobRuntimeProperties jobRuntimeProperties = scheduledJob.getJobRuntimeProperties();
			// JobProperties jobProperties = jobRuntimeProperties.getJobProperties();
			insertLastStateInfo(scheduledJob, StateName.PENDING, SubstateName.IDLED, null);
			// LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.PENDING, SubstateName.IDLED, null);
			// scheduledJob.sendStatusChangeInfo();
		}
	}

}

/*
 * TlosFaz_V2.0
 * com.likya.tlos.jmx.mp.helper : ProcessInfoProvider.java
 * @author Serkan Taş
 * Tarih : Apr 6, 2009 2:19:17 PM
 */

package com.likya.tlossw.jmx.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.common.JobCommandTypeDocument.JobCommandType;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.cpc.model.RunInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.helpers.JobQueueOperations;
import com.likya.tlossw.core.spc.helpers.RunMapHelper;
import com.likya.tlossw.core.spc.helpers.SortType;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.jmx.JMXTLSServer;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.model.client.spc.SpcInfoTypeClient;
import com.likya.tlossw.model.client.spc.SpcLookUpTableTypeClient;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.model.path.BasePathType;
import com.likya.tlossw.model.path.TlosSWPathType;
import com.likya.tlossw.model.tree.GunlukIslerNode;
import com.likya.tlossw.model.tree.JobNode;
import com.likya.tlossw.model.tree.RunNode;
import com.likya.tlossw.model.tree.ScenarioNode;
import com.likya.tlossw.model.tree.TlosSpaceWideNode;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.utils.date.DateUtils;

public class LiveJSTreeInfoProvider implements LiveJSTreeInfoProviderMBean {

	@Override
	public String getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNbChanges() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	private boolean isTester(JmxUser jmxUser) {

		if (CommonConstantDefinitions.EXIST_MYDATA.equals(jmxUser.getViewRoleId())) {
			return true;
		}

		return false;
	}

	private boolean treeLevelComparer(String treePath, String spcId) {
		StringTokenizer treePathToken = new StringTokenizer(treePath, ".");
		StringTokenizer spcToken = new StringTokenizer(spcId, ".");

		if (spcId.indexOf(treePath + ".") != -1 && treePathToken.countTokens() + 1 == spcToken.countTokens()) {
			return true;
		}
		return false;
	}

	private SpcLookUpTableTypeClient retrieveSpcLookupTable(JmxUser jmxUser, String runId, String treePath) {

		HashMap<String, SpcInfoType> spcLookUpTable = null;

		// StringTokenizer treePathToken = new StringTokenizer(treePath, ".");
		// int treeLevel = treePathToken.countTokens();

		if (isTester(jmxUser)) {
			spcLookUpTable = TlosSpaceWide.getSpaceWideRegistry().getCpcTesterReference().getSpcLookupTable("" + jmxUser.getId()).getTable();
			runId = new String("" + jmxUser.getId());
		} else {
			spcLookUpTable = TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable().get(runId).getSpcLookupTable().getTable();
		}

		SpcLookUpTableTypeClient spcLookUpTableTypeClient = new SpcLookUpTableTypeClient();

		Iterator<String> keyIterator = spcLookUpTable.keySet().iterator();

		while (keyIterator.hasNext()) {

			String spcId = keyIterator.next();

			// StringTokenizer spcIdToken = new StringTokenizer(spcId, ".");

			SpcInfoType spcInfoType = spcLookUpTable.get(spcId);

			// if (spcIdToken.countTokens() <= treeLevel + 1 && treeLevelComparer(treePath, spcId)) {

			SpcInfoTypeClient spcInfoTypeClient = new SpcInfoTypeClient();
			spcInfoTypeClient.setSpcId(spcInfoType.getSpcId().getFullPath());

			spcInfoTypeClient.setJsName(spcInfoType.getScenario().getBaseScenarioInfos().getJsName());

			if (spcInfoType.getSpcReferance() != null) {
				// No spc defined for this scenario, it is NOT a BUG !
				spcInfoTypeClient.setNumOfJobs(JobQueueOperations.getNumOfJobs(spcInfoType.getSpcReferance().getJobQueue()));
				spcInfoTypeClient.setNumOfActiveJobs(JobQueueOperations.getNumOfActiveJobs(spcInfoType.getSpcReferance().getJobQueue()));

				spcInfoTypeClient.setPausable(spcInfoType.getSpcReferance().isPausable());
				spcInfoTypeClient.setResumable(spcInfoType.getSpcReferance().isResumable());
				spcInfoTypeClient.setStopable(spcInfoType.getSpcReferance().isStopable());
				spcInfoTypeClient.setStartable(spcInfoType.getSpcReferance().isStartable());
				spcInfoTypeClient.setNativeRunId(spcInfoType.getSpcReferance().getNativeRunId());
				spcInfoTypeClient.setCurrentRunId(spcInfoType.getSpcReferance().getCurrentRunId());

			} else {
				spcInfoTypeClient.setNativeRunId(runId);
				spcInfoTypeClient.setCurrentRunId(runId);
			}

			// spcLookUpTableTypeClient.getSpcInfoTypeClientList().put(spcId, spcInfoTypeClient);

			if (treePath == null || treeLevelComparer(treePath, spcInfoType.getSpcId().getFullPath())) {
				spcLookUpTableTypeClient.getSpcInfoTypeClientList().put(spcId, spcInfoTypeClient);
			}
			// }

		}

		return spcLookUpTableTypeClient;
	}

	public TlosSpaceWideNode createPlanNodeObject(JmxUser jmxUser) {

		TlosSpaceWideNode tlosSpaceWideNodeObject = new TlosSpaceWideNode();

		GunlukIslerNode gunlukIslerNode = new GunlukIslerNode();

		if (CommonConstantDefinitions.EXIST_MYDATA.equals(jmxUser.getViewRoleId())) {
			RunNode runNode = new RunNode("" + jmxUser.getId());
			gunlukIslerNode.getRunNodes().put(jmxUser.getId() + "", runNode);
		} else {
			for (String runId : TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable().keySet()) {
				RunNode runNode = new RunNode(runId);
				gunlukIslerNode.getRunNodes().put(runId, runNode);
			}
		}

		tlosSpaceWideNodeObject.setGunlukIslerNode(gunlukIslerNode);

		return tlosSpaceWideNodeObject;
	}

	private SpcLookUpTableTypeClient retrieveSpcLookupTable(JmxUser jmxUser, String treePath) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		SpcLookUpTableTypeClient spcLookUpTableTypeClient = null;

		if (isTester(jmxUser)) {
			spcLookUpTableTypeClient = retrieveSpcLookupTable(jmxUser, "" + jmxUser.getId(), treePath);
			if (spcLookUpTableTypeClient.getSpcInfoTypeClientList().size() > 0) {
				return spcLookUpTableTypeClient;
			}
		} else {

			HashMap<String, RunInfoType> runLookUpTable = TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable();
			Iterator<String> runKeyIterator = runLookUpTable.keySet().iterator();

			while (runKeyIterator.hasNext()) {
				String tmpPlanId = runKeyIterator.next();
				spcLookUpTableTypeClient = retrieveSpcLookupTable(jmxUser, tmpPlanId, treePath);
				if (spcLookUpTableTypeClient.getSpcInfoTypeClientList().size() > 0) {
					return spcLookUpTableTypeClient;
				}
			}
		}

		return spcLookUpTableTypeClient;
	}

	private ScenarioNode scenarioListContainsSpc(ArrayList<ScenarioNode> scenarioNodes, String spcId) {
		for (ScenarioNode scenarioNode : scenarioNodes) {
			if (scenarioNode.getSpcInfoTypeClient().getSpcId().equals(spcId)) {
				return scenarioNode;
			}
		}
		return null;
	}

	private ScenarioNode getDetails(JmxUser jmxUser, ScenarioNode treeNode) {

		ScenarioNode newScenarioNode = new ScenarioNode();
		newScenarioNode.setSpcInfoTypeClient(treeNode.getSpcInfoTypeClient());

		SpcLookUpTableTypeClient spcLookUpTableTypeClient = retrieveSpcLookupTable(jmxUser, treeNode.getSpcInfoTypeClient().getSpcId());

		for (String spcId : spcLookUpTableTypeClient.getSpcInfoTypeClientList().keySet()) {

			ScenarioNode innerScenarioNode = scenarioListContainsSpc(treeNode.getScenarioNodes(), spcId);

			TlosSWPathType tlosSWPathType = new TlosSWPathType(spcId);

			if (innerScenarioNode != null) {
				ScenarioNode newInnerScenarioNode = getDetails(jmxUser, innerScenarioNode);
				newInnerScenarioNode.setId(tlosSWPathType.getId().toString());
				// System.err.println("name : " + newInnerScenarioNode.getSpcInfoTypeClient().getJsName());
				newInnerScenarioNode.setName(newInnerScenarioNode.getSpcInfoTypeClient().getJsName());
				// System.err.println("label : " + generateLabel(newInnerScenarioNode));
				newInnerScenarioNode.setLabelText(generateLabel(newInnerScenarioNode));
				newScenarioNode.getScenarioNodes().add(newInnerScenarioNode);
			} else {
				SpcInfoTypeClient tmpSpcInfoTypeClient = spcLookUpTableTypeClient.getSpcInfoTypeClientList().get(spcId);
				ScenarioNode tmpScenarioNode = new ScenarioNode();
				tmpScenarioNode.setId(tlosSWPathType.getId().toString());
				// System.err.println("name 2 : " + tmpSpcInfoTypeClient.getJsName());
				tmpScenarioNode.setName(tmpSpcInfoTypeClient.getJsName());
				tmpScenarioNode.setSpcInfoTypeClient(tmpSpcInfoTypeClient);
				// System.err.println("label : " + generateLabel(tmpScenarioNode));
				tmpScenarioNode.setLabelText(generateLabel(tmpScenarioNode));
				newScenarioNode.getScenarioNodes().add(tmpScenarioNode);
			}

		}

		ArrayList<JobInfoTypeClient> jobInfoTypeClientList = retrieveJobListDetails(jmxUser, treeNode.getSpcInfoTypeClient().getSpcId(), false);
		for (JobInfoTypeClient jobInfoTypeClient : jobInfoTypeClientList) {
			JobNode jobNode = new JobNode();
			jobNode.setId(jobInfoTypeClient.getJobId());
			jobNode.setName(jobInfoTypeClient.getJobName());
			jobNode.setJobInfoTypeClient(jobInfoTypeClient);
			jobNode.setJobType(JobCommandType.Enum.forString(jobInfoTypeClient.getJobCommandType().toUpperCase()).intValue());
			jobNode.setLabelText(generateLabel(jobNode));
			newScenarioNode.getJobNodes().add(jobNode);
		}

		return newScenarioNode;
	}

	public ArrayList<JobInfoTypeClient> retrieveJobListDetails(JmxUser jmxUser, String spcFullPath, Boolean transformToLocalTime) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		ArrayList<JobInfoTypeClient> jobInfoTypeClientList = new ArrayList<JobInfoTypeClient>();

		SpcInfoType spcInfoType = null;

		if (isTester(jmxUser)) {
			spcInfoType = TlosSpaceWide.getSpaceWideRegistry().getCpcTesterReference().getSpcLookupTable(jmxUser.getId() + "").getTable().get(spcFullPath);
		} else {
			spcInfoType = RunMapHelper.findSpc(spcFullPath, TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable());
		}

		if (spcInfoType.getSpcReferance() == null) {
			return jobInfoTypeClientList;
		}

		Iterator<SortType> jobQueueIndexIterator = spcInfoType.getSpcReferance().getJobQueueIndex().iterator();
		while (jobQueueIndexIterator.hasNext()) {

			SortType sortType = jobQueueIndexIterator.next();
			Job scheduledJob = spcInfoType.getSpcReferance().getJobQueue().get(sortType.getJobId());

			JobRuntimeProperties jobRuntimeProperties = scheduledJob.getJobRuntimeProperties();
			JobInfoTypeClient jobInfoTypeClient = new JobInfoTypeClient();

			jobInfoTypeClient.setJobId(jobRuntimeProperties.getJobProperties().getID());
			jobInfoTypeClient.setJobName(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName());
			// jobInfoTypeClient.setJobKey(jobRuntimeProperties.getJobProperties().getID());
			jobInfoTypeClient.setJobCommand(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommand());
			jobInfoTypeClient.setJobCommandType(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommandType().toString());
			jobInfoTypeClient.setTreePath(jobRuntimeProperties.getAbsoluteJobPath());
			jobInfoTypeClient.setJobPath(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobPath());
			jobInfoTypeClient.setCurrentRunId(spcInfoType.getSpcReferance().getCurrentRunId());
			jobInfoTypeClient.setNativeRunId(jobRuntimeProperties.getJobProperties().getRunId());
			jobInfoTypeClient.setJobLogPath(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobLogPath());
			jobInfoTypeClient.setJobLogName(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobLogFile());
			jobInfoTypeClient.setoSystem(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getOSystem().toString());

			// TODO Geçici olarak tip dönüşümü yaptım.
			jobInfoTypeClient.setJobPriority(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobPriority().intValue());

			jobInfoTypeClient.setJobPlanTime(DateUtils.jobTimeToString(jobRuntimeProperties.getJobProperties().getTimeManagement().getJsPlannedTime().getStartTime().getTime(), transformToLocalTime));
			jobInfoTypeClient.setJobTimeOut(jobRuntimeProperties.getJobProperties().getTimeManagement().getJsTimeOut().getValueInteger().toString() + " " + jobRuntimeProperties.getJobProperties().getTimeManagement().getJsTimeOut().getUnit());

			// agentlarda calisan joblarin PlannedExecutionDate, CompletionDate ve WorkDuration alanlari set edilmediginden onlari jobRealTime kismindan set ediyoruz
			if (jobRuntimeProperties.getPlannedExecutionDate() == null && (jobRuntimeProperties.getJobProperties().getTimeManagement().getJsRealTime() != null)) {
				jobInfoTypeClient.setPlannedExecutionDate(DateUtils.jobRealTimeToString(jobRuntimeProperties.getJobProperties().getTimeManagement().getJsRealTime(), true, transformToLocalTime));

				// is hala calisiyorsa
				if (jobRuntimeProperties.getJobProperties().getTimeManagement().getJsRealTime().getStopTime() != null) {
					jobInfoTypeClient.setCompletionDate(DateUtils.jobRealTimeToString(jobRuntimeProperties.getJobProperties().getTimeManagement().getJsRealTime(), false, transformToLocalTime));
				}

			} else if (jobRuntimeProperties.getPlannedExecutionDate() != null) {
				jobInfoTypeClient.setPlannedExecutionDate(DateUtils.calendarToString(jobRuntimeProperties.getPlannedExecutionDate(), false));

				if (jobRuntimeProperties.getCompletionDate() != null) {
					jobInfoTypeClient.setCompletionDate(DateUtils.calendarToString(jobRuntimeProperties.getCompletionDate(), false));
				}
			}

			jobInfoTypeClient.setWorkDuration(DateUtils.getJobWorkDuration(jobRuntimeProperties.getJobProperties().getTimeManagement().getJsRealTime(), false));

			// LiveStateInfo listesindeki ilk eleman alinarak islem yapildi, yani guncel state i alindi
			jobInfoTypeClient.setOver(jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(StateName.FINISHED));
			jobInfoTypeClient.setLiveStateInfo(jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0));

			// TODO geçici olarak döüşüm yaptım ama xsd de problem var ????
			jobInfoTypeClient.setSafeRestart(jobRuntimeProperties.getJobProperties().getCascadingConditions().getJobSafeToRestart().toString());

			jobInfoTypeClient.setRetriable(jobRuntimeProperties.isRetriable());
			jobInfoTypeClient.setSuccessable(jobRuntimeProperties.isSuccessable());
			jobInfoTypeClient.setSkippable(jobRuntimeProperties.isSkippable());
			jobInfoTypeClient.setStopable(jobRuntimeProperties.isStopable());
			jobInfoTypeClient.setPausable(jobRuntimeProperties.isPausable());
			jobInfoTypeClient.setResumable(jobRuntimeProperties.isResumable());
			jobInfoTypeClient.setStartable(jobRuntimeProperties.isStartable());

			if (jobRuntimeProperties.getJobProperties().getDependencyList() != null) {
				ArrayList<Item> dependencyList = new ArrayList<Item>(Arrays.asList(jobRuntimeProperties.getJobProperties().getDependencyList().getItemArray()));
				Iterator<Item> dependencyListIterator = dependencyList.iterator();
				ArrayList<String> depenArrayList = new ArrayList<String>();
				while (dependencyListIterator.hasNext()) {
					depenArrayList.add(dependencyListIterator.next().getJsId());
				}
				jobInfoTypeClient.setJobDependencyList(depenArrayList);
			}
			jobInfoTypeClient.setAgentId(jobRuntimeProperties.getJobProperties().getAgentId());

			if (jobInfoTypeClient.getAgentId() > 0) {
				SWAgent agent = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference().getSwAgentsCache().get(jobInfoTypeClient.getAgentId() + "");

				jobInfoTypeClient.setResourceName(agent.getResource().getStringValue());
			}

			jobInfoTypeClientList.add(jobInfoTypeClient);
		}

		return jobInfoTypeClientList;

	}

	/**
	 * web ekranindaki senaryo ve joblarin oldugu agac render edilmeden once bu metodu cagirip guncel senaryo ve job bilgilerini aliyor
	 */
	public TlosSpaceWideNode getLiveTreeInfo(JmxUser jmxUser, TlosSpaceWideNode tlosSWReqNode) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		TlosSpaceWideNode tlosSWRespNode = createPlanNodeObject(jmxUser);

		GunlukIslerNode gunlukIslerNodeServer = tlosSWRespNode.getGunlukIslerNode();

		GunlukIslerNode gunlukIslerNodeClient = tlosSWReqNode.getGunlukIslerNode();

		/**
		 * ekranda run dugumu acilmissa yani altindaki kisimlar aciktaysa buraya giriyor,
		 * yoksa icinde run gelmedigi icin girmiyor
		 */

		if (gunlukIslerNodeClient != null) {

			HashMap<String, RunNode> serverRunNodes = gunlukIslerNodeServer.getRunNodes();

			HashMap<String, RunNode> clientRunNodes = gunlukIslerNodeClient.getRunNodes();

			for (String runId : gunlukIslerNodeClient.getRunNodes().keySet()) {

				RunNode clientRunNode = clientRunNodes.get(runId);

				RunNode serverRunNode = serverRunNodes.get(runId);

				if (serverRunNode == null) {
					// Bu durumda client'dan gelen run id server tarafta bulunamadı demek.
					// Bu ancak ve ancak server tarafta restart sonrası ya da GD sonrası
					// oluşabilecek bir durum. Böyler bir durumda direk client'a boş
					// bir nesne dönmesi gerekir.
					continue;
				}

				// Okudugumuz run'in altındaki senaryolari alip yeni TD'ye ekliyoruz

				HashMap<String, SpcInfoTypeClient> spcInfoTypeClientList = null;

				String selectedNodeId = new String(BasePathType.getRootPath() + "." + clientRunNode.getRunId());

				// run altindaki tum senaryolari spcInfoTypeClient turune donusturup, bunlari scenarioNode'un spcInfoTypeClient datasina atiyor.
				spcInfoTypeClientList = retrieveSpcLookupTable(jmxUser, runId, selectedNodeId).getSpcInfoTypeClientList();

				// Her bir scenarioNodu da run scenarioNodeMap'ine atiyor
				for (String spcId : spcInfoTypeClientList.keySet()) {
					SpcInfoTypeClient spcInfoTypeClient = spcInfoTypeClientList.get(spcId);

					ScenarioNode serverNode = new ScenarioNode();
					serverNode.setName(spcInfoTypeClient.getJsName());
					serverNode.setId(new TlosSWPathType(spcId).getId().toString());
					serverNode.setSpcInfoTypeClient(spcInfoTypeClient);
					serverNode.setLabelText(generateLabel(serverNode));
					serverRunNode.getScenarioNodeMap().put(spcId, serverNode);
				}

				// Simdi ise, run'ın altindaki senaryolarin detaylarini alacaz.

				for (String spcId : clientRunNode.getScenarioNodeMap().keySet()) {
					ScenarioNode myScenarioNode = clientRunNode.getScenarioNodeMap().get(spcId);

					ScenarioNode newScenarioNode = null;

					newScenarioNode = getDetails(jmxUser, myScenarioNode);
					newScenarioNode.setName(newScenarioNode.getSpcInfoTypeClient().getJsName());
					newScenarioNode.setId(new TlosSWPathType(spcId).getId().toString());
					newScenarioNode.setLabelText(generateLabel(newScenarioNode));
					serverRunNode.getScenarioNodeMap().put(spcId, newScenarioNode);
				}

			}
		}

		return tlosSWRespNode;
	}

	private String generateLabel(ScenarioNode scenarioNode) {

		String labelText = "";

		if (!scenarioNode.getSpcInfoTypeClient().getNativeRunId().equalsIgnoreCase(scenarioNode.getSpcInfoTypeClient().getCurrentRunId())) {
			labelText = scenarioNode.getLabelText() + "[NRID:" + scenarioNode.getSpcInfoTypeClient().getNativeRunId() + "][" + scenarioNode.getId() + "]";
		} else {
			labelText = scenarioNode.getLabelText() + "[" + scenarioNode.getId() + "]";
		}

		// System.err.println("labelText : " + labelText);
		return labelText;
	}

	private String generateLabel(JobNode jobNode) {

		String labelText = "";

		if (!jobNode.getJobInfoTypeClient().getNativeRunId().equalsIgnoreCase(jobNode.getJobInfoTypeClient().getCurrentRunId())) {
			labelText = jobNode.getLabelText() + "[NRID:" + jobNode.getJobInfoTypeClient().getNativeRunId() + "][" + jobNode.getId() + "]";
		} else {
			labelText = jobNode.getLabelText() + "[" + jobNode.getId() + "]";
		}

		// System.err.println("labelText : " + labelText);
		return labelText;
	}

}

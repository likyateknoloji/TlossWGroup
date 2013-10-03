package com.likya.tlossw.test.spc;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.BaseScenarioInfosDocument;
import com.likya.tlos.model.xmlbeans.data.BaseScenarioInfosDocument.BaseScenarioInfos;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.cpc.model.InstanceInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.exceptions.TlosFatalException;
import com.likya.tlossw.model.SpcLookupTable;
import com.likya.tlossw.model.path.ScenarioPathType;
import com.likya.tlossw.test.TestSuit;
import com.likya.tlossw.utils.LiveStateInfoUtils;

public class TestSpc extends TestSuit {

	static Logger myLogger = Logger.getLogger(TestSpc.class);

	public TestSpc() {
		super();
	}

	public static void main(String[] args) {
		new TestSpc().startTest();
	}

	public void startTest() {

		// JobProperties jobProperties = getJobPropertiesFromExist();
		String fileName1 = "src/demo1.xml";
		String fileName2 = "src/demo2.xml";

		ScenarioPathType spcId = new ScenarioPathType("TestSpc_" + System.currentTimeMillis());
		String instanceId = "TestInstance_" + +System.currentTimeMillis();

		JobProperties jobProperties1 = getJobPropertiesFromFile(fileName1);
		JobProperties jobProperties2 = getJobPropertiesFromFile(fileName2);

		JobRuntimeProperties jobRuntimeProperties1 = new JobRuntimeProperties();
		JobRuntimeProperties jobRuntimeProperties2 = new JobRuntimeProperties();

		jobRuntimeProperties1.setTreePath(spcId);
		jobRuntimeProperties1.setJobProperties(jobProperties1);
		jobRuntimeProperties1.setTreePath(spcId);

		jobRuntimeProperties2.setTreePath(spcId);
		jobRuntimeProperties2.setJobProperties(jobProperties2);
		jobRuntimeProperties2.setTreePath(spcId);

		ArrayList<JobRuntimeProperties> transformTable = new ArrayList<JobRuntimeProperties>();

		transformTable.add(jobRuntimeProperties1);
		transformTable.add(jobRuntimeProperties2);

		LiveStateInfoUtils.insertNewLiveStateInfo(jobRuntimeProperties1.getJobProperties(), StateName.PENDING, SubstateName.IDLED);
		LiveStateInfoUtils.insertNewLiveStateInfo(jobRuntimeProperties2.getJobProperties(), StateName.PENDING, SubstateName.IDLED);

		try {
			getSpaceWideRegistry().setEXistColllection(geteXistCollection());
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}

		try {

			getSpaceWideRegistry().setParameters(prepareParameterList());
			startInfoBusSystem(myLogger);

			//startPerformanceManager();

			startAgentManager();

			Spc mySpc = prepareSpc(instanceId, spcId.getFullPath(), transformTable);

			Thread myRunner = new Thread(mySpc);

			mySpc.setExecuterThread(myRunner);

			myRunner.start();

			boolean serveOnce = true;

			while (myRunner.isAlive()) {
				Thread.sleep(1000);
				if (serveOnce) {
					System.out.println("Hala canl� !");
					serveOnce = false;
				}
			}

			shutDownInfoBusSystem();

			System.out.println("Geberdi pezevenk sonunda !");

		} catch (TlosFatalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Spc prepareSpc(String instanceId, String spcId, ArrayList<JobRuntimeProperties> transformTable) throws TlosFatalException {

		Spc spc = new Spc(new ScenarioPathType(spcId), getSpaceWideRegistry(), transformTable);
		spc.setInstanceId(instanceId);

		SpcInfoType spcInfoType = new SpcInfoType();
		spcInfoType.setSpcReferance(spc);

		HashMap<String, SpcInfoType> spcLookupTable = new HashMap<String, SpcInfoType>();
		spcLookupTable.put(spcId, spcInfoType);
		
		SpcLookupTable spctbl = new SpcLookupTable();
		spctbl.setTable(spcLookupTable);

		HashMap<String, InstanceInfoType> myInstanceLookupTable = new HashMap<String, InstanceInfoType>();

		InstanceInfoType instanceInfoType = new InstanceInfoType();
		instanceInfoType.setInstanceId(instanceId);
		instanceInfoType.setSpcLookupTable(spctbl);

		myInstanceLookupTable.put(instanceId, instanceInfoType);

		getSpaceWideRegistry().setInstanceLookupTable(myInstanceLookupTable);

		LiveStateInfo myLiveStateInfo = LiveStateInfo.Factory.newInstance();

		myLiveStateInfo.setStateName(StateName.RUNNING);
		// myLiveStateInfo.setSubstateName(SubstateName.IDLED);
		spc.setLiveStateInfo(myLiveStateInfo);

		BaseScenarioInfos baseScenarioInfos = BaseScenarioInfosDocument.BaseScenarioInfos.Factory.newInstance();
		baseScenarioInfos.setJsName(spcId);

		spc.setBaseScenarioInfos(baseScenarioInfos);

		// Scenario tmpScenario = tmpScenarioList.get(scenarioId);
		//
		// spc.setJsName(tmpScenario.getBaseScenarioInfos().getJsName());
		// spc.setConcurrent(tmpScenario.getConcurrencyManagement().getConcurrent());
		// spc.setComment(tmpScenario.getBaseScenarioInfos().getComment());
		// spc.setInstanceId(instanceId);
		// // spc.setDependencyList(tmpScenario.getDependencyList());
		// // spc.setScenarioStatusList(tmpScenario.getScenarioStatusList());
		// spc.setUserName(null);
		// // spc.setUserName(tmpScenario.getID());
		//
		// tmpScenario.getConcurrencyManagement().setInstanceId(getSpaceWideRegistry().getTlosProcessData().getInstanceId());
		//
		// spc.setBaseScenarioInfos(tmpScenario.getBaseScenarioInfos());
		// spc.setDependencyList(tmpScenario.getDependencyList());
		// spc.setScenarioStatusList(tmpScenario.getScenarioStatusList());
		// spc.setAlarmPreference(tmpScenario.getAlarmPreference());
		// spc.setTimeManagement(tmpScenario.getTimeManagement());
		// spc.setAdvancedScenarioInfos(tmpScenario.getAdvancedScenarioInfos());
		// spc.setConcurrencyManagement(tmpScenario.getConcurrencyManagement());
		// spc.setLocalParameters(tmpScenario.getLocalParameters());

		if (!spc.initScenarioInfo() || spc.getJobQueue().size() == 0) {
			myLogger.warn(spcId + " isimli senaryo bilgileri y�klenemedi ya da is listesi bos geldi !");
			System.exit(-1);
		}
		return spc;

	}
}

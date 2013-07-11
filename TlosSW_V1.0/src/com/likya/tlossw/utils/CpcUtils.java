package com.likya.tlossw.utils;

import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;

public class CpcUtils {

	public static Scenario getScenario(TlosProcessData tlosProcessData, String instanceId) {
		
		Scenario scenario = Scenario.Factory.newInstance();
		scenario.setJobList(tlosProcessData.getJobList());

		tlosProcessData.getConcurrencyManagement().setInstanceId(instanceId);

		scenario.setBaseScenarioInfos(tlosProcessData.getBaseScenarioInfos());
		scenario.setDependencyList(tlosProcessData.getDependencyList());
		scenario.setScenarioStatusList(tlosProcessData.getScenarioStatusList());
		scenario.setAlarmPreference(tlosProcessData.getAlarmPreference());
		scenario.setTimeManagement(tlosProcessData.getTimeManagement());
		scenario.setAdvancedScenarioInfos(tlosProcessData.getAdvancedScenarioInfos());
		scenario.setConcurrencyManagement(tlosProcessData.getConcurrencyManagement());
		scenario.setLocalParameters(tlosProcessData.getLocalParameters());
		
		return scenario;
	}

	public static Scenario getScenario(Spc spc) {

		Scenario scenario = Scenario.Factory.newInstance();

		scenario.setBaseScenarioInfos(spc.getBaseScenarioInfos());
		scenario.setDependencyList(spc.getDependencyList());
		scenario.setScenarioStatusList(spc.getScenarioStatusList());
		scenario.setAlarmPreference(spc.getAlarmPreference());
		scenario.setTimeManagement(spc.getTimeManagement());
		scenario.setAdvancedScenarioInfos(spc.getAdvancedScenarioInfos());
		scenario.setConcurrencyManagement(spc.getConcurrencyManagement());
		scenario.setLocalParameters(spc.getLocalParameters());

		return scenario;
	}
	
	public static SpcInfoType getSpcInfo(Spc spc, String userId, String instanceId, Scenario tmpScenario) {

		LiveStateInfo myLiveStateInfo = LiveStateInfo.Factory.newInstance();

		myLiveStateInfo.setStateName(StateName.PENDING);
		myLiveStateInfo.setSubstateName(SubstateName.IDLED);
		spc.setLiveStateInfo(myLiveStateInfo);

		Thread thread = new Thread(spc);

		spc.setExecuterThread(thread);

		spc.setJsName(tmpScenario.getBaseScenarioInfos().getJsName());
		spc.setConcurrent(tmpScenario.getConcurrencyManagement().getConcurrent());
		spc.setComment(tmpScenario.getBaseScenarioInfos().getComment());
		spc.setUserId(userId);

		tmpScenario.getConcurrencyManagement().setInstanceId(instanceId);

		spc.setBaseScenarioInfos(tmpScenario.getBaseScenarioInfos());
		spc.setDependencyList(tmpScenario.getDependencyList());
		spc.setScenarioStatusList(tmpScenario.getScenarioStatusList());
		spc.setAlarmPreference(tmpScenario.getAlarmPreference());
		spc.setTimeManagement(tmpScenario.getTimeManagement());
		spc.setAdvancedScenarioInfos(tmpScenario.getAdvancedScenarioInfos());
		spc.setConcurrencyManagement(tmpScenario.getConcurrencyManagement());
		spc.setLocalParameters(tmpScenario.getLocalParameters());

		SpcInfoType spcInfoType = new SpcInfoType();

		spcInfoType.setJsName(spc.getBaseScenarioInfos().getJsName());
		spcInfoType.setConcurrent(spc.getConcurrencyManagement().getConcurrent());
		spcInfoType.setComment(spc.getBaseScenarioInfos().getComment());
		spc.setInstanceId(instanceId);
		spc.setUserId(userId);

		Scenario scenario = CpcUtils.getScenario(spc);

		spcInfoType.setScenario(scenario);
		spcInfoType.setSpcReferance(spc);

		return spcInfoType;
	}

}

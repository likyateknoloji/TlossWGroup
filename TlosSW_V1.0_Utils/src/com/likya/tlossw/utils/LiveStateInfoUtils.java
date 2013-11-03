package com.likya.tlossw.utils;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.JsStateDocument.JsState;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.ReturnCodeDocument.ReturnCode;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.utils.date.DateUtils;

public class LiveStateInfoUtils {

	public static boolean equalStates(LiveStateInfo liveStateInfo, int stateName, int substateName, int statusName) {

		boolean returnValue = false;

		StateName.Enum enumStateName;
		SubstateName.Enum enumSubStateName;
		StatusName.Enum enumStatusName;

		if (stateName > 0 && substateName > 0 && statusName > 0) {

			enumStateName = StateName.Enum.forInt(stateName);
			enumSubStateName = SubstateName.Enum.forInt(substateName);
			enumStatusName = StatusName.Enum.forInt(statusName);

			returnValue = equalStates(liveStateInfo, enumStateName, enumSubStateName, enumStatusName);
		}

		return returnValue;
	}

	public static boolean equalStates(LiveStateInfo liveStateInfo, int stateName, int substateName) {

		boolean returnValue = false;

		StateName.Enum enumStateName;
		SubstateName.Enum enumSubStateName;

		if (stateName > 0 && substateName > 0) {

			enumStateName = StateName.Enum.forInt(stateName);
			enumSubStateName = SubstateName.Enum.forInt(substateName);

			returnValue = equalStates(liveStateInfo, enumStateName, enumSubStateName);
		}

		return returnValue;
	}
	
	public static boolean equalStates(JobProperties jobProperties, StateName.Enum stateNameEnum) {
		return equalStates(jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0), stateNameEnum);
	}

	public static boolean equalStates(JobProperties jobProperties, StateName.Enum stateNameEnum, SubstateName.Enum substateNameEnum) {
		return equalStates(jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0), stateNameEnum, substateNameEnum);
	}

	public static boolean equalStates(JobProperties jobProperties, StateName.Enum stateNameEnum, SubstateName.Enum substateNameEnum, StatusName.Enum statusNameEnum) {
		return equalStates(jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0), stateNameEnum, substateNameEnum, statusNameEnum);
	}
	
	public static boolean equalStates(LiveStateInfo liveStateInfo, StateName.Enum stateNameEnum) {
		return liveStateInfo.getStateName().equals(stateNameEnum);
	}

	public static boolean equalStates(LiveStateInfo liveStateInfo, StateName.Enum stateNameEnum, SubstateName.Enum substateNameEnum) {
		return liveStateInfo.getStateName().equals(stateNameEnum) && liveStateInfo.getSubstateName().equals(substateNameEnum);
	}

	public static boolean equalStates(LiveStateInfo liveStateInfo, StateName.Enum stateNameEnum, SubstateName.Enum substateNameEnum, StatusName.Enum statusNameEnum) {	
		return liveStateInfo.getStateName() != null && liveStateInfo.getSubstateName() != null && liveStateInfo.getStatusName() != null && liveStateInfo.getStateName().equals(stateNameEnum) && liveStateInfo.getSubstateName().equals(substateNameEnum) && liveStateInfo.getStatusName().equals(statusNameEnum);
	}

	public static boolean equalStates(LiveStateInfo liveStateInfoSrc, LiveStateInfo liveStateInfoTrg) {
		return liveStateInfoSrc.getStateName().equals(liveStateInfoTrg.getStateName()) && liveStateInfoSrc.getSubstateName().equals(liveStateInfoTrg.getSubstateName()) && liveStateInfoSrc.getStatusName().equals(liveStateInfoTrg.getStatusName());
	}
	
	public static LiveStateInfo cloneLiveStateInfo(LiveStateInfo liveStateInfo) {

		LiveStateInfo cloneLiveStateInfo = LiveStateInfo.Factory.newInstance();

		if (liveStateInfo.getStateName() != null) {
			cloneLiveStateInfo.setStateName(liveStateInfo.getStateName());
		}
		if (liveStateInfo.getStatusName() != null) {
			cloneLiveStateInfo.setStatusName(liveStateInfo.getStatusName());
		}
		if (liveStateInfo.getSubstateName() != null) {
			cloneLiveStateInfo.setSubstateName(liveStateInfo.getSubstateName());
		}

		return cloneLiveStateInfo;
	}

	public static LiveStateInfo generateLiveStateInfo(int enumStateName, int enumSubstateName, int enumStatusName) {

		LiveStateInfo liveStateInfo = generateLiveStateInfo(enumStateName, enumSubstateName);

		if (enumStatusName > 0) {
			liveStateInfo.setStatusName(StatusName.Enum.forInt(enumStatusName));
		}

		return liveStateInfo;
	}

	public static LiveStateInfo generateLiveStateInfo(int enumStateName, int enumSubstateName) {

		LiveStateInfo liveStateInfo = LiveStateInfo.Factory.newInstance();

		liveStateInfo.setLSIDateTime(DateUtils.getServerW3CDateTime());

		if (enumStateName > 0) {
			liveStateInfo.setStateName(StateName.Enum.forInt(enumStateName));
		}
		if (enumSubstateName > 0) {
			liveStateInfo.setSubstateName(SubstateName.Enum.forInt(enumSubstateName));
		}

		return liveStateInfo;
	}

	public static LiveStateInfo generateLiveStateInfo(StateName.Enum stateNameEnum, SubstateName.Enum substateNameEnum, StatusName.Enum statusNameEnum) {

		LiveStateInfo liveStateInfo = generateLiveStateInfo(stateNameEnum, substateNameEnum);

		liveStateInfo.setStatusName(statusNameEnum);

		return liveStateInfo;
	}

	public static LiveStateInfo generateLiveStateInfo(StateName.Enum stateNameEnum, SubstateName.Enum substateNameEnum) {

		LiveStateInfo liveStateInfo = LiveStateInfo.Factory.newInstance();

		liveStateInfo.setLSIDateTime(DateUtils.getServerW3CDateTime());

		liveStateInfo.setStateName(stateNameEnum);
		liveStateInfo.setSubstateName(substateNameEnum);

		return liveStateInfo;
	}

	public static LiveStateInfo generateLiveStateInfo(StateName.Enum stateNameEnum) {

		LiveStateInfo liveStateInfo = LiveStateInfo.Factory.newInstance();

		liveStateInfo.setLSIDateTime(DateUtils.getServerW3CDateTime());

		liveStateInfo.setStateName(stateNameEnum);

		return liveStateInfo;
	}

	public static void insertNewLiveStateInfo(JobProperties jobProperties, StateName.Enum stateNameEnum, SubstateName.Enum substateNameEnum, StatusName.Enum statusNameEnum, int returnCode, String returnDecription) {

		synchronized (jobProperties) {

			LiveStateInfo liveStateInfo = generateLiveStateInfo(stateNameEnum, substateNameEnum, statusNameEnum);

			ReturnCode returnCodeObject = liveStateInfo.addNewReturnCode();
			returnCodeObject.setCode(returnCode);
			returnCodeObject.setDesc(returnDecription);

			jobProperties.getStateInfos().getLiveStateInfos().insertNewLiveStateInfo(0);
			jobProperties.getStateInfos().getLiveStateInfos().setLiveStateInfoArray(0, liveStateInfo);

		}

	}

	public static void insertNewLiveStateInfo(JobProperties jobProperties, StateName.Enum stateNameEnum, SubstateName.Enum substateNameEnum, StatusName.Enum statusNameEnum) {

		synchronized (jobProperties) {

			LiveStateInfo liveStateInfo = generateLiveStateInfo(stateNameEnum, substateNameEnum, statusNameEnum);

			jobProperties.getStateInfos().getLiveStateInfos().insertNewLiveStateInfo(0);
			jobProperties.getStateInfos().getLiveStateInfos().setLiveStateInfoArray(0, liveStateInfo);

		}

	}

	public static void insertNewLiveStateInfo(JobProperties jobProperties, StateName.Enum stateNameEnum, SubstateName.Enum substateNameEnum) {

		synchronized (jobProperties) {

			LiveStateInfo liveStateInfo = generateLiveStateInfo(stateNameEnum, substateNameEnum);

			jobProperties.getStateInfos().getLiveStateInfos().insertNewLiveStateInfo(0);
			jobProperties.getStateInfos().getLiveStateInfos().setLiveStateInfoArray(0, liveStateInfo);

		}

	}

	public static LiveStateInfo insertNewLiveStateInfo(JobProperties jobProperties, int enumStateName, int enumSubstateName, int enumStatusName) {

		synchronized (jobProperties) {

			LiveStateInfo liveStateInfo = generateLiveStateInfo(enumStateName, enumSubstateName, enumStatusName);

			if(jobProperties.getStateInfos() == null || jobProperties.getStateInfos().getLiveStateInfos() == null) {
				System.err.println(jobProperties.toString());
			}
			
			LiveStateInfo returnInfo = jobProperties.getStateInfos().getLiveStateInfos().insertNewLiveStateInfo(0);
			jobProperties.getStateInfos().getLiveStateInfos().setLiveStateInfoArray(0, liveStateInfo);
			
			return returnInfo;

		}

	}

	public static void insertNewLiveStateInfo(JobProperties jobProperties, int enumStateName, int enumSubstateName) {

		synchronized (jobProperties) {

			LiveStateInfo liveStateInfo = generateLiveStateInfo(enumStateName, enumSubstateName, 0);

			jobProperties.getStateInfos().getLiveStateInfos().insertNewLiveStateInfo(0);
			jobProperties.getStateInfos().getLiveStateInfos().setLiveStateInfoArray(0, liveStateInfo);

		}

	}
	
	public static void insertNewLiveStateInfo(JobProperties jobProperties, LiveStateInfo liveStateInfo) {
		synchronized (jobProperties) {

			liveStateInfo.setLSIDateTime(DateUtils.getServerW3CDateTime());
			jobProperties.getStateInfos().getLiveStateInfos().insertNewLiveStateInfo(0);
			jobProperties.getStateInfos().getLiveStateInfos().setLiveStateInfoArray(0, liveStateInfo);

		}

	}

	public static boolean equalStates(LiveStateInfo liveStateInfo, JsState jsState) {
		return (jsState.getStateName() == null || liveStateInfo.getStateName().equals(jsState.getStateName())) && (jsState.getSubstateName() == null || liveStateInfo.getSubstateName().equals(jsState.getSubstateName())) && (jsState.getStatusName() == null || liveStateInfo.getStatusName().equals(jsState.getStatusName()));
	}

}

package com.likya.tlossw.core.spc.helpers;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.error.ErrDetailDocument.ErrDetail;
import com.likya.tlos.model.xmlbeans.error.ErrLevelDocument.ErrLevel;
import com.likya.tlos.model.xmlbeans.error.ErrResultDocument.ErrResult;
import com.likya.tlos.model.xmlbeans.error.ErrTypeDocument.ErrType;
import com.likya.tlos.model.xmlbeans.error.SWErrorDocument.SWError;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.db.utils.ErrorDbUtils;
import com.likya.tlossw.infobus.helper.TlosSWError;
import com.likya.tlossw.utils.date.DateUtils;

public class SWErrorOperations {
	
	public static SWError errorMsgForOutJmxAvailable(SWAgent agent) {
		
		SWError swError = SWError.Factory.newInstance();
		
		swError.setResource(agent.getResource());
		swError.setAgentId(agent.getId());
		swError.setLSIDateTime(DateUtils.getW3CDateTime());
		swError.setErrLevel(ErrLevel.APPLICATION_LEVEL);
		swError.setErrType(ErrType.INTERACTION_AND_NETWORK_ERRORS);
		swError.setErrDetail(ErrDetail.JMX_COMMUNICATION_PROTOCOL);
		swError.setErrResult(ErrResult.AGENT_OUT_JMX_UNAVAILABLE);
		
		return swError;
	}
	
	public static void sendErrMsgForOutJmxAvailable(SWAgent agent) {
		SWError error = errorMsgForOutJmxAvailable(agent);
		TlosSWError tlosSWError = new TlosSWError();
		tlosSWError.setSwError(error);
		
		if (tlosSWError != null) TlosSpaceWide.getSpaceWideRegistry().getInfoBus().addInfo(tlosSWError);
	}
	
	public static SWError errorMsgForInJmxAvailable(SWAgent agent) {
		
		SWError swError = SWError.Factory.newInstance();
		
		swError.setResource(agent.getResource());
		swError.setAgentId(agent.getId());
		swError.setLSIDateTime(DateUtils.getW3CDateTime());
		swError.setErrLevel(ErrLevel.APPLICATION_LEVEL);
		swError.setErrType(ErrType.INTERACTION_AND_NETWORK_ERRORS);
		swError.setErrDetail(ErrDetail.JMX_COMMUNICATION_PROTOCOL);
		swError.setErrResult(ErrResult.AGENT_IN_JMX_UNAVAILABLE);
		
		return swError;
	}
	
	public static void sendErrMsgForInJmxAvailable(SWAgent agent) {
		SWError error = errorMsgForInJmxAvailable(agent);
		TlosSWError tlosSWError = new TlosSWError();
		tlosSWError.setSwError(error);
		
		TlosSpaceWide.getSpaceWideRegistry().getInfoBus().addInfo(tlosSWError);
	}

	public static SWError errorMsgForJmxAvailable(SWAgent agent) {
		
		SWError swError = SWError.Factory.newInstance();
		
		swError.setResource(agent.getResource());
		swError.setAgentId(agent.getId());
		swError.setLSIDateTime(DateUtils.getW3CDateTime());
		swError.setErrLevel(ErrLevel.APPLICATION_LEVEL);
		swError.setErrType(ErrType.INTERACTION_AND_NETWORK_ERRORS);
		swError.setErrDetail(ErrDetail.JMX_COMMUNICATION_PROTOCOL);
		swError.setErrResult(ErrResult.AGENT_JMX_UNAVAILABLE);
		
		return swError;
	}
	
	public static void sendErrMsgForJmxAvailable(SWAgent agent) {
		SWError error = errorMsgForJmxAvailable(agent);
		TlosSWError tlosSWError = new TlosSWError();
		tlosSWError.setSwError(error);
		
		TlosSpaceWide.getSpaceWideRegistry().getInfoBus().addInfo(tlosSWError);
	}
	
	public static boolean insertError(TlosSWError tlosSWError) {
		boolean insertResult = ErrorDbUtils.insertSWError(tlosSWError.getSwError());
		return insertResult;
	}

}

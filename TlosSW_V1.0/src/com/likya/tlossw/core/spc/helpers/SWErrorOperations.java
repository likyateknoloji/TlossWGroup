package com.likya.tlossw.core.spc.helpers;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.error.ErrDetailDocument.ErrDetail;
import com.likya.tlos.model.xmlbeans.error.ErrLevelDocument.ErrLevel;
import com.likya.tlos.model.xmlbeans.error.ErrResultDocument.ErrResult;
import com.likya.tlos.model.xmlbeans.error.ErrTypeDocument.ErrType;
import com.likya.tlos.model.xmlbeans.error.SWErrorDocument.SWError;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.cpc.Cpc;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.db.utils.ErrorDbUtils;
import com.likya.tlossw.exceptions.UnresolvedDependencyException;
import com.likya.tlossw.infobus.helper.TlosSWError;
import com.likya.tlossw.utils.date.DateUtils;

public class SWErrorOperations {

	public static SWError errorMsgForOutJmxAvailable(SWAgent agent) {

		SWError swError = SWError.Factory.newInstance();

		swError.setResource(agent.getResource());
		swError.setAgentId(agent.getId());
		swError.setLSIDateTime(DateUtils.getServerW3CDateTime());
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

		if (tlosSWError != null)
			TlosSpaceWide.getSpaceWideRegistry().getInfoBus().addInfo(tlosSWError);
	}

	public static SWError errorMsgForInJmxAvailable(SWAgent agent) {

		SWError swError = SWError.Factory.newInstance();

		swError.setResource(agent.getResource());
		swError.setAgentId(agent.getId());
		swError.setLSIDateTime(DateUtils.getServerW3CDateTime());
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
		swError.setLSIDateTime(DateUtils.getServerW3CDateTime());
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

	public static void logErrorForJob(Logger logger, String jsName, String itemJsName, String itemJsPath, String instanceId, String spcId) throws UnresolvedDependencyException {

		logger.error("     > Genel bagimlilik tanimi yapilan :");
		logger.error("     > Ana is adi : " + jsName);
		logger.error("     > Bagli is : " + itemJsName + " tanimli mi? Tanimli ise bagimlilik ile ilgili bir problem olabilir! (Problem no:1045)");
		logger.error("     >    Dizin : " + Cpc.getRootPath() + "." + instanceId + "." + itemJsPath);
		logger.error("     > 	Yukaridaki is  " + spcId + " adli senaryoda bulunamadi !");
		logger.error("     > Uygulama sona eriyor !");
		logger.info("     > Bagimlilikla ilgili bir problemden dolayi uygulama sona eriyor !");
		throw new UnresolvedDependencyException("     > Bagimlilikla ilgili bir problemden dolayi uygulama sona eriyor !");
	}

	public static void logErrorForSpcInfoType(Logger logger, String jsName, String itemJsPath, String instanceId, String treePath, HashMap<String, SpcInfoType> spcLookupTable) throws UnresolvedDependencyException {

		logger.error("     > Genel bagimlilik tanimi yapilan senaryo bulunamadi : " + Cpc.getRootPath() + "." + instanceId + "." + itemJsPath);
		logger.error("     > Ana is adi : " + jsName);
		logger.error("     > Ana senaryo yolu : " + treePath);
		logger.error("     > Uygulama sona eriyor !");
		logger.info("     > Bagimlilikla ilgili bir problemden dolayi uygulama sona eriyor !");
		Cpc.dumpSpcLookupTable(instanceId, spcLookupTable);
		throw new UnresolvedDependencyException("     > Genel bagimlilik tanimi yapilan senaryo bulunamadi : " + Cpc.getRootPath() + "." + instanceId + "." + itemJsPath);
	}

	public static void logErrorForItemJsId(Logger logger, String jsName, String itemJsName, String treePath, String Id) throws UnresolvedDependencyException {
	
		logger.error("     > Yerel bagimlilik tanimi yapilan is bulunamadi : " + itemJsName);
		logger.error("     > Ana is adi : " + jsName);
		logger.error("     > Ana is Id : " + Id);
		logger.error("     > Ana senaryo yolu : " + treePath);
		logger.info("     > Bagimlilikla ilgili bir problemden dolayi uygulama sona eriyor !");
		throw new UnresolvedDependencyException("     > Yerel bagimlilik tanimi yapilan is bulunamadi : " + itemJsName);
	}
}

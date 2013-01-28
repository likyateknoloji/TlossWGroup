package com.likya.tlossw.model;

import java.io.Serializable;
import java.util.HashMap;

public class MessagesCodeMapping implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int ENGINE_INSTANCE_ABSENT = 1000;
	public static final int SCENARIO_DUPLICATEJOBNAME = 2000;
	public static final int DB_FREEJOB_INSERT_ERROR = 3000;
	public static final int JMX_USER_AUTHORIZATION_ERROR = 4000;
	public static final int JMX_ERROR = 5000;
	public static final int ENGINE_FREEJOB_INSERT_ERROR = 6000;
	
	public static final int ENGINE_FREEJOB_INSERT_SUCESS = 7000;

	private static final HashMap<Integer,TlosGuiMessage> messageCodes = new HashMap<Integer, TlosGuiMessage>(){
		private static final long serialVersionUID = 1L;
		{
			put(ENGINE_INSTANCE_ABSENT, new TlosGuiMessage("tlos.error.engine.instanceAbsent", TlosGuiMessageType.ERROR));
			put(SCENARIO_DUPLICATEJOBNAME, new TlosGuiMessage("tlos.error.scenario.dublicateJobName", TlosGuiMessageType.ERROR));
			put(DB_FREEJOB_INSERT_ERROR, new TlosGuiMessage("tlos.error.db.freeJob.insertError", TlosGuiMessageType.ERROR));
			put(JMX_ERROR, new TlosGuiMessage("tlos.error.jmx", TlosGuiMessageType.ERROR));
			put(JMX_USER_AUTHORIZATION_ERROR, new TlosGuiMessage("tlos.error.jmx.user.authorization", TlosGuiMessageType.ERROR));
			put(ENGINE_FREEJOB_INSERT_ERROR, new TlosGuiMessage("tlos.success.engine.freeJob.insert", TlosGuiMessageType.ERROR));
			
			put(ENGINE_FREEJOB_INSERT_SUCESS, new TlosGuiMessage("tlos.success.engine.freeJob.insert", TlosGuiMessageType.SUCCESS));
		}
	};
	
	public static HashMap<Integer, TlosGuiMessage> getErrorCodes() {
		return messageCodes;
	}
	
	public static TlosGuiMessage fetchTlosGuiMessage(int tlosGuiCode) {
		return messageCodes.get(new Integer(tlosGuiCode));
	}
	
	
}

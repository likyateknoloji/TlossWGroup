/*
 * TlosFaz_V2.0
 * com.likya.tlos.utils : PersistenceUtils.java
 * @author Serkan Ta�
 * Tarih : 31.Mar.2010 22:39:32
 */

package com.likya.tlossw.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.state.GlobalStateDefinitionDocument.GlobalStateDefinition;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.infobus.helper.InfoType;
import com.likya.tlossw.infobus.helper.mail.TlosMail;
import com.likya.tlossw.model.engine.EngineeConstants;


public class PersistenceUtils {

	private static Logger logger = SpaceWideRegistry.getGlobalLogger();
	
	private static String tempFolder = EngineeConstants.tempDir;
	
	public static final String persistSWRegisteryFile = "SWRegistery.recover";
	public static final String persistGlobalStatesFile = "GlobalStates.recover";
	public static final String persistAgentCacheFile = "AgentCache.recover";
	public static final String persistMailQueueFile = "MailQueue.recover";
	public static final String persistInfoQueueFile = "InfoQueue.recover";
	
	private static Object recover(String fileName) {

		FileInputStream fis = null;
		ObjectInputStream in = null;

		Object input;
		
		try {
			fis = new FileInputStream(System.getProperty(tempFolder) + "/" + fileName);
			in = new ObjectInputStream(fis);
			input = in.readObject();

			in.close();
			
		} catch (FileNotFoundException fnf) {
			return null;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		return input;
	}
	
	private static boolean persist(String fileName, Object data) {

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			File file = new File(System.getProperty(tempFolder) + "/" + fileName);
			fos = new FileOutputStream(file);  

			out = new ObjectOutputStream(fos);
			out.writeObject(data);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return true;

	}
	
	public static boolean persistGlobalStateDefinition(Object data) {

		
		if (data == null) {
			logger.fatal("Global State Tan�m� bo� olmamal� !");
			logger.fatal("Program sona erdi !");
			return false;
		}
		
		persist(persistGlobalStatesFile, data);
		
		return true;

	}
	
	public static boolean persistAgentCache(Object data) {

		if (data == null) {
			logger.fatal("AgentCache bo� olmamal� !");
			logger.fatal("Program sona erdi !");
			return false;
		}
		
		persist(persistAgentCacheFile, data);
		
		return true;

	}
	
	public static boolean persistMailQueue(Object data) {

		persist(persistMailQueueFile, data);
		
		return true;

	}
	
	public static boolean persistInfoQueue(Object data) {
		
		persist(persistInfoQueueFile, data);
		
		return true;

	}
	
	public static boolean persistSWRegistry() {

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(System.getProperty(tempFolder) + "/" + persistSWRegisteryFile);
			out = new ObjectOutputStream(fos);
			out.writeObject(new Long(SpaceWideRegistry.getInstance().getScenarioReadTime()));
			out.writeObject(SpaceWideRegistry.getInstance());
			out.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return true;
	}

	public static boolean recoverSWRegistry() {

		logger.info("Recovering job queue...");

		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			
			fis = new FileInputStream(System.getProperty(tempFolder) + "/" + persistSWRegisteryFile);
			in = new ObjectInputStream(fis);
			
			Object input = in.readObject();
			long scenarioReadTime = (Long)input;
			
			input = in.readObject();
			
			SpaceWideRegistry.setInstance((SpaceWideRegistry)input);
			SpaceWideRegistry.getInstance().setScenarioReadTime(scenarioReadTime);
			
			in.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		logger.info("Job queue recovered !");

		return true;
	}
	
	public static boolean recoverGlobalStateDefinition() {

		logger.info("Recovering GlobalStateDefinitions...");

		GlobalStateDefinition globalStateDefinition = (GlobalStateDefinition) recover(persistGlobalStatesFile);
		
		if(globalStateDefinition == null) {
			return false;
		}
		
		SpaceWideRegistry.getInstance().setGlobalStateDefinition(globalStateDefinition);
		
		logger.info("GlobalStateDefinition recovered !");
		
		return true;
	}
	
	public static HashMap<String, SWAgent> recoverAgentCache() {

		logger.info("Recovering Agent cache...");

		@SuppressWarnings("unchecked")
		HashMap<String, SWAgent> agentCache = (HashMap<String, SWAgent>) recover(persistAgentCacheFile);
		
		if(agentCache == null) {
			return null;
		}
		
		logger.info("AgentCache recovered !");
		
		return agentCache;
	}

	public static ArrayList<TlosMail> recoverMailQueue() {

		logger.info("Recovering Mail Queue...");

		@SuppressWarnings("unchecked")
		ArrayList<TlosMail> mailQueue = (ArrayList<TlosMail>) recover(persistMailQueueFile);
		
		logger.info("Mail Queue recovered !");
		
		return mailQueue;
	}
	
	public static ArrayList<InfoType> recoverInfoQueue() {

		logger.info("Recovering Info Queue...");

		@SuppressWarnings("unchecked")
		ArrayList<InfoType> infoQueue = (ArrayList<InfoType>) recover(persistInfoQueueFile);
		
		logger.info("Info Queue recovered !");
		
		return infoQueue;
	}

	public static boolean isAnyPersistFileExists() {
		
		if(!FileUtils.checkTempFile(persistSWRegisteryFile, tempFolder) || !FileUtils.checkTempFile(persistGlobalStatesFile, tempFolder) || !FileUtils.checkTempFile(persistAgentCacheFile, tempFolder) || !FileUtils.checkTempFile(persistMailQueueFile, tempFolder) || !FileUtils.checkTempFile(persistInfoQueueFile, tempFolder)) {
			return true;
		}
		
		return false;
	}

	public static boolean isMainPersistFilesExists() {
		
		if(FileUtils.checkTempFile(persistSWRegisteryFile, tempFolder)) {
			return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<String, Job> recoverTempFiles(String fileName) {

		HashMap<String, Job> jobQueue = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(System.getProperty("tlos.tmpdir") + "/" + fileName);
			in = new ObjectInputStream(fis);
			Object input = in.readObject();
			jobQueue = new HashMap<String, Job>();
			jobQueue.putAll((HashMap<String, Job>) input);
			in.close();
			
		} catch (FileNotFoundException fnf) {
			return jobQueue;
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return jobQueue;
	}
}

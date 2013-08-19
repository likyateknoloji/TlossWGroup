package com.likya.tlossw.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.xmldb.api.base.Collection;

import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument.TlosConfigInfo;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.state.GlobalStateDefinitionDocument.GlobalStateDefinition;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.agents.AgentManager;
import com.likya.tlossw.core.cpc.Cpc;
import com.likya.tlossw.core.cpc.CpcTester;
import com.likya.tlossw.core.cpc.model.InstanceInfoType;
import com.likya.tlossw.core.spc.helpers.LikyaDayKeeper;
import com.likya.tlossw.exceptions.TlosException;
import com.likya.tlossw.infobus.servers.MailServer;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.nagios.NagiosServer;
import com.likya.tlossw.perfmng.PerformanceManager;
import com.likyateknoloji.xmlServerConfigTypes.ServerConfigDocument.ServerConfig;

public class SpaceWideRegistry implements GlobalRegistry, Serializable {
	
	private static final long serialVersionUID = 579016499754067105L;

	private transient static SpaceWideRegistry spaceWideRegistry;
	
	public static boolean isDebug = true;

	private boolean FIRST_TIME = true;
	
	private transient TlosSpaceWide spaceWideReference;
	
	private boolean isRecovered = false;
	
	private boolean isUserSelectedRecover = false;
	
	private transient ResourceBundle applicationResources;
	
	private transient Collection eXistColllection;

	private String dbUri;
	
	private String xQueryModuleUrl;
	
	private String xmlsUrl;
	
	private transient Cpc cpcReference;

	private transient CpcTester cpcTesterReference;

	private static Logger globalLogger;
	
	private transient GlobalStateDefinition globalStateDefinition;
	
	private ArrayList<Parameter> parameters = null;

	private HashMap<Integer, ArrayList<Parameter>> allParameters = null;
	
	private long scenarioReadTime;
	
	private transient TlosProcessData tlosProcessData;
	
	private transient TlosConfigInfo tlosSWConfigInfo;
	
	private transient ServerConfig serverConfig;
	
	private transient MailServer mailServer;
	
	private transient NagiosServer nagiosServer;
	
	/**
	 * InfoBusManager Reference
	 */
	private transient InfoBus infoBus;
	
	/**
	 * DayKeeper referance
	 */

	private transient LikyaDayKeeper dayKeeperReference;
	
	private boolean isGünDönümüPeryodPassed = false;

	private boolean isSolsticePassed = false;

	private transient AgentManager agentManagerReference;
	
	private transient PerformanceManager performanceManagerReference;
	
	private HashMap<String, InstanceInfoType> instanceLookupTable = new HashMap<String, InstanceInfoType>();
	
	private transient Server httpServer;
	
	private JmxUser jmxUser;
	
	private boolean waitConfirmOfGUI = false;
	
	private boolean restartAllScenarios = false;
	
	public static SpaceWideRegistry getInstance() {
		if(spaceWideRegistry == null) {
			spaceWideRegistry = new SpaceWideRegistry(globalLogger);
		} 
		return spaceWideRegistry;
	}
	
	public static void setInstance(Object object) throws TlosException {
		if(object instanceof SpaceWideRegistry) {
			spaceWideRegistry = (SpaceWideRegistry) object;
		} else {
			throw new TlosException("Invalid object type for setter !");
		}
	}
	
	private SpaceWideRegistry(Logger globalLogger) {
		super();
		SpaceWideRegistry.globalLogger = globalLogger;
	}

	public TlosSpaceWide getSpaceWideReference() {
		return spaceWideReference;
	}

	public void setSpaceWideReference(TlosSpaceWide spaceWideReference) {
		this.spaceWideReference = spaceWideReference;
	}

	public ResourceBundle getApplicationResources() {
		return applicationResources;
	}

	public void setApplicationResources(ResourceBundle applicationResources) {
		this.applicationResources = applicationResources;
	}

	public Collection getEXistColllection() {
		return eXistColllection;
	}

	public void setEXistColllection(Collection eXistColllection) {
		this.eXistColllection = eXistColllection;
	}

	public static Logger getGlobalLogger() {
		
		if(globalLogger == null) {
			globalLogger = Logger.getLogger(TlosSpaceWide.class);
		} 
		
		return globalLogger;
	}

	public static void setGlobalLogger(Logger globalLogger) {
		SpaceWideRegistry.globalLogger = globalLogger;
	}

	public boolean isRecovered() {
		return isRecovered;
	}

	public void setRecovered(boolean isRecovered) {
		this.isRecovered = isRecovered;
	}

	public Cpc getCpcReference() {
		return cpcReference;
	}

	public void setCpcReference(Cpc cpcReference) {
		this.cpcReference = cpcReference;
	}

	public GlobalStateDefinition getGlobalStateDefinition() {
		return globalStateDefinition;
	}

	public void setGlobalStateDefinition(GlobalStateDefinition globalStateDefinition) {
		this.globalStateDefinition = globalStateDefinition;
	}

	public long getScenarioReadTime() {
		return scenarioReadTime;
	}

	public void setScenarioReadTime(long scenarioReadTime) {
		this.scenarioReadTime = scenarioReadTime;
	}

	public TlosProcessData getTlosProcessData() {
		return tlosProcessData;
	}

	public void setTlosProcessData(TlosProcessData tlosProcessData) {
		this.tlosProcessData = tlosProcessData;
	}

	public TlosConfigInfo getTlosSWConfigInfo() {
		return tlosSWConfigInfo;
	}

	public void setTlosSWConfigInfo(TlosConfigInfo tlosSWConfigInfo) {
		this.tlosSWConfigInfo = tlosSWConfigInfo;
	}

	public MailServer getMailServer() {
		return mailServer;
	}

	public void setMailServer(MailServer mailServer) {
		this.mailServer = mailServer;
	}
	
	public NagiosServer getNagiosServer() {
		return nagiosServer;
	}

	public void setNagiosServer(NagiosServer nagiosServer) {
		this.nagiosServer = nagiosServer;
	}

	public InfoBus getInfoBus() {
		return infoBus;
	}

	public void setInfoBus(InfoBus infoBus) {
		this.infoBus = infoBus;
	}

	public LikyaDayKeeper getDayKeeperReference() {
		return dayKeeperReference;
	}

	public void setDayKeeperReference(LikyaDayKeeper dayKeeperReference) {
		this.dayKeeperReference = dayKeeperReference;
	}

	public boolean isGünDönümüPeryodPassed() {
		return isGünDönümüPeryodPassed;
	}

	public void setGünDönümüPeryodPassed(boolean isGünDönümüPeryodPassed) {
		this.isGünDönümüPeryodPassed = isGünDönümüPeryodPassed;
	}

	public boolean isSolsticePassed() {
		return isSolsticePassed;
	}

	public void setSolsticePassed(boolean isSolsticePassed) {
		this.isSolsticePassed = isSolsticePassed;
	}

	public AgentManager getAgentManagerReference() {
		return agentManagerReference;
	}

	public void setAgentManagerReference(AgentManager agentManagerReference) {
		this.agentManagerReference = agentManagerReference;
	}

	public HashMap<String, InstanceInfoType> getInstanceLookupTable() {
		return instanceLookupTable;
	}

	public void setInstanceLookupTable(
			HashMap<String, InstanceInfoType> instanceLookupTable) {
		this.instanceLookupTable = instanceLookupTable;
	}

	public void setHttpServer(Server httpServer) {
		this.httpServer = httpServer;
	}

	public Server getHttpServer() {
		return httpServer;
	}

	public void setJmxUser(JmxUser jmxUser) {
		this.jmxUser = jmxUser;
	}

	public JmxUser getJmxUser() {
		return jmxUser;
	}

	public void setWaitConfirmOfGUI(boolean waitConfirmOfGUI) {
		this.waitConfirmOfGUI = waitConfirmOfGUI;
	}

	public boolean isWaitConfirmOfGUI() {
		return waitConfirmOfGUI;
	}

	public boolean isUserSelectedRecover() {
		return isUserSelectedRecover;
	}

	public void setUserSelectedRecover(boolean isUserSelectedRecover) {
		this.isUserSelectedRecover = isUserSelectedRecover;
	}

	public boolean isFIRST_TIME() {
		return FIRST_TIME;
	}

	public void setFIRST_TIME(boolean fIRST_TIME) {
		FIRST_TIME = fIRST_TIME;
	}

	public ArrayList<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<Parameter> parameters) {
		this.parameters = parameters;
	}

	public boolean isRestartAllScenarios() {
		return restartAllScenarios;
	}

	public void setRestartAllScenarios(boolean restartAllScenarios) {
		this.restartAllScenarios = restartAllScenarios;
	}

	public PerformanceManager getPerformanceManagerReference() {
		return performanceManagerReference;
	}

	public void setPerformanceManagerReference(PerformanceManager performanceManagerReference) {
		this.performanceManagerReference = performanceManagerReference;
	}

	public HashMap<Integer, ArrayList<Parameter>> getAllParameters() {
		return allParameters;
	}

	public void setAllParameters(HashMap<Integer, ArrayList<Parameter>> allParameters) {
		this.allParameters = allParameters;
	}

	public String getxQueryModuleUrl() {
		return xQueryModuleUrl;
	}

	public void setxQueryModuleUrl(String xQueryModuleUrl) {
		this.xQueryModuleUrl = xQueryModuleUrl;
	}

	public String getXmlsUrl() {
		return xmlsUrl;
	}

	public void setXmlsUrl(String xmlsUrl) {
		this.xmlsUrl = xmlsUrl;
	}

	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	public String getDbUri() {
		return dbUri;
	}

	public void setDbUri(String dbUri) {
		this.dbUri = dbUri;
	}

	public CpcTester getCpcTesterReference() {
		return cpcTesterReference;
	}

	public void setCpcTesterReference(CpcTester cpcTesterReference) {
		this.cpcTesterReference = cpcTesterReference;
	}

}

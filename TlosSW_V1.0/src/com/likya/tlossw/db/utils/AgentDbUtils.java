package com.likya.tlossw.db.utils;

import java.util.ArrayList;

import org.apache.xmlbeans.XmlObject;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument;
import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.agent.SWAgentsDocument.SWAgents;
import com.likya.tlossw.model.jmx.JmxAgentUser;
import com.likya.tlossw.utils.SpaceWideRegistry;

public class AgentDbUtils extends DBBase {

	public static int checkJmxUser(JmxAgentUser jmxAgentUser) {

		String swAgentXML = jmxAgentUser.getSwAgentXML();
		int checkJmx = -1;
		
		String xQueryStr = agentFunctionConstructor("lk:checkAgent", swAgentXML);
				
		SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);

		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		for(XmlObject currentObject : objectList) {
			checkJmx = Integer.parseInt(currentObject.toString());
		}

		return checkJmx;
	}
	/*
	public static int checkJmxUserOld(JmxAgentUser jmxAgentUser) {

		String swAgentXML = jmxAgentUser.getSwAgentXML();
		int checkJmx = -1;

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String metaData = spaceWideRegistry.getXmlsUrl() + CommonConstantDefinitions.META_DATA;

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleAgentOperations.xquery\";" + CommonConstantDefinitions.decNsAgnt + CommonConstantDefinitions.decNsRes + "lk:checkAgent(\"" + metaData + "\", " + swAgentXML + ")";

		SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				checkJmx = Integer.parseInt(r.getContent() + "");
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return -1;
		}

		return checkJmx;
	}
	

	public static boolean updateAgentToAvailable(int agentId) {
		boolean returnValue = false;

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String metaData = spaceWideRegistry.getXmlsUrl() + CommonConstantDefinitions.META_DATA;

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleAgentOperations.xquery\";" + CommonConstantDefinitions.decNsAgnt + CommonConstantDefinitions.decNsRes + "lk:updateAgentToAvailableLock(\"" + metaData + "\", " + agentId + ")";

		SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				returnValue = ((Boolean.parseBoolean(r.getContent().toString())));
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return returnValue;
	}

    */
	
	public static boolean updateAgentToAvailable(int agentId) {
		
		boolean returnValue = false;

		String xQueryStr = agentFunctionConstructor("lk:checkAgent", "" + agentId);
		
		SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		for(XmlObject currentObject : objectList) {
			returnValue = ((Boolean.parseBoolean(currentObject.toString())));
		}

		return returnValue;
	}

	
	/*
	 * public static boolean updateAgentOutJmxValue(int agentId, boolean outJmxValue){
	 * boolean returnValue = false;
	 * String dbOutJmxValue = "";
	 * 
	 * if(outJmxValue) {
	 * dbOutJmxValue = "true()";
	 * }else {
	 * dbOutJmxValue = "false()";
	 * }
	 * 
	 * SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
	 * 
	 * String dataFile = spaceWideRegistry.getXmlsUrl() + CommonConstantDefinitions.AGENT_DATA;
	 * 
	 * String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + CommonConstantDefinitions.xQueryModuleUrl + "/moduleAgentOperations.xquery\";" +
	 * CommonConstantDefinitions.decNsAgnt + CommonConstantDefinitions.decNsRes + "lk:updateOutJmxValueLock(\"" + dataFile + "\", " + agentId + "," + dbOutJmxValue + ")";
	 * 
	 * SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
	 * 
	 * Collection collection = spaceWideRegistry.getEXistColllection();
	 * XPathQueryService service = null;
	 * try {
	 * service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
	 * service.setProperty("indent", "yes");
	 * 
	 * ResourceSet result = service.query(xQueryStr);
	 * ResourceIterator i = result.getIterator();
	 * while (i.hasMoreResources()) {
	 * Resource r = i.nextResource();
	 * returnValue = ((Boolean.parseBoolean(r.getContent().toString())));
	 * }
	 * } catch (XMLDBException e) {
	 * e.printStackTrace();
	 * return false;
	 * }
	 * 
	 * return returnValue;
	 * }
	 * 
	 * public static boolean updateAgentInJmxValue(int agentId, boolean inJmxValue){
	 * boolean returnValue = false;
	 * String dbInJmxValue = "";
	 * 
	 * if(inJmxValue) {
	 * dbInJmxValue = "true()";
	 * }else {
	 * dbInJmxValue = "false()";
	 * }
	 * 
	 * SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
	 * 
	 * String dataFile = spaceWideRegistry.getXmlsUrl() + CommonConstantDefinitions.AGENT_DATA;
	 * 
	 * String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + CommonConstantDefinitions.xQueryModuleUrl + "/moduleAgentOperations.xquery\";" +
	 * CommonConstantDefinitions.decNsAgnt + CommonConstantDefinitions.decNsRes + "lk:updateInJmxValueLock(\"" + dataFile + "\", " + agentId + "," + dbInJmxValue + ")";
	 * 
	 * SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
	 * 
	 * Collection collection = spaceWideRegistry.getEXistColllection();
	 * XPathQueryService service = null;
	 * try {
	 * service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
	 * service.setProperty("indent", "yes");
	 * 
	 * ResourceSet result = service.query(xQueryStr);
	 * ResourceIterator i = result.getIterator();
	 * while (i.hasMoreResources()) {
	 * Resource r = i.nextResource();
	 * returnValue = ((Boolean.parseBoolean(r.getContent().toString())));
	 * }
	 * } catch (XMLDBException e) {
	 * e.printStackTrace();
	 * return false;
	 * }
	 * 
	 * return returnValue;
	 * }
	 */
	
	/*	
	public static boolean updateAgentJmxValue(int agentId, boolean jmxValue, String islem) {
		boolean returnValue = false;
		String dbJmxValue = "false()";

		if (jmxValue) {
			dbJmxValue = "true()";
		}

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String metaData = spaceWideRegistry.getXmlsUrl() + CommonConstantDefinitions.META_DATA;

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleAgentOperations.xquery\";" + CommonConstantDefinitions.decNsAgnt + CommonConstantDefinitions.decNsRes + "lk:updateJmxValueLock(\"" + metaData + "\", " + agentId + "," + dbJmxValue + ",\"" + islem + "\")";

		SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				returnValue = ((Boolean.parseBoolean(r.getContent().toString())));
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return returnValue;
	}
	*/
	
	public static boolean updateAgentJmxValue(int agentId, boolean jmxValue, String islem) {
		
		boolean returnValue = false;
		String dbJmxValue = "false()";

		if (jmxValue) {
			dbJmxValue = "true()";
		}

		String xQueryStr = agentFunctionConstructor("lk:checkAgent", "" + agentId, "", dbJmxValue, "" + islem);
		
		SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		for(XmlObject currentObject : objectList) {
			returnValue = ((Boolean.parseBoolean(currentObject.toString())));
		}
		
		return returnValue;
	}

	/*
	public static boolean updateUserStopRequestValue(int agentId, String userStopRequestValue) {
		boolean returnValue = false;

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String metaData = spaceWideRegistry.getXmlsUrl() + CommonConstantDefinitions.META_DATA;

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleAgentOperations.xquery\";" + CommonConstantDefinitions.decNsAgnt + CommonConstantDefinitions.decNsRes + "lk:updateUserStopRequestValueLock(\"" + metaData + "\", " + agentId + "," + userStopRequestValue + "\")";

		SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				returnValue = ((Boolean.parseBoolean(r.getContent().toString())));
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return returnValue;
	}
	*/
	
	public static boolean updateUserStopRequestValue(int agentId, String userStopRequestValue) {
		
		boolean returnValue = false;

		String xQueryStr = agentFunctionConstructor("lk:checkAgent", "" + agentId, "" + userStopRequestValue);
		
		SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		for(XmlObject currentObject : objectList) {
			returnValue = ((Boolean.parseBoolean(currentObject.toString())));
		}

		return returnValue;
	}

	/*
	public static SWAgents getResorces() {

		SWAgents swAgents = SWAgents.Factory.newInstance();

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String metaData = spaceWideRegistry.getXmlsUrl() + CommonConstantDefinitions.META_DATA;

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleAgentOperations.xquery\";" + CommonConstantDefinitions.decNsAgnt + CommonConstantDefinitions.decNsRes + "lk:getResorces(\"" + metaData + "\")";

		SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();
				SWAgent agent = SWAgent.Factory.newInstance();
				try {
					agent = SWAgentDocument.Factory.parse(xmlContent).getSWAgent();
				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
				swAgents.addNewSWAgent();
				swAgents.setSWAgentArray(swAgents.getSWAgentArray().length - 1, agent);
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}

		return swAgents;
	}
	*/
	
	public static SWAgents getResorces() {

		SWAgents swAgents = SWAgents.Factory.newInstance();
		
		String xQueryStr = agentFunctionConstructor("lk:getResorces");
		
		SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		for(XmlObject currentObject : objectList) {
			SWAgent swAgent = ((SWAgentDocument) currentObject).getSWAgent();
			swAgents.addNewSWAgent();
			swAgents.setSWAgentArray(swAgents.getSWAgentArray().length - 1, swAgent);
		}

		return swAgents;
	}

	/*
	public static boolean updateResourceNrpeValues(String resourceXML, boolean nrpeValue) {
		boolean returnValue = false;
		String dbNrpeValue = "";

		if (nrpeValue) {
			dbNrpeValue = "true()";
		} else {
			dbNrpeValue = "false()";
		}

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String metaData = spaceWideRegistry.getXmlsUrl() + CommonConstantDefinitions.META_DATA;

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleAgentOperations.xquery\";" + CommonConstantDefinitions.decNsAgnt + CommonConstantDefinitions.decNsRes + "lk:updateNrpeValueLock(\"" + metaData + "\", " + resourceXML + "," + dbNrpeValue + ")";

		SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				returnValue = ((Boolean.parseBoolean(r.getContent().toString())));
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return returnValue;
	}
	*/
	
	public static boolean updateResourceNrpeValues(String resourceXML, boolean nrpeValue) {
		
		boolean returnValue = false;
		String dbNrpeValue = "";

		if (nrpeValue) {
			dbNrpeValue = "true()";
		} else {
			dbNrpeValue = "false()";
		}
		
		String xQueryStr = agentFunctionConstructor("lk:checkAgent", resourceXML, dbNrpeValue);
		
		SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);
		
		ArrayList<XmlObject> objectList = moduleGeneric(xQueryStr);

		for(XmlObject currentObject : objectList) {
			returnValue = ((Boolean.parseBoolean(currentObject.toString())));
		}

		return returnValue;
	}

}

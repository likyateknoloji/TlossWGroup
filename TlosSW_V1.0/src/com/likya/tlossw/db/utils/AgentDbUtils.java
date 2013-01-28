package com.likya.tlossw.db.utils;

import org.apache.xmlbeans.XmlException;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument;
import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.agent.SWAgentsDocument.SWAgents;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.model.jmx.JmxAgentUser;
import com.likya.tlossw.utils.SpaceWideRegistry;

public class AgentDbUtils {
	
	public static int checkJmxUser(JmxAgentUser jmxAgentUser){
		
		String swAgentXML = jmxAgentUser.getSwAgentXML();
		int checkJmx = -1;
		
		String xQueryStr = "xquery version \"1.0\";" +
					       "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAgentOperations.xquery\";" 
					       + "declare namespace agnt = \"http://www.likyateknoloji.com/XML_agent_types\";  "
					       + "declare namespace res = \"http://www.likyateknoloji.com/resource-extension-defs\"; "
					       + "lk:checkAgent("+ swAgentXML +")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return -1;
		}
		
		try {
			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				checkJmx = Integer.parseInt(r.getContent()+"");
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return -1;
		}
		
		return checkJmx;
	}
	
	public static boolean updateAgentToAvailable(int agentId){
		boolean returnValue = false;
		
		String xQueryStr = "xquery version \"1.0\";" +
					       "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAgentOperations.xquery\";" 
					       + "declare namespace agnt = \"http://www.likyateknoloji.com/XML_agent_types\";  "
					       + "declare namespace res = \"http://www.likyateknoloji.com/resource-extension-defs\"; "
					       + "lk:updateAgentToAvailableLock("+ agentId +")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return false;
		}
		
		try {
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
/*	
	public static boolean updateAgentOutJmxValue(int agentId, boolean outJmxValue){
		boolean returnValue = false;
		String dbOutJmxValue = "";
		
		if(outJmxValue) {
			dbOutJmxValue = "true()";
		}else {
			dbOutJmxValue = "false()";
		}
		
		String xQueryStr = "xquery version \"1.0\";" +
					       "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAgentOperations.xquery\";" 
					       + "declare namespace agnt = \"http://www.likyateknoloji.com/XML_agent_types\";  "
					       + "declare namespace res = \"http://www.likyateknoloji.com/resource-extension-defs\"; "
					       + "lk:updateOutJmxValueLock("+ agentId + "," + dbOutJmxValue + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return false;
		}
		
		try {
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
	
	public static boolean updateAgentInJmxValue(int agentId, boolean inJmxValue){
		boolean returnValue = false;
		String dbInJmxValue = "";
		
		if(inJmxValue) {
			dbInJmxValue = "true()";
		}else {
			dbInJmxValue = "false()";
		}
		
		String xQueryStr = "xquery version \"1.0\";" +
					       "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAgentOperations.xquery\";" 
					       + "declare namespace agnt = \"http://www.likyateknoloji.com/XML_agent_types\";  "
					       + "declare namespace res = \"http://www.likyateknoloji.com/resource-extension-defs\"; "
					       + "lk:updateInJmxValueLock("+ agentId + "," + dbInJmxValue + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return false;
		}
		
		try {
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
	public static boolean updateAgentJmxValue(int agentId, boolean jmxValue, String islem){
		boolean returnValue = false;
		String dbJmxValue = "";
		
		if(jmxValue) {
			dbJmxValue = "true()";
		}else {
			dbJmxValue = "false()";
		}
		
		String xQueryStr = "xquery version \"1.0\";" +
					       "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAgentOperations.xquery\";" 
					       + "declare namespace agnt = \"http://www.likyateknoloji.com/XML_agent_types\";  "
					       + "declare namespace res = \"http://www.likyateknoloji.com/resource-extension-defs\"; "
					       + "lk:updateJmxValueLock("+ agentId + "," + dbJmxValue + ",\"" + islem + "\")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return false;
		}
		
		try {
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
	
	public static boolean updateUserStopRequestValue(int agentId, String userStopRequestValue){
		boolean returnValue = false;
		
		String xQueryStr = "xquery version \"1.0\";" +
					       "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAgentOperations.xquery\";" 
					       + "declare namespace agnt = \"http://www.likyateknoloji.com/XML_agent_types\";  "
					       + "declare namespace res = \"http://www.likyateknoloji.com/resource-extension-defs\"; "
					       + "lk:updateUserStopRequestValueLock("+ agentId + ",\"" + userStopRequestValue + "\")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return false;
		}
		
		try {
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
	
	public static SWAgents getResorces(){
		
		SWAgents swAgents = SWAgents.Factory.newInstance();
		
		String xQueryStr = "xquery version \"1.0\";" +
					       "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAgentOperations.xquery\";" 
					       + "declare namespace agnt = \"http://www.likyateknoloji.com/XML_agent_types\";  " 
					       + "declare namespace res = \"http://www.likyateknoloji.com/resource-extension-defs\"; "					       
					       + "lk:getResorces()";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return null;
		}
		
		try {
			ResourceSet result = service.query(xQueryStr);
			ResourceIterator i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();
				SWAgent agent = SWAgent.Factory.newInstance();
				try {
					agent = SWAgentDocument.Factory.parse(xmlContent).getSWAgent();
				}catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
				swAgents.addNewSWAgent();
				swAgents.setSWAgentArray(swAgents.getSWAgentArray().length-1, agent);
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			return null;
		}
		
		return swAgents;
	}

	public static boolean updateResourceNrpeValues(String resourceXML, boolean nrpeValue){
		boolean returnValue = false;
		String dbNrpeValue = "";
		
		if(nrpeValue) {
			dbNrpeValue = "true()";
		}else {
			dbNrpeValue = "false()";
		}
		
		String xQueryStr = "xquery version \"1.0\";" +
					       "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleAgentOperations.xquery\";" 
					       + "declare namespace agnt = \"http://www.likyateknoloji.com/XML_agent_types\";  "
					       + "declare namespace res = \"http://www.likyateknoloji.com/resource-extension-defs\"; "
					       + "lk:updateNrpeValueLock("+ resourceXML + "," + dbNrpeValue + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return false;
		}
		
		try {
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

}

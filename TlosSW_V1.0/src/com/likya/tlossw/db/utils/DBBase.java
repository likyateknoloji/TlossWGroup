package com.likya.tlossw.db.utils;

import java.util.ArrayList;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;

public abstract class DBBase {

	protected static String localFunctionConstructor(String moduleName, String functionName, String moduleNamesSpace, String... param) {
		
		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		
		return ParsingUtils.getFunctionString(spaceWideRegistry.getCollectionName(), spaceWideRegistry.getxQueryModuleUrl(), moduleName, functionName, moduleNamesSpace, param);
	}
	
	protected static String localFunctionConstructorNS(String moduleName, String functionName, String declaredNameSpaces, String moduleNamesSpace, String... param) {
		
		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		
		return ParsingUtils.getFunctionString(spaceWideRegistry.getCollectionName(), spaceWideRegistry.getxQueryModuleUrl(), moduleName, functionName, declaredNameSpaces, moduleNamesSpace, param);
	}
	
	protected static String alarmFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructor("moduleAlarmOperations.xquery", functionName, CommonConstantDefinitions.lkNsUrl, param);
	}

	protected static String agentFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructorNS("moduleAgentOperations.xquery", functionName, CommonConstantDefinitions.decNsRes, CommonConstantDefinitions.lkNsUrl, param);
	}

	protected static String stateFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructorNS("moduleStateOperations.xquery", functionName, CommonConstantDefinitions.decNsSt, CommonConstantDefinitions.lkNsUrl, param);
	}

	protected static String dailyFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructorNS("moduleDailyOperations.xquery", functionName, CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat, CommonConstantDefinitions.hsNsUrl, param);
	}

	protected static String parameterFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructor("moduleParameterOperations.xquery", functionName, CommonConstantDefinitions.lkNsUrl, param);
	}

	protected static String scenarioFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructorNS("moduleScenarioOperations.xquery", functionName, CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat, CommonConstantDefinitions.hsNsUrl, param);
	}

	protected static String userFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructor("moduleUserOperations.xquery", functionName, CommonConstantDefinitions.hsNsUrl, param);
	}
	
	protected static String ftpFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructorNS("moduleFTPConnectionsOperations.xquery", functionName, CommonConstantDefinitions.decNsFtp + CommonConstantDefinitions.decNsCom, CommonConstantDefinitions.fcNsUrl, param);
	}
	
	protected static ArrayList<Object> moduleGeneric(String xQueryStr) {

		ArrayList<Object> returnObjectArray = new ArrayList<Object>();
		XmlObject objectProperties = null;

		try {

			SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
			Collection collection = spaceWideRegistry.getEXistColllection();
			
			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr.toString());
			ResourceIterator i = result.getIterator();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = r.getContent().toString();

				try {
					objectProperties = XmlObject.Factory.parse(xmlContent);
					returnObjectArray.add(objectProperties);
				} catch (XmlException e) {
					returnObjectArray.add(xmlContent);
					// e.printStackTrace();
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		// System.out.println(objectProperties.toString());

		return returnObjectArray;
	}

}

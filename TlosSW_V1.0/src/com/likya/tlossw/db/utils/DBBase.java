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
import com.likya.tlossw.utils.SpaceWideRegistry;

public abstract class DBBase {

	/*
	private String localFunctionConstructor(String moduleName, String functionName, String moduleNamesSpace, String... param) {
		return ParsingUtils.getFunctionString(ExistClient.dbCollectionName, xQueryModuleUrl, moduleName, functionName, moduleNamesSpace, param);
	}

	private String localFunctionConstructor(String moduleName, String functionName, String declaredNameSpaces, String moduleNamesSpace, String... param) {
		return ParsingUtils.getFunctionString(ExistClient.dbCollectionName, xQueryModuleUrl, moduleName, functionName, declaredNameSpaces, moduleNamesSpace, param);
	}

	private String agentFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructor("moduleAgentOperations.xquery", functionName, CommonConstantDefinitions.decNsRes, CommonConstantDefinitions.lkNsUrl, param);
	}

	private String alarmFunctionConstructor(String functionName, String... param) {
		return localFunctionConstructor("moduleAlarmOperations.xquery", functionName, CommonConstantDefinitions.lkNsUrl, param);
	}
	*/
	
	public ArrayList<XmlObject> moduleGeneric(String xQueryStr) {

		ArrayList<XmlObject> returnObjectArray = new ArrayList<XmlObject>();
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
				String xmlContent = (String) r.getContent();

				try {
					objectProperties = XmlObject.Factory.parse(xmlContent);
					returnObjectArray.add(objectProperties);
				} catch (XmlException e) {
					e.printStackTrace();
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		System.out.println(objectProperties.toString());

		return returnObjectArray;
	}

}

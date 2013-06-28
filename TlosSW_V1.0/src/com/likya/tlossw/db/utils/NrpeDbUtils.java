/*
 * TlosSW_V1.0
 * com.likya.tlossw.db.utils : NrpeDbUtils.java
 * @author �ahin Kekevi
 * Tarih : 13.May.2011 09:12:00
 */

package com.likya.tlossw.db.utils;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import com.likya.tlos.model.xmlbeans.error.SWErrorDocument.SWError;
import com.likya.tlos.model.xmlbeans.nrpe.NrpeCallDocument.NrpeCall;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;


public class NrpeDbUtils extends DBBase {
	
	public static boolean insertNrpeOld(NrpeCall nrpeCall){
		
		QName qName = NrpeCall.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String nrpeCallXML = nrpeCall.xmlText(xmlOptions);

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleNrpeOperations.xquery\";" + "lk:insertNrpe("+ nrpeCallXML + ")";

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			@SuppressWarnings("unused")
			ResourceSet result = service.query(xQueryStr);
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	public static boolean insertNrpe(NrpeCall nrpeCall){

		boolean returnValue = false;
		
		QName qName = SWError.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		
		String nrpeCallXML = nrpeCall.xmlText(xmlOptions);
		
		String xQueryStr = nrpeFunctionConstructor("lk:insertNrpe", nrpeCallXML);
				
		SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			returnValue = ((Boolean.parseBoolean(currentObject.toString())));
		}

		return returnValue;
	}

	public static boolean deleteExpiredNrpeMessagesOld(String currentTimeZone , int expireHour){

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleNrpeOperations.xquery\";" + 
				"lk:deleteExpiredNrpeMessagesLock("+ "'" + currentTimeZone + "'" + "," + expireHour + ")";

		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			@SuppressWarnings("unused")
			ResourceSet result = service.query(xQueryStr);
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean deleteExpiredNrpeMessages(String currentTimeZone , int expireHour){

		boolean returnValue = false;
		
		String xQueryStr = nrpeFunctionConstructor("lk:deleteExpiredNrpeMessagesLock", currentTimeZone, "" + expireHour);
				
		SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			returnValue = ((Boolean.parseBoolean(currentObject.toString())));
		}

		return returnValue;
	}
}

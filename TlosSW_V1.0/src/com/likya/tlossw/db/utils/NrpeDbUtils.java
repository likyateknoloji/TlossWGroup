/*
 * TlosSW_V1.0
 * com.likya.tlossw.db.utils : NrpeDbUtils.java
 * @author Þahin Kekevi
 * Tarih : 13.May.2011 09:12:00
 */

package com.likya.tlossw.db.utils;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import com.likya.tlos.model.xmlbeans.nrpe.NrpeCallDocument.NrpeCall;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;


public class NrpeDbUtils {
	
	public static boolean insertNrpe(NrpeCall nrpeCall){
		
		QName qName = NrpeCall.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String nrpeCallXML = nrpeCall.xmlText(xmlOptions);
		
		String xQueryStr = "xquery version \"1.0\";" +
					       "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleNrpeOperations.xquery\";" +
					       "lk:insertNrpe("+ nrpeCallXML + ")";

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
			@SuppressWarnings("unused")
			ResourceSet result = service.query(xQueryStr);
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	public static boolean deleteExpiredNrpeMessages(String currentTimeZone , int expireHour){
		
		String xQueryStr = "xquery version \"1.0\";" +
					       "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleNrpeOperations.xquery\";" +
					       "lk:deleteExpiredNrpeMessagesLock("+ "'" + currentTimeZone + "'" + "," + expireHour + ")";

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
			@SuppressWarnings("unused")
			ResourceSet result = service.query(xQueryStr);
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}


}

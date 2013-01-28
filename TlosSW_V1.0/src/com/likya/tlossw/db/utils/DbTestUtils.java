package com.likya.tlossw.db.utils;

import org.apache.xmlbeans.XmlException;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import com.likya.tlos.model.xmlbeans.agent.RxMessageBodyTypeDocument.RxMessageBodyType;
import com.likya.tlos.model.xmlbeans.agent.RxMessageDocument.RxMessage;
import com.likya.tlos.model.xmlbeans.agent.RxMessageTypeEnumerationDocument.RxMessageTypeEnumeration;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.utils.SpaceWideRegistry;

public class DbTestUtils {
	
	public static JobProperties getJob(String jobPath, String jobName) {
//		jobPath = "/dat:TlosProcessData/dat:scenario[com:jsName = \"deneme\"]/dat:jobList";
//		jobName = "\"deneme\"";
		
		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		String xmlContent = null;
		JobProperties jobProperties = null;
		
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e1) {
			e1.printStackTrace();
		}
		
		String xQueryStr = "xquery version \"1.0\";" +
		"import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleTestOperations.xquery\";" +
	      "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";  "+
		  "declare namespace dat = \"http://www.likyateknoloji.com/XML_data_types\";  "+
	      "lk:getJob("+ jobPath + "," + jobName + " )";

		ResourceIterator i;
		try {
			ResourceSet result = service.query(xQueryStr);
			i = result.getIterator();
			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				xmlContent = (String) r.getContent();
			}
			jobProperties = JobPropertiesDocument.Factory.parse(xmlContent).getJobProperties();
		} catch (XMLDBException e1) {
			e1.printStackTrace();
		} catch (XmlException e) {
			e.printStackTrace();
		}
		
		return jobProperties;
	}
	
	public static RxMessage generateRxMessage(JobProperties job) {
		RxMessage rxMessage = RxMessage.Factory.newInstance();
		rxMessage.setId(RxMessageTypeEnumeration.JOB + "." + job.getID());
		rxMessage.setRxMessageTypeEnumeration(RxMessageTypeEnumeration.JOB);
		RxMessageBodyType rxMessageBodyType = RxMessageBodyType.Factory.newInstance();
		rxMessageBodyType.setJobProperties(job);
		rxMessage.setRxMessageBodyType(rxMessageBodyType);
		
		return rxMessage;
	}

	
}

package com.likya.tlossw.test;

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
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.utils.SpaceWideRegistry;

public class DbTestUtils {
	
	public static JobProperties getJob(String jobPath, String jobName) {
//		jobPath = "/dat:TlosProcessData/dat:scenario[com:jsName = \"deneme\"]/dat:jobList";
//		jobName = "\"deneme\"";
		
		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		String xmlContent = null;
		JobProperties jobProperties = null;

		String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.lkNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleTestOperations.xquery\";" + 
				CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsDat + "lk:getJob("+ jobPath + "," + jobName + " )";

		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceIterator i;
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

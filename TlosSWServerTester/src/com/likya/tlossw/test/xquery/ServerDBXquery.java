package com.likya.tlossw.test.xquery;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import com.likya.tlossw.utils.FileUtils;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.web.db.DBOperations;

/**
 * Tests for {@link Foo}.
 * 
 * @author user@example.com (John Doe)
 */
@RunWith(JUnit4.class)
public class ServerDBXquery {

	public static DBOperations dbOperations;

	private static String dbUri = "xmldb:exist://192.168.1.71:8094/exist/xmlrpc/db/";
	// private static String dbUri = "xmldb:exist://localhost:8093/exist/xmlrpc/db/" + "tlossw".toUpperCase();
	private static String collectionName = "tlossw-serkan";
	
	private static XPathQueryService service;
	private static Collection root = null;
	private static Database database = null;

	public static String filePath;

	@BeforeClass
	public static void setUp() {

		try {
			// initialize driver
			@SuppressWarnings("rawtypes")
			Class cl = Class.forName("org.exist.xmldb.DatabaseImpl");
			database = (Database) cl.newInstance();
			database.setProperty("create-database", "true");
			DatabaseManager.registerDatabase(database);
			root = DatabaseManager.getCollection(dbUri + collectionName, "admin", "admin");
			service = (XPathQueryService) root.getService("XQueryService", "1.0");

		} catch (Exception e) {
			e.printStackTrace();
		}

		// dbOperations = new DBOperations();
	}

	public String getFile(String fileName) {
		return ParsingUtils.getConcatenatedPathAndFileName("moduleTest" + File.separator, fileName);
	}
	
	@Test
	public void moduleGeneric() throws Exception {

		String fileName = getFile("moduleAgentOperations.searchAgent.xquery");

		StringBuffer xQueryStr = FileUtils.readFile(fileName);
		
		XmlObject ftpProperties = null;
		
		try {
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(xQueryStr.toString());
			ResourceIterator i = result.getIterator();
			

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				String xmlContent = (String) r.getContent();

				try {
					ftpProperties = XmlObject.Factory.parse(xmlContent);

				} catch (XmlException e) {
					e.printStackTrace();
				}
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		System.out.println(ftpProperties.toString());
	}
	
	@Test
	public void moduleManagementOperations() throws Exception {

		String fileName = getFile("moduleManagementOperations.getTlosConfig.xquery");

		StringBuffer xQueryStr = FileUtils.readFile(fileName);

		ResourceSet result;
		result = service.query(xQueryStr.toString());
		assertEquals("Boş !", true, result.getIterator().hasMoreResources());

	}

	@Test
	public void moduleStateOperations() throws Exception {

		String fileName = getFile("moduleStateOperations.getTlosGlobalStates.xquery");

		StringBuffer xQueryStr = FileUtils.readFile(fileName);

		ResourceSet result;
		result = service.query(xQueryStr.toString());
		assertEquals("Boş !", true, result.getIterator().hasMoreResources());

	}
	
	@Test
	public void moduleAgentOperationsGetResources() throws Exception {

		String fileName = getFile("moduleAgentOperations.getResources.xquery");

		StringBuffer xQueryStr = FileUtils.readFile(fileName);

		ResourceSet result;
		result = service.query(xQueryStr.toString());
		assertEquals("Boş !", true, result.getIterator().hasMoreResources());

	}

	@Test
	public void moduleAgentOperationsSearchAgent() throws Exception {

		String fileName = getFile("moduleAgentOperations.searchAgent.xquery");

		StringBuffer xQueryStr = FileUtils.readFile(fileName);

		ResourceSet result;
		result = service.query(xQueryStr.toString());
		assertEquals("Boş !", true, result.getIterator().hasMoreResources());

	}
	
	@Test
	public void moduleDailyOperationsDoPlanAndSelectJobsAndScenarios() throws Exception {

		String fileName = getFile("moduleDailyOperations.doPlanAndSelectJobsAndScenarios.xquery");

		StringBuffer xQueryStr = FileUtils.readFile(fileName);

		ResourceSet result;
		result = service.query(xQueryStr.toString());
		assertEquals("Boş !", true, result.getIterator().hasMoreResources());

	}
	
	@Test
	public void moduleAgentOperationsGetAgents() throws Exception {
		
		String fileName = getFile("moduleAgentOperations.getAgents.xquery");

		StringBuffer xQueryStr = FileUtils.readFile(fileName);

		ResourceSet result;
		result = service.query(xQueryStr.toString());
		
		if (result.getIterator().hasMoreResources()) {
			Resource resource = result.getIterator().nextResource();
			String content = (String) resource.getContent();
			
			System.out.println(content);
			
			assertEquals("Agent listesi boş !", true, content != "");
		} else {
			assertEquals("Agent listesi boş !", true, false);
		}
		
	}
	
	@Test
	public void moduleAlarmOperationsAlarms() throws Exception {
		
		String fileName = getFile("moduleAlarmOperations.alarms.xquery");

		StringBuffer xQueryStr = FileUtils.readFile(fileName);

		ResourceSet result;
		result = service.query(xQueryStr.toString());
		assertEquals("Alarm listesi boş !", true, result.getIterator().hasMoreResources());
		
	}
	
	@Test
	public void moduleCalendarOperationsCalendars() throws Exception {
		
		String fileName = getFile("moduleCalendarOperations.calendars.xquery");

		StringBuffer xQueryStr = FileUtils.readFile(fileName);

		ResourceSet result;
		result = service.query(xQueryStr.toString());
		assertEquals("Takvim listesi boş !", true, result.getIterator().hasMoreResources());
		
	}
	
	@Test
	public void moduleFTPConnectionsOperationsInsertFTPConnection() {

		String fileName = getFile("moduleFTPConnectionsOperations.insertFTPConnection.xquery");

		StringBuffer xQueryStr = FileUtils.readFile(fileName);

		Boolean result = false;
		ResourceSet resourceSet = null;
		try {
			resourceSet = service.query(xQueryStr.toString());
		
			if (resourceSet.getIterator().hasMoreResources()) {
				Resource resource = resourceSet.getIterator().nextResource();
				result = Boolean.valueOf(resource.getContent().toString());
			}
			
		} catch (XMLDBException e) {
			// e.printStackTrace();
			
			System.out.println(e.getMessage());
		}
		
		assertEquals("FTP bağlantı tanımı kaydedilemedi !", true, result);
	}

	@Test
	public void moduleAgentOperationsUpdateJmxValueLock() throws Exception {

		String fileName = getFile("moduleAgentOperations.updateJmxValueLock.xquery");

		StringBuffer xQueryStr = FileUtils.readFile(fileName);

		ResourceSet result;
		result = service.query(xQueryStr.toString());
		assertEquals("Boş !", true, result.getIterator().hasMoreResources());

	}
	
	@Test
	public void thisAlwaysPasses() {
	}

	@Test
	@Ignore
	public void thisIsIgnored() {
	}
}
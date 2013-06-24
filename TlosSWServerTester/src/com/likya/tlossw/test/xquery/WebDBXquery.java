package com.likya.tlossw.test.xquery;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceSet;
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
public class WebDBXquery {

	public static DBOperations dbOperations;

	private static String dbUri = "xmldb:exist://192.168.1.71:8094/exist/xmlrpc/db/" + "tlossw-serkan";
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
			root = DatabaseManager.getCollection(dbUri, "admin", "admin");
			service = (XPathQueryService) root.getService("XQueryService", "1.0");

		} catch (Exception e) {
			e.printStackTrace();
		}

		// dbOperations = new DBOperations();
	}

	public String getFile(String fileName) {
		return ParsingUtils.getConcatenatedPathAndFileName("moduleTest" + File.separator, fileName);
	}
	@Ignore
	@Test
	public void moduleAgentOperationsGetResources() throws Exception {

		String fileName = getFile("moduleAgentOperations.getResources.xquery");

		StringBuffer xQueryStr = FileUtils.readFile(fileName);

		ResourceSet result;
		result = service.query(xQueryStr.toString());
		assertEquals("", true, result.getIterator().hasMoreResources());

	}
	@Ignore
	@Test
	public void moduleAgentOperationsSearchAgent() throws Exception {

		String fileName = getFile("moduleAgentOperations.searchAgent.xquery");

		StringBuffer xQueryStr = FileUtils.readFile(fileName);

		ResourceSet result;
		result = service.query(xQueryStr.toString());
		assertEquals("", true, result.getIterator().hasMoreResources());

	}
	
	@Test
	public void moduleDailyOperationsDoPlanAndSelectJobsAndScenarios() throws Exception {

		String fileName = getFile("moduleDailyOperations.doPlanAndSelectJobsAndScenarios.xquery");

		StringBuffer xQueryStr = FileUtils.readFile(fileName);

		ResourceSet result;
		result = service.query(xQueryStr.toString());
		assertEquals("", true, result.getIterator().hasMoreResources());

	}

	@Test
	public void thisAlwaysPasses() {
	}

	@Test
	@Ignore
	public void thisIsIgnored() {
	}
}
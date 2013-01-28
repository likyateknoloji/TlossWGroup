package com.likya.tlossw.test;

import javax.xml.transform.OutputKeys;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

public class DBConnection {

	private static String standAloneDBSystemUri = "xmldb:exist://localhost:8093/exist/xmlrpc/db/TLOSSW";

	public static Collection startExistDBSystem() throws Exception {

		Collection col = null;

		String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class<?> cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);

		col = DatabaseManager.getCollection(standAloneDBSystemUri, "admin", "admin");

		if (col == null) {
			System.out.println("Collection is null, check your eXist DB if it is running !");
			System.exit(-1);
		}
		col.setProperty(OutputKeys.INDENT, "no");

		System.out.println("DB connection has been created.");

		return col;
	}

	public static String executeExistQuery(String xqueryMethod, Collection collection) throws XMLDBException {

		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
		service.setProperty("indent", "yes");

		String xQueryStr = "xquery version \"1.0\";" + "import module namespace fl=\"http://listener.tlos.com/\" at \"xmldb:exist://db/test/modules/moduleFileListenerOperations.xquery\";" + xqueryMethod;

		ResourceSet result = service.query(xQueryStr);
		String resultData = (String) result.getResource(0).getContent();

		return resultData;
	}
}

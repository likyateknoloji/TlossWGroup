/*
 * Tlos_V2.0MC_JmxMp_Gxt
 * com.likya.tlos.omc : TEJmxMpClientBase.java
 * @author Serkan Taþ
 * Tarih : Apr 13, 2009 10:10:44 AM
 */

package com.likya.tlossw.web.exist;

import javax.xml.transform.OutputKeys;

import org.apache.log4j.Logger;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

public class ExistClient {

	private static final Logger logger = Logger.getLogger(ExistClient.class);

	public static boolean tryReconnect = true;

	public static Database database;

	public static Collection getCollection() {

		logger.info("");
		logger.info("##### Veritabani sistemi ile baglanti ######");

		String driver = "org.exist.xmldb.DatabaseImpl";
		String dbUri = "xmldb:exist://localhost:8093/exist/xmlrpc/db/TLOSSW";
		
		Collection collection = null;

		logger.debug("Getting collection on " + dbUri + "...");
		
		int attemptCount = 0;

		while (tryReconnect) {
			try {

				// initialize database driver
				Class<?> cl = Class.forName(driver);
				database = (Database) cl.newInstance();
				database.setProperty("create-database", "true");
				DatabaseManager.registerDatabase(database);
				
				String userName = "admin";
				String password = "admin";

				collection = DatabaseManager.getCollection(dbUri, userName, password);

				if (collection != null) {
					logger.info(">> Collection successfully obtained to " + dbUri);
					collection.setProperty(OutputKeys.INDENT, "no");
					tryReconnect = false;
					break;
				}

				errprintln("Collection is null, check your eXist DB if it is running !");

			} catch (XMLDBException xException) {
				xException.printStackTrace();
			} catch (RuntimeException runtimeException) {
				runtimeException.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoClassDefFoundError e) {
				e.printStackTrace();
			}
			
			logger.info(">> eXist Collection can NOT be obtained ! Waiting for 5 second before retry...");
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			logger.info(">> Trying to recollect. Attempt count " + ++attemptCount);

		}

		logger.info("############################################");

		logger.info("");

		return collection;
	}
	
	public static void errprintln(String message) {
		System.err.println(message);
	}

}

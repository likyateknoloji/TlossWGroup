package com.likya.tlossw.test.gundonumu;

import org.apache.log4j.Logger;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.modules.XPathQueryService;

import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.db.utils.DBUtils;
import com.likya.tlossw.exceptions.TlosFatalException;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.validation.XMLValidations;

public class GetDailyData {

	protected Logger logger = Logger.getLogger(GetDailyData.class);

	private static String standAloneDBSystemUri = "xmldb:exist://192.168.1.71:8094/exist/xmlrpc/db/tlossw-serkan";
	
	private static SpaceWideRegistry spaceWideRegistry = SpaceWideRegistry.getInstance();

	public static void main(String[] args) {

		
		spaceWideRegistry.setDbUri(standAloneDBSystemUri);

		TlosSpaceWide.setSpaceWideRegistry(spaceWideRegistry);

		String xQueryModuleUrl = ParsingUtils.getXQueryModuleUrl(spaceWideRegistry.getDbUri());
		spaceWideRegistry.setxQueryModuleUrl(xQueryModuleUrl);

		new GetDailyData().loadDailyPlan();
	}

	public void initCollection() throws Exception {

		Collection collection = null;

		String driver = "org.exist.xmldb.DatabaseImpl";

		// initialize database driver
		Class<?> cl = Class.forName(driver);
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);

		collection = DatabaseManager.getCollection(standAloneDBSystemUri, "tlossw", "tlossw");

		XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");

		service.setProperty("indent", "yes");
		
		spaceWideRegistry.setEXistColllection(collection);
		
		TlosSpaceWide.setSpaceWideRegistry(spaceWideRegistry);
	}
	

	public void loadDailyPlan() {
		
		try {
			initCollection();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		logger.info("   > İş listesi KDS nden sorgulanıyor ...");

		TlosProcessData tlosProcessData = null;
		
		try {
			
			int planId = 0;
			int scenarioId = 0;

			tlosProcessData = DBUtils.getTlosDailyData(scenarioId, planId);

			if (tlosProcessData == null || !XMLValidations.validateWithXSDAndLog(logger, tlosProcessData)) {
				throw new TlosFatalException("DBUtils.getTlosDailyData : TlosProcessData is null or tlosProcessData xml is damaged !");
			}

		} catch (TlosFatalException e) {
			e.printStackTrace();
			return;
		}
		
		System.out.println(tlosProcessData);

		logger.info("   > İş listesi KDS nden sorgulandı ! ...");
	}

}

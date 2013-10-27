package com.likya.tlossw.test.gundonumu;

import java.util.HashMap;

import org.junit.Test;

import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument.TlosConfigInfo;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlossw.core.cpc.helper.Consolidator;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.helpers.JobQueueOperations;
import com.likya.tlossw.model.path.TlosSWPathType;
import com.likya.tlossw.test.cpc.CpcBaseTester;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likyateknoloji.xmlServerConfigTypes.ServerConfigDocument.ServerConfig;

public class GunDonumuTester extends CpcBaseTester {
	
	@Test
	public void runGunDonumu() throws Exception {
		
		ServerConfig serverConfig = getServerConfig();
		TlosConfigInfo tlocConfigInfo = getTlosConfigInfo();

		SpaceWideRegistry spaceWideRegistry = SpaceWideRegistry.getInstance();
		
		spaceWideRegistry.setServerConfig(serverConfig);
		spaceWideRegistry.setTlosSWConfigInfo(tlocConfigInfo);

		TlosProcessData tlosProcessDataToday = getTlosProcessData("tlosDataToday.xml");
		spaceWideRegistry.setTlosProcessData(tlosProcessDataToday);
		HashMap<String, SpcInfoType> spcLookUpTableToday = prepareSpcLookupTable(spaceWideRegistry, "root");
		System.out.println("Size of Spc LookUp Table Today : " + spcLookUpTableToday.size());
		
		TlosProcessData tlosProcessDataYesterday = getTlosProcessData("tlosDataYesterday.xml");
		spaceWideRegistry.setTlosProcessData(tlosProcessDataYesterday);
		HashMap<String, SpcInfoType> spcLookUpTableYesterday = prepareSpcLookupTable(spaceWideRegistry, "root");
		System.out.println("Size of Spc LookUp Table Yesterday : " + spcLookUpTableYesterday.size());
		
		String oldInstance = new TlosSWPathType(spcLookUpTableYesterday.keySet().toArray()[0].toString()).getRunId();
		
		Consolidator.compareAndConsolidateTwoTables(oldInstance, spcLookUpTableToday, spcLookUpTableYesterday);
		
		for(String spcId : spcLookUpTableToday.keySet()) {
			JobQueueOperations.dumpJobQueue(new TlosSWPathType(spcId).getAbsolutePath(), spcLookUpTableToday.get(spcId).getSpcReferance().getJobQueue());
		}
	}
	
}

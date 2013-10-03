package com.likya.tlossw.test.gundonumu;

import java.util.HashMap;

import org.junit.Test;

import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument.TlosConfigInfo;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlossw.core.cpc.helper.Consolidator;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.test.cpc.CpcBaseTester;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likyateknoloji.xmlServerConfigTypes.ServerConfigDocument.ServerConfig;

public class GunDonumuTester extends CpcBaseTester {
	
	@Test
	public void runGunDonumu() throws Exception {
		
		TlosProcessData tlosProcessDataToday = getTlosProcessData("tlosDataToday.xml");
		TlosProcessData tlosProcessDataYesterday = getTlosProcessData("tlosDataYesterday.xml");
		
		ServerConfig serverConfig = getServerConfig();
		TlosConfigInfo tlocConfigInfo = getTlosConfigInfo();
		

		SpaceWideRegistry spaceWideRegistryToday = SpaceWideRegistry.getInstance();
		SpaceWideRegistry spaceWideRegistryYesterday = SpaceWideRegistry.getInstance();

		spaceWideRegistryToday.setServerConfig(serverConfig);
		spaceWideRegistryToday.setTlosSWConfigInfo(tlocConfigInfo);

		spaceWideRegistryToday.setTlosProcessData(tlosProcessDataToday);
		spaceWideRegistryYesterday.setTlosProcessData(tlosProcessDataYesterday);
	
		HashMap<String, SpcInfoType> spcLookUpTableToday = prepareSpcLookupTable(spaceWideRegistryToday, "root");
		
		HashMap<String, SpcInfoType> spcLookUpTableYesterday = prepareSpcLookupTable(spaceWideRegistryYesterday, "root");

		
		System.out.println("Size of Spc LookUp Table Today : " + spcLookUpTableToday.size());
		System.out.println("Size of Spc LookUp Table Yesterday : " + spcLookUpTableYesterday.size());
		
		String oldInstance = "";
		
		Consolidator.compareAndConsolidateTwoTables(oldInstance, spcLookUpTableToday, spcLookUpTableYesterday);
	}
	
}

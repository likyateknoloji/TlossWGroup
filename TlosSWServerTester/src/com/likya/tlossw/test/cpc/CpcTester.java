package com.likya.tlossw.test.cpc;

import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.utils.SpaceWideRegistry;

/**
 * Tests for {@link www.tlos.com.tr}.
 * 
 * @author serkan.tas@likyateknoloji.com (Serkan Taş)
 */
@RunWith(JUnit4.class)
public class CpcTester extends CpcBaseTester {

	@BeforeClass
	public static void setUp() {
	}

	@Test
	public void runPrep() throws Exception {

		SpaceWideRegistry spaceWideRegistry = initTest();

		HashMap<String, SpcInfoType> spcLookUpTable = prepareSpcLookupTable(spaceWideRegistry, "root");

		System.out.println("Size of Spc LookUp Table : " + spcLookUpTable.size());

	}

	@Test
	public void runTable() {

		try {
			SpaceWideRegistry spaceWideRegistry = initTest();

			startInfoBusSystem(myLogger, spaceWideRegistry);

			HashMap<String, SpcInfoType> spcLookUpTable = prepareSpcLookupTable(spaceWideRegistry, "root");

			System.out.println("Size of Spc LookUp Table : " + spcLookUpTable.size());

			for (String spcId : spcLookUpTable.keySet()) {

				myLogger.info("   > Senaryo " + spcId + " calistiriliyor !");

				SpcInfoType mySpcInfoType = spcLookUpTable.get(spcId);
				Spc spc = mySpcInfoType.getSpcReferance();

				/**
				 * Bu thread daha once calistirildi mi? Degilse thread i baslatabiliriz !!
				 **/
				if (mySpcInfoType.isVirgin() && !spc.getExecuterThread().isAlive()) {

					mySpcInfoType.setVirgin(false); /* Artik baslattik */
					/** Statuleri set edelim **/
					spc.getLiveStateInfo().setStateName(StateName.RUNNING);
					spc.getLiveStateInfo().setSubstateName(SubstateName.STAGE_IN);

					myLogger.info("     > Senaryo " + spcId + " aktive edildi !");

					/** Senaryonun thread lerle calistirildigi yer !! **/

					Thread myThread = spc.getExecuterThread();
					myThread.start();

					myLogger.info("     > OK");

				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		System.out.println("runTable is over !");
	}
}
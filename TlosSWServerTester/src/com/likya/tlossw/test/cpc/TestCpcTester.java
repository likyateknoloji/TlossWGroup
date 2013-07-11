package com.likya.tlossw.test.cpc;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.likya.tlossw.core.cpc.CpcTester;
import com.likya.tlossw.utils.SpaceWideRegistry;

/**
 * Tests for {@link www.tlos.com.tr}.
 * 
 * @author serkan.tas@likyateknoloji.com (Serkan Taş)
 */
@RunWith(JUnit4.class)
public class TestCpcTester extends CpcBaseTester {

	@BeforeClass
	public static void setUp() {
	}

	@Ignore
	@Test
	public void starter() throws Exception {

		SpaceWideRegistry spaceWideRegistry = initTest();

		CpcTester cpcTester = new CpcTester(spaceWideRegistry);
		Thread cpcTesterExecuterThread = new Thread(cpcTester);

		cpcTesterExecuterThread.start();
		
		System.out.println("Deneme");

	}

	@Test
	public void executer() throws Exception {

		SpaceWideRegistry spaceWideRegistry = initTest();
		startInfoBusSystem(Logger.getLogger(getClass()), spaceWideRegistry);

		CpcTester cpcTester = new CpcTester(spaceWideRegistry);
		Thread cpcTesterExecuterThread = new Thread(cpcTester);
		cpcTester.setExecuterThread(cpcTesterExecuterThread);
		cpcTesterExecuterThread.setDaemon(true);
		cpcTesterExecuterThread.start();
		
		cpcTester.addTestData(getTlosProcessData());
		
		synchronized (cpcTesterExecuterThread) {
			cpcTesterExecuterThread.notifyAll();
		}

		System.out.println("Deneme");

	}

	
}
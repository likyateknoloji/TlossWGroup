package com.likya.tlossw.test.cpc;

import org.junit.BeforeClass;
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

	@Test
	public void runPrep() throws Exception {

		SpaceWideRegistry spaceWideRegistry = initTest();

		CpcTester cpcTester = new CpcTester(spaceWideRegistry);
		Thread cpcTesterExecuterThread = new Thread(cpcTester);

		cpcTesterExecuterThread.start();

	}

}
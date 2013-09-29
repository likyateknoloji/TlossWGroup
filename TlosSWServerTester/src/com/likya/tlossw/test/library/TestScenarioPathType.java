package com.likya.tlossw.test.library;

import com.likya.tlossw.model.path.ScenarioPathType;

public class TestScenarioPathType {
	
	public static void main(String[] args) {
		testGetters();
	}
	
	public static void testGetters() {

		String testStr = "root.212.22.2.2.2.3";
		//
		// System.out.println(testStr.split("\\.").length);

		long start = System.currentTimeMillis();
		ScenarioPathType scenarioPathType = new ScenarioPathType(testStr);
		long stop = System.currentTimeMillis();
		System.out.println("Süre : " + (stop - start) + "ms");

		start = System.currentTimeMillis();
		String retStr = scenarioPathType.getInstanceId();
		stop = System.currentTimeMillis();
		System.out.println("instance id : " + retStr);
		System.out.println("Süre : " + (stop - start) + "ms");

		start = System.currentTimeMillis();
		retStr = scenarioPathType.getAbsolutePath();
		stop = System.currentTimeMillis();
		System.out.println("absolute path : " + retStr);
		System.out.println("Süre : " + (stop - start) + "ms");

		start = System.currentTimeMillis();
		retStr = scenarioPathType.getId();
		stop = System.currentTimeMillis();
		System.out.println("id : " + retStr);
		System.out.println("Süre : " + (stop - start) + "ms");

		start = System.currentTimeMillis();
		retStr = scenarioPathType.getFullPath();
		stop = System.currentTimeMillis();
		System.out.println("Full Path : " + retStr);
		System.out.println("Süre : " + (stop - start) + "ms");
	}
	
	public static void testSetters() {
	}

}

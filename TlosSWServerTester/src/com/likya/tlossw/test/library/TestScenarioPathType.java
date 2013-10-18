package com.likya.tlossw.test.library;

import com.likya.tlossw.model.path.JSPathId;
import com.likya.tlossw.model.path.TlosSWPathType;

public class TestScenarioPathType {
	
	public static void main(String[] args) {
		// testGetters();
		// testSetters();
		testIdSequence();
	}
	
	public static void testIdSequence() {
		
		TlosSWPathType tlosSWPathType = new TlosSWPathType();
		tlosSWPathType.setId(new JSPathId("0"));
		tlosSWPathType.setPlanId("1234");
		String spcFullPath = tlosSWPathType.getFullPath();
		
		System.out.println(spcFullPath);
		
	}
	
	public static void testGetters() {

		String testStr = "root.212.22.2.2.2.3";
		//
		// System.out.println(testStr.split("\\.").length);

		long start = System.currentTimeMillis();
		TlosSWPathType scenarioPathType = new TlosSWPathType(testStr);
		long stop = System.currentTimeMillis();
		System.out.println("Süre : " + (stop - start) + "ms");

		start = System.currentTimeMillis();
		String retStr = scenarioPathType.getPlanId();
		stop = System.currentTimeMillis();
		System.out.println("plan id : " + retStr);
		System.out.println("Süre : " + (stop - start) + "ms");

		start = System.currentTimeMillis();
		retStr = scenarioPathType.getAbsolutePath();
		stop = System.currentTimeMillis();
		System.out.println("absolute path : " + retStr);
		System.out.println("Süre : " + (stop - start) + "ms");

		start = System.currentTimeMillis();
		retStr = scenarioPathType.getId().toString();
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
		
		String testStr = "root.212.22.2.2.2.3";
		
		TlosSWPathType scenarioPathType = new TlosSWPathType(testStr);
		
		long start = System.currentTimeMillis();
		scenarioPathType.add("3333");
		long stop = System.currentTimeMillis();
		System.out.println("Full Path : " + scenarioPathType.getFullPath());
		System.out.println("Süre : " + (stop - start) + "ms");

		start = System.currentTimeMillis();
		scenarioPathType.setPlanId("444");
		stop = System.currentTimeMillis();
		System.out.println("Full Path : " + scenarioPathType.getFullPath());
		System.out.println("Plan Id : " + scenarioPathType.getPlanId());
		System.out.println("Süre : " + (stop - start) + "ms");

		start = System.currentTimeMillis();
		scenarioPathType.setPlanId("444");
		stop = System.currentTimeMillis();
		System.out.println("Full Path : " + scenarioPathType.getFullPath());
		System.out.println("Id : " + scenarioPathType.getId());
		System.out.println("Base Id : " + scenarioPathType.getId().getBaseId());
		JSPathId jsPathId = scenarioPathType.getId();
		jsPathId.incrementRuId();
		System.out.println("RuId Id : " + jsPathId.getRuid());
		
		scenarioPathType.setId(jsPathId);
		System.out.println("RuId Id : " + scenarioPathType.getId().getRuid());
		System.out.println("Süre : " + (stop - start) + "ms");

		System.out.println("Incremented RuId Id : " + scenarioPathType.incrementRuId());
		System.out.println("Süre : " + (stop - start) + "ms");
	}

}

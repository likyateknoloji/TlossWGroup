package com.likya.tlossw.core.cpc.model;


public class AppState {
	
	public static final int INT_NOT_STARTED = 0x00;
	public static final int INT_STARTING = 0x1;
	public static final int INT_RUNNING = 0x2;
	public static final int INT_STOPPING = 0x3;
	public static final int INT_SUSPENDED = 0x4;
	public static final int INT_STANDBY = 0x5;
	
	
	private static String [] stringValues = {
		"NOT_STARTED", "STARTING", "RUNNING", "STOPPING", "SUSPENDED", "STANDBY"
	};
	
	public static int getValue(String string) {
		int counter = 0;
		for(String str: stringValues) {
			if(str.equals(string)) {
				return counter;
			}
			counter ++;
		}
		return -1;
	}
	
	public static String getString(int value) {
		String returnValue = "";
		try {		
			returnValue = stringValues[value];
		} catch(ArrayIndexOutOfBoundsException a) {
			// do nothing
		}
		
		return returnValue;
	}

}

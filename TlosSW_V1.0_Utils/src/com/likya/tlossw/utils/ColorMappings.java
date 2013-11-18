package com.likya.tlossw.utils;

import java.util.HashMap;

public class ColorMappings {

	public static HashMap<String, String> getColorHashMap() {
		
		HashMap<String, String> ColorHashMap = new HashMap<String, String>();
		
		ColorHashMap.put("running", "4962EE");
		ColorHashMap.put("failed", "FF0000");
		ColorHashMap.put("ready", "00FF00");
		ColorHashMap.put("waiting", "FFBF00");
		ColorHashMap.put("success", "006400");
		ColorHashMap.put("look4resource", "BC8F8F");
		ColorHashMap.put("userChooseResource", "FFFF00");
		ColorHashMap.put("userWaiting", "40E0D0");
		ColorHashMap.put("cancelled", "191970");
		ColorHashMap.put("timeout", "8A2BE2");
		ColorHashMap.put("development", "D2B48C");
		ColorHashMap.put("test", "E6E6FA");
		ColorHashMap.put("request", "FF69B4");
		ColorHashMap.put("deployed", "FF4500");

		return ColorHashMap;
	}
	
	public String getColorHex(String color) {
		return getColorHashMap().get(color);
	}
	
}

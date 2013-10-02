package com.likya.tlossw.test.library;

import com.likya.tlossw.core.cpc.model.AppState;

public class TestAppState {
	
	public static void main(String[] args) {
		
		// System.out.println("İşte : [" + AppState.getString(-1) + "]");
		
		System.out.println("İşte : [" + AppState.getString(AppState.INT_SUSPENDED) + "]");
		
		System.out.println("İşte : [" + AppState.getValue("SUSPENDED") + "]");
	}

}

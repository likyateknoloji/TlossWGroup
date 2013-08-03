package com.likya.tlossw.core.events.types;

import java.util.Observable;

import com.likya.tlossw.utils.GlobalRegistry;
/* this is Event Handler */

public class JobStartEvent extends TlosBaseEvent {
	
	public JobStartEvent(GlobalRegistry globalRegistry) {
		super(globalRegistry);
		// TODO Auto-generated constructor stub
	}

	private String resp;

	public void update(Observable obj, Object arg) {
		if (arg instanceof String) {
			resp = (String) arg;
			System.out.println("\nReceived Response: " + resp);
		}
	}
}
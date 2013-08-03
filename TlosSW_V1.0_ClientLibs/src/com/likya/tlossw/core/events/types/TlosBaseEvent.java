package com.likya.tlossw.core.events.types;

import java.util.Observable;
import java.util.Observer;

import com.likya.tlossw.utils.GlobalRegistry;


public class TlosBaseEvent implements Observer {

	private GlobalRegistry globalRegistry;
	
	public TlosBaseEvent(GlobalRegistry globalRegistry) {
		super();
		this.globalRegistry = globalRegistry;
	}

	public GlobalRegistry getGlobalRegistry() {
		return globalRegistry;
	}

	public void setGlobalRegistry(GlobalRegistry globalRegistry) {
		this.globalRegistry = globalRegistry;
	}

	@Override
	public void update(Observable o, Object arg) {
		
	}
	
}

package com.likya.tlossw.utils;

import java.io.Serializable;

import com.likya.tlos.model.xmlbeans.state.GlobalStateDefinitionDocument.GlobalStateDefinition;


public interface GlobalRegistry extends Serializable {
	
	public GlobalStateDefinition getGlobalStateDefinition();
	public InfoBus getInfoBus();
	
}

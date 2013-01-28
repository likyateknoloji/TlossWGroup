package com.likya.tlossw.infobus.helper;

import com.likya.tlos.model.xmlbeans.error.SWErrorDocument.SWError;

/**
 * TlosSW de cikmasi olasi herturlu hatanin tutuldugu SWError un 
 * ve ilgili set ve get metodlarinin tanimlandigi class. 
 * @author tlosSW Dev Team
 * @since v1.0
 * 
 */
public class TlosSWError implements InfoType {
	
	private static final long serialVersionUID = -4258437512024550629L;
	
	private SWError swError;

	public SWError getSwError() {
		return swError;
	}

	public void setSwError(SWError swError) {
		this.swError = swError;
	}
	
}

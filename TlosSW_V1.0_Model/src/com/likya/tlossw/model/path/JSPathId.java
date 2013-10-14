package com.likya.tlossw.model.path;

import com.likya.tlossw.exceptions.TlosException;

public class JSPathId {
	
	// Base Id of Scenario or Job
	private long baseId;
	
	// Running instance id of scenario or job
	private int ruid;

	public JSPathId(String jsPathId) {
		
		String pathArray[] = jsPathId.split("\\:");
		
		if (pathArray.length == 1) {
			String bidStr = pathArray[0];
			try {
				baseId = Integer.parseInt(bidStr);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			ruid = 0;
		} else if (pathArray.length == 2) {
			String ruidStr = pathArray[1];
			try {
				ruid = Integer.parseInt(ruidStr);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		} else {
			try {
				throw new TlosException("Invalid path string format : baseid:ruid >> " + jsPathId);
			} catch (TlosException e) {
				e.printStackTrace();
			}
		}
	}

	public long getBaseId() {
		return baseId;
	}

	public void setBaseId(long baseId) {
		this.baseId = baseId;
	}

	public int getRuid() {
		return ruid;
	}

	public void setRuid(int ruid) {
		this.ruid = ruid;
	}
	
	public int incrementRuId() {
		return ++ruid;
	}
	
	public int incrementRuId(int incValue) {
		return ruid += incValue;
	}

	@Override
	public String toString() {
		
		String retValue = "" + baseId;
		
		if(ruid != 0) {
			retValue += ":" + ruid;
		}
		
		return retValue;
	}

}

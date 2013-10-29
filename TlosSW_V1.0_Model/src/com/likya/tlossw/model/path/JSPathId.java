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
			baseId = strToLong(pathArray[0]);
			ruid = 0;
		} else if (pathArray.length == 2) {
			baseId = strToLong(pathArray[0]);
			ruid = strToInt(pathArray[1]);
		} else {
			try {
				throw new TlosException("Invalid path string format : baseid:ruid >> " + jsPathId);
			} catch (TlosException e) {
				e.printStackTrace();
			}
		}
	}

	private long strToLong(String retStr) {

		long retValue = 0;

		try {
			retValue = Long.parseLong(retStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return retValue;
	}

	private int strToInt(String retStr) {

		int retValue = 0;

		try {
			retValue = Integer.parseInt(retStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return retValue;
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

		if (ruid != 0) {
			retValue += ":" + ruid;
		}

		return retValue;
	}

}

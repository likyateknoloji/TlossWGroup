package com.likya.tlossw.utils;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;

public class NullChecker {

	public static boolean checkNull(JobProperties jobProperties) {
		
		boolean retValue = false;
		
		if(jobProperties == null) {
			System.err.println("NULL : jobProperties");
		} else if(jobProperties.getStateInfos() == null) {
			System.err.println("NULL : jobProperties.getStateInfos()");
		} else if(jobProperties.getStateInfos().getLiveStateInfos() == null) {
			System.err.println("NULL : jobProperties.getStateInfos().getLiveStateInfos()");
		} else if(jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0) == null) {
			System.err.println("NULL : jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0)");
		} else if(jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName() == null) {
			System.err.println("NULL : jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName()");
		} else {
			retValue = true;
		}
		
		return retValue;
	}
}

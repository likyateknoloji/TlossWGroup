/*
 * TlosSW_V1.0_Agent
 * com.likya.tlosswagent.jmx.beans : ProcessInfoProvider.java
 * @author Þahin Kekevi
 * Tarih : July 14, 2011 2:19:17 PM
 */

package com.likya.tlosswagent.jmx.beans;

import com.likya.tlosswagent.TlosSWAgent;

public class ProcessInfoProvider implements ProcessInfoProviderMBean {
	
	@Override
	public String getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getNbChanges() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object runningJobs() {
		Object jobs = TlosSWAgent.getSwAgentRegistry().getTaskQueManagerRef().runningJobsXML();
		
		return jobs;
	}
	



}
 
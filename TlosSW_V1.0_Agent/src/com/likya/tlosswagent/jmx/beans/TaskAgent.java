/*
 * TlosFaz_V2.0
 * com.likya.tlos.jmx.mp.helper : ProcessInfoProvider.java
 * @author Serkan Taþ
 * Tarih : Apr 6, 2009 2:19:17 PM
 */

package com.likya.tlosswagent.jmx.beans;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.xmlbeans.XmlException;

import com.likya.tlos.model.xmlbeans.agent.RxMessageDocument;
import com.likya.tlos.model.xmlbeans.agent.RxMessageDocument.RxMessage;
import com.likya.tlossw.model.jmx.JmxAgentUser;
import com.likya.tlosswagent.TlosSWAgent;
import com.likya.tlosswagent.core.cpc.Cpc;
import com.likya.tlosswagent.jmx.JMXServer;



public class TaskAgent implements TaskAgentMBean {
	
	@Override
	public int getNbChanges() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setState(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean jobHandle(String message, JmxAgentUser jmxAgentUser) {
		if (!JMXServer.authroize(jmxAgentUser)) {
			return false;
		}
		
		RxMessage rxMessage = null;
		try {
			rxMessage = RxMessageDocument.Factory.parse(message).getRxMessage();
		} catch (XmlException e) {
			e.printStackTrace();
			return false;
		}
		if(rxMessage != null) {
			TlosSWAgent.getSwAgentRegistry().getTaskQueManagerRef().addTask(rxMessage);
		}
		
		return true;
	}

	@Override
	public void scenarioHandle(String scenario) {
		System.out.println(scenario);
	}
	
	public boolean resetAgent(JmxAgentUser jmxAgentUser) {
		if (!JMXServer.authroize(jmxAgentUser)) {
			return false;
		}

		Cpc.cleanAllTasks();
		
		return true;
	}
	
	public void shutdown(String str) {
		
		String ipAddr = null;
		try {
			InetAddress addr = InetAddress.getLocalHost();
			// Get IP Address
			ipAddr = addr.getHostAddress();
		} catch (UnknownHostException e) {
		}

		if(!str.equals(ipAddr.toString())) {
			return;
		}
		TlosSWAgent.stopAgent();
	}

}

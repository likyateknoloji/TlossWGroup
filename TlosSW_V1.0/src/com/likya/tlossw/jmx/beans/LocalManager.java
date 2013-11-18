/*
 * TlosFaz_V2.0
 * com.likya.tlos.jmx.mp.helper : ProcessInfoProvider.java
 * @author Serkan Ta�
 * Tarih : Apr 6, 2009 2:19:17 PM
 */

package com.likya.tlossw.jmx.beans;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.likya.tlossw.TlosSpaceWide;

public class LocalManager implements LocalManagerMBean {
	
	@Override
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

		TlosSpaceWide.stopSpacewide();
	}
	
	public void shiftTransitionTime(boolean backupReports) {
		new ProcessManagementInterface().shiftTransitionTime(backupReports);
	}
	
	public void startOver(boolean backupReports) {
		new ProcessManagementInterface().startOver(backupReports);
	}
	
	public void simulateGunDonumu() {
		new ProcessManagementInterface().simulateGunDonumu();
	}
	
	/*
	public void redeploy(String str) {
		
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

		try {
			TlosSpaceWide.getSpaceWideRegistry().getSpaceWideReference().shutDownHttpServer();
			TlosSpaceWide.getSpaceWideRegistry().getSpaceWideReference().startWebSystem();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
	
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

}

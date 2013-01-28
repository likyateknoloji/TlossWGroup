package com.likya.tlossw.nagios;

import java.util.ArrayList;

public class NagiosClient {
	
	public static ArrayList<String> checkResource(String ipAddress, int port, ArrayList<String> commandList) {

		NrpePacket nrpePacket = null;
		ArrayList<String> resultList = new ArrayList<String>();
		
		for(int i = 0; i < commandList.size(); i++) {
			try {
				nrpePacket = CheckNrpe.check(ipAddress, port, commandList.get(i));
				resultList.add(nrpePacket.getBuffer());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return resultList;
	}
}

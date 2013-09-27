package com.likya.tlossw;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public abstract class LocalJmx {
	
	protected static Map<String, String> env = new HashMap<String, String>();	
	
	protected static String getIpAddress() {
		
		String ipAddr = null;
		
		try {
			InetAddress addr = InetAddress.getLocalHost();

			// Get IP Address
			ipAddr = addr.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		return ipAddr;
	}

	protected static void setUpTls() {

		if (System.getProperty("javax.net.ssl.trustStore") == null)
			System.setProperty("javax.net.ssl.trustStore", "likyaKeystore");
		if (System.getProperty("javax.net.ssl.keyStorePassword") == null)
			System.setProperty("javax.net.ssl.keyStorePassword", "likya1!+");
		if (System.getProperty("javax.net.ssl.keyStore") == null)
			System.setProperty("javax.net.ssl.keyStore", "likyaKeystore");

		env.put("jmx.remote.profiles", "TLS");
		env.put("jmx.remote.tls.need.client.authentication", "true");
	}
	
}

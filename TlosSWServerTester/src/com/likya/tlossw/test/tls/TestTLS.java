package com.likya.tlossw.test.tls;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class TestTLS {

	public static void main(String[] args) {
		
		System.setProperty("javax.net.debug", "ssl");
		
		String host = "127.0.0.1";
		int port = 5555; // jmx için 5554; //jmxtls icin 5555
		
		try {
			
			JMXServiceURL url = new JMXServiceURL("jmxmp", host, port);
			
			JMXConnector jmxConnector = JMXConnectorFactory.connect(url, getEnv());
			
			jmxConnector.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected static Map<String, String> getEnv() {
		Map<String, String> env = new HashMap<String, String>();

		if (System.getProperty("javax.net.ssl.trustStore") == null)
			System.setProperty("javax.net.ssl.trustStore", "/Users/serkan/programlar/dev/workspace/TlosSW_V3.0_Web/likyaKeystore");
		if (System.getProperty("javax.net.ssl.keyStorePassword") == null)
			System.setProperty("javax.net.ssl.keyStorePassword", "likya1!+");
		if (System.getProperty("javax.net.ssl.keyStore") == null)
			System.setProperty("javax.net.ssl.keyStore", "/Users/serkan/programlar/dev/workspace/TlosSW_V3.0_Web/likyaKeystore");

		env.put("jmx.remote.profiles", "TLS");

		return env;

	}

}

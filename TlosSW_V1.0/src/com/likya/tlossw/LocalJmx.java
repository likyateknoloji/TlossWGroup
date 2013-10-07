package com.likya.tlossw;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public abstract class LocalJmx {

	private static final String PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	protected static Map<String, String> env = new HashMap<String, String>();

	private String ipAddress = null;
	private int portNumber = 0;

	public LocalJmx() {
		super();
		setIpAddress(getLocalIpAddress());
		setPortNumber(5555);
	}

	public JMXConnector getJMXConnector() throws IOException {
	
		setUpTls();
		System.out.println("\nCreate a JMXMP connector client and " + "connect it to the JMXMP connector server");
		JMXServiceURL url = new JMXServiceURL("jmxmp", null, 5555);
		JMXConnector jmxc = JMXConnectorFactory.connect(url, env);
		
		return jmxc;
	}
	
	
	
	private static boolean validateIp(final String ip) {

		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}
	
	private static boolean validatePortNumber(final int portNumber) {
		
		if(portNumber < 1 || portNumber > 65535) return false;
		
		return true;
		
	}

	private String getLocalIpAddress() {

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

	protected void parseArguments(String programName, String[] args) {

		String USAGE_MSG = "Kullanım: " + programName + " [-ipAddress] [-portNumber ]";

		String arg = "";
		int i = 0;

		String ipAddressArg = null;
		int portNumberArg = 0;

		System.out.println(USAGE_MSG);

		while (i < args.length && args[i].startsWith("-")) {

			arg = args[i++];

			try {
				ipAddressArg = arg.substring(1);
			} catch (Throwable t) {
				System.err.println(t.getLocalizedMessage());
			}

			// use this type of check for "wordy" arguments
			if (ipAddressArg == null || !validateIp(ipAddressArg)) {
				System.out.println(arg.substring(1) + " is not valid, using local ip address for server >> " + getIpAddress());
			} else {
				setIpAddress(ipAddressArg);
				System.out.println("Using given ip address for server >> " + getIpAddress());
			}

			arg = args[i++];

			try {
				portNumberArg = Integer.parseInt(arg.substring(1));
			} catch (Throwable t) {
				System.err.println(t.getLocalizedMessage());
			}

			if (portNumberArg == 0 || !validatePortNumber(portNumberArg)) {
				System.out.println(arg.substring(1) + " is not valid, using 5555 for server >> " + getPortNumber());
			} else {
				setPortNumber(portNumberArg);
				System.out.println("Using given port number for server >> " + getPortNumber());
			}

		}

	}

	private void setUpTls() {

		if (System.getProperty("javax.net.ssl.trustStore") == null) {
			System.setProperty("javax.net.ssl.trustStore", "likyaKeystore");
			System.out.println("Using default values for trustStore, to change use -Djavax.net.ssl.trustStore as VM argument !");
		}

		if (System.getProperty("javax.net.ssl.keyStorePassword") == null) {
			System.setProperty("javax.net.ssl.keyStorePassword", "likya1!+");
			System.out.println("Using default values for keyStorePassword, to change use -Djavax.net.ssl.keyStorePassword as VM argument !");
		}

		if (System.getProperty("javax.net.ssl.keyStore") == null) {
			System.setProperty("javax.net.ssl.keyStore", "likyaKeystore");
			System.out.println("Using default values for keyStore, to change use -Djavax.net.ssl.keyStore as VM argument !");
		}

		env.put("jmx.remote.profiles", "TLS");
		env.put("jmx.remote.tls.need.client.authentication", "true");
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

}

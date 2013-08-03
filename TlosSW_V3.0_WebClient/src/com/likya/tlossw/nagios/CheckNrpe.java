package com.likya.tlossw.nagios;

import java.io.OutputStream;
import java.net.Socket;

public class CheckNrpe {
	public static final int DEFAULT_PORT = 5666;
	public static final int DEFAULT_TIMEOUT = 15;

	public static NrpePacket check(String host, int port, String command) throws Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append(command);

		CheckNrpe c = new CheckNrpe();
		// XXX still need to do something with the timeout
		NrpePacket p = c.executeQuery(host, port, buffer.toString(), NrpePacket.DEFAULT_PADDING);
		//		System.out.println(p.getBuffer());
		//		System.exit(p.getResultCode());
		return p;
	}

	public NrpePacket executeQuery(String host, String buffer) throws Exception {
		return executeQuery(host, DEFAULT_PORT, buffer, NrpePacket.DEFAULT_PADDING);
	}

	public NrpePacket executeQuery(String host, String buffer, int padding) throws Exception {
		return executeQuery(host, DEFAULT_PORT, buffer, padding);
	}

	public NrpePacket executeQuery(String host, int port, String buffer, int padding) throws Exception {

		NrpePacket p = new NrpePacket(NrpePacket.QUERY_PACKET, (short) 0, buffer);
		byte[] b = p.buildPacket(padding);
		Socket s = new Socket(host, port);
		OutputStream o = s.getOutputStream();
		o.write(b);

		NrpePacket nrpePacket = NrpePacket.receivePacket(s.getInputStream(), padding);

		s.close();

		return nrpePacket;
	}

	public NrpePacket sendPacket(short type, short resultCode, String buffer) throws Exception {

		int padding = NrpePacket.DEFAULT_PADDING;

		NrpePacket p = new NrpePacket(type, resultCode, buffer);
		byte[] b = p.buildPacket(padding);
		Socket s = new Socket("localhost", DEFAULT_PORT);
		OutputStream o = s.getOutputStream();
		o.write(b);

		NrpePacket nrpePacket = NrpePacket.receivePacket(s.getInputStream(), padding);

		s.close();

		return nrpePacket;
	}
}

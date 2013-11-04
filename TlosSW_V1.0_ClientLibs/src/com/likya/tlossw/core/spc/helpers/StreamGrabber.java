package com.likya.tlossw.core.spc.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;

import org.apache.log4j.Logger;

public class StreamGrabber extends Thread {
	
	private InputStream is;
	private String type;
	private StringBuffer outputBuffer;
	private BufferedReader bufferedReader;
	
	private Logger myLogger;
	
	boolean live = true;
	
	public StreamGrabber(InputStream is, String type, StringBuffer opBuff, Logger myLogger) {
		this.is = is;
		this.type = type;
		this.outputBuffer = opBuff;
		this.myLogger = myLogger;
	}
	
	public synchronized void stopStreamGobbler() {
		live = false;
	}

	public void run() {
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(is);

			bufferedReader = new BufferedReader(inputStreamReader);
			String bufferString = null;

			/*
			 * Not 1 : Eğer, bufferedReader.ready() olmadan
			 * bufferedReader.readLine() yapacak olur isek, thread okuma
			 * satırında takıldığında, terminate etmek işe yaramıyor.
			 */
			while (!bufferedReader.ready()) {
				if(!live) {
					bufferedReader.close();
					inputStreamReader.close();
					exitClass();
					return;
				}
				// System.out.println("StreamGobbler : Checking buffer if ready ...");
				Thread.sleep(200);
			}

			while ((bufferString = bufferedReader.readLine()) != null) {
				//SpaceWideRegistry.getSpaceWideLogger().debug(type + ">" + bufferString);
				myLogger.info(type + ">" + bufferString);
				
				outputBuffer.append(type + ">" + bufferString + "\n");
				while (!bufferedReader.ready()) {
					if(!live) {
						bufferedReader.close();
						inputStreamReader.close();
						exitClass();
						return;
					}
					// System.out.println("StreamGobbler : Checking buffer if ready ...");
					Thread.sleep(200);
				}				
			}
		} catch(InterruptedIOException iioe) {
			myLogger.debug("StreamGrabber : Terminating " + iioe.getMessage(), iioe);
		} catch (IOException ioe) {
			myLogger.debug("StreamGrabber : Terminating " + ioe.getMessage(), ioe);
		} catch (InterruptedException e) {
			myLogger.debug("StreamGrabber : Terminating " + this.getName());
		}
		
		exitClass();
	}
	
	private void exitClass() {
		try {
			is.close();
		} catch (IOException e) {
		}
		is = null;
	}

	public StringBuffer getOutputBuffer() {
		return outputBuffer;
	}
}
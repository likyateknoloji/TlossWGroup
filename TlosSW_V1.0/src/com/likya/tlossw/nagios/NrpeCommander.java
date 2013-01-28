package com.likya.tlossw.nagios;

import java.util.ArrayList;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.nrpe.MessageDocument.Message;
import com.likya.tlos.model.xmlbeans.nrpe.NrpeCallDocument.NrpeCall;
import com.likya.tlos.model.xmlbeans.nrpe.ResponseDocument.Response;
import com.likya.tlossw.db.utils.AgentDbUtils;
import com.likya.tlossw.db.utils.NrpeDbUtils;
import com.likya.tlossw.utils.XmlUtils;
import com.likya.tlossw.utils.date.DateUtils;

public class NrpeCommander implements Runnable {
	
	private ArrayList<NrpeCommand> nrpeCommanList = null;
	private SWAgent nrpeHost = null;
	private volatile boolean nrpePermission = true;
	private static final long TIME_TOSLEEP = 5000;
	transient private Thread nrpeThread;
	
	public NrpeCommander(ArrayList<NrpeCommand> nrpeCommanList,
			SWAgent nrpeHost) {
		super();
		this.nrpeCommanList = nrpeCommanList;
		this.nrpeHost = nrpeHost;
	}

	@Override
	public void run() {
		nrpeThread = Thread.currentThread();
		nrpeThread.setName(nrpeHost.getResource().getStringValue());
		while(nrpePermission){
			try {
				if(nrpeHost.getNrpeAvailable()) {
					try {
						commander();
					} catch (Exception e) {
						if(nrpeHost.getNrpeAvailable()) {
							AgentDbUtils.updateResourceNrpeValues(XmlUtils.getResourceXML(nrpeHost.getResource()), false); 
						}
						nrpeHost.setNrpeAvailable(false);
						e.printStackTrace();
					}
				}
				Thread.sleep(TIME_TOSLEEP);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void commander() throws Exception{
		NrpeCall nrpeCall = null;
		
		for(NrpeCommand nrpeCommand : nrpeCommanList) {
			if(nrpeCommand.getOsType().toString().equals(nrpeHost.getOsType().toString())) {
				NrpePacket nrpePacket = null;
				try {
					nrpePacket = CheckNrpe.check(nrpeHost.getResource().getStringValue(), nrpeHost.getNrpePort(), nrpeCommand.getCommand());
				} catch (Exception e) {
					e.printStackTrace();
				}
				nrpeCall = constructNrpeCall(nrpeHost, nrpeCommand.getCommand(), nrpePacket.getBuffer(), nrpeCall);
				System.out.println(nrpePacket.getBuffer());
			}
		}
		
		NrpeDbUtils.insertNrpe(nrpeCall);
	}
	
	public NrpeCall constructNrpeCall(SWAgent nrpeHost, String nrpeCommand, String response, NrpeCall nrpeCall) {
		
		Response nrpeResponse = Response.Factory.newInstance();
		nrpeResponse.setCommand(replaceSpecialXmlChars(nrpeCommand));
		nrpeResponse.setValue(response);
		
		if(nrpeCall == null) {
			nrpeCall = NrpeCall.Factory.newInstance(); 
			
			nrpeCall.setEntryName(nrpeHost.getResource().getStringValue());
			nrpeCall.setPort(nrpeHost.getNrpePort());
			nrpeCall.setOs(nrpeHost.getOsType().toString());
			
			Message nrpeMessage = Message.Factory.newInstance();
			nrpeMessage.setTime(DateUtils.getW3CDateTime());
			nrpeMessage.setId(DateUtils.getCurrentTimeMilliseconds()+"");

			nrpeMessage.addNewResponse();
			nrpeMessage.setResponseArray(nrpeMessage.getResponseArray().length-1, nrpeResponse);
			nrpeCall.addNewMessage();
			nrpeCall.setMessageArray(nrpeCall.getMessageArray().length-1, nrpeMessage);
		}else {
			nrpeCall.getMessageArray(0).addNewResponse();
			nrpeCall.getMessageArray(0).setResponseArray(nrpeCall.getMessageArray(0).getResponseArray().length - 1, nrpeResponse);
		}
		
		return nrpeCall;
	}
	
	public static String replaceSpecialXmlChars(String xmlText) {
		if(xmlText.indexOf("&") != -1) {
			xmlText = xmlText.replace("&", "&amp;");
		}
		if(xmlText.indexOf("<") != -1) {
			xmlText = xmlText.replace("<", "&lt;");
		}
		if(xmlText.indexOf(">") != -1) {
			xmlText = xmlText.replace(">", "&gt;");
		}
		
		return xmlText;
	}

	public Thread getNrpeThread() {
		return nrpeThread;
	}

	public boolean getNrpePermission() {
		return nrpePermission;
	}

	public void setNrpePermission(boolean nrpePermission) {
		this.nrpePermission = nrpePermission;
	}

	public ArrayList<NrpeCommand> getNrpeCommanList() {
		return nrpeCommanList;
	}

	public SWAgent getNrpeHost() {
		return nrpeHost;
	}
	
}

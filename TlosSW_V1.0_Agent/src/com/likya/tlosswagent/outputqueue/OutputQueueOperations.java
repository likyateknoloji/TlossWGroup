package com.likya.tlosswagent.outputqueue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Queue;

import com.likya.tlos.model.xmlbeans.agent.TxMessageBodyTypeDocument.TxMessageBodyType;
import com.likya.tlos.model.xmlbeans.agent.TxMessageDocument.TxMessage;
import com.likya.tlos.model.xmlbeans.agent.TxMessageTypeEnumerationDocument.TxMessageTypeEnumeration;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlossw.model.jmx.JmxAgentUser;
import com.likya.tlosswagent.TlosSWAgent;
import com.likya.tlosswagent.serverclient.TSWServerJmxClient;
import com.likya.tlosswagent.utils.SWAgentRegistry;
import com.likya.tlosswagent.utils.XmlUtils;

public class OutputQueueOperations {

	public static synchronized void addLiveStateInfo(LiveStateInfo liveStateInfo, String txMessageId) {

		TxMessage txMessage = TxMessage.Factory.newInstance();
		txMessage.setId(txMessageId);
		txMessage.setTxMessageTypeEnumeration(TxMessageTypeEnumeration.JOB_STATE);

		TxMessageBodyType txMessageBodyType = TxMessageBodyType.Factory.newInstance();
		txMessageBodyType.setLiveStateInfo(liveStateInfo);

		txMessage.setTxMessageBodyType(txMessageBodyType);

		TlosSWAgent.getSwAgentRegistry().getOutputQueManagerRef().addTxMessage(txMessage);

	}

	public static synchronized void addJobProperties(JobProperties jobProperties, String txMessageId) {

		TxMessage txMessage = TxMessage.Factory.newInstance();
		txMessage.setId(txMessageId);
		txMessage.setTxMessageTypeEnumeration(TxMessageTypeEnumeration.JOB);

		TxMessageBodyType txMessageBodyType = TxMessageBodyType.Factory.newInstance();
		txMessageBodyType.setJobProperties(jobProperties);

		txMessage.setTxMessageBodyType(txMessageBodyType);

		TlosSWAgent.getSwAgentRegistry().getOutputQueManagerRef().addTxMessage(txMessage);

	}

	public static synchronized boolean sendTxMessage(TxMessage txMessage) {

		String txMessageXML = XmlUtils.getTxMessageXML(txMessage);
		String serverHost = TlosSWAgent.getSwAgentRegistry().getAgentConfigInfo().getSettings().getServerInfo().getResource().getStringValue();
		int serverJmxPort = TlosSWAgent.getSwAgentRegistry().getAgentConfigInfo().getSettings().getServerInfo().getPortNumber();
		JmxAgentUser jmxAgentUser = TlosSWAgent.getSwAgentRegistry().getJmxAgentUser();

		boolean messageDelivery = TSWServerJmxClient.txMessageHandle(jmxAgentUser, serverHost, serverJmxPort, txMessageXML);

		return messageDelivery;

	}

	public static boolean persistOutputQueue(String fileName, Queue<TxMessage> outputQueue) {

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		if (outputQueue.size() == 0) {
			SWAgentRegistry.getsWAgentLogger().fatal("Çýkýþ kuyruðu boþ olmamalý !");
			SWAgentRegistry.getsWAgentLogger().fatal("Program sona erdi !");
			return false;
		}
		try {
			File fileTemp = new File(System.getProperty("tlosAgent.tmpdir") + "/" + fileName + ".temp"); //$NON-NLS-1$
			fos = new FileOutputStream(fileTemp);

			out = new ObjectOutputStream(fos);
			out.writeObject(outputQueue);
			out.close();

			File file = new File(System.getProperty("tlosAgent.tmpdir") + "/" + fileName);

			if (file.exists()) {
				file.delete();
			}

			fileTemp.renameTo(file);

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return true;

	}

	@SuppressWarnings("unchecked")
	public static boolean recoverOutputQueue(String fileName, Queue<TxMessage> outputQueue) {

		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			SWAgentRegistry.getsWAgentLogger().info("Cikis Kuyrugu Yerine konuyor (Recovering) !");
			fis = new FileInputStream(System.getProperty("tlosAgent.tmpdir") + "/" + fileName);
			in = new ObjectInputStream(fis);
			Object input = in.readObject();

			outputQueue.addAll((Queue<TxMessage>) input);
			in.close();

		} catch (FileNotFoundException fnf) {
			SWAgentRegistry.getsWAgentLogger().info("Recover dosyasi bulunamadigindan Cikis Kuyrugu Yerine konamadi,  (NOT Recovered) !");
			return false;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		} catch (NullPointerException  np) {
			np.printStackTrace();
			return false;
		} catch (SecurityException   se) {
			se.printStackTrace();
			return false;
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			return false;
		} finally {
			try {
				if(in!=null) in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		SWAgentRegistry.getsWAgentLogger().info("Cikis Kuyrugu Yerine kondu (Recovered) !");
		return true;
	}

}

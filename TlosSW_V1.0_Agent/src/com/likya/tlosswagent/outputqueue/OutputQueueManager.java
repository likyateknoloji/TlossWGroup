package com.likya.tlosswagent.outputqueue;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.agent.TxMessageDocument.TxMessage;
import com.likya.tlosswagent.exceptions.TlosFatalException;
import com.likya.tlosswagent.utils.SWAgentRegistry;

public class OutputQueueManager implements Runnable, Serializable {

	private static final long serialVersionUID = -2955959644804456769L;

	private SWAgentRegistry swAgentRegistry;

	private boolean executionPermission = true;

	transient private Thread executerThread;

	transient private Logger outputQueueLogger;

	private Queue<TxMessage> outputQueue = new LinkedList<TxMessage>();

	public boolean isRecoverAction = false;

	private boolean messageDelivery;
	
	private boolean logSendError = true;

	public OutputQueueManager(SWAgentRegistry swAgentRegistry) {
		super();
		this.swAgentRegistry = swAgentRegistry;
		this.outputQueueLogger = SWAgentRegistry.getsWAgentLogger();

		boolean isRecoverAction = getSwAgentRegistry().getAgentConfigInfo().getSettings().getIsPersistent().getValueBoolean();
		if (isRecoverAction) {
			OutputQueueOperations.recoverOutputQueue("outputQueue", outputQueue);
		}
	}

	@Override
	public void run() {

		while (isExecutionPermission()) {

			try {

				TxMessage txMessage = poll(outputQueue);

				if (txMessage != null) {
					messageDelivery = OutputQueueOperations.sendTxMessage(txMessage);

					if (messageDelivery) {
						SWAgentRegistry.getsWAgentLogger().info("Message send: " + txMessage.getId() + "-->" + txMessage.getTxMessageTypeEnumeration().toString());
						System.out.println("Message send: " + txMessage.getId() + "-->" + txMessage.getTxMessageTypeEnumeration().toString());
						logSendError = true;
					} else {
						if(logSendError) {
							SWAgentRegistry.getsWAgentLogger().info("Message send error ! " + txMessage.getId() + "-->" + txMessage.getTxMessageTypeEnumeration().toString());
							System.err.println("Message send error ! " + txMessage.getId() + "-->" + txMessage.getTxMessageTypeEnumeration().toString());
							logSendError = false;
						} else {
							System.out.print(".");
						}
						addTxMessage(txMessage);
						/**
						 * @author serkan
						 * mesaj gonderme islemi hata aldigi durumlarda, tekrar islemeye baslamadan bir sure beklemekte cok fayda var.
						 */
						Thread.sleep(1000);
					}

				}

				if (getOutputQueue() != null && getOutputQueue().size() > 0 && !OutputQueueOperations.persistOutputQueue("outputQueue", getOutputQueue())) {
					getOutputQueueLogger().error("Taskqueue persist error : Queue name : " + "taskQueue");
					throw new TlosFatalException();
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (TlosFatalException tfe) {
				tfe.printStackTrace();
			}

		}

	}

	public SWAgentRegistry getSwAgentRegistry() {
		return swAgentRegistry;
	}

	public boolean isExecutionPermission() {
		return executionPermission;
	}

	public void setExecutionPermission(boolean executionPermission) {
		this.executionPermission = executionPermission;
	}

	public Thread getExecuterThread() {
		return executerThread;
	}

	public void setExecuterThread(Thread executerThread) {
		this.executerThread = executerThread;
	}

	public Logger getOutputQueueLogger() {
		return outputQueueLogger;
	}

	public Queue<TxMessage> getOutputQueue() {
		return outputQueue;
	}

	public void setOutputQueue(Queue<TxMessage> outputQueue) {
		this.outputQueue = outputQueue;
	}

	protected synchronized void addTxMessage(TxMessage txMessage) {
		outputQueue.add(txMessage);
	}

	private synchronized TxMessage poll(Queue<TxMessage> outputQueue) {
		return outputQueue.poll();
	}

	public synchronized void resetOutputQueue() {
		outputQueue.removeAll(outputQueue);
	}

}

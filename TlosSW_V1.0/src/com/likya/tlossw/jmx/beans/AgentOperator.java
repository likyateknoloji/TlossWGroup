/*
 * TlosFaz_V2.0
 * com.likya.tlos.jmx.mp.helper : ProcessInfoProvider.java
 * @author Serkan Taş
 * Tarih : Apr 6, 2009 2:19:17 PM
 */

package com.likya.tlossw.jmx.beans;

import java.util.HashMap;

import com.likya.tlos.model.xmlbeans.agent.TxMessageDocument.TxMessage;
import com.likya.tlos.model.xmlbeans.agent.TxMessageTypeEnumerationDocument.TxMessageTypeEnumeration;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.cpc.model.RunInfoType;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.db.utils.AgentDbUtils;
import com.likya.tlossw.jmx.JMXServer;
import com.likya.tlossw.jmx.JMXTLSServer;
import com.likya.tlossw.model.engine.TxMessageIdBean;
import com.likya.tlossw.model.jmx.JmxAgentUser;
import com.likya.tlossw.model.path.BasePathType;
import com.likya.tlossw.model.path.TlosSWPathType;
import com.likya.tlossw.utils.XmlUtils;

public class AgentOperator implements AgentOperatorMBean {

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

	@Override
	public int intsquare(int x) {
		return x * x;
	}

	@Override
	public int checkJmxUser(JmxAgentUser jmxAgentUser) {
		int checkJmx = AgentDbUtils.checkJmxUser(jmxAgentUser);
		return checkJmx;
	}

	@Override
	public boolean txMessageHandle(JmxAgentUser jmxAgentUser, String txMessageXML) {
		if (!JMXServer.authorize(jmxAgentUser)) {
			return false;
		}

		System.out.println(txMessageXML);
		TxMessage txMessage = XmlUtils.convertToTxMessage(txMessageXML);
		TxMessageIdBean txMessageIdBean = XmlUtils.tokenizeTxIds(txMessage.getId());
		boolean isAgentAvailable = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference().getSwAgentCache(txMessageIdBean.getAgentId() + "").getJmxAvailable();

		String runId = txMessageIdBean.getRunId();
		String spcId = txMessageIdBean.getSpcId();
		String jobId = txMessageIdBean.getJobKey();
		
		TlosSWPathType tlosSWPathType = new TlosSWPathType(BasePathType.getRootPath() + "." + runId + "." + spcId);
		
		if (isAgentAvailable) {
			
			HashMap<String, RunInfoType> runLookupTable = TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable();

			if (txMessage.getTxMessageTypeEnumeration().equals(TxMessageTypeEnumeration.JOB_STATE)) {

				Job job = runLookupTable.get(runId).getSpcLookupTable().getTable().get(tlosSWPathType.getFullPath()).getSpcReferance().getJobQueue().get(jobId);
				job.insertNewLiveStateInfo(txMessage.getTxMessageBodyType().getLiveStateInfo());
				// job.changeStateInfo(txMessage.getTxMessageBodyType().getLiveStateInfo());
			} else if (txMessage.getTxMessageTypeEnumeration().equals(TxMessageTypeEnumeration.JOB)) {
				
				Spc spc = runLookupTable.get(txMessageIdBean.getRunId()).getSpcLookupTable().getTable().get(tlosSWPathType.getFullPath()).getSpcReferance();
				Job job = spc.getJobQueue().get(jobId);
				
				job.sendEndInfo(txMessageIdBean.getSpcId(), txMessage.getTxMessageBodyType().getJobProperties());
				if (txMessage.getTxMessageBodyType().getJobProperties().getAgentId() != 0) { // Baska
																								// ne
																								// olabilir
																								// ki?
					if (job.getJobRuntimeProperties().getJobProperties().getTimeManagement().getJsRealTime().getStartTime() == null) {
						job.getJobRuntimeProperties().getJobProperties().getTimeManagement().getJsRealTime().addNewStartTime().setTime(txMessage.getTxMessageBodyType().getJobProperties().getTimeManagement().getJsRealTime().getStartTime().getTime());
					} else {
						job.getJobRuntimeProperties().getJobProperties().getTimeManagement().getJsRealTime().getStartTime().setTime(txMessage.getTxMessageBodyType().getJobProperties().getTimeManagement().getJsRealTime().getStartTime().getTime());
					}

					if (txMessage.getTxMessageBodyType().getJobProperties().getTimeManagement().getJsRealTime().getStopTime() != null) {
						job.getJobRuntimeProperties().getJobProperties().getTimeManagement().getJsRealTime().addNewStopTime().setTime(txMessage.getTxMessageBodyType().getJobProperties().getTimeManagement().getJsRealTime().getStopTime().getTime());
					}

					// ftp, file listener gibi islerde log dosyasinin ismine zaman ifadesi eklendigi icin agentta calisan islerde bu bilgi sunucu tarafinda set ediliyor
					String jobLogFile = txMessage.getTxMessageBodyType().getJobProperties().getBaseJobInfos().getJobLogFile();
					job.getJobRuntimeProperties().getJobProperties().getBaseJobInfos().setJobLogFile(jobLogFile);

					/*
					 * job.getJobRuntimeProperties().setPlannedExecutionDate(
					 * txMessage
					 * .getTxMessageBodyType().getJobProperties().getJobRealTime
					 * ().getStartTime().getTime()+"");
					 * job.getJobRuntimeProperties
					 * ().setRealExecutionDate(txMessage
					 * .getTxMessageBodyType().getJobProperties
					 * ().getJobRealTime().getStartTime().getTime());
					 * job.getJobRuntimeProperties
					 * ().setPlannedExecutionDate(txMessage
					 * .getTxMessageBodyType().getPlannedExecutionDate());
					 * 
					 * addNewJobRealTime().addNewStartTime().setTime(txMessage.
					 * getTxMessageBodyType
					 * ().getJobProperties().getJobRealTime()
					 * .getStartTime().getTime());
					 */
				}
			}

		}

		return true;
	}

	@Override
	public void pulse(JmxAgentUser jmxAgentUser) {
		TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference().updateHeartBeatTime(jmxAgentUser);
	}

	public Object retrieveGlobalStates(JmxAgentUser jmxAgentUser) {

		if (!JMXTLSServer.authorizeAgent(jmxAgentUser)) {
			return false;
		}

		String globalStateDefinitionXML = XmlUtils.getGlobalStateDefinitionsXML(jmxAgentUser);

		return globalStateDefinitionXML;
	}

}

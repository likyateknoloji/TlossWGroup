package com.likya.tlossw.core.spc.helpers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import net.java.dev.eval.Expression;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.cpc.Cpc;
import com.likya.tlossw.core.cpc.model.InstanceInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.exceptions.TlosFatalException;
import com.likya.tlossw.exceptions.UnresolvedDependencyException;

public class DependencyResolver {
	
	public static boolean isJobDependencyResolved(Logger logger, Job ownerJob, String dependencyExpression, Item[] dependencyArray, String instanceId, HashMap<String, Job> jobQueue, HashMap<String, SpcInfoType> spcLookupTable) throws UnresolvedDependencyException {
	
		String ownerJsName = ownerJob.getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJsName();
		
		dependencyExpression = dependencyExpression.replace("AND", "&&");
		dependencyExpression = dependencyExpression.replace("OR", "||");

		Expression exp = new Expression(dependencyExpression);
		BigDecimal result = new BigDecimal(0);

		ArrayIterator dependencyArrayIterator = new ArrayIterator(dependencyArray);

		Map<String, BigDecimal> variables = new HashMap<String, BigDecimal>();

		while (dependencyArrayIterator.hasNext()) {

			Item item = (Item) (dependencyArrayIterator.next());
			JobRuntimeProperties jobRuntimeProperties = null;
			
		
			if (dependencyExpression.indexOf(item.getDependencyID().toUpperCase()) < 0) {
				String errorMessage = "     > " + ownerJsName + " isi icin hatali bagimlilik tanimlamasi yapilmis ! (" + dependencyExpression + ") kontrol ediniz.";
				logger.info(errorMessage);
				logger.error(errorMessage);
				throw new UnresolvedDependencyException(errorMessage);
			}

			if (item.getJsPath() == null || item.getJsPath() == "") { 
				// Lokal bir bagimlilik
				if (jobQueue.get(item.getJsId()) == null) {
					SWErrorOperations.logErrorForItemJsId(logger, ownerJsName, item.getJsName(), ownerJob.getJobRuntimeProperties().getTreePath(), ownerJob.getJobRuntimeProperties().getJobProperties().getID());
				}
				jobRuntimeProperties = jobQueue.get(item.getJsName()).getJobRuntimeProperties();
			} else { 
				// Global bir bagimlilik
				SpcInfoType spcInfoType = spcLookupTable.get(Cpc.getRootPath() + "." + instanceId + "." + item.getJsPath());

				if (spcInfoType == null) {
					SWErrorOperations.logErrorForSpcInfoType(logger, ownerJsName, item.getJsPath(), instanceId, ownerJob.getJobRuntimeProperties().getTreePath(), spcLookupTable);
				}

				Job job = spcInfoType.getSpcReferance().getJobQueue().get(item.getJsId());
				if (job == null) {
					SWErrorOperations.logErrorForJob(logger, ownerJsName, item.getJsName(), item.getJsPath(), instanceId, spcInfoType.getSpcReferance().getSpcId());
				}

				jobRuntimeProperties = job.getJobRuntimeProperties();
			}

			if (jobRuntimeProperties.getJobProperties() == null) {
				logger.info("     > jobRuntimeProperties.getJobProperties() == null !!");
				throw new UnresolvedDependencyException("     > jobRuntimeProperties.getJobProperties() == null !!");
			}
			
			LiveStateInfo liveStateInfo = jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0);
			
			StateName.Enum jobStateName = liveStateInfo.getStateName();
			SubstateName.Enum jobSubstateName = liveStateInfo.getSubstateName();
			StatusName.Enum jobStatusName = liveStateInfo.getStatusName();
			
			StateName.Enum itemStateName = item.getJsDependencyRule().getStateName();
			SubstateName.Enum itemSubstateName = item.getJsDependencyRule().getSubstateName();
			StatusName.Enum itemStatusName = item.getJsDependencyRule().getStatusName();
			

			if (itemStateName != null && itemSubstateName == null && itemStatusName == null) {
				if (jobStateName.equals(itemStateName)) {
					variables.put(item.getDependencyID(), new BigDecimal(1)); // true
				} else {
					variables.put(item.getDependencyID(), new BigDecimal(0)); // false
				}
			} else if (itemStateName != null && itemSubstateName != null && itemStatusName == null) {
				if (jobStateName.equals(itemStateName) && jobSubstateName.equals(itemSubstateName)) {
					variables.put(item.getDependencyID(), new BigDecimal(1)); // true
				} else {
					variables.put(item.getDependencyID(), new BigDecimal(0)); // false
				}
			} else if (itemStateName != null && itemSubstateName != null && itemStatusName != null && jobStateName != null && jobSubstateName != null) {
				if (jobStateName.equals(itemStateName) && jobSubstateName.equals(itemSubstateName)) {
					if (jobStatusName != null)
						if (jobStatusName.equals(itemStatusName)) {
							variables.put(item.getDependencyID(), new BigDecimal(1)); // true
						} else {
							variables.put(item.getDependencyID(), new BigDecimal(0)); // false
						}
				} else {
					variables.put(item.getDependencyID(), new BigDecimal(0)); // false
				}
			} else {
				return false;
			}

		}

		result = exp.eval(variables);
		
		return result.intValue() == 0 ? false : true;
	}
	
	public static boolean isScenarioDependencyResolved(Logger logger, DependencyList dependencyList, String spcId, String jsName, String instanceId, LiveStateInfo liveStateInfo, HashMap<String, SpcInfoType> spcLookUpTable, HashMap<String, InstanceInfoType> instanceLookUpTable) throws TlosFatalException {
		
		if (dependencyList == null || dependencyList.getItemArray().length == 0) {
			// There is no dependency defined so it is allowed to execute
			return true;
		} else {
			String dependencyExpression = dependencyList.getDependencyExpression();
			Item[] dependencyArray = dependencyList.getItemArray();

			dependencyExpression = dependencyExpression.replace("AND", "&&");
			dependencyExpression = dependencyExpression.replace("OR", "||");

			Expression exp = new Expression(dependencyExpression);
			BigDecimal result = new BigDecimal(0);

			ArrayIterator dependencyArrayIterator = new ArrayIterator(dependencyArray);

			Map<String, BigDecimal> variables = new HashMap<String, BigDecimal>();

			while (dependencyArrayIterator.hasNext()) {

				Item item = (Item) (dependencyArrayIterator.next());
				Spc spc = null;

				if (dependencyExpression.indexOf(item.getDependencyID().toUpperCase()) < 0) {
					logger.error("Hatalı tanımlama ! Uygulama sona eriyor !");
					throw new TlosFatalException();
				}

				if (item.getJsPath() == null || item.getJsPath() == "") {
					logger.error("Hatalı sanal bağımlılık ! Tanımı yapılan senaryonun yolu yanlış ! Sernaryo adı : " + item.getJsName());
					logger.error("Ana senaryo adı : " + spcId);
					logger.error("Ana senaryo yolu : " + jsName);
					logger.error("Uygulama sona eriyor !");
					throw new TlosFatalException();
				} else {

					SpcInfoType spcInfoType = InstanceMapHelper.findSpc(item.getJsPath(), instanceLookUpTable);

					if (spcInfoType == null) {
						logger.error("Genel bağımlılık tanımı yapılan senaryo bulunamadı : " + Cpc.getRootPath() + "." + instanceId + "." + item.getJsPath());
						logger.error("Ana senaryo adı : " + spcId);
						logger.error("Ana senaryo yolu : " + jsName);
						logger.error("Uygulama sona eriyor !");
						Cpc.dumpSpcLookupTable(instanceId, spcLookUpTable);
						throw new TlosFatalException();
					}

					spc = spcInfoType.getSpcReferance();
				}

				if (item.getJsDependencyRule().getStateName() != null && item.getJsDependencyRule().getSubstateName() == null && item.getJsDependencyRule().getStatusName() == null) {
					if (spc.getLiveStateInfo().getStateName().equals(item.getJsDependencyRule().getStateName())) {
						variables.put(item.getDependencyID(), new BigDecimal(1)); // true
					} else {
						variables.put(item.getDependencyID(), new BigDecimal(0)); // false
					}
				} else if (item.getJsDependencyRule().getStateName() != null && item.getJsDependencyRule().getSubstateName() != null && item.getJsDependencyRule().getStatusName() == null) {
					if (spc.getLiveStateInfo().getStateName().equals(item.getJsDependencyRule().getStateName()) && spc.getLiveStateInfo().getSubstateName().equals(item.getJsDependencyRule().getSubstateName())) {
						variables.put(item.getDependencyID(), new BigDecimal(1)); // true
					} else {
						variables.put(item.getDependencyID(), new BigDecimal(0)); // false
					}
				} else if (item.getJsDependencyRule().getStateName() != null && item.getJsDependencyRule().getSubstateName() != null && item.getJsDependencyRule().getStatusName() != null) {
					if (spc.getLiveStateInfo().getStateName().equals(item.getJsDependencyRule().getStateName()) && spc.getLiveStateInfo().getSubstateName().equals(item.getJsDependencyRule().getSubstateName()) && spc.getLiveStateInfo().getStatusName().equals(item.getJsDependencyRule().getStatusName())) {
						variables.put(item.getDependencyID(), new BigDecimal(1)); // true
					} else {
						variables.put(item.getDependencyID(), new BigDecimal(0)); // false
					}
				} else {
					return false;
				}

			}

			result = exp.eval(variables);

			boolean retValue = (result.intValue() == 0 ? false : true);

			if (!retValue) {
				liveStateInfo.setStateName(StateName.PENDING);
				liveStateInfo.setSubstateName(SubstateName.READY);
			} else {
				liveStateInfo.setStateName(StateName.RUNNING);
			}

			return retValue;
		}

	}

}

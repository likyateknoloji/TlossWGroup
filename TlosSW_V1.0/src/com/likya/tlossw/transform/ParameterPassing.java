package com.likya.tlossw.transform;

/**
 [Class description.  This class is being used for parameter pass between jobs.]

 [Other notes, including guaranteed invariants, usage instructions and/or examples, reminders
 about desired improvements, etc.]

 @author <A HREF="mailto:hakan.saribiyik@likyateknoloji.com">Hakan Saribiyik</A>
 @version $Revision: 1.1.1.1 $ $Date: 2012/08/17 15:15:25 $
 @see [String]
 @see [URL]
 @see [ParameterPassing#passParameter]
 **/

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.xmlbeans.XmlException;

import com.likya.tlos.model.xmlbeans.common.InParamDocument.InParam;
import com.likya.tlos.model.xmlbeans.common.LocalParametersDocument.LocalParameters;
import com.likya.tlos.model.xmlbeans.common.OutParamDocument.OutParam;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.cpc.model.RunInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.exceptions.TlosFatalException;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.xml.ApplyXPath;

public class ParameterPassing {

	public void findInputValues(String xpath) throws TlosFatalException {
		for (String runId : TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable().keySet()) {

			findInputValue(runId, xpath);
		}

		return;
	}

	private String[] findInputValue(String runId, String xpath) throws TlosFatalException {

		RunInfoType runInfoType = TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable().get(runId);

		HashMap<String, SpcInfoType> spcLookupTable = runInfoType.getSpcLookupTable().getTable();

		for (String spcId : spcLookupTable.keySet()) {

			Spc spc = spcLookupTable.get(spcId).getSpcReferance();
			try {
				return findInputValue(spc, xpath);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	private String[] findInputValue(Spc spcc, String sorgu) throws Throwable {

		JobProperties xmlDoc = null;

		Iterator<Job> jobsIterator = spcc.getJobQueue().values().iterator();

		while (jobsIterator.hasNext()) {
			Job job = jobsIterator.next();

			xmlDoc = job.getJobRuntimeProperties().getJobProperties();

			return ApplyXPath.queryXmlWithXPath(xmlDoc, sorgu);
			// applyXPath.queryXmlWithXPath(xmlDoc, xpath);
		}
		return null;
	}

	public Boolean setOutputParameter(JobProperties job, String result, String parameterName) {

		Boolean assignmentOk = false;

		if (job instanceof JobProperties) {
			LocalParameters localParameter = job.getLocalParameters();
			if (localParameter == null) {
				job.addNewLocalParameters();
			}
			if (localParameter.getOutParam() == null) {
				localParameter.addNewOutParam();
			}
			OutParam outParam = localParameter.getOutParam();
			Boolean paramF = false;
			for (int j = 0; j < outParam.sizeOfParameterArray(); j++) {
				paramF = outParam.getParameterArray(j).getName().equalsIgnoreCase(parameterName);
				if (paramF) {
					outParam.getParameterArray(j).getPreValue().setStringValue(result);
					outParam.getParameterArray(j).getPreValue().setType(new BigInteger("2"));
					outParam.getParameterArray(j).setValueString(result);
					assignmentOk = true;
					break;
				}
			}
			if (!paramF) {
				outParam.addNewParameter();
				outParam.getParameterArray(outParam.getParameterArray().length - 1).addNewPreValue();
				outParam.getParameterArray(outParam.getParameterArray().length - 1).getPreValue().setStringValue(result);
				outParam.getParameterArray(outParam.getParameterArray().length - 1).getPreValue().setType(new BigInteger("2"));
				outParam.getParameterArray(outParam.getParameterArray().length - 1).setValueString(result);
				assignmentOk = true;
			}
		}
		if (assignmentOk)
			return true;
		else
			return false;
	}

	public Boolean setInputParameter(JobProperties job) {

		Boolean assignmentOk = false;
		/**** parametre gecisi varsa yapalim **************/

		// Once job in kullandigi parametreler icinde baska bir job in output u
		// olan parametre var mi diye bakalim.
		// Bunun icin
		// com:specialParameters/com:inParam/par:parameter[par:preValue/@type=\"6\"]
		// kosulu aranir.

		String inputs[] = null, inputPar[] = null;
		String xpath = "/dat:jobProperties/com:localParameters/com:inParam/par:parameter[par:preValue/@type=\"6\"]";
		try {
			inputs = ApplyXPath.queryXmlWithXPath(job, xpath);
			Parameter parameterList[] = new Parameter[inputs.length];
			if (inputs != null && inputs.length > 0) {
				try {
					// Simdi input olan parametrenin degerini ilgili job dan
					// alalim.
					for (int i = 0; i < inputs.length; i++) {

						parameterList[i] = ParameterDocument.Factory.parse(inputs[i]).getParameter();
						String sorgu = "/" + parameterList[i].getPreValue().getStringValue();

						inputPar = findInputValue(SpaceWideRegistry.getInstance().getTlosProcessData().getRunId(), sorgu);

						System.out.println("Gecen parametre = " + inputPar[0].toString());

						Parameter parameterInput = ParameterDocument.Factory.parse(inputPar[0]).getParameter();
						String parameterName = parameterInput.getName();
						String result = parameterInput.getValueString();

						if (job instanceof JobProperties) {
							LocalParameters localParameter = job.getLocalParameters();
							if (localParameter == null) {
								job.addNewLocalParameters();
							}
							if (localParameter.getInParam() == null) {
								localParameter.addNewInParam();
							}
							InParam inParam = localParameter.getInParam();
							Boolean paramF = false;
							for (int j = 0; j < inParam.sizeOfParameterArray(); j++) {
								paramF = inParam.getParameterArray(j).getName().equalsIgnoreCase(parameterName);
								if (paramF) {
									inParam.getParameterArray(j).getPreValue().setStringValue(result);
									inParam.getParameterArray(j).getPreValue().setType(new BigInteger("2"));
									inParam.getParameterArray(j).setValueString(result);
									assignmentOk = true;
									break;
								}
							}
							if (!paramF) {
								inParam.addNewParameter();
								inParam.getParameterArray(0).getPreValue().setStringValue(result);
								inParam.getParameterArray(0).getPreValue().setType(new BigInteger("2"));
								inParam.getParameterArray(0).setValueString(result);
								assignmentOk = true;
							}
						}
					}

				} catch (XmlException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (assignmentOk)
			return true;
		else
			return false;
	}
}

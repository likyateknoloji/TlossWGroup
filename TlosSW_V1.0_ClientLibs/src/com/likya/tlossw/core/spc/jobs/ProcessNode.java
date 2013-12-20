package com.likya.tlossw.core.spc.jobs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.LocalParametersDocument.LocalParameters;
import com.likya.tlos.model.xmlbeans.common.SpecialParametersDocument.SpecialParameters;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlos.model.xmlbeans.processnode.ProcessNodeDocument;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.helpers.ParamList;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.date.DateUtils;
import com.likya.tlossw.utils.xml.ApplyXslt;

public class ProcessNode extends Job {

	private static final long serialVersionUID = -1819467510271574850L;

	private Logger myLogger = Logger.getLogger(ProcessNode.class);

	private Writer outputFile = null;

	private boolean retryFlag = true;

	transient protected Process process;

	public final static String PN_RESULT = "output1";

	public final static String XSLT_CODE = "XSLTKodu";

	public ProcessNode(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(globalRegistry, globalLogger, jobRuntimeProperties);
	}

	public void localRun() {

		initStartUp(myLogger);

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();
		ArrayList<ParamList> myParamList = new ArrayList<ParamList>();

		while (true) {

			try {

				startWathcDogTimer();

				String logFilePath = jobProperties.getBaseJobInfos().getJobLogPath();
				String logFileName = jobProperties.getBaseJobInfos().getJobLogFile().substring(0, jobProperties.getBaseJobInfos().getJobLogFile().indexOf('.')) + "_" + DateUtils.getCurrentTimeForFileName() + jobProperties.getBaseJobInfos().getJobLogFile().substring(jobProperties.getBaseJobInfos().getJobLogFile().indexOf('.'), jobProperties.getBaseJobInfos().getJobLogFile().length());

				String logFile = ParsingUtils.getConcatenatedPathAndFileName(logFilePath, logFileName);

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_RUNNING, SubstateName.INT_ON_RESOURCE, StatusName.INT_TIME_IN);

				try {
					setOutputFile(new BufferedWriter(new FileWriter(logFile)));
				} catch (IOException e) {
					handleLogException(e, myLogger);
				}

				// TODO log dosyasinin ismine zaman damgasi verildigi icin bu ismi dailyScenarios.xml'de guncellemek gerekiyor
				// DBUtils.insertLogFileNameForJob(jobProperties, jobPath, logFileName);

				jobProperties.getBaseJobInfos().setJobLogFile(logFileName);

				ParamList thisParam = new ParamList(PN_RESULT, "STRING", "VARIABLE", processInput());
				myParamList.add(thisParam);

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_SUCCESS);

				try {
					// outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Result:" + System.getProperty("line.separator"));
					outputFile.write((String) myParamList.get(0).getParamRef());

				} catch (IOException e) {
					handleLogException(e, myLogger);
				}

			} catch (Exception err) {
				handleException(err, myLogger);

				try {
					outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " XSLT transformasyon isinde hata !" + System.getProperty("line.separator"));
					outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " " + err.getMessage() + System.getProperty("line.separator"));

					ParamList thisParam = new ParamList(ERR_RESULT, "STRING", "VARIABLE", err.getMessage());
					myParamList.add(thisParam);
					
					for (StackTraceElement element : err.getStackTrace()) {
						outputFile.write("\t" + element.toString() + System.getProperty("line.separator"));
					}

				} catch (IOException ioe) {
					ioe.printStackTrace();
				}

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED);
			}

			if (processJobResult(retryFlag, myLogger)) {
				retryFlag = false;
				continue;
			}

			break;
		}

		cleanUp(process, myLogger);

		try {
			outputFile.close();
			
			ParamList thisParam = new ParamList(LOG_RESULT, "STRING", "VARIABLE", outputFile.toString());
			myParamList.add(thisParam);
			
			processJobResult(retryFlag, myLogger, myParamList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String processInput() {

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();
		String fileContent = null;
		// StreamSource XSLTCode = null;

		LocalParameters localParameters = jobProperties.getLocalParameters();

		if (localParameters != null && localParameters.getInParam() != null) {

			Parameter[] inParamList = localParameters.getInParam().getParameterArray();
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>(Arrays.asList(inParamList));

			Iterator<Parameter> parameterIterator = parameterList.iterator();

			while (parameterIterator.hasNext()) {

				Parameter parameter = parameterIterator.next();

				if (parameter.getIoName() == null || !parameter.getIoName().contains("input"))
					continue;
				/*
				 * if (parameter.getName().equals(ProcessNode.XSLT_CODE)) {
				 * 
				 * StringReader xslReader = new StringReader(parameter.getValueString());
				 * 
				 * XSLTCode = new StreamSource(xslReader);
				 * //break; illaki islenecek birseyler bulmasi lazim. hs
				 * 
				 * }
				 */
//				if (parameter.getIoType()) { // OutPut
//					if (parameter.getName().equals(FileProcessExecuter.READ_FILE_RESULT)) {
//						fileContent = parameter.getValueString();
//						// break; sonuclardan birini alacagiz, nasil bir mantik kurgulamali? hs
//
//					} else if (parameter.getName().equals(WebServiceExecuter.WS_RESULT)) {
//						fileContent = parameter.getValueString();
//						// break;
//					} else if (parameter.getName().equals(JDBCPostgreSQLSentenceExecuter.DB_RESULT)) {
//						fileContent = parameter.getValueString();
//						// break;
//					} else if (parameter.getName().equals(PN_RESULT)) {
//						fileContent = parameter.getValueString();
//						// break;
//					}
//				}
				// input parametresi ilgili degiskene ataniyor.
				if (!parameter.getIoType() && parameter.getIoName().equalsIgnoreCase("input1")) { // OutPut
					fileContent = parameter.getValueString();
				}
			}
		}

//		if (jobProperties.getLocalParameters() != null && jobProperties.getLocalParameters().getInParam() != null) {
//
//			Parameter[] inParamList = jobProperties.getLocalParameters().getInParam().getParameterArray();
//			ArrayList<Parameter> parameterList = new ArrayList<Parameter>(Arrays.asList(inParamList));
//
//			Iterator<Parameter> parameterIterator = parameterList.iterator();
//
//			while (parameterIterator.hasNext()) {
//
//				Parameter parameter = parameterIterator.next();
//
//				if (parameter.getName() == null)
//					continue;
//				/*
//				 * if (parameter.getName().equals(ProcessNode.XSLT_CODE)) {
//				 * 
//				 * StringReader xslReader = new StringReader(parameter.getValueString());
//				 * 
//				 * XSLTCode = new StreamSource(xslReader);
//				 * 
//				 * }
//				 */
//				if (parameter.getName().equals(FileProcessExecuter.READ_FILE_RESULT)) {
//					fileContent = parameter.getValueString();
//					// break; sonuclardan birini alacagiz, nasil bir mantik kurgulamali? hs
//
//				} else if (parameter.getName().equals(WebServiceExecuter.WS_RESULT)) {
//					fileContent = parameter.getValueString();
//					// break;
//				} else if (parameter.getName().equals(JDBCPostgreSQLSentenceExecuter.DB_RESULT)) {
//					fileContent = parameter.getValueString();
//					// break;
//				} else if (parameter.getName().equals(PN_RESULT)) {
//					fileContent = parameter.getValueString();
//					// break;
//				}
//			}
//		}

		String transformedXML = "";

		if (fileContent != null) {
			SpecialParameters specialParameters = jobProperties.getBaseJobInfos().getJobTypeDetails().getSpecialParameters();

			if (specialParameters != null && specialParameters.getProcessNodes() != null) {
				ProcessNodeDocument.ProcessNode[] myProcessNode = specialParameters.getProcessNodes().getProcessNodeArray();
				ArrayList<ProcessNodeDocument.ProcessNode> ProcessNodeList = new ArrayList<ProcessNodeDocument.ProcessNode>(Arrays.asList(myProcessNode));

				Iterator<ProcessNodeDocument.ProcessNode> processNodeIterator = ProcessNodeList.iterator();

				while (processNodeIterator.hasNext()) {

					ProcessNodeDocument.ProcessNode processNode = processNodeIterator.next();

					// XML Doc process
					if (processNode.getProcess().getSource() != null && processNode.getProcess().getSource().toString().equalsIgnoreCase("xml")) {
						if (processNode.getTransform() != null && processNode.getTransform().getWith().toString().equalsIgnoreCase("xslt")) {

							try {
								if (processNode.getTransform().getStringValue() == null)
									// XSLTCode = TransformUtils.getStreamSource(); // Default cevrim. hs
									transformedXML = ApplyXslt.transformXML(fileContent, getTransformXsl());
								else {
									// XSLTCode = TransformUtils.getStreamSource(processNode.getTransform().getStringValue());
									transformedXML = ApplyXslt.transformXML(fileContent, processNode.getTransform().getStringValue());
								}

								outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + "\n " + "Output from Tlos SW");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								insertNewLiveStateInfo(StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED, e.getMessage());
								e.printStackTrace();
							}
						} else if (processNode.getFilter() != null && processNode.getFilter().getType().toString().equalsIgnoreCase("xpath")) {
							// String inputs[] = null;
							// String xpath = processNode.getFilter().getStringValue();
							// TODO XPath i normal xml icin yapmamisiz. Gerceklestirelim. hs.
							// inputs = ApplyXPath.queryXmlWithXPath(fileContent, xpath);
							System.out.println("Filtreleme icin XPATH yerine XSLT kullanin !!");
							insertNewLiveStateInfo(StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED, "Filtreleme icin XPATH yerine XSLT kullanilmali !!");
						} else if (processNode.getTransform() != null && processNode.getTransform().getWith().toString().equalsIgnoreCase("xpath")) {
							System.out.println("Transformasyon icin XPATH kullanmiyoruz. XSLT kullanin !!");
							insertNewLiveStateInfo(StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED, "Transformasyon icin XPATH kullanmiyoruz. XSLT kullanilmali !!");
						}
					} else if (processNode.getProcess().getSource() != null && processNode.getProcess().getSource().toString().equalsIgnoreCase("text")) {
						// TODO Text doc process
						if (processNode.getFilter() != null && processNode.getFilter().getType() != null && processNode.getFilter().getType().toString().equalsIgnoreCase("stringFunction")) {
							// String inputs[] = null;
							// String filterString = processNode.getFilter().getStringValue();

							System.out.println("Filtreleme icin XPATH yerine XSLT kullanin !!");
							insertNewLiveStateInfo(StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED, "Filtreleme icin XPATH yerine XSLT kullanilmali !!");
						} else if (processNode.getReplace() != null && processNode.getReplace().getFind() != null && !processNode.getReplace().getFind().toString().isEmpty()) {
							String replaceThis = processNode.getReplace().getFind().toString();
							String withThis = processNode.getReplace().getStringValue().toString();
							transformedXML = fileContent.replace(replaceThis, withThis);
						}
					} else {
						System.out.println("XML ve TEXT Doc dışında doküman işleyemiyoruz henüz.");
						insertNewLiveStateInfo(StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED, "XML ve TEXT Doc disinda dokuman isleyemiyoruz henuz! ");
					}
				}
			}

		}
		return transformedXML;
	}

	public Writer getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(Writer outputFile) {
		this.outputFile = outputFile;
	}

}

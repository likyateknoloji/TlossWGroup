package com.likya.tlossw.core.spc.jobs;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.wsdl.extensions.http.HTTPBinding;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap12.SOAP12Binding;
import javax.xml.namespace.QName;

import org.apache.commons.lang.ClassUtils;
import org.apache.cxf.common.jaxb.JAXBUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ClientImpl;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingMessageInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.log4j.Logger;

import com.ibm.wsdl.extensions.http.HTTPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12BindingImpl;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlos.model.xmlbeans.webservice.BindingNameDocument.BindingName;
import com.likya.tlos.model.xmlbeans.webservice.OperationDocument.Operation;
import com.likya.tlos.model.xmlbeans.webservice.WebServiceDefinitionDocument.WebServiceDefinition;
import com.likya.tlossw.core.spc.helpers.ParamList;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.cxf.CXFUtils;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.date.DateUtils;

public class WebServiceExecuter extends Job {

	private static final long serialVersionUID = -1819467510271574850L;

	private Logger myLogger = Logger.getLogger(WebServiceExecuter.class);

	private Writer outputFile = null;

	private boolean retryFlag = true;

	transient protected Process process;

	public final static String WS_RESULT = "wsResult";

	public WebServiceExecuter(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
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

				sendStatusChangeInfo();

				try {
					setOutputFile(new BufferedWriter(new FileWriter(logFile)));
				} catch (IOException e) {
					handleException(e, myLogger);
				}

				// TODO log dosyasinin ismine zaman damgasi verildigi icin bu ismi dailyScenarios.xml'de guncellemek gerekiyor
				// DBUtils.insertLogFileNameForJob(jobProperties, jobPath, logFileName);

				jobProperties.getBaseJobInfos().setJobLogFile(logFileName);

				ParamList thisParam = new ParamList(WS_RESULT, "STRING", "VARIABLE", callOperation());
				myParamList.add(thisParam);

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_SUCCESS);
				sendStatusChangeInfo();

				try {
					outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Result:" + System.getProperty("line.separator"));
					outputFile.write((String) myParamList.get(0).getParamRef() + System.getProperty("line.separator"));

				} catch (IOException e) {
					handleException(e, myLogger);
				}

			} catch (Exception err) {
				handleException(err, myLogger);

				try {
					outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Web servis isinde hata !" + System.getProperty("line.separator"));
					outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " " + err.getMessage() + System.getProperty("line.separator"));

					for (StackTraceElement element : err.getStackTrace()) {
						outputFile.write("\t" + element.toString() + System.getProperty("line.separator"));
					}

				} catch (IOException ioe) {
					ioe.printStackTrace();
				}

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED);
				sendStatusChangeInfo();
			}

			if (processJobResult(retryFlag, myLogger, myParamList)) {
				retryFlag = false;
				continue;
			}

			break;
		}

		cleanUp(process, myLogger);

		try {
			outputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String callOperation() {

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();
		WebServiceDefinition webService = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters().getWebServiceDefinition();

		// sadece soap 1.1 icin calisiyor
		if (!webService.getBindingList().getBindingArray(0).getBindingName().equals(BindingName.SOAP_1_1)) {
			try {
				outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + "Unsupported binding type : " + webService.getBindingList().getBindingArray(0).getBindingName() + System.getProperty("line.separator"));
			} catch (IOException e) {
				handleException(e, myLogger);
			}

			return "";
		}

		String wsdlAddress = webService.getWsdlAddress();

		Operation operation = webService.getBindingList().getBindingArray(0).getOperationArray(0);

		JaxWsDynamicClientFactory jaxWsDynamicClientFactory = JaxWsDynamicClientFactory.newInstance();

		Client client = jaxWsDynamicClientFactory.createClient(wsdlAddress);

		ClientImpl clientImpl = (ClientImpl) client;

		Endpoint endpoint = clientImpl.getEndpoint();

		ArrayList<ServiceInfo> serviceInfoList = (ArrayList<ServiceInfo>) endpoint.getService().getServiceInfos();
		Iterator<ServiceInfo> serviceInfoListIterator = serviceInfoList.iterator();

		while (serviceInfoListIterator.hasNext()) {
			ServiceInfo serviceInfo = (ServiceInfo) serviceInfoListIterator.next();

			Collection<BindingInfo> bindingCollection = serviceInfo.getBindings();
			Iterator<BindingInfo> iterator = bindingCollection.iterator();

			while (iterator.hasNext()) {
				BindingInfo bindingInfo = iterator.next();

				// System.out.println(bindingInfo.getBindingId());
				// System.out.println(bindingInfo.getName());

				AtomicReference<Object[]> extensorList = bindingInfo.getExtensors();

				try {
					Object extensorObject = extensorList.get()[0];

					if (extensorObject instanceof HTTPBinding) {

						// http get ve postu burada yaziyor
						HTTPBindingImpl httpBindingImpl = (HTTPBindingImpl) extensorObject;
						outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + "\nBinding : HTTP : " + httpBindingImpl.getVerb() + System.getProperty("line.separator"));

						continue;
					} else if (extensorObject instanceof SOAPBinding) {

						SOAPBindingImpl soapBindingImpl = (SOAPBindingImpl) extensorObject;

						outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + "\nBinding : SOAP1.1 : " + soapBindingImpl.getTransportURI() + System.getProperty("line.separator"));

					} else if (extensorObject instanceof SOAP12Binding) {

						// soap1.2 yi burada yaziyor
						SOAP12BindingImpl soap12BindingImpl = (SOAP12BindingImpl) extensorObject;
						outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + "\nBinding : SOAP1.2 :" + soap12BindingImpl.getTransportURI() + System.getProperty("line.separator"));

						continue;
					} else {
						LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED);

						sendStatusChangeInfo();

						throw new Throwable("Unsupported binding type: " + extensorObject.getClass().getCanonicalName());
					}

					Collection<BindingOperationInfo> bindingOperationInfoList = bindingInfo.getOperations();
					Iterator<BindingOperationInfo> bindingOperationInfoIterator = bindingOperationInfoList.iterator();

					// her metoda tek tek bakiyor
					while (bindingOperationInfoIterator.hasNext()) {
						BindingOperationInfo bindingOperationInfo = bindingOperationInfoIterator.next();

						// parametreleri girilen fonksiyonu buluyor
						if (operation.getOperationName().equals(bindingOperationInfo.getOperationInfo().getName().getLocalPart())) {

							BindingMessageInfo inputMessageInfo = bindingOperationInfo.getInput();
							List<MessagePartInfo> parts = inputMessageInfo.getMessageParts();

							MessagePartInfo partInfo = parts.get(0);

							// metodun sinifi
							Class<?> partClass = partInfo.getTypeClass();

							if (partClass == null) {
								QName typeQName = partInfo.getTypeQName();
								String builtInJavaType = JAXBUtils.builtInTypeToJavaType(typeQName.getLocalPart());
								try {
									partClass = Class.forName(builtInJavaType);
								} catch (ClassNotFoundException e) {
									handleException(e, myLogger);
								}
							}

							outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + "\n\tOperation : " + partClass.getCanonicalName() + System.getProperty("line.separator"));

							Object inputObject = null;

							try {
								inputObject = partClass.newInstance();

							} catch (InstantiationException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}

							// metodun parametreleri
							Field[] fields = partClass.getDeclaredFields();

							for (int i = 0; i < fields.length; i++) {
								outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + "\t\t" + (i + 1) + ".field: " + fields[i].getName() + " " + fields[i].getType() + System.getProperty("line.separator"));

								PropertyDescriptor propertyDescriptor = null;

								try {
									propertyDescriptor = new PropertyDescriptor(fields[i].getName(), partClass);
								} catch (IntrospectionException e) {
									e.printStackTrace();
								}

								Object inputParameter;

								if (!operation.getParameterList().getParameterArray(i).getIsEnum()) {
									// This is the type of the class which really contains all the parameter information.
									Class<?> propertyType = propertyDescriptor.getPropertyType();

									inputParameter = CXFUtils.newInstance(propertyType, operation.getParameterList().getParameterArray(i).getParameterValue());
								} else {
									Class<? extends Enum> propertyType = (Class<? extends Enum>) propertyDescriptor.getPropertyType();

									inputParameter = Enum.valueOf(propertyType, operation.getParameterList().getParameterArray(i).getParameterValue().toString());
								}

								try {
									propertyDescriptor.getWriteMethod().invoke(inputObject, inputParameter);
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
							}

							Object[] result = null;

							result = client.invoke(bindingOperationInfo, inputObject);

							String resultStr = "";

							if (result != null) {

								resultStr = resolveResult(result[0], resultStr, null);

								return resultStr;
							}
							outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + "Result is null" + System.getProperty("line.separator"));

							return null;
						}

					}

				} catch (Throwable e) {

					try {
						outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " Web Servis cagirmada hata !" + System.getProperty("line.separator"));
						outputFile.write(DateUtils.getCurrentTimeWithMilliseconds() + " " + e.getMessage() + System.getProperty("line.separator"));

						for (StackTraceElement element : e.getStackTrace()) {
							outputFile.write("\t" + element.toString() + System.getProperty("line.separator"));
						}

					} catch (IOException ioe) {
						ioe.printStackTrace();
					}

					LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED);
					sendStatusChangeInfo();
				}
			}
		}

		return "";
	}

	public static String resolveResult(Object result, String resultStr, String methodName) {

		if (result != null) {
			Class<?> resultClass = result.getClass();

			if (resultClass.isPrimitive() || ClassUtils.wrapperToPrimitive(resultClass) != null || resultClass.equals(String.class)) {

				if (methodName != null) {
					resultStr += "<" + methodName + ">";
					resultStr += result.toString();
					resultStr += "</" + methodName + ">";
				} else {
					resultStr += result.toString();
				}

				return resultStr;

			} else if (resultClass.equals(Byte[].class) || resultClass.equals(byte[].class)) {
				resultStr += "<" + methodName + ">";
				resultStr += new String((byte[]) result);
				resultStr += "</" + methodName + ">";

				return resultStr;

			} else if (result instanceof ArrayList<?>) {

				List<?> myResultList = (List<?>) result;
				Iterator<?> resultListIterator = myResultList.iterator();

				while (resultListIterator.hasNext()) {
					Object myResultInnerObject = resultListIterator.next();

					resultStr = resolveResult(myResultInnerObject, resultStr, null);
				}

			} else {
				resultStr += "<" + resultClass.getSimpleName() + ">";

				Method[] resultMethods = resultClass.getDeclaredMethods();

				Object[] o = null;

				for (int i = 0; i < resultMethods.length; i++) {

					if (!resultMethods[i].getReturnType().equals(void.class)) {

						Object methodResult = null;

						try {
							methodResult = resultMethods[i].invoke(result, o);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						resultStr = resolveResult(methodResult, resultStr, resultMethods[i].getName());
					}
				}

				resultStr += "</" + resultClass.getSimpleName() + ">";
			}
		}

		return resultStr;
	}

	public Writer getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(Writer outputFile) {
		this.outputFile = outputFile;
	}

}

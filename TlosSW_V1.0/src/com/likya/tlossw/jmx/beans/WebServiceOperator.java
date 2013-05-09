package com.likya.tlossw.jmx.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
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

import com.ibm.wsdl.extensions.http.HTTPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12BindingImpl;
import com.likya.tlossw.cxf.CXFUtils;
import com.likya.tlossw.jmx.JMXTLSServer;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.model.webservice.Function;
import com.likya.tlossw.model.webservice.Parameter;
import com.likya.tlossw.model.webservice.WebService;

public class WebServiceOperator implements WebServiceOperatorMBean {
	
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
	public WebService getWsOperationList(JmxUser jmxUser, String wsdlAddress) {
		
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}
		
		WebService webService = new WebService();
		
		ArrayList<Function> functionList = new ArrayList<Function>();
		
		JaxWsDynamicClientFactory jaxWsDynamicClientFactory = JaxWsDynamicClientFactory.newInstance();
		
		Client client = jaxWsDynamicClientFactory.createClient(wsdlAddress, ClassLoader.getSystemClassLoader());

		ClientImpl clientImpl = (ClientImpl) client;

		Endpoint endpoint = clientImpl.getEndpoint();
		
		ArrayList<ServiceInfo> serviceInfoList = (ArrayList<ServiceInfo>) endpoint.getService().getServiceInfos();
		Iterator<ServiceInfo> serviceInfoListIterator = serviceInfoList.iterator();
		
		while(serviceInfoListIterator.hasNext()) {
			ServiceInfo serviceInfo = (ServiceInfo)serviceInfoListIterator.next();
		
			Collection<BindingInfo> bindingCollection = serviceInfo.getBindings();
			Iterator<BindingInfo> iterator = bindingCollection.iterator();
			
			while(iterator.hasNext()) {
				BindingInfo bindingInfo = iterator.next();
				
				System.out.println(bindingInfo.getBindingId());
				System.out.println(bindingInfo.getName());
				
				AtomicReference<Object[]> extensorList = bindingInfo.getExtensors();
				
				try {
					Object extensorObject = extensorList.get()[0];
					
					if(extensorObject instanceof HTTPBinding) {
						
						//http get ve postu burada yaziyor
						HTTPBindingImpl httpBindingImpl = (HTTPBindingImpl)extensorObject;
						System.out.println("\nBinding : HTTP : " + httpBindingImpl.getVerb());
						
						continue;
					} else if(extensorObject instanceof SOAPBinding) {
						
						//SOAPBindingImpl tipine cast edebiliyorsa operasyon listesine ve parametrelerine bakiyor edemezse zaten catche dusuyor
						SOAPBindingImpl soapBindingImpl = (SOAPBindingImpl)extensorObject;

						System.out.println("\nBinding : SOAP1.1 : " + soapBindingImpl.getTransportURI());
						
					} else if(extensorObject instanceof SOAP12Binding) {
						
						//soap1.2 yi burada yaziyor
						SOAP12BindingImpl soap12BindingImpl = (SOAP12BindingImpl)extensorObject;
						System.out.println("\nBinding : SOAP1.2 :" + soap12BindingImpl.getTransportURI());
						
						continue;
					} else {
						throw new Throwable("Unsupported binding type: " + extensorObject.getClass().getCanonicalName());
					}
					
					String serviceName = serviceInfo.getName().getLocalPart();
					webService.setServiceName(serviceName);
					
					Collection<BindingOperationInfo> bindingOperationInfoList = bindingInfo.getOperations();
					Iterator<BindingOperationInfo> bindingOperationInfoIterator = bindingOperationInfoList.iterator();
					
					Function function;
					
					//her metoda tek tek bakiyor
					while(bindingOperationInfoIterator.hasNext()) {
						BindingOperationInfo bindingOperationInfo = bindingOperationInfoIterator.next();
//						System.out.println(bindingOperationInfo.getOperationInfo().getName().toString()); 
						
						function = new Function();
						function.setFunctionName(bindingOperationInfo.getOperationInfo().getName().getLocalPart());
						
						
						BindingMessageInfo inputMessageInfo = bindingOperationInfo.getInput();
						List<MessagePartInfo> parts = inputMessageInfo.getMessageParts();
						
				        MessagePartInfo partInfo = parts.get(0);
				        
				        //metodun sinifi
				        Class<?> partClass = partInfo.getTypeClass();
				        
				        if(partClass == null) {
				        	QName typeQName = partInfo.getTypeQName();
				        	String builtInJavaType = JAXBUtils.builtInTypeToJavaType(typeQName.getLocalPart());
				        	try {
								partClass = Class.forName(builtInJavaType);
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
				        }
				        
				        System.out.println("\n\tOperation : " + partClass.getCanonicalName()); 
				        
				        ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
				        
				        //metodun parametreleri
				        Field[] fields = partClass.getDeclaredFields();
				        
				        for(int i = 0; i < fields.length; i++) {
				        	 System.out.println("\t\t" + (i+1) + ".field: " + fields[i].getName() + " " + fields[i].getType());
				        	 
				        	 Parameter parameter = new Parameter();
				        	 
				        	 if(fields[i].getType().isPrimitive()) {
				        		 
				        		 String javaBuiltInType =  JAXBUtils.builtInTypeToJavaType(fields[i].getType().toString());
				        		 parameter.setParameterType(javaBuiltInType);
				        	 } else {
				        		 parameter.setParameterType(fields[i].getType().toString());
				        	 }
				        	 
				        	//ENUM KONTROLU
				        	 if(fields[i].getType().getEnumConstants() != null) {
				        		 parameter.setIsEnum(true);

				        		 String[] enumList = new String[fields[i].getType().getEnumConstants().length];

				        		 for(int j = 0; j < fields[i].getType().getEnumConstants().length; j++) {
				        			 enumList[j] = fields[i].getType().getEnumConstants()[j].toString();
				        		 }
				        		 parameter.setEnumList(enumList);
				        	 }
					        	
				        	 parameter.setParameterName(fields[i].getName());
				        	 
				        	 parameterList.add(parameter);
				        }
				        
				        function.setParameterList(parameterList);
				        functionList.add(function);
					}
					
				} catch (Throwable e) {
					
					e.printStackTrace();
				}
			}
		}
		webService.setFunctionList(functionList);
		
		return webService;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String callOperation(JmxUser jmxUser, String wsdlAddress, Function function) {
		
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}
		
		JaxWsDynamicClientFactory jaxWsDynamicClientFactory = JaxWsDynamicClientFactory.newInstance();
		
		Client client = jaxWsDynamicClientFactory.createClient(wsdlAddress);
		
		
		ClientImpl clientImpl = (ClientImpl) client;

		Endpoint endpoint = clientImpl.getEndpoint();
		
		ArrayList<ServiceInfo> serviceInfoList = (ArrayList<ServiceInfo>) endpoint.getService().getServiceInfos();
		Iterator<ServiceInfo> serviceInfoListIterator = serviceInfoList.iterator();
		
		while(serviceInfoListIterator.hasNext()) {
			ServiceInfo serviceInfo = (ServiceInfo)serviceInfoListIterator.next();
//			System.out.println("\tserviceInfo.getDocumentation() : " + serviceInfo.getDocumentation());	
		
			Collection<BindingInfo> bindingCollection = serviceInfo.getBindings();
			Iterator<BindingInfo> iterator = bindingCollection.iterator();
			
			while(iterator.hasNext()) {
				BindingInfo bindingInfo = iterator.next();
				
				System.out.println(bindingInfo.getBindingId());
				System.out.println(bindingInfo.getName());
				
				AtomicReference<Object[]> extensorList = bindingInfo.getExtensors();
				
				try {
					Object extensorObject = extensorList.get()[0];
					
					if(extensorObject instanceof HTTPBinding) {
						
						//http get ve postu burada yaziyor
						HTTPBindingImpl httpBindingImpl = (HTTPBindingImpl)extensorObject;
						System.out.println("\nBinding : HTTP : " + httpBindingImpl.getVerb());
						
						continue;
					} else if(extensorObject instanceof SOAPBinding) {
						
						//SOAPBindingImpl tipine cast edebiliyorsa operasyon listesine ve parametrelerine bakiyor edemezse zaten catche dusuyor
						SOAPBindingImpl soapBindingImpl = (SOAPBindingImpl)extensorObject;

						System.out.println("\nBinding : SOAP1.1 : " + soapBindingImpl.getTransportURI());
						
					} else if(extensorObject instanceof SOAP12Binding) {
						
						//soap1.2 yi burada yaziyor
						SOAP12BindingImpl soap12BindingImpl = (SOAP12BindingImpl)extensorObject;
						System.out.println("\nBinding : SOAP1.2 :" + soap12BindingImpl.getTransportURI());
						
						continue;
					} else {
						throw new Throwable("Unsupported binding type: " + extensorObject.getClass().getCanonicalName());
					}
					
					Collection<BindingOperationInfo> bindingOperationInfoList = bindingInfo.getOperations();
					Iterator<BindingOperationInfo> bindingOperationInfoIterator = bindingOperationInfoList.iterator();
					
					//her metoda tek tek bakiyor
					while(bindingOperationInfoIterator.hasNext()) {
						BindingOperationInfo bindingOperationInfo = bindingOperationInfoIterator.next();
//						System.out.println(bindingOperationInfo.getOperationInfo().getName().toString()); 
						
						//parametreleri girilen fonksiyonu buluyor
						if(function.getFunctionName().equals(bindingOperationInfo.getOperationInfo().getName().getLocalPart())) {
							
							BindingMessageInfo inputMessageInfo = bindingOperationInfo.getInput();
							List<MessagePartInfo> parts = inputMessageInfo.getMessageParts();
							
					        MessagePartInfo partInfo = parts.get(0);
					        
					        //metodun sinifi
					        Class<?> partClass = partInfo.getTypeClass();
					        
					        if(partClass == null) {
					        	QName typeQName = partInfo.getTypeQName();
					        	String builtInJavaType = JAXBUtils.builtInTypeToJavaType(typeQName.getLocalPart());
					        	try {
									partClass = Class.forName(builtInJavaType);
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								}
					        }
					        
					        System.out.println("\n\tOperation : " + partClass.getCanonicalName()); 
					        
//					        ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
					        
					        Object inputObject = null;
					        
							try {
								inputObject = partClass.newInstance();
								
							} catch (InstantiationException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
					        
					        //metodun parametreleri
					        Field[] fields = partClass.getDeclaredFields();
					        
					        for(int i = 0; i < fields.length; i++) {
					        	System.out.println("\t\t" + (i+1) + ".field: " + fields[i].getName() + " " + fields[i].getType());

					        	PropertyDescriptor propertyDescriptor = null;

					        	try {
					        		propertyDescriptor = new PropertyDescriptor(fields[i].getName(), partClass);
					        	} catch (IntrospectionException e) {
					        		e.printStackTrace();
					        	}

					        	Object inputParameter;
					        	
					        	if(!function.getParameterList().get(i).getIsEnum()) {
						        	// This is the type of the class which really contains all the parameter information.
						        	Class<?> propertyType = propertyDescriptor.getPropertyType(); 
						        	
						        	inputParameter = CXFUtils.newInstance(propertyType, function.getParameterList().get(i).getValue());
					        	} else {
					        		@SuppressWarnings({ "rawtypes" })
									Class<? extends Enum> propertyType = (Class<? extends Enum>) propertyDescriptor.getPropertyType(); 
					        		
					        		inputParameter = Enum.valueOf(propertyType, function.getParameterList().get(i).getValue().toString());
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
							try {
								result = client.invoke(bindingOperationInfo, inputObject);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							String resultStr = "";
							
							if(result != null) {

								resultStr = resolveResult(result[0], resultStr, null);
								
								System.out.println(resultStr);

								return resultStr;
							}
							
							return null;
						}
						
					}
					
				} catch (Throwable e) {
					
					e.printStackTrace();
				}
			}
		}
		
		return "bo�";
	}
	
	public static String resolveResult(Object result, String resultStr, String methodName) {
		
		if(result != null) {
			Class<?> resultClass = result.getClass();
			
			if(resultClass.isPrimitive() || ClassUtils.wrapperToPrimitive(resultClass) != null || resultClass.equals(String.class)){

				if(methodName != null) {
					resultStr += "<" + methodName + ">";
					resultStr += result.toString();
					resultStr += "</" + methodName + ">";
				} else {
					resultStr += result.toString();
				}
				//resultStr += "</" + resultClass.getSimpleName() + ">";
				return resultStr;

			} else if (resultClass.equals(Byte[].class) || resultClass.equals(byte[].class)) {
				resultStr += "<" + methodName + ">";
				resultStr += new String((byte[])result);
				resultStr += "</" + methodName + ">";
				
//				resultStr += "</" + resultClass.getSimpleName() + ">";
				
				return resultStr;
	        	
	        } else if(result instanceof ArrayList<?>) {
//	        	resultStr += "<" + resultClass.getSimpleName() + ">";
	        	
				List<?> myResultList = (List<?>) result;
				Iterator<?> resultListIterator = myResultList.iterator();

				while(resultListIterator.hasNext()) {
					Object myResultInnerObject = resultListIterator.next();

					resultStr = resolveResult(myResultInnerObject, resultStr, null);
				}

			} else {
				resultStr += "<" + resultClass.getSimpleName() + ">";
				
				Method[] resultMethods = resultClass.getDeclaredMethods();

				Object[] o = null;

				for(int i = 0; i < resultMethods.length; i++) {

					if(!resultMethods[i].getReturnType().equals(void.class)) {

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
						//resultStr += "<" + resultMethods[i].getName() + ">";
						resultStr = resolveResult(methodResult, resultStr, resultMethods[i].getName());
						//resultStr += "</" + resultMethods[i].getName() + ">";
					}
				}

				resultStr += "</" + resultClass.getSimpleName() + ">";
			}
		} 
		
		return resultStr;
	}
	
	public static Object newInstance(Class<?> type, Object value){
	    if (!type.isPrimitive()) {
	        try {
				Object o = type.newInstance();
				o = value;
				return o;
				
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
	    } else {
	        if (type.equals(Integer.class) || type.equals(int.class)) {
	            return new Integer(Integer.parseInt(value.toString()));
	            
	        } else if (type.equals(Float.class) || type.equals(float.class)) {
	        	return new Float(Float.parseFloat(value.toString())); 
	        	
	        }
	        else if (type.equals(Double.class) || type.equals(double.class)) {
	        	return new Double(Double.parseDouble(value.toString())); 
	        	
	        } else if (type.equals(Byte.class) || type.equals(byte.class)) {
	        	return new Byte(Byte.parseByte(value.toString())); 
	        
	        } else if (type.equals(Short.class) || type.equals(short.class)) {
	        	return new Short(Short.parseShort(value.toString())); 
	        	
	        } else if (type.equals(Long.class) || type.equals(long.class)) {
	        	return new Long(Long.parseLong(value.toString())); 
	        	
	        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
	        	return new Boolean(Boolean.parseBoolean(value.toString())); 
	        	
	        } else if (type.equals(Character.class) || type.equals(char.class)) {
	        	return new Character((Character) value); 
	        	
	        } else {
	        	return null;
	        }
	    }
	    return null;
	}

}

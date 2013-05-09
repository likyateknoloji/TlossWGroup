package com.likya.tlossw.webclient;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.model.webservice.Function;
import com.likya.tlossw.model.webservice.WebService;

public class TEJmxMpWSClient extends TEJmxMpClientBase {

	private TEJmxMpWSClient() {
		
	}
	
	/**
	 * Verilen wsdl adresindeki web servisin operasyon listesini sunucudan istiyor
	 * 
	 * @param jmxUser Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param wsdlAddress wsdl adresi
	 * @return operasyon listesi
	 */
	public static WebService getWsOperationList(JmxUser jmxUser, String wsdlAddress) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, wsdlAddress };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String"};
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=6"), "getWsOperationList", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return (WebService) o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Verilen wsdl fonksiyonunun sonucunu sunucudan istiyor
	 * 
	 * @param jmxUser Jmx sunucusuna baglanmak icin gerekli kullanici bilgileri
	 * @param wsdlAddress wsdl adresi
	 * @param function icinde fonksiyon ismi, parametre isimleri, tipleri ve degerlerinin oldugu fonksiyon nesnesi
	 * @return fonksiyonun sonucunu donuyor
	 */
	public static String callOperation(JmxUser jmxUser, String wsdlAddress, Function function) {

		JMXConnector jmxConnector = TEJmxMpClient.getJMXConnection();

		Object[] paramList = { jmxUser, wsdlAddress, function };
		String[] signature = { "com.likya.tlossw.model.jmx.JmxUser", "java.lang.String", "com.likya.tlossw.model.webservice.Function"};
		Object o;
		try {
			MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
			o = mbeanServerConnection.invoke(new ObjectName("MBeans:type=6"), "callOperation", paramList, signature);
			TEJmxMpClient.disconnect(jmxConnector);
			return o.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

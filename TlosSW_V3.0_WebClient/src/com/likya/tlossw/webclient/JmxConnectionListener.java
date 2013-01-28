package com.likya.tlossw.webclient;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnectionNotification;

public class JmxConnectionListener implements NotificationListener {

	
	public void handleNotification(Notification notification, Object handback) {
		
		JMXConnectionNotification jmxConnectionNotification = (JMXConnectionNotification) notification;
		
		String notificationType = jmxConnectionNotification.getType();
		
		if(!notificationType.equals(JMXConnectionNotification.OPENED)) {
			handback = null;
		}
	}

}

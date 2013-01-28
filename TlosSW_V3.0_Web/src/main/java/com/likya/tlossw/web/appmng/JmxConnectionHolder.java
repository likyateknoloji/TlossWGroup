package com.likya.tlossw.web.appmng;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.management.remote.JMXConnector;

import com.likya.tlossw.webclient.TEJmxMpClient;
import com.likya.tlossw.webclient.TEJmxMpClientBase;

@ManagedBean(name = "jmxConnectionHolder")
@SessionScoped
public class JmxConnectionHolder implements Serializable {

	private static final long serialVersionUID = -7948774998363534929L;

	JMXConnector jmxConnector;

	@PostConstruct
	public void startUp() {
		TEJmxMpClientBase.tryReconnect = true;
		jmxConnector = TEJmxMpClient.getJMXConnection();
		String connId;
		if (jmxConnector != null) {
			try {
				connId = jmxConnector.getConnectionId();
				System.err.println(connId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@PreDestroy
	public void dispose() {
		if (jmxConnector != null) {
			try {
				TEJmxMpClientBase.tryReconnect = false;
				String connId = null;
				if (jmxConnector != null) {
					connId = jmxConnector.getConnectionId();
				}
				jmxConnector.close();
				System.out.println("Session disposed > " + connId);
				jmxConnector = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public JMXConnector getJmxConnector() {
		if (jmxConnector == null) {
			return TEJmxMpClient.getJMXConnection();
		}
		return jmxConnector;
	}

}

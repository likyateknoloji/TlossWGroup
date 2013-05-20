package com.likya.tlossw.test.webclient;

import javax.management.remote.JMXConnector;

import com.likya.tlossw.model.auth.AppUser;
import com.likya.tlossw.model.jmx.JmxAppUser;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.model.tree.resource.ResourceListNode;
import com.likya.tlossw.model.tree.resource.TlosSWResourceNode;
import com.likya.tlossw.web.appmng.JmxConnectionHolder;
import com.likya.tlossw.webclient.TEJmxMpClient;
import com.likya.tlossw.webclient.TEJmxMpDBClient;

public class TestTEJmxMpClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// TestTEJmxMpClient.getLiveResourceTreeInfo();
		TestTEJmxMpClient.authanticate();
	}

	public static void getLiveResourceTreeInfo() {

		TlosSWResourceNode requestResourceNode = new TlosSWResourceNode();
		ResourceListNode resourceListNode = new ResourceListNode();
		requestResourceNode.setResourceListNode(resourceListNode);

		// TlosSWResourceNode tlosSpaceWideInputNode = preparePreRenderLiveTree((DefaultMutableTreeNode) ((DefaultMutableTreeNode) getModel().getRoot()).getChildAt(0));
		// sunucudan guncel makine listesini ve o makinelerdeki agent listelerini aliyor
		TlosSWResourceNode responseResourceNode = TEJmxMpClient.getLiveResourceTreeInfo(new JmxUser(), requestResourceNode);

		System.out.println(responseResourceNode.toString());

	}

	public static void authanticate() {

		JMXConnector jmxConnector;

		String userName = "admin";
		String userPassword = "admin";

		JmxConnectionHolder jmxConnectionHolder = new JmxConnectionHolder();
		jmxConnectionHolder.startUp();

		JmxAppUser jmxAppUser = new JmxAppUser();
		AppUser appUser = new AppUser();
		appUser.setUsername(userName);
		appUser.setPassword(userPassword);

		jmxAppUser.setAppUser(appUser);

		int i = 0;
		
		while (i < 100) {
			
			jmxConnector = jmxConnectionHolder.getJmxConnector();

			Object o = TEJmxMpDBClient.checkUser(jmxConnector, jmxAppUser);

			if (o instanceof JmxUser) {
				//System.out.println("Authanticated !");
			} else {
				//System.out.println("NOT Authanticated !");
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}

	}
}

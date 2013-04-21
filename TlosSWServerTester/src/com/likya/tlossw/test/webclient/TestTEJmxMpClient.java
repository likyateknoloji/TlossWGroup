package com.likya.tlossw.test.webclient;

import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.model.tree.resource.ResourceListNode;
import com.likya.tlossw.model.tree.resource.TlosSWResourceNode;
import com.likya.tlossw.webclient.TEJmxMpClient;

public class TestTEJmxMpClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		TestTEJmxMpClient.getLiveResourceTreeInfo();
		
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
}

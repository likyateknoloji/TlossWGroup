package com.likya.tlossw.web.live;

import javax.faces.bean.ManagedBean;

import org.primefaces.model.TreeNode;
import org.primefaces.model.DefaultTreeNode;

@ManagedBean(name = "demoTreeBean")
public class DemoTreeBean {

	private TreeNode root;

	@SuppressWarnings("unused")
	public DemoTreeBean() {
		root = new DefaultTreeNode("Root", null);
		TreeNode node0 = new DefaultTreeNode("First", "First Node", root);
		TreeNode node1 = new DefaultTreeNode("Second", "Second Node", root);
		TreeNode node2 = new DefaultTreeNode("Third", "Third Node", root);

		TreeNode node3 = new DefaultTreeNode("Third", "Fourth Node", node0);
		TreeNode node4 = new DefaultTreeNode("Third", "Fifth Node", node1);
		TreeNode node5 = new DefaultTreeNode("Third", "Sixth Node", node2);
		
	}

	public TreeNode getRoot() {
		return root;
	}
}

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
		
		}

	public TreeNode getRoot() {
		return root;
	}
}
					
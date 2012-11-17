package org.encog.ml.tree;

import java.util.List;

public interface TreeNode {

	public List<TreeNode> getChildNodes();
	public void addChildNodes(TreeNode[] args);
	public boolean allLeafChildren();
	public boolean isLeaf();
	public int size();
	
}

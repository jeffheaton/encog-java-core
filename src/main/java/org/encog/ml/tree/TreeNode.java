package org.encog.ml.tree;

import java.util.List;

public interface TreeNode<T> {

	public List<T> getChildNodes();
	public void addChildNodes(T[] args);
	public boolean allLeafChildren();
	public boolean isLeaf();
	
}

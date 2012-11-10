package org.encog.ml.tree.traverse;

import org.encog.ml.tree.TreeNode;

public class DepthFirstTraversal<T extends TreeNode<T>> implements TreeTraversal<T> {

	@Override
	public void traverse(T treeNode, TreeTraversalTask<T> task) {
		task.task(treeNode);
		
		for(T childNode : treeNode.getChildNodes()) {
			traverse(childNode,task);
		}
	}

}

package org.encog.ml.tree.traverse;

import org.encog.ml.tree.TreeNode;

public class DepthFirstTraversal implements TreeTraversal {

	@Override
	public void traverse(TreeNode treeNode, TreeTraversalTask task) {
		if( !task.task(treeNode) )
			return;
		
		for(TreeNode childNode : treeNode.getChildNodes()) {
			traverse(childNode,task);
		}
	}

}

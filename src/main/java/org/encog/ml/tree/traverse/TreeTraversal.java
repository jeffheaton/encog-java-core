package org.encog.ml.tree.traverse;

import org.encog.ml.tree.TreeNode;

public interface TreeTraversal {
	void traverse(TreeNode tree, TreeTraversalTask task);
	
}

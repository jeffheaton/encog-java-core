package org.encog.ml.tree.traverse;

import org.encog.ml.tree.TreeNode;

public interface TreeTraversal<T extends TreeNode<T>> {
	void traverse(T tree, TreeTraversalTask<T> task);
	
}

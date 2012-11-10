package org.encog.ml.tree.traverse;

import org.encog.ml.tree.TreeNode;

public interface TreeTraversalTask<T extends TreeNode<T>> {
	boolean task(T node);
}

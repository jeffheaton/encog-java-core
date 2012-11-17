package org.encog.ml.tree.traverse;

import org.encog.ml.tree.TreeNode;

public interface TreeTraversalTask {
	boolean task(TreeNode node);
}

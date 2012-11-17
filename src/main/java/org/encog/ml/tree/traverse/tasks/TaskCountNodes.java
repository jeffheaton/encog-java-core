package org.encog.ml.tree.traverse.tasks;

import org.encog.ml.tree.TreeNode;
import org.encog.ml.tree.traverse.TreeTraversalTask;

public class TaskCountNodes implements TreeTraversalTask {
	private int nodeCount;
	
	public TaskCountNodes() {
		this.nodeCount = 0;
	}

	@Override
	public boolean task(TreeNode node) {
		this.nodeCount++;
		return true;
	}

	public int getNodeCount() {
		return nodeCount;
	}

	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}
	
	public static int process(TreeNode node) {
		return 0;
	}

}

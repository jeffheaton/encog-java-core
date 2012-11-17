package org.encog.ml.tree.traverse.tasks;

import org.encog.ml.tree.TreeNode;
import org.encog.ml.tree.traverse.DepthFirstTraversal;
import org.encog.ml.tree.traverse.TreeTraversalTask;

public class TaskGetNodeIndex implements TreeTraversalTask {
	private int nodeCount;
	private int targetIndex;
	private TreeNode result;
	
	public TaskGetNodeIndex(int theIndex) {
		this.targetIndex = theIndex;
		this.nodeCount = 0;
	}

	@Override
	public boolean task(TreeNode node) {
		if( this.nodeCount>=targetIndex) {
			if( result==null ) {
				result = node;
			}
			return false;
		}
		
		this.nodeCount++;
		return true;
	}
	
	public TreeNode getResult() {
		return result;
	}

	public static TreeNode process(int index, TreeNode node) {
		TaskGetNodeIndex task = new TaskGetNodeIndex(index);
		DepthFirstTraversal trav = new DepthFirstTraversal();
		trav.traverse(node, task);
		return task.getResult();
	}

}

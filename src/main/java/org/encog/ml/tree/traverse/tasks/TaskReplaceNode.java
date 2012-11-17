package org.encog.ml.tree.traverse.tasks;

import org.encog.ml.prg.ProgramNode;
import org.encog.ml.tree.TreeNode;
import org.encog.ml.tree.traverse.DepthFirstTraversal;
import org.encog.ml.tree.traverse.TreeTraversalTask;

public class TaskReplaceNode implements TreeTraversalTask {
	
	private final ProgramNode replaceThisNode; 
	private final ProgramNode replaceWith;
	private boolean done;
	
	public TaskReplaceNode(ProgramNode theReplaceThisNode, ProgramNode theReplaceWith) {
		this.replaceThisNode = theReplaceThisNode;
		this.replaceWith = theReplaceWith;
		this.done = false;
	}
	
	
	
	public ProgramNode getReplaceThisNode() {
		return replaceThisNode;
	}



	public ProgramNode getReplaceWith() {
		return replaceWith;
	}



	@Override
	public boolean task(TreeNode node) {
		if( done ) {
			return false;
		}
		
		for(int i=0;i<node.getChildNodes().size();i++) {
			TreeNode childNode = node.getChildNodes().get(i);
			if( childNode==replaceThisNode) {
				node.getChildNodes().set(i, this.replaceWith);
				done = true;
				return false;
			}
		}
		return true;
	}
	
	public static void process(ProgramNode rootNode, ProgramNode replaceThisNode, ProgramNode replaceWith) {
		TaskReplaceNode task = new TaskReplaceNode(replaceThisNode, replaceWith);
		DepthFirstTraversal trav = new DepthFirstTraversal();
		trav.traverse(rootNode, task);
	}

}

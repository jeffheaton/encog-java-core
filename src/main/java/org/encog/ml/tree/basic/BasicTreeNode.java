package org.encog.ml.tree.basic;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.tree.TreeNode;
import org.encog.ml.tree.traverse.tasks.TaskCountNodes;

public class BasicTreeNode implements TreeNode {
	private final List<TreeNode> childNodes = new ArrayList<TreeNode>();
	
	public List<TreeNode> getChildNodes() {
		return this.childNodes;
	}
	
	public void addChildNodes(TreeNode[] args) {
		for( TreeNode pn: args) {
			this.childNodes.add(pn);
		}
	}
	
	public boolean allLeafChildren() {
		boolean result = true;
		
		for(TreeNode node: this.childNodes) {
			if( !node.isLeaf() ) {
				result = false;
				break;
			}
		}
		
		return result;
	}

	public boolean isLeaf() {
		return this.childNodes.size()==0;
	}

	@Override
	public int size() {
		return TaskCountNodes.process(this);
	}
	
}

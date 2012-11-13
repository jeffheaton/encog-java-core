package org.encog.ml.tree.util;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.tree.TreeNode;

public class FindRandomNode<T extends TreeNode<T>> {
	
	private final T baseNode;
	
	public FindRandomNode(T theBaseNode) {
		this.baseNode = theBaseNode;
	}

	public TreeNode<T> getBaseNode() {
		return baseNode;
	}
	
	public TreeNode<T> find() {
		int size = this.baseNode.size();
		int idx = size * RangeRandomizer.randomInt(0, size);		
		return traverse(this.baseNode,idx);
	}
	
	private TreeNode<T> traverse(final T node, final int depth) {
		if( depth<=0 ) {
			return node;
		}
		
		int currentDepth = depth;
		
		for(T childNode: node.getChildNodes()) {
			currentDepth--;
			TreeNode<T> temp = traverse(childNode,currentDepth);
			
			if(temp!=null) {
				return temp;
			}
		}
		
		return null;
		
	}
	
}

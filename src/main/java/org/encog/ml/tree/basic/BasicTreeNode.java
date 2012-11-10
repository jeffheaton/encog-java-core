package org.encog.ml.tree.basic;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.prg.ProgramNode;
import org.encog.ml.tree.TreeNode;

public class BasicTreeNode<T extends TreeNode<T>> implements TreeNode<T> {
	private final List<T> childNodes = new ArrayList<T>();
	
	public List<T> getChildNodes() {
		return this.childNodes;
	}
	
	public void addChildNodes(T[] args) {
		for( T pn: args) {
			this.childNodes.add(pn);
		}
	}
	
	public boolean allLeafChildren() {
		boolean result = true;
		
		for(T node: this.childNodes) {
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
	
}

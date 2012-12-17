package org.encog.ml.prg.util;

import org.encog.ml.prg.EncogProgram;
import org.encog.util.datastruct.StackObject;

public class MapProgram {
	
	private final MappedNode rootNode;

	public MapProgram(EncogProgram theProgram) {
		StackObject<MappedNode> stack = new StackObject<MappedNode>(100);
		TraverseProgram trav = new TraverseProgram(theProgram);
		while(trav.next()) {
			MappedNode node = new MappedNode(trav);
			
			if( !trav.isLeaf() ) {
				for(int i=0;i<node.getTemplate().getChildNodeCount();i++) {
					node.getChildren().add(0,stack.pop());
				}
			}
			
			stack.push(node);
		}
		
		this.rootNode = stack.pop();
		
	}

	/**
	 * @return the rootNode
	 */
	public MappedNode getRootNode() {
		return rootNode;
	}
	
	
}

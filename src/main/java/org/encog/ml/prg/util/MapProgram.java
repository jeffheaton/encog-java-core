package org.encog.ml.prg.util;

import java.io.Serializable;

import org.encog.ml.prg.EncogProgram;
import org.encog.util.datastruct.StackObject;

public class MapProgram implements Serializable {

	private final MappedNode rootNode;

	public MapProgram(final EncogProgram theProgram) {
		final StackObject<MappedNode> stack = new StackObject<MappedNode>(100);
		final TraverseProgram trav = new TraverseProgram(theProgram);
		while (trav.next()) {
			final MappedNode node = new MappedNode(trav);

			if (!trav.isLeaf()) {
				for (int i = 0; i < node.getTemplate().getChildNodeCount(); i++) {
					node.getChildren().add(0, stack.pop());
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
		return this.rootNode;
	}

}

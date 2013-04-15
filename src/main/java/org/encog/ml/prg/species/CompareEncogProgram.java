package org.encog.ml.prg.species;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;

/**
 * Compare two Encog programs for speciation. Count the nodes that are the same.
 */
public class CompareEncogProgram {

	/**
	 * Compare program 1 and 2 node for node.
	 * @param prg1
	 * @param prg2
	 * @return
	 */
	public double compare(final EncogProgram prg1, final EncogProgram prg2) {
		return compareNode(0, prg1.getRootNode(), prg2.getRootNode());
	}

	/**
	 * Compare two nodes.
	 * @param result
	 * @param node1
	 * @param node2
	 * @return
	 */
	private double compareNode(final double result, final ProgramNode node1,
			final ProgramNode node2) {
		double newResult = result;

		if (node1.getTemplate() != node2.getTemplate()) {
			newResult++;
		}

		final int node1Size = node1.getChildNodes().size();
		final int node2Size = node2.getChildNodes().size();
		final int childNodeCount = Math.max(node1Size, node2Size);

		for (int i = 0; i < childNodeCount; i++) {
			if (i < node1Size && i < node2Size) {
				final ProgramNode childNode1 = node1.getChildNode(i);
				final ProgramNode childNode2 = node2.getChildNode(i);
				newResult = compareNode(newResult, childNode1, childNode2);
			} else {
				newResult++;
			}
		}

		return newResult;
	}
}

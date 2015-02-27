/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.prg.species;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;

/**
 * Compare two Encog programs for speciation. Count the nodes that are the
 * different, the higher the compare value, the more different two genomes are.
 * Only the opcodes are compared, the actual values are not. This causes the
 * comparison to be more about structure than actual values. Two genomes with
 * the same structure, and different values, can be identical.
 */
public class CompareEncogProgram {

	/**
	 * Compare program 1 and 2 node for node. Lower values mean more similar genomes.
	 * 
	 * @param prg1 The first program.
	 * @param prg2 The second program.
	 * @return The result of the compare.
	 */
	public double compare(final EncogProgram prg1, final EncogProgram prg2) {
		return compareNode(0, prg1.getRootNode(), prg2.getRootNode());
	}

	/**
	 * Compare two nodes.
	 * 
	 * @param result The result of previous comparisons.
	 * @param node1 The first node to compare.
	 * @param node2 The second node to compare.
	 * @return The result.
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

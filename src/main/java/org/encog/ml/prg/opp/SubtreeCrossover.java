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
package org.encog.ml.prg.opp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;

/**
 * Perform a type-safe subtree crossover. The crossover points will be chosen
 * randomly but must be type-safe. The first parent will be cloned to produce
 * the child. The tree formed from the crossover point of the second child will
 * be copied and grafted into the parent's clone and its crossover point.
 */
public class SubtreeCrossover implements EvolutionaryOperator {

	/**
	 * This method is called reflexivly as we iterate downward. Once we reach
	 * the desired point (when current level drops to zero), the operation is
	 * performed.
	 * 
	 * @param rnd
	 *            A random number generator.
	 * @param parentNode
	 *            The parent node.
	 * @param types
	 *            The desired node
	 * @param holder
	 *            The level holder.
	 */
	private void findNode(final Random rnd, final ProgramNode parentNode,
			final List<ValueType> types, final LevelHolder holder) {
		if (holder.getCurrentLevel() == 0) {
			holder.decreaseLevel();
			holder.setTypes(types);
			holder.setNodeFound(parentNode);
		} else {
			holder.decreaseLevel();
			for (int i = 0; i < parentNode.getTemplate().getChildNodeCount(); i++) {
				final ProgramNode childNode = parentNode.getChildNode(i);
				final List<ValueType> childTypes = parentNode.getTemplate()
						.getParams().get(i).determineArgumentTypes(types);
				findNode(rnd, childNode, childTypes, holder);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final EvolutionaryAlgorithm theOwner) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return Returns the number of offspring produced. In this case, one.
	 */
	@Override
	public int offspringProduced() {
		return 1;
	}

	/**
	 * @return Returns the number of parents needed. In this case, two.
	 */
	@Override
	public int parentsNeeded() {
		return 2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performOperation(final Random rnd, final Genome[] parents,
			final int parentIndex, final Genome[] offspring,
			final int offspringIndex) {
		final EncogProgram parent1 = (EncogProgram) parents[0];
		final EncogProgram parent2 = (EncogProgram) parents[1];
		offspring[0] = null;

		final EncogProgramContext context = parent1.getContext();
		final int size1 = parent1.getRootNode().size();
		final int size2 = parent2.getRootNode().size();

		boolean done = false;
		int tries = 100;

		while (!done) {
			final int p1Index = rnd.nextInt(size1);
			final int p2Index = rnd.nextInt(size2);

			final LevelHolder holder1 = new LevelHolder(p1Index);
			final LevelHolder holder2 = new LevelHolder(p2Index);

			final List<ValueType> types = new ArrayList<ValueType>();
			types.add(context.getResult().getVariableType());

			findNode(rnd, parent1.getRootNode(), types, holder1);
			findNode(rnd, parent2.getRootNode(), types, holder2);

			if (LevelHolder.compatibleTypes(holder1.getTypes(),
					holder2.getTypes())) {
				final EncogProgram result = context.cloneProgram(parent1);
				final ProgramNode resultNode = parent1.findNode(p1Index);
				final ProgramNode p2Node = parent2.findNode(p2Index);
				final ProgramNode newInsert = context.cloneBranch(result,
						p2Node);
				result.replaceNode(resultNode, newInsert);
				offspring[0] = result;
				done = true;
			} else {
				tries--;
				if (tries < 0) {
					done = true;
				}
			}

		}
	}

}

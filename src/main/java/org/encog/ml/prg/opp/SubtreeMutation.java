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
import org.encog.ml.prg.generator.PrgGenerator;
import org.encog.ml.prg.generator.PrgGrowGenerator;

/**
 * Perform a type-safe subtree mutation. The mutation point is chosen randomly,
 * but the new tree will be generated with compatible types to the parent.
 */
public class SubtreeMutation implements EvolutionaryOperator {

	/**
	 * A random generator.
	 */
	private PrgGenerator generator;
	
	/**
	 * The maximum depth.
	 */
	private final int maxDepth;

	/**
	 * Construct the subtree mutation object.
	 * @param theContext The program context.
	 * @param theMaxDepth The maximum depth.
	 */
	public SubtreeMutation(final EncogProgramContext theContext,
			final int theMaxDepth) {
		this.generator = new PrgGrowGenerator(theContext, theMaxDepth);
		this.maxDepth = theMaxDepth;
	}

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
	private void findNode(final Random rnd, final EncogProgram result,
			final ProgramNode parentNode, final List<ValueType> types,
			final int[] globalIndex) {
		if (globalIndex[0] == 0) {
			globalIndex[0]--;

			final ProgramNode newInsert = this.generator.createNode(rnd,
					result, this.maxDepth, types);
			result.replaceNode(parentNode, newInsert);
		} else {
			globalIndex[0]--;
			for (int i = 0; i < parentNode.getTemplate().getChildNodeCount(); i++) {
				final ProgramNode childNode = parentNode.getChildNode(i);
				final List<ValueType> childTypes = parentNode.getTemplate()
						.getParams().get(i).determineArgumentTypes(types);
				findNode(rnd, result, childNode, childTypes, globalIndex);
			}
		}
	}

	/**
	 * @return The random tree generator to use.
	 */
	public PrgGenerator getGenerator() {
		return this.generator;
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
	 * @return Returns the number of parents needed. In this case, one.
	 */
	@Override
	public int parentsNeeded() {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performOperation(final Random rnd, final Genome[] parents,
			final int parentIndex, final Genome[] offspring,
			final int offspringIndex) {
		final EncogProgram program = (EncogProgram) parents[0];
		final EncogProgramContext context = program.getContext();
		final EncogProgram result = context.cloneProgram(program);

		final List<ValueType> types = new ArrayList<ValueType>();
		types.add(context.getResult().getVariableType());
		final int[] globalIndex = new int[1];
		globalIndex[0] = rnd.nextInt(result.getRootNode().size());
		findNode(rnd, result, result.getRootNode(), types, globalIndex);

		offspring[0] = result;
	}

	/**
	 * Set the random tree generator to use.
	 * @param generator The random tree generator.
	 */
	public void setGenerator(final PrgGenerator generator) {
		this.generator = generator;
	}

}

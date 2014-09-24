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

import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.tree.TreeNode;

/**
 * Mutate the constant nodes of an Encog program. This mutation only changes
 * values and does not alter the structure.
 */
public class ConstMutation implements EvolutionaryOperator {

	/**
	 * The frequency that constant nodes are mutated with.
	 */
	private final double frequency;

	/**
	 * The sigma value used to generate gaussian random numbers.
	 */
	private final double sigma;

	/**
	 * Construct a const mutator.
	 * 
	 * @param theContext
	 *            The program context.
	 * @param theFrequency
	 *            The frequency of mutation.
	 * @param theSigma
	 *            The sigma to use for mutation.
	 */
	public ConstMutation(final EncogProgramContext theContext,
			final double theFrequency, final double theSigma) {
		this.frequency = theFrequency;
		this.sigma = theSigma;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final EvolutionaryAlgorithm theOwner) {
		// TODO Auto-generated method stub

	}

	/**
	 * Called for each node in the progrmam. If this is a const node, then
	 * mutate it according to the frequency and sigma specified.
	 * 
	 * @param rnd Random number generator.
	 * @param node The node to mutate.
	 */
	private void mutateNode(final Random rnd, final ProgramNode node) {
		if (node.getTemplate() == StandardExtensions.EXTENSION_CONST_SUPPORT) {
			if (rnd.nextDouble() < this.frequency) {
				final ExpressionValue v = node.getData()[0];
				if (v.isFloat()) {
					final double adj = rnd.nextGaussian() * this.sigma;
					node.getData()[0] = new ExpressionValue(v.toFloatValue()
							+ adj);
				}
			}
		}

		for (final TreeNode n : node.getChildNodes()) {
			final ProgramNode childNode = (ProgramNode) n;
			mutateNode(rnd, childNode);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int offspringProduced() {
		return 1;
	}

	/**
	 * {@inheritDoc}
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
		mutateNode(rnd, result.getRootNode());
		offspring[0] = result;
	}
}

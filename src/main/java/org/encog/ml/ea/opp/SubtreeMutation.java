/*
 * Encog(tm) Core v3.2 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.ml.ea.opp;

import java.io.Serializable;
import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.generator.PrgGrowGenerator;
import org.encog.ml.prg.generator.PrgPopulationGenerator;

/**
 * Subtree mutation is used in Genetic Programming to mutate part of a genetic
 * program expressed as a tree. This implementation is only designed to work
 * with EPL programs.
 * 
 * This mutation works by selecting a random point in the tree. This random
 * point is replaced with a randomly generated subtree.
 */
public class SubtreeMutation implements EvolutionaryOperator, Serializable {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A random population generator.
	 */
	private final PrgPopulationGenerator generator;

	public SubtreeMutation(final EncogProgramContext theContext,
			final int theMaxDepth) {
		this.generator = new PrgGrowGenerator(theContext, null, theMaxDepth);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final EvolutionaryAlgorithm theOwner) {
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
		final EncogProgram program = (EncogProgram) parents[parentIndex];
		offspring[offspringIndex] = parents[0].getPopulation().getGenomeFactory().factor(parents[0]);
		final EncogProgram result = (EncogProgram) offspring[offspringIndex];
		result.clear();

		// find the mutation point, this is simply a node position based on the
		// node count, it does not take int account node-sizes. Also, because
		// this
		// is RPN, the mutation point is the end of the mutation.
		final int programSize = program.size();
		final int mutationPosition = rnd.nextInt(programSize);

		// now find the actual frame index of the end of the mutation
		final int mutationIndex = program.findFrame(mutationPosition);

		final int mutationStart = program.findNodeStart(mutationIndex);
		final int mutationSize = (program.nextIndex(mutationIndex) - mutationStart);
		final int mutationEnd = mutationStart + mutationSize;

		// copy left of the mutation point
		result.copy(program, 0, 0, mutationStart);
		result.setProgramLength(mutationStart);
		result.setProgramCounter(mutationStart);

		// handle mutation point
		this.generator.createNode(rnd, result, 0, this.generator.getMaxDepth());

		// copy right of the mutation point
		final int rightSize = program.getProgramLength() - mutationStart
				- mutationSize;
		final int t = result.getProgramLength();
		result.setProgramLength(result.getProgramLength() + rightSize);
		result.copy(program, mutationEnd, t, rightSize);

		result.size();

	}
}

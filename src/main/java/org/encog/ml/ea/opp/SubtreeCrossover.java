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

/**
 * Subtree crossover is used in Genetic Programming to combine traits of two
 * genes and produce offspring. This implementation is only designed to work
 * with EPL programs.
 * 
 * Subtree crossover occurs in several steps. First, parent A is cloned into an
 * offspring genome. Second, a random point is chosen in parent B. Third, a
 * random point is chosen in the offspring and is replaced by a copy of the tree
 * from the random point in parent B.
 * 
 */
public class SubtreeCrossover implements EvolutionaryOperator, Serializable {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

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
		return 2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performOperation(final Random rnd, final Genome[] parents,
			final int parentIndex, final Genome[] theOffspring,
			final int offspringIndex) {

		final EncogProgram parent1 = (EncogProgram) parents[parentIndex];
		final EncogProgram parent2 = (EncogProgram) parents[parentIndex + 1];
		final EncogProgram offspring = (EncogProgram) theOffspring[0];

		// find the position for the two cut-points, this is simply a node
		// position based on the
		// node count, it does not take int account node-sizes.
		final int p1Position = rnd.nextInt(parent1.size());
		final int p2Position = rnd.nextInt(parent2.size());

		// now convert these two positions into actual indexes into the code.
		final int p1Index = parent1.findFrame(p1Position);
		final int p2Index = parent2.findFrame(p2Position);

		// write to offspring
		offspring.copy(parent1);
		offspring.replaceNode(parent2, p2Index, p1Index);
		offspring.evaluate();

	}

}

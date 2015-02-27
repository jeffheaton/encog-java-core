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
package org.encog.ml.ea.sort;

import java.util.Comparator;

import org.encog.ml.ea.species.Species;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;

/**
 * This comparator is used to compare two species. This is done by comparing the
 * scores of the two leaders.
 */
public class SpeciesComparator implements Comparator<Species> {

	/**
	 * The training method.
	 */
	private EvolutionaryAlgorithm training;

	/**
	 * Create a species comparator.
	 * 
	 * @param theTraining
	 *            The trainer.
	 */
	public SpeciesComparator(EvolutionaryAlgorithm theTraining) {
		this.training = theTraining;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Species sp1, Species sp2) {
		return training.getBestComparator().compare(sp1.getLeader(),
				sp2.getLeader());
	}

}

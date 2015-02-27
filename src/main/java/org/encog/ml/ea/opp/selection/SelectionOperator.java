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
package org.encog.ml.ea.opp.selection;

import java.util.Random;

import org.encog.ml.ea.species.Species;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;

/**
 * Provides the interface to a selection operator. This allows genomes to be
 * selected for offspring production or elimination.
 */
public interface SelectionOperator {
	
	/**
	 * Selects an fit genome.
	 * @param rnd A random number generator.
	 * @param species The species to select the genome from.
	 * @return The selected genome.
	 */
	int performSelection(Random rnd, Species species);

	/**
	 * Selects an unfit genome.
	 * @param rnd A random number generator.
	 * @param species The species to select the genome from.
	 * @return The selected genome.
	 */
	int performAntiSelection(Random rnd, Species species);

	/**
	 * @return The trainer being used.
	 */
	EvolutionaryAlgorithm getTrainer();
}

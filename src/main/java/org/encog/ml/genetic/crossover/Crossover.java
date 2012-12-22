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
package org.encog.ml.genetic.crossover;

import java.util.Random;

import org.encog.ml.genetic.genome.Genome;

/**
 * Specifies how "crossover" or mating happens.
 */
public interface Crossover {

	/**
	 * Perform a crossover between two genomes.
	 * @param rnd Random number generator.
	 * @param parent1 The first parent.
	 * @param parent2 The second parent.
	 * @param offspring The offspring.
	 * @param index The index to start writing offspring to.
	 */
	void performCrossover(Random rnd, Genome parent1, Genome parent2,
			Genome[] offspring, int index);
	
	/**
	 * @return The number of offspring produced by this type of crossover.
	 */
	int offspringProduced();
}

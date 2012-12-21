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

import org.encog.ml.genetic.genome.Chromosome;
import org.encog.ml.genetic.genome.Genome;

/**
 * Specifies how "crossover" or mating happens.
 */
public interface Crossover {

	/**
	 * Mate two chromosomes.
	 * 
	 * @param mother
	 *            The mother.
	 * @param father
	 *            The father.
	 * @param offspring1
	 *            The first offspring.
	 * @param offspring2
	 *            The second offspring.
	 */
	void mate(final Genome mother, final Genome father,
			final Genome offspring1, final Genome offspring2);
}

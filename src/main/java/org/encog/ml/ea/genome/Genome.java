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
package org.encog.ml.ea.genome;

import org.encog.ml.ea.population.Population;

/**
 * A genome is the basic blueprint for creating an organism in Encog. A genome
 * is made up of one or more chromosomes, which are in turn made up of genes.
 * 
 */
public interface Genome {

	/**
	 * Get the adjusted score, this considers old-age penalties and youth
	 * bonuses. If there are no such bonuses or penalties, this is the same as
	 * the score.
	 * 
	 * @return The adjusted score.
	 */
	double getAdjustedScore();

	/**
	 * @return The population that this genome belongs to.
	 */
	Population getPopulation();

	/**
	 * @return The score for this genome.
	 */
	double getScore();

	/**
	 * Set the adjusted score.
	 * 
	 * @param adjustedScore
	 *            The adjusted score.
	 */
	void setAdjustedScore(double adjustedScore);

	/**
	 * Set the population that this genome belongs to.
	 * 
	 * @param population
	 *            The population that this genome belongs to.
	 */
	void setPopulation(Population population);

	/**
	 * Set the score.
	 * 
	 * @param score
	 *            The new score.
	 */
	void setScore(double score);

	/**
	 * Copy from the specified genome into this one.
	 * 
	 * @param source The source genome.
	 */
	void copy(Genome source);

	/**
	 * @return Return the size of this genome. This size is a relative number
	 *         that indicates the complexity of the genome.
	 */
	int size();
}

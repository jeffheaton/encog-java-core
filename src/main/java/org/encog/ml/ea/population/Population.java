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
package org.encog.ml.ea.population;

import java.io.Serializable;
import java.util.List;

import org.encog.ml.MLMethod;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.ml.ea.species.Species;

/**
 * Defines a population of genomes.
 */
public interface Population extends Serializable, MLMethod {

	/**
	 * Clear all genomes from this population.
	 */
	void clear();

	/**
	 * Create a species.
	 * 
	 * @return The newly created species.
	 */
	Species createSpecies();

	/**
	 * Determine which species has the top genome.
	 * 
	 * @return The species with the top genome.
	 */
	Species determineBestSpecies();

	/**
	 * Flatten the species into a single list of genomes.
	 * 
	 * @return The genomes that make up all species in the population.
	 */
	List<Genome> flatten();

	/**
	 * @return The best genome in the population.
	 */
	Genome getBestGenome();

	/**
	 * @return A factory used to create genomes.
	 */
	GenomeFactory getGenomeFactory();

	/**
	 * @return The max size that an individual can become.
	 */
	int getMaxIndividualSize();

	/**
	 * @return The max population size.
	 */
	int getPopulationSize();

	/**
	 * @return The species that make up the population.
	 */
	List<Species> getSpecies();

	/**
	 * Set the best genome.
	 * 
	 * @param bestGenome
	 *            The best genome.
	 */
	void setBestGenome(Genome bestGenome);

	/**
	 * Set the gnome factory.
	 * 
	 * @param factory
	 *            The genome factory.
	 */
	void setGenomeFactory(GenomeFactory factory);

	/**
	 * Set the max population size.
	 * 
	 * @param populationSize
	 *            The max population size.
	 */
	void setPopulationSize(final int populationSize);

	/**
	 * @return The size of the population.
	 */
	int size();
	
	/**
	 * Purge any invalid genomes.
	 */
	void purgeInvalidGenomes();
}

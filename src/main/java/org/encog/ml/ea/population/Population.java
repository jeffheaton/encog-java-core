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
package org.encog.ml.ea.population;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.ml.ea.species.BasicSpecies;
import org.encog.ml.ea.species.Species;
import org.encog.ml.prg.train.rewrite.RewriteRule;

/**
 * Defines a population of genomes.
 */
public interface Population extends Serializable {

	/**
	 * Clear all genomes from this population.
	 */
	void clear();

	/**
	 * @return The max population size.
	 */
	int getPopulationSize();


	/**
	 * Set the max population size.
	 * @param populationSize The max population size.
	 */
	void setPopulationSize(final int populationSize);

	/**
	 * @return The size of the population.
	 */
	int size();

	void rewrite(Genome prg);
	
	GenomeFactory getGenomeFactory();

	void setGenomeFactory(GenomeFactory factory);

	int getMaxIndividualSize();

	void addRewriteRule(RewriteRule rule);

	List<Species> getSpecies();

	List<Genome> flatten();

	Species createSpecies();
	
	Genome getBestGenome();

	void setBestGenome(Genome bestGenome);
	
	Species determineBestSpecies();
}

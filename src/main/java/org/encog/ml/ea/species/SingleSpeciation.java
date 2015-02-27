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
package org.encog.ml.ea.species;

import java.util.Collections;
import java.util.List;

import org.encog.EncogError;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.sort.SortGenomesForSpecies;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;

/**
 * This speciation strategy simply creates a single species that contains the
 * entire population. Use this speciation strategy if you do not wish to use
 * species.
 * 
 */
public class SingleSpeciation implements Speciation {

	/**
	 * The trainer.
	 */
	private EvolutionaryAlgorithm owner;

	/**
	 * The method used to sort the genomes in the species. More desirable
	 * genomes should come first for later selection.
	 */
	private SortGenomesForSpecies sortGenomes;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final EvolutionaryAlgorithm theOwner) {
		this.owner = theOwner;
		this.sortGenomes = new SortGenomesForSpecies(this.owner);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performSpeciation(final List<Genome> genomeList) {
		updateShare();
		final Species species = this.owner.getPopulation().getSpecies().get(0);
		species.getMembers().clear();
		species.getMembers().addAll(genomeList);
		Collections.sort(species.getMembers(), this.sortGenomes);
		species.setLeader(species.getMembers().get(0));

	}

	/**
	 * Update the species share of the next population.
	 */
	private void updateShare() {
		final int speciesCount = this.owner.getPopulation().getSpecies().size();
		if (speciesCount != 1) {
			throw new EncogError(
					"SingleSpeciation can only be used with a species count of 1, species count is "
							+ speciesCount);
		}

		final Species species = this.owner.getPopulation().getSpecies().get(0);
		species.setOffspringCount(this.owner.getPopulation()
				.getPopulationSize());
	}

}

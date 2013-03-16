package org.encog.ml.ea.species;

import java.util.Collections;
import java.util.List;

import org.encog.EncogError;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.sort.SortGenomesForSpecies;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;

public class SingleSpeciation implements Speciation {

	private EvolutionaryAlgorithm owner;
	
	/**
	 * The method used to sort the genomes in the species. More desirable
	 * genomes should come first for later selection.
	 */
	private SortGenomesForSpecies sortGenomes;

	@Override
	public void init(final EvolutionaryAlgorithm theOwner) {
		this.owner = theOwner;
		this.sortGenomes = new SortGenomesForSpecies(this.owner);
	}

	@Override
	public boolean isIterationBased() {
		return false;
	}

	@Override
	public void performSpeciation(final List<Genome> genomeList) {
		updateShare();
		final Species species = this.owner.getPopulation().getSpecies().get(0);
		species.getMembers().clear();
		species.getMembers().addAll(genomeList);
		Collections.sort(species.getMembers(), this.sortGenomes);
		species.setLeader(species.getMembers().get(0));
		
	}

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

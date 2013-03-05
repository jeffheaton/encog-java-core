package org.encog.ml.ea.species;

import java.util.List;

import org.encog.EncogError;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;

public class SingleSpeciation implements Speciation {

	private EvolutionaryAlgorithm owner;

	@Override
	public void init(final EvolutionaryAlgorithm theOwner) {
		this.owner = theOwner;
	}

	@Override
	public boolean isIterationBased() {
		return false;
	}

	@Override
	public void performSpeciation(final List<Genome> genomeList) {
		updateShare();
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

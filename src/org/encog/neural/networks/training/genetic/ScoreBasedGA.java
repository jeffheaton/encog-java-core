package org.encog.neural.networks.training.genetic;

import org.encog.neural.networks.BasicNetwork;
import org.encog.util.randomize.Randomizer;

public abstract class ScoreBasedGA extends NeuralGeneticAlgorithm {
	
	public ScoreBasedGA(final BasicNetwork network,
			final Randomizer randomizer,
			final int populationSize, final double mutationPercent,
			final double percentToMate) {

		super();
		getGenetic().setMutationPercent(mutationPercent);
		getGenetic().setMatingPopulation(percentToMate * 2);
		getGenetic().setPopulationSize(populationSize);
		getGenetic().setPercentToMate(percentToMate);
		
		getGenetic().setShouldMinimize(false);

		getGenetic().setChromosomes(
				new NeuralChromosome[getGenetic()
						.getPopulationSize()]);
		for (int i = 0; i < getGenetic().getChromosomes().length; i++) {
			final BasicNetwork chromosomeNetwork = (BasicNetwork) network
					.clone();
			randomizer.randomize(chromosomeNetwork);

			final ScoreBasedChromosome c = 
				new ScoreBasedChromosome(
					this, chromosomeNetwork);
			getGenetic().setChromosome(i, c);
		}
		getGenetic().sortChromosomes();
		getGenetic().defineCutLength();
	}
	
	public abstract double calculateScore(BasicNetwork network);
	
}

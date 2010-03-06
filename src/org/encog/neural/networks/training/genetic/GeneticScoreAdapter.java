package org.encog.neural.networks.training.genetic;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.solve.genetic.genome.CalculateGenomeScore;
import org.encog.solve.genetic.genome.Genome;

public class GeneticScoreAdapter implements CalculateGenomeScore {

	private final CalculateScore calculateScore;
	
	public GeneticScoreAdapter(CalculateScore calculateScore)
	{
		this.calculateScore = calculateScore;
	}
	
	@Override
	public double calculateScore(Genome genome) {
		BasicNetwork network = (BasicNetwork)genome.getOrganism();
		return this.calculateScore.calculateScore(network);
	}

	@Override
	public boolean shouldMinimize() {
		return this.calculateScore.shouldMinimize();
	}

}

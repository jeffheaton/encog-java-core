package org.encog.neural.networks.training.genetic;

import org.encog.neural.networks.BasicNetwork;

public class ScoreBasedChromosome  extends NeuralChromosome {
	
	private ScoreBasedGA genetic;
	
	public ScoreBasedChromosome(
			final ScoreBasedGA genetic,
			final BasicNetwork network) {
		setGeneticAlgorithm(genetic.getGenetic());
		this.genetic = genetic;
		setNetwork(network);

		initGenes(network.getWeightMatrixSize());
		updateGenes();
	}
	
	@Override
	public void calculateScore() {
		// update the network with the new gene values
		updateNetwork();
		setScore( genetic.calculateScore(this.getNetwork()) );		
	}


}

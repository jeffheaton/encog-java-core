package org.encog.ml.ea.score.parallel;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.score.CalculateGenomeScore;

public class ParallelScore {
	private final Population population;
	private final CalculateGenomeScore scoreFunction;
	
	public ParallelScore(Population thePopulation, CalculateGenomeScore theScoreFunction) {
		this.population = thePopulation;
		this.scoreFunction = theScoreFunction;
	}

	/**
	 * @return the population
	 */
	public Population getPopulation() {
		return population;
	}

	/**
	 * @return the scoreFunction
	 */
	public CalculateGenomeScore getScoreFunction() {
		return scoreFunction;
	}
	
	public void process() {
		for(Genome genome: this.population.getGenomes()) {
			double score = this.scoreFunction.calculateScore(genome);
			genome.setScore(score);
		}
	}
}

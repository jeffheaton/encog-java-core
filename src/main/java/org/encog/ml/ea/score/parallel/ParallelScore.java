package org.encog.ml.ea.score.parallel;

import java.util.List;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.score.AdjustScore;
import org.encog.ml.ea.score.CalculateGenomeScore;
import org.encog.ml.ea.train.basic.BasicEA;

public class ParallelScore {
	private final Population population;
	private final CalculateGenomeScore scoreFunction;
	private final List<AdjustScore> adjusters; 
	
	public ParallelScore(Population thePopulation, List<AdjustScore> theAdjusters, CalculateGenomeScore theScoreFunction) {
		this.population = thePopulation;
		this.scoreFunction = theScoreFunction;
		this.adjusters = theAdjusters;
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
			BasicEA.calculateScoreAdjustment(genome, adjusters);
		}
	}
}

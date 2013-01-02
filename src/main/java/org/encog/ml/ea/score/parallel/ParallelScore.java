package org.encog.ml.ea.score.parallel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.score.AdjustScore;
import org.encog.ml.ea.score.CalculateGenomeScore;
import org.encog.ml.genetic.GeneticError;

public class ParallelScore {
	private final Population population;
	private final CalculateGenomeScore scoreFunction;
	private final List<AdjustScore> adjusters;
	private final int threads;

	public ParallelScore(Population thePopulation,
			List<AdjustScore> theAdjusters,
			CalculateGenomeScore theScoreFunction,
			int theThreadCount) {
		this.population = thePopulation;
		this.scoreFunction = theScoreFunction;
		this.adjusters = theAdjusters;
		if( theScoreFunction.requireSingleThreaded() ) {
			this.threads = 1;
		} else if( theThreadCount==0 ) {
			this.threads = Runtime.getRuntime().availableProcessors();
		} else {
			this.threads = theThreadCount;
		}
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

		ExecutorService taskExecutor = null;
		
		if( this.threads==1 ) {
			taskExecutor = Executors.newSingleThreadScheduledExecutor();
		} else {
			taskExecutor = Executors.newFixedThreadPool(this.threads);
		}
		
		Executors.newFixedThreadPool(this.threads);

		for (Genome genome : this.population.getGenomes()) {
			taskExecutor.execute(new ParallelScoreTask(genome, scoreFunction,
					adjusters));
		}

		taskExecutor.shutdown();
		try {
			taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new GeneticError(e);
		}
	}
}

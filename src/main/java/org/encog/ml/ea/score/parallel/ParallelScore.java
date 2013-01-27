package org.encog.ml.ea.score.parallel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.encog.ml.CalculateScore;
import org.encog.ml.ea.codec.GeneticCODEC;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.score.AdjustScore;
import org.encog.ml.genetic.GeneticError;

public class ParallelScore {
	private final Population population;
	private final GeneticCODEC codec;
	private final CalculateScore scoreFunction;
	private final List<AdjustScore> adjusters;
	private final int threads;

	public ParallelScore(Population thePopulation,
			GeneticCODEC theCODEC,
			List<AdjustScore> theAdjusters,
			CalculateScore theScoreFunction,
			int theThreadCount) {
		this.codec = theCODEC;
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
	public CalculateScore getScoreFunction() {
		return scoreFunction;
	}

	/**
	 * @return the codec
	 */
	public GeneticCODEC getCodec() {
		return codec;
	}

	public void process() {

		ExecutorService taskExecutor = null;
		
		if( this.threads==1 ) {
			taskExecutor = Executors.newSingleThreadScheduledExecutor();
		} else {
			taskExecutor = Executors.newFixedThreadPool(this.threads);
		}

		for (Genome genome : this.population.getGenomes()) {
			taskExecutor.execute(new ParallelScoreTask(genome, this));
		}

		taskExecutor.shutdown();
		try {
			taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new GeneticError(e);
		}
	}

	public List<AdjustScore> getAdjusters() {
		return this.adjusters;
	}
}

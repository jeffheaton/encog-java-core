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
import org.encog.ml.ea.species.Species;
import org.encog.ml.genetic.GeneticError;
import org.encog.util.concurrency.MultiThreadable;

public class ParallelScore implements MultiThreadable {
	private final Population population;
	private final GeneticCODEC codec;
	private final CalculateScore scoreFunction;
	private final List<AdjustScore> adjusters;
	private int threads;
	private int actualThreads;

	public ParallelScore(Population thePopulation, GeneticCODEC theCODEC,
			List<AdjustScore> theAdjusters, CalculateScore theScoreFunction,
			int theThreadCount) {
		this.codec = theCODEC;
		this.population = thePopulation;
		this.scoreFunction = theScoreFunction;
		this.adjusters = theAdjusters;
		this.actualThreads = 0;
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
		// determine thread usage
		if (this.scoreFunction.requireSingleThreaded()) {
			this.actualThreads = 1;
		} else if (threads == 0) {
			this.actualThreads = Runtime.getRuntime().availableProcessors();
		} else {
			this.actualThreads = threads;
		}

		// start up
		ExecutorService taskExecutor = null;

		if (this.threads == 1) {
			taskExecutor = Executors.newSingleThreadScheduledExecutor();
		} else {
			taskExecutor = Executors.newFixedThreadPool(this.actualThreads);
		}

		for (Species species : this.population.getSpecies()) {
			for (Genome genome : species.getMembers()) {
				taskExecutor.execute(new ParallelScoreTask(genome, this));
			}
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

	@Override
	public int getThreadCount() {
		return this.threads;
	}

	@Override
	public void setThreadCount(int numThreads) {
		this.threads = numThreads;
	}
}

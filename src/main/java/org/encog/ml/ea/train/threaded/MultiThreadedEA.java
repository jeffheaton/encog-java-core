package org.encog.ml.ea.train.threaded;

import java.io.Serializable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.EncogShutdownTask;
import org.encog.mathutil.randomize.factory.RandomFactory;
import org.encog.ml.CalculateScore;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.opp.OperationList;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.score.parallel.ParallelScore;
import org.encog.ml.ea.train.basic.BasicEA;
import org.encog.ml.genetic.GeneticError;
import org.encog.ml.prg.exception.EncogProgramError;
import org.encog.ml.prg.train.ThreadedGenomeSelector;
import org.encog.util.concurrency.MultiThreadable;

public class MultiThreadedEA extends BasicEA
		implements MultiThreadable, EncogShutdownTask, Serializable {

	private GeneticTrainWorker[] workers;

	private final OperationList operators = new OperationList();

	private boolean needBestGenome = true;

	private int iterationNumber;
	private int subIterationCounter;
	private final Lock iterationLock = new ReentrantLock();
	private transient Throwable currentError;
	private ThreadedGenomeSelector selector;
	private final Genome bestGenome;
	private RandomFactory randomNumberFactory = Encog.getInstance()
			.getRandomFactory().factorFactory();

	/**
	 * Condition used to check if we are done.
	 */
	private final Condition iterationCondition = this.iterationLock
			.newCondition();

	/**
	 * The thread count;
	 */
	private int threadCount;

	public MultiThreadedEA(Population thePopulation,
			CalculateScore theScoreFunction) {
		super(thePopulation, theScoreFunction);

		this.bestGenome = thePopulation.getGenomeFactory().factor();
		this.selector = new ThreadedGenomeSelector(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getThreadCount() {
		return this.threadCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setThreadCount(int numThreads) {
		this.threadCount = numThreads;

	}

	private void startup() {
		
		if( this.getOperators().size()<1 ) {
			throw new EncogProgramError("Can't train, there are no evolutionary operators.");
		}
		
		// rescore everything
		ParallelScore s = new ParallelScore(getPopulation(),this.getCODEC(),this.getScoreAdjusters(),this.getScoreFunction(),this.getThreadCount());
		s.process();
		
		// spin up the threads
		int actualThreadCount = Runtime.getRuntime().availableProcessors();
		Encog.getInstance().addShutdownTask(this);
		
		if (this.threadCount != 0) {
			actualThreadCount = this.threadCount;
		}

		this.workers = new GeneticTrainWorker[actualThreadCount];

		for (int i = 0; i < this.workers.length; i++) {
			this.workers[i] = new GeneticTrainWorker(this);
			this.workers[i].start();
		}

		this.needBestGenome = true;

	}

	@Override
	public void iteration() {
		if (this.workers == null) {
			this.operators.finalizeStructure();
			startup();
		}

		this.iterationLock.lock();
		try {
			this.iterationCondition.await();
			if (this.currentError != null) {
				throw new EncogError(this.currentError);
			}
		} catch (InterruptedException e) {

		} finally {
			this.iterationLock.unlock();
		}

		if (this.currentError != null) {
			finishTraining();
		}
	}

	public void addOperation(double probability, EvolutionaryOperator opp) {
		this.operators.add(probability, opp);
	}

	public void finishTraining() {
		if (this.workers != null) {
			for (int i = 0; i < this.workers.length; i++) {
				this.workers[i].requestTerminate();
			}

			for (int i = 0; i < this.workers.length; i++) {
				try {
					this.workers[i].join();
				} catch (InterruptedException e) {
					throw new EncogError("Can't shut down training threads.");
				}
			}
		}

		this.workers = null;
		Encog.getInstance().removeShutdownTask(this);

	}

	public void evaluateBestGenome(Genome prg) {
		this.iterationLock.lock();
		try {
			BasicEA.calculateScoreAdjustment(prg, getScoreAdjusters());
			if (this.needBestGenome || this.getSelectionComparator().isBetterThan(prg, this.bestGenome) ) {
				this.bestGenome.copy(prg);
				this.needBestGenome = false;
			}
		} finally {
			this.iterationLock.unlock();
		}

	}

	public void copyBestGenome(Genome target) {
		this.iterationLock.lock();
		try {
			target.copy(this.bestGenome);
		} finally {
			this.iterationLock.unlock();
		}
	}

	public void addGenome(Genome[] genome, int index, int size) {
		Genome replaceTarget = null;
		this.iterationLock.lock();
		try {
			for (int i = 0; i < size; i++) {
				if (genome[i].size() > getMaxIndividualSize()) {
					throw new GeneticError(
							"Program is too large to be added to population.");
				}
				replaceTarget = this.selector.antiSelectGenome();
				getPopulation().rewrite(genome[index + i]);
				replaceTarget.copy(genome[index + i]);
				evaluateBestGenome(genome[index + i]);
			}
		} finally {
			this.iterationLock.unlock();
			if (replaceTarget != null) {
				this.selector.releaseGenome(replaceTarget);
			}
		}
	}

	public void notifyProgress() {
		this.iterationLock.lock();
		try {
			this.subIterationCounter++;
			if (this.subIterationCounter > getPopulation().size()) {
				this.subIterationCounter = 0;
				this.iterationNumber++;
				this.iterationCondition.signal();
			}
		} finally {
			this.iterationLock.unlock();
		}
	}

	public void reportError(Throwable t) {
		this.iterationLock.lock();
		try {
			this.currentError = t;
			this.iterationCondition.signal();
		} finally {
			this.iterationLock.unlock();
		}
	}

	public void signalDone() {
		this.iterationLock.lock();
		try {
			this.iterationCondition.signal();
		} finally {
			this.iterationLock.unlock();
		}
	}

	/**
	 * @return the selector
	 */
	public ThreadedGenomeSelector getSelector() {
		return selector;
	}

	/**
	 * @return the operators
	 */
	public OperationList getOperators() {
		return operators;
	}

	public double getError() {
		return this.bestGenome.getScore();
	}

	public int getIteration() {
		return this.iterationNumber;
	}

	/**
	 * @return the randomNumberFactory
	 */
	public RandomFactory getRandomNumberFactory() {
		return randomNumberFactory;
	}

	/**
	 * @param randomNumberFactory
	 *            the randomNumberFactory to set
	 */
	public void setRandomNumberFactory(RandomFactory randomNumberFactory) {
		this.randomNumberFactory = randomNumberFactory;
	}

	@Override
	public void performShutdownTask() {
		finishTraining();
	}
}

package org.encog.ml.genetic;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.mathutil.randomize.factory.RandomFactory;
import org.encog.ml.genetic.evolutionary.EvolutionaryOperator;
import org.encog.ml.genetic.evolutionary.OperationList;
import org.encog.ml.genetic.genome.CalculateGenomeScore;
import org.encog.ml.genetic.genome.Genome;
import org.encog.ml.genetic.population.Population;
import org.encog.ml.genetic.sort.GenomeComparator;
import org.encog.ml.genetic.sort.MaximizeAdjustedScoreScoreComp;
import org.encog.ml.genetic.sort.MinimizeAdjustedScoreScoreComp;
import org.encog.ml.prg.train.GeneticTrainingParams;
import org.encog.ml.prg.train.ThreadedGenomeSelector;
import org.encog.ml.prg.train.selection.PrgSelection;
import org.encog.ml.prg.train.selection.TournamentSelection;
import org.encog.util.concurrency.MultiThreadable;

public class MultiThreadedGeneticAlgorithm extends BasicGeneticAlgorithm implements MultiThreadable {

	/**
	 * Should this run multi-threaded.
	 */
	private boolean multiThreaded = true;
	
	private GeneticTrainWorker[] workers;
	
	private final OperationList operators = new OperationList();
	
	private boolean needBestGenome = true;
	
	private int iterationNumber;
	private int subIterationCounter;
	private final Lock iterationLock = new ReentrantLock();
	private Throwable currentError;
	private ThreadedGenomeSelector selector;
	private final Population population;
	private final CalculateGenomeScore scoreFunction;
	private PrgSelection selection;
	private final Genome bestGenome;
	private GenomeComparator compareScore;
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
	
	
	public MultiThreadedGeneticAlgorithm(Population thePopulation,
			CalculateGenomeScore theScoreFunction) {
		this.population = thePopulation;
		this.scoreFunction = theScoreFunction;
		this.selection = new TournamentSelection(this, 4);
		
		this.bestGenome = thePopulation.getGenomeFactory().factor();
		if (theScoreFunction.shouldMinimize()) {
			this.compareScore = new MinimizeAdjustedScoreScoreComp();
		} else {
			this.compareScore = new MaximizeAdjustedScoreScoreComp();
		}
		
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
		int actualThreadCount = Runtime.getRuntime().availableProcessors();

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
		
		if( this.currentError!=null ) {
			finishTraining();
		}
	}
	
	public void addOperation(double probability, EvolutionaryOperator opp) {
		this.operators.add(probability, opp);
	}
	
	public void finishTraining() {
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

		this.workers = null;

	}
	
	public void evaluateBestGenome(Genome prg) {
		this.iterationLock.lock();
		try {
			calculateEffectiveScore(prg);
			if (this.needBestGenome || isGenomeBetter(prg, this.bestGenome)) {
				this.bestGenome.copy(prg);
				this.needBestGenome = false;
			}
		} finally {
			this.iterationLock.unlock();
		}
		
	}

	public boolean isGenomeBetter(Genome genome, Genome betterThan) {
		return this.compareScore.compare(genome, betterThan) < 0;
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
			for(int i=0;i<size;i++) {
				if( genome[i].size()>getMaxIndividualSize() ) {
					throw new GeneticError("Program is too large to be added to population.");
				}
				replaceTarget = this.selector.antiSelectGenome();
				this.population.rewrite(genome[index+i]);
				replaceTarget.copy(genome[index+i]);
				evaluateBestGenome(genome[index+i]);
			}
		} finally {
			this.iterationLock.unlock();
			if( replaceTarget!=null ) {
				this.selector.releaseGenome(replaceTarget);
			}
		}
	}
	
	public void notifyProgress() {
		this.iterationLock.lock();
		try {			
			this.subIterationCounter++;
			if (this.subIterationCounter > this.population.size()) {
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
	
	public Population getPopulation() {
		return population;
	}

	public CalculateGenomeScore getScoreFunction() {
		return scoreFunction;
	}

	public PrgSelection getSelection() {
		return selection;
	}

	public void setSelection(PrgSelection selection) {
		this.selection = selection;
	}

	public double getError() {
		return this.bestGenome.getScore();
	}

	public int getIteration() {
		return this.iterationNumber;
	}

	public GenomeComparator getCompareScore() {
		return compareScore;
	}

	public void setCompareScore(GenomeComparator compareScore) {
		this.compareScore = compareScore;
	}

	public void createRandomPopulation(int maxDepth) {
		Random random = this.randomNumberFactory.factor();
		this.population.getGenomeFactory().factorRandomPopulation(random, population, scoreFunction, maxDepth);
		
		for(Genome genome: this.population.getGenomes()) {
			evaluateBestGenome(genome);
		}
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
	
	public void calculateEffectiveScore(Genome genome) {
		GeneticTrainingParams params = getParams();
		double result = genome.getScore();
		if (genome.size() > params.getComplexityPenaltyThreshold()) {
			int over = genome.size() - params.getComplexityPenaltyThreshold();
			int range = params.getComplexityPentaltyFullThreshold()
					- params.getComplexityPenaltyThreshold();
			double complexityPenalty = ((params.getComplexityFullPenalty() - params
					.getComplexityPenalty()) / range) * over;
			result += (result * complexityPenalty);
		}
		genome.setAdjustedScore(result);
	}


	@Override
	public int getMaxIndividualSize() {
		return this.population.getMaxIndividualSize();
	}	
}

package org.encog.ml.prg.train;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.mathutil.randomize.factory.RandomFactory;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.train.crossover.PrgCrossover;
import org.encog.ml.prg.train.crossover.SubtreeCrossover;
import org.encog.ml.prg.train.mutate.PrgMutate;
import org.encog.ml.prg.train.mutate.SubtreeMutation;
import org.encog.ml.prg.train.selection.PrgSelection;
import org.encog.ml.prg.train.selection.TournamentSelection;
import org.encog.ml.prg.train.sort.MaximizeScoreComp;
import org.encog.ml.prg.train.sort.MinimizeScoreComp;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.concurrency.MultiThreadable;

public class PrgGenetic implements MLTrain, MultiThreadable {
	private final EncogProgramContext context;
	private final PrgPopulation population;
	private final CalculateScore scoreFunction;
	private PrgSelection selection;
	private PrgMutate mutation;
	private PrgCrossover crossover;
	private EncogProgram bestGenome = null;
	private Comparator<EncogProgram> compareScore;
	private int threadCount;
	private GeneticTrainWorker[] workers;
	private int iterationNumber;
	private int subIterationCounter;
	private final Lock iterationLock = new ReentrantLock();
	private RandomFactory randomNumberFactory = Encog.getInstance().getRandomFactory().factorFactory();
	
	/**
	 * Condition used to check if we are done.
	 */
	private final Condition iterationCondition = 
		this.iterationLock.newCondition();

	public PrgGenetic(PrgPopulation thePopulation, CalculateScore theScoreFunction) {
		this.population = thePopulation;
		this.context = population.getContext();
		this.scoreFunction = theScoreFunction;
		this.selection = new TournamentSelection(this,4);
		this.crossover = new SubtreeCrossover();
		this.mutation = new SubtreeMutation(thePopulation.getContext(),4);
		if( theScoreFunction.shouldMinimize()) {
			this.compareScore = new MinimizeScoreComp();
		} else {
			this.compareScore = new MaximizeScoreComp();
		}
	}

	public PrgGenetic(PrgPopulation thePopulation, MLDataSet theTrainingSet) {
		this(thePopulation, new TrainingSetScore(theTrainingSet));
	}

	public PrgPopulation getPopulation() {
		return population;
	}

	public CalculateScore getScoreFunction() {
		return scoreFunction;
	}

	public PrgSelection getSelection() {
		return selection;
	}

	public void setSelection(PrgSelection selection) {
		this.selection = selection;
	}

	public PrgMutate getMutation() {
		return mutation;
	}

	public void setMutation(PrgMutate mutation) {
		this.mutation = mutation;
	}

	public PrgCrossover getCrossover() {
		return crossover;
	}

	public void setCrossover(PrgCrossover crossover) {
		this.crossover = crossover;
	}

	@Override
	public TrainingImplementationType getImplementationType() {
		// TODO Auto-generated method stub
		return TrainingImplementationType.Background;
	}

	@Override
	public boolean isTrainingDone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MLDataSet getTraining() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void startup() {
		int actualThreadCount = Runtime.getRuntime().availableProcessors();
		
		if( this.threadCount!=0 ) {
			actualThreadCount = this.threadCount;
		}
		
		this.workers = new GeneticTrainWorker[actualThreadCount];
		
		for(int i=0;i<this.workers.length;i++) {
			this.workers[i] = new GeneticTrainWorker(this);
			this.workers[i].start();
		}
		
	}

	@Override
	public void iteration() {
		if( this.workers==null ) {
			startup();
		}
		
		this.iterationLock.lock();
		try {
			this.iterationCondition.await();
		} catch (InterruptedException e) {

		}
		finally {
			this.iterationLock.unlock();
		}
	}

	@Override
	public double getError() {
		return this.bestGenome.getScore();
	}

	@Override
	public void finishTraining() {
		for(int i=0;i<this.workers.length;i++) {
			this.workers[i].requestTerminate();
		}
		
		for(int i=0;i<this.workers.length;i++) {
			try {
				this.workers[i].join();
			} catch (InterruptedException e) {
				throw new EncogError("Can't shut down training threads.");
			}
		}
		
		this.workers = null;

	}

	@Override
	public void iteration(int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getIteration() {
		return this.iterationNumber;
	}

	@Override
	public boolean canContinue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TrainingContinuation pause() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resume(TrainingContinuation state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addStrategy(Strategy strategy) {
		// TODO Auto-generated method stub

	}

	@Override
	public MLMethod getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Strategy> getStrategies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setError(double error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setIteration(int iteration) {
		// TODO Auto-generated method stub

	}

	public Comparator<EncogProgram> getCompareScore() {
		return compareScore;
	}

	public void setCompareScore(Comparator<EncogProgram> compareScore) {
		this.compareScore = compareScore;
	}

	public void createRandomPopulation(int maxDepth) {
		CreateRandom rnd = new CreateRandom(this.context, maxDepth);

		for (int i = 0; i < this.population.getMaxPopulation(); i++) {

			boolean done = false;
			EncogProgram prg = null;
			do {
				prg = rnd.generate();
				double score = this.scoreFunction.calculateScore(prg);
				if (!Double.isInfinite(score) && !Double.isNaN(score)) {
					prg.setScore(score);
					done = true;
				}
			} while (!done);

			evaluateBestGenome(prg);
			this.population.rewrite(prg);
			this.population.getMembers()[i] = prg;
		}
	}

	private void evaluateBestGenome(EncogProgram prg) {
		if (this.bestGenome == null || isGenomeBetter(prg, this.bestGenome)) {
			this.bestGenome = prg;
		}
	}

	public boolean isGenomeBetter(EncogProgram genome, EncogProgram betterThan) {
		return this.compareScore.compare(genome, betterThan) < 0;
	}

	public EncogProgram getBestGenome() {
		return this.bestGenome;
	}

	public void sort() {
		Arrays.sort(this.getPopulation().getMembers(), this.compareScore);

	}

	public void addGenome(EncogProgram newPrg) {
		this.iterationLock.lock();
		try {
			int replaceIndex = selection.performAntiSelection();
			this.population.getMembers()[replaceIndex] = newPrg;
			evaluateBestGenome(newPrg);
			
			this.subIterationCounter++;
			if( this.subIterationCounter>this.population.size() ) {
				this.subIterationCounter=0;
				this.iterationNumber++;
				this.iterationCondition.signal();
			}
		} finally {
			this.iterationLock.unlock();
		}
	}

	@Override
	public int getThreadCount() {
		return this.threadCount;
	}

	@Override
	public void setThreadCount(int numThreads) {
		this.threadCount = numThreads;
	}

	/**
	 * @return the randomNumberFactory
	 */
	public RandomFactory getRandomNumberFactory() {
		return randomNumberFactory;
	}

	/**
	 * @param randomNumberFactory the randomNumberFactory to set
	 */
	public void setRandomNumberFactory(RandomFactory randomNumberFactory) {
		this.randomNumberFactory = randomNumberFactory;
	}
	
	

}

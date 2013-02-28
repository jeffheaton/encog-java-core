package org.encog.ml.ea.train.species;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.encog.EncogError;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.score.AdjustScore;
import org.encog.ml.ea.score.parallel.ParallelScore;
import org.encog.ml.ea.train.basic.BasicEA;
import org.encog.ml.genetic.GeneticError;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATSpecies;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATTrainWorker;
import org.encog.neural.neat.training.species.Speciation;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.concurrency.MultiThreadable;

public class SpeciesEA extends BasicEA implements MLTrain, MultiThreadable {
	
	/**
	 * The iteration number.
	 */
	private int iteration;
	
	private int threadCount;
	private int actualThreadCount = -1;
	private Speciation speciation;
	private Throwable reportedError;
	private NEATGenome oldBestGenome;
	private List<Genome> newPopulation = new ArrayList<Genome>();
	private EvolutionaryOperator champMutation;
	
	/**
	 * The best ever network.
	 */
	private NEATGenome bestGenome;
	
	

	public SpeciesEA(Population thePopulation, CalculateScore theScoreFunction) {
		super(thePopulation, theScoreFunction);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Not used.
	 * 
	 * @param error
	 *            Not used.
	 */
	@Override
	public void setError(final double error) {
	}
	
	/**
	 * @return True if training can progress no further.
	 */
	@Override
	public boolean isTrainingDone() {
		return false;
	}
	
	@Override
	public TrainingImplementationType getImplementationType() {
		return TrainingImplementationType.Iterative;
	}

	@Override
	public int getIteration() {
		return this.iteration;
	}
	
	@Override
	public void setIteration(final int iteration) {
		this.iteration = iteration;
	}
	
	/**
	 * Perform the specified number of training iterations. This is a basic
	 * implementation that just calls iteration the specified number of times.
	 * However, some training methods, particularly with the GPU, benefit
	 * greatly by calling with higher numbers than 1.
	 * 
	 * @param count
	 *            The number of training iterations.
	 */
	@Override
	public void iteration(final int count) {
		for (int i = 0; i < count; i++) {
			iteration();
		}
	}

	@Override
	public TrainingContinuation pause() {
		return null;
	}

	@Override
	public void resume(final TrainingContinuation state) {

	}
	
	/**
	 * Not supported, will throw an error.
	 * 
	 * @param strategy
	 *            Not used.
	 */
	@Override
	public void addStrategy(final Strategy strategy) {
		throw new TrainingError(
				"Strategies are not supported by this training method.");
	}

	@Override
	public boolean canContinue() {
		return false;
	}

	/**
	 * Called when training is done.
	 */
	@Override
	public void finishTraining() {
		sortPopulation();
	}

	/**
	 * return The error for the best genome.
	 */
	@Override
	public double getError() {
		if (this.bestGenome != null) {
			return this.bestGenome.getScore();
		} else {
			if (this.getScoreFunction().shouldMinimize()) {
				return Double.POSITIVE_INFINITY;
			} else {
				return Double.NEGATIVE_INFINITY;
			}
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
	 * @return the speciation
	 */
	public Speciation getSpeciation() {
		return speciation;
	}

	/**
	 * @param speciation
	 *            the speciation to set
	 */
	public void setSpeciation(Speciation speciation) {
		this.speciation = speciation;
	}

	/**
	 * @return the bestGenome
	 */
	public NEATGenome getBestGenome() {
		return bestGenome;
	}

	public void reportError(Throwable t) {
		synchronized(this) {
			if( this.reportedError==null ) {
				this.reportedError = t;
			}
		}
	}
	
	/**
	 * Sort the genomes.
	 */
	public void sortPopulation() {
		getPopulation().sort(this.getBestComparator());
	}
	
	/**
	 * Perform one training iteration.
	 */
	@Override
	public void iteration() {
		if (this.actualThreadCount == -1) {
			preIteration();
		}

		this.iteration++;

		ExecutorService taskExecutor = null;

		if (this.actualThreadCount == 1) {
			taskExecutor = Executors.newSingleThreadScheduledExecutor();
		} else {
			taskExecutor = Executors.newFixedThreadPool(this.actualThreadCount);
		}

		// Clear new population to just best genome.
		newPopulation.clear();
		this.newPopulation.add(this.bestGenome);
		this.oldBestGenome = this.bestGenome;
		
		// execute species in parallel
		for (final NEATSpecies s : ((NEATPopulation) getPopulation())
				.getSpecies()) {
			NEATTrainWorker worker = new NEATTrainWorker(this, s);
			taskExecutor.execute(worker);
		}

		// wait for threadpool to shutdown
		taskExecutor.shutdown();
		try {
			taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new GeneticError(e);
		}
		
		if( this.reportedError!=null ) {
			throw new GeneticError(this.reportedError);
		}

		getPopulation().clear();
		//getPopulation().addAll(newPopulation);
		
		if( isValidationMode() ) {
			int currentPopSize = this.newPopulation.size();
			int targetPopSize = this.getPopulation().getPopulationSize();
			if( currentPopSize != targetPopSize) {
				throw new EncogError("Population size of "+currentPopSize+" is outside of the target size of " + targetPopSize);
			}
			
			
			if( this.oldBestGenome!=null && 
					!this.newPopulation.contains(this.oldBestGenome)) {
				throw new EncogError("The top genome died, this should never happen!!");
			}
			
			if (this.bestGenome != null
					&& this.oldBestGenome != null
					&& this.getBestComparator().isBetterThan(
							this.oldBestGenome, this.bestGenome)) {
				throw new EncogError(
						"The best genome's score got worse, this should never happen!! Went from "
								+ this.oldBestGenome.getScore() + " to "
								+ this.bestGenome.getScore());
			}
		}

		this.speciation.performSpeciation(newPopulation);
	}

	public boolean addChild(NEATGenome genome) {
		synchronized (this.newPopulation) {
			if (this.newPopulation.size() < this.getPopulation().getPopulationSize()) {
				// don't readd the old best genome, it was already added
				if( genome!=this.oldBestGenome ) {
					
					if( isValidationMode() ) {
						if( this.newPopulation.contains(genome) ) {
							throw new EncogError("Genome already added to population: " + genome.toString());
						}
					}
					
					this.newPopulation.add(genome);
				}
				
				if ( getBestComparator().isBetterThan(genome,this.bestGenome)) {
					this.bestGenome = genome;
				}
				return true;
			} else {
				if( this.isValidationMode() ) {
					//throw new EncogError("Population overflow");
				}
				return false;
			}
		}
	}
	
	/**
	 * @return A network created for the best genome.
	 */
	@Override
	public MLMethod getMethod() {
		if (this.bestGenome != null) {
			return this.getCODEC().decode(this.bestGenome);
		} else {
			return null;
		}
	}
	
	/**
	 * @return the oldBestGenome
	 */
	public NEATGenome getOldBestGenome() {
		return oldBestGenome;
	}
	
	private void preIteration() {

		this.speciation.init(this);

		// find out how many threads to use
		if (this.threadCount == 0) {
			this.actualThreadCount = Runtime.getRuntime().availableProcessors();
		} else {
			this.actualThreadCount = this.threadCount;
		}

		// score the initial population
		ParallelScore pscore = new ParallelScore(getPopulation(),
				this.getCODEC(), new ArrayList<AdjustScore>(),
				this.getScoreFunction(), this.actualThreadCount);
		pscore.setThreadCount(this.actualThreadCount);
		pscore.process();
		this.actualThreadCount = pscore.getThreadCount();
		
		// just pick the first genome as best, it will be updated later.
		// also most populations are sorted this way after training finishes (for reload)
		this.bestGenome = (NEATGenome) this.getPopulation().getGenomes().get(0);

		// speciate
		this.speciation.performSpeciation(this.getPopulation().getGenomes());

	}
	
	/**
	 * Returns null, does not use a training set, rather uses a score function.
	 * 
	 * @return null, not used.
	 */
	@Override
	public MLDataSet getTraining() {
		return null;
	}
	
	/**
	 * Returns an empty list, strategies are not supported.
	 * 
	 * @return The strategies in use(none).
	 */
	@Override
	public List<Strategy> getStrategies() {
		return new ArrayList<Strategy>();
	}

	/**
	 * @return the champMutation
	 */
	public EvolutionaryOperator getChampMutation() {
		return champMutation;
	}

	/**
	 * @param champMutation the champMutation to set
	 */
	public void setChampMutation(EvolutionaryOperator champMutation) {
		this.champMutation = champMutation;
	}
	
	
	
}

/*
 * Encog(tm) Core v3.2 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.neural.neat.training;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLContext;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.score.AdjustScore;
import org.encog.ml.ea.score.parallel.ParallelScore;
import org.encog.ml.ea.sort.MinimizeAdjustedScoreComp;
import org.encog.ml.ea.sort.MinimizeScoreComp;
import org.encog.ml.ea.train.basic.BasicEA;
import org.encog.ml.genetic.GeneticError;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.hyperneat.HyperNEATCODEC;
import org.encog.neural.neat.NEATCODEC;
import org.encog.neural.neat.NEATGenomeFactory;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATSpecies;
import org.encog.neural.neat.training.opp.NEATCrossover;
import org.encog.neural.neat.training.opp.NEATMutateAddLink;
import org.encog.neural.neat.training.opp.NEATMutateAddNode;
import org.encog.neural.neat.training.opp.NEATMutateRemoveLink;
import org.encog.neural.neat.training.opp.NEATMutateWeights;
import org.encog.neural.neat.training.species.SimpleNEATSpeciation;
import org.encog.neural.neat.training.species.Speciation;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.concurrency.MultiThreadable;

/**
 * Implements NEAT genetic training.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
public class NEATTraining extends BasicEA implements MLTrain, MultiThreadable {


	/**
	 * The best ever score.
	 */
	private double bestEverScore;

	/**
	 * The best ever network.
	 */
	private NEATGenome bestEverGenome;

	/**
	 * The number of inputs.
	 */
	private final int inputCount;

	/**
	 * The number of output neurons.
	 */
	private final int outputCount;

	/**
	 * The iteration number.
	 */
	private int iteration;
	
	private Speciation speciation;
	private List<NEATGenome> newPopulation = new ArrayList<NEATGenome>();
	private int threadCount;
	private int actualThreadCount = -1;
	private int maxTries = 5;
	private double mutateRate = 0.2;
	private double probNewMutate = 0.1;
	private double maxPertubation = 0.5;
	private EvolutionaryOperator champMutation;

	/**
	 * Construct a neat trainer with a new population. The new population is
	 * created from the specified parameters.
	 * 
	 * @param calculateScore
	 *            The score calculation object.
	 * @param inputCount
	 *            The input neuron count.
	 * @param outputCount
	 *            The output neuron count.
	 * @param populationSize
	 *            The population size.
	 */
	public NEATTraining(final CalculateScore calculateScore,
			final int inputCount, final int outputCount,
			final int populationSize) {
		super(new NEATPopulation(inputCount, outputCount,
				populationSize), calculateScore);

		getNEATPopulation().reset();
		this.inputCount = inputCount;
		this.outputCount = outputCount;

		setBestComparator(new MinimizeScoreComp());
		setSelectionComparator(new MinimizeAdjustedScoreComp());

		init();
		
	}

	/**
	 * Construct neat training with an existing population.
	 * 
	 * @param calculateScore
	 *            The score object to use.
	 * @param population
	 *            The population to use.
	 */
	public NEATTraining(final CalculateScore calculateScore,
			final NEATPopulation population) {
		super(population,calculateScore);
		
		if (population.size() < 1) {
			throw new TrainingError("Population can not be empty.");
		}
		
		final NEATGenome genome = (NEATGenome) population.getGenomes().get(0);
		setPopulation(population);
		this.inputCount = genome.getInputCount();
		this.outputCount = genome.getOutputCount();
		init();
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

	}

	/**
	 * return The error for the best genome.
	 */
	@Override
	public double getError() {
		return this.bestEverScore;
	}

	@Override
	public TrainingImplementationType getImplementationType() {
		return TrainingImplementationType.Iterative;
	}

	/**
	 * @return The innovations.
	 */
	public NEATInnovationList getInnovations() {
		return (NEATInnovationList)((NEATPopulation)getPopulation()).getInnovations();
	}

	/**
	 * @return The input count.
	 */
	public int getInputCount() {
		return this.inputCount;
	}

	@Override
	public int getIteration() {
		return this.iteration;
	}

	/**
	 * @return A network created for the best genome.
	 */
	@Override
	public MLMethod getMethod() {
		return this.getCODEC().decode(this.bestEverGenome);
	}

	/**
	 * @return The number of output neurons.
	 */
	public int getOutputCount() {
		return this.outputCount;
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
	 * Returns null, does not use a training set, rather uses a score function.
	 * 
	 * @return null, not used.
	 */
	@Override
	public MLDataSet getTraining() {
		return null;
	}

	/**
	 * setup for training.
	 */
	private void init() {
	
		this.champMutation = new NEATMutateWeights();
		addOperation(0.5,new NEATCrossover());
		addOperation(0.40,this.champMutation);
		addOperation(0.05,new NEATMutateAddNode());
		addOperation(0.04,new NEATMutateAddLink());
		addOperation(0.01,new NEATMutateRemoveLink());
		this.getOperators().finalizeStructure();
		
		this.speciation = new SimpleNEATSpeciation();
		this.speciation.init(this);
		
		if (this.getScoreFunction().shouldMinimize()) {
			this.bestEverScore = Double.POSITIVE_INFINITY;
		} else {
			this.bestEverScore = Double.NEGATIVE_INFINITY;
		}
		
		if( this.getNEATPopulation().isHyperNEAT() ) {
			setCODEC(new HyperNEATCODEC());
		} else {
			setCODEC(new NEATCODEC());
		}

		// check the population
		for (final Genome obj : getPopulation().getGenomes()) {
			if (!(obj instanceof NEATGenome)) {
				throw new TrainingError(
						"Population can only contain objects of NEATGenome.");
			}

			final NEATGenome neat = (NEATGenome) obj;

			if ((neat.getInputCount() != this.inputCount)
					|| (neat.getOutputCount() != this.outputCount)) {
				throw new TrainingError(
						"All NEATGenome's must have the same input and output sizes as the base network.");
			}
		}
		
		// score the initial population
		ParallelScore pscore = new ParallelScore(getPopulation(), this.getCODEC(), new ArrayList<AdjustScore>(), this.getScoreFunction(), inputCount);
		pscore.process();

		// sort the population
		sortAndRecord();
		this.speciation.performSpeciation();
		
		if( this.threadCount==0 ) {
			this.actualThreadCount = Runtime.getRuntime().availableProcessors();
		} else {
			this.actualThreadCount = this.threadCount;
		}
	}

	/**
	 * @return True if training can progress no further.
	 */
	@Override
	public boolean isTrainingDone() {
		return false;
	}

	/**
	 * Perform one training iteration.
	 */
	@Override
	public void iteration() {
		this.iteration++;

		ExecutorService taskExecutor = null;
		
		if( this.actualThreadCount==1 ) {
			taskExecutor = Executors.newSingleThreadScheduledExecutor();
		} else {
			taskExecutor = Executors.newFixedThreadPool(this.actualThreadCount);
		}
		
		newPopulation.clear();
		
		// add in the best genome
		if( this.bestEverGenome!=null ) {
			NEATGenome sel = ((NEATGenomeFactory) getPopulation()
					.getGenomeFactory()).factor((NEATGenome)getPopulation().get(0));
			NEATGenome[] parent = { sel };
			parent[0].setGenomeID(getNEATPopulation().assignGenomeID());
			this.champMutation.performOperation(getNEATPopulation().getRandom(), parent, 0, parent, 0);
			this.addChild(parent[0]);
		}

		for (final NEATSpecies s : ((NEATPopulation)getPopulation()).getSpecies()) {
			NEATTrainWorker worker = new NEATTrainWorker(this,s);
			taskExecutor.execute(worker);
		}

		taskExecutor.shutdown();
		try {
			taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new GeneticError(e);
		}
		
		int champRange = Math.min(this.getPopulation().size(), 10);
		
		Random rnd = new Random();

		while (newPopulation.size() < getNEATPopulation().getPopulationSize()) {
			int index = rnd.nextInt(champRange);
			NEATGenome sel = ((NEATGenomeFactory) getPopulation()
					.getGenomeFactory()).factor((NEATGenome)getPopulation().get(index));
			NEATGenome[] parent = { sel };
			parent[0].setGenomeID(getNEATPopulation().assignGenomeID());
			this.champMutation.performOperation(getNEATPopulation().getRandom(), parent, 0, parent, 0);
			this.addChild(parent[0]);
		}

		getPopulation().clear();
		getPopulation().addAll(newPopulation);

		sortAndRecord();
		this.speciation.performSpeciation();
	}
	
	public boolean addChild(NEATGenome genome) {
		synchronized(this.newPopulation) {
			if( this.newPopulation.size()<this.getPopulation().size() ) {
				this.newPopulation.add(genome);
				return true;
			} else {
				return false;
			}
		}
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
	 * Not used.
	 * 
	 * @param error
	 *            Not used.
	 */
	@Override
	public void setError(final double error) {
	}

	@Override
	public void setIteration(final int iteration) {
		this.iteration = iteration;
	}

	/**
	 * Sort the genomes.
	 */
	public void sortAndRecord() {

		getPopulation().sort(this.getBestComparator() );

		final Genome genome = getPopulation().get(0);
		final double currentBest = genome.getScore();

		if (getSelectionComparator().isBetterThan(currentBest, this.bestEverScore)) {
			this.bestEverScore = currentBest;
			this.bestEverGenome = (NEATGenome)genome;
		}

		if( getSelectionComparator().isBetterThan(getError(), this.bestEverScore) ) {
			this.bestEverScore = getError();
		}
		
	}



	/**
	 * Select a gene using a tournament.
	 * 
	 * @param numComparisons
	 *            The number of compares to do.
	 * @return The chosen genome.
	 */
	public NEATGenome tournamentSelection(final int numComparisons) {
		double bestScoreSoFar = 0;

		int ChosenOne = 0;

		for (int i = 0; i < numComparisons; ++i) {
			final int ThisTry = (int) RangeRandomizer.randomize(0,
					getPopulation().size() - 1);

			if (getPopulation().get(ThisTry).getScore() > bestScoreSoFar) {
				ChosenOne = ThisTry;

				bestScoreSoFar = getPopulation().get(ThisTry).getScore();
			}
		}

		return (NEATGenome) getPopulation().get(ChosenOne);
	}
	
	/**
	 * Calculate the score for this genome. The genome's score will be set.
	 * 
	 * @param g
	 *            The genome to calculate for.
	 */
	public void calculateScore(final Genome g) {
		MLMethod phenotype = this.getCODEC().decode(g);
		if (phenotype instanceof MLContext) {
			((MLContext) phenotype).clearContext();
		}
		final double score = this.getScoreFunction().calculateScore(phenotype);
		g.setScore(score);
	}	
	
	public NEATPopulation getNEATPopulation() {
		return (NEATPopulation)getPopulation();
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
	 * @return the maxTries
	 */
	public int getMaxTries() {
		return maxTries;
	}

	/**
	 * @param maxTries the maxTries to set
	 */
	public void setMaxTries(int maxTries) {
		this.maxTries = maxTries;
	}

	/**
	 * @return the mutateRate
	 */
	public double getMutateRate() {
		return mutateRate;
	}

	/**
	 * @param mutateRate the mutateRate to set
	 */
	public void setMutateRate(double mutateRate) {
		this.mutateRate = mutateRate;
	}

	/**
	 * @return the probNewMutate
	 */
	public double getProbNewMutate() {
		return probNewMutate;
	}

	/**
	 * @param probNewMutate the probNewMutate to set
	 */
	public void setProbNewMutate(double probNewMutate) {
		this.probNewMutate = probNewMutate;
	}

	/**
	 * @return the maxPertubation
	 */
	public double getMaxPertubation() {
		return maxPertubation;
	}

	/**
	 * @param maxPertubation the maxPertubation to set
	 */
	public void setMaxPertubation(double maxPertubation) {
		this.maxPertubation = maxPertubation;
	}
	
	
	
}

/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.neural.neat;

import java.io.Serializable;
import java.util.Random;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSteepenedSigmoid;
import org.encog.mathutil.randomize.factory.RandomFactory;
import org.encog.ml.MLError;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.ea.codec.GeneticCODEC;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.BasicPopulation;
import org.encog.ml.ea.species.BasicSpecies;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.hyperneat.FactorHyperNEATGenome;
import org.encog.neural.hyperneat.HyperNEATCODEC;
import org.encog.neural.hyperneat.HyperNEATGenome;
import org.encog.neural.hyperneat.substrate.Substrate;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATInnovationList;
import org.encog.util.identity.BasicGenerateID;
import org.encog.util.identity.GenerateID;
import org.encog.util.obj.ChooseObject;

/**
 * A population for a NEAT or HyperNEAT system. This population holds the
 * genomes, substrate and other values for a NEAT or HyperNEAT network.
 * -----------------------------------------------------------------------------
 * http://www.cs.ucf.edu/~kstanley/ Encog's NEAT implementation was drawn from
 * the following three Journal Articles. For more complete BibTeX sources, see
 * NEATNetwork.java.
 * 
 * Evolving Neural Networks Through Augmenting Topologies
 * 
 * Generating Large-Scale Neural Networks Through Discovering Geometric
 * Regularities
 * 
 * Automatic feature selection in neuroevolution
 */
public class NEATPopulation extends BasicPopulation implements Serializable,
		MLError, MLRegression {

	/**
	 * The default survival rate.
	 */
	public static final double DEFAULT_SURVIVAL_RATE = 0.2;

	/**
	 * The activation function to use.
	 */
	public static final String PROPERTY_NEAT_ACTIVATION = "neatAct";

	/**
	 * Property tag for the population size.
	 */
	public static final String PROPERTY_POPULATION_SIZE = "populationSize";

	/**
	 * Property tag for the survival rate.
	 */
	public static final String PROPERTY_SURVIVAL_RATE = "survivalRate";

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default number of activation cycles.
	 */
	public static final int DEFAULT_CYCLES = 4;

	/**
	 * Property to hold the number of cycles.
	 */
	public static final String PROPERTY_CYCLES = "cycles";

	/**
	 * Default link weight range for NEAT networks.
	 */
	public static final double DEFAULT_NEAT_WEIGHT_RANGE = 1.0;


	/**
	 * Default link weight range for HyperNEAT networks.
	 */
	public static final double DEFAULT_HYPERNEAT_WEIGHT_RANGE = 5.0;

	/**
	 * Change the weight, do not allow the weight to go out of the weight range.
	 * 
	 * @param w
	 *            The amount to change the weight by.
	 * @param weightRange
	 *            Specify the weight range. The range is from -weightRange to
	 *            +weightRange.
	 * @return The new weight value.
	 */
	public static double clampWeight(final double w, final double weightRange) {
		if (w < -weightRange) {
			return -weightRange;
		} else if (w > weightRange) {
			return weightRange;
		} else {
			return w;
		}
	}

	/**
	 * The number of activation cycles that the networks produced by this
	 * population will use.
	 */
	private int activationCycles = NEATPopulation.DEFAULT_CYCLES;

	/**
	 * Generate gene id's.
	 */
	private final GenerateID geneIDGenerate = new BasicGenerateID();

	/**
	 * Generate innovation id's.
	 */
	private final GenerateID innovationIDGenerate = new BasicGenerateID();

	/**
	 * A list of innovations, or null if this feature is not being used.
	 */
	private NEATInnovationList innovations;

	/**
	 * The weight range. Weights will be between -weight and +weight.
	 */
	private double weightRange = DEFAULT_NEAT_WEIGHT_RANGE;

	/**
	 * The best genome that we've currently decoded into the bestNetwork
	 * property. If this value changes to point to a new genome reference then
	 * the phenome will need to be recalculated.
	 */
	private Genome cachedBestGenome;

	/**
	 * The best network. If the population is used as an MLMethod, then this
	 * network will represent.
	 */
	private NEATNetwork bestNetwork;

	/**
	 * The number of input units. All members of the population must agree with
	 * this number.
	 */
	int inputCount;

	/**
	 * The number of output units. All members of the population must agree with
	 * this number.
	 */
	int outputCount;

	/**
	 * The survival rate.
	 */
	private double survivalRate = NEATPopulation.DEFAULT_SURVIVAL_RATE;

	/**
	 * The substrate, if this is a hyperneat network.
	 */
	private Substrate substrate;

	/**
	 * The activation functions that we can choose from.
	 */
	private final ChooseObject<ActivationFunction> activationFunctions = new ChooseObject<ActivationFunction>();

	/**
	 * The CODEC used to decode the NEAT genomes into networks. Different
	 * CODEC's are used for NEAT vs HyperNEAT.
	 */
	private GeneticCODEC codec;

	/**
	 * The initial connection density for the initial random population of
	 * genomes.
	 */
	private double initialConnectionDensity = 0.1;

	/**
	 * A factory to create random number generators.
	 */
	private RandomFactory randomNumberFactory = Encog.getInstance()
			.getRandomFactory().factorFactory();

	/**
	 * An empty constructor for serialization.
	 */
	public NEATPopulation() {

	}

	/**
	 * Construct a starting NEAT population. This does not generate the initial
	 * random population of genomes.
	 * 
	 * @param inputCount
	 *            The input neuron count.
	 * @param outputCount
	 *            The output neuron count.
	 * @param populationSize
	 *            The population size.
	 */
	public NEATPopulation(final int inputCount, final int outputCount,
			final int populationSize) {
		super(populationSize, null);
		this.inputCount = inputCount;
		this.outputCount = outputCount;

		setNEATActivationFunction(new ActivationSteepenedSigmoid());

		if (populationSize == 0) {
			throw new NeuralNetworkError(
					"Population must have more than zero genomes.");
		}

	}

	/**
	 * Construct a starting HyperNEAT population. This does not generate the
	 * initial random population of genomes.
	 * 
	 * @param theSubstrate
	 *            The substrate ID.
	 * @param populationSize The population size.
	 */
	public NEATPopulation(final Substrate theSubstrate, final int populationSize) {
		super(populationSize, new FactorHyperNEATGenome());
		this.substrate = theSubstrate;
		this.inputCount = 6;
		this.outputCount = 2;
		this.weightRange = DEFAULT_HYPERNEAT_WEIGHT_RANGE;
		HyperNEATGenome.buildCPPNActivationFunctions(this.activationFunctions);
	}

	/**
	 * @return A newly generated gene id.
	 */
	public long assignGeneID() {
		return this.geneIDGenerate.generate();
	}

	/**
	 * @return A newly generated innovation id.
	 */
	public long assignInnovationID() {
		return this.innovationIDGenerate.generate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateError(final MLDataSet data) {
		updateBestNetwork();
		if( this.bestNetwork==null ) {
			return Double.POSITIVE_INFINITY;
		}
		return this.bestNetwork.calculateError(data);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLData compute(final MLData input) {
		updateBestNetwork();
		return this.bestNetwork.compute(input);
	}

	/**
	 * @return Get the activation cycles.
	 */
	public int getActivationCycles() {
		return this.activationCycles;
	}

	/**
	 * @return the activationFunctions
	 */
	public ChooseObject<ActivationFunction> getActivationFunctions() {
		return this.activationFunctions;
	}

	/**
	 * @return the codec
	 */
	public GeneticCODEC getCODEC() {
		return this.codec;
	}

	/**
	 * @return the geneIDGenerate
	 */
	public GenerateID getGeneIDGenerate() {
		return this.geneIDGenerate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NEATGenomeFactory getGenomeFactory() {
		return (NEATGenomeFactory) super.getGenomeFactory();
	}

	/**
	 * @return the initialConnectionDensity
	 */
	public double getInitialConnectionDensity() {
		return this.initialConnectionDensity;
	}

	/**
	 * @return the innovationIDGenerate
	 */
	public GenerateID getInnovationIDGenerate() {
		return this.innovationIDGenerate;
	}

	/**
	 * @return Get the innovations.
	 */
	public NEATInnovationList getInnovations() {
		return this.innovations;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInputCount() {
		return this.inputCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getOutputCount() {
		return this.outputCount;
	}

	/**
	 * @return the randomNumberFactory
	 */
	public RandomFactory getRandomNumberFactory() {
		return this.randomNumberFactory;
	}

	/**
	 * @return Returns the hyper-neat substrate.
	 */
	public Substrate getSubstrate() {
		return this.substrate;
	}

	/**
	 * @return The survival rate, this is the number of genomes used to mate.
	 */
	public double getSurvivalRate() {
		return this.survivalRate;
	}

	/**
	 * @return the weightRange
	 */
	public double getWeightRange() {
		return this.weightRange;
	}

	/**
	 * @return Returns true if this is a hyperneat population.
	 */
	public boolean isHyperNEAT() {
		return this.substrate != null;
	}

	/**
	 * Create an initial random population.
	 */
	public void reset() {
		// create the genome factory
		if (isHyperNEAT()) {
			this.codec = new HyperNEATCODEC();
			setGenomeFactory(new FactorHyperNEATGenome());
		} else {
			this.codec = new NEATCODEC();
			setGenomeFactory(new FactorNEATGenome());
		}

		// create the new genomes
		getSpecies().clear();

		// reset counters
		getGeneIDGenerate().setCurrentID(1);
		getInnovationIDGenerate().setCurrentID(1);

		final Random rnd = this.randomNumberFactory.factor();

		// create one default species
		final BasicSpecies defaultSpecies = new BasicSpecies();
		defaultSpecies.setPopulation(this);

		// create the initial population
		for (int i = 0; i < getPopulationSize(); i++) {
			final NEATGenome genome = getGenomeFactory().factor(rnd, this,
					this.inputCount, this.outputCount,
					this.initialConnectionDensity);
			defaultSpecies.add(genome);
		}
		defaultSpecies.setLeader(defaultSpecies.getMembers().get(0));
		getSpecies().add(defaultSpecies);

		// create initial innovations
		setInnovations(new NEATInnovationList(this));
	}

	/**
	 * Set the number of activation cycles to use.
	 * @param activationCycles The number of activatino cycles to use.
	 */
	public void setActivationCycles(final int activationCycles) {
		this.activationCycles = activationCycles;
	}

	/**
	 * @param codec
	 *            the codec to set
	 */
	public void setCODEC(final GeneticCODEC codec) {
		this.codec = codec;
	}

	/**
	 * @param initialConnectionDensity
	 *            the initialConnectionDensity to set
	 */
	public void setInitialConnectionDensity(
			final double initialConnectionDensity) {
		this.initialConnectionDensity = initialConnectionDensity;
	}

	/**
	 * Set the innovation list to use.
	 * @param theInnovations The innovation list to use.
	 */
	public void setInnovations(final NEATInnovationList theInnovations) {
		this.innovations = theInnovations;
	}

	/**
	 * @param inputCount
	 *            the inputCount to set
	 */
	public void setInputCount(final int inputCount) {
		this.inputCount = inputCount;
	}

	/**
	 * Specify to use a single activation function. This is typically the case
	 * for NEAT, but not for HyperNEAT.
	 * 
	 * @param af The activation function to use.
	 */
	public void setNEATActivationFunction(final ActivationFunction af) {
		this.activationFunctions.clear();
		this.activationFunctions.add(1.0, af);
		this.activationFunctions.finalizeStructure();
	}

	/**
	 * @param outputCount
	 *            the outputCount to set
	 */
	public void setOutputCount(final int outputCount) {
		this.outputCount = outputCount;
	}

	/**
	 * @param randomNumberFactory
	 *            the randomNumberFactory to set
	 */
	public void setRandomNumberFactory(final RandomFactory randomNumberFactory) {
		this.randomNumberFactory = randomNumberFactory;
	}

	/**
	 * @param substrate
	 *            the substrate to set
	 */
	public void setSubstrate(final Substrate substrate) {
		this.substrate = substrate;
	}

	/**
	 * Set the survival rate, this is the percent of the population allowed to mate.
	 * @param theSurvivalRate The survival rate.
	 */
	public void setSurvivalRate(final double theSurvivalRate) {
		this.survivalRate = theSurvivalRate;
	}

	/**
	 * Sets the weight range for links in genomes in this population.
	 * @param weightRange The link weight range.
	 */
	public void setWeightRange(final double weightRange) {
		this.weightRange = weightRange;
	}

	/**
	 * See if the best genome has changed, and decode a new best network, if
	 * needed.
	 */
	private void updateBestNetwork() {
		if (getBestGenome() != this.cachedBestGenome) {
			this.cachedBestGenome = getBestGenome();
			this.bestNetwork = (NEATNetwork) getCODEC().decode(getBestGenome());
		}
	}

}

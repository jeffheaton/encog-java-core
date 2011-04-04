package org.encog.neural.neat;

import java.io.Serializable;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.genetic.population.BasicPopulation;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATInnovationList;

public class NEATPopulation extends BasicPopulation implements Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String PROPERTY_NEAT_ACTIVATION = "neatAct";
	public static final String PROPERTY_OUTPUT_ACTIVATION = "outAct";

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
	 * The activation function for neat to use.
	 */
	private ActivationFunction neatActivationFunction = new ActivationSigmoid();
	
	private boolean snapshot;

	/**
	 * The activation function to use on the output layer of Encog.
	 */
	private ActivationFunction outputActivationFunction = new ActivationLinear();

	/**
	 * Construct a starting NEAT population.
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
	public NEATPopulation(final int inputCount, final int outputCount,
			final int populationSize) {
		super(populationSize);
		this.inputCount = inputCount;
		this.outputCount = outputCount;

		if (populationSize == 0) {
			throw new NeuralNetworkError(
					"Population must have more than zero genomes.");
		}

		// create the initial population
		for (int i = 0; i < populationSize; i++) {
			NEATGenome genome = new NEATGenome(assignGenomeID(), inputCount,
					outputCount);
			add(genome);
		}

		// create initial innovations
		NEATGenome genome = (NEATGenome) this.getGenomes().get(0);
		this.setInnovations(new NEATInnovationList(this, genome.getLinks(),
				genome.getNeurons()));

	}

	public NEATPopulation() {

	}

	/**
	 * @return the inputCount
	 */
	public int getInputCount() {
		return inputCount;
	}

	/**
	 * @param inputCount the inputCount to set
	 */
	public void setInputCount(int inputCount) {
		this.inputCount = inputCount;
	}

	/**
	 * @return the outputCount
	 */
	public int getOutputCount() {
		return outputCount;
	}

	/**
	 * @param outputCount the outputCount to set
	 */
	public void setOutputCount(int outputCount) {
		this.outputCount = outputCount;
	}

	/**
	 * @return the neatActivationFunction
	 */
	public ActivationFunction getNeatActivationFunction() {
		return neatActivationFunction;
	}

	/**
	 * @param neatActivationFunction the neatActivationFunction to set
	 */
	public void setNeatActivationFunction(
			ActivationFunction neatActivationFunction) {
		this.neatActivationFunction = neatActivationFunction;
	}

	/**
	 * @return the outputActivationFunction
	 */
	public ActivationFunction getOutputActivationFunction() {
		return outputActivationFunction;
	}

	/**
	 * @param outputActivationFunction the outputActivationFunction to set
	 */
	public void setOutputActivationFunction(
			ActivationFunction outputActivationFunction) {
		this.outputActivationFunction = outputActivationFunction;
	}

	/**
	 * @return the snapshot
	 */
	public boolean isSnapshot() {
		return snapshot;
	}

	/**
	 * @param snapshot the snapshot to set
	 */
	public void setSnapshot(boolean snapshot) {
		this.snapshot = snapshot;
	}
	
	

}

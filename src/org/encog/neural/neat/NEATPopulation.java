package org.encog.neural.neat;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.genetic.population.BasicPopulation;
import org.encog.ml.genetic.population.Population;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.persist.Persistor;
import org.encog.persist.map.PersistConst;
import org.encog.persist.map.PersistedObject;
import org.encog.persist.persistors.generic.GenericPersistor;

public class NEATPopulation extends BasicPopulation {

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
	public NEATPopulation(
			final int inputCount, final int outputCount,
			final int populationSize) {

		this.inputCount = inputCount;
		this.outputCount = outputCount;

		// create the initial population
		for (int i = 0; i < populationSize; i++) {
			add(new NEATGenome(assignGenomeID(), inputCount, outputCount));
		}
	}
	
	public NEATPopulation() {
		
	}

	public NEATPopulation(int populationSize) {
		super(populationSize);
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
	public void setNeatActivationFunction(ActivationFunction neatActivationFunction) {
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
	
	public void persistToMap(PersistedObject obj)
	{
		obj.clear(PersistConst.TYPE_NEAT_POPULATION);
		obj.setStandardProperties(this);
		populationToMap(obj);		
	}
	
	public void persistFromMap(PersistedObject obj)
	{
		obj.requireType(PersistConst.TYPE_NEAT_POPULATION);
		populationFromMap(obj);
	}

	/**
	 * @return A persistor for this object.
	 */
	public Persistor createPersistor() {
		return new GenericPersistor(NEATPopulation.class);
	}

	
}

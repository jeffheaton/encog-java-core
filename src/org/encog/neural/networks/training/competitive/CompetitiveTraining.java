/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.neural.networks.training.competitive;

import java.util.Collection;

import org.encog.matrix.Matrix;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.competitive.neighborhood.NeighborhoodFunction;
import org.encog.util.math.BoundMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements competitive training, which would be used in a
 * winner-take-all neural network, such as the self organizing map (SOM). This
 * is an unsupervised training method, no ideal data is needed on the training
 * set. If ideal data is provided, it will be ignored.
 * 
 * A neighborhood function is required to determine the degree to which
 * neighboring neurons (to the winning neuron) are updated by each training
 * iteration.
 * 
 * @author jheaton
 * 
 */
public class CompetitiveTraining extends BasicTraining implements LearningRate {

	/**
	 * The neighborhood function to use to determine to what degree a neuron
	 * should be "trained".
	 */
	private final NeighborhoodFunction neighborhood;

	/**
	 * The learning rate. To what degree should changes be applied.
	 */
	private double learningRate;

	/**
	 * The network being trained.
	 */
	private final BasicNetwork network;

	/**
	 * The input layer.
	 */
	private final Layer inputLayer;

	/**
	 * The output layer.
	 */
	private final Layer outputLayer;

	/**
	 * A collection of the synases being modified.
	 */
	private final Collection<Synapse> synapses;

	/**
	 * How many neurons in the input layer.
	 */
	private final int inputNeuronCount;

	/**
	 * How many neurons in the output layer.
	 */
	private final int outputNeuronCount;

	private double worstDistance;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Create an instance of competitive training.
	 * 
	 * @param network
	 *            The network to train.
	 * @param learningRate
	 *            The learning rate, how much to apply per iteration.
	 * @param training
	 *            The training set (unsupervised).
	 * @param neighborhood
	 *            The neighborhood function to use.
	 */
	public CompetitiveTraining(final BasicNetwork network,
			final double learningRate, final NeuralDataSet training,
			final NeighborhoodFunction neighborhood) {
		this.neighborhood = neighborhood;
		setTraining(training);
		this.learningRate = learningRate;
		this.network = network;
		this.inputLayer = network.getInputLayer();
		this.outputLayer = network.getOutputLayer();
		this.synapses = network.getStructure().getPreviousSynapses(
				this.outputLayer);
		this.inputNeuronCount = this.inputLayer.getNeuronCount();
		this.outputNeuronCount = this.outputLayer.getNeuronCount();
		setError(0);

		// set the threshold to zero
		for (final Synapse synapse : this.synapses) {
			final Matrix matrix = synapse.getMatrix();
			for (int col = 0; col < matrix.getCols(); col++) {
				matrix.set(matrix.getRows() - 1, col, 0);
			}
		}
	}


	/**
	 * @return The learning rate. This was set when the object was created.
	 */
	public double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * @return The network neighborhood function.
	 */
	public NeighborhoodFunction getNeighborhood() {
		return this.neighborhood;
	}

	/**
	 * @return The network being trained.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	private double calculateEuclideanDistance(Synapse synapse,
			NeuralData input, int outputNeuron) {
		double result = 0;
		for (int i = 0; i < input.size(); i++) {
			double diff = input.getData(i) - synapse.getMatrix().get(i, outputNeuron);
			result += diff*diff;
		}
		return BoundMath.sqrt(result);
	}

	private int calculateBMU(Synapse synapse, NeuralData input) {
		int result = 0;
		double lowestDistance = Double.MAX_VALUE;

		for (int i = 0; i < this.outputNeuronCount; i++) {
			double distance = calculateEuclideanDistance(synapse, input, i);

			// Track the lowest distance, this is the BMU.
			if (distance < lowestDistance) {
				lowestDistance = distance;
				result = i;
			}
		}
		
		// Track the worst distance, this is the error for the entire network.
		if( lowestDistance> this.worstDistance ) {
			worstDistance = lowestDistance;
		}
		
		return result;
	}

	/**
	 * Perform one training iteration.
	 */
	public void iteration() {

		if (this.logger.isInfoEnabled()) {
			this.logger.info("Performing Competitive Training iteration.");
		}

		preIteration();

		this.worstDistance = Double.MIN_VALUE;

		// Apply competitive training
		for (final NeuralDataPair pair : getTraining()) {

			final NeuralData input = pair.getInput();

			for (final Synapse synapse : this.synapses) {

				final int bmu = calculateBMU(synapse, input);

				// adjust the weight for the BMU and its neighborhood
				for (int outputNeuron = 0; outputNeuron < this.outputNeuronCount; outputNeuron++) {
					for (int inputNeuron = 0; inputNeuron < this.inputNeuronCount; inputNeuron++) {
						
						double currentWeight = synapse.getMatrix().get(inputNeuron, outputNeuron);
						double inputValue = input.getData(inputNeuron);
						
						double newWeight = adjustWeight(
								currentWeight, 
								inputValue, 
								outputNeuron, 
								bmu);
						
						synapse.getMatrix().set(inputNeuron, outputNeuron, newWeight);
					}
				}
			}
		}

		// update the error

		this.learningRate *= 0.95;

		setError(this.worstDistance);

		postIteration();
	}
	
	/**
	 * Adjusts the weight for a single neuron during a training iteration.
	 * 
	 * @param startingWeight
	 *            The starting weight.
	 * @param input
	 *            The input to this neuron.
	 * @param currentNeuron
	 *            The neuron who's weight is being updated.
	 * @param bestNeuron
	 *            The neuron that "won".
	 * @return The new weight value.
	 */
	private double adjustWeight(final double weight,
			final double input, final int currentNeuron, final int bmu) {

		double delta = this.neighborhood.function(currentNeuron, bmu)
				* this.learningRate * (input - weight);
		
		return weight+delta;
	}

	/**
	 * Set the learning rate. This is the rate at which the weights are changed.
	 * 
	 * @param rate
	 *            The learning rate.
	 */
	public void setLearningRate(final double rate) {
		this.learningRate = rate;
	}

}

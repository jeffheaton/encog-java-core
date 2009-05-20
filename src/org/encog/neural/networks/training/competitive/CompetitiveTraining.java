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
	 * Keep track of which neurons "won".
	 */
	private final int[] won;

	/**
	 * A collection of the synases being modified.
	 */
	private final Collection<Synapse> synapses;

	/**
	 * How many neurons in the input layer.
	 */
	private final int inputNeuronCount;

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
		setError(0);
		this.won = new int[this.outputLayer.getNeuronCount()];

		// set the threshold to zero
		for (final Synapse synapse : this.synapses) {
			final Matrix matrix = synapse.getMatrix();
			for (int col = 0; col < matrix.getCols(); col++) {
				matrix.set(matrix.getRows() - 1, col, 0);
			}
		}
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
	private double adjustWeight(final double startingWeight,
			final double input, final int currentNeuron, final int bestNeuron) {
		double wt = startingWeight;
		final double vw = input;
		wt += this.neighborhood.function(currentNeuron, bestNeuron)
				* this.learningRate * (vw - wt);
		return wt;

	}

	/**
	 * We would not like to have "unutilized output neurons". If there was an
	 * output neuron that did not react to anything, then force it to be a
	 * winner for a training item.
	 */
	private void forceWin() {
		int best;
		NeuralDataPair which = null;

		// final Matrix outputWeights = this.som.getOutputWeights();

		// Loop over all training sets. Find the training set with
		// the least output.
		double dist = Double.MAX_VALUE;
		for (final NeuralDataPair pair : getTraining()) {
			final NeuralData output = this.network.compute(pair.getInput());
			best = BasicNetwork.determineWinner(output);

			if (output.getData(best) < dist) {
				dist = output.getData(best);
				which = pair;
			}
		}

		final NeuralData output = this.network.compute(which.getInput());
		best = BasicNetwork.determineWinner(output);

		dist = Double.MIN_VALUE;
		int i = this.network.getOutputLayer().getNeuronCount();
		int whichNeuron = -1;
		while ((i--) > 0) {
			if (this.won[i] != 0) {
				continue;
			}
			if (output.getData(i) > dist) {
				dist = output.getData(i);
				whichNeuron = i;
			}
		}

		// update the weights

		if (whichNeuron != -1) {
			for (final Synapse synapse : this.synapses) {
				for (int j = 0; j < which.getInput().size(); j++) {
					synapse.getMatrix().set(whichNeuron, j,
							which.getInput().getData(j));
				}
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

	/**
	 * Perform one training iteration.
	 */
	public void iteration() {

		if (this.logger.isInfoEnabled()) {
			this.logger.info("Performing Competitive Training iteration.");
		}

		preIteration();

		for (int i = 0; i < this.won.length; i++) {
			this.won[i] = 0;
		}

		double error = 0;
		int trainingSize = 0;

		// Apply competitive training
		for (final NeuralDataPair pair : getTraining()) {

			trainingSize++;

			final NeuralData input = pair.getInput();
			final int best = this.network.winner(input);

			double length = 0.0;

			this.won[best]++;
			for (final Synapse synapse : this.synapses) {
				final Matrix wptr = synapse.getMatrix().getCol(best);

				for (int i = 0; i < this.inputNeuronCount; i++) {
					final double diff = input.getData(i) - wptr.get(i, 0);
					length += diff * diff;
					final double newWeight = adjustWeight(wptr.get(i, 0), input
							.getData(i), best, i);
					synapse.getMatrix().set(i, best, newWeight);
				}
			}

			if (length > error) {
				error = length;
			}
		}

		// see if there are any neurons that "did not win"

		int winners = 0;
		for (final int element : this.won) {
			if (element != 0) {
				winners++;
			}
		}

		if ((winners < getNetwork().getOutputLayer().getNeuronCount())
				&& (winners < trainingSize)) {
			forceWin();
		}

		// update the error

		setError(Math.sqrt(error));

		postIteration();
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

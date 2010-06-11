/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.networks.training.hebbian;

import java.util.List;
import java.util.ArrayList;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.TrainingError;

/**
 * This class implements Hebbian learning for Enocg. This class specifically
 * handles the following three cases of Hebbian learning.
 * 
 * Supervised Hebbian Learning Unsupervised Hebbian Learning OJA Unsupervised
 * Hebbian Learning
 * 
 * Choosing between supervised and unsupervised is simply a matter of passing in
 * training data that has ideal outputs, in the case of supervised, or lacks
 * ideal outputs, in the case of unsupervised.
 * 
 * OJA's rule can be used with unsupervised training. It can be specified using
 * a flag to the constructor. For more information on OJA's rule, see:
 * 
 * http://en.wikipedia.org/wiki/Oja%27s_rule
 * 
 */
public class HebbianTraining extends BasicTraining implements LearningRate {

	/**
	 * The learning rate.
	 */
	private double learningRate;

	/**
	 * The network that is to be trained.
	 */
	private final BasicNetwork network;

	/**
	 * The training data to be used.
	 */
	private final NeuralDataSet training;

	/**
	 * Holds a true value if this training is supervised.
	 */
	private final boolean supervised;

	/**
	 * True if OJA's rule should be used.
	 */
	private final boolean oja;

	/**
	 * The output layer that is being trained.
	 */
	private final Layer outputLayer;

	/**
	 * The output synapses that are being trained.
	 */
	private List<Synapse> outputSynapse = new ArrayList<Synapse>();

	/**
	 * Construct a Hebbian training object. It will train in supervised or
	 * unsupervised mode, depending on the nature of the training data.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data.
	 * @param oja
	 *            True of OJA's rule should be used. This can only be used with
	 *            unsupervised data.
	 * @param learningRate
	 *            The learning rate.
	 */
	public HebbianTraining(final BasicNetwork network,
			final NeuralDataSet training, final boolean oja,
			final double learningRate) {
		this.network = network;
		this.training = training;
		this.learningRate = learningRate;
		this.supervised = training.getIdealSize() > 0;
		this.oja = oja;
		this.outputLayer = this.network.getLayer(BasicNetwork.TAG_OUTPUT);

		if (this.outputLayer == null) {
			throw new TrainingError(
					"Can't use Hebbian training without an output layer.");
		}

		if ( (this.oja == true) && (this.supervised == true)) {
			throw new TrainingError(
					"Can't use OJA Hebbian training with supervised data.");
		}

		this.outputSynapse = this.network.getStructure().getPreviousSynapses(
				this.outputLayer);

		if (this.outputSynapse.size() == 0) {
			throw new TrainingError(
				"Can't use Hebbian learning, the output layer " +
				"has no inbound synapses.");
		}

	}

	/**
	 * @return The learning rate.
	 */
	public double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * @return The network to train.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * @return The training data.
	 */
	@Override
	public NeuralDataSet getTraining() {
		return this.training;
	}

	/**
	 * @return True if OJA's rule is in use.
	 */
	public boolean isOja() {
		return this.oja;
	}

	/**
	 * @return True if this is supervised training.
	 */
	public boolean isSupervised() {
		return this.supervised;
	}

	/**
	 * Perform a single training iteration.
	 */
	public void iteration() {
		preIteration();

		for (final NeuralDataPair pair : this.training) {
			for (final Synapse synapse : this.outputSynapse) {
				trainSynapse(synapse, pair);
			}
		}
		postIteration();
	}

	/**
	 * Set the learning date.
	 * @param rate The new learning rate.
	 */
	public void setLearningRate(final double rate) {
		this.learningRate = rate;
	}

	/**
	 * Train a single synapse.
	 * @param synapse The synapse to train.
	 * @param pair The data to train it with.
	 */
	private void trainSynapse(final Synapse synapse, 
			final NeuralDataPair pair) {
		final NeuralData outputData = this.network.compute(pair.getInput());

		final double[] input = pair.getInput().getData();
		final double[] output = outputData.getData();
		final double[][] matrix = synapse.getMatrix().getData();

		for (int toNeuron = 0; toNeuron < synapse.getToNeuronCount(); 
			toNeuron++) {

			double z;

			if (this.supervised) {
				z = pair.getIdeal().getData(toNeuron);
			} else {
				z = this.learningRate;
			}

			double deltaWeight;

			for (int fromNeuron = 0; fromNeuron  
				< synapse.getFromNeuronCount(); fromNeuron++) {

				if (this.oja) {
					deltaWeight = (input[fromNeuron] - output[toNeuron]
							* matrix[fromNeuron][toNeuron])
							* output[toNeuron] * this.learningRate;
				} else {
					deltaWeight = input[toNeuron] * output[toNeuron] * z;
				}

				matrix[fromNeuron][toNeuron] += deltaWeight;
			}
		}
	}

}

/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog.neural.networks.training.hebbian;

import org.encog.mathutil.matrices.Matrix;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
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
		
		if ( (this.oja == true) && (this.supervised == true)) {
			throw new TrainingError(
					"Can't use OJA Hebbian training with supervised data.");
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
			trainLayer(this.network,this.network.getLayerCount()-2,pair);
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
	private void trainLayer(final BasicNetwork network, int layerNumber,
			final NeuralDataPair pair) {
		final NeuralData outputData = this.network.compute(pair.getInput());

		final double[] input = pair.getInput().getData();
		final double[] output = outputData.getData();

		for (int toNeuron = 0; toNeuron < network.getOutputCount(); 
			toNeuron++) {

			double z;

			if (this.supervised) {
				z = pair.getIdeal().getData(toNeuron);
			} else {
				z = this.learningRate;
			}

			double deltaWeight;

			for (int fromNeuron = 0; fromNeuron  
				< network.getLayerTotalNeuronCount(fromNeuron); fromNeuron++) {

				if (this.oja) {
					deltaWeight = (input[fromNeuron] - output[toNeuron]
							* network.getWeight(layerNumber,fromNeuron,toNeuron)
							* output[toNeuron] * this.learningRate);
				} else {
					deltaWeight = input[toNeuron] * output[toNeuron] * z;
				}

				network.addWeight(layerNumber,fromNeuron,toNeuron,deltaWeight);
			}
		}
	}

}

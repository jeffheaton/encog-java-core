/*
 * Encog(tm) Core v2.6 - Java Version
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
package org.encog.neural.networks.training.lma;

import java.util.List;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.networks.BasicNetwork;

/**
 * Calculate the Jacobian using the chain rule.
 * ----------------------------------------------------
 * 
 * This implementation of the Levenberg Marquardt algorithm is based heavily on code
 * published in an article by Cesar Roberto de Souza.  The original article can be
 * found here:
 * 
 * http://crsouza.blogspot.com/2009/11/neural-network-learning-by-levenberg_18.html
 * 
 * Portions of this class are under the following copyright/license.
 * Copyright 2009 by Cesar Roberto de Souza, Released under the LGPL.
 */
public class JacobianChainRule implements ComputeJacobian {
	/**
	 * The network that is to be trained.
	 */
	private final BasicNetwork network;

	/**
	 * THe training set to use. Must be indexable.
	 */
	private final NeuralDataSet indexableTraining;

	/**
	 * The number of training set elements.
	 */
	private final int inputLength;

	/**
	 * The number of weights and bias values in the neural network.
	 */
	private final int parameterSize;

	/**
	 * The Jacobian matrix that was calculated.
	 */
	private final double[][] jacobian;

	/**
	 * The current row in the Jacobian matrix.
	 */
	private int jacobianRow;

	/**
	 * The current column in the Jacobian matrix.
	 */
	private int jacobianCol;

	/**
	 * Used to read the training data.
	 */
	private final NeuralDataPair pair;

	/**
	 * The errors for each row in the Jacobian.
	 */
	private final double[] rowErrors;

	/**
	 * The current error.
	 */
	private double error;

	/**
	 * Construct the chain rule calculation.
	 * 
	 * @param network
	 *            The network to use.
	 * @param indexableTraining
	 *            The training set to use.
	 */
	public JacobianChainRule(final BasicNetwork network,
			final NeuralDataSet indexableTraining) {
		this.indexableTraining = indexableTraining;
		this.network = network;
		this.parameterSize = network.getStructure().calculateSize();
		this.inputLength = (int) this.indexableTraining.getRecordCount();
		this.jacobian = new double[this.inputLength][this.parameterSize];
		this.rowErrors = new double[this.inputLength];

		final BasicNeuralData input = new BasicNeuralData(
				this.indexableTraining.getInputSize());
		final BasicNeuralData ideal = new BasicNeuralData(
				this.indexableTraining.getIdealSize());
		this.pair = new BasicNeuralDataPair(input, ideal);
	}

	/**
	 * Calculate the derivative.
	 * 
	 * @param a
	 *            The activation function.
	 * @param d
	 *            The value to calculate for.
	 * @return The derivative.
	 */
	private double calcDerivative(final ActivationFunction a, final double d) {

		return a.derivativeFunction(d);
	}

	/**
	 * Calculate the derivative.
	 * 
	 * @param a
	 *            The activation function.
	 * @param d
	 *            The value to calculate for.
	 * @return The derivative.
	 */
	private double calcDerivative2(final ActivationFunction a, final double d) {
		final double[] temp = new double[1];
		temp[0] = d;
		a.activationFunction(temp,0,temp.length);
		temp[0] = a.derivativeFunction(temp[0]);
		return temp[0];
	}

	/**
	 * Calculate the Jacobian matrix.
	 * 
	 * @param weights
	 *            The weights for the neural network.
	 * @return The sum squared of the weights.
	 */
	public double calculate(final double[] weights) {
		double result = 0.0;

		for (int i = 0; i < this.inputLength; i++) {
			this.jacobianRow = i;
			this.jacobianCol = 0;

			this.indexableTraining.getRecord(i, this.pair);

			final double e = calculateDerivatives(this.pair);
			this.rowErrors[i] = e;
			result += e * e;

		}

		return result / 2.0;
	}

	/**
	 * Calculate the derivatives for this training set element.
	 * 
	 * @param pair
	 *            The training set element.
	 * @return The sum squared of errors.
	 */
	private double calculateDerivatives(final NeuralDataPair pair) {
		// error values
		double e = 0.0;
		double sum = 0.0;
/*
		final ActivationFunction function = this.network.getLayer(
				BasicNetwork.TAG_INPUT).getActivationFunction();

		this.network.compute(pair.getInput());

		int synapseNumber = 0;

		Synapse synapse = synapses.get(synapseNumber++);

		double output = holder.getOutput().getData(0);
		e = pair.getIdeal().getData(0) - output;

		for (int i = 0; i < synapse.getFromNeuronCount(); i++) {
			final double lastOutput = holder.getResult().get(synapse)
					.getData(i);

			this.jacobian[this.jacobianRow][this.jacobianCol++] 
			    = calcDerivative(
					function, output)
					* lastOutput;
		}
		
		this.jacobian[this.jacobianRow][this.jacobianCol++] = calcDerivative(
				function, output);


		Synapse lastSynapse;

		while (synapseNumber < synapses.size()) {
			lastSynapse = synapse;
			synapse = synapses.get(synapseNumber++);
			final NeuralData outputData = holder.getResult().get(lastSynapse);

			// for each neuron in the input layer
			for (int neuron = 0; neuron 
			  < synapse.getToNeuronCount(); neuron++) {
				output = outputData.getData(neuron);
				
				final double w = lastSynapse.getMatrix().get(neuron, 0);
				final double val = calcDerivative(function, output)
						* calcDerivative2(function, sum) * w;

				// for each weight of the input neuron
				for (int i = 0; i < synapse.getFromNeuronCount(); i++) {
					sum = 0.0;
					// for each neuron in the next layer
					for (int j = 0; j < lastSynapse.getToNeuronCount(); j++) {
						// for each weight of the next neuron
						for (int k = 0; k 
						  < lastSynapse.getFromNeuronCount(); k++) {
							sum += lastSynapse.getMatrix().get(k, j) * output;
						}
						sum += lastSynapse.getToLayer().getBiasWeight(j);
					}

					this.jacobian[this.jacobianRow][this.jacobianCol++] = val
							* holder.getResult().get(synapse).getData(i);
				}
				
				if( synapse.getToLayer().hasBias() ) {
					this.jacobian[this.jacobianRow][this.jacobianCol++] += val;
				}
			}
		}
*/
		// return error
		return e;
	}

	/**
	 * @return The sum squared errors.
	 */
	public double getError() {
		return this.error;
	}

	/**
	 * @return The Jacobian matrix.
	 */
	public double[][] getJacobian() {
		return this.jacobian;
	}

	/**
	 * @return The errors for each row of the Jacobian.
	 */
	public double[] getRowErrors() {
		return this.rowErrors;
	}
}

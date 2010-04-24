/*
 * Encog(tm) Core v2.4
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

package org.encog.neural.networks.training.lma;

import java.util.List;

import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.propagation.gradient.GradientUtil;

/**
 * Calculate the Jacobian using the chain rule.
 */
public class JacobianChainRule implements ComputeJacobian {
	/**
	 * The network that is to be trained.
	 */
	private final BasicNetwork network;

	/**
	 * THe training set to use. Must be indexable.
	 */
	private final Indexable indexableTraining;

	/**
	 * The number of training set elements.
	 */
	private final int inputLength;

	/**
	 * The number of weights and thresholds in the neural network.
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
			final Indexable indexableTraining) {
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
		final double[] temp = new double[1];
		temp[0] = d;
		//a.activationFunction(temp);
		a.derivativeFunction(temp);
		return temp[0];
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
		a.activationFunction(temp);
		a.derivativeFunction(temp);
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

		final ActivationFunction function = this.network.getLayer(
				BasicNetwork.TAG_INPUT).getActivationFunction();

		final NeuralOutputHolder holder = new NeuralOutputHolder();
		
		this.network.compute(pair.getInput(), holder);

		final List<Synapse> synapses = this.network.getStructure()
				.getSynapses();

		int synapseNumber = 0;

		Synapse synapse = synapses.get(synapseNumber++);

		double output = holder.getOutput().getData(0);
		e = pair.getIdeal().getData(0) - output;

		this.jacobian[this.jacobianRow][this.jacobianCol++] = calcDerivative(
				function, output);

		for (int i = 0; i < synapse.getFromNeuronCount(); i++) {
			final double lastOutput = holder.getResult().get(synapse)
					.getData(i);

			this.jacobian[this.jacobianRow][this.jacobianCol++] = calcDerivative(
					function, output)
					* lastOutput;
		}
		
		Synapse lastSynapse;

		while (synapseNumber < synapses.size()) {
			lastSynapse = synapse;
			synapse = synapses.get(synapseNumber++);
			final NeuralData outputData = holder.getResult().get(lastSynapse);

			// for each neuron in the input layer
			for (int neuron = 0; neuron < synapse.getToNeuronCount(); neuron++) {
				output = outputData.getData(neuron);

				final int thresholdCol = this.jacobianCol++;

				// for each weight of the input neuron
				for (int i = 0; i < synapse.getFromNeuronCount(); i++) {
					sum = 0.0;
					// for each neuron in the next layer
					for (int j = 0; j < lastSynapse.getToNeuronCount(); j++) {
						// for each weight of the next neuron
						for (int k = 0; k < lastSynapse.getFromNeuronCount(); k++) {
							double x = lastSynapse.getMatrix().get(k, j);
							double y = output;
							sum += lastSynapse.getMatrix().get(k, j)
									* output;
						}
						sum += lastSynapse.getToLayer().getThreshold(j);
					}
					
					double x1 = calcDerivative(function, output);
					double x2 = calcDerivative2(function, sum);
					double x3 = holder.getResult().get(synapse).getData(i);

					final double w = lastSynapse.getMatrix().get(neuron, 0);
					final double val = calcDerivative(function, output)
							* calcDerivative2(function, sum) * w;
					
					double z1 = val
					* holder.getResult().get(synapse).getData(i);
					double z2 = val;

					this.jacobian[this.jacobianRow][this.jacobianCol++] = val
							* holder.getResult().get(synapse).getData(i);
					this.jacobian[this.jacobianRow][thresholdCol] = val;
				}
			}
		}

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

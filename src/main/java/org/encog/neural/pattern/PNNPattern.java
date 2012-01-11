/*
 * Encog(tm) Core v3.1 - Java Version
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
package org.encog.neural.pattern;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.MLMethod;
import org.encog.neural.pnn.BasicPNN;
import org.encog.neural.pnn.PNNKernelType;
import org.encog.neural.pnn.PNNOutputMode;

/**
 * Pattern to create a PNN.
 * 
 */
public class PNNPattern implements NeuralNetworkPattern {

	/**
	 * The kernel type.
	 */
	private PNNKernelType kernel = PNNKernelType.Gaussian;

	/**
	 * The output model.
	 */
	private PNNOutputMode outmodel = PNNOutputMode.Regression;

	/**
	 * The number of input neurons.
	 */
	private int inputNeurons;

	/**
	 * The number of output neurons.
	 */
	private int outputNeurons;

	/**
	 * Add a hidden layer. PNN networks do not have hidden layers, so this will
	 * throw an error.
	 * 
	 * @param count
	 *            The number of hidden neurons.
	 */
	@Override
	public final void addHiddenLayer(final int count) {
		throw new PatternError("A PNN network does not have hidden layers.");
	}

	/**
	 * Clear out any hidden neurons.
	 */
	@Override
	public void clear() {
	}

	/**
	 * Generate the RSOM network.
	 * 
	 * @return The neural network.
	 */
	@Override
	public final MLMethod generate() {
		final BasicPNN pnn = new BasicPNN(this.kernel, this.outmodel,
				this.inputNeurons, this.outputNeurons);
		return pnn;
	}

	/**
	 * @return The number of input neurons.
	 */
	public final int getInputNeurons() {
		return this.inputNeurons;
	}

	/**
	 * @return The kernel.
	 */
	public final PNNKernelType getKernel() {
		return this.kernel;
	}

	/**
	 * @return The output model.
	 */
	public final PNNOutputMode getOutmodel() {
		return this.outmodel;
	}

	/**
	 * @return The number of output neurons.
	 */
	public final int getOutputNeurons() {
		return this.outputNeurons;
	}

	/**
	 * Set the activation function. A PNN uses a linear activation function, so
	 * this method throws an error.
	 * 
	 * @param activation
	 *            The activation function to use.
	 */
	@Override
	public final void setActivationFunction(final ActivationFunction activation) {
		throw new PatternError(
				"A SOM network can't define an activation function.");

	}

	/**
	 * Set the input neuron count.
	 * 
	 * @param count
	 *            The number of neurons.
	 */
	@Override
	public final void setInputNeurons(final int count) {
		this.inputNeurons = count;

	}

	/**
	 * Set the kernel type.
	 * 
	 * @param kernel
	 *            The kernel type.
	 */
	public final void setKernel(final PNNKernelType kernel) {
		this.kernel = kernel;
	}

	/**
	 * Set the output model.
	 * @param outmodel The output model.
	 */
	public final void setOutmodel(final PNNOutputMode outmodel) {
		this.outmodel = outmodel;
	}

	/**
	 * Set the output neuron count.
	 * 
	 * @param count
	 *            The number of neurons.
	 */
	@Override
	public final void setOutputNeurons(final int count) {
		this.outputNeurons = count;
	}

}

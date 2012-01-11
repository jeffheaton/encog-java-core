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
import org.encog.mathutil.rbf.RBFEnum;
import org.encog.ml.MLMethod;
import org.encog.neural.rbf.RBFNetwork;

/**
 * A radial basis function (RBF) network uses several radial basis functions to
 * provide a more dynamic hidden layer activation function than many other types
 * of neural network. It consists of a input, output and hidden layer.
 * 
 * @author jheaton
 * 
 */
public class RadialBasisPattern implements NeuralNetworkPattern {

	private RBFEnum rbfType = RBFEnum.Gaussian;
	
	/**
	 * The number of input neurons to use. Must be set, default to invalid -1
	 * value.
	 */
	private int inputNeurons = -1;

	/**
	 * The number of hidden neurons to use. Must be set, default to invalid -1
	 * value.
	 */
	private int outputNeurons = -1;

	/**
	 * The number of hidden neurons to use. Must be set, default to invalid -1
	 * value.
	 */
	private int hiddenNeurons = -1;

	/**
	 * Add the hidden layer, this should be called once, as a RBF has a single
	 * hidden layer.
	 * 
	 * @param count
	 *            The number of neurons in the hidden layer.
	 */
	public final void addHiddenLayer(final int count) {
		if (this.hiddenNeurons != -1) {
			throw new PatternError("A RBF network usually has a single "
					+ "hidden layer.");

		} else {
			this.hiddenNeurons = count;
		}
	}

	/**
	 * Clear out any hidden neurons.
	 */
	public final void clear() {
		this.hiddenNeurons = -1;
	}

	/**
	 * Generate the RBF network.
	 * 
	 * @return The neural network.
	 */
	public final MLMethod generate() {

		RBFNetwork result = new RBFNetwork(inputNeurons, this.hiddenNeurons ,outputNeurons,this.rbfType);
		return result;
	}

	/**
	 * Set the activation function, this is an error. The activation function
	 * may not be set on a RBF layer.
	 * 
	 * @param activation
	 *            The new activation function.
	 */
	public final void setActivationFunction(final ActivationFunction activation) {
		throw new PatternError( "Can't set the activation function for "
				+ "a radial basis function network.");
	}

	/**
	 * Set the number of input neurons.
	 * 
	 * @param count
	 *            The number of input neurons.
	 */
	public final void setInputNeurons(final int count) {
		this.inputNeurons = count;
	}

	/**
	 * Set the number of output neurons.
	 * 
	 * @param count
	 *            The number of output neurons.
	 */
	public final void setOutputNeurons(final int count) {
		this.outputNeurons = count;
	}

	public final void setRBF(RBFEnum type) {
		this.rbfType = type;
		
	}
}

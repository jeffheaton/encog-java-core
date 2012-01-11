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
import org.encog.neural.art.ART1;

/**
 * Pattern to create an ART-1 neural network.
 */
public class ART1Pattern implements NeuralNetworkPattern {

	/**
	 * The number of input neurons.
	 */
	private int inputNeurons;

	/**
	 * The number of output neurons.
	 */
	private int outputNeurons;

	/**
	 * A parameter for F1 layer.
	 */
	private double a1 = 1;

	/**
	 * B parameter for F1 layer.
	 */
	private double b1 = 1.5;

	/**
	 * C parameter for F1 layer.
	 */
	private double c1 = 5;

	/**
	 * D parameter for F1 layer.
	 */
	private double d1 = 0.9;

	/**
	 * L parameter for net.
	 */
	private double l = 3;

	/**
	 * The vigilance parameter.
	 */
	private double vigilance = 0.9;

	/**
	 * This will fail, hidden layers are not supported for this type of network.
	 * 
	 * @param count
	 *            Not used.
	 */
	public final void addHiddenLayer(final int count) {
		throw new PatternError("A ART1 network has no hidden layers.");
	}

	/**
	 * Clear any properties set for this network.
	 */
	public final void clear() {
		this.inputNeurons = 0; 
		this.outputNeurons = 0;

	}

	/**
	 * Generate the neural network.
	 * 
	 * @return The generated neural network.
	 */
	public final MLMethod generate() {

		ART1 art = new ART1(this.inputNeurons, this.outputNeurons);
		art.setA1(this.a1);
		art.setB1(this.b1);
		art.setC1(this.c1);
		art.setD1(this.d1);
		art.setL(this.l);
		art.setVigilance(this.vigilance);
		return art;
	}

	/**
	 * @return The A1 parameter.
	 */
	public final double getA1() {
		return this.a1;
	}

	/**
	 * @return The B1 parameter.
	 */
	public final double getB1() {
		return this.b1;
	}

	/**
	 * @return The C1 parameter.
	 */
	public final double getC1() {
		return this.c1;
	}

	/**
	 * @return The D1 parameter.
	 */
	public final double getD1() {
		return this.d1;
	}

	/**
	 * @return The L parameter.
	 */
	public final double getL() {
		return this.l;
	}

	/**
	 * @return The vigilance for the network.
	 */
	public final double getVigilance() {
		return this.vigilance;
	}

	/**
	 * Set the A1 parameter.
	 * 
	 * @param a1
	 *            The new value.
	 */
	public final void setA1(final double a1) {
		this.a1 = a1;
	}

	/**
	 * This method will throw an error, you can't set the activation function
	 * for an ART1. type network.
	 * 
	 * @param activation
	 *            The activation function.
	 */
	public final void setActivationFunction(final ActivationFunction activation) {
		throw new PatternError("Can't set the activation function for an ART1.");
	}

	/**
	 * Set the B1 parameter.
	 * 
	 * @param b1
	 *            The new value.
	 */
	public final void setB1(final double b1) {
		this.b1 = b1;
	}

	/**
	 * Set the C1 parameter.
	 * 
	 * @param c1
	 *            The new value.
	 */
	public final void setC1(final double c1) {
		this.c1 = c1;
	}

	/**
	 * Set the D1 parameter.
	 * 
	 * @param d1
	 *            The new value.
	 */
	public final void setD1(final double d1) {
		this.d1 = d1;
	}

	/**
	 * Set the input neuron (F1 layer) count.
	 * 
	 * @param count
	 *            The input neuron count.
	 */
	public final void setInputNeurons(final int count) {
		this.inputNeurons = count;
	}

	/**
	 * Set the L parameter.
	 * 
	 * @param l
	 *            The new value.
	 */
	public final void setL(final double l) {
		this.l = l;
	}

	/**
	 * Set the output neuron (F2 layer) count.
	 * 
	 * @param count
	 *            The output neuron count.
	 */
	public final void setOutputNeurons(final int count) {
		this.outputNeurons = count;
	}

	/**
	 * Set the vigilance for the network.
	 * 
	 * @param vigilance
	 *            The new value.
	 */
	public final void setVigilance(final double vigilance) {
		this.vigilance = vigilance;
	}
}

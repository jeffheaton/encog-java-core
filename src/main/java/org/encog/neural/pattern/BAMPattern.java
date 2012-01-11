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
import org.encog.neural.bam.BAM;

/**
 * Construct a Bidirectional Access Memory (BAM) neural network. This neural
 * network type learns to associate one pattern with another. The two patterns
 * do not need to be of the same length. This network has two that are connected
 * to each other. Though they are labeled as input and output layers to Encog,
 * they are both equal, and should simply be thought of as the two layers that
 * make up the net.
 * 
 */
public class BAMPattern implements NeuralNetworkPattern {

	/**
	 * The number of neurons in the first layer.
	 */
	private int f1Neurons;

	/**
	 * The number of neurons in the second layer.
	 */
	private int f2Neurons;

	/**
	 * Unused, a BAM has no hidden layers.
	 * 
	 * @param count
	 *            Not used.
	 */
	public final void addHiddenLayer(final int count) {
		throw new PatternError("A BAM network has no hidden layers.");
	}

	/**
	 * Clear any settings on the pattern.
	 */
	public final void clear() {
		this.f1Neurons = 0;
		this.f2Neurons = 0;

	}

	/**
	 * @return The generated network.
	 */
	public final MLMethod generate() {
		BAM bam = new BAM(this.f1Neurons,this.f2Neurons);
		return bam;
	}

	/**
	 * Not used, the BAM uses a bipoloar activation function.
	 * 
	 * @param activation
	 *            Not used.
	 */
	public final void setActivationFunction(final ActivationFunction activation) {
		throw new PatternError("A BAM network can't specify a custom activation function.");
	}

	/**
	 * Set the F1 neurons. The BAM really does not have an input and output
	 * layer, so this is simply setting the number of neurons that are in the
	 * first layer.
	 * 
	 * @param count
	 *            The number of neurons in the first layer.
	 */
	public final void setF1Neurons(final int count) {
		this.f1Neurons = count;
	}

	/**
	 * Set the output neurons. The BAM really does not have an input and output
	 * layer, so this is simply setting the number of neurons that are in the
	 * second layer.
	 * 
	 * @param count
	 *            The number of neurons in the second layer.
	 */
	public final void setF2Neurons(final int count) {
		this.f2Neurons = count;
	}

	/**
	 * Set the number of input neurons.
	 * 
	 * @param count
	 *            The number of input neurons.
	 */
	public final void setInputNeurons(final int count) {
		throw new PatternError( "A BAM network has no input layer, consider setting F1 layer.");
	}

	/**
	 * Set the number of output neurons.
	 * 
	 * @param count
	 *            The output neuron count.
	 */
	public final void setOutputNeurons(final int count) {
		throw new PatternError("A BAM network has no output layer, consider setting F2 layer.");
	}

}

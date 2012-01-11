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

/**
 * Patterns are used to create common sorts of neural networks. Information
 * about the structure of the neural network is communicated to the pattern, and
 * then generate is called to produce a neural network of this type.
 * 
 * @author jheaton
 * 
 */
public interface NeuralNetworkPattern {

	/**
	 * Add the specified hidden layer.
	 * 
	 * @param count
	 *            The number of neurons in the hidden layer.
	 */
	void addHiddenLayer(int count);

	/**
	 * Clear the hidden layers so that they can be redefined.
	 */
	void clear();

	/**
	 * Generate the specified neural network.
	 * 
	 * @return The resulting neural network.
	 */
	MLMethod generate();

	/**
	 * Set the activation function to be used for all created layers that allow
	 * an activation function to be specified. Not all patterns allow the
	 * activation function to be specified.
	 * 
	 * @param activation
	 *            The activation function.
	 */
	void setActivationFunction(ActivationFunction activation);

	/**
	 * Set the number of input neurons.
	 * 
	 * @param count
	 *            The number of input neurons.
	 */
	void setInputNeurons(int count);

	/**
	 * Set the number of output neurons.
	 * 
	 * @param count
	 *            The output neuron count.
	 */
	void setOutputNeurons(int count);
}

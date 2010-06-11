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

package org.encog.neural.pattern;

import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.networks.BasicNetwork;

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
	BasicNetwork generate();

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

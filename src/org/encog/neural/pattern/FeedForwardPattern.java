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

import java.util.ArrayList;
import java.util.List;

import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to create feedforward neural networks. A feedforward network has an
 * input and output layers separated by zero or more hidden layers. The
 * feedforward neural network is one of the most common neural network patterns.
 * 
 * @author jheaton
 * 
 */
public class FeedForwardPattern implements NeuralNetworkPattern {
	/**
	 * The number of input neurons.
	 */
	private int inputNeurons;

	/**
	 * The number of output neurons.
	 */
	private int outputNeurons;

	/**
	 * The activation function.
	 */
	private ActivationFunction activation;

	/**
	 * The number of hidden neurons.
	 */
	private final List<Integer> hidden = new ArrayList<Integer>();

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Add a hidden layer, with the specified number of neurons.
	 * 
	 * @param count
	 *            The number of neurons to add.
	 */
	public void addHiddenLayer(final int count) {
		this.hidden.add(count);
	}

	/**
	 * Clear out any hidden neurons.
	 */
	public void clear() {
		this.hidden.clear();
	}

	/**
	 * Generate the Elman neural network.
	 * 
	 * @return The Elman neural network.
	 */
	public BasicNetwork generate() {
		int y = PatternConst.START_Y;
		final Layer input = new BasicLayer(this.activation, false,
				this.inputNeurons);

		final BasicNetwork result = new BasicNetwork();
		result.addLayer(input);

		input.setX(PatternConst.START_X);
		input.setY(y);
		y += PatternConst.INC_Y;

		for (final Integer count : this.hidden) {

			final Layer hidden = new BasicLayer(this.activation, true, count);

			result.addLayer(hidden);
			hidden.setX(PatternConst.START_X);
			hidden.setY(y);
			y += PatternConst.INC_Y;
		}

		final Layer output = new BasicLayer(this.activation, true,
				this.outputNeurons);
		result.addLayer(output);
		output.setX(PatternConst.START_X);
		output.setY(y);
		y += PatternConst.INC_Y;

		result.getStructure().finalizeStructure();
		result.reset();

		return result;
	}

	/**
	 * Set the activation function to use on each of the layers.
	 * 
	 * @param activation
	 *            The activation function.
	 */
	public void setActivationFunction(final ActivationFunction activation) {
		this.activation = activation;
	}

	/**
	 * Set the number of input neurons.
	 * 
	 * @param count
	 *            Neuron count.
	 */
	public void setInputNeurons(final int count) {
		this.inputNeurons = count;
	}

	/**
	 * Set the number of output neurons.
	 * 
	 * @param count
	 *            Neuron count.
	 */
	public void setOutputNeurons(final int count) {
		this.outputNeurons = count;
	}

}

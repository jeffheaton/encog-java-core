/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
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
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.ContextLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.SynapseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to generate an Elman style recurrent neural network. This
 * network type consists of three regular layers, an input output and hidden
 * layer. There is also a context layer which accepts output from the hidden
 * layer and outputs back to the hidden layer. This makes it a recurrent neural
 * network.
 * 
 * The Elman neural network is useful for temporal input data. The specified
 * activation function will be used on all layers. The Elman neural network is
 * similar to the Jordan neural network.
 * 
 * @author jheaton
 * 
 */
public class ElmanPattern implements NeuralNetworkPattern {

	/**
	 * The number of input neurons.
	 */
	private int inputNeurons;

	/**
	 * The number of output neurons.
	 */
	private int outputNeurons;

	/**
	 * The number of hidden neurons.
	 */
	private int hiddenNeurons;

	/**
	 * The activation function.
	 */
	private ActivationFunction activation;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Create an object to generate Elman neural networks.
	 */
	public ElmanPattern() {
		this.inputNeurons = -1;
		this.outputNeurons = -1;
		this.hiddenNeurons = -1;
	}

	/**
	 * Add a hidden layer with the specified number of neurons.
	 * 
	 * @param count
	 *            The number of neurons in this hidden layer.
	 */
	public void addHiddenLayer(final int count) {
		if (this.hiddenNeurons != -1) {
			final String str = "An Elman neural network should have only one hidden layer.";
			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}
			throw new PatternError(str);
		}

		this.hiddenNeurons = count;

	}

	/**
	 * Clear out any hidden neurons.
	 */
	public void clear() {
		this.hiddenNeurons = 0;
	}

	/**
	 * Generate the Elman neural network.
	 * 
	 * @return The Elman neural network.
	 */
	public BasicNetwork generate() {
		int y = PatternConst.START_Y;
		final Layer context = new ContextLayer(this.hiddenNeurons);
		final BasicNetwork network = new BasicNetwork();

		final Layer input = new BasicLayer(this.activation, true,
				this.inputNeurons);
		network.addLayer(input);
		input.setX(PatternConst.START_X);
		input.setY(y);
		y += PatternConst.INC_Y;

		final Layer hidden = new BasicLayer(this.activation, true,
				this.hiddenNeurons);
		network.addLayer(hidden);
		hidden.setX(PatternConst.START_X);
		hidden.setY(y);
		context.setX(PatternConst.INDENT_X);
		context.setY(y);
		y += PatternConst.INC_Y;
		hidden.addNext(context, SynapseType.OneToOne);
		context.addNext(hidden);
		final Layer output = new BasicLayer(this.activation, true,
				this.outputNeurons);
		network.addLayer(output);
		output.setX(PatternConst.START_X);
		output.setY(y);
		network.getStructure().finalizeStructure();
		network.reset();
		return network;
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

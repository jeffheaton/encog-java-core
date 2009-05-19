/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 * This class is used to generate an Jordan style recurrent neural network. This
 * network type consists of three regular layers, an input output and hidden
 * layer. There is also a context layer which accepts output from the output
 * layer and outputs back to the hidden layer. This makes it a recurrent neural
 * network.
 * 
 * The Jordan neural network is useful for temporal input data. The specified
 * activation function will be used on all layers.  The Jordan neural network
 * is similar to the Elman neural network.
 * 
 * @author jheaton
 * 
 */
public class JordanPattern implements NeuralNetworkPattern {

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
	 * Construct an object to create a Jordan type neural network.
	 */
	public JordanPattern() {
		this.inputNeurons = -1;
		this.outputNeurons = -1;
		this.hiddenNeurons = -1;
	}

	/**
	 * Add a hidden layer, there should be only one.
	 * 
	 * @param count
	 *            The number of neurons in this hidden layer.
	 */
	public void addHiddenLayer(final int count) {
		if (this.hiddenNeurons != -1) {
			final String str = 
				"A Jordan neural network should have only one hidden "
					+ "layer.";
			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}
			throw new PatternError(str);
		}

		this.hiddenNeurons = count;

	}

	/**
	 * Generate a Jordan neural network.
	 * 
	 * @return A Jordan neural network.
	 */
	public BasicNetwork generate() {
		// construct an Jordan type network
		final Layer input = new BasicLayer(this.activation, true,
				this.inputNeurons);
		final Layer hidden = new BasicLayer(this.activation, true,
				this.hiddenNeurons);
		final Layer output = new BasicLayer(this.activation, true,
				this.outputNeurons);
		final Layer context = new ContextLayer(this.outputNeurons);
		final BasicNetwork network = new BasicNetwork();
		network.addLayer(input);
		network.addLayer(hidden);
		network.addLayer(output);

		output.addNext(context, SynapseType.OneToOne);
		context.addNext(hidden);

		int y = PatternConst.START_Y;
		input.setX(PatternConst.START_X);
		input.setY(y);
		y += PatternConst.INC_Y;
		hidden.setX(PatternConst.START_X);
		hidden.setY(y);
		context.setX(PatternConst.INDENT_X);
		context.setY(y);
		y += PatternConst.INC_Y;
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
	
	/**
	 * Clear out any hidden neurons.
	 */
	public void clear() {
		this.hiddenNeurons = 0;		
	}

}

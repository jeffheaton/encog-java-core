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
import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.layers.RadialBasisFunctionLayer;
import org.encog.neural.networks.synapse.SynapseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A radial basis function (RBF) network uses several radial basis functions to
 * provide a more dynamic hidden layer activation function than many other types
 * of neural network. It consists of a input, output and hidden layer.
 * 
 * @author jheaton
 * 
 */
public class RadialBasisPattern implements NeuralNetworkPattern {
	public static final String RBF_LAYER = "RBF";

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
	public void addHiddenLayer(final int count) {
		if (this.hiddenNeurons != -1) {
			final String str = "A RBF network usually has a single "
					+ "hidden layer.";
			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}
			throw new PatternError(str);
		} else {
			this.hiddenNeurons = count;
		}
	}

	/**
	 * Clear out any hidden neurons.
	 */
	public void clear() {
		this.hiddenNeurons = -1;
	}

	/**
	 * Generate the RBF network.
	 * 
	 * @return The neural network.
	 */
	public BasicNetwork generate() {

		final Layer input = new BasicLayer(new ActivationLinear(), false,
				this.inputNeurons);
		final Layer output = new BasicLayer(new ActivationLinear(), true, this.outputNeurons);
		final BasicNetwork network = new BasicNetwork();
		final RadialBasisFunctionLayer rbfLayer = new RadialBasisFunctionLayer(
				this.hiddenNeurons);
		network.addLayer(input);
		network.addLayer(rbfLayer, SynapseType.Direct);
		network.addLayer(output);
		network.getStructure().finalizeStructure();
		network.reset();
		network.tagLayer(RBF_LAYER, rbfLayer);
		rbfLayer.randomizeGaussianCentersAndWidths(0, 1);
		int y = PatternConst.START_Y;
		input.setX(PatternConst.START_X);
		input.setY(y);
		y += PatternConst.INC_Y;
		rbfLayer.setX(PatternConst.START_X);
		rbfLayer.setY(y);
		y += PatternConst.INC_Y;
		output.setX(PatternConst.START_X);
		output.setY(y);
		return network;
	}

	/**
	 * Set the activation function, this is an error. The activation function
	 * may not be set on a RBF layer.
	 * 
	 * @param activation
	 *            The new activation function.
	 */
	public void setActivationFunction(final ActivationFunction activation) {
		final String str = "Can't set the activation function for "
				+ "a radial basis function network.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);
	}

	/**
	 * Set the number of input neurons.
	 * 
	 * @param count
	 *            The number of input neurons.
	 */
	public void setInputNeurons(final int count) {
		this.inputNeurons = count;
	}

	/**
	 * Set the number of output neurons.
	 * 
	 * @param count
	 *            The number of output neurons.
	 */
	public void setOutputNeurons(final int count) {
		this.outputNeurons = count;
	}
}

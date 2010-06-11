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

import org.encog.neural.activation.ActivationBiPolar;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.logic.HopfieldLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create a Hopfield pattern. A Hopfield neural network has a single layer that
 * functions both as the input and output layers. There are no hidden layers.
 * Hopfield networks are used for basic pattern recognition. When a Hopfield
 * network recognizes a pattern, it "echos" that pattern on the output.
 * 
 * @author jheaton
 * 
 */
public class HopfieldPattern implements NeuralNetworkPattern {

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * How many neurons in the Hopfield network. Default to -1, which is
	 * invalid. Therefore this value must be set.
	 */
	private int neuronCount = -1;

	/**
	 * Add a hidden layer. This will throw an error, because the Hopfield neural
	 * network has no hidden layers.
	 * 
	 * @param count
	 *            The number of neurons.
	 */
	public void addHiddenLayer(final int count) {
		final String str = "A Hopfield network has no hidden layers.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);

	}

	/**
	 * Nothing to clear.
	 */
	public void clear() {
	}

	/**
	 * Generate the Hopfield neural network.
	 * 
	 * @return The generated network.
	 */
	public BasicNetwork generate() {
		final Layer layer = new BasicLayer(new ActivationBiPolar(), false,
				this.neuronCount);

		final BasicNetwork result = new BasicNetwork(new HopfieldLogic());
		result.addLayer(layer);
		layer.addNext(layer);
		layer.setX(PatternConst.START_X);
		layer.setY(PatternConst.START_Y);
		result.getStructure().finalizeStructure();
		result.reset();
		return result;
	}

	/**
	 * Set the activation function to use. This function will throw an error,
	 * because the Hopfield network must use the BiPolar activation function.
	 * 
	 * @param activation
	 *            The activation function to use.
	 */
	public void setActivationFunction(final ActivationFunction activation) {
		final String str = "A Hopfield network will use the BiPolar activation "
				+ "function, no activation function needs to be specified.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);

	}

	/**
	 * Set the number of input neurons, this must match the output neurons.
	 * 
	 * @param count
	 *            The number of neurons.
	 */
	public void setInputNeurons(final int count) {
		this.neuronCount = count;

	}

	/**
	 * Set the number of output neurons, should not be used with a hopfield
	 * neural network, because the number of input neurons defines the number of
	 * output neurons.
	 * 
	 * @param count
	 *            The number of neurons.
	 */
	public void setOutputNeurons(final int count) {
		final String str = "A Hopfield network has a single layer, so no need "
				+ "to specify the output count.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);

	}

}

/*
 * Encog(tm) Core v2.6 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

import org.encog.engine.network.activation.ActivationBiPolar;
import org.encog.engine.network.activation.ActivationFunction;
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

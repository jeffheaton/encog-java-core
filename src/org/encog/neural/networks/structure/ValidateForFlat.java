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
package org.encog.neural.networks.structure;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.activation.ActivationTANH;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;

/**
 * Only certain types of networks can be converted to a flat network. This class
 * validates this. Specifically the network must be:
 * 
 * 1. Feedforward only, no self-connections or recurrent links 2. Sigmoid, TANH
 * or linear activation only 3. Must have bias weight values
 */
public final class ValidateForFlat {

	/**
	 * Determine if the specified neural network can be flat. If it can a null
	 * is returned, otherwise, an error is returned to show why the network
	 * cannot be flattened.
	 * 
	 * @param network
	 *            The network to check.
	 * @return Null, if the net can not be flattened, an error message
	 *         otherwise.
	 */
	public static String canBeFlat(final BasicNetwork network) {
		final Layer inputLayer = network.getLayer(BasicNetwork.TAG_INPUT);
		final Layer outputLayer = network.getLayer(BasicNetwork.TAG_INPUT);

		if (inputLayer == null) {
			return "To convert to a flat network, there must be an input layer.";
		}

		if (outputLayer == null) {
			return "To convert to a flat network, there must be an output layer.";
		}

		if (network.getStructure().isRecurrent()) {
			return "To convert to a flat network there cannot be context layers.";
		}

		for (final Layer layer : network.getStructure().getLayers()) {
			// only feedforward
			if (layer.getNext().size() > 1) {
				return "To convert to flat a network must be feedforward only.";
			}

			if (!(layer.getActivationFunction() instanceof ActivationSigmoid)
					&& !(layer.getActivationFunction() instanceof ActivationTANH) 
					&& !(layer.getActivationFunction() instanceof ActivationLinear)) {
				return "To convert to flat a network must only use sigmoid, linear or tanh activation.";
			}

			if (!layer.hasBias() && (layer != inputLayer)) {
				return "To convert to flat, all non-input layers must have bias weight values.";
			}
		}
		return null;
	}

	/**
	 * Validate the specified network.
	 * 
	 * @param network
	 *            The network to validate.
	 */
	public static void validateNetwork(final BasicNetwork network) {
		final String str = ValidateForFlat.canBeFlat(network);
		if (str != null) {
			throw new NeuralNetworkError(str);
		}
	}

	/**
	 * Private constructor.
	 */
	private ValidateForFlat() {

	}

}

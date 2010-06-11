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

package org.encog.neural.networks.logic;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the neural logic for an Simple Recurrent Network (SRN) type network.
 * This class is used for the Elman and Jordan networks. This class will work
 * just fine for a feedforward neural network, however it is not efficient.
 */
public class SimpleRecurrentLogic extends FeedforwardLogic {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = -7477229575064477961L;

	/**
	 * The logging object.
	 */
	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(SimpleRecurrentLogic.class);

	/**
	 * Handle recurrent layers. See if there are any recurrent layers before the
	 * specified layer that must affect the input.
	 * 
	 * @param layer
	 *            The layer being processed, see if there are any recurrent
	 *            connections to this.
	 * @param input
	 *            The input to the layer, will be modified with the result from
	 *            any recurrent layers.
	 * @param source
	 *            The source synapse.
	 */
	@Override
	public void preprocessLayer(final Layer layer, final NeuralData input,
			final Synapse source) {
		for (final Synapse synapse : getNetwork().getStructure()
				.getPreviousSynapses(layer)) {
			if (synapse != source) {
				if (SimpleRecurrentLogic.LOGGER.isDebugEnabled()) {
					SimpleRecurrentLogic.LOGGER.debug(
							"Recurrent layer from: {}", input);
				}
				final NeuralData recurrentInput = synapse.getFromLayer()
						.recur();

				if (recurrentInput != null) {
					final NeuralData recurrentOutput = synapse
							.compute(recurrentInput);

					for (int i = 0; i < input.size(); i++) {
						input.setData(i, input.getData(i)
								+ recurrentOutput.getData(i));
					}

					if (SimpleRecurrentLogic.LOGGER.isDebugEnabled()) {
						SimpleRecurrentLogic.LOGGER.debug(
								"Recurrent layer to: {}", input);
					}
				}
			}
		}
	}
}

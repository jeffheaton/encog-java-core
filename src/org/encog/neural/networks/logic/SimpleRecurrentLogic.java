/*
 * Encog(tm) Core v2.5 - Java Version
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

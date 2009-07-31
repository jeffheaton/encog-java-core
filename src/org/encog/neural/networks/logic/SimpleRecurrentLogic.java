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
package org.encog.neural.networks.logic;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the neural logic for an Simple Recurrent Network (SRN) type network.  
 * This class is used for the Elman and Jordan networks.
 */
public class SimpleRecurrentLogic extends FeedforwardLogic {

	/**
	 * The serial ID. 
	 */
	private static final long serialVersionUID = -7477229575064477961L;
	
	
	/**
	 * The logging object.
	 */
	private transient static final Logger logger = LoggerFactory.getLogger(SimpleRecurrentLogic.class);
		
	/**
	 * Handle recurrent layers.  See if there are any recurrent layers before
	 * the specified layer that must affect the input.
	 * @param layer The layer being processed, see if there are any recurrent
	 * connections to this.
	 * @param input The input to the layer, will be modified with the result
	 * from any recurrent layers.
	 * @param source The source synapse.
	 */
	public void preprocessLayer(final Layer layer,
			final NeuralData input, final Synapse source) {
		for (final Synapse synapse 
				: this.getNetwork().getStructure().getPreviousSynapses(layer)) {
			if (synapse != source) {
				if (SimpleRecurrentLogic.logger.isDebugEnabled()) {
					SimpleRecurrentLogic.logger.debug("Recurrent layer from: {}", input);
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

					if (SimpleRecurrentLogic.logger.isDebugEnabled()) {
						SimpleRecurrentLogic.logger.debug("Recurrent layer to: {}", input);
					}
				}
			}
		}
	}
}

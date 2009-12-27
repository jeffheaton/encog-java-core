/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the neural logic for an Feedforward type network. See
 * FeedforwardPattern for more information on this type of network.
 */
public class FeedforwardLogic implements NeuralLogic {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1779691422598188487L;

	/**
	 * The logging object.
	 */
	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(FeedforwardLogic.class);

	/**
	 * The network to use.
	 */
	private BasicNetwork network;

	/**
	 * Compute the output for a given input to the neural network. This method
	 * provides a parameter to specify an output holder to use. This holder
	 * allows propagation training to track the output from each layer. If you
	 * do not need this holder pass null, or use the other compare method.
	 * 
	 * @param input
	 *            The input provide to the neural network.
	 * @param useHolder
	 *            Allows a holder to be specified, this allows propagation
	 *            training to check the output of each layer.
	 * @return The results from the output neurons.
	 */
	public NeuralData compute(final NeuralData input,
			final NeuralOutputHolder useHolder) {
		NeuralOutputHolder holder;

		final Layer inputLayer = this.network.getLayer(BasicNetwork.TAG_INPUT);

		if (FeedforwardLogic.LOGGER.isDebugEnabled()) {
			FeedforwardLogic.LOGGER.debug(
					"Pattern {} presented to neural network", input);
		}

		if (useHolder == null) {
			holder = new NeuralOutputHolder();
		} else {
			holder = useHolder;
		}

		getNetwork().checkInputSize(input);
		compute(holder, inputLayer, input, null);
		return holder.getOutput();
	}

	/**
	 * Internal computation method for a single layer. This is called, as the
	 * neural network processes.
	 * 
	 * @param holder
	 *            The output holder.
	 * @param layer
	 *            The layer to process.
	 * @param input
	 *            The input to this layer.
	 * @param source
	 *            The source synapse.
	 */
	private void compute(final NeuralOutputHolder holder, final Layer layer,
			final NeuralData input, final Synapse source) {

		if (FeedforwardLogic.LOGGER.isDebugEnabled()) {
			FeedforwardLogic.LOGGER.debug("Processing layer: {}, input= {}",
					layer, input);
		}

		// typically used to process any recurrent layers that feed into this
		// layer.
		preprocessLayer(layer, input, source);

		for (final Synapse synapse : layer.getNext()) {
			if (!holder.getResult().containsKey(synapse)) {
				if (FeedforwardLogic.LOGGER.isDebugEnabled()) {
					FeedforwardLogic.LOGGER.debug("Processing synapse: {}",
							synapse);
				}
				NeuralData pattern = synapse.compute(input);
				pattern = synapse.getToLayer().compute(pattern);
				synapse.getToLayer().process(pattern);
				holder.getResult().put(synapse, input);
				compute(holder, synapse.getToLayer(), pattern, synapse);

				final Layer outputLayer = this.network
						.getLayer(BasicNetwork.TAG_OUTPUT);

				// Is this the output from the entire network?
				if (synapse.getToLayer() == outputLayer) {
					holder.setOutput(pattern);
				}
			}
		}
	}

	/**
	 * @return The network in use.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Setup the network logic, read parameters from the network.
	 * 
	 * @param network
	 *            The network that this logic class belongs to.
	 */
	public void init(final BasicNetwork network) {
		this.network = network;
	}

	/**
	 * Can be overridden by subclasses. Usually used to implement recurrent
	 * layers.
	 * 
	 * @param layer
	 *            The layer to process.
	 * @param input
	 *            The input to this layer.
	 * @param source
	 *            The source from this layer.
	 */
	public void preprocessLayer(final Layer layer, final NeuralData input,
			final Synapse source) {
		// nothing to do
	}
}

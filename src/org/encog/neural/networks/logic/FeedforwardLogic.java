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
package org.encog.neural.networks.logic;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
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
		
		if( useHolder==null && this.network.getStructure().getFlat()!=null ) {
			this.network.getStructure().updateFlatNetwork();
			NeuralData result = new BasicNeuralData(this.network.getStructure().getFlat().getOutputCount());
			this.network.getStructure().getFlat().compute(input.getData(), result.getData());
			return result;
		}
		
		this.network.getStructure().updateFlatNetwork();

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

		try {
			if (FeedforwardLogic.LOGGER.isDebugEnabled()) {
				FeedforwardLogic.LOGGER.debug(
						"Processing layer: {}, input= {}", layer, input);
			}
			
			this.network.getStructure().updateFlatNetwork();

			// typically used to process any recurrent layers that feed into
			// this layer.
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
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new NeuralNetworkError("Size mismatch on input of size " + input.size() + " and layer: ", ex);
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

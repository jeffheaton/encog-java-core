/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.neural.networks.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.engine.network.activation.ActivationLinear;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.flat.FlatLayer;
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;

/**
 * Holds "cached" information about the structure of the neural network. This is
 * a very good performance boost since the neural network does not need to
 * traverse itself each time a complete collection of layers or synapses is
 * needed.
 * 
 * @author jheaton
 * 
 */
public class NeuralStructure implements Serializable {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = -2929683885395737817L;

	/**
	 * The layers in this neural network.
	 */
	private final List<Layer> layers = new ArrayList<Layer>();

	/**
	 * The neural network this class belongs to.
	 */
	private final BasicNetwork network;

	/**
	 * The limit, below which a connection is treated as zero.
	 */
	private double connectionLimit;

	/**
	 * Are connections limited?
	 */
	private boolean connectionLimited;

	/**
	 * The flattened form of the network.
	 */
	private FlatNetwork flat;

	/**
	 * Construct a structure object for the specified network.
	 * 
	 * @param network
	 *            The network to construct a structure for.
	 */
	public NeuralStructure(final BasicNetwork network) {
		this.network = network;
	}

	/**
	 * Calculate the size that an array should be to hold all of the weights and
	 * bias values.
	 * 
	 * @return The size of the calculated array.
	 */
	public final int calculateSize() {
		return NetworkCODEC.networkSize(this.network);
	}

	/**
	 * Enforce that all connections are above the connection limit. Any
	 * connections below this limit will be severed.
	 */
	public final void enforceLimit() {
		if (!this.connectionLimited) {
			return;
		}

		final double[] weights = this.flat.getWeights();

		for (int i = 0; i < weights.length; i++) {
			if (Math.abs(weights[i]) < this.connectionLimit) {
				weights[i] = 0;
			}
		}
	}

	/**
	 * Parse/finalize the limit value for connections.
	 */
	public void finalizeLimit() {
		// see if there is a connection limit imposed
		final String limit = this.network
				.getPropertyString(BasicNetwork.TAG_LIMIT);
		if (limit != null) {
			try {
				this.connectionLimited = true;
				this.connectionLimit = Double.parseDouble(limit);
				enforceLimit();
			} catch (final NumberFormatException e) {
				throw new NeuralNetworkError("Invalid property("
						+ BasicNetwork.TAG_LIMIT + "):" + limit);
			}
		} else {
			this.connectionLimited = false;
			this.connectionLimit = 0;
		}
	}

	/**
	 * Build the synapse and layer structure. This method should be called after
	 * you are done adding layers to a network, or change the network's logic
	 * property.
	 */
	public final void finalizeStructure() {

		if (this.layers.size() < 2) {
			throw new NeuralNetworkError(
					"There must be at least two layers before the structure is finalized.");
		}

		final FlatLayer[] flatLayers = new FlatLayer[this.layers.size()];

		for (int i = 0; i < this.layers.size(); i++) {
			final BasicLayer layer = (BasicLayer) this.layers.get(i);
			if (layer.getActivation() == null) {
				layer.setActivation(new ActivationLinear());
			}

			flatLayers[i] = layer;
		}

		this.flat = new FlatNetwork(flatLayers);

		finalizeLimit();
		this.layers.clear();
		enforceLimit();
	}

	/**
	 * @return The connection limit.
	 */
	public final double getConnectionLimit() {
		return this.connectionLimit;
	}

	/**
	 * @return The flat network.
	 */
	public final FlatNetwork getFlat() {
		requireFlat();
		return this.flat;
	}

	/**
	 * @return The layers in this neural network.
	 */
	public final List<Layer> getLayers() {
		return this.layers;
	}

	/**
	 * @return The network this structure belongs to.
	 */
	public final BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * @return True if this is not a fully connected feedforward network.
	 */
	public final boolean isConnectionLimited() {
		return this.connectionLimited;
	}

	/**
	 * Throw an error if there is no flat network.
	 */
	public final void requireFlat() {
		if (this.flat == null) {
			throw new NeuralNetworkError(
					"Must call finalizeStructure before using this network.");
		}
	}

	/**
	 * Set the flat network.
	 * @param flat The flat network.
	 */
	public final void setFlat(final FlatNetwork flat) {
		this.flat = flat;
	}

	/**
	 * Update any properties from the property map.
	 */
	public final void updateProperties() {
		if (this.network.getProperties().containsKey(BasicNetwork.TAG_LIMIT)) {
			this.connectionLimit = this.network
					.getPropertyDouble(BasicNetwork.TAG_LIMIT);
			this.connectionLimited = true;
		} else {
			this.connectionLimited = false;
			this.connectionLimit = 0;
		}

		if (this.flat != null) {
			this.flat.setConnectionLimit(this.connectionLimit);
		}

	}

}

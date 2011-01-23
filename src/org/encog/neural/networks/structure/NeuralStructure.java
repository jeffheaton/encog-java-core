/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
import java.util.Map.Entry;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.flat.FlatLayer;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.util.obj.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * The logging object.
	 */
	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(NeuralStructure.class);

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
	 * The next ID to be assigned to a layer.
	 */
	private int nextID = 1;

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
	public int calculateSize() {
		return NetworkCODEC.networkSize(this.network);
	}

	/**
	 * Determine if the network contains a layer of the specified type.
	 * 
	 * @param type
	 *            The layer type we are looking for.
	 * @return True if this layer type is present.
	 */
	public boolean containsLayerType(final Class<?> type) {
		for (final Layer layer : this.layers) {
			if (ReflectionUtil.isInstanceOf(layer.getClass(), type)) {
				return true;
			}
		}

		return false;
	}


	/**
	 * Enforce that all connections are above the connection limit. Any
	 * connections below this limit will be severed.
	 */
	public void enforceLimit() {
		if (!this.connectionLimited) {
			return;
		}
		
		double[] weights = this.flat.getWeights();
		
		for(int i=0;i<weights.length;i++) {
			if (Math.abs(weights[i]) < this.connectionLimit) {
				weights[i] = 0;
			}
		}
	}

	/**
	 * Parse/finalize the limit value for connections.
	 */
	private void finalizeLimit() {
		// see if there is a connection limit imposed
		final String limit = this.network
				.getPropertyString(BasicNetwork.TAG_LIMIT);
		if (limit != null) {
			try {
				this.connectionLimited = true;
				this.connectionLimit = Double.parseDouble(limit);
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
	public void finalizeStructure() {
		
		if( this.layers.size()<2 ) {
			throw new NeuralNetworkError("There must be at least two layers before the structure is finalized.");
		}
		
		final double[] params = new double[1];
		FlatLayer[] flatLayers = new FlatLayer[this.layers.size()];
				
		params[0] = 1; // slope

		for(int i=0;i<this.layers.size();i++)
		{
			BasicLayer layer = (BasicLayer)this.layers.get(i);	
			ActivationFunction act = layer.getActivationFunction();
			if( act==null )
				act = new ActivationLinear();

			flatLayers[i] = new FlatLayer(act,layer.getNeuronCount(),layer.getBiasActivation());
		}
				
		this.flat = new FlatNetwork(flatLayers);
		
		finalizeLimit();
		this.layers.clear();
		enforceLimit();
	}

	/**
	 * @return The connection limit.
	 */
	public double getConnectionLimit() {
		return this.connectionLimit;
	}

	/**
	 * @return The flat network.
	 */
	public FlatNetwork getFlat() {
		requireFlat();
		return this.flat;
	}

	/**
	 * @return The layers in this neural network.
	 */
	public List<Layer> getLayers() {
		return this.layers;
	}

	/**
	 * @return The network this structure belongs to.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Get the next layer id.
	 * 
	 * @return The next layer id.
	 */
	public int getNextID() {
		return this.nextID++;
	}


	/**
	 * @return True if this is not a fully connected feedforward network.
	 */
	public boolean isConnectionLimited() {
		return this.connectionLimited;
	}

	public void requireFlat() {
		if( this.flat==null ) {
			throw new NeuralNetworkError("Must call finalizeStructure before using this network.");
		}		
	}

	public void updateProperties() {
		if( this.network.getProperties().containsKey(BasicNetwork.TAG_LIMIT) ) {			
			this.connectionLimit = this.network.getPropertyDouble(BasicNetwork.TAG_LIMIT);
			this.connectionLimited = true;
		} else {
			this.connectionLimited = false;
			this.connectionLimit = 0;
		}
		
		if( this.flat!=null ) {
			this.flat.setConnectionLimit(this.connectionLimit);
		}
		
	}

}

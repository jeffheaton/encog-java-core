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

package org.encog.neural.networks.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.encog.engine.network.flat.ActivationFunctions;
import org.encog.engine.network.flat.FlatLayer;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.network.flat.FlatNetworkRBF;
import org.encog.engine.util.EngineArray;
import org.encog.engine.util.ObjectPair;
import org.encog.mathutil.matrices.Matrix;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.ContextLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.layers.RadialBasisFunctionLayer;
import org.encog.neural.networks.logic.FeedforwardLogic;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.util.ReflectionUtil;
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
	 * The synapses in this neural network.
	 */
	private final List<Synapse> synapses = new ArrayList<Synapse>();

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
	private transient FlatNetwork flat;
	
	/**
	 * What type of update is needed to the flat network.
	 */
	private transient FlatUpdateNeeded flatUpdate;

	/**
	 * Construct a structure object for the specified network.
	 * 
	 * @param network
	 *            The network to construct a structure for.
	 */
	public NeuralStructure(final BasicNetwork network) {
		this.network = network;
		this.flatUpdate = FlatUpdateNeeded.None;
	}

	/**
	 * Assign an ID to every layer that does not already have one.
	 */
	public void assignID() {
		for (final Layer layer : this.layers) {
			assignID(layer);
		}
		sort();
	}

	/**
	 * Assign an ID to the specified layer.
	 * 
	 * @param layer
	 *            The layer to get an ID assigned.
	 */
	public void assignID(final Layer layer) {
		if (layer.getID() == -1) {
			layer.setID(getNextID());
		}
	}

	/**
	 * Calculate the size that an array should be to hold all of the weights and
	 * bias values.
	 * 
	 * @return The size of the calculated array.
	 */
	public int calculateSize() {
		return NetworkCODEC.networkSize(network);
	}

	/**
	 * Determine if the network contains a layer of the specified type.
	 * 
	 * @param type
	 *            The layer type we are looking for.
	 * @return True if this layer type is present.
	 */
	public boolean containsLayerType(final Class< ? > type) {
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

		for (final Synapse synapse : this.synapses) {
			final Matrix matrix = synapse.getMatrix();
			if (matrix != null) {
				for (int row = 0; row < matrix.getRows(); row++) {
					for (int col = 0; col < matrix.getCols(); col++) {
						final double value = matrix.get(row, col);
						if (Math.abs(value) < this.connectionLimit) {
							matrix.set(row, col, 0);
						}
					}
				}
			}
		}
	}

	/**
	 * Build the layer structure.
	 */
	private void finalizeLayers() {
		
		// no bias values on the input layer
		if( network.getLogic() instanceof FeedforwardLogic )
		{
			Layer inputLayer = this.network.getLayer(BasicNetwork.TAG_INPUT);
			inputLayer.setBiasWeights(null);
		}
		
		final List<Layer> result = new ArrayList<Layer>();
		this.layers.clear();

		for (final Layer layer : this.network.getLayerTags().values()) {
			getLayers(result, layer);
		}

		this.layers.addAll(result);

		// make sure that the current ID is not going to cause a repeat
		for (final Layer layer : this.layers) {
			if (layer.getID() >= this.nextID) {
				this.nextID = layer.getID() + 1;
			}
		}

		sort();
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
		finalizeLayers();
		finalizeSynapses();
		finalizeLimit();
		Collections.sort(this.layers);
		assignID();
		this.network.getLogic().init(this.network);
		enforceLimit();
		flatten();
	}

	/**
	 * Build the synapse structure.
	 */
	private void finalizeSynapses() {
		final Set<Synapse> result = new HashSet<Synapse>();
		for (final Layer layer : getLayers()) {
			for (final Synapse synapse : layer.getNext()) {
				result.add(synapse);
			}
		}
		this.synapses.clear();
		this.synapses.addAll(result);
	}

	/**
	 * Find the specified synapse, throw an error if it is required.
	 * 
	 * @param fromLayer
	 *            The from layer.
	 * @param toLayer
	 *            The to layer.
	 * @param required
	 *            Is this required?
	 * @return The synapse, if it exists, otherwise null.
	 */
	public Synapse findSynapse(final Layer fromLayer, final Layer toLayer,
			final boolean required) {
		Synapse result = null;
		for (final Synapse synapse : getSynapses()) {
			if ((synapse.getFromLayer() == fromLayer)
					&& (synapse.getToLayer() == toLayer)) {
				result = synapse;
				break;
			}
		}

		if (required && (result == null)) {
			final String str = 
				"This operation requires a network with a synapse between the "
					+ nameLayer(fromLayer)
					+ " layer to the "
					+ nameLayer(toLayer) + " layer.";
			if (NeuralStructure.LOGGER.isErrorEnabled()) {
				NeuralStructure.LOGGER.error(str);
			}
			throw new NeuralNetworkError(str);
		}

		return result;
	}

	/**
	 * @return The connection limit.
	 */
	public double getConnectionLimit() {
		return this.connectionLimit;
	}

	/**
	 * @return The layers in this neural network.
	 */
	public List<Layer> getLayers() {
		return this.layers;
	}

	/**
	 * Called to help build the layer structure.
	 * 
	 * @param result
	 *            The layer list.
	 * @param layer
	 *            The current layer being processed.
	 */
	private void getLayers(final List<Layer> result, final Layer layer) {

		if (!result.contains(layer)) {
			result.add(layer);
		}

		for (final Synapse synapse : layer.getNext()) {
			final Layer nextLayer = synapse.getToLayer();

			if (!result.contains(nextLayer)) {
				getLayers(result, nextLayer);
			}
		}
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
	 * Get the previous layers from the specified layer.
	 * 
	 * @param targetLayer
	 *            The target layer.
	 * @return The previous layers.
	 */
	public Collection<Layer> getPreviousLayers(final Layer targetLayer) {
		final Collection<Layer> result = new HashSet<Layer>();
		for (final Layer layer : this.getLayers()) {
			for (final Synapse synapse : layer.getNext()) {
				if (synapse.getToLayer() == targetLayer) {
					result.add(synapse.getFromLayer());
				}
			}
		}
		return result;
	}

	/**
	 * Get the previous synapses.
	 * 
	 * @param targetLayer
	 *            The layer to get the previous layers from.
	 * @return A collection of synapses.
	 */
	public List<Synapse> getPreviousSynapses(final Layer targetLayer) {

		final List<Synapse> result = new ArrayList<Synapse>();

		for (final Synapse synapse : this.synapses) {
			if (synapse.getToLayer() == targetLayer) {
				if (!result.contains(synapse)) {
					result.add(synapse);
				}
			}
		}

		return result;

	}

	/**
	 * @return All synapses in the neural network.
	 */
	public List<Synapse> getSynapses() {
		return this.synapses;
	}

	/**
	 * @return True if this is not a fully connected feedforward network.
	 */
	public boolean isConnectionLimited() {
		return this.connectionLimited;
	}

	/**
	 * Obtain a name for the specified layer.
	 * 
	 * @param layer
	 *            The layer to name.
	 * @return The name of this layer.
	 */
	public List<String> nameLayer(final Layer layer) {
		final List<String> result = new ArrayList<String>();

		for (final Entry<String, Layer> entry : this.network.getLayerTags()
				.entrySet()) {
			if (entry.getValue() == layer) {
				result.add(entry.getKey());
			}
		}

		return result;
	}

	/**
	 * Sort the layers and synapses.
	 */
	public void sort() {
		Collections.sort(this.layers, new LayerComparator(this));
		Collections.sort(this.synapses, new SynapseComparator(this));
	}

	/**
	 * @return Are there any context layers.
	 */
	public boolean isRecurrent() {
		for (Layer layer : this.getLayers()) {
			if (layer instanceof ContextLayer) {
				return true;
			}
		}
		return false;
	}
		
	public void unflattenWeights() {
		double[] sourceWeights = flat.getWeights();		
		NetworkCODEC.arrayToNetwork(sourceWeights, network);
		this.flatUpdate = FlatUpdateNeeded.None;
	}
	
	private int countNonContext()
	{
		int result = 0;
		
		for(Layer layer: this.getLayers() ) {
			if( layer.getClass()!=ContextLayer.class )
				result++;
		}
		
		return result;
	}
	
	public Synapse findPreviousSynapseByLayerType(Layer layer, Class<? extends Layer> type)
	{
		for( Synapse synapse : this.getPreviousSynapses(layer) ) {
			if( synapse.getFromLayer().getClass()==type )
				return synapse;
		}
		return null;
	}
	
	public Synapse findNextSynapseByLayerType(Layer layer, Class<? extends Layer> type)
	{
		for( Synapse synapse : layer.getNext() ) {
			if( synapse.getToLayer().getClass()==type )
				return synapse;
		}
		return null;
	}
	
	public void flatten() {
		boolean isRBF = false;
		Map<Layer, FlatLayer> regular2flat = new HashMap<Layer, FlatLayer>();
		List<ObjectPair<Layer, Layer>> contexts = new ArrayList<ObjectPair<Layer, Layer>>();
		this.flat = null;

		ValidateForFlat val = new ValidateForFlat();
		
		if (val.isValid(this.network) == null) {
			if( this.layers.size()==3 &&  this.layers.get(1) instanceof RadialBasisFunctionLayer )
			{
				RadialBasisFunctionLayer rbf = (RadialBasisFunctionLayer)this.layers.get(1);
				this.flat = new FlatNetworkRBF(
					this.network.getInputCount(),
					rbf.getNeuronCount(),
					this.network.getOutputCount(),
					rbf.getCenter(),
					rbf.getRadius());
				flattenWeights();
				this.flatUpdate = FlatUpdateNeeded.None;
				return;
			}
			
			
			FlatLayer[] flatLayers = new FlatLayer[countNonContext()];

			int index = flatLayers.length - 1;
			for (Layer layer : this.layers) {

				if (layer instanceof ContextLayer) {
					Synapse inboundSynapse = network.getStructure()
							.findPreviousSynapseByLayerType(layer,
									BasicLayer.class);
					Synapse outboundSynapse = network
							.getStructure()
							.findNextSynapseByLayerType(layer, BasicLayer.class);

					if (inboundSynapse == null)
						throw new NeuralNetworkError(
								"Context layer must be connected to by one BasicLayer.");

					if (outboundSynapse == null)
						throw new NeuralNetworkError(
								"Context layer must connect to by one BasicLayer.");

					Layer inbound = inboundSynapse.getFromLayer();
					Layer outbound = outboundSynapse.getToLayer();

					contexts.add(new ObjectPair<Layer, Layer>(inbound, outbound));
				} 
				else {
					double bias = this.findNextBias(layer);

					int activationType;
					double[] params = new double[1];
					
					if( layer.getActivationFunction()==null )
					{
						activationType = ActivationFunctions.ACTIVATION_LINEAR;
						params = new double[1];
						params[0] = 1;
					}
					else
					{
						activationType = layer.getActivationFunction().getEngineID();
						params = layer.getActivationFunction().getParams();
					}
					
					FlatLayer flatLayer = new FlatLayer(
							activationType,
							layer.getNeuronCount(), 
							bias,
							params);

					regular2flat.put(layer, flatLayer);
					flatLayers[index--] = flatLayer;
				}
			}
			
			// now link up the context layers
			for(ObjectPair<Layer, Layer> context: contexts)
			{
				Layer layer = context.getB();
				Synapse synapse = this.network.getStructure().findPreviousSynapseByLayerType(layer, BasicLayer.class);
				FlatLayer from = regular2flat.get(context.getA());
				FlatLayer to = regular2flat.get(synapse.getFromLayer());
				to.setContextFedBy(from);
			}

			this.flat = new FlatNetwork(flatLayers);
			
			if( isRBF ) {
				this.flat.setEndTraining(flatLayers.length-1);
			}
			
			flattenWeights();
			
			if( this.isConnectionLimited() ) {
				
			}
			
			this.flatUpdate = FlatUpdateNeeded.None;
		} else
			this.flatUpdate = FlatUpdateNeeded.Never;
	}
	
	public void flattenWeights() {
		if (this.flat != null) {
			this.flatUpdate = FlatUpdateNeeded.Flatten;
			
			double[] targetWeights = this.flat.getWeights();
			double[] sourceWeights = NetworkCODEC.networkToArray(this.network);

			EngineArray.arrayCopy(sourceWeights, targetWeights);
			this.flatUpdate = FlatUpdateNeeded.None;
			
			// handle limited connection networks
			if( this.connectionLimited ) {
				this.flat.setConnectionLimit(this.connectionLimit);
			} else {
				this.flat.clearConnectionLimit();
			}
		}
	}

	public FlatUpdateNeeded getFlatUpdate() {
		return flatUpdate;
	}
	
	public void setFlatUpdate(FlatUpdateNeeded flatUpdate) {
		this.flatUpdate = flatUpdate;
	}


	public FlatNetwork getFlat() {
		return flat;
	}

	public void updateFlatNetwork() {

		switch (this.flatUpdate) {

		case Flatten:
			flattenWeights();
			break;

		case Unflatten:
			unflattenWeights();
			break;

		case None:
		case Never:
			return;
		}

		this.flatUpdate = FlatUpdateNeeded.None;
	}
	
	private double findNextBias(Layer layer)
	{
		double bias = FlatNetwork.NO_BIAS_ACTIVATION;

		if (layer.getNext().size() > 0) {
			Synapse synapse = network.getStructure()
					.findNextSynapseByLayerType(layer,
							BasicLayer.class);
			if (synapse != null) {
				Layer nextLayer = synapse.getToLayer();
				if( nextLayer.hasBias() )
					bias = nextLayer.getBiasActivation();
			}
		}
		return bias;
	}	

	private double[] constructRBFParams(RadialBasisFunctionLayer layer)
	{
		double[] result = new double[layer.getNeuronCount()+(layer.getNeuronCount()*layer.getDimensions())];
		
		int index = 0;
		
		// copy radius
		for(int i=0;i<layer.getRadius().length; i++) {
			result[index++] = layer.getRadius()[i];
		}
		
		// copy centers
		for(int i=0;i<layer.getCenter().length;i++)
		{
			for(int j=0;j<layer.getCenter()[i].length;j++)
			{
				result[index++] = layer.getCenter()[i][j];
			}
		}
		
		
		return result;
	}
}

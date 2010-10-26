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

package org.encog.neural.networks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.Encog;
import org.encog.engine.util.EngineArray;
import org.encog.engine.util.ErrorCalculation;
import org.encog.mathutil.randomize.NguyenWidrowRandomizer;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.logic.NeuralLogic;
import org.encog.neural.networks.logic.SimpleRecurrentLogic;
import org.encog.neural.networks.structure.FlatUpdateNeeded;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.structure.NeuralStructure;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.persist.BasicPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.BasicNetworkPersistor;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;
import org.encog.util.obj.ObjectCloner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a neural network. This class works in conjunction the
 * Layer classes. Layers are added to the BasicNetwork to specify the structure
 * of the neural network.
 * 
 * The first layer added is the input layer, the final layer added is the output
 * layer. Any layers added between these two layers are the hidden layers.
 * 
 * The network structure is stored in the structure member. It is important to
 * call:
 * 
 * network.getStructure().finalizeStructure();
 * 
 * Once the neural network has been completely constructed.
 * 
 */
public class BasicNetwork extends BasicPersistedObject implements Serializable,
		Network, ContextClearable {

	/**
	 * Tag used for the input layer.
	 */
	public static final String TAG_INPUT = "INPUT";

	/**
	 * Tag used for the output layer.
	 */
	public static final String TAG_OUTPUT = "OUTPUT";

	/**
	 * Tag used for the connection limit.
	 */
	public static final String TAG_LIMIT = "CONNECTION_LIMIT";

	public static final String DEFAULT_CONNECTION_LIMIT = "0.0000000001";

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = -136440631687066461L;

	/**
	 * The logging object.
	 */
	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(BasicNetwork.class);

	/**
	 * Determine which member of the output is the winning neuron.
	 * 
	 * @param output
	 *            The output from the neural network.
	 * @return The winning neuron.
	 */
	public static int determineWinner(final NeuralData output) {

		int win = 0;

		double biggest = Double.MIN_VALUE;
		for (int i = 0; i < output.size(); i++) {

			if (output.getData(i) > biggest) {
				biggest = output.getData(i);
				win = i;
			}
		}

		return win;
	}

	/**
	 * Holds the structure of the network. This keeps the network from having to
	 * constantly lookup layers and synapses.
	 */
	private final NeuralStructure structure;

	/**
	 * This class tells the network how to calculate the output for each of the
	 * layers.
	 */
	private NeuralLogic logic;

	/**
	 * Properties about the neural network. Some NeuralLogic classes require
	 * certain properties to be set.
	 */
	private final Map<String, String> properties = new HashMap<String, String>();

	/**
	 * The tags for the layers.
	 */
	private final Map<String, Layer> layerTags = new HashMap<String, Layer>();

	/**
	 * Construct an empty neural network.
	 */
	public BasicNetwork() {
		this.structure = new NeuralStructure(this);
		this.logic = new SimpleRecurrentLogic();
	}

	/**
	 * Construct a basic network using the specified logic.
	 * 
	 * @param logic
	 *            The logic to use with the neural network.
	 */
	public BasicNetwork(final NeuralLogic logic) {
		this.structure = new NeuralStructure(this);
		this.logic = logic;
	}

	/**
	 * Add a layer to the neural network. The first layer added is the input
	 * layer, the last layer added is the output layer. This layer is added with
	 * a weighted synapse.
	 * 
	 * @param layer
	 *            The layer to be added.
	 */
	public void addLayer(final Layer layer) {
		layer.setNetwork(this);
		this.structure.assignID(layer);
		addLayer(layer, SynapseType.Weighted);
	}

	/**
	 * Add a layer to the neural network. If there are no layers added this
	 * layer will become the input layer. This function automatically updates
	 * both the input and output layer references.
	 * 
	 * @param layer
	 *            The layer to be added to the network.
	 * @param type
	 *            What sort of synapse should connect this layer to the last.
	 */
	public void addLayer(final Layer layer, final SynapseType type) {

		// is this the first layer? If so, mark as the input layer.
		if (this.layerTags.size() == 0) {
			tagLayer(BasicNetwork.TAG_INPUT, layer);
			tagLayer(BasicNetwork.TAG_OUTPUT, layer);
		} else {
			// add the layer to any previous layers
			final Layer outputLayer = getLayer(BasicNetwork.TAG_OUTPUT);
			outputLayer.addNext(layer, type);
			tagLayer(BasicNetwork.TAG_OUTPUT, layer);
		}
	}

	/**
	 * Calculate the error for this neural network. The error is calculated
	 * using root-mean-square(RMS).
	 * 
	 * @param data
	 *            The training set.
	 * @return The error percentage.
	 */
	public double calculateError(final NeuralDataSet data) {
		final ErrorCalculation errorCalculation = new ErrorCalculation();
		clearContext();

		for (final NeuralDataPair pair : data) {
			final NeuralData actual = compute(pair.getInput());
			errorCalculation.updateError(actual.getData(), pair.getIdeal()
					.getData());
		}
		return errorCalculation.calculate();
	}

	/**
	 * Calculate the total number of neurons in the network across all layers.
	 * 
	 * @return The neuron count.
	 */
	public int calculateNeuronCount() {
		int result = 0;
		for (final Layer layer : this.structure.getLayers()) {
			result += layer.getNeuronCount();
		}
		return result;
	}

	/**
	 * Clear any data from any context layers.
	 */
	public void clearContext() {
		for (final Layer layer : this.structure.getLayers()) {
			if (layer instanceof ContextClearable) {
				((ContextClearable) layer).clearContext();
			}
		}

		for (final Synapse synapse : this.structure.getSynapses()) {
			if (synapse instanceof ContextClearable) {
				((ContextClearable) synapse).clearContext();
			}
		}

		this.structure.updateFlatNetwork();
		if (this.structure.getFlat() != null) {
			this.structure.getFlat().clearContext();
		}
	}

	/**
	 * Remove all layer tags.
	 */
	public void clearLayerTags() {
		this.layerTags.clear();
	}

	/**
	 * Return a clone of this neural network. Including structure, weights and
	 * bias values. This is a deep copy.
	 * 
	 * @return A cloned copy of the neural network.
	 */
	@Override
	public Object clone() {
		final BasicNetwork result = (BasicNetwork) ObjectCloner.deepCopy(this);
		result.getStructure().finalizeStructure();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void compute(final double[] input, final double[] output) {
		final BasicNeuralData input2 = new BasicNeuralData(input);
		final NeuralData output2 = this.compute(input2);
		EngineArray.arrayCopy(output2.getData(), output);
	}

	/**
	 * Compute the output for a given input to the neural network.
	 * 
	 * @param input
	 *            The input to the neural network.
	 * @return The output from the neural network.
	 */
	public NeuralData compute(final NeuralData input) {
		try {
			return this.logic.compute(input, null);
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new NeuralNetworkError(
					"Index exception: there was likely a mismatch between layer sizes, or the size of the input presented to the network.",
					ex);
		}
	}

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
		return this.logic.compute(input, useHolder);
	}

	/**
	 * Create a persistor for this object.
	 * 
	 * @return The newly created persistor.
	 */
	@Override
	public Persistor createPersistor() {
		return new BasicNetworkPersistor();
	}

	/**
	 * @return The weights as a comma separated list.
	 */
	public String dumpWeights() {
		this.structure.updateFlatNetwork();
		final StringBuilder result = new StringBuilder();
		NumberList.toList(CSVFormat.EG_FORMAT, result, this.structure.getFlat()
				.getWeights());
		return result.toString();
	}

	/**
	 * Enable, or disable, a connection.
	 * @param synapse The synapse that contains the connection.
	 * @param fromNeuron The source neuron.
	 * @param toNeuron The target connection.
	 * @param enable True to enable, false to disable.
	 */
	public void enableConnection(final Synapse synapse, final int fromNeuron,
			final int toNeuron, final boolean enable) {
		if (synapse.getMatrix() == null) {
			throw new NeuralNetworkError(
					"Can't enable/disable connection on a synapse that does not have a weight matrix.");
		}

		final double value = synapse.getMatrix().get(fromNeuron, toNeuron);

		if (enable) {
			if (!this.structure.isConnectionLimited()) {
				return;
			}

			if (Math.abs(value) < this.structure.getConnectionLimit()) {
				synapse.getMatrix().set(fromNeuron, toNeuron,
						RangeRandomizer.randomize(-1, 1));
			}
		} else {
			if (!this.structure.isConnectionLimited()) {
				this.properties.put(BasicNetwork.TAG_LIMIT,
						BasicNetwork.DEFAULT_CONNECTION_LIMIT);
				this.structure.finalizeStructure();
			}
			synapse.getMatrix().set(fromNeuron, toNeuron, 0);
		}

		this.structure.setFlatUpdate(FlatUpdateNeeded.Flatten);
	}

	/**
	 * Compare the two neural networks. For them to be equal they must be of the
	 * same structure, and have the same matrix values.
	 * 
	 * @param other
	 *            The other neural network.
	 * @return True if the two networks are equal.
	 */
	public boolean equals(final BasicNetwork other) {
		return equals(other, Encog.DEFAULT_PRECISION);
	}

	/**
	 * Determine if this neural network is equal to another. Equal neural
	 * networks have the same weight matrix and bias values, within a specified
	 * precision.
	 * 
	 * @param other
	 *            The other neural network.
	 * @param precision
	 *            The number of decimal places to compare to.
	 * @return True if the two neural networks are equal.
	 */
	public boolean equals(final BasicNetwork other, final int precision) {
		return NetworkCODEC.equals(this, other, precision);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getInputCount() {
		final Layer layer = this.layerTags.get(BasicNetwork.TAG_INPUT);
		if (layer == null) {
			return 0;
		} else {
			return layer.getNeuronCount();
		}
	}

	/**
	 * Get the layer specified by the tag.
	 * 
	 * @param tag
	 *            The tag.
	 * @return The layer associated with that tag.
	 */
	public Layer getLayer(final String tag) {
		return this.layerTags.get(tag);
	}

	/**
	 * @return The map of all layer tags.
	 */
	public Map<String, Layer> getLayerTags() {
		return this.layerTags;
	}

	/**
	 * @return The logic used by this network.
	 */
	public NeuralLogic getLogic() {
		return this.logic;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getOutputCount() {
		final Layer layer = this.layerTags.get(BasicNetwork.TAG_OUTPUT);
		if (layer == null) {
			return 0;
		} else {
			return layer.getNeuronCount();
		}
	}

	/**
	 * @return A map of all properties.
	 */
	public Map<String, String> getProperties() {
		return this.properties;
	}

	/**
	 * Get the specified property as a double.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The property as a double.
	 */
	public double getPropertyDouble(final String name) {
		return Double.parseDouble(this.properties.get(name));
	}

	/**
	 * Get the specified property as a long.
	 * 
	 * @param name
	 *            The name of the specified property.
	 * @return The value of the specified property.
	 */
	public long getPropertyLong(final String name) {
		return Long.parseLong(this.properties.get(name));
	}

	/**
	 * Get the specified property as a string.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The value of the property.
	 */
	public String getPropertyString(final String name) {
		return this.properties.get(name);
	}

	/**
	 * @return Get the structure of the neural network. The structure allows you
	 *         to quickly obtain synapses and layers without traversing the
	 *         network.
	 */
	public NeuralStructure getStructure() {
		return this.structure;
	}

	/**
	 * Get a list of all of the tags on a specific layer.
	 * 
	 * @param layer
	 *            The layer to check.
	 * @return A collection of the layer tags.
	 */
	public Collection<String> getTags(final Layer layer) {
		final Collection<String> result = new ArrayList<String>();

		for (final Entry<String, Layer> entry : this.layerTags.entrySet()) {
			if (entry.getValue() == layer) {
				result.add(entry.getKey());
			}
		}

		return result;
	}

	/**
	 * @return The size of the matrix.
	 */
	public int getWeightMatrixSize() {
		int result = 0;
		for (final Synapse synapse : this.structure.getSynapses()) {
			result += synapse.getMatrixSize();
		}
		return result;
	}

	/**
	 * Generate a hash code.
	 * 
	 * @return THe hash code.
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Determine if the specified connection is enabled.
	 * @param synapse The synapse.
	 * @param fromNeuron The source neuron.
	 * @param toNeuron THe target neuron.
	 * @return True, if the connection is enabled, false otherwise.
	 */
	public boolean isConnected(final Synapse synapse, final int fromNeuron,
			final int toNeuron) {
		if (!this.structure.isConnectionLimited()) {
			return true;
		}
		final double value = synapse.getMatrix().get(fromNeuron, toNeuron);

		return (Math.abs(value) > this.structure.getConnectionLimit());
	}

	/**
	 * Reset the weight matrix and the bias values. This will use a
	 * Nguyen-Widrow randomizer with a range between -1 and 1. If the network
	 * does not have an input, output or hidden layers, then Nguyen-Widrow
	 * cannot be used and a simple range randomize between -1 and 1 will be
	 * used.
	 * 
	 */
	public void reset() {
		final Layer inputLayer = getLayer(BasicNetwork.TAG_INPUT);
		final Layer outputLayer = getLayer(BasicNetwork.TAG_OUTPUT);

		if ((this.structure.getLayers().size() < 3) || (inputLayer == null)
				|| (outputLayer == null)) {
			(new RangeRandomizer(-1, 1)).randomize(this);
		} else {
			(new NguyenWidrowRandomizer(-1, 1)).randomize(this);
		}
		this.structure.setFlatUpdate(FlatUpdateNeeded.Flatten);
		this.structure.flattenWeights();
	}

	/**
	 * Sets the bias activation for every layer that supports bias. Make sure
	 * that the network structure has been finalized before calling this method.
	 * 
	 * @param activation
	 *            THe new activation.
	 */
	public void setBiasActivation(final double activation) {
		for (final Layer layer : this.structure.getLayers()) {
			if (layer.hasBias()) {
				layer.setBiasActivation(activation);
			}
		}
	}

	/**
	 * Set the type of logic this network should use.
	 * 
	 * @param logic
	 *            The logic used by the network.
	 */
	public void setLogic(final NeuralLogic logic) {
		this.logic = logic;
	}

	/**
	 * Set a property as a double.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param d
	 *            The value of the property.
	 */
	public void setProperty(final String name, final double d) {
		this.properties.put(name, "" + d);
	}

	/**
	 * Set a property as a long.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param l
	 *            The value of the property.
	 */
	public void setProperty(final String name, final long l) {
		this.properties.put(name, "" + l);
	}

	/**
	 * Set a property as a double.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param value
	 *            The value of the property.
	 */
	public void setProperty(final String name, final String value) {
		this.properties.put(name, value);
	}

	/**
	 * Tag a layer.
	 * 
	 * @param tag
	 *            The tag name.
	 * @param layer
	 *            THe layer to tag.
	 */
	public void tagLayer(final String tag, final Layer layer) {
		layer.setNetwork(this);
		this.layerTags.put(tag, layer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("[BasicNetwork: Layers=");
		final int layers = this.structure.getLayers().size();
		builder.append(layers);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Determine the winner for the specified input. This is the number of the
	 * winning neuron.
	 * 
	 * @param input
	 *            The input patter to present to the neural network.
	 * @return The winning neuron.
	 */
	public int winner(final NeuralData input) {

		final NeuralData output = compute(input);
		return BasicNetwork.determineWinner(output);
	}
}

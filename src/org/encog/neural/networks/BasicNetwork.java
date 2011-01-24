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
package org.encog.neural.networks;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.encog.Encog;
import org.encog.engine.util.EngineArray;
import org.encog.engine.util.ErrorCalculation;
import org.encog.mathutil.randomize.NguyenWidrowRandomizer;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.MLContext;
import org.encog.ml.MLEncodable;
import org.encog.ml.MLRegression;
import org.encog.ml.MLResettable;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.structure.NeuralStructure;
import org.encog.persist.BasicPersistedObject;
import org.encog.persist.Persistor;
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
		MLContext, MLRegression, MLEncodable, MLResettable {

	/**
	 * Tag used for the connection limit.
	 */
	public static final String TAG_LIMIT = "CONNECTION_LIMIT";

	public static final double DEFAULT_CONNECTION_LIMIT = 0.0000000001;

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
	 * Properties about the neural network. Some NeuralLogic classes require
	 * certain properties to be set.
	 */
	private final Map<String, String> properties = new HashMap<String, String>();

	/**
	 * Construct an empty neural network.
	 */
	public BasicNetwork() {
		this.structure = new NeuralStructure(this);
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
	public void addLayer(final Layer layer) {
		layer.setNetwork(this);
		this.structure.getLayers().add(layer);
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

		if (this.structure.getFlat() != null) {
			this.structure.getFlat().clearContext();
		}
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
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
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
			NeuralData result = new BasicNeuralData(this.structure.getFlat()
					.getOutputCount());
			this.structure.getFlat().compute(input.getData(), result.getData());
			return result;
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new NeuralNetworkError(
					"Index exception: there was likely a mismatch between layer sizes, or the size of the input presented to the network.",
					ex);
		}
	}

	/**
	 * Create a persistor for this object.
	 * 
	 * @return The newly created persistor.
	 */
	@Override
	public Persistor createPersistor() {
		return null;
	}

	/**
	 * @return The weights as a comma separated list.
	 */
	public String dumpWeights() {

		final StringBuilder result = new StringBuilder();
		NumberList.toList(CSVFormat.EG_FORMAT, result, this.structure.getFlat()
				.getWeights());
		return result.toString();
	}

	/**
	 * Enable, or disable, a connection.
	 * @param fromLayer The layer that contains the from neuron.
	 * @param fromNeuron The source neuron.
	 * @param toNeuron The target connection.
	 * @param enable True to enable, false to disable.
	 */
	public void enableConnection(final int fromLayer, final int fromNeuron,
			final int toNeuron, final boolean enable) {

		final double value = this.getWeight(fromLayer, fromNeuron, toNeuron);

		if (enable) {
			if (!this.structure.isConnectionLimited()) {
				return;
			}

			if (Math.abs(value) < this.structure.getConnectionLimit()) {
				this.setWeight(fromLayer, fromNeuron, toNeuron, 
						RangeRandomizer.randomize(-1, 1));
			}
		} else {
			if (!this.structure.isConnectionLimited()) {
				this.setProperty(BasicNetwork.TAG_LIMIT,
						BasicNetwork.DEFAULT_CONNECTION_LIMIT);
				
			}
			this.setWeight(fromLayer, fromNeuron, toNeuron, 0);
		}
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
	public boolean isConnected(final int layer, final int fromNeuron,
			final int toNeuron) {
		/*if (!this.structure.isConnectionLimited()) {
			return true;
		}
		final double value = synapse.getMatrix().get(fromNeuron, toNeuron);

		return (Math.abs(value) > this.structure.getConnectionLimit());*/
		return false;
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

		if (this.getLayerCount()<3) {
			(new RangeRandomizer(-1, 1)).randomize(this);
		} else {
			(new NguyenWidrowRandomizer(-1, 1)).randomize(this);
		}
	}

	/**
	 * Sets the bias activation for every layer that supports bias. Make sure
	 * that the network structure has been finalized before calling this method.
	 * 
	 * @param activation
	 *            THe new activation.
	 */
	public void setBiasActivation(final double activation) {
		// first, see what mode we are on.  If the network has not been finalized, set the layers
		if (this.structure.getFlat() == null) {
			for (final Layer layer : this.structure.getLayers()) {
				if (layer.hasBias()) {
					layer.setBiasActivation(activation);
				}
			}
		} else {
			for(int i=0;i<getLayerCount();i++)
			{
				if( this.isLayerBiased(i) )
				{
					setLayerBiasActivation(i,activation);
				}
			}
		}
	}
	
	public int getLayerCount()
	{
		this.structure.requireFlat();
		return this.structure.getFlat().getLayerCounts().length;
	}
	
	public int getLayerTotalNeuronCount(int l)
	{
		this.structure.requireFlat();
		int layerNumber = getLayerCount()-l-1;
		return this.structure.getFlat().getLayerCounts()[layerNumber];
	}
	
	public int getLayerNeuronCount(int l)
	{
		this.structure.requireFlat();
		int layerNumber = getLayerCount()-l-1;
		return this.structure.getFlat().getLayerFeedCounts()[layerNumber];
	}
	
	public boolean isLayerBiased(int l)
	{
		this.structure.requireFlat();
		int layerNumber = getLayerCount()-l-1;
		return this.structure.getFlat().getLayerCounts()[layerNumber]!=this.structure.getFlat().getLayerFeedCounts()[layerNumber];
	}
	
	public double getLayerBiasActivation(int l)
	{
		if( !isLayerBiased(l) )
		{
			throw new NeuralNetworkError("Error, the specified layer does not have a bias: " + l);
		}
		
		this.structure.requireFlat();
		int layerNumber = getLayerCount()-l-1;
		
		int layerOutputIndex = this.structure.getFlat().getLayerIndex()[layerNumber];
		int count = this.structure.getFlat().getLayerCounts()[layerNumber];		
		return this.structure.getFlat().getLayerOutput()[layerOutputIndex+count-1];
	}
	
	public void setLayerBiasActivation(int l, double value)
	{
		if( !isLayerBiased(l) )
		{
			throw new NeuralNetworkError("Error, the specified layer does not have a bias: " + l);
		}
		
		this.structure.requireFlat();
		int layerNumber = getLayerCount()-l-1;
		
		int layerOutputIndex = this.structure.getFlat().getLayerIndex()[layerNumber];
		int count = this.structure.getFlat().getLayerCounts()[layerNumber];		
		this.structure.getFlat().getLayerOutput()[layerOutputIndex+count-1] = value;
	}
	
	public double getWeight(int fromLayer, int fromNeuron, int toNeuron)
	{
		this.structure.requireFlat();
		int fromLayerNumber = getLayerCount()-fromLayer-1;
		int toLayerNumber = fromLayerNumber-1;
		
		if( toLayerNumber<0 )
		{
			throw new NeuralNetworkError("The specified layer is not connected to another layer: " + fromLayer);
		}
		
		int weightBaseIndex = this.structure.getFlat().getWeightIndex()[toLayerNumber];
		int count = this.structure.getFlat().getLayerCounts()[fromLayerNumber];
		int weightIndex = weightBaseIndex + fromNeuron + (toNeuron*count);
		
		return this.structure.getFlat().getWeights()[weightIndex];
	}
	
	public void setWeight(int fromLayer, int fromNeuron, int toNeuron, double value)
	{
		this.structure.requireFlat();
		int fromLayerNumber = getLayerCount()-fromLayer-1;
		int toLayerNumber = fromLayerNumber-1;
		
		if( toLayerNumber<0 )
		{
			throw new NeuralNetworkError("The specified layer is not connected to another layer: " + fromLayer);
		}
		
		int weightBaseIndex = this.structure.getFlat().getWeightIndex()[toLayerNumber];
		int count = this.structure.getFlat().getLayerCounts()[fromLayerNumber];
		int weightIndex = weightBaseIndex + fromNeuron + (toNeuron*count);
		
		this.structure.getFlat().getWeights()[weightIndex] = value;
	}
	
	public void addWeight(int fromLayer, int fromNeuron, int toNeuron, double value)
	{
		double old = getWeight(fromLayer,fromNeuron,toNeuron);
		setWeight(fromLayer,fromNeuron,toNeuron,old+value);
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
		this.structure.updateProperties();
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
		this.structure.updateProperties();
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
		this.structure.updateProperties();
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

	@Override
	public int encodedArrayLength() {
		this.structure.requireFlat();
		return this.structure.getFlat().getEncodeLength();
	}

	@Override
	public void decodeFromArray(double[] encoded) {
		this.structure.requireFlat();
		double[] weights = this.structure.getFlat().getWeights();
		if (weights.length != encoded.length) {
			throw new NeuralNetworkError(
					"Size mismatch, encoded array should be of length "
							+ weights.length);
		}

		EngineArray.arrayCopy(encoded, weights);
	}

	@Override
	public void encodeToArray(double[] encoded) {
		this.structure.requireFlat();
		double[] weights = this.structure.getFlat().getWeights();
		if (weights.length != encoded.length) {
			throw new NeuralNetworkError(
					"Size mismatch, encoded array should be of length "
							+ weights.length);
		}

		EngineArray.arrayCopy(weights, encoded);
	}

	@Override
	public void reset(int seed) {
		reset();
	}

	@Override
	public int getInputCount() {
		this.structure.requireFlat();
		return this.getStructure().getFlat().getInputCount();
	}

	@Override
	public int getOutputCount() {
		this.structure.requireFlat();
		return this.getStructure().getFlat().getOutputCount();
	}
}

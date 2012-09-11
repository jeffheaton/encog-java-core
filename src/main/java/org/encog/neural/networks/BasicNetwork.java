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
package org.encog.neural.networks;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.mathutil.randomize.NguyenWidrowRandomizer;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.BasicML;
import org.encog.ml.MLClassification;
import org.encog.ml.MLContext;
import org.encog.ml.MLEncodable;
import org.encog.ml.MLError;
import org.encog.ml.MLFactory;
import org.encog.ml.MLRegression;
import org.encog.ml.MLResettable;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.structure.NeuralStructure;
import org.encog.util.EngineArray;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;
import org.encog.util.obj.ObjectCloner;
import org.encog.util.simple.EncogUtility;

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
public class BasicNetwork extends BasicML implements ContainsFlat, MLContext,
		MLRegression, MLEncodable, MLResettable, MLClassification, MLError,
		MLFactory {

	/**
	 * Tag used for the connection limit.
	 */
	public static final String TAG_LIMIT = "CONNECTION_LIMIT";

	/**
	 * The default connection limit.
	 */
	public static final double DEFAULT_CONNECTION_LIMIT = 0.0000000001;

	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = -136440631687066461L;

	/**
	 * The property for connection limit.
	 */
	public static final String TAG_CONNECTION_LIMIT = "connectionLimit";
	
	/**
	 * The property for begin training.
	 */
	public static final String TAG_BEGIN_TRAINING = "beginTraining";
	
	/**
	 * The property for context target offset.
	 */
	public static final String TAG_CONTEXT_TARGET_OFFSET = "contextTargetOffset";
	
	/**
	 * The property for context target size.
	 */
	public static final String TAG_CONTEXT_TARGET_SIZE = "contextTargetSize";
	
	/**
	 * The property for end training.
	 */
	public static final String TAG_END_TRAINING = "endTraining";
	
	/**
	 * The property for has context.
	 */
	public static final String TAG_HAS_CONTEXT = "hasContext";
	
	/**
	 * The property for layer counts.
	 */
	public static final String TAG_LAYER_COUNTS = "layerCounts";
	
	/**
	 * The property for layer feed counts.
	 */
	public static final String TAG_LAYER_FEED_COUNTS = "layerFeedCounts";
	
	/**
	 * The property for layer index.
	 */
	public static final String TAG_LAYER_INDEX = "layerIndex";
	
	/**
	 * The property for weight index.
	 */
	public static final String TAG_WEIGHT_INDEX = "weightIndex";
	
	/**
	 * The property for bias activation.
	 */
	public static final String TAG_BIAS_ACTIVATION = "biasActivation";

	/**
	 * The property for layer context count.
	 */
	public static final String TAG_LAYER_CONTEXT_COUNT = "layerContextCount";

	/**
	 * Holds the structure of the network. This keeps the network from having to
	 * constantly lookup layers and synapses.
	 */
	private final NeuralStructure structure;

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
	 */
	public void addLayer(final Layer layer) {
		layer.setNetwork(this);
		this.structure.getLayers().add(layer);
	}

	/**
	 * Add to a weight.
	 * @param fromLayer The from layer.
	 * @param fromNeuron The from neuron.
	 * @param toNeuron The to neuron.
	 * @param value The value to add.
	 */
	public void addWeight(final int fromLayer, 
			final int fromNeuron,
			final int toNeuron, final double value) {
		final double old = getWeight(fromLayer, fromNeuron, toNeuron);
		setWeight(fromLayer, fromNeuron, toNeuron, old + value);
	}

	/**
	 * Calculate the error for this neural network.
	 * 
	 * @param data
	 *            The training set.
	 * @return The error percentage.
	 */
	@Override
	public double calculateError(final MLDataSet data) {
		return EncogUtility.calculateRegressionError(this, data);
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
	 * {@inheritDoc}
	 */
	@Override
	public int classify(final MLData input) {
		return winner(input);
	}

	/**
	 * Clear any data from any context layers.
	 */
	@Override
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
	 * Compute the output for this network.
	 * 
	 * @param input
	 *            The input.
	 * @param output
	 *            The output.
	 */
	public void compute(final double[] input, final double[] output) {
		final BasicMLData input2 = new BasicMLData(input);
		final MLData output2 = this.compute(input2);
		EngineArray.arrayCopy(output2.getData(), output);
	}

	/**
	 * Compute the output for a given input to the neural network.
	 * 
	 * @param input
	 *            The input to the neural network.
	 * @return The output from the neural network.
	 */
	@Override
	public MLData compute(final MLData input) {
		try {
			final MLData result = new BasicMLData(this.structure.getFlat()
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
	 * {@inheritDoc}
	 */
	@Override
	public void decodeFromArray(final double[] encoded) {
		this.structure.requireFlat();
		final double[] weights = this.structure.getFlat().getWeights();
		if (weights.length != encoded.length) {
			throw new NeuralNetworkError(
					"Size mismatch, encoded array should be of length "
							+ weights.length);
		}

		EngineArray.arrayCopy(encoded, weights);
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
	 * 
	 * @param fromLayer
	 *            The layer that contains the from neuron.
	 * @param fromNeuron
	 *            The source neuron.
	 * @param toNeuron
	 *            The target connection.
	 * @param enable
	 *            True to enable, false to disable.
	 */
	public void enableConnection(final int fromLayer, 
			final int fromNeuron,
			final int toNeuron, final boolean enable) {

		final double value = getWeight(fromLayer, fromNeuron, toNeuron);

		if (enable) {
			if (!this.structure.isConnectionLimited()) {
				return;
			}

			if (Math.abs(value) < this.structure.getConnectionLimit()) {
				setWeight(fromLayer, fromNeuron, toNeuron,
						RangeRandomizer.randomize(-1, 1));
			}
		} else {
			if (!this.structure.isConnectionLimited()) {
				this.setProperty(BasicNetwork.TAG_LIMIT,
						BasicNetwork.DEFAULT_CONNECTION_LIMIT);
				this.structure.updateProperties();

			}
			setWeight(fromLayer, fromNeuron, toNeuron, 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int encodedArrayLength() {
		this.structure.requireFlat();
		return this.structure.getFlat().getEncodeLength();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void encodeToArray(final double[] encoded) {
		this.structure.requireFlat();
		final double[] weights = this.structure.getFlat().getWeights();
		if (weights.length != encoded.length) {
			throw new NeuralNetworkError(
					"Size mismatch, encoded array should be of length "
							+ weights.length);
		}

		EngineArray.arrayCopy(weights, encoded);
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
	 * Get the activation function for the specified layer.
	 * @param layer The layer.
	 * @return The activation function.
	 */
	public ActivationFunction getActivation(final int layer) {
		this.structure.requireFlat();
		final int layerNumber = getLayerCount() - layer - 1;
		return this.structure.getFlat().getActivationFunctions()[layerNumber];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FlatNetwork getFlat() {
		return getStructure().getFlat();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInputCount() {
		this.structure.requireFlat();
		return getStructure().getFlat().getInputCount();
	}

	/**
	 * Get the bias activation for the specified layer.
	 * @param l The layer.
	 * @return The bias activation.
	 */
	public double getLayerBiasActivation(final int l) {
		if (!isLayerBiased(l)) {
			throw new NeuralNetworkError(
					"Error, the specified layer does not have a bias: " + l);
		}

		this.structure.requireFlat();
		final int layerNumber = getLayerCount() - l - 1;

		final int layerOutputIndex 
			= this.structure.getFlat().getLayerIndex()[layerNumber];
		final int count 
			= this.structure.getFlat().getLayerCounts()[layerNumber];
		return this.structure.getFlat().getLayerOutput()[layerOutputIndex
				+ count - 1];
	}

	/**
	 * @return The layer count.
	 */
	public int getLayerCount() {
		this.structure.requireFlat();
		return this.structure.getFlat().getLayerCounts().length;
	}

	/**
	 * Get the neuron count.
	 * @param l The layer.
	 * @return The neuron count.
	 */
	public int getLayerNeuronCount(final int l) {
		this.structure.requireFlat();
		final int layerNumber = getLayerCount() - l - 1;
		return this.structure.getFlat().getLayerFeedCounts()[layerNumber];
	}

	/**
	 * Get the layer output for the specified neuron.
	 * @param layer The layer.
	 * @param neuronNumber The neuron number.
	 * @return The output from the last call to compute.
	 */
	public double getLayerOutput(final int layer, 
				final int neuronNumber) {
		this.structure.requireFlat();
		final int layerNumber = getLayerCount() - layer - 1;
		final int index = this.structure.getFlat().getLayerIndex()[layerNumber]
				+ neuronNumber;
		final double[] output = this.structure.getFlat().getLayerOutput();
		if (index >= output.length) {
			throw new NeuralNetworkError("The layer index: " + index
					+ " specifies an output index larger than the network has.");
		}
		return output[index];
	}

	/**
	 * Get the total (including bias and context) neuron cont for a layer.
	 * @param l The layer.
	 * @return The count.
	 */
	public int getLayerTotalNeuronCount(final int l) {
		this.structure.requireFlat();
		final int layerNumber = getLayerCount() - l - 1;
		return this.structure.getFlat().getLayerCounts()[layerNumber];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getOutputCount() {
		this.structure.requireFlat();
		return getStructure().getFlat().getOutputCount();
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
	 * Get the weight between the two layers.
	 * @param fromLayer The from layer.
	 * @param fromNeuron The from neuron.
	 * @param toNeuron The to neuron.
	 * @return The weight value.
	 */
	public double getWeight(final int fromLayer, 
			final int fromNeuron,
			final int toNeuron) {
		this.structure.requireFlat();
		validateNeuron(fromLayer, fromNeuron);
		validateNeuron(fromLayer + 1, toNeuron);
		final int fromLayerNumber = getLayerCount() - fromLayer - 1;
		final int toLayerNumber = fromLayerNumber - 1;

		if (toLayerNumber < 0) {
			throw new NeuralNetworkError(
					"The specified layer is not connected to another layer: "
							+ fromLayer);
		}

		final int weightBaseIndex 
			= this.structure.getFlat().getWeightIndex()[toLayerNumber];
		final int count 
			= this.structure.getFlat().getLayerCounts()[fromLayerNumber];
		final int weightIndex = weightBaseIndex + fromNeuron
				+ (toNeuron * count);

		return this.structure.getFlat().getWeights()[weightIndex];
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
	 * 
	 * @param layer
	 *            The layer to check.
	 * @param fromNeuron
	 *            The source neuron.
	 * @param toNeuron
	 *            THe target neuron.
	 * @return True, if the connection is enabled, false otherwise.
	 */
	public boolean isConnected(final int layer, final int fromNeuron,
			final int toNeuron) {
		/*
		 * if (!this.structure.isConnectionLimited()) { return true; } final
		 * double value = synapse.getMatrix().get(fromNeuron, toNeuron);
		 * 
		 * return (Math.abs(value) > this.structure.getConnectionLimit());
		 */
		return false;
	}

	/**
	 * Determine if the specified layer is biased.
	 * @param l The layer number.
	 * @return True, if the layer is biased.
	 */
	public boolean isLayerBiased(final int l) {
		this.structure.requireFlat();
		final int layerNumber = getLayerCount() - l - 1;
		return this.structure.getFlat().getLayerCounts()[layerNumber] 
		                                                 != this.structure
				.getFlat().getLayerFeedCounts()[layerNumber];
	}

	/**
	 * Reset the weight matrix and the bias values. This will use a
	 * Nguyen-Widrow randomizer with a range between -1 and 1. If the network
	 * does not have an input, output or hidden layers, then Nguyen-Widrow
	 * cannot be used and a simple range randomize between -1 and 1 will be
	 * used.
	 * 
	 */
	@Override
	public void reset() {

		if (getLayerCount() < 3) {
			(new RangeRandomizer(-1, 1)).randomize(this);
		} else {
			(new NguyenWidrowRandomizer()).randomize(this);
		}
	}

	/**
	 * Randomize between -1 and 1, use the specified seed.
	 * 
	 */
	@Override
	public void reset(final int seed) {
		ConsistentRandomizer randomizer = new ConsistentRandomizer(-1,1,seed);
		randomizer.randomize(this);
	}

	/**
	 * Sets the bias activation for every layer that supports bias. Make sure
	 * that the network structure has been finalized before calling this method.
	 * 
	 * @param activation
	 *            THe new activation.
	 */
	public void setBiasActivation(final double activation) {
		// first, see what mode we are on. If the network has not been
		// finalized, set the layers
		if (this.structure.getFlat() == null) {
			for (final Layer layer : this.structure.getLayers()) {
				if (layer.hasBias()) {
					layer.setBiasActivation(activation);
				}
			}
		} else {
			for (int i = 0; i < getLayerCount(); i++) {
				if (isLayerBiased(i)) {
					setLayerBiasActivation(i, activation);
				}
			}
		}
	}

	/**
	 * Set the bias activation for the specified layer.
	 * @param l The layer to use.
	 * @param value The bias activation.
	 */
	public void setLayerBiasActivation(final int l, 
				final double value) {
		if (!isLayerBiased(l)) {
			throw new NeuralNetworkError(
					"Error, the specified layer does not have a bias: " + l);
		}

		this.structure.requireFlat();
		final int layerNumber = getLayerCount() - l - 1;

		final int layerOutputIndex 
			= this.structure.getFlat().getLayerIndex()[layerNumber];
		final int count 
			= this.structure.getFlat().getLayerCounts()[layerNumber];
		this.structure.getFlat().getLayerOutput()[layerOutputIndex + count - 1] 
		                                          = value;
	}

	/**
	 * Set the weight between the two specified neurons. The bias neuron is always
	 * the last neuron on a layer.
	 * @param fromLayer The from layer.
	 * @param fromNeuron The from neuron.
	 * @param toNeuron The to neuron.
	 * @param value The to value.
	 */
	public void setWeight(final int fromLayer, final int fromNeuron,
			final int toNeuron, final double value) {
		this.structure.requireFlat();
		final int fromLayerNumber = getLayerCount() - fromLayer - 1;
		final int toLayerNumber = fromLayerNumber - 1;

		if (toLayerNumber < 0) {
			throw new NeuralNetworkError(
					"The specified layer is not connected to another layer: "
							+ fromLayer);
		}

		final int weightBaseIndex 
			= this.structure.getFlat().getWeightIndex()[toLayerNumber];
		final int count 
			= this.structure.getFlat().getLayerCounts()[fromLayerNumber];
		final int weightIndex = weightBaseIndex + fromNeuron
				+ (toNeuron * count);

		this.structure.getFlat().getWeights()[weightIndex] = value;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("[BasicNetwork: Layers=");
		int layers = 0;
		if( this.structure.getFlat()==null)
		{
			layers = this.structure.getLayers().size();
		}
		else 
		{
			layers = this.structure.getFlat().getLayerCounts().length;
		}
		
		builder.append(layers);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateProperties() {
		this.structure.updateProperties();

	}

	/**
	 * Validate the the specified targetLayer and neuron are valid.
	 * @param targetLayer The target layer.
	 * @param neuron The target neuron.
	 */
	public void validateNeuron(final int targetLayer, final int neuron) {
		if ((targetLayer < 0) || (targetLayer >= getLayerCount())) {
			throw new NeuralNetworkError("Invalid layer count: " + targetLayer);
		}

		if ((neuron < 0) || (neuron >= getLayerTotalNeuronCount(targetLayer))) {
			throw new NeuralNetworkError("Invalid neuron number: " + neuron);
		}
	}

	/**
	 * Determine the winner for the specified input. This is the number of the
	 * winning neuron.
	 * 
	 * @param input
	 *            The input patter to present to the neural network.
	 * @return The winning neuron.
	 */
	public int winner(final MLData input) {
		final MLData output = compute(input);
		return EngineArray.maxIndex(output.getData());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFactoryType() {
		return MLMethodFactory.TYPE_FEEDFORWARD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFactoryArchitecture() {
		StringBuilder result = new StringBuilder();
		
		//?:B->SIGMOID->4:B->SIGMOID->?
		
		for(int currentLayer = 0; currentLayer< this.getLayerCount(); currentLayer++) {
						
			// need arrow from prvious levels?
			if( currentLayer>0 ) {
				result.append("->");
			}
			
			// handle activation function
			if( currentLayer>0 && this.getActivation(currentLayer)!=null ) {
				ActivationFunction activationFunction = getActivation(currentLayer);
				result.append(activationFunction.getFactoryCode());
				result.append("->");
			}
			
			result.append(this.getLayerNeuronCount(currentLayer));
			if( this.isLayerBiased(currentLayer) ) {
				result.append(":B");
			}						
		}
						
		return result.toString();
	}
	
	
}

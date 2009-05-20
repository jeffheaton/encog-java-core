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
package org.encog.neural.networks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.encog.Encog;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.BasicNetworkPersistor;
import org.encog.util.ErrorCalculation;
import org.encog.util.randomize.RangeRandomizer;
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
public class BasicNetwork implements Serializable, Network {
	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = -136440631687066461L;

	/**
	 * The input layer.
	 */
	private Layer inputLayer;

	/**
	 * The output layer.
	 */
	private Layer outputLayer;

	/**
	 * The description of this object.
	 */
	private String description;

	/**
	 * The name of this object.
	 */
	private String name;

	/**
	 * Holds the structure of the network. This keeps the network from having to
	 * constantly lookup layers and synapses.
	 */
	private final NeuralStructure structure;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct an empty neural network.
	 */
	public BasicNetwork() {
		this.structure = new NeuralStructure(this);
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
		if (this.inputLayer == null) {
			this.outputLayer = layer; 
			this.inputLayer = layer;
		} else {
			// add the layer to any previous layers
			this.outputLayer.addNext(layer, type);
			this.outputLayer = layer;
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

		for (final NeuralDataPair pair : data) {
			final NeuralData actual = compute(pair.getInput());
			errorCalculation.updateError(actual, pair.getIdeal());
		}
		return errorCalculation.calculateRMS();
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
	 * Check that the input size is acceptable, if it does not match
	 * the input layer, then throw an error.
	 * @param input The input data.
	 */
	public void checkInputSize(final NeuralData input) {
		if (input.size() != this.inputLayer.getNeuronCount()) {

			final String str = 
				"Size mismatch: Can't compute outputs for input size="
					+ input.size()
					+ " for input layer size="
					+ this.inputLayer.getNeuronCount();

			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}

			throw new NeuralNetworkError(str);
		}
	}

	/**
	 * Return a clone of this neural network. Including structure, weights and
	 * threshold values.
	 * 
	 * @return A cloned copy of the neural network.
	 */
	@Override
	public Object clone() {
		final BasicNetwork result = new BasicNetwork();
		final Layer input = cloneLayer(this.inputLayer, result);
		result.setInputLayer(input);
		result.getStructure().finalizeStructure();
		return result;
	}

	/**
	 * Clone an individual layer, called internally by clone.
	 * @param layer The layer to be cloned.
	 * @param network The new network being created.
	 * @return The cloned layer.
	 */
	private Layer cloneLayer(final Layer layer, final BasicNetwork network) {
		final Layer newLayer = (Layer) layer.clone();

		if (layer == getOutputLayer()) {
			network.setOutputLayer(newLayer);
		}

		for (final Synapse synapse : layer.getNext()) {
			final Synapse newSynapse = (Synapse) synapse.clone();
			newSynapse.setFromLayer(layer);
			if (synapse.getToLayer() != null) {
				final Layer to = cloneLayer(synapse.getToLayer(), network);
				newSynapse.setToLayer(to);
			}
			newLayer.getNext().add(newSynapse);

		}
		return newLayer;
	}

	/**
	 * Used to compare one neural network to another, compare two layers.
	 * @param layerThis The layer being compared.
	 * @param layerOther The other layer.
	 * @param precision The precision to use, how many decimal places.
	 * @return Returns true if the two layers are the same.
	 */
	public boolean compareLayer(final Layer layerThis, final Layer layerOther,
			final int precision) {
		final Iterator<Synapse> iteratorOther = layerOther.getNext().iterator();

		for (final Synapse synapseThis : layerThis.getNext()) {
			if (!iteratorOther.hasNext()) {
				return false;
			}
			final Synapse synapseOther = iteratorOther.next();
			if (!synapseThis.getMatrix().equals(synapseOther.getMatrix(),
					precision)) {
				return false;
			}
			if (synapseThis.getToLayer() != null) {
				if (synapseOther.getToLayer() == null) {
					return false;
				}
				if (!compareLayer(synapseThis.getToLayer(), synapseOther
						.getToLayer(), precision)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Compute the output for a given input to the neural network.
	 * @param input The input to the neural network.
	 * @return The output from the neural network.
	 */
	public NeuralData compute(final NeuralData input) {
		return compute(input, null);
	}

	/**
	 * Compute the output for a given input to the neural network. This method
	 * provides a parameter to specify an output holder to use.  This holder
	 * allows propagation training to track the output from each layer.
	 * If you do not need this holder pass null, or use the other 
	 * compare method.
	 * 
	 * @param input The input provide to the neural network.
	 * @param useHolder Allows a holder to be specified, this allows
	 * propagation training to check the output of each layer.  
	 * @return The results from the output neurons.
	 */
	public NeuralData compute(final NeuralData input,
			final NeuralOutputHolder useHolder) {
		NeuralOutputHolder holder;

		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Pattern {} presented to neural network", input);
		}

		if (useHolder == null) {
			holder = new NeuralOutputHolder();
		} else {
			holder = useHolder;
		}

		checkInputSize(input);
		compute(holder, this.inputLayer, input, null);
		return holder.getOutput();

	}

	/**
	 * Internal computation method for a single layer.  This is called, 
	 * as the neural network processes.
	 * @param holder The output holder.
	 * @param layer The layer to process.
	 * @param input The input to this layer.
	 * @param source The source synapse.
	 */
	private void compute(final NeuralOutputHolder holder, final Layer layer,
			final NeuralData input, final Synapse source) {

		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Processing layer: {}, input= {}", layer, input);
		}

		handleRecurrentInput(layer, input, source);

		for (final Synapse synapse : layer.getNext()) {
			if (!holder.getResult().containsKey(synapse)) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Processing synapse: {}", synapse);
				}
				NeuralData pattern = synapse.compute(input);
				pattern = synapse.getToLayer().compute(pattern);
				synapse.getToLayer().process(pattern);
				holder.getResult().put(synapse, input);
				compute(holder, synapse.getToLayer(), pattern, synapse);

				// Is this the output from the entire network?
				if (synapse.getToLayer() == this.outputLayer) {
					holder.setOutput(pattern);
				}
			}
		}
	}

	/**
	 * Create a persistor for this object.
	 * 
	 * @return The newly created persistor.
	 */
	public Persistor createPersistor() {
		return new BasicNetworkPersistor();
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
		return compareLayer(this.inputLayer, other.getInputLayer(),
				Encog.DEFAULT_PRECISION);
	}

	/**
	 * Determine if this neural network is equal to another.  Equal neural
	 * networks have the same weight matrix and threshold values, within
	 * a specified precision.
	 * @param other The other neural network.
	 * @param precision The number of decimal places to compare to.
	 * @return True if the two neural networks are equal.
	 */
	public boolean equals(final BasicNetwork other, final int precision) {
		return compareLayer(this.inputLayer, other.getInputLayer(), precision);
	}

	/**
	 * @return The description for this object.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Get the count for how many hidden layers are present.
	 * 
	 * @return The hidden layer count.
	 */
	public int getHiddenLayerCount() {
		return getHiddenLayers().size();
	}

	/**
	 * Get a collection of the hidden layers in the network.
	 * 
	 * @return The hidden layers.
	 */
	public Collection<Layer> getHiddenLayers() {
		final Collection<Layer> result = new ArrayList<Layer>();

		for (final Layer layer : this.structure.getLayers()) {
			if (isHidden(layer)) {
				result.add(layer);
			}
		}

		return result;
	}

	/**
	 * Get the input layer.
	 * 
	 * @return The input layer.
	 */
	public Layer getInputLayer() {
		return this.inputLayer;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the output layer.
	 * 
	 * @return The output layer.
	 */
	public Layer getOutputLayer() {
		return this.outputLayer;
	}

	/**
	 * @return Get the structure of the neural network.  The structure 
	 * allows you to quickly obtain synapses and layers without 
	 * traversing the network.
	 */
	public NeuralStructure getStructure() {
		return this.structure;
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
	 * Handle recurrent layers.  See if there are any recurrent layers before
	 * the specified layer that must affect the input.
	 * @param layer The layer being processed, see if there are any recurrent
	 * connections to this.
	 * @param input The input to the layer, will be modified with the result
	 * from any recurrent layers.
	 * @param source The source synapse.
	 */
	private void handleRecurrentInput(final Layer layer,
			final NeuralData input, final Synapse source) {
		for (final Synapse synapse 
				: this.structure.getPreviousSynapses(layer)) {
			if (synapse != source) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Recurrent layer from: {}", input);
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

					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Recurrent layer to: {}", input);
					}
				}
			}
		}
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
	 * Called to cause the network to attempt to infer which layer should be
	 * the output layer.
	 */
	public void inferOutputLayer() {
		// set the output layer to null, if we can figure it out it will be set
		// to something else
		this.outputLayer = null;

		// if we do not know the input layer, then there is no way to infer the
		// output layer
		if (getInputLayer() == null) {
			return;
		}

		this.outputLayer = inferOutputLayer(this.inputLayer);
	}

	/**
	 * Internal method that allows the use of recurrsion to determine
	 * the output layer.
	 * @param layer The layer currently being evaluated.
	 * @return The potential output layer.
	 */
	private Layer inferOutputLayer(final Layer layer) {
		for (final Synapse synapse : layer.getNext()) {
			if (synapse.isTeachable() && !synapse.isSelfConnected()) {
				return inferOutputLayer(synapse.getToLayer());
			}
		}

		return layer;
	}

	/**
	 * Determine if this layer is hidden.
	 * @param layer The layer to evaluate.
	 * @return True if this layer is a hidden layer.
	 */
	public boolean isHidden(final Layer layer) {
		return !isInput(layer) && !isOutput(layer);
	}

	/**
	 * Determine if this layer is the input layer.
	 * @param layer The layer to evaluate.
	 * @return True if this layer is the input layer.
	 */
	public boolean isInput(final Layer layer) {
		return this.inputLayer == layer;
	}

	/**
	 * Determine if this layer is the output layer.
	 * @param layer The layer to evaluate.
	 * @return True if this layer is the output layer.
	 */
	public boolean isOutput(final Layer layer) {
		return this.outputLayer == layer;
	}

	/**
	 * Reset the weight matrix and the thresholds.
	 * 
	 */
	public void reset() {
		(new RangeRandomizer(-1, 1)).randomize(this);
	}

	/**
	 * Set the description for this object.
	 * 
	 * @param theDescription
	 *            The description.
	 */
	public void setDescription(final String theDescription) {
		this.description = theDescription;
	}

	/**
	 * Define the input layer for the network.
	 * 
	 * @param input
	 *            The new input layer.
	 */
	public void setInputLayer(final Layer input) {
		this.inputLayer = input;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @param outputLayer
	 *            the outputLayer to set
	 */
	public void setOutputLayer(final Layer outputLayer) {
		this.outputLayer = outputLayer;
	}

	/**
	 * @return Convert this object to a string.
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
		return determineWinner(output);
	}
	
	/**
	 * Determine which member of the output is the winning neuron.
	 * @param output The output from the neural network.
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

}

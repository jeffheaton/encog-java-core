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
import org.encog.matrix.MatrixMath;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.neural.persist.Persistor;
import org.encog.neural.persist.persistors.BasicNetworkPersistor;
import org.encog.util.ErrorCalculation;
import org.encog.util.randomize.RangeRandomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BasicNetwork: This class implements a neural network. This class works in
 * conjunction the Layer classes. Layers are added to the BasicNetwork to
 * specify the structure of the neural network.
 * 
 * The first layer added is the input layer, the final layer added is the output
 * layer. Any layers added between these two layers are the hidden layers.
 */
public class BasicNetwork implements Serializable, Network
		 {
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

	private NeuralStructure structure;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct an empty neural network.
	 */
	public BasicNetwork() {
		this.structure = new NeuralStructure(this);
	}

	public void addLayer(final Layer layer, final SynapseType type) {
		if (this.inputLayer == null)
			this.outputLayer = this.inputLayer = layer;
		else {
			this.outputLayer.addNext(layer, type);
			this.outputLayer = layer;
		}
	}

	/**
	 * Add a layer to the neural network. The first layer added is the input
	 * layer, the last layer added is the output layer.
	 * 
	 * @param layer
	 *            The layer to be added.
	 */
	public void addLayer(final Layer layer) {
		addLayer(layer, SynapseType.Weighted);
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
			NeuralData actual = compute(pair.getInput());
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
		for (Layer layer : this.structure.getLayers()) {
			result += layer.getNeuronCount();
		}
		return result;
	}

	/**
	 * Return a clone of this neural network. Including structure, weights and
	 * threshold values.
	 * 
	 * @return A cloned copy of the neural network.
	 */
	@Override
	public Object clone() {
		BasicNetwork result = new BasicNetwork();
		Layer input = cloneLayer(this.inputLayer, result);
		result.setInputLayer(input);
		result.getStructure().finalizeStructure();
		return result;
	}

	public void setInputLayer(Layer input) {
		this.inputLayer = input;
	}

	private Layer cloneLayer(Layer layer, BasicNetwork network) {
		Layer newLayer = (Layer) layer.clone();

		if (layer == getOutputLayer())
			network.setOutputLayer(newLayer);

		for (Synapse synapse : layer.getNext()) {
			Synapse newSynapse = (Synapse) synapse.clone();
			newSynapse.setFromLayer(layer);
			if (synapse.getToLayer() != null) {
				Layer to = cloneLayer(synapse.getToLayer(), network);
				newSynapse.setToLayer(to);
			}
			newLayer.getNext().add(newSynapse);

		}
		return newLayer;
	}

	public void checkInputSize(final NeuralData input) {
		if (input.size() != this.inputLayer.getNeuronCount()) {
			
			String str = "Size mismatch: Can't compute outputs for input size="
				+ input.size() + " for input layer size="
				+ this.inputLayer.getNeuronCount(); 
			
			if( logger.isErrorEnabled())
			{
				logger.error(str);
			}
			
			throw new NeuralNetworkError(str);
		}
	}

	public NeuralData compute(final NeuralData input) {
		return compute(input, null);
	}

	/**
	 * Compute the output for a given input to the neural network.
	 * 
	 * @param input
	 *            The input provide to the neural network.
	 * @return The results from the output neurons.
	 */
	public NeuralData compute(final NeuralData input,
			NeuralOutputHolder useHolder) {
		NeuralOutputHolder holder;

		if (logger.isDebugEnabled()) {
			logger.debug("Pattern {} presented to neural network", input);
		}

		if (useHolder == null)
			holder = new NeuralOutputHolder();
		else
			holder = useHolder;

		checkInputSize(input);
		compute(holder, this.inputLayer, input, null);
		return holder.getOutput();

	}

	private void compute(NeuralOutputHolder holder, Layer layer,
			NeuralData input, Synapse source) {

		if (logger.isDebugEnabled()) {
			logger.debug("Processing layer: {}, input= {}", layer, input);
		}

		handleRecurrentInput(layer, input, source);

		for (Synapse synapse : layer.getNext()) {
			if (!holder.getResult().containsKey(synapse)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Processing synapse: {}", synapse);
				}
				NeuralData pattern = synapse.compute(input);
				pattern = synapse.getToLayer().compute(pattern);
				synapse.getToLayer().process(pattern);
				holder.getResult().put(synapse, input);
				compute(holder, synapse.getToLayer(), pattern, synapse);

				// Is this the output from the entire network?
				if (synapse.getToLayer() == this.outputLayer)
					holder.setOutput(pattern);
			}
		}
	}

	private void handleRecurrentInput(final Layer layer,
			final NeuralData input, final Synapse source) {
		for (Synapse synapse : this.structure.getPreviousSynapses(layer)) {
			if (synapse != source) {
				if (logger.isDebugEnabled()) {
					logger.debug("Recurrent layer from: {}", input);
				}
				NeuralData recurrentInput = synapse.getFromLayer().recur();
				
				if( recurrentInput!=null )
				{
				NeuralData recurrentOutput = synapse.compute(recurrentInput);
				
				for(int i=0;i<input.size();i++)
				{
					input.setData(i,input.getData(i)+recurrentOutput.getData(i));
				}
				
				if (logger.isDebugEnabled()) {
					logger.debug("Recurrent layer to: {}", input);
				}
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

	public boolean equals(final BasicNetwork other, int precision) {
		return compareLayer(this.inputLayer, other.getInputLayer(), precision);
	}

	public boolean compareLayer(Layer layerThis, Layer layerOther, int precision) {
		Iterator<Synapse> iteratorOther = layerOther.getNext().iterator();

		for (Synapse synapseThis : layerThis.getNext()) {
			if (!iteratorOther.hasNext())
				return false;
			Synapse synapseOther = iteratorOther.next();
			if (!synapseThis.getMatrix().equals(synapseOther.getMatrix(),
					precision))
				return false;
			if (synapseThis.getToLayer() != null) {
				if (synapseOther.getToLayer() == null)
					return false;
				if (!compareLayer(synapseThis.getToLayer(), synapseOther
						.getToLayer(), precision))
					return false;
			}
		}
		return true;
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

		for (Layer layer : this.structure.getLayers()) {
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
	 * Get the size of the weight and threshold matrix.
	 * 
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
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Reset the weight matrix and the thresholds.
	 * 
	 */
	public void reset() {
		(new RangeRandomizer(-1,1)).randomize(this);
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
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
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

	public boolean isInput(Layer layer) {
		return this.inputLayer == layer;
	}

	public boolean isOutput(Layer layer) {
		return this.outputLayer == layer;
	}

	public boolean isHidden(Layer layer) {
		return !isInput(layer) && !isOutput(layer);
	}

	/**
	 * Prune one of the neurons from this layer. Remove all entries in this
	 * weight matrix and other layers.
	 * 
	 * @param neuron
	 *            The neuron to prune. Zero specifies the first neuron.
	 */
	public void prune(final Layer targetLayer, final int neuron) {
		// delete a row on this matrix
		for (Synapse synapse : targetLayer.getNext()) {
			synapse
					.setMatrix(MatrixMath
							.deleteRow(synapse.getMatrix(), neuron));
		}

		// delete a column on the previous
		final Collection<Layer> previous = this.structure
				.getPreviousLayers(targetLayer);

		for (Layer prevLayer : previous) {
			if (previous != null) {
				for (Synapse synapse : prevLayer.getNext()) {
					synapse.setMatrix(MatrixMath.deleteCol(synapse.getMatrix(),
							neuron));
				}
			}
		}

		targetLayer.setNeuronCount(targetLayer.getNeuronCount() - 1);

	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[BasicNetwork: Layers=");
		int layers = this.structure.getLayers().size();
		builder.append(layers);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @param outputLayer
	 *            the outputLayer to set
	 */
	public void setOutputLayer(Layer outputLayer) {
		this.outputLayer = outputLayer;
	}

	public NeuralStructure getStructure() {
		return structure;
	}

}

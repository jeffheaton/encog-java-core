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

package org.encog.neural.networks.layers;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.ContextClearable;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.ContextLayerPersistor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a context layer. A context layer is used to implement a simple
 * recurrent neural network, such as an Elman or Jordan neural network. The
 * context layer has a short-term memory. The context layer accept input, and
 * provide the same data as output on the next cycle. This continues, and the
 * context layer's output "one step" out of sync with the input.
 *
 * @author jheaton
 *
 */
public class ContextLayer extends BasicLayer implements ContextClearable {

	public static final String ERROR = "Bias is not suppoted for a contextlayer.";
	
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -5588659547177460637L;

	/**
	 * The context data that this layer will store.
	 */
	private final NeuralData context;
	
	/**
	 * The location in the flat network that the context is stored at.
	 */
	private int flatContextIndex = -1;

	/**
	 * The logging object.
	 */
	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(ContextLayer.class);

	/**
	 * Default constructor, mainly so the workbench can easily create a default
	 * layer.
	 */
	public ContextLayer() {
		this(1);
	}

	/**
	 * Construct a context layer with the parameters specified.
	 *
	 * @param activationFunction
	 *            The activation function to use.
	 * @param neuronCount
	 *            The neuron count to use.
	 */
	public ContextLayer(final ActivationFunction activationFunction, 
			final int neuronCount) {
		super(activationFunction, false, neuronCount);
		this.context = new BasicNeuralData(neuronCount);
	}

	/**
	 * Construct a default context layer that has the TANH activation function
	 * and the specified number of neurons. Use bias values.
	 *
	 * @param neuronCount
	 *            The number of neurons on this layer.
	 */
	public ContextLayer(final int neuronCount) {
		this(new ActivationTANH(), neuronCount);
	}

	/**
	 * Create a persistor for this layer.
	 *
	 * @return The new persistor.
	 */
	@Override
	public Persistor createPersistor() {
		return new ContextLayerPersistor();
	}

	/**
	 * @return The context, or memory of this layer. These will be the values
	 *         that were just output.
	 */
	public NeuralData getContext() {
		return this.context;
	}

	/**
	 * Called to process input from the previous layer. Simply store the output
	 * in the context.
	 *
	 * @param pattern
	 *            The pattern to store in the context.
	 */
	@Override
	public void process(final NeuralData pattern) {
		double[] s = pattern.getData();
		double[] t = this.context.getData();

		System.arraycopy(s, 0, t, 0, s.length);

		if (ContextLayer.LOGGER.isDebugEnabled()) {
			ContextLayer.LOGGER.debug("Updated ContextLayer to {}", pattern);
		}
	}

	/**
	 * Called to get the output from this layer when called in a recurrent
	 * manor. Simply return the context that was kept from the last iteration.
	 *
	 * @return The recurrent output.
	 */
	@Override
	public NeuralData recur() {
		return this.context;
	}

	/**
	 * Reset the context values back to zero.
	 */
	public void clearContext() {
		for (int i = 0; i < this.context.size(); i++) {
			this.context.setData(i, 0);
		}

	}
	
	/**
	 * @return The weight biases.
	 */
	public double[] getBiasWeights() {
		throw new NeuralNetworkError(ERROR);
	}

	/**
	 * Get one weight bias value.
	 * @param index The index to get.
	 * @return The bias value.
	 */
	public double getBiasWeight(final int index) {
		throw new NeuralNetworkError(ERROR);
	}

	/**
	 * Set the bias weights.
	 * @param d The bias weights.
	 */
	public void setBiasWeights(final double[] d) {
		throw new NeuralNetworkError(ERROR);
	}

	/**
	 * Set a bias weight.
	 * @param index The index of the value to set.
	 * @param d The new value.
	 */
	public void setBiasWeight(final int index, final double d) {
		throw new NeuralNetworkError(ERROR);
	}

	/**
	 * @return The index, in the flat network, where the context is stored.
	 */
	public int getFlatContextIndex() {
		return flatContextIndex;
	}

	/**
	 * Set the flat context index.
	 * @param flatContextIndex The index, in the flat network, where the context is stored.
	 */
	public void setFlatContextIndex(int flatContextIndex) {
		this.flatContextIndex = flatContextIndex;
	}
	
	

}

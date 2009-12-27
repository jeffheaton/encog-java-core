/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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
package org.encog.neural.networks.layers;

import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationTANH;
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

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -5588659547177460637L;

	/**
	 * The context data that this layer will store.
	 */
	private final NeuralData context;

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
	 * @param thresholdFunction
	 *            The threshold function to use.
	 * @param hasThreshold
	 *            Does this layer have thresholds?
	 * @param neuronCount
	 *            The neuron count to use.
	 */
	public ContextLayer(final ActivationFunction thresholdFunction,
			final boolean hasThreshold, final int neuronCount) {
		super(thresholdFunction, hasThreshold, neuronCount);
		this.context = new BasicNeuralData(neuronCount);
	}

	/**
	 * Construct a default context layer that has the TANH activation function
	 * and the specified number of neurons. Use threshold values.
	 * 
	 * @param neuronCount
	 *            The number of neurons on this layer.
	 */
	public ContextLayer(final int neuronCount) {
		this(new ActivationTANH(), true, neuronCount);
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

}

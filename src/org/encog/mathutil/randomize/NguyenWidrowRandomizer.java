/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.mathutil.randomize;

import org.encog.EncogError;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;

/**
 * Implementation of <i>Nguyen-Widrow</i> weight initialization. This is the
 * default weight initialization used by Encog, as it generally provides the
 * most trainable neural network.
 * 
 * 
 * @author St�phan Corriveau
 * 
 */
public class NguyenWidrowRandomizer extends RangeRandomizer implements
		Randomizer {

	/**
	 * Construct a Nguyen-Widrow randomizer.
	 * 
	 * @param min
	 *            The min of the range.
	 * @param max
	 *            The max of the range.
	 */
	public NguyenWidrowRandomizer(final double min, final double max) {
		super(min, max);
	}

	/**
	 * The <i>Nguyen-Widrow</i> initialization algorithm is the following :
	 * <br>
	 * 1. Initialize all weight of hidden layers with (ranged) random values<br>
	 * 2. For each hidden layer<br>
	 * 2.1 calculate beta value, 0.7 * Nth(#neurons of input layer) root of
	 * #neurons of current layer <br>
	 * 2.2 for each synapse<br>
	 * 2.1.1 for each weight <br>
	 * 2.1.2 Adjust weight by dividing by norm of weight for neuron and
	 * multiplying by beta value
	 * @param network The network to randomize.
	 */
	@Override
	public final void randomize(final BasicNetwork network) {

		super.randomize(network);

		int neuronCount = 0;

		for (final Layer layer : network.getStructure().getLayers()) {
			neuronCount += layer.getNeuronCount();
		}

		final Layer inputLayer = network.getLayer(BasicNetwork.TAG_INPUT);
		final Layer outputLayer = network.getLayer(BasicNetwork.TAG_OUTPUT);

		if (inputLayer == null) {
			throw new EncogError("Must have an input layer for Nguyen-Widrow.");
		}

		if (outputLayer == null) {
			throw new EncogError("Must have an output layer for Nguyen-Widrow.");
		}

		final int hiddenNeurons = neuronCount - inputLayer.getNeuronCount()
				- outputLayer.getNeuronCount();

		// can't really do much, use regular randomization
		if (hiddenNeurons < 1) {
			return;
		}

		final double beta = 0.7 * Math.pow(hiddenNeurons, 1.0 / inputLayer
				.getNeuronCount());

		for (final Synapse synapse : network.getStructure().getSynapses()) {
			randomize(beta, synapse);
		}

	}

	/**
	 * Randomize the specified synapse.
	 * 
	 * @param beta
	 *            The beta value.
	 * @param synapse
	 *            The synapse to modify.
	 */
	private void randomize(final double beta, final Synapse synapse) {
		if (synapse.getMatrix() == null) {
			return;
		}

		for (int j = 0; j < synapse.getToNeuronCount(); j++) {
			double norm = 0.0;

			// Calculate the Euclidean Norm for the weights
			for (int k = 0; k < synapse.getFromNeuronCount(); k++) {
				final double value = synapse.getMatrix().get(k, j);
				norm += value * value;
			}

			if (synapse.getToLayer().hasBias()) {
				final double value = synapse.getToLayer().getBiasWeight(j);
				norm += value * value;
				norm = Math.sqrt(norm);
			}

			// Rescale the weights using beta and the norm
			for (int k = 0; k < synapse.getFromNeuronCount(); k++) {
				final double value = synapse.getMatrix().get(k, j);
				synapse.getMatrix().set(k, j, beta * value / norm);
			}

			if (synapse.getToLayer().hasBias()) {
				final double value = synapse.getToLayer().getBiasWeight(j);
				synapse.getToLayer().setBiasWeight(j, beta * value / norm);
			}
		}
	}
}

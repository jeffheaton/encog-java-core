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

package org.encog.neural.networks.training.propagation.gradient;

import java.util.HashMap;
import java.util.Map;

import org.encog.engine.util.ErrorCalculation;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;

/**
 * Single threaded class that actually calculates the gradients. This is used by
 * the individual gradient worker classes.
 */
public class GradientUtil {

	/**
	 * The network we are calculating for.
	 */
	private final BasicNetwork network;

	/**
	 * The error deltas for each layer.
	 */
	private final Map<Layer, Object> layerDeltas = new HashMap<Layer, Object>();

	/**
	 * The gradients.
	 */
	private final double[] errors;

	/**
	 * The weights.
	 */
	private double[] weights;

	/**
	 * The output from the last iteration of the network.
	 */
	private NeuralOutputHolder holder;

	/**
	 * The RMS error.
	 */
	private final ErrorCalculation error = new ErrorCalculation();

	/**
	 * The number of training patterns.
	 */
	private int count;

	/**
	 * Construct the gradient utility.
	 * 
	 * @param network
	 *            The network to calculate gradients for.
	 */
	public GradientUtil(final BasicNetwork network) {
		this.network = network;
		final int size = network.getStructure().calculateSize();
		this.errors = new double[size];
		this.holder = new NeuralOutputHolder();
	}

	/**
	 * Calculate the gradents between the input and ideal data.
	 * 
	 * @param input
	 *            The input data.
	 * @param ideal
	 *            The desired output data.
	 */
	public void calculate(final NeuralData input, final NeuralData ideal) {
		clearDeltas();
		this.count++;
		this.holder = new NeuralOutputHolder();
		final Layer output = this.network.getLayer(BasicNetwork.TAG_OUTPUT);
		final NeuralData actual = this.network.compute(input, this.holder);

		this.error.updateError(actual.getData(), ideal.getData());

		final double[] deltas = getLayerDeltas(output);
		final double[] idealData = ideal.getData();
		final double[] actualData = actual.getData();

		if (output.getActivationFunction().hasDerivative()) {
			for (int i = 0; i < deltas.length; i++) {
				deltas[i] = actual.getData(i);
			}

			// take the derivative of these outputs
			output.getActivationFunction().derivativeFunction(deltas);

			// multiply by the difference between the actual and idea
			for (int i = 0; i < output.getNeuronCount(); i++) {
				deltas[i] = deltas[i] * (idealData[i] - actualData[i]);
			}
		} else {
			for (int i = 0; i < output.getNeuronCount(); i++) {
				deltas[i] = (idealData[i] - actualData[i]);
			}
		}

		int index = 0;
		for (final Layer layer : this.network.getStructure().getLayers()) {
			final double layerDeltas[] = getLayerDeltas(layer);

			if (layer.hasBias()) {
				for (final double layerDelta : layerDeltas) {
					this.errors[index++] += layerDelta;
				}
			}

			for (final Synapse synapse : this.network.getStructure()
					.getPreviousSynapses(layer)) {
				if (synapse.getMatrix() != null) {
					index = calculate(synapse, index);
				}
			}
		}
	}

	/**
	 * Calculate for an entire training set.
	 * 
	 * @param training
	 *            The training set to use.
	 * @param weights
	 *            The weights to use.
	 */
	public void calculate(final NeuralDataSet training, final double[] weights) {
		reset(weights);
		for (final NeuralDataPair pair : training) {
			calculate(pair.getInput(), pair.getIdeal());
		}
	}

	/**
	 * Calculate for an individual synapse.
	 * 
	 * @param synapse
	 *            The synapse to calculate for.
	 * @param index
	 *            The current index in the weight array.
	 * @return The new index value.
	 */
	private int calculate(final Synapse synapse, int index) {

		final double toDeltas[] = getLayerDeltas(synapse.getToLayer());
		final double fromDeltas[] = getLayerDeltas(synapse.getFromLayer());

		final NeuralData actual = this.holder.getResult().get(synapse);
		final double[] actualData = actual.getData();

		for (int x = 0; x < synapse.getToNeuronCount(); x++) {
			for (int y = 0; y < synapse.getFromNeuronCount(); y++) {
				final double value = actualData[y] * toDeltas[x];
				this.errors[index] += value;
				fromDeltas[y] += this.weights[index] * toDeltas[x];
				index++;
			}
		}

		final double[] temp = new double[fromDeltas.length];

		for (int i = 0; i < fromDeltas.length; i++) {
			temp[i] = actualData[i];
		}

		// get an activation function to use
		synapse.getToLayer().getActivationFunction().derivativeFunction(temp);

		for (int i = 0; i < temp.length; i++) {
			fromDeltas[i] *= temp[i];
		}

		return index;
	}

	/**
	 * Clear any deltas.
	 */
	private void clearDeltas() {
		for (final Object obj : this.layerDeltas.values()) {
			final double[] d = (double[]) obj;
			for (int i = 0; i < d.length; i++) {
				d[i] = 0;
			}
		}
	}

	/**
	 * @return The training set count.
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * @return The currenht error
	 */
	public double getError() {
		return this.error.calculate();
	}

	/**
	 * @return The gradients.
	 */
	public double[] getErrors() {
		return this.errors;
	}

	/**
	 * Get the deltas for a layer. The deltas are the difference between actual
	 * and ideal.
	 * 
	 * @param layer
	 *            The layer.
	 * @return The deltas as an array.
	 */
	private double[] getLayerDeltas(final Layer layer) {
		if (this.layerDeltas.containsKey(layer)) {
			return (double[]) this.layerDeltas.get(layer);
		}

		final double[] result = new double[layer.getNeuronCount()];
		this.layerDeltas.put(layer, result);
		return result;
	}

	/**
	 * Reset for an iteration.
	 * 
	 * @param weights
	 *            The weights.
	 */
	public void reset(final double[] weights) {
		this.error.reset();
		this.weights = weights;
		this.layerDeltas.clear();
		this.count = 0;

		for (int i = 0; i < this.errors.length; i++) {
			this.errors[i] = 0;
		}
	}

}

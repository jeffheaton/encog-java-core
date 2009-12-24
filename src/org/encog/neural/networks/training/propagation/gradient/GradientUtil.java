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
package org.encog.neural.networks.training.propagation.gradient;

import java.util.HashMap;
import java.util.Map;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.util.ErrorCalculation;

/**
 * Utility to calculate gradients.  Used by the multithreaded gradient 
 * calculation class.
 *
 */
public class GradientUtil {
	
	/**
	 * The network to calculate for.
	 */
	private final BasicNetwork network;
	
	/**
	 * The layer deltas.
	 */
	private final Map<Layer, Object> layerDeltas = 
		new HashMap<Layer, Object>();
	
	/**
	 * The errors.
	 */
	private final double[] gradients;
	
	/**
	 * The weights.
	 */
	private double[] weights;
	
	/**
	 * Holds the output from each layer in the neural network.
	 */
	private NeuralOutputHolder holder;
	
	/**
	 * The error calculation object.
	 */
	private final ErrorCalculation error = new ErrorCalculation();
	
	/**
	 * How much training data there is to process.
	 */
	private int count;

	/**
	 * Construct a gradient calculation object.
	 * @param network The network to calculate for.
	 */
	public GradientUtil(final BasicNetwork network) {
		this.network = network;
		final int size = network.getStructure().calculateSize();
		this.gradients = new double[size];
		this.holder = new NeuralOutputHolder();
	}

	/**
	 * Calculate one pair.
	 * @param input The input data.
	 * @param ideal The ideal data.
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
			final double[] layerDeltas = getLayerDeltas(layer);

			if (layer.hasThreshold()) {
				for (final double element : layerDeltas) {
					this.gradients[index++] += element;
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
	 * Calculate from the specified training set.
	 * @param training The training set.
	 * @param weights The weights.
	 */
	public void calculate(final NeuralDataSet training, 
			final double[] weights) {
		reset(weights);
		for (final NeuralDataPair pair : training) {
			calculate(pair.getInput(), pair.getIdeal());
		}
	}

	/**
	 * Calculate for the specified synapse. 
	 * @param synapse The synapse.
	 * @param index The starting index.
	 * @return The ending index position in the resulting gradient array.
	 */
	private int calculate(final Synapse synapse, final int index) {

		final double[] toDeltas = getLayerDeltas(synapse.getToLayer());
		final double[] fromDeltas = getLayerDeltas(synapse.getFromLayer());

		final NeuralData actual = this.holder.getResult().get(synapse);
		final double[] actualData = actual.getData();

		int result = index;
		
		for (int x = 0; x < synapse.getToNeuronCount(); x++) {
			for (int y = 0; y < synapse.getFromNeuronCount(); y++) {
				final double value = actualData[y] * toDeltas[x];
				this.gradients[index] += value;
				fromDeltas[y] += this.weights[index] * toDeltas[x];
				result++;
			}
		}

		final double[] temp = new double[fromDeltas.length];

		for (int i = 0; i < fromDeltas.length; i++) {
			temp[i] = actualData[i];
		}

		// get an activation function to use
		synapse.getFromLayer().getActivationFunction().derivativeFunction(temp);

		for (int i = 0; i < temp.length; i++) {
			fromDeltas[i] *= temp[i];
		}

		return result;
	}

	/**
	 * Clear the deltas.
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
	 * @return The count of training data.
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * @return The overall error.
	 */
	public double getError() {
		return this.error.calculateRMS();
	}

	/**
	 * @return The gradients.
	 */
	public double[] getGradients() {
		return this.gradients;
	}

	/**
	 * Get the layer deltas for a layer.
	 * @param layer The layer.
	 * @return The layer deltas.
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
	 * @param weights The weights to use on this iteration.
	 */
	public void reset(final double[] weights) {
		this.error.reset();
		this.weights = weights;
		this.layerDeltas.clear();
		this.count = 0;

		for (int i = 0; i < this.gradients.length; i++) {
			this.gradients[i] = 0;
		}
	}

}

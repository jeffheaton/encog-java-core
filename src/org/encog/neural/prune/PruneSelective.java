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

package org.encog.neural.prune;

import java.util.Collection;

import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.matrices.MatrixMath;
import org.encog.mathutil.randomize.Distort;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prune a neural network selectively. This class allows you to either add or
 * remove neurons from layers of a neural network. Tools
 * 
 * @author jheaton
 * 
 */
public class PruneSelective {

	/**
	 * 
	 */
	private final BasicNetwork network;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct an object prune the neural network.
	 * 
	 * @param network
	 *            The network to prune.
	 */
	public PruneSelective(final BasicNetwork network) {
		this.network = network;
	}

	/**
	 * Change the neuron count for the network. If the count is increased then a
	 * zero-weighted neuron is added, which will not affect the output of the
	 * neural network. If the neuron count is decreased, then the weakest neuron
	 * will be removed.
	 * 
	 * @param layer
	 *            The layer to adjust.
	 * @param neuronCount
	 *            The new neuron count for this layer.
	 */
	public void changeNeuronCount(final Layer layer, final int neuronCount) {

		if (neuronCount == 0) {
			throw new NeuralNetworkError("Can't decrease to zero neurons.");
		}

		// is there anything to do?
		if (neuronCount == layer.getNeuronCount()) {
			return;
		}

		if (neuronCount > layer.getNeuronCount()) {
			increaseNeuronCount(layer, neuronCount);
		} else {
			decreaseNeuronCount(layer, neuronCount);
		}
	}

	/**
	 * Internal function to decrease the neuron count of a layer.
	 * 
	 * @param layer
	 *            The layer to affect.
	 * @param neuronCount
	 *            The new neuron count.
	 */
	private void decreaseNeuronCount(final Layer layer, final int neuronCount) {
		// create an array to hold the least significant neurons, which will be
		// removed

		final int lostNeuronCount = layer.getNeuronCount() - neuronCount;
		final int[] lostNeuron = findWeakestNeurons(layer, lostNeuronCount);

		// finally, actually prune the neurons that the previous steps
		// determined to remove
		for (int i = 0; i < lostNeuronCount; i++) {
			prune(layer, lostNeuron[i] - i);
		}
	}

	/**
	 * Determine the significance of the neuron. The higher the return value,
	 * the more significant the neuron is.
	 * 
	 * @param layer
	 *            The layer to query.
	 * @param neuron
	 *            The neuron to query.
	 * @return How significant is this neuron.
	 */
	public double determineNeuronSignificance(final Layer layer,
			final int neuron) {
		// calculate the bias significance
		double result = 0;

		if (layer.hasBias()) {
			result += layer.getBiasWeight(neuron);
		}

		// calculate the outbound significance
		for (final Synapse synapse : layer.getNext()) {
			for (int i = 0; i < synapse.getToNeuronCount(); i++) {
				result += synapse.getMatrix().get(neuron, i);
			}
		}

		// calculate the bias significance
		final Collection<Synapse> inboundSynapses = this.network.getStructure()
				.getPreviousSynapses(layer);

		for (final Synapse synapse : inboundSynapses) {
			if (synapse.getMatrix() != null) {
				for (int i = 0; i < synapse.getFromNeuronCount(); i++) {
					result += synapse.getMatrix().get(i, neuron);
				}
			}
		}

		return Math.abs(result);
	}

	/**
	 * Find the weakest neurons on a layer.  Considers both weight and bias.
	 * @param layer The layer to search.
	 * @param count The number of neurons to find.
	 * @return An array of the indexes of the weakest neurons.
	 */
	private int[] findWeakestNeurons(final Layer layer, final int count) {
		// create an array to hold the least significant neurons, which will be
		// returned
		final double[] lostNeuronSignificance = new double[count];
		final int[] lostNeuron = new int[count];

		// init the potential lost neurons to the first ones, we will find
		// better choices if we can
		for (int i = 0; i < count; i++) {
			lostNeuron[i] = i;
			lostNeuronSignificance[i] = determineNeuronSignificance(layer, i);
		}

		// now loop over the remaining neurons and see if any are better ones to
		// remove
		for (int i = count; i < layer.getNeuronCount(); i++) {
			final double significance = determineNeuronSignificance(layer, i);

			// is this neuron less significant than one already chosen?
			for (int j = 0; j < count; j++) {
				if (lostNeuronSignificance[j] > significance) {
					lostNeuron[j] = i;
					lostNeuronSignificance[j] = significance;
					break;
				}
			}
		}

		return lostNeuron;
	}

	/**
	 * @return The network that is being processed.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Internal function to increase the neuron count. This will add a
	 * zero-weight neuron to this layer.
	 * 
	 * @param layer
	 *            The layer to increase.
	 * @param neuronCount
	 *            The new neuron count.
	 */
	private void increaseNeuronCount(final Layer layer, final int neuronCount) {
		// adjust the bias
		final double[] newBias = new double[neuronCount];
		if (layer.hasBias()) {
			for (int i = 0; i < layer.getNeuronCount(); i++) {
				newBias[i] = layer.getBiasWeight(i);
			}

			layer.setBiasWeights(newBias);
		}

		// adjust the outbound weight matrixes
		for (final Synapse synapse : layer.getNext()) {
			final Matrix newMatrix = new Matrix(neuronCount,
					synapse.getToNeuronCount());
			// copy existing matrix to new matrix
			for (int row = 0; row < layer.getNeuronCount(); row++) {
				for (int col = 0; col < synapse.getToNeuronCount(); col++) {
					newMatrix.set(row, col, synapse.getMatrix().get(row, col));
				}
			}
			synapse.setMatrix(newMatrix);
		}

		// adjust the inbound weight matrixes
		final Collection<Synapse> inboundSynapses = this.network.getStructure()
				.getPreviousSynapses(layer);

		for (final Synapse synapse : inboundSynapses) {
			if (synapse.getMatrix() != null) {
				final Matrix newMatrix = new Matrix(
						synapse.getFromNeuronCount(), neuronCount);
				// copy existing matrix to new matrix
				for (int row = 0; row < synapse.getFromNeuronCount(); row++) {
					for (int col = 0; col < synapse.getToNeuronCount(); col++) {
						newMatrix.set(row, col,
								synapse.getMatrix().get(row, col));
					}
				}

				synapse.setMatrix(newMatrix);
			}
		}

		// adjust the bias
		if (layer.hasBias()) {
			final double[] newBias2 = new double[neuronCount];

			for (int i = 0; i < layer.getNeuronCount(); i++) {
				newBias2[i] = layer.getBiasWeight(i);
			}
			layer.setBiasWeights(newBias2);
		}

		// finally, up the neuron count
		layer.setNeuronCount(neuronCount);
	}

	/**
	 * Prune one of the neurons from this layer. Remove all entries in this
	 * weight matrix and other layers.
	 * 
	 * @param targetLayer
	 *            The neuron to prune. Zero specifies the first neuron.
	 * @param neuron
	 *            The neuron to prune.
	 */
	public void prune(final Layer targetLayer, final int neuron) {
		// delete a row on this matrix
		for (final Synapse synapse : targetLayer.getNext()) {
			synapse.setMatrix(MatrixMath.deleteRow(synapse.getMatrix(), neuron));
		}

		// delete a column on the previous
		final Collection<Layer> previous = this.network.getStructure()
				.getPreviousLayers(targetLayer);

		for (final Layer prevLayer : previous) {
			if (previous != null) {
				for (final Synapse synapse : prevLayer.getNext()) {
					if (synapse.getMatrix() != null) {
						synapse.setMatrix(MatrixMath.deleteCol(
								synapse.getMatrix(), neuron));
					}
				}
			}
		}

		// remove the bias
		if (targetLayer.hasBias()) {
			final double[] newBias = new double[targetLayer.getNeuronCount() - 1];

			int targetIndex = 0;
			for (int i = 0; i < targetLayer.getNeuronCount(); i++) {
				if (i != neuron) {
					newBias[targetIndex++] = targetLayer.getBiasWeight(i);
				}
			}

			targetLayer.setBiasWeights(newBias);
		}

		// update the neuron count
		targetLayer.setNeuronCount(targetLayer.getNeuronCount() - 1);

	}

	/**
	 * Stimulate the specified neuron by the specified percent. This is used to
	 * randomize the weights and bias values for weak neurons.
	 * 
	 * @param percent
	 *            The percent to randomize by.
	 * @param layer
	 *            The layer that the neuron is on.
	 * @param neuron
	 *            The neuron to randomize.
	 */
	public void stimulateNeuron(final double percent, final Layer layer,
			final int neuron) {
		final Distort d = new Distort(percent);

		if (layer.hasBias()) {
			layer.setBiasWeight(neuron,
					d.randomize(layer.getBiasWeight(neuron)));
		}

		// calculate the outbound significance
		for (final Synapse synapse : layer.getNext()) {
			for (int i = 0; i < synapse.getToNeuronCount(); i++) {
				final double v = synapse.getMatrix().get(neuron, i);
				synapse.getMatrix().set(neuron, i, d.randomize(v));
			}
		}

		final Collection<Synapse> inboundSynapses = this.network.getStructure()
				.getPreviousSynapses(layer);

		for (final Synapse synapse : inboundSynapses) {
			for (int i = 0; i < synapse.getFromNeuronCount(); i++) {
				final double v = synapse.getMatrix().get(i, neuron);
				synapse.getMatrix().set(i, neuron, d.randomize(v));
			}
		}
	}

	/**
	 * Stimulate weaker neurons on a layer. Find the weakest neurons and then
	 * randomize them by the specified percent.
	 * 
	 * @param layer
	 *            The layer to stimulate.
	 * @param count
	 *            The number of weak neurons to stimulate.
	 * @param percent
	 *            The percent to stimulate by.
	 */
	public void stimulateWeakNeurons(final Layer layer, final int count,
			final double percent) {
		final int[] weak = findWeakestNeurons(layer, count);
		for (final int element : weak) {
			stimulateNeuron(percent, layer, element);
		}
	}
}

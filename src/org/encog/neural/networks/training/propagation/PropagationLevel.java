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

package org.encog.neural.networks.training.propagation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds a level worth of information used by each of the propagation methods. A
 * level is defined as all of the layers that feed a single next layer. In a
 * pure feedforward neural network there will be only one layer per level.
 * However, recurrent neural networks will contain multiple layers per level.
 * 
 * @author jheaton
 * 
 */
public class PropagationLevel {

	/**
	 * The number of neurons on this level.
	 */
	private final int neuronCount;
	
	/**
	 * The layers that make up this level.
	 */
	private final List<Layer> layers = new ArrayList<Layer>();
	
	/**
	 * All outgoing synapses from this level.
	 */
	private final List<PropagationSynapse> outgoing = 
		new ArrayList<PropagationSynapse>();
	
	/**
	 * The differences between the actual and expected output for this
	 * level.
	 */
	private final double[] deltas;
	
	/**
	 * The calculated threshold gradients for this level.
	 */
	private final double[] thresholdGradients;
	
	/**
	 * The last iteration's calculated threshold gradients.
	 */
	private final double[] lastThresholdGradients;
	
	/**
	 * The deltas to be applied to the threshold values.
	 */
	private final double[] thresholdDeltas;
	
	/**
	 * The propagation class that this level belongs to.
	 */
	private final PropagationUtil propagationUtil;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a propagation level.
	 * @param propagation The propagation object that created this.
	 * @param layer The initial layer, others can be added later.
	 */
	public PropagationLevel(final PropagationUtil propagation, final Layer layer) {
		this.neuronCount = layer.getNeuronCount();
		this.deltas = new double[this.neuronCount];
		this.thresholdGradients = new double[this.neuronCount];
		this.lastThresholdGradients = new double[this.neuronCount];
		this.layers.add(layer);
		this.propagationUtil = propagation;
		this.thresholdDeltas = new double[this.neuronCount];
	}

	/**
	 * Construct a propagation level with a list of outgoing synapses.
	 * @param propagationUtil The propagation util object to use.
	 * @param outgoing The outgoing synapses.
	 */
	public PropagationLevel(final PropagationUtil propagationUtil,
			final List<Synapse> outgoing) {
		int count = 0;

		this.propagationUtil = propagationUtil;
		this.outgoing.clear();

		final Set<Layer> layerSet = new HashSet<Layer>();

		for (final Synapse synapse : outgoing) {
			count += synapse.getFromNeuronCount();
			layerSet.add(synapse.getFromLayer());
			final PropagationSynapse propSynapse = new PropagationSynapse(
					synapse);
			this.outgoing.add(propSynapse);
		}

		this.layers.addAll(layerSet);

		this.neuronCount = count;

		this.deltas = new double[this.neuronCount];
		this.thresholdGradients = new double[this.neuronCount];
		this.lastThresholdGradients = new double[this.neuronCount];
		this.thresholdDeltas = new double[this.neuronCount];
	}

	/**
	 * Call this method to accumulate the threshold gradients during
	 * a batch.
	 * @param index The index of the gradient to modify.
	 * @param value The value to be added to the existing gradients.
	 */
	public void accumulateThresholdGradient(final int index, 
			final double value) {
		this.thresholdGradients[index] += value;
	}

	/**
	 * Determine the previous synapses from this level.
	 * @return A list of the previous synapses.
	 */
	public List<Synapse> determinePreviousSynapses() {
		final List<Synapse> result = new ArrayList<Synapse>();

		for (final Layer layer : this.layers) {
			final Collection<Synapse> synapses = this.propagationUtil.getNetwork()
					.getStructure().getPreviousSynapses(layer);

			// add all teachable synapses
			for (final Synapse synapse : synapses) {
				if (synapse.isTeachable()) {
					result.add(synapse);
				}
			}
		}

		return result;

	}

	/**
	 * Get the actual output from the specified neuron.
	 * @param index The neuron needed.
	 * @return The actual output from that neuron.
	 */
	public double getActual(final int index) {
		int currentIndex = index;

		// is this the output layer, if so then we need to return the output
		// from
		// the entire network.
		if (this.outgoing.size() == 0) {
			final NeuralData actual = this.propagationUtil.getOutputHolder()
					.getOutput();
			return actual.getData(index);
		}

		// not the output layer, so we need output from one of the previous
		// layers.
		for (final PropagationSynapse synapse : this.outgoing) {
			final int count = synapse.getSynapse().getFromNeuronCount();

			if (currentIndex < count) {
				final NeuralData actual = this.propagationUtil.getOutputHolder()
						.getResult().get(synapse.getSynapse());
				return actual.getData(currentIndex);
			}

			currentIndex -= count;
		}

		throw new NeuralNetworkError(
				"Could not find actual value while propagation training.");
	}

	/**
	 * The deltas, or differences, between the ideal and actual.
	 * @param index The neuron for which we seek a delta.
	 * @return The delta for the specified neuron.
	 */
	public double getDelta(final int index) {
		return this.deltas[index];
	}

	/**
	 * @return The differences between the ideal and actual output.
	 */
	public double[] getDeltas() {
		return this.deltas;
	}

	/**
	 * Get the specified threshold gradient, from the last iteration
	 * of training.
	 * @param index The neuron for which this threshold gradient is
	 * needed.
	 * @return The threshold gradient for the specified neuron.
	 */
	public double getLastThresholdGradent(final int index) {
		return this.lastThresholdGradients[index];
	}

	/**
	 * @return All layers associated with this level.
	 */
	public List<Layer> getLayers() {
		return this.layers;
	}

	/**
	 * @return The neuron count for this level.
	 */
	public int getNeuronCount() {
		return this.neuronCount;
	}

	/**
	 * @return The outgoing synapses for this level.
	 */
	public List<PropagationSynapse> getOutgoing() {
		return this.outgoing;
	}

	/**
	 * Get the specified threshold value's delta.
	 * @param i The threshold value needed.
	 * @return The actual value of this threshold's delta.
	 */
	public double getThresholdDelta(final int i) {
		return this.thresholdDeltas[i];
	}

	/**
	 * Get a specific threshold gradient.
	 * @param index The gradient index to retrieve.
	 * @return The value of the specified gradient index.
	 */
	public double getThresholdGradient(final int index) {
		return this.thresholdGradients[index];
	}

	/**
	 * @return The threshold gradients.
	 */
	public double[] getThresholdGradients() {
		return this.thresholdGradients;
	}

	/**
	 * Set the specified delta value.
	 * @param index The delta value to set.
	 * @param d The new delta value.
	 */
	public void setDelta(final int index, final double d) {
		this.deltas[index] = d;
	}

	/**
	 * Set the threshold gradient from the last iteration.
	 * @param i The index of the threshold gradient to set.
	 * @param d The new gradient value.
	 */
	public void setLastThresholdGradient(final int i, final double d) {
		this.lastThresholdGradients[i] = d;

	}

	/**
	 * Set the specified threshold delta.
	 * @param i The index of the threshold delta to change.
	 * @param d The new value of the specified threshold delta.
	 */
	public void setThresholdDelta(final int i, final double d) {
		this.thresholdDeltas[i] = d;
	}

	/**
	 * Set the specified threshold gradient to the specified value.
	 * @param index The gradient index to be set.
	 * @param d The new value for the specified gradient index.
	 */
	public void setThresholdGradient(final int index, final double d) {
		this.thresholdGradients[index] = d;
	}

	/**
	 * @return This object as a string.
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[PropagationLevel(");
		result.append(this.neuronCount);
		result.append("):");
		for (final Layer layer : this.layers) {
			result.append(layer.toString());
		}
		result.append("]");
		return result.toString();
	}

}

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

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that is used to calculate the partial derivatives for the error for
 * individual layers of a neural network. This calculation must be performed by
 * each of the propagation techniques.
 * 
 * @author jheaton
 * 
 */
public class CalculatePartialDerivative {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Calculate the partial derivative of the error for a layer.
	 * 
	 * @param output
	 *            A holder that contains the output from all of the layers.
	 * @param fromLevel
	 *            The source level.
	 * @param toLevel
	 *            The target level.
	 */
	public void calculateError(final NeuralOutputHolder output,
			final PropagationLevel fromLevel, final PropagationLevel toLevel) {

		// used to hold the errors from this level to the next
		final double[] errors = new double[fromLevel.getNeuronCount()];

		int toNeuronGlobal = 0;

		// loop over every element of the weight matrix and determine the deltas
		// also determine the threshold deltas.
		for (final Layer toLayer : toLevel.getLayers()) {
			for (int toNeuron = 0; toNeuron < toLayer.getNeuronCount(); toNeuron++) {
				int fromNeuronGlobal = 0;

				for (final PropagationSynapse fromSynapse : fromLevel
						.getOutgoing()) {
					for (int fromNeuron = 0; fromNeuron < fromSynapse
							.getSynapse().getFromNeuronCount(); fromNeuron++) {
						errors[fromNeuronGlobal++] += handleMatrixDelta(output,
								fromLevel, toLevel, toLayer, toNeuron,
								fromSynapse, fromNeuron, toNeuronGlobal);
					}

				}

				toLevel.setThresholdGradient(toNeuronGlobal, toLevel
						.getThresholdGradient(toNeuronGlobal)
						+ toLevel.getDelta(toNeuronGlobal));
				toNeuronGlobal++;
			}
		}
		
		double[] deltas = fromLevel.getDeltas();

		for (int i = 0; i < fromLevel.getNeuronCount(); i++) {
			deltas[i] = fromLevel.getActual(i);
		}
		
		// get an activation function to use
		final Layer l = toLevel.getLayers().get(0);
		l.getActivationFunction().derivativeFunction(deltas);

		for (int i = 0; i < fromLevel.getNeuronCount(); i++) {
			deltas[i]*=errors[i];
		}

	}

	/**
	 * Calculate the error for an individual weight matrix element.
	 * 
	 * @param outputHolder
	 *            The output from each of the layers of the neural network.
	 * @param fromLevel
	 *            The from level.
	 * @param toLevel
	 *            The to level.
	 * @param toLayer
	 *            The to layer.
	 * @param toNeuronLocal
	 *            The neuron, within the layer.
	 * @param fromSynapse
	 *            The from synapse.
	 * @param fromNeuron
	 *            The from neuron.
	 * @param toNeuronGlobal
	 *            The global location inside of the level.
	 * @return The error for this individual connection.
	 */
	private double handleMatrixDelta(final NeuralOutputHolder outputHolder,
			final PropagationLevel fromLevel, final PropagationLevel toLevel,
			final Layer toLayer, final int toNeuronLocal,
			final PropagationSynapse fromSynapse, final int fromNeuron,
			final int toNeuronGlobal) {
		final NeuralData output = outputHolder.getResult().get(
				fromSynapse.getSynapse());
		fromSynapse.getAccMatrixGradients().getData()
			[fromNeuron][toNeuronLocal] += toLevel
				.getDelta(toNeuronGlobal)
				* output.getData(fromNeuron);
		return (fromSynapse.getSynapse().getMatrix().getData()
				[fromNeuron][toNeuronLocal] * toLevel
				.getDelta(toNeuronGlobal));
	}
}

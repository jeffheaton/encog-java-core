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

package org.encog.neural.networks.training.propagation.back;

import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixMath;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.propagation.CalculatePartialDerivative;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.PropagationLevel;
import org.encog.neural.networks.training.propagation.PropagationMethod;
import org.encog.neural.networks.training.propagation.PropagationSynapse;
import org.encog.neural.networks.training.propagation.PropagationUtil;
import org.encog.util.logging.DumpMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the specifics of how the backpropagation algorithm is
 * used. Specifically, the partial derivatives are simply applied to the weight
 * matrix.
 * 
 * @author jheaton
 * 
 */
public class BackpropagationMethod implements PropagationMethod {

	/**
	 * The backpropagation class that owns this method.
	 */
	private PropagationUtil propagationUtil;
	
	private double learningRate;
	private double momentum;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Utility class to calculate the partial derivatives.
	 */
	private final CalculatePartialDerivative pderv 
		= new CalculatePartialDerivative();
	
	public BackpropagationMethod(double learningRate, double momentum)	
	{
		this.learningRate = learningRate;
		this.momentum = momentum;
	}

	/**
	 * Calculate the error between these two levels.
	 * @param output The output to the "to level".
	 * @param fromLevel The from level.
	 * @param toLevel The target level.
	 */
	public void calculateError(final NeuralOutputHolder output,
			final PropagationLevel fromLevel, final PropagationLevel toLevel) {
		this.pderv.calculateError(output, fromLevel, toLevel);

	}

	/**
	 * Setup this propagation method using the specified propagation class.
	 * @param propagation The propagation class creating this method.
	 */
	public void init(final PropagationUtil propagationUtil) {
		this.propagationUtil = propagationUtil;
	}

	/**
	 * Modify the weight matrix and thresholds based on the last call to
	 * calcError.
	 */
	public void learn() {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Backpropagation learning pass");
		}

		for (final PropagationLevel level : this.propagationUtil.getLevels()) {
			learnLevel(level);
		}
	}

	/**
	 * Apply learning to this level.
	 * @param level The level that is to learn.
	 */
	private void learnLevel(final PropagationLevel level) {
		// teach the synapses
		for (final PropagationSynapse synapse : level.getOutgoing()) {
			learnSynapse(synapse);
		}

		// teach the threshold
		for (final Layer layer : level.getLayers()) {
			if (layer.hasThreshold()) {
				for (int i = 0; i < layer.getNeuronCount(); i++) {
					double delta = level.getThresholdGradient(i)
							* this.learningRate;
					delta += level.getLastThresholdGradent(i)
							* this.momentum;
					layer.setThreshold(i, layer.getThreshold(i) + delta);
					level.setLastThresholdGradient(i, delta);
					level.setThresholdGradient(i, 0.0);
				}
			}
		}
	}

	/**
	 * Teach this synapse, based on the error that was calculated earlier.
	 * @param synapse The synapse that is to learn.
	 */
	private void learnSynapse(final PropagationSynapse synapse) {

		final Matrix m1 = MatrixMath.multiply(synapse.getAccMatrixGradients(),
				this.learningRate);
		final Matrix m2 = MatrixMath.multiply(synapse.getLastMatrixGradients(),
				this.momentum);
		synapse.setLastMatrixGradients(MatrixMath.add(m1, m2));

		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Backpropagation learning: applying delta=\n"
					+ DumpMatrix.dumpMatrix(synapse.getLastMatrixGradients()));
		}
		synapse.getSynapse().getMatrix().add(synapse.getLastMatrixGradients());
		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Backpropagation learning: new weight matrix=\n"
					+ DumpMatrix.dumpMatrix(synapse.getSynapse().getMatrix()));
		}

		synapse.getAccMatrixGradients().clear();

	}

}

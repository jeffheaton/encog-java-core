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

package org.encog.neural.networks.training.propagation.resilient;

import org.encog.matrix.Matrix;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.propagation.CalculatePartialDerivative;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.PropagationLevel;
import org.encog.neural.networks.training.propagation.PropagationMethod;
import org.encog.neural.networks.training.propagation.PropagationSynapse;
import org.encog.neural.networks.training.propagation.PropagationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the specifics of the resilient propagation training algorithm.
 * 
 * @author jheaton
 * 
 */
public class ResilientPropagationMethod implements PropagationMethod {

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The propagation class that this method is used with.
	 */
	private PropagationUtil propagationUtil;
	
	/**
	 * Utility class to calculate the partial derivative.
	 */
	private final CalculatePartialDerivative pderv 
		= new CalculatePartialDerivative();
	
	private double zeroTolerance;
	private double maxStep;
	private double initialUpdate;
	
	public ResilientPropagationMethod(double zeroTolerance, double maxStep, double initialUpdate)
	{
		this.zeroTolerance = zeroTolerance;
		this.maxStep = maxStep;
		this.initialUpdate = initialUpdate;
	}

	/**
	 * Calculate the error between these two levels.
	 * 
	 * @param output
	 *            The output to the "to level".
	 * @param fromLevel
	 *            The from level.
	 * @param toLevel
	 *            The target level.
	 */
	public void calculateError(final NeuralOutputHolder output,
			final PropagationLevel fromLevel, final PropagationLevel toLevel) {

		this.pderv.calculateError(output, fromLevel, toLevel);

	}

	/**
	 * Init with the specified propagation object.
	 * 
	 * @param propagation
	 *            The propagation object that this method will be used with.
	 */
	public void init(final PropagationUtil propagation) {
		this.propagationUtil = (PropagationUtil) propagation;
		
		// set the initialUpdate to all of the threshold and matrix update
		// values.
		// This is necessary for the first step. RPROP always builds on the
		// previous
		// step, and there is no previous step on the first iteration.
		for (final PropagationLevel level : this.propagationUtil.getLevels()) {
			for (int i = 0; i < level.getNeuronCount(); i++) {
				level.setThresholdDelta(i, this.initialUpdate);
			}

			for (final PropagationSynapse synapse : level.getOutgoing()) {
				synapse.getDeltas().set(this.initialUpdate);
			}
		}

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
	 * Apply the learning to the specified level.
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

					// multiply the current and previous gradient, and take the
					// sign. We want to see if the gradient has changed its
					// sign.
					final int change = sign(level.getThresholdGradient(i)
							* level.getLastThresholdGradent(i));
					double weightChange = 0;

					// if the gradient has retained its sign, then we increase
					// the delta so that it will converge faster
					if (change > 0) {
						double delta = level.getThresholdDelta(i)
								* ResilientPropagation.POSITIVE_ETA;
						delta = Math.min(delta, this.maxStep);
						weightChange = sign(level.getThresholdGradient(i))
								* delta;
						level.setThresholdDelta(i, delta);
						level.setLastThresholdGradient(i, level
								.getThresholdGradient(i));
					} else if (change < 0) {
						// if change<0, then the sign has changed, and the last
						// delta was too big
						double delta = level.getThresholdDelta(i)
								* ResilientPropagation.NEGATIVE_ETA;
						delta = Math.max(delta, ResilientPropagation.DELTA_MIN);
						level.setThresholdDelta(i, delta);
						// set the previous gradient to zero so that there will
						// be no adjustment the next iteration
						level.setLastThresholdGradient(i, 0);
					} else if (change == 0) {
						// if change==0 then there is no change to the delta
						final double delta = level.getThresholdDelta(i);
						weightChange = sign(level.getThresholdGradient(i))
								* delta;
						level.setLastThresholdGradient(i, level
								.getThresholdGradient(i));
					}

					// apply the weight change, if any
					layer.setThreshold(i, layer.getThreshold(i) + weightChange);

					level.setThresholdGradient(i, 0.0);
				}
			}
		}

	}

	/**
	 * Learn from the last error calculation.
	 *
	 * @param synapse The synapse to teach.
	 */
	private void learnSynapse(final PropagationSynapse synapse) {

		final Matrix matrix = synapse.getSynapse().getMatrix();
		double[][] accData = synapse.getAccMatrixGradients().getData();
		double[][] lastData = synapse.getLastMatrixGradients().getData();
		double[][] deltas = synapse.getDeltas().getData();
		double[][] weights = synapse.getSynapse().getMatrix().getData();
		
		for (int row = 0; row < matrix.getRows(); row++) {
			for (int col = 0; col < matrix.getCols(); col++) {
				// multiply the current and previous gradient, and take the
				// sign. We want to see if the gradient has changed its sign.
				final int change = sign(accData[row] [col]
						* lastData[row][col]);
				double weightChange = 0;

				// if the gradient has retained its sign, then we increase the
				// delta so that it will converge faster
				if (change > 0) {
					double delta = deltas[row][col]
							* ResilientPropagation.POSITIVE_ETA;
					delta = Math.min(delta, this.maxStep);
					weightChange = sign(accData[row][col])
							* delta;
					deltas[row][col] = delta;
					lastData[row][col] = accData[row][col];
				} else if (change < 0) {
					// if change<0, then the sign has changed, and the last 
					// delta was too big
					double delta = deltas[row][col]
							* ResilientPropagation.NEGATIVE_ETA;
					delta = Math.max(delta, ResilientPropagation.DELTA_MIN);
					deltas[row][col] = delta;
					// set the previous gradent to zero so that there will be no
					// adjustment the next iteration
					lastData[row][col] = 0;
				} else if (change == 0) {
					// if change==0 then there is no change to the delta
					final double delta = deltas[row][col];
					weightChange = sign(accData[row][col])
							* delta;
					lastData[row][col] = accData[row][col]; 
				}

				// apply the weight change, if any
				weights[row][col] += weightChange;

			}
		}

		// clear out the gradient accumulator for the next iteration
		synapse.getAccMatrixGradients().clear();
	}

	/**
	 * Determine the sign of the value.  
	 * @param value The value to check.
	 * @return -1 if less than zero, 1 if greater, or 0 if zero.
	 */
	private int sign(final double value) {
		if (Math.abs(value) < this.zeroTolerance) {
			return 0;
		} else if (value > 0) {
			return 1;
		} else {
			return -1;
		}
	}

}

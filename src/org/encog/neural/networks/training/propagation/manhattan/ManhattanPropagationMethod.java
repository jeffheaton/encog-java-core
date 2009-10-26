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

package org.encog.neural.networks.training.propagation.manhattan;

import org.encog.matrix.Matrix;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.propagation.CalculatePartialDerivative;
import org.encog.neural.networks.training.propagation.PropagationLevel;
import org.encog.neural.networks.training.propagation.PropagationMethod;
import org.encog.neural.networks.training.propagation.PropagationSynapse;
import org.encog.neural.networks.training.propagation.PropagationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the specifics of the Manhattan propagation algorithm. This class
 * actually handles the updates to the weight matrix.
 * 
 * @author jheaton
 * 
 */
public class ManhattanPropagationMethod implements PropagationMethod {

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The Manhattan propagation class that this method is used by.
	 */
	private PropagationUtil propagationUtil;

	/**
	 * The partial derivative utility class.
	 */
	private final CalculatePartialDerivative pderv = new CalculatePartialDerivative();

	/**
	 * The zero tolerance to use.
	 */
	private double zeroTolerance;

	/**
	 * The learning rate to use. This is the Manhattan update constant.
	 */
	private double learningRate;

	/**
	 * Construct a Manhattan update trainer.
	 * 
	 * @param zeroTolerance
	 *            The zero tolerance to use.
	 * @param learningRate
	 *            The learning rate to use, this is the Manhattan update
	 *            constant.
	 */
	public ManhattanPropagationMethod(final double zeroTolerance,
			final double learningRate) {
		this.zeroTolerance = zeroTolerance;
		this.learningRate = learningRate;
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
	 * Determine the change that should be applied. If the partial derivative
	 * was zero(or close enough to zero) then do nothing otherwise apply the
	 * learning rate with the same sign as the partial derivative.
	 * 
	 * @param value
	 *            The partial derivative.
	 * @return The change to be applied to the weight matrix.
	 */
	private double determineChange(final double value) {
		if (Math.abs(value) < this.zeroTolerance) {
			return 0;
		} else if (value > 0) {
			return this.learningRate;
		} else {
			return -this.learningRate;
		}
	}

	/**
	 * Init with the specified propagation object.
	 * 
	 * @param propagationUtil
	 *            The propagation object that this method will be used with.
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
	 * Apply learning for this level. This is where the weight matrixes are
	 * actually changed. This method will call learnSynapse for each of the
	 * synapses on this level.
	 * 
	 * @param level
	 *            The level that is to learn.
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
					final double change = determineChange(level
							.getThresholdGradient(i)
							* this.learningRate);
					layer.setThreshold(i, layer.getThreshold(i) + change);
				}
			}
		}
	}

	/**
	 * Learn from the last error calculation.
	 * 
	 * @param synapse
	 *            The synapse that is to learn.
	 */
	private void learnSynapse(final PropagationSynapse synapse) {

		final Matrix matrix = synapse.getSynapse().getMatrix();

		for (int row = 0; row < matrix.getRows(); row++) {
			for (int col = 0; col < matrix.getCols(); col++) {
				final double change = determineChange(synapse
						.getAccMatrixGradients().get(row, col));
				matrix.set(row, col, matrix.get(row, col) + change);
			}
		}
	}

}

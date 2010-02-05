/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
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

package org.encog.neural.networks.training;

import java.util.ArrayList;
import java.util.List;

import org.encog.neural.data.NeuralDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract class that implements basic training for most training
 * algorithms. Specifically training strategies can be added to enhance the
 * training.
 * 
 * @author jheaton
 * 
 */
public abstract class BasicTraining implements Train {

	/**
	 * The training strategies to use.
	 */
	private final List<Strategy> strategies = new ArrayList<Strategy>();

	/**
	 * The training data.
	 */
	private NeuralDataSet training;

	/**
	 * The current error rate.
	 */
	private double error;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Training strategies can be added to improve the training results. There
	 * are a number to choose from, and several can be used at once.
	 * 
	 * @param strategy
	 *            The strategy to add.
	 */
	public void addStrategy(final Strategy strategy) {
		strategy.init(this);
		this.strategies.add(strategy);
	}

	/**
	 * Should be called after training has completed and the iteration method
	 * will not be called any further.
	 */
	public void finishTraining() {

	}

	/**
	 * Get the current error percent from the training.
	 * 
	 * @return The current error.
	 */
	public double getError() {
		return this.error;
	}

	/**
	 * @return The strategies to use.
	 */
	public List<Strategy> getStrategies() {
		return this.strategies;
	}

	/**
	 * @return The training data to use.
	 */
	public NeuralDataSet getTraining() {
		return this.training;
	}

	/**
	 * Call the strategies after an iteration.
	 */
	public void postIteration() {
		for (final Strategy strategy : this.strategies) {
			strategy.postIteration();
		}
	}

	/**
	 * Call the strategies before an iteration.
	 */
	public void preIteration() {
		for (final Strategy strategy : this.strategies) {
			strategy.preIteration();
		}
	}

	/**
	 * @param error
	 *            Set the current error rate. This is usually used by training
	 *            strategies.
	 */
	public void setError(final double error) {
		this.error = error;
	}

	/**
	 * Set the training object that this strategy is working with.
	 * 
	 * @param training
	 *            The training object.
	 */
	public void setTraining(final NeuralDataSet training) {
		this.training = training;
	}

}

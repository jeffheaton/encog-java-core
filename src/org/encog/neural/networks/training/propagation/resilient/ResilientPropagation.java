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

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.PropagationLevel;
import org.encog.neural.networks.training.propagation.PropagationSynapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * One problem with the backpropagation algorithm is that the magnitude of the
 * partial derivative is usually too large or too small. Further, the learning
 * rate is a single value for the entire neural network. The resilient
 * propagation learning algorithm uses a special update value(similar to the
 * learning rate) for every neuron connection. Further these update values are
 * automatically determined, unlike the learning rate of the backpropagation
 * algorithm.
 * 
 * For most training situations, we suggest that the resilient propagation
 * algorithm (this class) be used for training.
 * 
 * There are a total of three parameters that must be provided to the resilient
 * training algorithm. Defaults are provided for each, and in nearly all cases,
 * these defaults are acceptable. This makes the resilient propagation algorithm
 * one of the easiest and most efficient training algorithms available.
 * 
 * The optional parameters are:
 * 
 * zeroTolerance - How close to zero can a number be to be considered zero. The
 * default is 0.00000000000000001.
 * 
 * initialUpdate - What are the initial update values for each matrix value. The
 * default is 0.1.
 * 
 * maxStep - What is the largest amount that the update values can step. The
 * default is 50.
 * 
 * 
 * Usually you will not need to use these, and you should use the constructor
 * that does not require them.
 * 
 * 
 * @author jheaton
 * 
 */
public class ResilientPropagation extends Propagation {

	/**
	 * The default zero tolerance.
	 */
	public static final double DEFAULT_ZERO_TOLERANCE = 0.00000000000000001;
	
	/** 
	 * The POSITIVE ETA value.  This is specified by the resilient 
	 * propagation algorithm.  This is the percentage by which 
	 * the deltas are increased by if the partial derivative is
	 * greater than zero.
	 */
	public static final double POSITIVE_ETA = 1.2;
	
	/** 
	 * The NEGATIVE ETA value.  This is specified by the resilient 
	 * propagation algorithm.  This is the percentage by which 
	 * the deltas are increased by if the partial derivative is
	 * less than zero.
	 */
	public static final double NEGATIVE_ETA = 0.5;
	
	/**
	 * The minimum delta value for a weight matrix value.
	 */
	public static final double DELTA_MIN = 1e-6;
	
	/**
	 * The starting update for a delta.
	 */
	public static final double DEFAULT_INITIAL_UPDATE = 0.1;
	
	/**
	 * The maximum amount a delta can reach.
	 */
	public static final double DEFAULT_MAX_STEP = 50;
	
	/**
	 * The zero tolerance.
	 */
	private final double zeroTolerance;
	
	/**
	 * The initial update value.
	 */
	private final double initialUpdate;
	
	/**
	 * The maximum delta amount.
	 */
	private final double maxStep;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a resilient training object.  Use the defaults for all
	 * training parameters.  Usually this is the constructor to use as
	 * the resilient training algorithm is designed for the default 
	 * parameters to be acceptable for nearly all problems.
	 * @param network The network to train.
	 * @param training The training set to use.
	 */
	public ResilientPropagation(final BasicNetwork network,
			final NeuralDataSet training) {
		this(network, training, ResilientPropagation.DEFAULT_ZERO_TOLERANCE,
				ResilientPropagation.DEFAULT_INITIAL_UPDATE,
				ResilientPropagation.DEFAULT_MAX_STEP);
	}

	/**
	 * Construct a resilient training object, allow the training parameters
	 * to be specified.  Usually the default parameters are acceptable for
	 * the resilient training algorithm.  Therefore you should usually
	 * use the other constructor, that makes use of the default values.
	 * @param network The network to train.
	 * @param training The training set to use.
	 * @param zeroTolerance The zero tolerance.
	 * @param initialUpdate The initial update values, this is the amount 
	 * that the deltas are all initially set to.
	 * @param maxStep The maximum that a delta can reach.
	 */
	public ResilientPropagation(final BasicNetwork network,
			final NeuralDataSet training, final double zeroTolerance,
			final double initialUpdate, final double maxStep) {

		super(network, new ResilientPropagationMethod(), training);
		this.initialUpdate = initialUpdate;
		this.maxStep = maxStep;
		this.zeroTolerance = zeroTolerance;

		// set the initialUpdate to all of the threshold and matrix update
		// values.
		// This is necessary for the first step. RPROP always builds on the
		// previous
		// step, and there is no previous step on the first iteration.
		for (final PropagationLevel level : getLevels()) {
			for (int i = 0; i < level.getNeuronCount(); i++) {
				level.setThresholdDelta(i, this.initialUpdate);
			}

			for (final PropagationSynapse synapse : level.getOutgoing()) {
				synapse.getDeltas().set(this.initialUpdate);
			}
		}
	}

	/**
	 * @return The initial update amount, set by the constructor.
	 */
	public double getInitialUpdate() {
		return this.initialUpdate;
	}

	/**
	 * @return The maximum step, set by the constructor.
	 */
	public double getMaxStep() {
		return this.maxStep;
	}

	/**
	 * @return The zero tolerance, set by the constructor.
	 */
	public double getZeroTolerance() {
		return this.zeroTolerance;
	}

}

/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.propagation.gradient.CalculateGradient;
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
	 * The POSITIVE ETA value. This is specified by the resilient propagation
	 * algorithm. This is the percentage by which the deltas are increased by if
	 * the partial derivative is greater than zero.
	 */
	public static final double POSITIVE_ETA = 1.2;

	/**
	 * The NEGATIVE ETA value. This is specified by the resilient propagation
	 * algorithm. This is the percentage by which the deltas are increased by if
	 * the partial derivative is less than zero.
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
	
	public static final String LAST_GRADIENTS = "LAST_GRADIENTS";
	public static final String UPDATE_VALUES = "UPDATE_VALUES";

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
	

	private double[] updateValues;
	private double[] lastGradient;
	private double[] gradients;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a resilient training object. Use the defaults for all training
	 * parameters. Usually this is the constructor to use as the resilient
	 * training algorithm is designed for the default parameters to be
	 * acceptable for nearly all problems.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training set to use.
	 */
	public ResilientPropagation(final BasicNetwork network,
			final NeuralDataSet training) {
		this(network, training, ResilientPropagation.DEFAULT_ZERO_TOLERANCE,
				ResilientPropagation.DEFAULT_INITIAL_UPDATE,
				ResilientPropagation.DEFAULT_MAX_STEP);
	}

	/**
	 * Construct a resilient training object, allow the training parameters to
	 * be specified. Usually the default parameters are acceptable for the
	 * resilient training algorithm. Therefore you should usually use the other
	 * constructor, that makes use of the default values.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training set to use.
	 * @param zeroTolerance
	 *            The zero tolerance.
	 * @param initialUpdate
	 *            The initial update values, this is the amount that the deltas
	 *            are all initially set to.
	 * @param maxStep
	 *            The maximum that a delta can reach.
	 */
	public ResilientPropagation(final BasicNetwork network,
			final NeuralDataSet training, final double zeroTolerance,
			final double initialUpdate, final double maxStep) {

		super(network,training);
		this.initialUpdate = initialUpdate;
		this.maxStep = maxStep;
		this.zeroTolerance = zeroTolerance;

		
		this.updateValues = new double[network.getStructure().calculateSize()];
		this.lastGradient = new double[network.getStructure().calculateSize()];
		
		for(int i = 0;i< this.updateValues.length;i++) {
			this.updateValues[i] = this.initialUpdate;
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

	/**
	 * Perform a training iteration.  This is where the actual RPROP
	 * specific training takes place.
	 * @param prop The gradients.
	 * @param weights The network weights.
	 */
	public void performIteration(CalculateGradient prop, double[] weights) {		
	
		this.gradients = prop.getGradients();
		
		for(int i=0;i<this.gradients.length;i++) {
			weights[i]+=updateWeight(gradients,i);
		}
	}
	
	/**
	 * Determine the sign of the value.
	 * 
	 * @param value
	 *            The value to check.
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
	
	private double updateWeight(double[] gradients, int index)
	{		
		// multiply the current and previous gradient, and take the
		// sign. We want to see if the gradient has changed its sign.
		final int change = sign(this.gradients[index] * this.lastGradient[index]);
		double weightChange = 0;

		// if the gradient has retained its sign, then we increase the
		// delta so that it will converge faster
		if (change > 0) {
			double delta = this.updateValues[index]
					* ResilientPropagation.POSITIVE_ETA;
			delta = Math.min(delta, this.maxStep);
			weightChange = sign(this.gradients[index]) * delta;
			this.updateValues[index] = delta;
			this.lastGradient[index] = this.gradients[index];
		} else if (change < 0) {
			// if change<0, then the sign has changed, and the last
			// delta was too big
			double delta = this.updateValues[index]
					* ResilientPropagation.NEGATIVE_ETA;
			delta = Math.max(delta, ResilientPropagation.DELTA_MIN);
			 this.updateValues[index] = delta;
			// set the previous gradent to zero so that there will be no
			// adjustment the next iteration
			this.lastGradient[index] = 0;
		} else if (change == 0) {
			// if change==0 then there is no change to the delta
			final double delta = this.lastGradient[index];
			weightChange = sign(this.gradients[index]) * delta;
			this.lastGradient[index] = this.gradients[index];
		}

		// apply the weight change, if any
		return weightChange;
	}
	
	public TrainingContinuation pause()
	{
		TrainingContinuation result = new TrainingContinuation();
		result.set(LAST_GRADIENTS, this.lastGradient);
		result.set(UPDATE_VALUES, this.updateValues);
		return result;
	}
	
	public void resume(TrainingContinuation state)
	{
		if( !isValidResume(state))
		{
			throw new TrainingError("Invalid training resume data length");
		}
		this.lastGradient = (double[])state.get(LAST_GRADIENTS);
		this.updateValues = (double[])state.get(UPDATE_VALUES);
	}
	
	public boolean isValidResume(TrainingContinuation state)
	{
		if( !state.getContents().containsKey(LAST_GRADIENTS) || 
			!state.getContents().containsKey(UPDATE_VALUES) )
			return false;
		
		double[] d = (double[])state.get(LAST_GRADIENTS);
		return d.length == this.getNetwork().getStructure().calculateSize();		
	}
	
	public boolean canContinue()
	{
		return true;
	}
}

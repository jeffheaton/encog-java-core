/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.neural.networks.training.propagation.resilient;

import org.encog.mathutil.EncogMath;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.EngineArray;

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
	 * The update values, for the weights and thresholds.
	 */
	private final double[] updateValues;
	
	private final double[] lastDelta;

	/**
	 * The zero tolerance.
	 */
	private final double zeroTolerance;

	/**
	 * The maximum step value for rprop.
	 */
	private final double maxStep;
	
	private RPROPType rpropType = RPROPType.RPROPp;
	
	private double[] lastWeightChange;


	/**
	 * Continuation tag for the last gradients.
	 */
	public static final String LAST_GRADIENTS = "LAST_GRADIENTS";

	/**
	 * Continuation tag for the last values.
	 */
	public static final String UPDATE_VALUES = "UPDATE_VALUES";

	/**
	 * Construct an RPROP trainer, allows an OpenCL device to be specified. Use
	 * the defaults for all training parameters. Usually this is the constructor
	 * to use as the resilient training algorithm is designed for the default
	 * parameters to be acceptable for nearly all problems.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 */
	public ResilientPropagation(final ContainsFlat network,
			final MLDataSet training) {
		this(network, training, RPROPConst.DEFAULT_INITIAL_UPDATE,
				RPROPConst.DEFAULT_MAX_STEP);
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
	 * @param initialUpdate
	 *            The initial update values, this is the amount that the deltas
	 *            are all initially set to.
	 * @param maxStep
	 *            The maximum that a delta can reach.
	 */
	public ResilientPropagation(final ContainsFlat network,
			final MLDataSet training, final double initialUpdate,
			final double maxStep) {

		super(network, training);

		this.updateValues = new double[network.getFlat().getWeights().length];
		this.lastDelta = new double[network.getFlat().getWeights().length];
		this.lastWeightChange = new double[network.getFlat().getWeights().length];
		this.zeroTolerance = RPROPConst.DEFAULT_ZERO_TOLERANCE;
		this.maxStep = maxStep;

		for (int i = 0; i < this.updateValues.length; i++) {
			this.updateValues[i] = initialUpdate;
			this.lastDelta[i] = 0;
		}
	}

	/**
	 * @return True, as RPROP can continue.
	 */
	@Override
	public boolean canContinue() {
		return true;
	}

	/**
	 * Determine if the specified continuation object is valid to resume with.
	 * 
	 * @param state
	 *            The continuation object to check.
	 * @return True if the specified continuation object is valid for this
	 *         training method and network.
	 */
	public boolean isValidResume(final TrainingContinuation state) {
		if (!state.getContents().containsKey(
				ResilientPropagation.LAST_GRADIENTS)
				|| !state.getContents().containsKey(
						ResilientPropagation.UPDATE_VALUES)) {
			return false;
		}

		if (!state.getTrainingType().equals(getClass().getSimpleName())) {
			return false;
		}

		final double[] d = (double[]) state
				.get(ResilientPropagation.LAST_GRADIENTS);
		return d.length == ((ContainsFlat) getMethod()).getFlat().getWeights().length;
	}

	/**
	 * Pause the training.
	 * 
	 * @return A training continuation object to continue with.
	 */
	@Override
	public TrainingContinuation pause() {
		final TrainingContinuation result = new TrainingContinuation();

		result.setTrainingType(this.getClass().getSimpleName());

		result.set(ResilientPropagation.LAST_GRADIENTS,getLastGradient());
		result.set(ResilientPropagation.UPDATE_VALUES,getUpdateValues());

		return result;
	}

	/**
	 * Resume training.
	 * 
	 * @param state
	 *            The training state to return to.
	 */
	@Override
	public void resume(final TrainingContinuation state) {
		if (!isValidResume(state)) {
			throw new TrainingError("Invalid training resume data length");
		}
		final double[] lastGradient = (double[]) state
				.get(ResilientPropagation.LAST_GRADIENTS);
		final double[] updateValues = (double[]) state
				.get(ResilientPropagation.UPDATE_VALUES);

		EngineArray.arrayCopy(lastGradient,getLastGradient());
		EngineArray.arrayCopy(updateValues,getUpdateValues());
	}
	
	/**
	 * Set the type of RPROP to use.  The default is RPROPp (RPROP+), or classic RPROP.
	 * @param t The type.
	 */
	public void setRPROPType(RPROPType t) {
		this.rpropType = t;
	}

	/**
	 * @return The type of RPROP used.
	 */
	public RPROPType getRPROPType() {
		return this.rpropType;
	}
	
	/**
	 * Perform training method specific init.
	 */
	public void initOthers() {
		
	}

	/**
	 * Calculate the amount to change the weight by.
	 * 
	 * @param gradients
	 *            The gradients.
	 * @param lastGradient
	 *            The last gradients.
	 * @param index
	 *            The index to update.
	 * @return The amount to change the weight by.
	 */
	@Override
	public double updateWeight(final double[] gradients,
			final double[] lastGradient, final int index) {
		double weightChange = 0;
		
		switch(this.rpropType) {
			case RPROPp:
				weightChange = updateWeightPlus(gradients,lastGradient,index);
				break;
			case RPROPm:
				weightChange = updateWeightMinus(gradients,lastGradient,index);
				break;
			case iRPROPp:
				weightChange = updateiWeightPlus(gradients,lastGradient,index);
				break;
			case iRPROPm:
				weightChange = updateiWeightMinus(gradients,lastGradient,index);
				break;
			default:
				throw new TrainingError("Unknown RPROP type: " + this.rpropType);
		}
		
		this.lastWeightChange[index] = weightChange;
		return weightChange;
	}
	
	
	public double updateWeightPlus(final double[] gradients,
			final double[] lastGradient, final int index) {
		// multiply the current and previous gradient, and take the
		// sign. We want to see if the gradient has changed its sign.
		final int change = EncogMath.sign(gradients[index] * lastGradient[index]);
		double weightChange = 0;

		// if the gradient has retained its sign, then we increase the
		// delta so that it will converge faster
		if (change > 0) {
			double delta = this.updateValues[index]
					* RPROPConst.POSITIVE_ETA;
			delta = Math.min(delta, this.maxStep);
			weightChange = EncogMath.sign(gradients[index]) * delta;
			this.updateValues[index] = delta;
			lastGradient[index] = gradients[index];
		} else if (change < 0) {
			// if change<0, then the sign has changed, and the last
			// delta was too big
			double delta = this.updateValues[index]
					* RPROPConst.NEGATIVE_ETA;
			delta = Math.max(delta, RPROPConst.DELTA_MIN);
			this.updateValues[index] = delta;
			weightChange = -this.lastWeightChange[index];
			// set the previous gradent to zero so that there will be no
			// adjustment the next iteration
			lastGradient[index] = 0;
		} else if (change == 0) {
			// if change==0 then there is no change to the delta
			final double delta = this.updateValues[index];
			weightChange = EncogMath.sign(gradients[index]) * delta;
			lastGradient[index] = gradients[index];
		}

		// apply the weight change, if any
		return weightChange;
	}
	
	public double updateWeightMinus(final double[] gradients,
			final double[] lastGradient, final int index) {
		// multiply the current and previous gradient, and take the
		// sign. We want to see if the gradient has changed its sign.
		final int change = EncogMath.sign(gradients[index] * lastGradient[index]);
		double weightChange = 0;
		double delta;

		// if the gradient has retained its sign, then we increase the
		// delta so that it will converge faster
		if (change > 0) {
			delta = this.lastDelta[index]
					* RPROPConst.POSITIVE_ETA;
			delta = Math.min(delta, this.maxStep);			
		} else {
			// if change<0, then the sign has changed, and the last
			// delta was too big
			delta = this.lastDelta[index]
					* RPROPConst.NEGATIVE_ETA;
			delta = Math.max(delta, RPROPConst.DELTA_MIN);
		}

		lastGradient[index] = gradients[index];
		weightChange = EncogMath.sign(gradients[index]) * delta;
		this.lastDelta[index] = delta;

		// apply the weight change, if any
		return weightChange;
	}
	
	public double updateiWeightPlus(final double[] gradients,
			final double[] lastGradient, final int index) {
		// multiply the current and previous gradient, and take the
		// sign. We want to see if the gradient has changed its sign.
		final int change = EncogMath.sign(gradients[index] * lastGradient[index]);
		double weightChange = 0;

		// if the gradient has retained its sign, then we increase the
		// delta so that it will converge faster
		if (change > 0) {
			double delta = this.updateValues[index]
					* RPROPConst.POSITIVE_ETA;
			delta = Math.min(delta, this.maxStep);
			weightChange = EncogMath.sign(gradients[index]) * delta;
			this.updateValues[index] = delta;
			lastGradient[index] = gradients[index];
		} else if (change < 0) {
			// if change<0, then the sign has changed, and the last
			// delta was too big
			double delta = this.updateValues[index]
					* RPROPConst.NEGATIVE_ETA;
			delta = Math.max(delta, RPROPConst.DELTA_MIN);
			this.updateValues[index] = delta;
			
			if( this.getError()>this.lastError ) {
				weightChange = -this.lastWeightChange[index];
			}
			
			// set the previous gradent to zero so that there will be no
			// adjustment the next iteration
			lastGradient[index] = 0;
		} else if (change == 0) {
			// if change==0 then there is no change to the delta
			final double delta = this.updateValues[index];
			weightChange = EncogMath.sign(gradients[index]) * delta;
			lastGradient[index] = gradients[index];
		}

		// apply the weight change, if any
		return weightChange;
	}
	
	public double updateiWeightMinus(final double[] gradients,
			final double[] lastGradient, final int index) {
		// multiply the current and previous gradient, and take the
		// sign. We want to see if the gradient has changed its sign.
		final int change = EncogMath.sign(gradients[index] * lastGradient[index]);
		double weightChange = 0;
		double delta;

		// if the gradient has retained its sign, then we increase the
		// delta so that it will converge faster
		if (change > 0) {
			delta = this.lastDelta[index]
					* RPROPConst.POSITIVE_ETA;
			delta = Math.min(delta, this.maxStep);			
		} else {
			// if change<0, then the sign has changed, and the last
			// delta was too big
			delta = this.lastDelta[index]
					* RPROPConst.NEGATIVE_ETA;
			delta = Math.max(delta, RPROPConst.DELTA_MIN);
			lastGradient[index] = 0;
		}

		lastGradient[index] = gradients[index];
		weightChange = EncogMath.sign(gradients[index]) * delta;
		this.lastDelta[index] = delta;

		// apply the weight change, if any
		return weightChange;
	}	

	/**
	 * @return The RPROP update values.
	 */
	public double[] getUpdateValues() {
		return updateValues;
	}

}

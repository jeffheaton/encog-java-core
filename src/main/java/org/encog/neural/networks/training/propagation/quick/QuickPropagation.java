/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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

package org.encog.neural.networks.training.propagation.quick;

import java.util.Random;

import org.encog.EncogError;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.EngineArray;
import org.encog.util.validate.ValidateNetwork;

/**
 * QPROP is an efficient training method that is based on Newton's Method.  
 * QPROP was introduced in a paper:
 * 
 * An Empirical Study of Learning Speed in Back-Propagation Networks" (Scott E. Fahlman, 1988)
 * 
 *  
 * http://www.heatonresearch.com/wiki/Quickprop
 *
 */
public class QuickPropagation extends Propagation implements 
		LearningRate {

	/**
	 * Continuation tag for the last gradients.
	 */
	public static final String LAST_GRADIENTS = "LAST_GRADIENTS";
	
	/**
	 * The learning rate.
	 */
	private double learningRate;

	/**
	 * The last delta values.
	 */
	private double[] lastDelta;

	/**
	 * This factor times the current weight is added to the slope 
	 * at the start of each output epoch. Keeps weights from growing 
	 * too big.
	 */
	private double decay = 0.0001d;

	/**
	 * Used to scale for the size of the training set.
	 */
	private double eps;

	/**
	 * Controls the amount of linear gradient descent 
     * to use in updating output weights.
	 */
	private double outputEpsilon = 0.35;

	/**
	 * Used in computing whether the proposed step is 
     * too large.  Related to learningRate.
	 */
	private double shrink;


	/**
	 * Construct a QPROP trainer for flat networks.  Uses a learning rate of 2.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data.
	 */
	public QuickPropagation(final ContainsFlat network, final MLDataSet training) {
		this(network, training, 2.0);
	}

	/**
	 * Construct a QPROP trainer for flat networks.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data.
	 * @param theLearningRate
	 *            The learning rate.  2 is a good suggestion as 
	 *            a learning rate to start with.  If it fails to converge, 
	 *            then drop it.  Just like backprop, except QPROP can 
	 *            take higher learning rates.
	 */
	public QuickPropagation(final ContainsFlat network,
			final MLDataSet training, final double theLearningRate) {
		super(network, training);
		ValidateNetwork.validateMethodToData(network, training);
		
		this.learningRate = theLearningRate;
		this.lastDelta = new double[this.network.getFlat().getWeights().length];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canContinue() {
		return false;
	}

	/**
	 * @return The last delta values.
	 */
	public double[] getLastDelta() {
		return this.lastDelta;
	}

	/**
	 * @return The learning rate, this is value is essentially a percent. It is
	 *         the degree to which the gradients are applied to the weight
	 *         matrix to allow learning.
	 */
	@Override
	public double getLearningRate() {
		return this.learningRate;
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
		if (!state.getContents().containsKey(QuickPropagation.LAST_GRADIENTS)) {
			return false;
		}

		if (!state.getTrainingType().equals(getClass().getSimpleName())) {
			return false;
		}

		final double[] d = (double[]) state.get(QuickPropagation.LAST_GRADIENTS);
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
		result.set(QuickPropagation.LAST_GRADIENTS, this.getLastGradient());
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
				.get(QuickPropagation.LAST_GRADIENTS);

		EngineArray.arrayCopy(lastGradient,this.getLastGradient());
	}

	/**
	 * Set the learning rate, this is value is essentially a percent. It is the
	 * degree to which the gradients are applied to the weight matrix to allow
	 * learning.
	 * 
	 * @param rate
	 *            The learning rate.
	 */
	@Override
	public void setLearningRate(final double rate) {
		this.learningRate = rate;
	}
	
	/**
	 * @return the outputEpsilon
	 */
	public double getOutputEpsilon() {
		return this.outputEpsilon;
	}

	/**
	 * @return the shrink
	 */
	public double getShrink() {
		return this.shrink;
	}

	/**
	 * @param s the shrink to set
	 */
	public void setShrink(double s) {
		this.shrink = s;
	}

	/**
	 * @param theOutputEpsilon the outputEpsilon to set
	 */
	public void setOutputEpsilon(double theOutputEpsilon) {
		this.outputEpsilon = theOutputEpsilon;
	}
	
	/**
	 * Perform training method specific init.
	 */
	public void initOthers() {
		this.eps = this.outputEpsilon / getTraining().getRecordCount();
		this.shrink = this.learningRate / (1.0 + this.learningRate);
				
	}
	/**
	 * Update a weight.
	 * 
	 * @param gradients
	 *            The gradients.
	 * @param lastGradient
	 *            The last gradients.
	 * @param index
	 *            The index.
	 * @return The weight delta.
	 */
	@Override
	public double updateWeight(final double[] gradients,
			final double[] lastGradient, final int index) {

		final double w = this.network.getFlat().getWeights()[index];
		final double d = this.lastDelta[index];
		final double s = -this.gradients[index] + this.decay * w;
		final double p = -lastGradient[index];
		double nextStep = 0.0;

		// The step must always be in direction opposite to the slope.
		if (d < 0.0) {
			// If last step was negative...
			if (s > 0.0) {
				// Add in linear term if current slope is still positive.
				nextStep -= this.eps * s;
			}
			// If current slope is close to or larger than prev slope...
			if (s >= (this.shrink * p)) {
				// Take maximum size negative step.
				nextStep += this.learningRate * d;
			} else {
				// Else, use quadratic estimate.
				nextStep += d * s / (p - s);
			}
		} else if (d > 0.0) {
			// If last step was positive...
			if (s < 0.0) {
				// Add in linear term if current slope is still negative.
				nextStep -= this.eps * s;
			}
			// If current slope is close to or more neg than prev slope...
			if (s <= (this.shrink * p)) {
				// Take maximum size negative step.
				nextStep += this.learningRate * d; 
			} else {
				// Else, use quadratic estimate.
				nextStep += d * s / (p - s); 
			}
		} else {
			// Last step was zero, so use only linear term. 
			nextStep -= this.eps * s;
		}

		// update global data arrays
		this.lastDelta[index] = nextStep;
		this.getLastGradient()[index] = gradients[index];

		return nextStep;
	}
	
	/**
	 * Update a weight with droput.
	 * 
	 * @param gradients
	 *            The gradients.
	 * @param lastGradient
	 *            The last gradients.
	 * @param index
	 *            The index.
	 * @param dropoutRate
	 * 			  The dropout rate.
	 * @return The weight delta.
	 */
	@Override
	public double updateWeight(final double[] gradients,
			final double[] lastGradient, final int index, double dropoutRate) {
		
		if (dropoutRate > 0 && dropoutRandomSource.nextDouble() < dropoutRate) {
			return 0;
		};
		
		final double w = this.network.getFlat().getWeights()[index];
		final double d = this.lastDelta[index];
		final double s = -this.gradients[index] + this.decay * w;
		final double p = -lastGradient[index];
		double nextStep = 0.0;

		// The step must always be in direction opposite to the slope.
		if (d < 0.0) {
			// If last step was negative...
			if (s > 0.0) {
				// Add in linear term if current slope is still positive.
				nextStep -= this.eps * s;
			}
			// If current slope is close to or larger than prev slope...
			if (s >= (this.shrink * p)) {
				// Take maximum size negative step.
				nextStep += this.learningRate * d;
			} else {
				// Else, use quadratic estimate.
				nextStep += d * s / (p - s);
			}
		} else if (d > 0.0) {
			// If last step was positive...
			if (s < 0.0) {
				// Add in linear term if current slope is still negative.
				nextStep -= this.eps * s;
			}
			// If current slope is close to or more neg than prev slope...
			if (s <= (this.shrink * p)) {
				// Take maximum size negative step.
				nextStep += this.learningRate * d; 
			} else {
				// Else, use quadratic estimate.
				nextStep += d * s / (p - s); 
			}
		} else {
			// Last step was zero, so use only linear term. 
			nextStep -= this.eps * s;
		}

		// update global data arrays
		this.lastDelta[index] = nextStep;
		this.getLastGradient()[index] = gradients[index];

		return nextStep;
	}

	/**
	 * Do not allow batch sizes other than 0, not supported.
	 */
	public void setBatchSize(int theBatchSize) {
		if( theBatchSize!=0 ) {
			throw new EncogError("Online training is not supported for:" + this.getClass().getSimpleName());
		}
	}
}
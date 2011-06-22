/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.neural.flat.train.prop;

import org.encog.ml.data.MLDataSet;
import org.encog.neural.flat.FlatNetwork;

/**
 * Train a flat network, using QuickPropagation.  Code in this file is based on work done by 
 */
public class TrainFlatNetworkQPROP extends TrainFlatNetworkProp {

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
	public TrainFlatNetworkQPROP(final FlatNetwork network,
			final MLDataSet training, final double theLearningRate) {
		super(network, training);
		this.learningRate = theLearningRate;
		this.lastDelta = new double[this.network.getWeights().length];
	}
	
	/**
	 * Perform training method specific init.
	 */
	public void initOthers() {
		this.eps = this.outputEpsilon / getTraining().getRecordCount();
		this.shrink = this.learningRate / (1.0 + this.learningRate);
				
	}

	/**
	 * @return the learningRate
	 */
	public final double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * Set the learning rate.
	 * 
	 * @param rate
	 *            The learning rate.
	 */
	public final void setLearningRate(final double rate) {
		this.learningRate = rate;
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
	public final double updateWeight(final double[] gradients,
			final double[] lastGradient, final int index) {

		final double w = this.network.getWeights()[index];
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
		getLastGradient()[index] = gradients[index];

		return nextStep;
	}

	/**
	 * @return the decay
	 */
	public double getDecay() {
		return decay;
	}

	/**
	 * @return the outputEpsilon
	 */
	public double getOutputEpsilon() {
		return outputEpsilon;
	}

	/**
	 * @return the shrink
	 */
	public double getShrink() {
		return shrink;
	}

	/**
	 * @param shrink the shrink to set
	 */
	public void setShrink(double shrink) {
		this.shrink = shrink;
	}

	/**
	 * @param outputEpsilon the outputEpsilon to set
	 */
	public void setOutputEpsilon(double outputEpsilon) {
		this.outputEpsilon = outputEpsilon;
	}

	/**
	 * @return the lastDelta
	 */
	public double[] getLastDelta() {
		return lastDelta;
	}

	/**
	 * @param lastDelta the lastDelta to set
	 */
	public void setLastDelta(double[] lastDelta) {
		this.lastDelta = lastDelta;
	}

	/**
	 * @return the eps
	 */
	public double getEps() {
		return eps;
	}

	/**
	 * @param decay the decay to set
	 */
	public void setDecay(double decay) {
		this.decay = decay;
	}
	
	

}

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
package org.encog.neural.networks.training.propagation.scg;

import org.encog.mathutil.BoundNumbers;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.EngineArray;

/**
 * This is a training class that makes use of scaled conjugate gradient methods.
 * It is a very fast and efficient training algorithm.
 * 
 */
public class ScaledConjugateGradient extends Propagation {

	/**
	 * The starting value for sigma.
	 */
	protected static final double FIRST_SIGMA = 1.E-4D;

	/**
	 * The starting value for lambda.
	 */
	protected static final double FIRST_LAMBDA = 1.E-6D;

	/**
	 * Should we restart?
	 */
	private boolean restart;

	/**
	 * The second lambda value.
	 */
	private double lambda2;

	/**
	 * The first lambda value.
	 */
	private double lambda;

	/**
	 * The number of iterations. The network will reset when this value
	 * increases over the number of weights in the network.
	 */
	private int k;

	/**
	 * Tracks if the latest training cycle was successful.
	 */
	private boolean success = true;

	/**
	 * The magnitude of p.
	 */
	private double magP;

	/**
	 * Step direction vector.
	 */
	private final double[] p;

	/**
	 * Step direction vector.
	 */
	private final double[] r;

	/**
	 * The neural network weights.
	 */
	private final double[] weights;

	/**
	 * The current delta.
	 */
	private double delta;

	/**
	 * The old error value, used to make sure an improvement happened.
	 */
	private double oldError;

	/**
	 * The old weight values, used to restore the neural network.
	 */
	private final double[] oldWeights;

	/**
	 * The old gradients, used to compare.
	 */
	private final double[] oldGradient;

	/**
	 * Should the initial gradients be calculated.
	 */
	private boolean mustInit;

	
	/**
	 * Construct a training class.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data.
	 */
	public ScaledConjugateGradient(final ContainsFlat network,
			final MLDataSet training) {
		super(network, training);

		this.success = true;
		this.delta = 0;
		this.lambda2 = 0;
		this.lambda = ScaledConjugateGradient.FIRST_LAMBDA;
		this.oldError = 0;
		this.magP = 0;
		this.restart = false;

		this.weights = EngineArray.arrayCopy(network.getFlat().getWeights());
		final int numWeights = this.weights.length;

		this.oldWeights = new double[numWeights];
		this.oldGradient = new double[numWeights];

		this.p = new double[numWeights];
		this.r = new double[numWeights];

		this.mustInit = true;
	}
	
	/**
	 * This training type does not support training continue.
	 * @return Always returns false.
	 */
	@Override
	public final boolean canContinue() {
		return false;
	}

	/**
	 * This training type does not support training continue.
	 * @return Always returns null.
	 */
	@Override
	public final TrainingContinuation pause() {
		return null;
	}

	/**
	 * This training type does not support training continue.
	 * @param state Not used.
	 */
	@Override
	public final void resume(final TrainingContinuation state) {
		
	}
	
	/**
	 * Calculate the gradients. They are normalized as well.
	 */
	@Override
	public void calculateGradients() {

		final int outCount = this.network.getFlat().getOutputCount();

		super.calculateGradients();

		// normalize

		final double factor = -2D / this.gradients.length / outCount;

		for (int i = 0; i < this.gradients.length; i++) {
			this.gradients[i] *= factor;
		}

	}
	
	/**
	 * Calculate the starting set of gradients.
	 */
	private void init() {
		final int numWeights = this.weights.length;

		calculateGradients();

		this.k = 1;

		for (int i = 0; i < numWeights; ++i) {
			this.p[i] = this.r[i] = -this.gradients[i];
		}

		this.mustInit = false;
	}

	/**
	 * Perform one iteration.
	 */
	@Override
	public void iteration() {

		if (this.mustInit) {
			init();
		}
		
		rollIteration();
		
		final int numWeights = this.weights.length;
		// Storage space for previous iteration values.

		if (this.restart) {
			// First time through, set initial values for SCG parameters.
			this.lambda = ScaledConjugateGradient.FIRST_LAMBDA;
			this.lambda2 = 0;
			this.k = 1;
			this.success = true;
			this.restart = false;
		}

		// If an error reduction is possible, calculate 2nd order info.
		if (this.success) {

			// If the search direction is small, stop.
			this.magP = EngineArray.vectorProduct(this.p, this.p);

			final double sigma = ScaledConjugateGradient.FIRST_SIGMA
					/ Math.sqrt(this.magP);

			// In order to compute the new step, we need a new gradient.
			// First, save off the old data.
			EngineArray.arrayCopy(this.gradients, this.oldGradient);
			EngineArray.arrayCopy(this.weights, this.oldWeights);
			this.oldError = getError();

			// Now we move to the new point in weight space.
			for (int i = 0; i < numWeights; ++i) {
				this.weights[i] += sigma * this.p[i];
			}

			EngineArray.arrayCopy(this.weights, this.network.getFlat().getWeights());

			// And compute the new gradient.
			calculateGradients();

			// Now we have the new gradient, and we continue the step
			// computation.
			this.delta = 0;
			for (int i = 0; i < numWeights; ++i) {
				final double step = (this.gradients[i] - this.oldGradient[i])
						/ sigma;
				this.delta += this.p[i] * step;
			}
		}

		// Scale delta.
		this.delta += (this.lambda - this.lambda2) * this.magP;

		// If delta <= 0, make Hessian positive definite.
		if (this.delta <= 0) {
			this.lambda2 = 2 * (this.lambda - this.delta / this.magP);
			this.delta = this.lambda * this.magP - this.delta;
			this.lambda = this.lambda2;
		}

		// Calculate step size.
		final double mu = EngineArray.vectorProduct(this.p, this.r);
		final double alpha = mu / this.delta;

		// Calculate the comparison parameter.
		// We must compute a new gradient, but this time we do not
		// want to keep the old values. They were useful only for
		// approximating the Hessian.
		for (int i = 0; i < numWeights; ++i) {
			this.weights[i] = this.oldWeights[i] + alpha * this.p[i];
		}

		EngineArray.arrayCopy(this.weights, this.network.getFlat().getWeights());

		calculateGradients();

		final double gdelta = 2 * this.delta * (this.oldError - getError())
				/ (mu * mu);

		// If gdelta >= 0, a successful reduction in error is possible.
		if (gdelta >= 0) {
			// Product of r(k+1) by r(k)
			double rsum = 0;

			// Now r = r(k+1).
			for (int i = 0; i < numWeights; ++i) {
				final double tmp = -this.gradients[i];
				rsum += tmp * this.r[i];
				this.r[i] = tmp;
			}
			this.lambda2 = 0;
			this.success = true;

			// Do we need to restart?
			if (this.k >= numWeights) {
				this.restart = true;
				EngineArray.arrayCopy(this.r, this.p);

			} else {
				// Compute new conjugate direction.
				final double beta = (EngineArray.vectorProduct(this.r, this.r) - rsum)
						/ mu;

				// Update direction vector.
				for (int i = 0; i < numWeights; ++i) {
					this.p[i] = this.r[i] + beta * this.p[i];
				}

				this.restart = false;
			}

			if (gdelta >= 0.75D) {
				this.lambda *= 0.25D;
			}

		} else {
			// A reduction in error was not possible.
			// under_tolerance = false;

			// Go back to w(k) since w(k) + alpha*p(k) is not better.
			EngineArray.arrayCopy(this.oldWeights, this.weights);
			this.setError( this.oldError );
			this.lambda2 = this.lambda;
			this.success = false;
		}

		if (gdelta < 0.25D) {
			this.lambda += this.delta * (1 - gdelta) / this.magP;
		}

		this.lambda = BoundNumbers.bound(this.lambda);

		++this.k;

		EngineArray.arrayCopy(this.weights, this.network.getFlat().getWeights());
	}

	/**
	 * Update the weights.
	 * @param gradients The current gradients.
	 * @param lastGradient The last gradients.
	 * @param index The weight index being updated.
	 * @return The new weight value.
	 */
	@Override
	public double updateWeight(final double[] gradients,
			final double[] lastGradient, final int index) {
		return 0;
	}

	/**
	 * Unused.
	 */
	public void initOthers() {
		
	}

}

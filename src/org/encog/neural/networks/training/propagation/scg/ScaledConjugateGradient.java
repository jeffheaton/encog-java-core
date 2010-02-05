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

package org.encog.neural.networks.training.propagation.scg;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.gradient.CalculateGradient;
import org.encog.util.EncogArray;
import org.encog.util.math.BoundNumbers;

/**
 * This is a training class that makes use of scaled conjugate 
 * gradient methods.  It is a very fast and efficient training
 * algorithm.
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
	 * The number of iterations.  The network will reset when this value
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
	 * The gradients after the training cycles.
	 */
	private double[] gradient;
	
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
	 * Construct a training class.
	 * @param network The network to train.
	 * @param training The training data.
	 */
	public ScaledConjugateGradient(final BasicNetwork network,
			final NeuralDataSet training) {
		super(network, training);

		this.success = true;
		this.delta = 0;
		this.lambda2 = 0;
		this.lambda = ScaledConjugateGradient.FIRST_LAMBDA;
		this.oldError = 0;
		this.magP = 0;
		this.restart = false;

		this.weights = NetworkCODEC.networkToArray(getNetwork());
		final int numWeights = this.weights.length;

		this.gradient = new double[numWeights];
		this.oldWeights = new double[numWeights];
		this.oldGradient = new double[numWeights];

		this.p = new double[numWeights];
		this.r = new double[numWeights];

		// Calculate the starting set of gradients.
		this.gradient = calcGradients(this.weights);

		this.k = 1;

		for (int i = 0; i < numWeights; ++i) {
			this.p[i] = this.r[i] = -this.gradient[i];
		}

	}

	/**
	 * Calculate the gradients.  They are normalized as well.
	 * @param weights The weights.
	 * @return The gradients.
	 */
	private double[] calcGradients(final double[] weights) {

		final Layer output = getNetwork().getLayer(BasicNetwork.TAG_OUTPUT);
		final int outCount = output.getNeuronCount();

		final CalculateGradient prop = new CalculateGradient(getNetwork(),
				getTraining(), getNumThreads());
		prop.calculate(weights);

		// normalize
		final double[] d = prop.getGradients();

		final double factor = -2D / prop.getCount() / outCount;

		for (int i = 0; i < d.length; i++) {
			d[i] *= factor;
		}

		setError(prop.getError());
		return prop.getGradients();
	}

	/**
	 * Perform one iteration.
	 */
	@Override
	public void iteration() {

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
			this.magP = EncogArray.vectorProduct(this.p, this.p);

			final double sigma = ScaledConjugateGradient.FIRST_SIGMA
					/ Math.sqrt(this.magP);

			// In order to compute the new step, we need a new gradient.
			// First, save off the old data.
			EncogArray.arrayCopy(this.gradient, this.oldGradient);
			EncogArray.arrayCopy(this.weights, this.oldWeights);
			this.oldError = getError();

			// Now we move to the new point in weight space.
			for (int i = 0; i < numWeights; ++i) {
				this.weights[i] += sigma * this.p[i];
			}

			NetworkCODEC.arrayToNetwork(this.weights, getNetwork());

			// And compute the new gradient.
			this.gradient = calcGradients(this.weights);

			// Now we have the new gradient, and we continue the step
			// computation.
			this.delta = 0;
			for (int i = 0; i < numWeights; ++i) {
				final double step = (this.gradient[i] - this.oldGradient[i])
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
		final double mu = EncogArray.vectorProduct(this.p, this.r);
		final double alpha = mu / this.delta;

		// Calculate the comparison parameter.
		// We must compute a new gradient, but this time we do not
		// want to keep the old values. They were useful only for
		// approximating the Hessian.
		for (int i = 0; i < numWeights; ++i) {
			this.weights[i] = this.oldWeights[i] + alpha * this.p[i];
		}

		NetworkCODEC.arrayToNetwork(this.weights, getNetwork());

		this.gradient = calcGradients(this.weights);

		final double gdelta = 2 * this.delta * (this.oldError - getError())
				/ (mu * mu);

		// If gdelta >= 0, a successful reduction in error is possible.
		if (gdelta >= 0) {
			// Product of r(k+1) by r(k)
			double rsum = 0;

			// Now r = r(k+1).
			for (int i = 0; i < numWeights; ++i) {
				final double tmp = -this.gradient[i];
				rsum += tmp * this.r[i];
				this.r[i] = tmp;
			}
			this.lambda2 = 0;
			this.success = true;

			// Do we need to restart?
			if (this.k >= numWeights) {
				this.restart = true;
				EncogArray.arrayCopy(this.r, this.p);

			} else {
				// Compute new conjugate direction.
				final double beta = (EncogArray.vectorProduct(this.r, this.r) 
						- rsum)/ mu;

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
			EncogArray.arrayCopy(this.oldWeights, this.weights);
			setError(this.oldError);
			this.lambda2 = this.lambda;
			this.success = false;
		}

		if (gdelta < 0.25D) {
			this.lambda += this.delta * (1 - gdelta) / this.magP;
		}

		this.lambda = BoundNumbers.bound(this.lambda);

		++this.k;

		NetworkCODEC.arrayToNetwork(this.weights, getNetwork());
	}

	/**
	 * Not used.
	 * 
	 * @param prop
	 *            Not used.
	 * @param weights
	 *            Not used.
	 */
	@Override
	public void performIteration(final CalculateGradient prop,
			final double[] weights) {
	}
}

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
package org.encog.neural.networks.training.propagation.scg;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.gradient.CalculateGradient;
import org.encog.util.EncogArray;
import org.encog.util.math.BoundNumbers;

public class ScaledConjugateGradient extends Propagation {

	protected static final double FIRST_SIGMA = 1.E-4D;
	protected static final double FIRST_LAMBDA = 1.E-6D;

	private BasicNetwork network;
	private NeuralDataSet training;
	private boolean restart;
	private double lambda2;
	private double lambda;
	private int k;
	private boolean success = true;
	private double magP;
	
	/**
	 * Step direction vector.
	 */
	private double[] p;
	
	/**
	 * Step direction vector.
	 */
	private double[] r;
	private double[] weights;
	private double[] gradient;
	private double delta;
	private double oldError;
	private double[] oldWeights;
	private double[] oldGradient;

	public ScaledConjugateGradient(BasicNetwork network, NeuralDataSet training) {
		this.network = network;
		this.training = training;

		this.success = true;
		this.delta = 0;
		this.lambda2 = 0;
		this.lambda = FIRST_LAMBDA;
		this.oldError = 0;
		this.magP = 0;
		this.restart = false;

		this.weights = NetworkCODEC.networkToArray(this.network);
		int numWeights = weights.length;

		this.gradient = new double[numWeights];
		this.oldWeights = new double[numWeights];
		this.oldGradient = new double[numWeights];

		this.p = new double[numWeights];
		this.r = new double[numWeights];

		// Calculate the starting set of gradients.
		this.gradient = calcGradients(weights);

		this.k = 1;

		for (int i = 0; i < numWeights; ++i)
			p[i] = r[i] = -gradient[i];

	}

	public BasicNetwork getNetwork() {
		return this.network;
	}

	private double[] calcGradients(double[] weights) {

		Layer output = this.network.getLayer(BasicNetwork.TAG_OUTPUT);
		int outCount = output.getNeuronCount();

		CalculateGradient prop = new CalculateGradient(this.network, this.training, this.getNumThreads());
		prop.calculate(training, weights);

		// normalize
		double[] d = prop.getErrors();

		double factor = -2D / prop.getCount() / outCount;

		for (int i = 0; i < d.length; i++)
			d[i] *= factor;

		this.setError(prop.getError());
		return prop.getErrors();
	}

	public void iteration() {
		int numWeights = weights.length;
		// Storage space for previous iteration values.

		if (restart) {
			// First time through, set initial values for SCG parameters.
			lambda = FIRST_LAMBDA;
			lambda2 = 0;
			k = 1;
			success = true;
			restart = false;
		}

		// If an error reduction is possible, calculate 2nd order info.
		if (success) {

			// If the search direction is small, stop.
			magP = EncogArray.vectorProduct(p, p);

			double sigma = FIRST_SIGMA / (double) Math.sqrt(magP);

			// In order to compute the new step, we need a new gradient.
			// First, save off the old data.
			EncogArray.arrayCopy(gradient, oldGradient);
			EncogArray.arrayCopy(weights, oldWeights);
			oldError = getError();

			// Now we move to the new point in weight space.
			for (int i = 0; i < numWeights; ++i)
				weights[i] += sigma * p[i];

			NetworkCODEC.arrayToNetwork(weights, this.network);

			// And compute the new gradient.
			gradient = calcGradients(weights);

			// Now we have the new gradient, and we continue the step
			// computation.
			delta = 0;
			for (int i = 0; i < numWeights; ++i) {
				double step = (gradient[i] - oldGradient[i]) / sigma;
				delta += p[i] * step;
			}
		}

		// Scale delta.
		delta += (lambda - lambda2) * magP;

		// If delta <= 0, make Hessian positive definite.
		if (delta <= 0) {
			lambda2 = 2 * (lambda - delta / magP);
			delta = lambda * magP - delta;
			lambda = lambda2;
		}

		// Calculate step size.
		double mu = EncogArray.vectorProduct(p, r);
		double alpha = mu / delta;

		// Calculate the comparison parameter.
		// We must compute a new gradient, but this time we do not
		// want to keep the old values. They were useful only for
		// approximating the Hessian.
		for (int i = 0; i < numWeights; ++i)
			weights[i] = oldWeights[i] + alpha * p[i];

		NetworkCODEC.arrayToNetwork(weights, this.network);

		gradient = calcGradients(weights);

		double gdelta = 2 * delta * (oldError - getError()) / (mu * mu);

		// If gdelta >= 0, a successful reduction in error is possible.
		if (gdelta >= 0) {
			// Product of r(k+1) by r(k)
			double rsum = 0;

			// Now r = r(k+1).
			for (int i = 0; i < numWeights; ++i) {
				double tmp = -gradient[i];
				rsum += tmp * r[i];
				r[i] = tmp;
			}
			lambda2 = 0;
			success = true;

			// Do we need to restart?
			if (k >= numWeights) {
				restart = true;
				EncogArray.arrayCopy(r, p);

			} else {
				// Compute new conjugate direction.
				double beta = (EncogArray.vectorProduct(r, r) - rsum) / mu;

				// Update direction vector.
				for (int i = 0; i < numWeights; ++i)
					p[i] = r[i] + beta * p[i];

				restart = false;
			}

			if (gdelta >= 0.75D)
				lambda *= 0.25D; // lambda = lambda/4.0

		} else {
			// A reduction in error was not possible.
			// under_tolerance = false;

			// Go back to w(k) since w(k) + alpha*p(k) is not better.
			EncogArray.arrayCopy(oldWeights, weights);
			setError(oldError);
			lambda2 = lambda;
			success = false;
		}

		if (gdelta < 0.25D)
			lambda += delta * (1 - gdelta) / magP;
		
		lambda = BoundNumbers.bound(lambda);

		++k;

		NetworkCODEC.arrayToNetwork(weights, this.network);
	}
}

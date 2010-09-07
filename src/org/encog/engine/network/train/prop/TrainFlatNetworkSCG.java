package org.encog.engine.network.train.prop;

import org.encog.engine.data.EngineDataSet;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.util.BoundNumbers;
import org.encog.engine.util.EngineArray;

public class TrainFlatNetworkSCG extends TrainFlatNetworkProp {

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

	
	public TrainFlatNetworkSCG(FlatNetwork network, EngineDataSet training) {
		super(network, training);
		
		this.success = true;
		this.delta = 0;
		this.lambda2 = 0;
		this.lambda = TrainFlatNetworkSCG.FIRST_LAMBDA;
		this.oldError = 0;
		this.magP = 0;
		this.restart = false;

		this.weights = EngineArray.arrayCopy(network.getWeights());
		final int numWeights = this.weights.length;

		//this.gradients = new double[numWeights];
		this.oldWeights = new double[numWeights];
		this.oldGradient = new double[numWeights];

		this.p = new double[numWeights];
		this.r = new double[numWeights];

		// Calculate the starting set of gradients.
		calculateGradients();

		this.k = 1;

		for (int i = 0; i < numWeights; ++i) {
			this.p[i] = this.r[i] = -this.gradients[i];
		}

	}
	
	/**
	 * Calculate the gradients.  They are normalized as well.
	 */
	public void calculateGradients() {

		final int outCount = this.getNetwork().getOutputCount();

		super.calculateGradients();

		// normalize
		
		final double factor = -2D / this.gradients.length / outCount;

		for (int i = 0; i < this.gradients.length; i++) {
			this.gradients[i] *= factor;
		}

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
			this.lambda = TrainFlatNetworkSCG.FIRST_LAMBDA;
			this.lambda2 = 0;
			this.k = 1;
			this.success = true;
			this.restart = false;
		}

		// If an error reduction is possible, calculate 2nd order info.
		if (this.success) {

			// If the search direction is small, stop.
			this.magP = EngineArray.vectorProduct(this.p, this.p);

			final double sigma = TrainFlatNetworkSCG.FIRST_SIGMA
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

			EngineArray.arrayCopy(this.weights, this.network.getWeights());

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

		EngineArray.arrayCopy(this.weights, this.network.getWeights());

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
				final double beta = (EngineArray.vectorProduct(this.r, this.r) 
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
			EngineArray.arrayCopy(this.oldWeights, this.weights);
			this.currentError = this.oldError;
			this.lambda2 = this.lambda;
			this.success = false;
		}

		if (gdelta < 0.25D) {
			this.lambda += this.delta * (1 - gdelta) / this.magP;
		}

		this.lambda = BoundNumbers.bound(this.lambda);

		++this.k;

		EngineArray.arrayCopy(this.weights, this.network.getWeights());
	}


	@Override
	public double updateWeight(double[] gradients, double[] lastGradient,
			int index) {
		return 0;
	}

}

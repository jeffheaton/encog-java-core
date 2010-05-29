package org.encog.neural.networks.flat;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

/**
 * Train a flat network using RPROP.
 */
public class TrainFlatNetworkResilient extends TrainFlatNetworkMulti {
	/**
	 * The update values, for the weights and thresholds.
	 */
	private final double[] updateValues;

	/**
	 * The zero tolerance.
	 */
	private final double zeroTolerance;

	/**
	 * The maximum step value for rprop.
	 */
	private final double maxStep;

	/**
	 * Construct a resilient trainer for flat networks.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 * @param zeroTolerance
	 *            How close a number should be to zero to be counted as zero.
	 * @param initialUpdate
	 *            The initial update value.
	 * @param maxStep
	 *            The maximum step value.
	 */
	public TrainFlatNetworkResilient(final FlatNetwork network,
			final NeuralDataSet training, final double zeroTolerance,
			final double initialUpdate, final double maxStep)

	{
		super(network, training);
		this.updateValues = new double[network.getWeights().length];
		this.zeroTolerance = zeroTolerance;
		this.maxStep = maxStep;

		for (int i = 0; i < this.updateValues.length; i++) {
			this.updateValues[i] = initialUpdate;
		}
	}

	public TrainFlatNetworkResilient(FlatNetwork flat,
			NeuralDataSet trainingSet) {
		this(flat,trainingSet,ResilientPropagation.DEFAULT_ZERO_TOLERANCE,ResilientPropagation.DEFAULT_INITIAL_UPDATE,ResilientPropagation.DEFAULT_MAX_STEP);
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
		// multiply the current and previous gradient, and take the
		// sign. We want to see if the gradient has changed its sign.
		final int change = sign(gradients[index] * lastGradient[index]);
		double weightChange = 0;

		// if the gradient has retained its sign, then we increase the
		// delta so that it will converge faster
		if (change > 0) {
			double delta = this.updateValues[index]
					* ResilientPropagation.POSITIVE_ETA;
			delta = Math.min(delta, this.maxStep);
			weightChange = sign(gradients[index]) * delta;
			this.updateValues[index] = delta;
			lastGradient[index] = gradients[index];
		} else if (change < 0) {
			// if change<0, then the sign has changed, and the last
			// delta was too big
			double delta = this.updateValues[index]
					* ResilientPropagation.NEGATIVE_ETA;
			delta = Math.max(delta, ResilientPropagation.DELTA_MIN);
			this.updateValues[index] = delta;
			// set the previous gradent to zero so that there will be no
			// adjustment the next iteration
			lastGradient[index] = 0;
		} else if (change == 0) {
			// if change==0 then there is no change to the delta
			final double delta = lastGradient[index];
			weightChange = sign(gradients[index]) * delta;
			lastGradient[index] = gradients[index];
		}

		// apply the weight change, if any
		return weightChange;
	}

}

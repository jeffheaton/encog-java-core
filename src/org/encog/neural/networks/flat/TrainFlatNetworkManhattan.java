package org.encog.neural.networks.flat;

import org.encog.Encog;
import org.encog.neural.data.NeuralDataSet;

/**
 * Train the flat network using Manhattan update rule.
 */
public class TrainFlatNetworkManhattan extends TrainFlatNetworkMulti {

	/**
	 * The zero tolerance to use.
	 */
	private final double zeroTolerance;

	/**
	 * The learning rate.
	 */
	private double learningRate;

	/**
	 * Construct a trainer for flat networks to use the Manhattan update rule.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 * @param learningRate
	 *            The learning rate to use.
	 */
	public TrainFlatNetworkManhattan(final FlatNetwork network,
			final NeuralDataSet training, final double learningRate) {
		super(network, training);
		this.learningRate = learningRate;
		this.zeroTolerance = Encog.DEFAULT_DOUBLE_EQUAL;
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
		if (Math.abs(gradients[index]) < this.zeroTolerance) {
			return 0;
		} else if (gradients[index] > 0) {
			return this.learningRate;
		} else {
			return -this.learningRate;
		}
	}

}

package org.encog.neural.networks.flat;

import org.encog.neural.data.NeuralDataSet;

public class TrainFlatNetworkBackPropagation extends TrainFlatNetworkMulti {

	/**
	 * The learning rate.
	 */
	private final double learningRate;

	/**
	 * The momentum.
	 */
	private final double momentum;

	/**
	 * The last delta values.
	 */
	private final double[] lastDelta;

	/**
	 * Construct a backprop trainer for flat networks.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data.
	 * @param learningRate
	 *            The learning rate.
	 * @param momentum
	 *            The momentum.
	 */
	public TrainFlatNetworkBackPropagation(final FlatNetwork network,
			final NeuralDataSet training, final double learningRate,
			final double momentum)

	{
		super(network, training);
		this.momentum = momentum;
		this.learningRate = learningRate;
		this.lastDelta = new double[network.getWeights().length];
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
		final double delta = (gradients[index] * this.learningRate)
				+ (this.lastDelta[index] * this.momentum);
		this.lastDelta[index] = delta;
		return delta;
	}

	/**
	 * @return the learningRate
	 */
	public double getLearningRate() {
		return learningRate;
	}

	/**
	 * @return the momentum
	 */
	public double getMomentum() {
		return momentum;
	}
	
	

}

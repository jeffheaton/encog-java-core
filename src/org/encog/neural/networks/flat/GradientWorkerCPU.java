package org.encog.neural.networks.flat;

import org.encog.mathutil.error.ErrorCalculation;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.util.EncogArray;
import org.encog.util.Stopwatch;

/**
 * Worker class for the mulithreaded training of flat networks.
 */
public class GradientWorkerCPU implements FlatGradientWorker {

	/**
	 * The network to train.
	 */
	private final FlatNetwork network;

	/**
	 * The error calculation method.
	 */
	private final ErrorCalculation errorCalculation = new ErrorCalculation();

	/**
	 * The actual values from the neural network.
	 */
	private final double[] actual;

	/**
	 * The deltas for each layer
	 */
	private final double[] layerDelta;

	/**
	 * The neuron counts, per layer.
	 */
	private final int[] layerCounts;

	/**
	 * The layer indexes
	 */
	private final int[] layerIndex;

	/**
	 * The index to each layer's weights and thresholds.
	 */
	private final int[] weightIndex;

	/**
	 * The output from each layer
	 */
	private final double[] layerOutput;

	/**
	 * The gradients
	 */
	private final double[] gradients;

	/**
	 * The weights and thresholds.
	 */
	private final double[] weights;

	/**
	 * The pair to use for training.
	 */
	private final NeuralDataPair pair;

	/**
	 * The training data.
	 */
	private final Indexable training;

	/**
	 * The high end of the training data.
	 */
	private final int low;

	/**
	 * The low end of the training.
	 */
	private final int high;

	/**
	 * The owner.
	 */
	private final TrainFlatNetworkMulti owner;

	/**
	 * The elapsed time.
	 */
	private long elapsedTime;

	/**
	 * The stopwatch, to evaluate performance.
	 */
	private final Stopwatch stopwatch;

	/**
	 * Construct a gradient worker.
	 * 
	 * @param network
	 *            The network to train.
	 * @param owner
	 *            The owner that is doing the training.
	 * @param training
	 *            The training data.
	 * @param low
	 *            The low index to use in the training data.
	 * @param high
	 *            The high index to use in the training data.
	 */
	public GradientWorkerCPU(final FlatNetwork network,
			final TrainFlatNetworkMulti owner, final Indexable training,
			final int low, final int high) {
		this.network = network;
		this.training = training;
		this.low = low;
		this.high = high;
		this.owner = owner;

		this.stopwatch = new Stopwatch();

		this.layerDelta = new double[network.getLayerOutput().length];
		this.gradients = new double[network.getWeights().length];
		this.actual = new double[network.getOutputCount()];

		this.weights = network.getWeights();
		this.layerIndex = network.getLayerIndex();
		this.layerCounts = network.getLayerCounts();
		this.weightIndex = network.getWeightIndex();
		this.layerOutput = network.getLayerOutput();

		this.pair = BasicNeuralDataPair.createPair(network.getInputCount(),
				network.getOutputCount());
	}

	/**
	 * @return Elapsed time for the last iteration.
	 */
	public long getElapsedTime() {
		return this.elapsedTime;
	}

	/**
	 * @return The weights for this network.
	 */
	public double[] getWeights() {
		return this.weights;
	}

	/**
	 * Process one training set element.
	 * 
	 * @param input
	 *            The network input.
	 * @param ideal
	 *            The ideal values.
	 */
	private void process(final double[] input, final double[] ideal) {
		this.network.compute(input, this.actual);

		this.errorCalculation.updateError(this.actual, ideal);

		for (int i = 0; i < this.actual.length; i++) {

			this.layerDelta[i] = FlatNetwork.calculateActivationDerivative(
					this.network.getActivationType()[0], this.actual[i])
					* (ideal[i] - this.actual[i]);
		}

		for (int i = 0; i < this.layerCounts.length - 1; i++) {
			processLevel(i);
		}
	}

	/**
	 * Process one level.
	 * 
	 * @param currentLevel
	 *            The level.
	 */
	private void processLevel(final int currentLevel) {
		final int fromLayerIndex = this.layerIndex[currentLevel + 1];
		final int toLayerIndex = this.layerIndex[currentLevel];
		final int fromLayerSize = this.layerCounts[currentLevel + 1];
		final int toLayerSize = this.layerCounts[currentLevel];

		// clear the to-deltas
		for (int i = 0; i < fromLayerSize; i++) {
			this.layerDelta[fromLayerIndex + i] = 0;
		}

		int index = this.weightIndex[currentLevel];

		// handle thresholds
		for (int i = 0; i < toLayerSize; i++) {
			this.gradients[index++] += this.layerDelta[toLayerIndex + i];
		}

		// handle weights
		for (int x = 0; x < toLayerSize; x++) {
			for (int y = 0; y < fromLayerSize; y++) {
				final double value = this.layerOutput[fromLayerIndex + y]
						* this.layerDelta[toLayerIndex + x];
				this.gradients[index] += value;
				this.layerDelta[fromLayerIndex + y] += this.weights[index]
						* this.layerDelta[toLayerIndex + x];
				index++;
			}
		}

		for (int i = 0; i < fromLayerSize; i++) {
			this.layerDelta[fromLayerIndex + i] *= FlatNetwork
					.calculateActivationDerivative(this.network
							.getActivationType()[currentLevel + 1],
							this.layerOutput[fromLayerIndex + i]);
		}
	}

	/**
	 * Perform the gradient calculation for the specified index range.
	 */
	public void run() {
		try {
			this.stopwatch.reset();
			this.stopwatch.start();
			this.errorCalculation.reset();
			for (int i = this.low; i <= this.high; i++) {
				this.training.getRecord(i, this.pair);
				process(this.pair.getInput().getData(), this.pair.getIdeal()
						.getData());
			}
			final double error = this.errorCalculation.calculate();
			this.owner.report(this.gradients, error, null);
			EncogArray.fill(this.gradients, 0);
			this.stopwatch.stop();
			this.elapsedTime = this.stopwatch.getElapsedTicks();
		} catch (Throwable ex) {
			this.owner.report(null, 0, ex);
		}
	}

}
